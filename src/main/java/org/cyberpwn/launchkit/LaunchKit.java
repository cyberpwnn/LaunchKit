package org.cyberpwn.launchkit;

import art.arcane.quill.collections.KList;
import art.arcane.quill.logging.L;

import java.io.File;

public class LaunchKit {
    public static KList<String> args;
    public static LaunchKit instance;

    public LaunchKit()
    {

    }

    public static void main(String[] a)
    {
        args = KList.from(a);
        L.i("Starting LaunchKit");
        instance = new LaunchKit();
    }
}
