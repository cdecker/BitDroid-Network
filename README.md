BitDroidNetwork
===============

This project aims at creating a native implementation of the [Bitcoin](http://bit.ly/eLnwyL)
[protocol](http://bit.ly/enx4M3).
We intentionally limited the capabilities of the protocol implementation to simple wire
message to object translation and back, the cryptographical implementation is located in
the project [BitDroidWallet](https://github.com/cdecker/BitDroidWallet).

The library is aimed at being reusable by implementing the publisher-subscriber pattern.
Incoming messages are published to all subscribers, which can respond by issuing responses
directly back to the client. The library does implement a minimal driver that attempts to
keep the connections alive as long as possible.

A pool of connections can be maintained by adding another listener.

The goals are:

* Speed!
* Reusability
* Minimalism (don't do too much, but do it right)

Getting started
---------------

There is not much to this, just take a look at the BitcoinEventListener Interface (that's
what you'll have to implement) and the Message clas and its descendants (that's what we'll
throw at you). A more sophisticated interface will soon follow.

Developers
----------

Currently I'm the only developer for this project, but I'm looking for interested people to
help me create a stable and well accepted base implementation in Java on which to build many
wonderful services :) 

Just contact me here or feel free to fork, but don't forget to file a Pull request should you
have something nice for us :)
