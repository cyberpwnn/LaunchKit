package org.cyberpwn.launchkit;

import static org.cyberpwn.launchkit.util.L.LOG.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.net.ssl.HttpsURLConnection;

import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.collections.KMap;
import ninja.bytecode.shuriken.collections.KSet;
import ninja.bytecode.shuriken.config.Props;
import ninja.bytecode.shuriken.execution.J;
import ninja.bytecode.shuriken.io.IO;
import org.apache.commons.io.FileUtils;
import org.cyberpwn.launchkit.net.DownloadManager;
import org.cyberpwn.launchkit.pack.Pack;
import org.cyberpwn.launchkit.pack.PackInstall;
import org.cyberpwn.launchkit.pack.PackProfile;
import org.cyberpwn.launchkit.pack.PackTweak;
import org.cyberpwn.launchkit.util.Artifact;
import org.cyberpwn.launchkit.util.ChronoLatch;
import org.cyberpwn.launchkit.util.Comp;
import org.cyberpwn.launchkit.util.DemoGen;
import org.cyberpwn.launchkit.util.JSONArray;
import org.cyberpwn.launchkit.util.JSONException;
import org.cyberpwn.launchkit.util.JSONObject;
import org.cyberpwn.launchkit.util.L;
import org.cyberpwn.launchkit.util.M;
import org.cyberpwn.launchkit.util.OSF;
import org.cyberpwn.launchkit.util.Platform;
import org.cyberpwn.launchkit.util.StreamGobbler;
import org.cyberpwn.launchkit.util.UniversalParser;
import org.zeroturnaround.zip.ZipUtil;

import com.google.common.collect.Lists;


public class Launcher
{
	private final DownloadManager downloadManager;
	private final Commander commander;
	private final KSet<String> extracted = new KSet<>();
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
	private final File packFile;
	private final File packFileEffective;
	private final File downloadCache;
	private final File packFileNew;
	private final String platform = OSF.rawOS();
	private final ProgressEmitter emitter;
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
	private PackProfile profile;
	private boolean authenticated;
	private boolean validated;
	private Process activeProcess;
	private boolean downloading;
	private boolean validating;
	private ChronoLatch stateLatch;
	private KList<Consumer<Double>> progressListemers;
	private Pack pack;
	
	public Launcher() throws InterruptedException, JSONException, IOException, ClassNotFoundException
	{
		validating = false;
		progressListemers = new KList<>();
		commander = new Commander();
		status("Starting");
		stateLatch = new ChronoLatch(1000);
		authenticated = false;
		root = new File(new File(Environment.local_fs ? "." : System.getProperty("user.home")), Environment.root_folder_name);
		v("Launcher Root Directory: " + root.getAbsolutePath());
		launcherRoot = new File(root, ".launchkit");
		launcherLibraries = new File(launcherRoot, "libraries");
		launcherMetadata = new File(launcherRoot, "metadata");
		nativesFolder = new File(launcherRoot, "natives");
		packFile = new File(launcherMetadata, "pack-meta.json");
		packFileNew = new File(launcherMetadata, "pack-live.json");
		packFileEffective = new File(launcherMetadata, "pack-effective.json");
		forgeUniversal = new File(launcherLibraries, "net/minecraftforge/forge/" + Environment.minecraft_version + "/" + Environment.forge_version + "/forge-" + Environment.minecraft_version + "-" + Environment.forge_version + "-universal.jar");
		versionManifestFile = new File(launcherMetadata, "version-manifest.json");
		forgeVersionFile = new File(launcherMetadata, "forge-" + Environment.minecraft_version + "-" + Environment.forge_version + ".json");
		minecraftVersionFile = new File(launcherMetadata, "minecraft-meta.json");
		minecraftFolder = new File(root, ".minecraft");
		assetsFolder = new File(minecraftFolder, "assets");
		downloadCache = new File(launcherRoot, "cache");
		assetsObjectsFolder = new File(assetsFolder, "objects");
		assetsIndexesFolder = new File(assetsFolder, "indexes");
		authFolder = new File(launcherRoot, "auth");
		downloadManager = new DownloadManager(downloadCache);
		setFolderVisibility(launcherRoot, true);
		validated = false;
		emitter = new ProgressEmitter(this, 100);
		emitter.start();
		downloading = false;
		init();
		status("ready");
		L.LOG.l("Ready. Use /help for help.");
		L.LOG.l("-------------------------------------------");
		autoStart();
	}

