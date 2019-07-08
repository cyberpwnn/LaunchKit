package org.cyberpwn.launchkit;

import static org.cyberpwn.launchkit.util.L.LOG.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.cyberpwn.launchkit.net.DownloadManager;
import org.cyberpwn.launchkit.util.Artifact;
import org.cyberpwn.launchkit.util.GList;
import org.cyberpwn.launchkit.util.JSONArray;
import org.cyberpwn.launchkit.util.JSONException;
import org.cyberpwn.launchkit.util.JSONObject;
import org.cyberpwn.launchkit.util.OSF;
import org.cyberpwn.launchkit.util.StreamGobbler;
import org.cyberpwn.launchkit.util.VIO;

import com.google.common.collect.Lists;

public class Launcher
{
	private final DownloadManager downloadManager;
	private final File root;
	private final File launcherRoot;
	private final File launcherLibraries;
	private final File launcherMetadata;
	private final File versionManifestFile;
	private final File authFolder;
	private final File forgeUniversal;
	private final File minecraftVersionFile;
	private final File forgeVersionFile;
	private final File minecraftFolder;
	private final File nativesFolder;
	private final File settingsFile;
	private File assetsIndexesFolder;
	private File assetsObjectsFolder;
	private File assetsFolder;
	private JSONObject assetManifest;
	private JSONObject versionManifest;
	private JSONObject minecraftVersion;
	private JSONObject forgeVersion;
	private String assetsIndexId;
	private String launcherStatus;
	private String versionType;
	private String authUsername;
	private String authUserType;
	private String authUUID;
	private String authAccessToken;
	private boolean authenticated;
	private final String platform = OSF.rawOS();

	public Launcher() throws InterruptedException, JSONException, IOException, ClassNotFoundException
	{
		status("Starting");
		authenticated = false;
		root = new File(new File(Environment.local_fs ? "." : System.getProperty("user.home")), Environment.root_folder_name);
		v("Launcher Root Directory: " + root.getAbsolutePath());
		launcherRoot = new File(root, ".launchkit");
		launcherLibraries = new File(launcherRoot, "libraries");
		launcherMetadata = new File(launcherRoot, "metadata");
		nativesFolder = new File(launcherRoot, "natives");
		forgeUniversal = new File(launcherLibraries, "net/minecraftforge/forge/" + Environment.minecraft_version + "/" + Environment.forge_version + "/forge-" + Environment.minecraft_version + "-" + Environment.forge_version + "-universal.jar");
		versionManifestFile = new File(launcherMetadata, "version-manifest.json");
		forgeVersionFile = new File(launcherMetadata, "forge-" + Environment.minecraft_version + "-" + Environment.forge_version + ".json");
		minecraftVersionFile = new File(launcherMetadata, "minecraft-" + Environment.minecraft_version + ".json");
		minecraftFolder = new File(root, ".minecraft");
		assetsFolder = new File(minecraftFolder, "assets");
		assetsObjectsFolder = new File(assetsFolder, "objects");
		assetsIndexesFolder = new File(assetsFolder, "indexes");
		authFolder = new File(launcherRoot, "auth");
		settingsFile = new File(root, "launch-settings.json");
		downloadManager = new DownloadManager();
		setFolderVisibility(launcherRoot, true);
	}

	public Launcher authenticateExternal(String profileName, String profileType, String uuid, String accessToken)
	{
		authenticated = true;
		authUsername = profileName;
		authUserType = profileType;
		authUUID = uuid;
		authAccessToken = accessToken;
		return this;
	}

	public Launcher authenticateWithCredentials(String username, String password) throws ClassNotFoundException, IOException
	{
		ClientAuthentication a = new ClientAuthentication(new File(authFolder, "auth.dx"), username, password);

		switch(a.authenticate())
		{
			case FAILED:
				authenticated = false;
				break;
			case SUCCESS:
				authenticated = true;
				authUsername = a.getProfileName();
				authUserType = a.getProfileType();
				authUUID = a.getUuid();
				authAccessToken = a.getAccessToken();
				break;
			default:
				break;
		}

		return this;
	}

