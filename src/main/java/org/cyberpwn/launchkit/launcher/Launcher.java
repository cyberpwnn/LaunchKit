package org.cyberpwn.launchkit.launcher;

import java.io.File;
import java.io.IOException;

public interface Launcher {
    public File getDataFolder();

    public File getMinecraftDataFolder();

    public File getLauncherDataFolder();

    public File getCacheDataFolder();

    public String getInstanceId();

    public String getForgeVersion();

    public String getGameVersion();

    public String getName();

    public String getJVMArgs();

    public int getMaxMem();

    public int getMinMem();

    public void start() throws IOException, InterruptedException, ClassNotFoundException;

    public File getAssetsFolder();

    public File getAssetsObjectsFolder();

    public File getAssetsIndexesFolder();

    public File getMinecraftVersionFile();

    public File getVersionManifestFile();

    public File getForgeVersionFile();

    public File getMinecraftClientFile();

    public File getLauncherLibrariesFolder();

    public File getLauncherNativesFolder();

    public File getForgeUniversalFile();
}
