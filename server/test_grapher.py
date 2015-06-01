from datetime import *
from models import *
from grapher import *

import unittest


class TestGrapher(unittest.TestCase):

    def setUp(self):
        self.now = datetime.now()
        self.yesterday = self.now + timedelta(-1)
        self.song = Song(startTime=self.yesterday,
                         stopTime=self.now)
        MotionInstant.objects().delete()

    def test_filter(self):
        d = timedelta(.1)
        mis = [MotionInstant(time=self.yesterday + i * d)
               for i in range(0, 15)]
        for mi in mis:
            mi.save()

        got = mis_for_song(self.song)
        self.assertEqual(len(got), 11)

if __name__ == "__main__":
    unittest.main()
