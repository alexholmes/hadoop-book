#! /usr/bin/env bash
#
# JAR.sh: run a MapReduce example from the book
#

# get the current directory
bin=`dirname "$0"`
bin=`cd "$bin">/dev/null; pwd`

# check command line args
if [[ $# == 0 ]]; then
  echo "usage: $(basename $0) <example-name>"
  exit 1;
fi

if [ -z "$HADOOP_CONF_DIR" ]; then
  echo "HADOOP_CONF_DIR must be defined and refer to your Hadoop config directory"
  exit 2;
fi

JAVA_HEAP_MAX=-Xmx512m

if [ ! -f "$HADOOP_BIN" ]; then
  echo "HADOOP_BIN must be set and point to the hadoop script"
  echo "If hadoop is already in the path then this is as simple as export HADOOP_BIN=`which hadoop`"
  exit 3;
fi


"$HADOOP_BIN" jar ${bin}/../target/hadoop-book-1.0.jar "$@"
