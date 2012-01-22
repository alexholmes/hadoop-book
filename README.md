Source code for book "Hadoop in Practice", Manning Publishing
=============================================================


## Building and running

####  Download from github

<pre><code>git clone git://github.com/alexholmes/hadoop-book.git
</code></pre>

####  Build

<pre><code>cd hadoop-book
mvn package
</code></pre>

####  Run an example

<pre><code># copy the input files into HDFS
hadoop -mkdir /tmp
hadoop -put test-data/ch1/* /tmp/

# run the map-reduce job
bin/jar.sh com.manning.hip.ch1.InvertedIndexMapReduce \
/tmp/file1.txt /tmp/file2.txt output
</code></pre>

