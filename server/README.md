Python's package manager can be a real hasle to deal with, so most
people use `virtualenv` to make it easier to deal with. Using
virtualenv lets you work inside a sandbox and install packages without
polluting your global package list.

The way to use it is:

1. `sudo pip install virtualenv` (assuming you don't have it yet)

2. `cd` to this directory

3. The first time, you use `virtualenv NAME` where NAME is the name of
   the directory where you want your virtualenv to live. I call mine
   "venv".

4. Now every time you start to do work in the directory, run `source
   NAME/bin/activate`. This will put you inside the virtualenv. Now
   you can install packages without `sudo` with `pip` and they'll go
   inside the virtualenv.


It also makes your life easier because it means that your virtualenv holds only those packages that you need for this project. So when you install new ones you can run


    pip freeze > requirements.txt


and the next person can run

    pip install -r requirements.txt


and have everything he needs.
