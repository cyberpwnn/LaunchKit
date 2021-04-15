package org.cyberpwn.launchkit.util;

import art.arcane.quill.collections.atomics.AtomicDoubleArray;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Task {
    @Getter
    @Setter
    private double progress;

    public Task()
    {
        progress = 0;
    }

    public abstract void execute();
}
