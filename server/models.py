# This is where the DB models go
from app import db

class User(db.Document):
    spotifyURI = db.StringField()
    name = db.StringField()

class Vote(db.Document):
    user = db.ReferenceField('User', unique=True)
    song = db.ReferenceField('Song', unique_with='user')
    isUp = db.BooleanField()

class MotionInstant(db.Document):
    user = db.ReferenceField('User')
    norm = db.FloatField()
    time = db.DateTimeField()

class Song(db.Document):
    spotifyURI = db.StringField()
    votes = db.ListField(db.ReferenceField('Vote'))
    startTime = db.DateTimeField()
    stopTime = db.DateTimeField()
    songRoom = db.ReferenceField('SongRoom')

class SongRoom(db.Document):
    name = db.StringField()
    host = db.ReferenceField('User')

    members = db.ListField(db.ReferenceField('User'))
    queue = db.ReferenceField('SongQueue')
    playing = db.ReferenceField('Song')
    history = db.ListField(db.ReferenceField('Song'))

    location = db.PointField()

class SongQueue(db.Document):
    songs = db.ListField(db.ReferenceField('Song'))
    # Other stuff too? I don't know
