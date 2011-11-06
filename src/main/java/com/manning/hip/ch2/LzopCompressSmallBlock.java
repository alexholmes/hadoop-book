package com.manning.hip.ch2;

import com.hadoop.compression.lzo.*;
import com.hadoop.mapreduce.LzoTextInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

import java.io.IOException;
import java.util.List;

public class LzopCompressSmallBlock {

  public static void main(String... args) throws Exception {
    Configuration config = new Configuration();

    LzopCodec codec = new LzopCodec();
    codec.setConf(config);

    Path srcFile = new Path(args[0]);
    int blockSize = Integer.valueOf(args[1]);
    compress(srcFile, blockSize, config);
  }

  public static Path compress(Path src, int blockSize,
                              Configuration config)
      throws IOException {

    System.out.println("Compressing");

    Configuration tmpConfig = new Configuration(config);
//    tmpConfig.setLong("dfs.block.size", blockSize);
//    tmpConfig.setInt(LzoCodec.LZO_BUFFER_SIZE_KEY, blockSize);

    Path compressedFile = LzopFileReadWrite.compress(src, tmpConfig);

    compressedFile.getFileSystem(tmpConfig)
        .delete(new Path(compressedFile.toString() + ".index"), false);

    System.out.println("Indexing");
    new LzoIndexer(tmpConfig).index(compressedFile);

    Job job = new Job(tmpConfig);
    job.setInputFormatClass(LzoTextInputFormat.class);
    LzoTextInputFormat inputFormat = new LzoTextInputFormat();
    TextInputFormat.setInputPaths(job, compressedFile);

    List<InputSplit> is = inputFormat.getSplits(job);

    System.out.println("Number of splits = " + is.size());

    return compressedFile;
  }

}
