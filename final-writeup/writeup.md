- Implementation details: libraries used, server-side component, phone
  or tablet app details, wearable device details, data sources,
  etc. Please provide a detailed description of all applicable parts
  of your implementation.

Our implementation has basically two halves: a server and the client
android app. Each requiring their own set of tools. The first half was
a server componant. We published the server on using Amazon Web
Services and wrote in in python using the Flask REST HTTP
framework. Our server also needed a database componant for this we
used mongo because it mapped nicely onto our client side classes and
it natively supported Location based queries, which we needed so that
we could post songrooms based on location. As a final result anything
button on in the songroom views is verified by the server and, if
valid, published to the rest of the room.

The second half of the implemention was the Android app itself. For
communication with the server we used a retrofit HTTP client. For our
musical needs we interfaced with spoify using their API.

- Challenges, overcome and insurmountable: what did you learn? what
  approaches did you have to adopt? what proved too complex to
  implement or achieve?

The biggest challenge we faced was Scala. We decided to try to use the
language for development, this unfortunately was ended up being a bad
idea as the language was not really at a point where it was able to be
used for android development. Eventually we were left to accept that
this was insurmountable and we switched to Java. After that our
development sped up significantly. However we did learn from this that
often maturity is as important or more important than fit when it
comes to choosing tools on a deadline. When using Scala it felt like
we were the first ones to attempt some of what we were doing,
unfortunately that also meant that if it was going to work we would
have to be the first ones to solve the associated problems. In this
way one of the biggest problems was library integration in scala, even
though in theory everything written in java should work in scala.

The next biggest challenge was probably interfacing with multiple
remote forms of communication while also having that influence the
view on screen. We had 3 main remote communications: the
server(including location, songroom information), the spotify API(for
getting songs, playlists, and user information), and the spotify
player(for actually playing music) This was mainly a problem because
the view thread cannot be blocked to wait for a request to return
without causing problems with android. This can be solved by having a
thread asnchronously deal with the request. However this solution is
has 2 problems: First, in Java threads are objects and these objects
don't have access to the activity objects and Second, and Android only
the main thread can change the view. We solved this by building each
activity using the observer pattern, with an observable thread. So the
thread would make asynchronous request to what it needed and when it
had the information it would notify observers, specifically the
activity. Then the activity would launch a runnable on the UIThread
which used its objects to make the neccesary changes.

The most insurmountable part of the project was the scope. We started
out with a lot of ideas such as, using sensing with the echo nest to
develop a live music taste profile (an idea we only partially even got
to get into), we also wanted to have a substantial social portion of
the app, including things like matching activity profiles together and
having handshake exchanges of spotify profiles. We also wanted to have
a watch app that integrated all of this in a way that made as much of
it natural and passive as was possible. If we had stayed on our
planned schedule some of this may have been possible. However, we
underestimated how long the core functionality would take to get up
and how long working with new, unknown technologies take to
integrate. The lesson was simply to start with as simple an idea as
possible, execute, and then look at how you can build on top it.

- If you could do this project again from scratch, what would you do
  differently?

The obvious is that we would have started with Android. However there
are serveral things that we think would have made our Android
development easier. The first, is that we should have made some
abstract Activity classes and multipurpose views. In a project with a
lot of views this is key, we didn't realize it initially but most of
what we doing in every view branched from a set of ideas, such at the
observer pattern mentioned earlier.

- What have you learned about the themes of the course through working
  on your project?


One of the biggest things was just the plethora of
  resources for finding location. While it seems obvious, this project
  sought to crowdsource music DJing, very much in line with the
  location sensing themes of this course. What we found in building
  was very well documented and a wide variety of location frameworks
  for both our mongo backend and our client side android app. This is
  almost certainly a capability that has the potenial to see increased
  use with increased ability to get location and better battery life
  to support constant fixes without fear of destroying battery life.

- Include screenshots that illustrate example usage scenarios of your
  app, step by step, with enough detail so that someone who was not at
  your demo could still fully appreciate what you have gotten up and
  running.
