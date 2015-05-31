# This is where the endpoints go.
from flask import request, make_response, jsonify, Response, json
from flask.ext.mongoengine import mongoengine
from mongoengine import NotUniqueError
from models import SongRoom, Song, User, SongQueue, Vote
from app import app, db


# In meters? I don't know
DEFAULT_SR_DISTANCE = 1000

# In the future, only POST requests should be allowed for most of
# these, but it's easier to test with curl if we allow both for now.

def get_from(obj, req, tag):
    return obj.objects(id=req.values[tag]).first()

def get_sr(req, tag='srId'):
    return get_from(SongRoom, req, tag)

def get_user(req, tag="userId"):
    return get_from(User, req, tag)

def get_song(req, tag="songId"):
    return get_from(Song, req, tag)

def get_queue(req, tag="queueId"):
    return get_from(SongQueue, req, tag)

def get_uri(req, tag="spotifyURI"):
    return req.values[tag]

def parse_loc(req, tag="location"):
    return [float(x) for x in req.values[tag].strip("()").split(",")]

def parse_bool(req, tag):
    return req.values[tag] == "true"

def mk_json(obj):
    return Response(json.dumps(obj), mimetype="application/json")

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

@app.route("/getQueue", methods=["GET", "POST"])
def get_songqueue():
    sr = get_sr(request)
    return sr.queue.to_json()

@app.route("/getSongs", methods=["GET", "POST"])
def get_songs():
    sq = get_queue(request)
    return mk_json(sq.songs)

@app.route("/srMembers", methods=["GET", "POST"])
def sr_members():
    sr = get_sr(request)
    return mk_json(sr.members)

# This is racy
@app.route("/changeQueue", methods=["GET", "POST"])
def change_queue():
    is_enq = parse_bool(request, "isEnq")
    song = get_song(request)

    def to_update():
        return SongQueue.objects(id=request.values["queueId"]).update_one

    if is_enq:
        to_update()(push__songs=song)
    else:
        to_update()(pull__songs=song)

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

# Test endpoints for us to hit
@app.route("/echo", methods=["GET", "POST"])
def echo():
    return jsonify(request.values)
