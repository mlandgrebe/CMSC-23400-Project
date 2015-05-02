# This is where the DB models go
from app import db

class User(db.Document):
    spotify_uri = db.StringField()

# TODO: We probably don't want to associate a song with one vote count
# globally. Not sure how we want to go about making this specific to a
# room
class Song(db.Document):
    spotify_uri = db.StringField()
    vote_count = db.IntField()

class SongRoom(db.Document):
    name = db.StringField()
    host = db.ReferenceField('User')

    members = db.ListField(db.ReferenceField('User'))
    queue = db.ListField(db.ReferenceField('Song'))
    played_history = db.ListField(db.ReferenceField('Song'))

    location = db.PointField()