	public Launcher authenticateWithToken() throws ClassNotFoundException, IOException
	{
		ClientAuthentication a = new ClientAuthentication(new File(authFolder, "auth.dx"));

		switch(a.authenticate())
		{
			case FAILED:
				authenticated = false;
				break;
			case SUCCESS:
				authenticated = true;
				authUsername = a.getProfileName();
				authUserType = a.getProfileType();
				authUUID = a.getUuid();
				authAccessToken = a.getAccessToken();
				break;
			default:
				break;
		}

		return this;
	}

	public Launcher launch() throws JSONException, IOException, InterruptedException
	{
		JSONObject o = new JSONObject(VIO.readAll(settingsFile));
		l("Using Java at " + javaw());
		GList<String> parameters = new GList<>();
		String mainClass = (Environment.forge_enabled ? forgeVersion : minecraftVersion).getString("mainClass");
		String t = (Environment.forge_enabled ? forgeVersion : minecraftVersion).getString("minecraftArguments");
		t = filter(t, "auth_player_name", authenticated ? authUsername : "Player" + (int) (Math.random() * 9) + "" + (int) (Math.random() * 9) + "" + (int) (Math.random() * 9));
		t = filter(t, "version_name", Environment.minecraft_version);
		t = filter(t, "game_directory", minecraftFolder.getAbsolutePath());
		t = filter(t, "assets_root", assetsFolder.getAbsolutePath());
		t = filter(t, "assets_index_name", assetsIndexId);
		t = filter(t, "auth_uuid", authenticated ? authUUID : UUID.randomUUID().toString().replaceAll("-", ""));
		t = filter(t, "auth_access_token", authenticated ? authAccessToken : "offline");
		t = filter(t, "user_type", authenticated ? authUserType : "mojang");
		t = filter(t, "version_type", versionType);
		parameters.add(javaw());
		parameters.add("-Xmx" + o.getString("jvm.memory.max"));
		parameters.add("-Xms" + o.getString("jvm.memory.min"));
		parameters.add(o.getString("jvm.opts").trim().split("\\Q \\E"));
		parameters.add("-Djava.library.path=" + nativesFolder.getAbsolutePath());
		parameters.add("-Dorg.lwjgl.librarypath=" + nativesFolder.getAbsolutePath());
		parameters.add("-Dnet.java.games.input.librarypath=" + nativesFolder.getAbsolutePath());
		parameters.add("-Duser.home=" + minecraftFolder.getAbsolutePath());
		parameters.add("-Duser.language=en");
		parameters.add("-Djava.net.preferIPv4Stack=true");
		parameters.add("-Djava.net.useSystemProxies=true");
		parameters.add("-Dlog4j.skipJansi=true");

		if(OSF.getCurrentOS() == OSF.OS.WINDOWS)
		{
			parameters.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
		}

		parameters.add("-cp");

		StringBuilder cpb = new StringBuilder("");

		for(File f : compileClasspath(launcherLibraries))
		{
			cpb.append(OSF.getJavaDelimiter());
			cpb.append(f.getAbsolutePath());
		}

		cpb.deleteCharAt(0);
		parameters.add(cpb.toString());
		parameters.add(mainClass);
		parameters.add(t.trim().split("\\Q \\E"));

		v("======================================================================");

		for(String i : parameters)
		{
			if(i.contains(";"))
			{
				for(String j : i.split(";"))
				{
					v("CLASSPATH: " + j);
				}
			}

			else
			{
				v("ARGS: " + i);
			}
		}

		v("======================================================================");
		ProcessBuilder pb = new ProcessBuilder(parameters);
		pb.directory(minecraftFolder);
		Process p = pb.start();
		new StreamGobbler(p.getInputStream(), "Client|OUT");
		new StreamGobbler(p.getErrorStream(), "Client|ERR");
		l("Client Process exited with code " + p.waitFor());
		return this;
	}

	private String filter(String template, String parameter, String value)
	{
		return template.replace("${" + parameter + "}", value);
	}

