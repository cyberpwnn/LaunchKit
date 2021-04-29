package org.cyberpwn.launchkit;

import art.arcane.quill.collections.KList;
import art.arcane.quill.io.IO;
import art.arcane.quill.logging.L;
import org.cyberpwn.launchkit.launcher.Launcher;
import org.cyberpwn.launchkit.launcher.MinecraftLauncher;

import java.io.File;

public class LaunchKit {
    public static KList<String> args;
    public static LaunchKit instance;

    public LaunchKit()
    {
        try {
            Launcher launcher = new MinecraftLauncher(KList.from(LauncherConfiguration.repository.split("\\Q/\\E")).popLast().split("\\Q.\\E")[0] + "-" + IO.hash(LauncherConfiguration.repository), new File(System.getProperty("user.home") + "/LaunchGit"));
            launcher.start();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void main(String[] a)
    {
        args = KList.from(a);

        for(String i : args)
        {
            if(i.startsWith("repo="))
            {
                LauncherConfiguration.repository = i.split("\\Q=\\E")[1];
            }
        }

        L.i("Starting LaunchKit");
        instance = new LaunchKit();
    }
}
