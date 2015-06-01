import seaborn as sns
import pandas

from flask.ext.mongoengine import mongoengine
from mongoengine import Q
from models import MotionInstant

# http://stackoverflow.com/questions/24963062
COLUMNS = ["norm", "time", "user"]

sns.set(style="darkgrid")

# def unzip_mis(mis):
#     norms = []
#     times = []
#     for mi in mis:
#         norms.append(mi.norm)
#         times.append(mi.time)
#     return (norms, times)

def df_for_song(song):
    mis = MotionInstant.objects(
        Q(time_gte=song.startTime) &
        Q(time_lte=song.endTime))
    df = pandas.DataFrame(mis, columns=COLUMNS)
    print df
    return df


def plot(df):
    return sns.tsplot(data=df, time="time", unit="user", value="norm")
