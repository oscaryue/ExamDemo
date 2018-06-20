package com.migu.schedule;


import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/*
 *类名和方法不能修改
 */
public class Schedule {

    // pending tasks
    private ArrayList<TaskConsumption> mPendingTasks = new ArrayList<>();
    // server nodes
    private ArrayList<ServerNode> mServerNodeList = new ArrayList<>();

    public int init() {
        initPendingTasks();
        initServerNodes();
        return ReturnCodeKeys.E001;
    }


    public int registerNode(int nodeId) {
        // Id must be greater than 0
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }

        // let me see if this id is already in the nodes list
        synchronized (mServerNodeList) {
            for (ServerNode node : mServerNodeList) {
                if (node.getId() == nodeId) {
                    return ReturnCodeKeys.E005;
                }
            }
        }

        // cool, let me add it to the nods list
        addNode(new ServerNode(nodeId));

        return ReturnCodeKeys.E003;
    }

    public int unregisterNode(int nodeId) {
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }
        synchronized (mServerNodeList) {
            ServerNode node = getNodeWithId(nodeId);
            if (node != null) {
                // if here are some working task on this node
                // we need to move them to the pending list
                ArrayList<TaskConsumption> tasks = node.getTaskList();
                if (tasks.size() > 0) {
                    for (int i = 0; i < tasks.size(); i++) {
                        addPendingTask(tasks.get(i).getTask(), tasks.get(i).getConsumption());
                    }
                }
                mServerNodeList.remove(node);
                return ReturnCodeKeys.E006;
            } else {
                return ReturnCodeKeys.E007;
            }
        }
    }


    public int addTask(int taskId, int consumption) {
        // TODO 方法未实现
        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }

        // go through the pending list to make sure the same Id is not here
        synchronized (mPendingTasks) {
            for (int i = 0; i < mPendingTasks.size(); i++) {
                if (mPendingTasks.get(i).getTask().getTaskId() == taskId) {
                    return ReturnCodeKeys.E010;
                }
            }
        }

        // now we can add it into the pending tasks list
        TaskInfo task = new TaskInfo();
        task.setTaskId(taskId);
        addPendingTask(task, consumption);

        return ReturnCodeKeys.E008;
    }


    public int deleteTask(int taskId) {
        // TODO 方法未实现
        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }

        boolean success = false;
        // deleting task from pending task list
        success = deletePendingTask(taskId);

        if (!success) {
            // deleting tsk from server node
            deleteWorkingTask(taskId);
        }
        if (success) {
            return ReturnCodeKeys.E011;
        } else {
            return ReturnCodeKeys.E012;
        }
    }


    public int scheduleTask(int threshold) {
        // TODO 方法未实现
        if (threshold <= 0) {
            return ReturnCodeKeys.E002;
        }
        
        return ReturnCodeKeys.E000;
    }


    public int queryTaskStatus(List<TaskInfo> tasks) {
        if (tasks == null) {
            return ReturnCodeKeys.E016;
        }
        //clear the list
        tasks.clear();

        // adding tasks from pending list
        synchronized (mPendingTasks) {
            for (int i = 0; i < mPendingTasks.size(); i++) {
                tasks.add(mPendingTasks.get(i).getTask());
            }
        }
        // adding tasks from working list
        synchronized (mServerNodeList) {
            for (int i = 0; i < mServerNodeList.size(); i++) {
                ArrayList<TaskConsumption> taskList = mServerNodeList.get(i).getTaskList();
                for (int j = 0; j < taskList.size(); j++) {
                    tasks.add(taskList.get(i).getTask());
                }
            }
        }
        // sorting
        TaskInfo t = null;
        for (int i = 0; i < tasks.size() - 1; i++) {
            for (int j = 0; j < tasks.size() - 1 - i; j++) {
                if (tasks.get(j).getTaskId() > tasks.get(j + 1).getTaskId()) {
                    t = tasks.get(j);
                    tasks.set(j, tasks.get(j + 1));
                    tasks.set(j + 1, t);
                }
            }
        }
        return ReturnCodeKeys.E015;
    }

    private void initPendingTasks() {
        synchronized (mPendingTasks) {
            mPendingTasks.clear();
        }
    }

    private void initServerNodes() {
        synchronized (mServerNodeList) {
            mServerNodeList.clear();
        }
    }

    private void addNode(ServerNode node) {
        if (node == null) {
            return;
        }

        synchronized (mServerNodeList) {
            mServerNodeList.add(node);
        }
    }

    private void addPendingTask(TaskInfo task, int consumption) {
        if (task == null) {
            return;
        }

        task.setNodeId(-1);
        synchronized (mPendingTasks) {
            mPendingTasks.add(new TaskConsumption(task, consumption));
        }
    }

    private boolean deletePendingTask(int taskId) {
        if (taskId <= 0) {
            return false;
        }

        boolean found = false;
        synchronized (mPendingTasks) {
            for (int i = 0; i < mPendingTasks.size() && !found; i++) {
                TaskConsumption taskConsumption = mPendingTasks.get(i);
                if (taskConsumption.getTask().getTaskId() == taskId) {
                    // find it and remove it
                    mPendingTasks.remove(taskConsumption);
                    found = true;
                }
            }
        }
        return found;
    }

    private boolean deleteWorkingTask(int taskId) {
        if (taskId <= 0) {
            return false;
        }

        boolean found = false;
        synchronized (mServerNodeList) {
            for (int i = 0; i < mServerNodeList.size() && !found; i++) {
                ServerNode node = mServerNodeList.get(i);
                found = node.deleteTask(taskId);
            }
        }
        return found;
    }

    private ServerNode getNodeWithId(int id) {
        if (id <= 0) {
            return null;
        }

        synchronized (mServerNodeList) {
            for (ServerNode node : mServerNodeList) {
                if (node.getId() == id) {
                    return node;
                }
            }
            return null;
        }
    }
}
