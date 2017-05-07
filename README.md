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

Build with ANT and build.xml or any other build tool.
The code in src/ only depends on the JVM (Java 6 should work).

The tests in src-test/ depends on JUnit (junit-4.12.jar, hamcrest-core-1.3.jar).


Log
===

Log entries, latest entry first, format: YYMMDD.

## 170507

Release 1.4. Minor changes from 1.3.

## 160128

Bug in field sort order fixed thanks to a bug report by Alexander Reshniuk.

## 151004

Release 1.1.

## 150925

Release 1.0.

## 150912

Added Binson Schema validation (might be called BINSON-SCHEMA-1) in 40 lines of code.

## 140911

First upload to github.com.

