binson-java/release-notes.md
============================

Releases of binson-java.


2.2, 2018-xx-xx
===============

The support for *Binson strings* has been removed. The JSON string format
is usually enough and Binson strings were not much used.



2.1, 2018-01-08
===============

The BinsonParser class improved to allow for better control of memory allocation.
The client of the library can now set a maximum supported size of a Binson 
object before the parsing is done. This can be important security.
In the previous release an OutOfMemoryException could be provoked by a 
maliciously crafted Binson object.


2.0, 2017-09-26
===============

Stable release, FormatException renamed to BinsonFormatException in this release.

Jar file compiled with Java 7 is included in the release. Also a Javadoc zip file. Just unzip to read the documentation. It has some examples. Also see README.md file.


1.x, 2015 - 2017
================

See releases on github.com.
