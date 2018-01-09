binson-java
===========

A Java implementation of Binson. Binson is like JSON, but faster, binary and 
even simpler. See [binson.org](http://binson.org/).


Use
===

Add binson.jar to your project. There are no other dependencies.
Most developers only need to use the Binson class.

The code below first creates a Binson object with two fields: one integer named 'a' and 
one string named 's'. Then 'obj' is serialized to bytes. The third line parses back the 
bytes to a Binson object again, and the last line checks that the parsed Binson object is 
the same as the original one.

    Binson obj = new Binson().put("a", 123).put("s", "Hello world!");
    byte[] bytes = obj.toBytes();
    Binson obj2 = Binson.fromBytes(bytes);
    assert obj2.equals(obj);

This library also contains JSON (http://json.org/) support. See methods 
Binson.fromJson() and Binson.toJson().


Build
=====

Build with ANT:

    ant

Other build tools should be easy to adapt for the source.
The code in src/ only depends on the JVM (Java 6 or later should work).

The tests in src-test/ depends on JUnit (junit-4.12.jar, hamcrest-core-1.3.jar).


Log
===

Log entries, latest entry first, format: YYMMDD.


## 180108

Frans: Tested the tool abnfgen (http://www.quut.com/abnfgen/). Not very useful since most
generated random abnf documents will not be valid Binson due to Binson's additional
rules beyond following the ABNF format. Most often, sizes of strings or bytes will
be wrong. This is expected. 

However, I did find an issue (known, but not fixed). It triggered rewriting BinsonParser
to support more controlled memory allocation. Now there is a maxSize setting; the
maximum size a Binson object should have, otherwise the Parser will throw a 
BinsonFormatException when parsing. This is important. In previous versions, 
a maliciously crafted Binson object could be used to provoke an OutOfMemoryException.


## 170926 

Frans: Release 2.0. Renaming: FormatException to BinsonFormatException.
Stable code: No known bugs, 225 successful unit tests, good coverage, no warnings. 
There has been zero known bugs since November 2015 and the library has been used 
heavily since then. Perhaps fuzz testing could find a few bugs, though? Would be
interesting to try https://github.com/nradov/abnffuzzer.


## 170816

Frans: Release 1.5. The BinsonArray.getXxx() method now throws
FormatException for unexpected element types.


## 170507

Frans: Release 1.4. Minor changes from previous release.
Note, this release was not added to the Github releases.


## 160128

Frans: Bug in field sort order fixed thanks to a bug report 
by Alexander Reshniuk.


## 151004

Frans: Release 1.1.


## 150925

Frans: Release 1.0.


## 150912

Frans: Added Binson Schema validation (might be called BINSON-SCHEMA-1) 
in 40 lines of code.


## 140911

Frans: First upload to github.com. Before that I stored it locally.

