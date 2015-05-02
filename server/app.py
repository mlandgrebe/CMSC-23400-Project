# This configures objects that need to exist and be imported later.

from flask import Flask
from flask.ext.mongoengine import MongoEngine

app = Flask(__name__)

# For now, we're only going to think about doing local development. In
# the future, we'll want to have two .cfg files that we can switch
# between depending on where we're running stuff.

app.config.from_pyfile("./local-dev.cfg")

db = MongoEngine(app)
