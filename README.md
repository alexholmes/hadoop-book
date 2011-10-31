Source code for book "Hadoop in Practice", Manning Publishing
=============================================================


## Building and running

1.  Download from github

<pre><code>
git clone git://github.com/alexholmes/hadoop-book.git
</code></pre>

2.  Build

<pre><code>
cd hadoop-book
mvn package
</code></pre>

3.  Run an example

<pre><code>
bin/jar.sh com.manning.hip.ch1.InvertedIndexMapReduce \
test-data/ch1/file1.txt test-data/ch1/file2.txt output
</code></pre>