	private void autoStart()
	{
		if(Environment.auto_start)
		{
			try
			{
				launch();
			}
			catch(JSONException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException | IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void init()
	{
		if(Environment.generate_example_json)
		{
			try
			{
				writeExample(DemoGen.generateExampleForge1122());
				writeExample(DemoGen.generateExampleVanilla1122());
				writeExample(DemoGen.generateExampleVanilla188());
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	private void writeExample(Pack pack) throws Throwable
	{
		L.LOG.l("Generating Example: " + pack.getIdentity().getName());
		File folder = new File(root, "example-packs");
		folder.mkdirs();
		IO.writeAll(new File(folder, pack.getIdentity().getName() + ".json"), UniversalParser.toJSON(pack).toString(4));
	}

	public void emitProgress()
	{
		if(downloading)
		{
			if(stateLatch.flip())
			{
				status(launcherStatus);
			}

			commander.sendMessage("progress=" + M.clip(0, 1D, Math.abs(((double) ((int) (downloadManager.getProgress().getProgress() * 100D))) / 10000D)));
		}
	}

	public Launcher authenticateExternal(String profileName, String profileType, String uuid, String accessToken)
	{
		authenticated = true;
		authUsername = profileName;
		authUserType = profileType;
		authUUID = uuid;
		authAccessToken = accessToken;
		L.LOG.l("Authentication from external sources cannot be checked. They will be passed to the client only.");
		return this;
	}

	public Launcher authenticateWithCredentials(String username, String password) throws ClassNotFoundException, IOException
	{
		ClientAuthentication a = new ClientAuthentication(new File(authFolder, "auth.dx"), username, password);

		switch(a.authenticate())
		{
			case FAILED:
				authenticated = false;
				L.LOG.w("Authentication FAILED");
				break;
			case SUCCESS:
				authenticated = true;
				authUsername = a.getProfileName();
				authUserType = a.getProfileType();
				authUUID = a.getUuid();
				authAccessToken = a.getAccessToken();
				L.LOG.l("Authentication SUCCESS! You can now use /mc auth next time instead of passing credentials again.");
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
				L.LOG.w("Authentication FAILED");
				break;
			case SUCCESS:
				authenticated = true;
				authUsername = a.getProfileName();
				authUserType = a.getProfileType();
				authUUID = a.getUuid();
				authAccessToken = a.getAccessToken();
				L.LOG.l("Authentication SUCCESS!");
				break;
			default:
				break;
		}

		return this;
	}

	public boolean isRunning()
	{
		if(activeProcess != null)
		{
			if(activeProcess.isAlive())
			{
				return true;
			}
		}

		return false;
	}

	public Launcher killGame()
	{
		if(activeProcess != null)
		{
			if(activeProcess.isAlive())
			{
				activeProcess.destroyForcibly();
			}
		}

		return this;
	}

	public Launcher launch() throws JSONException, IOException, InterruptedException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		if(validating)
		{
			w("Not Launching! Still Validating!");
			return this;
		}

		if(isRunning())
		{
			w("Minecraft is already running. Please kill it first or use a seperate minecraft instance.");
			status("error-already-running");
			return this;
		}

		validate();
		commander.sendMessage("progress=-1");

		if(!authenticated)
		{
			w("Game is not authenticated.");
			w("Attempting to authenticate with token");
			w("If there is no access token, or authentication fails, Minecraft will be launched in offline mode.");

			try
			{
				authenticateWithToken();
			}

			catch(ClassNotFoundException e)
			{

			}
		}

		l("Using Java at " + javaw());
		KList<String> parameters = new KList<>();
		JSONObject meta = Environment.forge_enabled ? forgeVersion : minecraftVersion;
		String mainClass = meta.getString("mainClass");
		String t = meta.getString("minecraftArguments");
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

		if(profile != null && !profile.getLaunchArgs().isEmpty())
		{
			v("Using Profile varargs");
			parameters.addAll(profile.getLaunchArgs());
		}

		else
		{
			w("Profile " + profile.getName() + " does not contain launch args. Using config/environment args.");
			parameters.add("-Xmx" + Environment.jvm_memory_max == null ? "1g" : Environment.jvm_memory_max);
			parameters.add("-Xms" + Environment.jvm_memory_min == null ? "1m" : Environment.jvm_memory_min);
			parameters.add(Environment.jvm_opts.trim().split("\\Q \\E"));
		}

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

		if(Environment.debug_launchparams)
		{
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
		}

		ProcessBuilder pb = new ProcessBuilder(parameters);
		pb.directory(minecraftFolder);
		activeProcess = pb.start();
		commander.sendMessage("progress=1");
		new StreamGobbler(activeProcess.getInputStream(), "Client|OUT");
		new StreamGobbler(activeProcess.getErrorStream(), "Client|ERR");
		commander.sendMessage("running");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					int code = activeProcess.waitFor();
					l("Client Process exited with code " + code);
					commander.sendMessage("progress=1");
					commander.sendMessage("stopped");

					if(code != 0)
					{
						commander.sendMessage("crashed");
					}

					activeProcess = null;
				}

				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
		return this;
	}

	private String filter(String template, String parameter, String value)
	{
		return template.replace("${" + parameter + "}", value);
	}

	public Launcher validate() throws InterruptedException, ZipException, IOException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, JSONException
	{
		if(validating)
		{
			w("Validation is already in progress!");
			status("error-already-validating");
			return this;
		}

		validating = true;
		downloading = true;

		for(int i = 0; i < 3; i++)
		{
			try
			{
				status("Validating Launch");
				J.attemptAndThrow(this::validatePackMeta);
				J.attemptAndThrow(this::swapQueues);
				J.attemptAndThrow(this::validatePack);
				J.attemptAndThrow(this::validateVersionManifest);
				J.attemptAndThrow(this::validateForgeUniversal);
				J.attemptAndThrow(this::swapQueues);
				J.attemptAndThrow(this::findMinecraftVersion);
				J.attemptAndThrow(this::extractForgeManifest);
				J.attemptAndThrow(this::swapQueues);
				J.attemptAndThrow(this::validateAssets);
				J.attemptAndThrow(this::validateMinecraft);
				J.attemptAndThrow(this::validateMinecraftLibraries);
				J.attemptAndThrow(this::validateForgeLibraries);
				J.attemptAndThrow(this::validateMinecraftConfiguration);
				J.attemptAndThrow(this::swapQueues);
				J.attemptAndThrow(this::validateNatives);
				J.attemptAndThrow(this::validateCleaning);
				J.attemptAndThrow(this::swapQueues);
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		commander.sendMessage("progress=1");
		status("Ready");
		commander.sendMessage("validated");
		validated = true;
		downloading = false;
		validating = false;


		v("Applying Pack Tweaks");
		for(PackTweak i : pack.getTweaks())
		{
			tweak(pack, i);
		}

		return this;
	}

	public void invalidate()
	{
		IO.delete(launcherRoot);
		IO.delete(minecraftFolder);
	}

	private void validateCleaning()
	{
		if(Environment.clean_logs)
		{
			IO.delete(new File(minecraftFolder, "crafttweaker.log"));
			IO.delete(new File(minecraftFolder, "crash-reports"));
		}
	}

	private void validatePack() throws IOException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, JSONException
	{
		boolean update = false;
		boolean revertOld = false;
		Pack newPack = null;

		if(!packFile.exists())
		{
			if(packFileNew.exists())
			{
				update = true;
				IO.copyFile(packFileNew, packFile);
			}
		}

		if(!packFile.exists())
		{
			f("No Pack File!");
			return;
		}

		File o = new File(root, "override-pack.json");
		if(o.exists())
		{
			IO.copyFile(o, packFileNew);
		}

		JSONObject jold = null;
		JSONObject jnew = null;
		jold = new JSONObject(IO.readAll(packFile));
		jnew = new JSONObject(IO.readAll(packFileNew));
		newPack = UniversalParser.fromJSON(jnew, Pack.class);
		IO.writeAll(packFileEffective, UniversalParser.toJSON(newPack).toString(4));
		v("Pack: " + newPack.getIdentity().getName() + " version " + newPack.getIdentity().getVersion());
		if(newPack.getGame().getForgeVersion().equals("no"))
		{
			v("Pack is not using Forge. Disabling");
			Environment.forge_enabled = false;
			Environment.forge_version = "no";
		}

		else
		{
			v("Pack is using Forge. Activating");
			Environment.forge_enabled = true;
			Environment.forge_version = newPack.getGame().getForgeVersion();
		}

		Environment.minecraft_version = newPack.getGame().getMinecraftVersion();
		File f = new File(launcherMetadata, "profile.info");
		String oldProfile = "unidentified";

		if(f.exists())
		{
			oldProfile = IO.readAll(f).trim();
		}

		computeProfile(newPack);
		String newProfile = profile.getName().trim();

		if(!oldProfile.equals(newProfile))
		{
			update = true;
			revertOld = true;
		}

		try
		{
			Pack oldPack = UniversalParser.fromJSON(jold, Pack.class);

			if(!newPack.getIdentity().getVersion().equals(oldPack.getIdentity().getVersion()))
			{
				w("Pack Upgrade, Clearing Download Caches");
				IO.delete(downloadCache);
				downloadCache.mkdirs();
			}

			if(!(newPack.getGame().getForgeVersion() + newPack.getGame().getMinecraftVersion()).equals(oldPack.getGame().getForgeVersion() + oldPack.getGame().getMinecraftVersion()))
			{
				w("Pack changed game versioning. Deleting Libraries");
				IO.delete(launcherLibraries);
			}

			if(!jold.toString().equals(jnew.toString()))
			{
				l("Pack Update Detected!");
				update = true;
				revertOld = true;
			}
		}

		catch(Throwable e)
		{

		}

		if(update)
		{
			if(revertOld)
			{
				revertForUpdate();
			}

			l("Validating Pack Install");
			validatePackInstall(newPack);
		}

		try
		{
			IO.copyFile(packFileNew, packFile);
		}

		catch(Throwable e)
		{

		}
		
		this.pack = newPack;
	}

	private boolean isUpToDate(String data)
	{
		String h = data + pack.getIdentity().getVersion();
		String r = Props.of("lk").get("version-" + IO.hash(data));
		r = r == null ? "" : r;
		return r.equals(IO.hash(h));
	}

	private void setUpToDate(String data)
	{
		String h = data + pack.getIdentity().getVersion();
		Props.of("lk").put("version-" + IO.hash(data), IO.hash(h));
	}

	private void unpackCached(File zip, File dest)
	{
		if(!isUpToDate(zip.getAbsolutePath()))
		{
			setUpToDate(zip.getAbsolutePath());
			String k = zip.getAbsolutePath() + "-" + dest.getAbsolutePath();
			if(!extracted.contains(k))
			{
				ZipUtil.unpack(zip, dest);
				extracted.add(k);
			}
		}
	}

	String getVersion()
	{
		return getPack().getIdentity().getVersion();
	}

	private void validatePackInstall(Pack newPack)
	{
		KList<String> resourcepacks = new KList<>();
		for(PackInstall i : newPack.getInstall())
		{
			try
			{
				if(i.shouldActivate(profile.getName()))
				{
					v("Install " + i.getDownload() + " into " + i.getLocation());
					String name = i.getName().trim().isEmpty() ? UUID.randomUUID().toString().split("\\Q-\\E")[0] : i.getName();
					String fullname = i.getType().trim().isEmpty() ? name : name + "." + i.getType();
					File bsa = i.getLocation().equals("/") ? minecraftFolder : new File(minecraftFolder, i.getLocation());
					File f = new File(bsa, fullname);
					bsa.mkdirs();
					String u = i.getDownload();

					if(i.getHint().contains("resourcepack"))
					{
						v(fullname + " will be configured as a resourcepack in options.txt");
						resourcepacks.add(f.getName());
					}

					if(u.startsWith("https://www.curseforge.com/minecraft/") || u.startsWith("https://curseforge.com/minecraft/"))
					{
						if(!u.endsWith("/download"))
						{
							if(u.endsWith("/files"))
							{
								u = u.replaceFirst("\\Q/files\\E", "/download");
								v("Tweaked Curse Downooad URL to: " + u);
							}

							else if(!u.endsWith("/"))
							{
								u = u + "/download";
								v("Tweaked Curse Downooad URL to: " + u);
							}
						}

						try
						{
							URL url = new URL(i.getDownload());
							HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
							con.setRequestMethod("GET");
							con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
							con.setRequestProperty("Host", "www.curseforge.com:443");
							con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
							con.setRequestProperty("x-ua-compatible", "IE=edge,chrome=1");
							con.setRequestProperty("strict-transport-security", "max-age=15768000");
							con.setRequestProperty("status", "200");
							con.setRequestProperty("server", "cloudflare");
							con.setRequestProperty("content-type", "text/html; charset=utf-8");
							con.setRequestProperty("content-encoding", "gzip");
							con.setRequestProperty("cf-ray", "523f680c2a75be69-RIC");
							con.setRequestProperty("cf-cache-status", "DYNAMIC");
							con.setRequestProperty("cache-control", "private");
							con.setRequestProperty("cache-control", "");
							con.setRequestProperty("Upgrade-Insecure-Requests", "1");
							con.setRequestProperty("Sec-Fetch-User", "?1");
							con.setRequestProperty("Sec-Fetch-Site", "none");
							con.setRequestProperty("Sec-Fetch-Mode", "navigate");

							InputStream in = con.getInputStream();
							String html = IO.readAll(in);
							in.close();

							for(String line : html.split("\\Q\n\\E"))
							{
								if(line.trim().startsWith("<a href=\"/minecraft/"))
								{
									u = "https://www.curseforge.com" + line.trim().replaceAll("\\Q<a href=\"\\E", "").replaceAll("\\Q\">here</a>\\E", "").trim();
									w("Curse URL Updated from " + i.getDownload() + " to " + u);
									break;
								}
							}
						}

						catch(Throwable e)
						{
							e.printStackTrace();
						}
					}

					if(i.getHint().contains("optifine"))
					{
						try
						{
							v("Applying special tweak for optifine download (wink)");
							URL url = new URL(i.getDownload());
							HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
							con.setRequestMethod("GET");
							con.setRequestProperty("Accept-Language", "en-gb,en;q=0.5");
							con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
							con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");

							v("Generated Optifine Download Token...");
							InputStream in = con.getInputStream();
							String html = IO.readAll(in);
							in.close();

							for(String line : html.split("\\Q\n\\E"))
							{
								if(line.trim().startsWith("<a href='downloadx?f=OptiFine_"))
								{
									u = "https://optifine.net/" + line.trim().replaceAll("\\Q<a href='\\E", "").split("\\Q'\\E")[0].trim();

									v("URL Updated to " + u);
									break;
								}
							}
						}

						catch(Throwable e)
						{
							e.printStackTrace();
						}
					}

					L.LOG.v("Downloading " + i.getDownload());

					if(i.getHint().contains("extract"))
					{
						downloadManager.downloadCached(u, f, -1, new Runnable()
						{
							@Override
							public void run()
							{
								L.LOG.v("Downloaded " + i.getDownload() + " to " + f.getPath());
								v("Extracting " + f.getPath() + "'s contents into " + bsa.getPath());
								ZipUtil.unpack(f, bsa);
								IO.delete(f);
							}
						});
					}

					if(i.getHint().contains("repository"))
					{
						File rfile = new File(downloadCache, "repos/" +IO.hash(u+getVersion()) + ".zip");
						File rfold = new File(downloadCache, "repos/" +IO.hash(u+getVersion()));
						rfile.getParentFile().mkdirs();
						rfold.mkdirs();
						downloadManager.downloadCached(u, rfile, -1, new Runnable()
						{
							@Override
							public void run()
							{
								L.LOG.v("Downloaded " + i.getDownload() + " to " + rfile.getPath());

								String into = i.getInto();
								String sub = i.getSub();
								File temp = new File(downloadCache, "version-lock-" + versionType + "/" + IO.hash(rfile.getAbsolutePath()));
								temp.mkdirs();
								File target = new File(temp, sub);
								v("Extracting " + rfile.getPath() + (" (/" + sub + ") ") + " Repo contents into " + target.getPath());
								unpackCached(rfile, temp);

								File dir = null;

								for(File i : temp.listFiles())
								{
									if(i.isDirectory())
									{
										temp = i;
										break;
									}
								}

								File ff = new File(bsa, into);

								ff.mkdirs();
								try {
									FileUtils.copyDirectory(new File(temp, sub), ff);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		if(!resourcepacks.isEmpty())
		{
			String p = "resourcePacks:" + resourcepacks.toJSONStringArray().toString(0);
			File f = new File(minecraftFolder, "options.txt");

			if(!f.exists())
			{
				try
				{
					IO.writeAll(f, p + "\n");
				}

				catch(IOException e)
				{
					e.printStackTrace();
				}
			}

			else
			{
				try
				{
					KList<String> m = new KList<String>(IO.readAll(f).split("\\Q\n\\E"));

					for(String i : m.copy())
					{
						if(i.startsWith("resourcePacks:"))
						{
							m.remove(i);
						}
					}

					m.add(p);
					m.reverse();
					IO.writeAll(f, m.toString("\n"));
				}

				catch(IOException e)
				{
					e.printStackTrace();
				}
			}

			v("Updated Resource Packs in options.txt");
		}

		v("Applying Pack Tweaks");
		for(PackTweak i : newPack.getTweaks())
		{
			tweak(newPack, i);
		}
	}

	private void tweak(Pack pack, PackTweak i)
	{
		if(i.shouldActivate(profile.getName()))
		{
			File file = new File(minecraftFolder, i.getFile());
			v("Applying Tweak on " + file.getPath());

			if(!file.exists() && i.isAddIfMissing())
			{
				v(file.getName() + " doesnt exist. Creating it");
				file.getParentFile().mkdirs();

				try
				{
					IO.writeAll(file, "");
				}

				catch(IOException e)
				{
					e.printStackTrace();
				}
			}

			try
			{
				String text = IO.readAll(file);
				KList<String> lines = new KList<>(text.split("\\Q\n\\E"));
				String newText = "";
				boolean found = false;

				for(String j : lines.copy())
				{
					String line = j;

					if(i.getFind().trim().equals(j.trim()) || j.trim().startsWith(i.getFind()))
					{
						found = true;
						line = i.getReplace();
					}

					newText += line + "\n";
				}

				if(!found && i.isAddIfMissing())
				{
					newText += i.getReplace() + "\n";
				}

				IO.writeAll(file, newText);
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	private void move(File s, File d)
	{
		try
		{
			Files.move(s.toPath(), d.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void revertForUpdate()
	{
		IO.delete(new File(minecraftFolder, "resourcepacks"));
		IO.delete(new File(minecraftFolder, "shaderpacks"));
		IO.delete(new File(minecraftFolder, "config"));
		IO.delete(new File(minecraftFolder, "mods"));
		IO.delete(new File(minecraftFolder, "resourcepacks"));
	}

	private void computeProfile(Pack newPack)
	{
		if(!Environment.profile.equals("auto"))
		{
			w("Not computing profile, environment profile != auto");
			if(newPack.getProfiles().isEmpty())
			{
				profile = new PackProfile("noprofile");
			}

			boolean found = false;

			for(PackProfile i : newPack.getProfiles())
			{
				if(i.getName().equals(Environment.profile))
				{
					profile = i;
					v("Selected Profile: " + i.getName());
					found = true;
					break;
				}
			}

			if(!found)
			{
				w("Could not locate profile " + Environment.profile);
				w("Choosing the first profile: " + newPack.getProfiles().get(0).getName());
				profile = newPack.getProfiles().get(0);
			}

			return;
		}

		if(newPack.getProfiles().isEmpty())
		{
			profile = new PackProfile("noprofile");
		}

		for(PackProfile i : newPack.getProfiles())
		{
			if(canBeActivated(i.getName(), i.getActivation()))
			{
				profile = i;
				v("Selected Profile: " + i.getName());
				break;
			}
		}
	}

	private boolean canBeActivated(String name, List<String> activation)
	{
		if(activation.isEmpty())
		{
			return true;
		}

		boolean fail = false;
		KMap<String, Double> mappers = new KMap<>();
		mappers.put("total_system_memory", (double) Platform.MEMORY.PHYSICAL.getTotalMemory() / 1024D / 1024D);
		mappers.put("free_system_memory", (double) Platform.MEMORY.PHYSICAL.getFreeMemory() / 1024D / 1024D);
		mappers.put("used_system_memory", (double) Platform.MEMORY.PHYSICAL.getUsedMemory() / 1024D / 1024D);
		mappers.put("cpu_threads", (double) Platform.CPU.getAvailableProcessors());
		mappers.put("free_space", (double) Platform.STORAGE.getFreeSpace(root) / 1024D / 1024D);

		KMap<String, Comp> functions = new KMap<>();
		functions.put("==", (a, b) -> a == b);
		functions.put(">=", (a, b) -> a >= b);
		functions.put("<=", (a, b) -> a <= b);
		functions.put("!=", (a, b) -> a != b);
		functions.put(">", (a, b) -> a > b);
		functions.put("<", (a, b) -> a < b);

		activating: for(String i : activation)
		{
			for(String j : functions.k())
			{
				if(i.contains(" " + j + " "))
				{
					Comp func = functions.get(j);
					String k = i.split("\\Q " + j + " \\E")[0].trim().toLowerCase();
					String v = i.split("\\Q " + j + " \\E")[1].trim().toLowerCase();

					if(mappers.containsKey(k))
					{
						try
						{
							double a = mappers.get(k);
							double b = Double.valueOf(v);

							if(func.compare(a, b))
							{
								l("System qualifies for " + name + "/" + i + " (" + a + ")");
							}

							else
							{
								fail = true;
								l("System does NOT qualify for profile " + name + " because of " + i + " (" + a + ")");
							}
						}

						catch(Throwable e)
						{
							w("Invalid Activator Value: " + v);
							continue activating;
						}
					}

					else
					{
						w("Invalid Activator: " + k);
						continue activating;
					}

					continue activating;
				}
			}
		}

		return !fail;
	}

	private void validatePackMeta()
	{
		packFileNew.delete();

		File o = new File(root, "override-pack.json");
		if(o.exists())
		{
			try
			{
				IO.copyFile(o, packFileNew);
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}

			w("Using override-pack pack as update pack.json.");
			w("To support downloading remote pack json files, delete this file.");
		}

		else
		{
			if(Environment.pack.startsWith("file://"))
			{
				try
				{
					Files.copy(new File(Environment.pack.substring("file://".length())).toPath(), packFileNew.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}

				catch(IOException e)
				{
					e.printStackTrace();
				}
			}

			else
			{
				downloadManager.download(Environment.pack, packFileNew);
			}
		}
	}

	private String javaw()
	{
		return System.getProperty("java.home") + File.separator + "bin" + File.separator + jex();
	}

	private String jex()
	{
		if(isWindows())
		{
			return "javaw.exe";
		}
		
		else if(isMac())
		{
			return "java";
		}
		
		else
		{
			return "java";
		}
	}

	public static boolean isWindows()
	{
		return (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);
	}

	public static boolean isMac()
	{
		return (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0);
	}

	public static boolean isUnix()
	{
		return (System.getProperty("os.name").toLowerCase().indexOf("nix") >= 0 || System.getProperty("os.name").toLowerCase().indexOf("nux") >= 0 || System.getProperty("os.name").toLowerCase().indexOf("aix") > 0);
	}

	public static boolean isSolaris()
	{
		return (System.getProperty("os.name").toLowerCase().indexOf("sunos") >= 0);
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
		try
		{
			JSONObject logFile = minecraftVersion.getJSONObject("logging").getJSONObject("client").getJSONObject("file");
			File logging = new File(assetsFolder, "log_configs/" + logFile.getString("id"));

			if(!logging.exists())
			{
				downloadManager.download(logFile.getString("url"), logging, logFile.getInt("size"));
			}
		}

		catch(Throwable e)
		{
			w("This version of minecraft is too old to support l4j configs");
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

			if(library.has("name") && ((library.has("serverreq") && library.getBoolean("serverreq")) || (library.has("clientreq") && library.getBoolean("clientreq"))))
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

		if(!client.exists())
		{
			downloadManager.download(minecraftVersion.getJSONObject("downloads").getJSONObject("client").getString("url"), client, minecraftVersion.getJSONObject("downloads").getJSONObject("client").getInt("size"));
		}
	}

	private void validateAssets() throws JSONException, IOException, InterruptedException
	{
		status("Validating Assets");
		minecraftVersion = new JSONObject(IO.readAll(minecraftVersionFile));
		JSONObject assetIndex = minecraftVersion.getJSONObject("assetIndex");
		assetsIndexId = assetIndex.getString("id");
		File index = new File(assetsIndexesFolder, assetsIndexId + ".json");

		if(!index.exists())
		{
			downloadManager.download(assetIndex.getString("url"), index);
			swapQueues();
		}

		assetManifest = new JSONObject(IO.readAll(index));
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
		versionManifest = new JSONObject(IO.readAll(versionManifestFile));
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
				KList<String> e = IO.listEntries(forgeUniversal);

				if(e.contains("version.json"))
				{
					FileOutputStream fos = new FileOutputStream(forgeVersionFile);
					IO.readEntry(forgeUniversal, "version.json", (s) ->
					{
						try
						{
							IO.fullTransfer(s, fos, 16819);
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

			forgeVersion = new JSONObject(IO.readAll(forgeVersionFile));
		}

		else if(forgeVersionFile.exists())
		{
			v("Deleting " + forgeVersionFile.getPath() + " because it's for forge.");
			forgeVersionFile.delete();
		}
	}

	private void validateForgeUniversal()
	{
		if(forgeUniversal.exists())
		{
			if(!Environment.forge_enabled)
			{
				v("Deleting " + forgeUniversal.getPath() + " because it's for forge.");
				forgeUniversal.delete();
			}

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
		commander.sendMessage("state=" + s);
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

		catch(Throwable e)
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

	public DownloadManager getDownloadManager()
	{
		return downloadManager;
	}

	public Commander getCommander()
	{
		return commander;
	}

	public File getRoot()
	{
		return root;
	}

	public File getLauncherRoot()
	{
		return launcherRoot;
	}

	public File getLauncherLibraries()
	{
		return launcherLibraries;
	}

	public File getLauncherMetadata()
	{
		return launcherMetadata;
	}

	public File getVersionManifestFile()
	{
		return versionManifestFile;
	}

	public File getAuthFolder()
	{
		return authFolder;
	}

	public File getForgeUniversal()
	{
		return forgeUniversal;
	}

	public File getMinecraftVersionFile()
	{
		return minecraftVersionFile;
	}

	public File getForgeVersionFile()
	{
		return forgeVersionFile;
	}

	public File getMinecraftFolder()
	{
		return minecraftFolder;
	}

	public File getNativesFolder()
	{
		return nativesFolder;
	}

	public File getAssetsIndexesFolder()
	{
		return assetsIndexesFolder;
	}

	public File getAssetsObjectsFolder()
	{
		return assetsObjectsFolder;
	}

	public File getAssetsFolder()
	{
		return assetsFolder;
	}

	public JSONObject getAssetManifest()
	{
		return assetManifest;
	}

	public JSONObject getVersionManifest()
	{
		return versionManifest;
	}

	public JSONObject getMinecraftVersion()
	{
		return minecraftVersion;
	}

	public JSONObject getForgeVersion()
	{
		return forgeVersion;
	}

	public String getAssetsIndexId()
	{
		return assetsIndexId;
	}

	public String getLauncherStatus()
	{
		return launcherStatus;
	}

	public String getVersionType()
	{
		return versionType;
	}

	public String getAuthUsername()
	{
		return authUsername;
	}

	public String getAuthUserType()
	{
		return authUserType;
	}

	public String getAuthUUID()
	{
		return authUUID;
	}

	public String getAuthAccessToken()
	{
		return authAccessToken;
	}

	public boolean isAuthenticated()
	{
		return authenticated;
	}

	public boolean isValidated()
	{
		return validated;
	}

	public Process getActiveProcess()
	{
		return activeProcess;
	}

	public String getPlatform()
	{
		return platform;
	}

	public void addProgressListener(Consumer<Double> p)
	{
		progressListemers.add(p);
	}

	public void publishProgress(double pgx)
	{
		progressListemers.forEach((p) -> p.accept(pgx));
	}

	public Pack getPack()
	{
		return pack;
	}
}
