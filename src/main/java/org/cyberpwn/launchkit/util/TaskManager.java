package org.cyberpwn.launchkit.util;

import art.arcane.quill.collections.KList;

import java.util.concurrent.ExecutorService;

public class TaskManager
{
    private final KList<Task> queue;
    private final KList<Task> enqueue;
    private boolean running;

    public TaskManager()
    {
        queue = new KList<>();
        enqueue = new KList<>();
        running = false;
    }

    public void execute(Task t)
    {
        synchronized (enqueue)
        {
            enqueue.add(t);
        }
    }
}
