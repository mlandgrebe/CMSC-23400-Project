# This is where the endpoints go.
from flask import request, make_response, jsonify
from models import SongRoom, Song, User
from app import app

# Only POST requests allowed
@app.route("/register", methods=["POST"])
def register():
    params = request.args
    host = params.get("HostID")
    location = params.get("Location")
    name = params.get("Name")

    # And now we can do something like this:
    # new_room = SongRoom(host, location, name)


# Test endpoints for us to hit
@app.route("/echo", methods=["GET", "POST"])
def echo():
    return jsonify(request.args)
