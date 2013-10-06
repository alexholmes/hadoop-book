Source code for book "Hadoop in Practice", Manning Publishing
=============================================================

## Overview

This repo contains the code, scripts and data files that are referenced
from the book [Hadoop in Practice](http://www.manning.com/holmes/), published by Manning.

##  Issues

If you hit any compilation or execution problems please create an issue
and I'll look into it as soon as I can.

## Hadoop Version

All the code has been exercised against CDH3u2, which for the purposes
of the code is the same has Hadoop 0.20.x.  There are a couple of places
where I utilize some features in Pig 0.9.1, which won't work with CDH3u1
which uses 0.8.1.

I've recently run some basic MapReduce jobs against CDH4, however I have
yet to fix the problems associated with the JobContext changing from a
class to an interface between Hadoop versions 1 and 2, so I expect the
following examples to fail:

* CSVInputFormat (chapter 3)
* JsonInputFormat (chapter 3)
* ReservoirSamplerInputFormat (chapter 4)
* CommonLogInputFormat (used in Pig and Hive chapters)


## Building and running

####  Download from github

<pre><code>git clone git://github.com/alexholmes/hadoop-book.git
</code></pre>

####  Build

<pre><code>cd hadoop-book
mvn package
</code></pre>

#### Runtime Dependencies

Many of the examples use Snappy and LZOP compression.  Therefore
you may get runtime errors if you don't have them installed and configured
in your cluster.

Snappy can be installed on CDH by following the instructions at
https://ccp.cloudera.com/display/CDHDOC/Snappy+Installation.

To install LZOP follow the instructions at https://github.com/kevinweil/hadoop-lzo.

####  Run an example

<pre><code># copy the input files into HDFS
hadoop fs -mkdir /tmp
hadoop fs -put test-data/ch1/* /tmp/

# replace the path below with the location of your Hadoop installation
# this isn't required if you are running CDH3
export HADOOP_HOME=/usr/local/hadoop

# run the map-reduce job
bin/run.sh com.manning.hip.ch1.InvertedIndexMapReduce /tmp/file1.txt /tmp/file2.txt output
</code></pre>
