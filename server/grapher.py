import seaborn as sns
import pandas

from flask.ext.mongoengine import mongoengine
from mongoengine import Q
from models import MotionInstant
from datetime import datetime

COLUMNS = ["time", "norm", "user"]

sns.set(style="darkgrid")

# def unzip_mis(mis):
#     norms = []
#     times = []
#     for mi in mis:
#         norms.append(mi.norm)
#         times.append(mi.time)
#     return (norms, times)

def mis_for_song(song, extra_Q=None):
    q = (Q(time__gte=song.startTime) &
         Q(time__lte=song.stopTime))
    if extra_Q:
        q &= extra_Q
    return MotionInstant.objects(q)

def users_for_song(song):
    return mis_for_song(song).distinct('user')

def agg_mis_for_song(song):
    users = users_for_song(song)
    return {user.name:mis_for_song(song, extra_Q=Q(user=user))
            for user in users}

def fmt_time(dt):
    return (dt - datetime(1970, 1, 1)).total_seconds()

def extract(mis):
    return [(fmt_time(m.time), m.norm, m.user.name) for m in
            mis]

def get_time(mis):
    return (fmt_time(mi.time) for mi in mis)

def get_norm(mis):
    return (mi.norm for mi in mis)

def get_both(mis):
    return [(fmt_time(mi.time), mi.norm) for mi in mis]

def df_for_song(song):
    mis = mis_for_song(song)
    df = pandas.DataFrame(extract(mis), columns=COLUMNS)
    print df
    return df


# def plot_df(df):
#     return sns.tsplot(data=df, time="time",
#                       condition="user", value="norm")
