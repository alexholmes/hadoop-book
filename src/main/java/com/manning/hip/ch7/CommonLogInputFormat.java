package com.manning.hip.ch7;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Assumes one line per log entry object
 */
public class CommonLogInputFormat
  extends FileInputFormat<LongWritable, CommonLogEntry> {

  @Override
  public RecordReader<LongWritable, CommonLogEntry> createRecordReader(
    InputSplit split,
    TaskAttemptContext
      context) {
    return new CommonLogRecordReader();
  }

  @Override
  protected boolean isSplitable(JobContext context, Path file) {
    CompressionCodec codec =
      new CompressionCodecFactory(context.getConfiguration())
        .getCodec(file);
    return codec == null;
  }

  public static class CommonLogRecordReader
    extends RecordReader<LongWritable, CommonLogEntry> {

    private static final Logger log =
        LoggerFactory.getLogger(CommonLogRecordReader.class);


    private LineRecordReader reader = new LineRecordReader();

    private CommonLogEntry value_ = new CommonLogEntry();

    ApacheCommonLogParser parser = new ApacheCommonLogParser();
    private SimpleDateFormat sdf =
      new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z");


    @Override
    public void initialize(InputSplit split,
                           TaskAttemptContext context)
      throws IOException, InterruptedException {
      reader.initialize(split, context);
    }

    @Override
    public synchronized void close() throws IOException {
      reader.close();
    }

    @Override
    public LongWritable getCurrentKey() throws IOException,
      InterruptedException {
      return reader.getCurrentKey();
    }

    @Override
    public CommonLogEntry getCurrentValue() throws IOException,
      InterruptedException {
      return value_;
    }

    @Override
    public float getProgress()
      throws IOException, InterruptedException {
      return reader.getProgress();
    }

    @Override
    public boolean nextKeyValue()
      throws IOException, InterruptedException {
      while (reader.nextKeyValue()) {
        if ((value_ = decodeLine(reader.getCurrentValue())) != null) {
          return true;
        }
      }
      return false;
    }

    public CommonLogEntry decodeLine(Text line) throws IOException {
      CommonLogEntry e = new CommonLogEntry();

      String parts[] = parser.parseLine(line.toString());

      if (parts == null || parts.length != 8) {
        return null;
      }

      e.setRemoteAddress(getAsString(parts[0]));
      e.setRemoteLogname(getAsString(parts[1]));
      e.setUserId(getAsString(parts[2]));
      e.setTime(getAsString(parts[3] + " " + parts[4]));
      e.setRequestLine(getAsString(parts[5]));
      e.setStatusCode(getAsLong(parts[6]));
      e.setObjSize(getAsLong(parts[7]));

      if (e.getRequestLine() != null) {
        String[] requestParts = e.getRequestLine().split(" ");
        e.setMethod(requestParts[0]);
        e.setResource(requestParts[1]);
        e.setProtocol(requestParts[2]);
      }

      // epoch
      String trimmedDate = e.getTime()
        .substring(1, e.getTime().length() - 1);

      try {
        e.setEpoch(sdf.parse(trimmedDate).getTime());
      } catch (java.text.ParseException e1) {
        log.error("Parse error with '" + trimmedDate + "'", e1);
      }
      return e;
    }

    public static boolean isNull(String part) {
      return StringUtils.isEmpty(part) || "-".equals(part);
    }

    public static String getAsString(String part) {
      if (isNull(part)) {
        return null;
      }
      return part;
    }

    public static Long getAsLong(String part) {
      if (isNull(part)) {
        return null;
      }
      return Long.valueOf(part);
    }
  }
}
