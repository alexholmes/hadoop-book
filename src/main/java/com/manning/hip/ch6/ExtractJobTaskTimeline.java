package com.manning.hip.ch6;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class ExtractJobTaskTimeline {

  public static void main(String... args) throws Exception {
    try {
      dumpTaskTimes(args);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public static void dumpTaskTimes(String... args)
      throws Exception {

    String outputFile = args[0];

    Configuration conf = new Configuration();

    FileSystem fs = FileSystem.getLocal(conf);


    JobHistory.JobInfo job = new JobHistory.JobInfo("");
    DefaultJobHistoryParser.parseJobTasks(outputFile, job, fs);

    long startTime = job.getLong(JobHistory.Keys.LAUNCH_TIME);
    long endTime = job.getLong(JobHistory.Keys.FINISH_TIME);

    List<TimeRange> mapRanges = new ArrayList<TimeRange>();
    List<TimeRange> reduceRanges = new ArrayList<TimeRange>();
    List<TimeRange> shuffleRanges = new ArrayList<TimeRange>();
    List<TimeRange> sortRanges = new ArrayList<TimeRange>();
    List<TimeRange> setupRanges = new ArrayList<TimeRange>();
    List<TimeRange> cleanupRanges = new ArrayList<TimeRange>();
    List<TimeRange> failedRanges = new ArrayList<TimeRange>();
    List<TimeRange> killedRanges = new ArrayList<TimeRange>();


    Map<String, JobHistory.Task> tasks = job.getAllTasks();
    for (JobHistory.Task task : tasks.values()) {
      for (JobHistory.TaskAttempt attempt : task.getTaskAttempts()
          .values()) {

        String taskId = attempt.get(JobHistory.Keys.TASK_ATTEMPT_ID);
        String taskType = task.get(JobHistory.Keys.TASK_TYPE);
        String taskStatus = task.get(JobHistory.Keys.TASK_STATUS);

        System.out.println(taskId + " " + taskType + " " + taskStatus);


        long taskStartTime =
            attempt.getLong(JobHistory.Keys.START_TIME);
        long taskEndTime =
            attempt.getLong(JobHistory.Keys.FINISH_TIME);

        TimeRange range =
            new TimeRange(TimeUnit.MILLISECONDS, taskStartTime,
                taskEndTime);

        if(JobHistory.Values.FAILED.name().equals(taskStatus)) {
          failedRanges.add(range);
        }
        else if(JobHistory.Values.KILLED.name().equals(taskStatus)) {
            killedRanges.add(range);
        } else {
          if (JobHistory.Values.MAP.name().equals(taskType)) {
            mapRanges.add(range);
          } else if (JobHistory.Values.REDUCE.name().equals(taskType)) {

            long shuffleEndTime =
                attempt.getLong(JobHistory.Keys.SHUFFLE_FINISHED);
            long sortEndTime =
                attempt.getLong(JobHistory.Keys.SORT_FINISHED);

            shuffleRanges.add(
                new TimeRange(TimeUnit.MILLISECONDS, taskStartTime,
                    shuffleEndTime));
            sortRanges.add(
                new TimeRange(TimeUnit.MILLISECONDS, shuffleEndTime,
                    sortEndTime));
            reduceRanges.add(
                new TimeRange(TimeUnit.MILLISECONDS, sortEndTime,
                    taskEndTime));
          } else if (JobHistory.Values.SETUP.name().equals(taskType)) {
            setupRanges.add(range);
          } else if (JobHistory.Values.CLEANUP.name().equals(taskType)) {
            cleanupRanges.add(range);
          }
          }
      }
    }

    // output the data, tab-separated in the following order:
    // time-offset  #-map-tasks  #-reduce-tasks  #-shuffle-tasks  #-sort-tasks  #-waste-tasks
    // steps of 1 second
    StringBuilder sb = new StringBuilder();
    sb.append("TIME")
        .append("\tMAP")
        .append("\tREDUCE")
        .append("\tSHUFFLE")
        .append("\tSORT")
        .append("\tSETUP")
        .append("\tCLEANUP")
        .append("\tFAILED")
        .append("\tKILLED")
    ;
    System.err.println(sb);

    int timeOffset = 0;
    for (long i = startTime; i <= endTime; i += 1000) {
      sb = new StringBuilder();
      sb.append(timeOffset)
          .append("\t").append(countRangesForTime(mapRanges, i))
          .append("\t").append(countRangesForTime(reduceRanges, i))
          .append("\t").append(countRangesForTime(shuffleRanges, i))
          .append("\t").append(countRangesForTime(sortRanges, i))
          .append("\t").append(countRangesForTime(setupRanges, i))
          .append("\t").append(countRangesForTime(cleanupRanges, i))
          .append("\t").append(countRangesForTime(failedRanges, i))
          .append("\t").append(countRangesForTime(killedRanges, i))
      ;

      System.err.println(sb);
      timeOffset++;

    }

  }

  public static int countRangesForTime(List<TimeRange> ranges,
                                       long time) {
    int count = 0;
    for (TimeRange range : ranges) {
      if (range.inRange(TimeUnit.MILLISECONDS, time)) {
        count++;
      }
    }
    return count;
  }

  public static class TimeRange {
    final long startTimeMillis;
    final long endTimeMillis;

    public TimeRange(TimeUnit unit, long start, long end) {
      startTimeMillis = unit.toMillis(start);
      endTimeMillis = unit.toMillis(end);
    }

    public boolean inRange(TimeUnit unit, long value) {
      long millis = unit.toMillis(value);
      return millis >= startTimeMillis && millis <= endTimeMillis;
    }
  }

}
