package org.cyberpwn.launchkit.launcher;

import art.arcane.quill.cache.AtomicCache;
import lombok.Data;
import lombok.Getter;

import java.io.File;

public class MinecraftLauncher implements Launcher
{
    @Getter
    private final String instanceId;
    private final File dataFolder;
    private final AtomicCache<File> minecraftFolder = new AtomicCache<>();
    private final AtomicCache<File> launcherFolder = new AtomicCache<>();
    private final AtomicCache<File> cacheFolder = new AtomicCache<>();

    public MinecraftLauncher(String instanceId, File dataFolder)
    {
        this.instanceId = instanceId;
        this.dataFolder = dataFolder;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public File getMinecraftDataFolder() {
        return minecraftFolder.aquire(() -> new File(getDataFolder(), "instances/" + instanceId + "/.minecraft"));
    }

    @Override
    public File getLauncherDataFolder() {
        return launcherFolder.aquire(() -> new File(getDataFolder(), ".launchkit"));
    }

    @Override
    public File getCacheDataFolder() {
        return cacheFolder.aquire(() -> new File(getLauncherDataFolder(), "cache"));
    }
}
