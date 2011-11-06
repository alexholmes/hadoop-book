#! /usr/bin/env bash
#
# run.sh: run an example from the book
#

# get the current directory
bin=`dirname "$0"`
bin=`cd "$bin">/dev/null; pwd`

# check command line args
if [[ $# == 0 ]]; then
  echo "usage: $(basename $0) <example-name>"
  exit 1;
fi

# set up Maven environment
MVN="mvn"
if [ -n "$MAVEN_HOME" ]; then
  MVN=${MAVEN_HOME}/bin/mvn
fi

if [ ! -d "$HADOOP_CONF_DIR" ]; then
  if [ -d "/etc/hadoop/conf" ]; then
    HADOOP_CONF_DIR="/etc/hadoop/conf"
    echo "HADOOP_CONF_DIR environment not set, but found directory $HADOOP_CONF_DIR"
  else
    echo "HADOOP_CONF_DIR must be defined and refer to your Hadoop config directory"
    exit 2;
  fi
fi

# classpath initially contains $HBASE_CONF_DIR
CLASSPATH="${HADOOP_CONF_DIR}"

# add classes first, triggers log4j.properties priority
if [ -d "${bin}/../target/classes" ]; then
  CLASSPATH=${CLASSPATH}:${bin}/../target/classes
fi

# create and cache Maven classpath
cpfile="${bin}/../target/cached_classpath.txt"
if [ ! -f "${cpfile}" ]; then
  echo "Generating classpath cache in $cpfile, this may take a few mins"
  echo "Run bin/clean_classpath_cache.sh to remove this file if you need to re-generate the classpath"
  ${MVN} -f "${bin}/../pom.xml" dependency:build-classpath -Dmdep.outputFile="${cpfile}" &> /dev/null
fi
CLASSPATH=${CLASSPATH}:`cat "${cpfile}"`

JAVA=$JAVA_HOME/bin/java
JAVA_HEAP_MAX=-Xmx512m

"$JAVA" $JAVA_HEAP_MAX -classpath "$CLASSPATH" "$@"