package org.cyberpwn.launchkit.launcher;

import java.io.File;

public interface Launcher {
    public File getDataFolder();

    public File getMinecraftDataFolder();

    public File getLauncherDataFolder();

    public File getCacheDataFolder();

    public String getInstanceId();
}
