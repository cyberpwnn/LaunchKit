package org.cyberpwn.launchkit.util;

import org.cyberpwn.launchkit.Environment;
import org.cyberpwn.launchkit.pack.Pack;
import org.cyberpwn.launchkit.pack.PackInstall;
import org.cyberpwn.launchkit.pack.PackProfile;
import org.cyberpwn.launchkit.pack.PackTweak;

public class DemoGen
{
	public static Pack generateExampleVanilla188()
	{
		Pack pack = new Pack();
		pack.getGame().setForgeVersion("no");
		pack.getGame().setMinecraftVersion("1.8.8");
		pack.getIdentity().setName("Vanilla 1.8.8");
		pack.getIdentity().setDescription("Pack Description");
		pack.getIdentity().setVersion("1.2");

		PackInstall pUnity = new PackInstall();
		pUnity.setLocation("resourcepacks");
		pUnity.setDownload("https://www.curseforge.com/minecraft/texture-packs/unity/download/2576530/file");
		pUnity.setType("zip");
		pUnity.setHint("resourcepack");

		pack.getInstall().add(pUnity);

		PackProfile defaultProfile = new PackProfile("default");
		defaultProfile.getLaunchArgs().add("-Xms1m");
		defaultProfile.getLaunchArgs().add("-Xmx850m");
		pack.getProfiles().add(defaultProfile);

		return pack;
	}

	public static Pack generateExampleVanilla1122()
	{
		Pack pack = new Pack();
		pack.getGame().setForgeVersion("no");
		pack.getGame().setMinecraftVersion("1.12.2");
		pack.getIdentity().setName("Vanilla 1.12.2");
		pack.getIdentity().setDescription("Pack Description");
		pack.getIdentity().setVersion("2.33");

		PackInstall pUnity = new PackInstall();
		pUnity.setLocation("resourcepacks");
		pUnity.setDownload("https://www.curseforge.com/minecraft/texture-packs/unity/download/2576530/file");
		pUnity.setType("zip");
		pUnity.setHint("resourcepack");

		pack.getInstall().add(pUnity);

		PackProfile defaultProfile = new PackProfile("default");
		defaultProfile.getLaunchArgs().add("-Xms1m");
		defaultProfile.getLaunchArgs().add("-Xmx850m");
		pack.getProfiles().add(defaultProfile);

		return pack;
	}

	public static Pack generateExampleForge1122()
	{
		Pack pack = new Pack();
		pack.getGame().setForgeVersion(Environment.forge_version);
		pack.getGame().setMinecraftVersion(Environment.minecraft_version);
		pack.getIdentity().setName("Forge 1.12.2");
		pack.getIdentity().setDescription("Pack Description");
		pack.getIdentity().setVersion("1.125");

		PackInstall pOptifine = new PackInstall();
		pOptifine.setLocation("mods");
		pOptifine.setDownload("https://optifine.net/adloadx?f=OptiFine_1.12.2_HD_U_E3.jar");
		pOptifine.setHint("optifine");
		pOptifine.setType("jar");

		PackInstall pDysurr = new PackInstall();
		pDysurr.setLocation("mods");
		pDysurr.setDownload("https://www.curseforge.com/minecraft/mc-mods/dynamic-surroundings/download");
		pDysurr.setType("jar");
		pDysurr.setActivation("ultra");

		PackInstall pUnity = new PackInstall();
		pUnity.setLocation("resourcepacks");
		pUnity.setDownload("https://www.curseforge.com/minecraft/texture-packs/unity/download");
		pUnity.setType("zip");
		pUnity.setHint("resourcepack");

		PackTweak t = new PackTweak();
		t.setFile("config/splash.properties");
		t.setFind("showMemory=true");
		t.setReplace("showMemory=false");
		t.setActivation("default");

		pack.getTweaks().add(t);

		pack.getInstall().add(pOptifine);
		pack.getInstall().add(pDysurr);
		pack.getInstall().add(pUnity);

		PackProfile defaultProfile = new PackProfile("default");
		defaultProfile.getLaunchArgs().add("-Xms1m");
		defaultProfile.getLaunchArgs().add("-Xmx1g");

		PackProfile ultra = new PackProfile("ultra");
		ultra.getLaunchArgs().add("-Xms1m");
		ultra.getLaunchArgs().add("-Xmx2g");
		ultra.getActivation().add("total_system_memory >= 8192");
		ultra.getActivation().add("free_system_memory >= 4096");
		ultra.getActivation().add("used_system_memory <= 4096");
		ultra.getActivation().add("cpu_threads >= 4");
		ultra.getActivation().add("free_space > 2000");

		pack.getProfiles().add(defaultProfile);
		pack.getProfiles().add(ultra);

		return pack;
	}
}
