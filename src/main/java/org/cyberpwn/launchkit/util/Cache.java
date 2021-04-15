package org.cyberpwn.launchkit.util;

import java.io.File;

public class Cache
{
    private transient final File root;

    public Cache(File root)
    {
        this.root = root;
    }
}
