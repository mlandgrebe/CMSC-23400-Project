# This is where the endpoints go.
from flask import request, make_response, jsonify
from models import SongRoom, Song, User
from app import app, db


# In meters? I don't know
DEFAULT_SR_DISTANCE = 1000

# In the future, only POST requests should be allowed for most of
# these, but it's easier to test with curl if we allow both for now.

def get_sr(req, tag='srId'):
    return SongRoom.objects(id=req.tag).first()

def get_user(req, tag="userId"):
    return User.objects(id=req.values[tag]).first()

def get_song(req, tag="songId"):
    return Song.objects(id=req.values[tag]).first()

def get_queue(req, tag="queueId"):
    return SongQueue.objects(id=req.values[tag]).first()

@app.route("/createSR", methods=["GET", "POST"])
def create_sr():
    params = request.values
    location = params["location"]
    name = params["name"]

    # Not sure how we need to format location so that MongoEngine understands
    new_room = SongRoom(get_user(request, "hostId"), location, name)
    new_room.save()
    return jsonify({"result":"OK"})

@app.route("/nearbySR", methods=["GET", "POST"])
def nearby_sr():
    # TODO: how do we parse this?
    location = params["location"]
    res = SongRoom.objects(location__near=location,
                           location__max_distance=DEFAULT_SR_DISTANCE)
    return jsonify(res)

def modify_sr(request, is_join):
    user = get_user(request)
    sr = get_sr(request)
    current_members = sr.members
    if is_join:
        current_members.append(user)
    else:
        current_members = [c for c in current_members if c != user]

    sr.modify(members=current_members)

# This will probably not play well with concurrency
@app.route("/joinSR", methods=["GET", "POST"])
def join_sr():
    modify_sr(request, True)

@app.route("/leaveSR", methods=["GET", "POST"])
def leave_sr():
    modify_sr(request, False)

@app.route("/getVotes", methods=["GET", "POST"])
def get_votes():
    song = get_song(request)
    return jsonify(song.votes)

@app.route("/submitVote", methods=["GET", "POST"])
def submit_vote():
    user = get_user(request)
    is_up = request.values["isUp"]
    vote = Vote(user, bool(is_up))
    vote.save()

@app.route("/getQueue", methods=["GET", "POST"])
def get_queue():
    sr = get_sr(request)
    return jsonify(sr.queue)


@app.route("/srMembers", methods=["GET", "POST"])
def sr_members():
    sr = get_sr(request)
    return jsonify(sr.members)

# This is racy
@app.route("/changeQueue", methods=["GET", "POST"])
def change_queue():
    queue = get_queue(request)
    song = get_song(request)
    is_enq = bool(request.values["isEnq"])
    current_queue = queue.songs

    if is_enq:
        current_queue.append(song)
    else:
        current_queue = [s for s in current_queue if s != song]
    queue.modify(songs=current_queue)


@app.route("/createUser", methods=["GET", "POST"])
def create_user():
    spotify_uri = request.values.get("spotifyUri")
    name = request.values["name"]

    user = User(spotify_uri, name)
    user.save()
    # return jsonify({'result':'OK'})
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

# Test endpoints for us to hit
@app.route("/echo", methods=["GET", "POST"])
def echo():
    return jsonify(request.values)
