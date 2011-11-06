package com.manning.hip.ch2;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SmallFilesMapReduce {

  @SuppressWarnings("deprecation")
  public static void main(String... args) throws Exception {
    JobConf job = new JobConf();
    job.setJarByClass(SmallFilesMapReduce.class);
    Path input = new Path(args[0]);
    Path output = new Path(args[1]);

    output.getFileSystem(job).delete(output);

    AvroJob.setInputSchema(job, SmallFilesWrite.SCHEMA);   //<co id="ch02_smallfilemr_comment1"/>

    job.setOutputFormat(TextOutputFormat.class);

    AvroJob.setMapperClass(job, Mapper.class);             //<co id="ch02_smallfilemr_comment2"/>
    FileInputFormat.setInputPaths(job, input);
    FileOutputFormat.setOutputPath(job, output);

    job.setNumReduceTasks(0);                     // map-only

    JobClient.runJob(job);
  }

  public static class Mapper
      extends AvroMapper<GenericRecord, Pair<Void, Void>> { //<co id="ch02_smallfilemr_comment3"/>
    @Override
    public void map(GenericRecord r,
                    AvroCollector<Pair<Void, Void>> collector,
                    Reporter reporter) throws IOException {
      String filename = (String)
        r.get(SmallFilesWrite.FIELD_FILENAME);    //<co id="ch02_smallfilemr_comment4"/>
      String md5 = DigestUtils.md5Hex(
            ((ByteBuffer) r.get(SmallFilesWrite.FIELD_CONTENTS))
              .array());
      System.out.println(filename + ": " + md5);
    }
  }
}