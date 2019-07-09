package org.cyberpwn.launchkit;

import java.lang.reflect.Field;

import org.cyberpwn.launchkit.util.L;

public class Environment
{
	public static void set(String key, String val)
	{
		try
		{
			Field f = Environment.class.getDeclaredField(key);
			f.setAccessible(true);

			if(f.getType().equals(String.class))
			{
				f.set(null, val);
				L.LOG.v("Set " + key + "=" + val);
			}

			else if(f.getType().equals(int.class))
			{
				f.setInt(null, Integer.valueOf(val));
				L.LOG.v("Set " + key + "=" + val);
			}

			else if(f.getType().equals(boolean.class))
			{
				f.setBoolean(null, Boolean.valueOf(val));
				L.LOG.v("Set " + key + "=" + val);
			}

			else
			{
				L.LOG.w("No Environment Variable TYPE Support for: " + key);
			}
		}

		catch(Throwable e)
		{
			L.LOG.w("No Environment Variable: " + key);
		}
	}

	// Forge
	public static boolean forge_enabled = true;
	public static String forge_version = "14.23.5.2838";

	// Game
	public static String minecraft_version = "1.12.2";

	// LaunchKit
	public static String root_folder_name = "lk";
	public static String remote_path = "user.home";
	public static boolean local_fs = false;
	public static int log_level = 2;
	public static int download_threads = 8;

	// Client Process
	public static String jvm_opts = "-XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=15 -XX:+DisableExplicitGC -XX:+ParallelRefProcEnabled -XX:ParallelGCThreads=8 -XX:ConcGCThreads=3 -XX:G1HeapWastePercent=9";
	public static String jvm_memory_min = "1m";
	public static String jvm_memory_max = "1g";
	public static boolean override_config = false;

	// URLs
	public static String url_asset_download = "http://resources.download.minecraft.net/{microhash}/{hash}";
	public static String url_forge_universal = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/{game-version}-{forge-version}/forge-{game-version}-{forge-version}-universal.jar";
	public static String url_version_manifest = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
	public static String minecraft_repository = "https://libraries.minecraft.net/";
	public static String default_repository = "http://central.maven.org/maven2/";
}
