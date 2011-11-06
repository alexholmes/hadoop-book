package com.manning.hip.ch2;


import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.*;

import java.io.IOException;

public final class CSVMapReduce {
  public static class Map extends Mapper<LongWritable, ArrayWritable, //<co id="ch02_comment_csv_mr1"/>
      LongWritable, Text> {

    @Override
    protected void map(LongWritable key, ArrayWritable value,
                       Context context)
        throws
        IOException, InterruptedException {

      String[] tokens = value.toStrings();
      context.write(key,                                //<co id="ch02_comment_csv_mr2"/>
          new Text(StringUtils.join(tokens, ",")));
    }
  }

  public static void main(String... args) throws Exception {
    runJob(args[0], args[1]);
  }

  public static void runJob(String input,
                            String output)
      throws Exception {
    Configuration conf = new Configuration();
    conf.set(CSVInputFormat.CSV_TOKEN_SEPARATOR_CONFIG, "\t");  //<co id="ch02_comment_csv_mr3"/>

    Job job = new Job(conf);
    job.setJarByClass(CSVMapReduce.class);
    job.setMapperClass(Map.class);
    job.setInputFormatClass(CSVInputFormat.class); //<co id="ch02_comment_csv_mr4"/>
    job.setNumReduceTasks(0);

    FileInputFormat.setInputPaths(job, new Path(input));
    Path outPath = new Path(output);
    FileOutputFormat.setOutputPath(job, outPath);

    outPath.getFileSystem(conf).delete(outPath, true);

    job.waitForCompletion(true);
  }
}
