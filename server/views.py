# This is where the endpoints go.
from flask import request, make_response, jsonify, Response, json
from flask.ext.mongoengine import mongoengine
from mongoengine import NotUniqueError
from models import SongRoom, Song, User, SongQueue, Vote, MotionInstant
from app import app, db
from datetime import datetime


# In meters? I don't know
DEFAULT_SR_DISTANCE = 1000

# In the future, only POST requests should be allowed for most of
# these, but it's easier to test with curl if we allow both for now.

class Empty:
    def to_json(self):
        return "{}"

empty = Empty()

def mk_date(d):
    if d:
        return {'date': d.isoformat().split('.')[0]}
    else:
        return {}

def obj_ref_acquire(obj, req, tag):
    return obj.objects(id=req.values[tag])

def get_from(obj, req, tag):
    return obj_ref_acquire(obj, req, tag).first()

def update_from(obj, req, tag):
    return obj_ref_acquire(obj, req, tag).update_one

def get_sr(req, tag='srId'):
    return get_from(SongRoom, req, tag)

def get_user(req, tag="userId"):
    return get_from(User, req, tag)

def get_song(req, tag="songId"):
    return get_from(Song, req, tag)

def get_queue(req, tag="queueId"):
    return get_from(SongQueue, req, tag)

def update_queue(req, tag="queueId"):
    return update_from(SongQueue, req, tag)

def update_song(req, tag="songId"):
    return update_from(Song, req, tag)

def get_uri(req, tag="spotifyURI"):
    return req.values[tag]

def parse_loc(req, tag="location"):
    return [float(x) for x in req.values[tag].strip("()").split(",")]

def parse_bool(req, tag):
    return req.values[tag] == "true"

# BE AWARE THAT YOU MIGHT BE FUCKING UP THE TZ
def parse_instant(s, u):
    print s
    print u
    d = json.loads(s)
    ts = datetime.utcfromtimestamp(d['timestamp'])
    norm = float(d['norm'])
    return MotionInstant(norm=norm, time=ts, user=u)

def mk_json(obj):
    return Response(json.dumps(obj), mimetype="application/json")

def get_ordered_songs(queue):
    return list(reversed(
        sorted(queue.songs,
               key=lambda s: sum(v.isUp for v in s.votes))))

@app.route("/createSR", methods=["GET", "POST"])
def create_sr():
    params = request.values
    location = parse_loc(request)
    print location
    name = params["name"]

    new_q = SongQueue(songs=[])
    new_q.save()
    new_room = SongRoom(host=get_user(request, "hostId"),
                        location=location,
                        name=name,
                        queue=new_q)
    new_room.save()
    print new_room.to_json()
    return new_room.to_json()

@app.route("/nearbySR", methods=["GET", "POST"])
def nearby_sr():
    # TODO: how do we parse this?
    location = parse_loc(request)
    res = SongRoom.objects(location__near=location,
                           location__max_distance=DEFAULT_SR_DISTANCE)
    return mk_json(res)

def modify_sr(request, is_join):
    user = get_user(request)
    sr = get_sr(request)
    current_members = sr.members
    if is_join:
        current_members.append(user)
    else:
        current_members = [c for c in current_members if c != user]

    sr.modify(members=current_members)
    sr.save()

# This will probably not play well with concurrency
@app.route("/joinSR", methods=["GET", "POST"])
def join_sr():
    print "Joining SR"
    modify_sr(request, True)
    return get_sr(request).to_json()

@app.route("/leaveSR", methods=["GET", "POST"])
def leave_sr():
    modify_sr(request, False)
    return "OK"

@app.route("/getVotes", methods=["GET", "POST"])
def get_votes():
    song = get_song(request)
    return mk_json(song.votes)

@app.route("/submitVote", methods=["GET", "POST"])
def submit_vote():
    user = get_user(request)
    is_up = parse_bool(request, "isUp")
    print get_song(request).to_json()
    vote = Vote(user=user, song=get_song(request), isUp=is_up)

    try:
        vote.save()
        Song.objects(id=request.values["songId"]).update_one(push__votes=vote)
    except NotUniqueError:
        pass

    return mk_json(get_song(request).votes)

# Racy, but only one person should touch this
@app.route("/popSong", methods=["GET"])
def pop_song():
    # -1? 1?
    room = get_sr(request)
    queue = room.queue
    songs = get_ordered_songs(queues)
    song = songs[-1]
    queue.modify(pull__songs=song)
    room.modify(playing=song)
    print [s.to_json() for s in queue.songs]
    return song.to_json()

