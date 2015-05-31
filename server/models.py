# This is where the DB models go
from app import db

class User(db.Document):
    spotifyURI = db.StringField()
    name = db.StringField()

class Vote(db.Document):
    user = db.ReferenceField('User')
    isUp = db.BooleanField()

# TODO: We probably don't want to associate a song with one vote count
# globally. Not sure how we want to go about making this specific to a
# room
class Song(db.Document):
    spotifyURI = db.StringField()
    votes = db.ListField(db.ReferenceField('Vote'))

class SongRoom(db.Document):
    name = db.StringField()
    host = db.ReferenceField('User')

    members = db.ListField(db.ReferenceField('User'))
    queue = db.ListField(db.ReferenceField('SongQueue'))
    # played_history = db.ListField(db.ReferenceField('Song'))

    location = db.PointField()

class SongQueue(db.Document):
    songs = db.ListField(db.ReferenceField('Song'))
    # Other stuff too? I don't know
