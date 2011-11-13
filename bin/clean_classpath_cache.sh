#! /usr/bin/env bash
#
# clean_classpath_cache.sh: helps executing the examples by setting up the Java CLASSPATH.
#

# get the current directory
bin=`dirname "$0"`
bin=`cd "$bin">/dev/null; pwd`

# create and cache Maven classpath
cpfile="${bin}/../target/cached_classpath.txt"
rm -rf $cpfile
