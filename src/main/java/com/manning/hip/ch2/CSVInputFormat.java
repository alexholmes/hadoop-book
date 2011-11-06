package com.manning.hip.ch2;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.compress.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;

import java.io.IOException;

/**
 * An {@link org.apache.hadoop.mapreduce.InputFormat} for CSV
 * plain text files.  Keys are byte offsets in
 * the file, and values are {@link ArrayWritable}'s with tokenized
 * values.
 */
public class CSVInputFormat extends
    FileInputFormat<LongWritable, ArrayWritable> {

  public static String CSV_TOKEN_SEPARATOR_CONFIG =
      "csvinputformat.token.delimiter";

  @Override
  public RecordReader<LongWritable, ArrayWritable>
  createRecordReader(InputSplit split,
                     TaskAttemptContext context) {
    String csvDelimiter = context.getConfiguration().get( //<co id="ch02_comment_csv_inputformat1"/>
        CSV_TOKEN_SEPARATOR_CONFIG);

    Character separator = null;
    if(csvDelimiter != null && csvDelimiter.length() == 1) {
      separator = csvDelimiter.charAt(0);
    }

    return new CSVRecordReader(separator);             //<co id="ch02_comment_csv_inputformat2"/>
  }

  @Override
  protected boolean isSplitable(JobContext context, Path file) {
    CompressionCodec codec =
        new CompressionCodecFactory(context.getConfiguration())
            .getCodec(file);
    return codec == null;    //<co id="ch02_comment_csv_inputformat3"/>
  }

  public static class CSVRecordReader              //<co id="ch02_comment_csv_inputformat4"/>
      extends RecordReader<LongWritable, ArrayWritable> {
    private LineRecordReader reader;
    private ArrayWritable value;
    private final CSVParser parser;

    public CSVRecordReader(Character csvDelimiter) {
      this.reader = new LineRecordReader();
      if (csvDelimiter == null) {
        parser = new CSVParser();             //<co id="ch02_comment_csv_inputformat5"/>
      } else {
        parser = new CSVParser(csvDelimiter);
      }
    }

    @Override
    public void initialize(InputSplit split,
                           TaskAttemptContext context)
        throws IOException, InterruptedException {
      reader.initialize(split, context);     //<co id="ch02_comment_csv_inputformat6"/>
    }

    @Override
    public boolean nextKeyValue()
        throws IOException, InterruptedException {
      if (reader.nextKeyValue()) {       //<co id="ch02_comment_csv_inputformat7"/>
        loadCSV();                        //<co id="ch02_comment_csv_inputformat8"/>
        return true;
      } else {
        value = null;
        return false;
      }
    }

    private void loadCSV() throws IOException {            //<co id="ch02_comment_csv_inputformat9"/>
      String line = reader.getCurrentValue().toString();
      String[] tokens = parser.parseLine(line);            //<co id="ch02_comment_csv_inputformat10"/>
      value = new ArrayWritable(tokens);
    }

    @Override
    public LongWritable getCurrentKey()      //<co id="ch02_comment_csv_inputformat11"/>
        throws IOException, InterruptedException {
      return reader.getCurrentKey();
    }

    @Override
    public ArrayWritable getCurrentValue()    //<co id="ch02_comment_csv_inputformat12"/>
        throws IOException, InterruptedException {
      return value;
    }

    @Override
    public float getProgress()
        throws IOException, InterruptedException {
      return reader.getProgress();
    }

    @Override
    public void close() throws IOException {
      reader.close();
    }
  }
}