	public Launcher validate() throws InterruptedException, ZipException, IOException
	{
		status("Validating Launch");
		validateVersionManifest();
		validateForgeUniversal();
		swapQueues();
		findMinecraftVersion();
		extractForgeManifest();
		swapQueues();
		validateAssets();
		validateMinecraft();
		validateMinecraftLibraries();
		validateForgeLibraries();
		validateMinecraftConfiguration();
		validateSettings();
		swapQueues();
		validateNatives();
		status("Ready");
		return this;
	}

	private String javaw()
	{
		return System.getProperty("java.home") + File.separator + "bin" + File.separator + "javaw.exe";
	}

	private void validateSettings() throws IOException
	{
		status("Validating Settings");
		JSONObject defaultConfig = new JSONObject();
		defaultConfig.put("jvm.memory.max", Environment.jvm_memory_max);
		defaultConfig.put("jvm.memory.min", Environment.jvm_memory_min);
		defaultConfig.put("jvm.opts", Environment.jvm_opts);

		JSONObject currentConfig = new JSONObject();

		if(settingsFile.exists())
		{
			try
			{
				currentConfig = new JSONObject(VIO.readAll(settingsFile));
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		boolean modified = false;

		for(String i : defaultConfig.keySet())
		{
			if(!currentConfig.has(i))
			{
				v("Adding Default Config Option " + i + " -> " + defaultConfig.get(i).toString());
				currentConfig.put(i, defaultConfig.get(i));
				modified = true;
			}
		}

		if(modified)
		{
			VIO.writeAll(settingsFile, currentConfig.toString(4));
		}
	}

	private void validateNatives()
	{
		nativesFolder.mkdirs();
		status("Validating Natives");
		JSONArray libraries = minecraftVersion.getJSONArray("libraries");
		validateLibraries(libraries);

		for(int i = 0; i < libraries.length(); i++)
		{
			JSONObject library = libraries.getJSONObject(i);
			if(library.has("downloads"))
			{
				JSONObject downloads = library.getJSONObject("downloads");

				if(downloads.has("classifiers"))
				{
					JSONObject classifiers = downloads.getJSONObject("classifiers");

					if(classifiers.has("natives-" + platform))
					{
						JSONObject nativeClassifier = classifiers.getJSONObject("natives-" + platform);
						File nativesArchive = new File(launcherLibraries, nativeClassifier.getString("path"));

						try
						{
							extractNatives(nativesArchive, nativesFolder);
						}

						catch(IOException e)
						{
							e.printStackTrace();
							f("Failed to extract native " + nativesArchive.getPath());
						}
					}
				}
			}
		}
	}

	private void validateMinecraftConfiguration()
	{
		JSONObject logFile = minecraftVersion.getJSONObject("logging").getJSONObject("client").getJSONObject("file");
		File logging = new File(assetsFolder, "log_configs/" + logFile.getString("id"));

		if(!logging.exists())
		{
			downloadManager.download(logFile.getString("url"), logging, logFile.getInt("size"));
		}
	}

	private void validateMinecraftLibraries()
	{
		status("Validating Minecraft Libraries");
		JSONArray libraries = minecraftVersion.getJSONArray("libraries");
		validateLibraries(libraries);
	}

	private void validateForgeLibraries()
	{
		if(Environment.forge_enabled)
		{
			status("Validating Forge Libraries");
			JSONArray libraries = forgeVersion.getJSONArray("libraries");
			validateLibrariesWithoutHelp(libraries);
		}
	}

	private void validateLibrariesWithoutHelp(JSONArray libraries)
	{
		for(int i = 0; i < libraries.length(); i++)
		{
			JSONObject library = libraries.getJSONObject(i);

			if(library.has("name") && library.has("clientreq") && library.getBoolean("clientreq"))
			{
				Artifact artifact = new Artifact(library.getString("name"), library.has("url") ? library.getString("url") : Environment.minecraft_repository);
				File file = artifact.getPath(launcherLibraries);

				if(!file.exists())
				{
					downloadManager.download(filter(artifact).getFormalUrl(), file);
				}
			}
		}
	}

	private Artifact filter(Artifact a)
	{
		if(a.getGroupId().equals("com.typesafe") && a.getArtifactId().equals("config"))
		{
			a.setRepo(Environment.default_repository);
		}

		if(a.getGroupId().equals("com.typesafe.akka") && a.getArtifactId().equals("akka-actor_2.11"))
		{
			a.setRepo(Environment.default_repository);
		}

		return a;
	}

	private void validateLibraries(JSONArray libraries)
	{
		for(int i = 0; i < libraries.length(); i++)
		{
			JSONObject library = libraries.getJSONObject(i);
			if(library.has("downloads"))
			{
				JSONObject downloads = library.getJSONObject("downloads");

				if(downloads.has("artifact"))
				{
					JSONObject artifact = downloads.getJSONObject("artifact");

					if(artifact != null)
					{
						File artifactFile = new File(launcherLibraries, artifact.getString("path"));

						if(!artifactFile.exists())
						{
							downloadManager.download(artifact.getString("url"), artifactFile, artifact.getInt("size"));
						}
					}
				}

				if(downloads.has("classifiers"))
				{
					JSONObject classifiers = downloads.getJSONObject("classifiers");

					if(classifiers.has("natives-" + platform))
					{
						JSONObject nativeClassifier = classifiers.getJSONObject("natives-" + platform);
						File nativesArchive = new File(launcherLibraries, nativeClassifier.getString("path"));

						if(!nativesArchive.exists())
						{
							downloadManager.download(nativeClassifier.getString("url"), nativesArchive, nativeClassifier.getInt("size"));
						}
					}
				}
			}
		}
	}

	private void validateMinecraft() throws IOException
	{
		status("Validating Minecraft");
		File client = new File(minecraftFolder, "versions/" + Environment.minecraft_version + "/" + Environment.minecraft_version + ".jar");
		File clientMeta = new File(minecraftFolder, "versions/" + Environment.minecraft_version + "/" + Environment.minecraft_version + ".json");

		if(!client.exists())
		{
			downloadManager.download(minecraftVersion.getJSONObject("downloads").getJSONObject("client").getString("url"), client, minecraftVersion.getJSONObject("downloads").getJSONObject("client").getInt("size"));
		}

		if(!clientMeta.exists())
		{
			clientMeta.getParentFile().mkdirs();
			Files.copy(versionManifestFile.toPath(), clientMeta.toPath());
		}
	}

	private void validateAssets() throws JSONException, IOException, InterruptedException
	{
		status("Validating Assets");
		minecraftVersion = new JSONObject(VIO.readAll(minecraftVersionFile));
		JSONObject assetIndex = minecraftVersion.getJSONObject("assetIndex");
		assetsIndexId = assetIndex.getString("id");
		File index = new File(assetsIndexesFolder, assetsIndexId + ".json");

		if(!index.exists())
		{
			downloadManager.download(assetIndex.getString("url"), index);
			swapQueues();
		}

		assetManifest = new JSONObject(VIO.readAll(index));
		JSONObject objects = assetManifest.getJSONObject("objects");
		Iterator<String> it = objects.keys();

		while(it.hasNext())
		{
			String key = it.next();
			JSONObject object = objects.getJSONObject(key);
			String hash = object.getString("hash");
			String microhash = hash.substring(0, 2);
			File objectFile = new File(assetsObjectsFolder, microhash + "/" + hash);

			if(objectFile.exists())
			{
				continue;
			}

			downloadManager.download(Environment.url_asset_download.replaceAll("\\Q{microhash}\\E", microhash).replaceAll("\\Q{hash}\\E", hash), objectFile, object.getInt("size"));
		}
	}

	private void swapQueues() throws InterruptedException
	{
		status("Downloading");
		downloadManager.waitForAll();
	}

	private void findMinecraftVersion() throws JSONException, IOException
	{
		versionManifest = new JSONObject(VIO.readAll(versionManifestFile));
		JSONArray versions = versionManifest.getJSONArray("versions");

		for(int i = 0; i < versions.length(); i++)
		{
			JSONObject version = versions.getJSONObject(i);

			if(version.getString("id").equals(Environment.minecraft_version))
			{
				if(!version.getString("type").equals("release"))
				{
					w("The version " + Environment.minecraft_version + " is considered a " + version.getString("type") + ". The game may not be stable!");
				}

				versionType = version.getString("type");
				status("Validating Minecraft Meta");
				downloadManager.download(version.getString("url"), minecraftVersionFile);
				break;
			}
		}
	}

	private void extractForgeManifest() throws ZipException, IOException
	{
		if(Environment.forge_enabled)
		{
			status("Validating Forge Meta");

			if(!forgeVersionFile.exists())
			{
				GList<String> e = VIO.listEntries(forgeUniversal);

				if(e.contains("version.json"))
				{
					FileOutputStream fos = new FileOutputStream(forgeVersionFile);
					VIO.readEntry(forgeUniversal, "version.json", (s) ->
					{
						try
						{
							VIO.fullTransfer(s, fos, 16819);
							v("Extracted Forge Version to " + forgeVersionFile.getPath());
						}

						catch(IOException e1)
						{
							e1.printStackTrace();
						}
					});
					fos.close();
				}

				else
				{
					f("Cannot find forge version meta in universal jar!");
				}
			}

			forgeVersion = new JSONObject(VIO.readAll(forgeVersionFile));
		}
	}

	private void validateForgeUniversal()
	{
		if(forgeUniversal.exists())
		{
			return;
		}

		if(Environment.forge_enabled)
		{
			status("Validating Forge");
			downloadManager.download(Environment.url_forge_universal.replaceAll("\\Q{game-version}\\E", Environment.minecraft_version).replaceAll("\\Q{forge-version}\\E", Environment.forge_version), forgeUniversal);
		}
	}

	private void validateVersionManifest()
	{
		if(versionManifestFile.exists())
		{
			return;
		}

		downloadManager.download(Environment.url_version_manifest, versionManifestFile);
	}

	private void status(String s)
	{
		launcherStatus = s;
		l(s);
	}

	public String getStatus()
	{
		return launcherStatus;
	}

	private void setFolderVisibility(File f, boolean hidden)
	{
		try
		{
			f.mkdirs();
			Files.setAttribute(f.toPath(), "dos:hidden", !hidden);
		}

		catch(IOException e)
		{
			w("Failed to set visibility:" + hidden + " on file " + f.getPath());
		}
	}

	private List<File> compileClasspath(File libs)
	{
		List<File> classpath = Lists.newArrayList();

		for(File i : getFiles(libs))
		{
			classpath.add(new File(i.getPath()));
		}

		classpath.add(new File(minecraftFolder, "versions/" + Environment.minecraft_version + "/" + Environment.minecraft_version + ".jar"));

		return classpath;
	}

	private static List<File> getFiles(File folder)
	{
		List<File> files = new ArrayList<File>();

		for(File i : folder.listFiles())
		{
			if(i.isFile())
			{
				if(i.getName().equals("guava-15.0.jar"))
				{
					// TODO: continue;
				}

				files.add(i);
			}

			else
			{
				files.addAll(getFiles(i));
			}
		}

		return files;
	}

	private static void extractNatives(File zip, File lfolder) throws IOException
	{
		final ZipFile file = new ZipFile(zip);

		try
		{
			final Enumeration<? extends ZipEntry> entries = file.entries();

			while(entries.hasMoreElements())
			{
				final ZipEntry entry = entries.nextElement();

				if(entry.getName().contains("META") || entry.getName().contains("MANIFEST"))
				{
					continue;
				}

				writeStream(entry.getName(), file.getInputStream(entry), lfolder, zip);
			}
		}

		finally
		{
			file.close();
		}
	}

	private static int writeStream(String n, final InputStream is, File lfolder, File zip) throws IOException
	{
		File f = new File(lfolder, n);

		if(f.exists())
		{
			return 0;
		}

		lfolder.mkdirs();
		FileOutputStream fos = new FileOutputStream(f);
		final byte[] buf = new byte[8192];
		int read = 0;
		int cntRead;

		while((cntRead = is.read(buf, 0, buf.length)) >= 0)
		{
			read += cntRead;
			fos.write(buf, 0, cntRead);
		}

		v("Extracted " + n + " from " + zip.getName());

		fos.close();

		return read;
	}
}
