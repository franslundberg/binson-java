binson-java/release-notes.md
============================

Releases of binson-java.


3.4, 2019-05-29
===============

* Added method Binson.fieldNames(). It returns a sorted list of field names.
  Can be used to iterate the fiels of a Binson object.



3.3, 2018-07-04
===============

* Added limit to number of fields that the parser accepts. By default, BinsonParser now
  accepts no more than 1000 fields of a Binson object. The actual limit can be set; 
  see BinsonParser.setMaxFieldCount().
  
* Some improvements to code beauty.


3.2, 2018-06-09
===============

* Strict parsing for integer ranges now enforced. As specified in BINSON-SPEC-1, integers
  MUST be stored with as few bytes as possible. This is now enforced by the parser.
  Data that violates this, will result in a BinsonFormatException.


3.1, 2018-05-18
===============

* Strict parsing introduced. See Issue #6.


3.0, 2018-01-10
===============

* Support for *Binson strings* removed.
* The Binson class now does *not* implement Map.
* The BinsonArray class now does *not* implement List.


2.1, 2018-01-08
===============

The BinsonParser class improved to allow for better control of memory allocation.
The client of the library can now set a maximum supported size of a Binson 
object before the parsing is done. This can be important for security.
In the previous release an OutOfMemoryException could be provoked by a 
maliciously crafted Binson object.


2.0, 2017-09-26
===============

Stable release, FormatException renamed to BinsonFormatException in this release.

Jar file compiled with Java 7 is included in the release. Also a Javadoc zip file. Just unzip to read the documentation. It has some examples. Also see README.md file.


1.x, 2015 - 2017
================

See releases on github.com.
