package com.migu.schedule;

import com.migu.schedule.info.TaskInfo;

import java.util.ArrayList;

public class ServerNode {
    public static final int NODE_ID_UNINIT = 0;
    private int mId = NODE_ID_UNINIT; // this ID must be greater than 0
    private ArrayList<TaskConsumption> mTaskList = new ArrayList<>();

    public ServerNode(int id) {
        mId = id;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void addTask(TaskInfo task, int consumption) {
        task.setNodeId(mId);
        synchronized (mTaskList) {
            mTaskList.add(new TaskConsumption(task, consumption));
        }
    }

    public ArrayList<TaskConsumption> getTaskList() {
        synchronized (mTaskList) {
            return mTaskList;
        }
    }

    /**
     *
     * @param taskId
     * @return return true if the task is removed successfully
     */
    public boolean deleteTask(int taskId) {
        if (taskId <= 0) {
            return false;
        }

        synchronized (mTaskList) {
            for (int i = 0; i < mTaskList.size(); i++) {
                TaskConsumption taskConsumption = mTaskList.get(i);
                if (taskConsumption.getTask().getTaskId() == taskId) {
                    // find it and remove it
                    mTaskList.remove(taskConsumption);
                    return true;
                }
            }
        }
        return false;
    }

    public int getConsumption() {
        int consumption = 0;
        synchronized (mTaskList) {
            for (int i = 0; i < mTaskList.size(); i++) {
                consumption += mTaskList.get(i).getConsumption();
            }
        }
        return consumption;
    }
}
