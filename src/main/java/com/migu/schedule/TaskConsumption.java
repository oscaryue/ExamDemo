package com.migu.schedule;

import com.migu.schedule.info.TaskInfo;

public class TaskConsumption {
    private TaskInfo mTask;
    private int mConsumption;

    public TaskConsumption(TaskInfo task, int consumption) {
        mTask = task;
        mConsumption = consumption;
    }

    public TaskInfo getTask() {
        return mTask;
    }

    public int getConsumption() {
        return mConsumption;
    }
}
