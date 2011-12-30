#! /usr/bin/env bash
##########################################################################
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
##########################################################################
#
# run.sh:  Launch a code example from the book "Hadoop in Practice"
#
# Pre-requisites:
# 1)  JAVA_HOME is set
# 2)  MAVEN_HOME is set and points to the local Maven installation
# 3)  HADOOP_HOME is set, and $HADOOP_HOME/conf contains your cluster
#     configuration
#
# If running on a CDH host with standard CDH directory locations in place,
# then you won't need to set HADOOP_HOME.
#
##########################################################################

# resolve links - $0 may be a softlink
PRG="${0}"

while [ -h "${PRG}" ]; do
  ls=`ls -ld "${PRG}"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "${PRG}"`/"$link"
  fi
done

# check command line args
if [[ $# == 0 ]]; then
  echo "usage: $(basename $0) <example-name>"
  exit 1;
fi


BASEDIR=`dirname ${PRG}`
BASEDIR=`cd ${BASEDIR}/..;pwd`

echo $BASEDIR

CDH_HADOOP_HOME=/usr/lib/hadoop

if [ ! -d "${HADOOP_HOME}" ]; then
  if [ -d "${CDH_HADOOP_HOME}" ]; then
    HADOOP_HOME=${CDH_HADOOP_HOME}
    echo "HADOOP_HOME environment not set, but found ${HADOOP_HOME} in path so using that"
  else
    echo "HADOOP_HOME must be set and point to the hadoop home directory"
    exit 2;
  fi
fi

HADOOP_CONF_DIR=${HADOOP_HOME}/conf

# set up Maven environment
MVN="mvn"
if [ -n "$M2_HOME" ]; then
  MVN=${M2_HOME}/bin/mvn
fi

if [ ! -d "$HADOOP_CONF_DIR" ]; then
  if [ -d "/etc/hadoop/conf" ]; then
    HADOOP_CONF_DIR="/etc/hadoop/conf"
    echo "HADOOP_CONF_DIR environment not set, but found directory $HADOOP_CONF_DIR"
  else
    echo "HADOOP_CONF_DIR must be defined and refer to your Hadoop config directory"
    exit 4;
  fi
fi

# classpath initially contains $HADOOP_CONF_DIR
CLASSPATH="${HADOOP_CONF_DIR}"

# add our JAR
CLASSPATH="${CLASSPATH}":${BASEDIR}/target/hadoop-book-1.0.jar


# create and cache Maven classpath
cpfile="${BASEDIR}/target/cached_classpath.txt"
pomfile="${BASEDIR}/pom.xml"
if [ ! -f "${cpfile}" ]; then
  echo "Generating classpath cache in $cpfile, this may take a few mins"
  echo "Run bin/clean_classpath_cache.sh to remove this file if you need to re-generate the classpath"
  ${MVN} -f "${pomfile}" dependency:build-classpath -Dmdep.outputFile="${cpfile}" &> /dev/null
else
  echo "Using cached Maven classes in $cpfile, remove this file if you want them re-generated"
fi
MVN_CLASSPATH=`cat "${cpfile}"`

function add_to_hadoop_classpath() {
  dir=$1
  for f in $dir/*.jar; do
    HADOOP_CLASSPATH=${HADOOP_CLASSPATH}:$f;
  done

  export HADOOP_CLASSPATH
}

HADOOP_LIB_DIR=$HADOOP_HOME
add_to_hadoop_classpath ${HADOOP_LIB_DIR}
HADOOP_LIB_DIR=$HADOOP_HOME/lib
add_to_hadoop_classpath ${HADOOP_LIB_DIR}

export CLASSPATH=${CLASSPATH}:${MVN_CLASSPATH}:${HADOOP_CLASSPATH}

JAVA=$JAVA_HOME/bin/java
JAVA_HEAP_MAX=-Xmx512m

# pick up the native Hadoop directory if it exists
# this is to support native compression codecs
#
if [ -d "${HADOOP_HOME}/build/native" -o -d "${HADOOP_HOME}/lib/native" -o -d "${HADOOP_HOME}/sbin" ]; then
  JAVA_PLATFORM=`CLASSPATH=${CLASSPATH} ${JAVA} -Xmx32m org.apache.hadoop.util.PlatformName | sed -e "s/ /_/g"`

  if [ -d "$HADOOP_HOME/build/native" ]; then
    if [ "x$JAVA_LIBRARY_PATH" != "x" ]; then
        JAVA_LIBRARY_PATH=${JAVA_LIBRARY_PATH}:${HADOOP_HOME}/build/native/${JAVA_PLATFORM}/lib
    else
        JAVA_LIBRARY_PATH=${HADOOP_HOME}/build/native/${JAVA_PLATFORM}/lib
    fi
  fi

  if [ -d "${HADOOP_HOME}/lib/native" ]; then
    if [ "x$JAVA_LIBRARY_PATH" != "x" ]; then
      JAVA_LIBRARY_PATH=${JAVA_LIBRARY_PATH}:${HADOOP_HOME}/lib/native/${JAVA_PLATFORM}
    else
      JAVA_LIBRARY_PATH=${HADOOP_HOME}/lib/native/${JAVA_PLATFORM}
    fi
  fi
fi

# echo $CLASSPATH

"$JAVA" $JAVA_HEAP_MAX -Djava.library.path=${JAVA_LIBRARY_PATH} -DMVN_CLASSPATH=$MVN_CLASSPATH -classpath "$CLASSPATH" "$@"
