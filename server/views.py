# This is where the endpoints go.
from flask import request, make_response, jsonify
from models import SongRoom, Song, User
from app import app, db


# In the future, only POST requests should be allowed for most of
# these, but it's easier to test with curl if we allow both for now.


@app.route("/register", methods=["GET", "POST"])
def register():
    params = request.values
    host = params["hostId"]
    location = params["location"]
    name = params["name"]

    # Not sure how we need to format location so that MongoEngine understands
    new_room = SongRoom(host, location, name)
    new_room.save()
    return jsonify({"result":"OK"})

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

@app.route("/userInfo", methods=["GET", "POST"])
def user_info():
    user_id = request.values["userId"]
    user = User.objects(id=user_id).first()
    return user.to_json()

# Test endpoints for us to hit
@app.route("/echo", methods=["GET", "POST"])
def echo():
    return jsonify(request.values)