@app.route("/getPlaying", methods=["GET"])
def get_playing():
    playing = get_sr(request).playing
    return (playing or empty).to_json()

@app.route("/startPlaying", methods=["GET"])
def start_playing():
    get_sr(request).playing.modify(startTime=datetime.now())
    return "OK"

@app.route("/stopPlaying", methods=["GET"])
def stop_playing():
    room = get_sr(request)
    song = room.playing
    song.modify(stopTime=datetime.now())
    room.modify(playing=None, push__history=song)

    return "OK"

@app.route("/getQueue", methods=["GET", "POST"])
def get_songqueue():
    sr = get_sr(request)
    return sr.queue.to_json()

@app.route("/getSongs", methods=["GET", "POST"])
def get_songs():
    sq = get_queue(request)
    return mk_json(get_ordered_songs(sq))

@app.route("/srMembers", methods=["GET", "POST"])
def sr_members():
    sr = get_sr(request)
    return mk_json(sr.members)

@app.route("/changeQueue", methods=["GET", "POST"])
def change_queue():
    is_enq = parse_bool(request, "isEnq")
    song = get_song(request)
    print get_queue(request)
    song.songRoom = SongRoom.objects(queue=get_queue(request)).first()
    song.save()

    if is_enq:
        update_queue(request)(push__songs=song)
    else:
        update_queue(request)(pull__songs=song)

    return mk_json(get_queue(request).songs)


@app.route("/createUser", methods=["GET", "POST"])
def create_user():
    spotify_uri = get_uri(request)
    name = request.values["name"]
    print spotify_uri
    print name
    user = User(spotifyURI=spotify_uri, name=name)
    print user
    user.save()
    # return jsonify({'result':'OK'})
    print user.to_json()
    return user.to_json()

@app.route("/deleteUser", methods=["GET", "POST"])
def delete_user():
    user_id = request.values["userId"]
    user = User.objects(id=user_id).first()
    user.delete()
    return jsonify({'result':'OK'})

@app.route("/updateUser", methods=["GET", "POST"])
def update_user():
    user_id = request.values["userId"]
    new_info = {param: request.values[param] for param
                in ["spotifyUri", "name"] if param in request.values}
    user = User.objects(id=user_id).first()
    user.modify(**new_info)
    return jsonify({'result':'OK'})

@app.route("/lookupUser", methods=["GET", "POST"])
def user_info():
    user_id = request.values["userId"]
    user = User.objects(id=user_id).first()
    return user.to_json()

@app.route("/createSong", methods=["GET", "POST"])
def create_song():
    spotify_uri = get_uri(request)
    song = Song(spotifyURI=spotify_uri, votes=[])
    song.save()
    return song.to_json()

@app.route("/getStart")
def get_start():
    song = get_song(request)
    print song.startTime and song.startTime.isoformat()
    return mk_json(mk_date(song.startTime))

@app.route("/getStop")
def get_stop():
    song = get_song(request)
    print song.stopTime and song.stopTime.isoformat()
    return mk_json(mk_date(song.stopTime))

# For integration tests
@app.route("/dropUsers", methods=["GET", "POST"])
def drop_users():
    print "Dropping"
    User.objects().delete()
    SongRoom.objects().delete()
    Song.objects().delete()
    SongQueue.objects().delete()
    Vote.objects().delete()
    return "OK"

@app.route("/bulkEnq", methods=["GET", "POST"])
def bulk_enq():
    uris = request.values.getlist('spotifyURIs')
    names = request.values.getlist('names')
    queue = get_queue(request)
    # Ugly, sorry
    sr = SongRoom.objects(queue=get_queue(request)).first()

    for uri, name in zip(uris, name):
        s = Song(spotifyURI=uri, songRoom=sr)
        s.save()
        update_queue(request)(push__songs=s)
    return "OK"


@app.route("/submitActivity", methods=["GET", "POST"])
def submit_activity():
    user = get_user(request)
    print request.values['instants']
    print request.values
    instants = request.values.getlist('instants')
    for instant_s in instants:
        instant = json.loads(instant_s)
        print instant
        mi = MotionInstant(user=user,
                           time=datetime.utcfromtimestamp(
                               instant['timestamp']),
                           norm=instant['norm'])
        mi.save()

    return "OK"

@app.route("/getActivity", methods=["GET", "POST"])
def get_activity():
    user = get_user(request)

    return mk_json([mi.to_json() for mi in
                    MotionInstant.objects(user=user)])

# Test endpoints for us to hit
@app.route("/echo", methods=["GET", "POST"])
def echo():
    return jsonify(request.values)
