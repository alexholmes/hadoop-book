package com.manning.hip.ch3.json;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.slf4j.*;

import java.io.IOException;

public final class HadoopPropertiesMapReduce {
  private static final Logger log = LoggerFactory.getLogger
      (HadoopPropertiesMapReduce.class);

  public static class Map extends Mapper<LongWritable, MapWritable,
      Text, Text> {

    @Override
    protected void map(LongWritable key, MapWritable value,
                       Context context)
        throws
        IOException, InterruptedException {

      for (java.util.Map.Entry<Writable, Writable> entry : value
          .entrySet()) {
        System.out.println(
            "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"");
      }
    }
  }

  public static void main(String... args) throws Exception {
    runJob(args[0], args[1]);
  }

  public static void runJob(String input,
                            String output)
      throws Exception {
    Configuration conf = new Configuration();
    conf.set("key.value.separator.in.input.line", " ");
    conf.set("xmlinput.start", "<property>");
    conf.set("xmlinput.end", "</property>");

    Job job = new Job(conf);
    job.setJarByClass(HadoopPropertiesMapReduce.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setMapperClass(Map.class);
    job.setInputFormatClass(JsonInputFormat.class);
    job.setNumReduceTasks(0);
    job.setOutputFormatClass(TextOutputFormat.class);

    FileInputFormat.setInputPaths(job, new Path(input));
    Path outPath = new Path(output);
    FileOutputFormat.setOutputPath(job, outPath);
    outPath.getFileSystem(conf).delete(outPath, true);

    job.waitForCompletion(true);
  }
}
