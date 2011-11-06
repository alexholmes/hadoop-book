package com.manning.hip.ch3;

import com.manning.hip.ch2.CSVParser;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.DefaultCodec;

import java.io.File;
import java.io.IOException;

public class SequenceFileStockWriter {

  public static void main(String... args) throws IOException {
    write(new File(args[0]), new Path(args[1]));
  }

  public static void write(File inputFile, Path outputPath)
      throws IOException {
    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.get(conf);

    CSVParser parser = new CSVParser();

    SequenceFile.Writer writer =    //<co id="ch03_comment_seqfile_write1"/>
        SequenceFile.createWriter(fs, conf, outputPath, Text.class,
            StockPriceWritable.class,
            SequenceFile.CompressionType.BLOCK,
            new DefaultCodec());
    try {
      Text key = new Text();

      for (String line : FileUtils.readLines(inputFile)) {   //<co id="ch03_comment_seqfile_write2"/>
        String[] parts = parser.parseLine(line);

        StockPriceWritable stock = new StockPriceWritable(   //<co id="ch03_comment_seqfile_write3"/>
            parts[0], parts[1], Double.valueOf(parts[2]),
            Double.valueOf(parts[3]),
            Double.valueOf(parts[4]),
            Double.valueOf(parts[5]),
            Integer.valueOf(parts[6]),
            Double.valueOf(parts[7])
        );

        key.set(parts[0]);
        writer.append(key,
            stock);        //<co id="ch03_comment_seqfile_write4"/>
      }
    } finally {
      writer.close();
    }
  }
}
