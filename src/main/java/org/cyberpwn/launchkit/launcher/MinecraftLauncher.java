package org.cyberpwn.launchkit.launcher;

import art.arcane.quill.cache.AtomicCache;
import art.arcane.quill.collections.KList;
import art.arcane.quill.execution.J;
import art.arcane.quill.format.Form;
import art.arcane.quill.io.IO;
import art.arcane.quill.io.StreamGobbler;
import art.arcane.quill.json.JSONArray;
import art.arcane.quill.json.JSONObject;
import art.arcane.quill.logging.L;
import art.arcane.quill.tools.Download;
import lombok.Data;
import lombok.Getter;
import org.cyberpwn.launchkit.LauncherConfiguration;
import org.cyberpwn.launchkit.RepoManager;
import org.cyberpwn.launchkit.multimc.MMCInstance;
import org.cyberpwn.launchkit.multimc.MMCPack;
import org.cyberpwn.launchkit.util.*;
import org.zeroturnaround.zip.ZipUtil;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MinecraftLauncher implements Launcher
{
    public static String default_repository = "http://central.maven.org/maven2/";
    public static String url_forge_installer = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/{game-version}-{forge-version}/forge-{game-version}-{forge-version}-installer.jar";
    public static String url_forge_universal = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/{game-version}-{forge-version}/forge-{game-version}-{forge-version}-universal.jar";
    public static String url_asset_download = "http://resources.download.minecraft.net/{microhash}/{hash}";
    public static String minecraft_repository = "https://libraries.minecraft.net/";

    @Getter
    private final String instanceId;
    private final String platform = OSF.rawOS();
    private final File dataFolder;
    private final RepoManager repo;
    private final AtomicCache<File> minecraftFolder = new AtomicCache<>();
    private final AtomicCache<File> launcherFolder = new AtomicCache<>();
    private final AtomicCache<File> cacheFolder = new AtomicCache<>();
    private final MMCPack mmcPack;
    private final MMCInstance mmcInstance;
    private String versionType;
    private long downloadTotal = 0;
    private boolean authenticated;
    private String authUsername;
    private String authUUID;
    private String authAccessToken;
    private String authUserType;
    private String assetsIndexId;

    public MinecraftLauncher(String instanceId, File dataFolder) throws Throwable
    {
        authenticated = false;
        this.instanceId = instanceId;
        this.dataFolder = dataFolder;
        this.repo = new RepoManager(LauncherConfiguration.repository, new File(dataFolder, "instances/" + instanceId), LauncherConfiguration.gitUser, LauncherConfiguration.gitAccess);
        mmcPack = MMCPack.read(new File(getDataFolder(), "instances/" + instanceId + "/mmc-pack.json"));
        mmcInstance = new MMCInstance(new File(getDataFolder(), "instances/" + instanceId + "/instance.cfg"));
    }

    @Override
    public void start() throws IOException, InterruptedException, ClassNotFoundException {
        authenticate();
        validateMinecraft();
        JSONObject version = new JSONObject(IO.readAll(getMinecraftVersionFile()));
        JSONObject assetIndex = version.getJSONObject("assetIndex");
        assetsIndexId = assetIndex.getString("id");
        L.l("Using Java at " + javaw());
        KList<String> parameters = new KList<>();
        JSONObject meta = new JSONObject(IO.readAll(getForgeVersionFile()));
        String mainClass = meta.getString("mainClass");
        String t = meta.getString("minecraftArguments");
        t = filter(t, "auth_player_name", authenticated ? authUsername : "Player" + (int) (Math.random() * 9) + "" + (int) (Math.random() * 9) + "" + (int) (Math.random() * 9));
        t = filter(t, "version_name", getGameVersion());
        t = filter(t, "game_directory", getMinecraftDataFolder().getAbsolutePath());
        t = filter(t, "assets_root", getAssetsFolder().getAbsolutePath());
        t = filter(t, "assets_index_name", assetsIndexId);
        t = filter(t, "auth_uuid", authenticated ? authUUID : UUID.randomUUID().toString().replaceAll("-", ""));
        t = filter(t, "auth_access_token", authenticated ? authAccessToken : "offline");
        t = filter(t, "user_type", authenticated ? authUserType : "mojang");
        t = filter(t, "version_type", versionType);
        parameters.add(javaw());

        parameters.add("-Xmx" + mmcInstance.getMaxMemAlloc() + "m");
        parameters.add("-Xms" + mmcInstance.getMinMemAlloc() + "m");

        if(mmcInstance.getJvmArgs().contains(" "))
        {
            parameters.addAll(Arrays.asList(mmcInstance.getJvmArgs().split("\\Q \\E")));
        }

        else
        {
            parameters.add(mmcInstance.getJvmArgs());
        }

        parameters.add("-Djava.library.path=" + getLauncherNativesFolder().getAbsolutePath());
        parameters.add("-Dorg.lwjgl.librarypath=" + getLauncherNativesFolder().getAbsolutePath());
        parameters.add("-Dnet.java.games.input.librarypath=" + getLauncherNativesFolder().getAbsolutePath());
        parameters.add("-Duser.home=" + getMinecraftDataFolder().getAbsolutePath());
        parameters.add("-Duser.language=en");
        parameters.add("-Djava.net.preferIPv4Stack=true");
        parameters.add("-Djava.net.useSystemProxies=true");
        parameters.add("-Dlog4j.skipJansi=true");
        parameters.add("-cp");
        StringBuilder cpb = new StringBuilder("");

        for(File f : compileClasspath(getLauncherLibrariesFolder()))
        {
            cpb.append(OSF.getJavaDelimiter());
            cpb.append(f.getAbsolutePath());
        }

        cpb.deleteCharAt(0);
        parameters.add(cpb.toString());
        parameters.add(mainClass);
        parameters.add(t.trim().split("\\Q \\E"));

        L.v("======================================================================");

        for(String i : parameters)
        {
            if(i.contains(";"))
            {
                for(String j : i.split(";"))
                {
                    L.v("CLASSPATH: " + j);
                }
            }

            else
            {
                L.v("ARGS: " + i);
            }
        }

        L.v("====================================================================== ");
        parameters.forEach(L::v);
        L.v("======================================================================  ");

        ProcessBuilder pb = new ProcessBuilder(parameters);
        pb.directory(getMinecraftDataFolder());
        new Thread(() -> {
            final Process activeProcess;
            try {
                activeProcess = pb.start();
                new StreamGobbler(activeProcess.getInputStream(), "Client|OUT");
                new StreamGobbler(activeProcess.getErrorStream(), "Client|ERR");
                int code = activeProcess.waitFor();
                L.l("Client Process exited with code " + code);
                J.sleep(500);
                L.flush();
                System.exit(code);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            J.sleep(500);
            L.flush();
            System.exit(0);
        }).start();
    }

    private void authenticate() throws IOException, ClassNotFoundException {
        if(authenticated)
        {
            return;
        }

        authenticateWithToken();

        if(!authenticated)
        {
            showAuthDialog();
        }

        if(!authenticated)
        {
            authenticate();
        }
    }

    private void showAuthDialog() {
        AtomicBoolean done = new AtomicBoolean(false);
        J.a(() -> EventQueue.invokeLater(() -> {
        JFrame f = new JFrame();
        LoginDialog ld = null;
        LoginDialog finalLd = ld;
        Consumer<String> cb = (up) -> {
            String[] upa = up.split("\\Q:X-:-X:\\E");
            try {
                authenticateWithCredentials(upa[0], upa[1]);
                f.setVisible(false);
                try
                {
                    finalLd.setVisible(false);
                }

                catch(Throwable e)
                {

                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            done.set(true);
        };

        ld = new LoginDialog(f, cb);
        ld.setVisible(true);
        ld.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }));

        while(!done.get())
        {
            J.sleep(100);
        }
    }

    public Launcher authenticateExternal(String profileName, String profileType, String uuid, String accessToken)
    {
        authenticated = true;
        authUsername = profileName;
        authUserType = profileType;
        authUUID = uuid;
        authAccessToken = accessToken;
        L.l("Authentication from external sources cannot be checked. They will be passed to the client only.");
        return this;
    }

    public File getAuthFolder()
    {
     return new File(getLauncherDataFolder(), "auth");
    }

    public Launcher authenticateWithCredentials(String username, String password) throws ClassNotFoundException, IOException
    {
        ClientAuthentication a = new ClientAuthentication(new File(getAuthFolder(), "auth.dx"), username, password);

        switch(a.authenticate())
        {
            case FAILED:
                authenticated = false;
                L.w("Authentication FAILED");
                break;
            case SUCCESS:
                authenticated = true;
                authUsername = a.getProfileName();
                authUserType = a.getProfileType();
                authUUID = a.getUuid();
                authAccessToken = a.getAccessToken();
                L.l("Authentication SUCCESS! You can now use /mc auth next time instead of passing credentials again.");
                break;
            default:
                break;
        }

        return this;
    }

    public Launcher authenticateWithToken() throws ClassNotFoundException, IOException
    {
        ClientAuthentication a = new ClientAuthentication(new File(getAuthFolder(), "auth.dx"));

        switch(a.authenticate())
        {
            case FAILED:
                authenticated = false;
                L.w("Authentication FAILED");
                break;
            case SUCCESS:
                authenticated = true;
                authUsername = a.getProfileName();
                authUserType = a.getProfileType();
                authUUID = a.getUuid();
                authAccessToken = a.getAccessToken();
                L.l("Authentication SUCCESS!");
                break;
            default:
                break;
        }

        return this;
    }

    private List<File> compileClasspath(File libs)
    {
        List<File> classpath = new KList<>();

        for(File i : getFiles(libs))
        {
            classpath.add(new File(i.getPath()));
        }

        classpath.add(getMinecraftClientFile());

        return classpath;
    }

    private String javaw()
    {
        return new File(getLauncherDataFolder(), "jre/bin/javaw.exe").getAbsolutePath();
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

    @Override
    public String getForgeVersion() {
        return mmcPack.findForgeVersion();
    }

    @Override
    public String getGameVersion() {
        return mmcPack.findGameVersion();
    }

    @Override
    public String getName() {
        return mmcInstance.getName();
    }

    @Override
    public String getJVMArgs() {
        return mmcInstance.getJvmArgs();
    }

    @Override
    public int getMaxMem() {
        return mmcInstance.getMaxMemAlloc();
    }

    @Override
    public int getMinMem() {
        return mmcInstance.getMinMemAlloc();
    }

    @Override
    public File getAssetsFolder() {
        return new File(getLauncherDataFolder(), "assets");
    }

    @Override
    public File getAssetsObjectsFolder() {
        return new File(getAssetsFolder(), "objects");
    }

    @Override
    public File getAssetsIndexesFolder() {
        return new File(getAssetsFolder(), "indexes");
    }

    @Override
    public File getMinecraftVersionFile() {
        return new File(getLauncherDataFolder(), "minecraft-" + getGameVersion() + ".json");
    }

    @Override
    public File getVersionManifestFile() {
        return new File(getLauncherDataFolder(), "version-manifest.json");
    }

    @Override
    public File getForgeVersionFile() {
        return new File(getLauncherDataFolder(), "forge-version-" + getForgeVersion() + "-" + getGameVersion() + ".json");
    }

    @Override
    public File getMinecraftClientFile() {
        return new File(getLauncherDataFolder(), "versions/minecraft-" + getGameVersion() + ".jar");
    }

    @Override
    public File getLauncherLibrariesFolder() {
        return new File(getLauncherDataFolder(), "libraries");
    }

    @Override
    public File getLauncherNativesFolder() {
        return new File(getLauncherDataFolder(), "natives");
    }

    @Override
    public File getForgeUniversalFile() {
        return null;
    }

    private void download(String url, File f)
    {
        try {
            f.getParentFile().mkdirs();
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            J.attemptAsync(readableByteChannel::close);
            downloadTotal += f.length();
            L.v("Downloaded " + f.getName() + " (" + Form.fileSize(f.length())  + ", " + Form.fileSize(downloadTotal) + "Total)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validateMinecraft() throws IOException {
        validateMinecraftClient();
        validateAssets();
        validateMinecraftLibraries();
        validateForgeLibraries();
        validateNatives();
        validateRuntime();
    }

    private void validateRuntime() {
        File jre = new File(getLauncherDataFolder(), "jre");
        File jred = new File(getCacheDataFolder(), "jre.zip");

        if(!jred.exists())
        {
            download("https://cdn.myguide.care/files/jre.zip", jred);
        }

        if(!jre.exists())
        {
            ZipUtil.unpack(jred, jre);
        }
    }

    private void validateMinecraftClient() throws IOException {
        validateMinecraftVersion();
        if(!getMinecraftClientFile().exists())
        {
            download(new JSONObject(IO.readAll(getMinecraftVersionFile())).getJSONObject("downloads").getJSONObject("client").getString("url"), getMinecraftClientFile());
            patchClientJar();
        }
    }


    private List<File> getFiles(File folder)
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

    private void patchClientJar() {
        File f = new File(getMinecraftDataFolder() + "launchkit/client-inject");

        // TODO: patch

    }

    private void validateMinecraftLibraries() throws IOException {
        validateMinecraftVersion();
        JSONArray libraries = new JSONObject(IO.readAll(getMinecraftVersionFile())).getJSONArray("libraries");
        validateLibraries(libraries);
    }

    private void validateForgeLibraries() throws IOException {
        validateForgeVersion();
        JSONArray libraries = new JSONObject(IO.readAll(getForgeVersionFile())).getJSONArray("libraries");
        validateLibraries(libraries);
    }

    private String filter(String template, String parameter, String value)
    {
        return template.replace("${" + parameter + "}", value);
    }

    public File getForgeUniversalLibraryFile()
    {
        return new File(getLauncherLibrariesFolder(), "net/minecraftforge/forge/" + getGameVersion() + "/" + getForgeVersion() + "/forge-" + getGameVersion() + "-" + getForgeVersion() + "-universal.jar");
    }

    private void validateForgeVersion()
    {
        File forgedl = getForgeUniversalLibraryFile();

        if(!forgedl.exists())
        {
            download(url_forge_universal.replaceAll("\\Q{game-version}\\E", getGameVersion()).replaceAll("\\Q{forge-version}\\E", getForgeVersion()), forgedl);
        }

        JSONObject v = null;

        try
        {
            v = new JSONObject(IO.readAll(new ByteArrayInputStream(ZipUtil.unpackEntry(forgedl, "version.json"))));
        }

        catch(Throwable e)
        {
            L.f("Failed to find version.json in " + forgedl.getPath());
            L.w("Attempting to download the forge INSTALLER instead. Hopefully it's in there!");

            File forgedli = new File(getCacheDataFolder(), "forge-" + getGameVersion() + "-" + getForgeVersion() + "-installer" + ".jar");

            if(!forgedli.exists())
            {
                download(url_forge_installer.replaceAll("\\Q{game-version}\\E", getGameVersion()).replaceAll("\\Q{forge-version}\\E", getForgeVersion()), forgedli);
            }

            try
            {
                v = new JSONObject(IO.readAll(new ByteArrayInputStream(ZipUtil.unpackEntry(forgedli, "version.json"))));
            }

            catch(Throwable e2)
            {
                e2.printStackTrace();
            }
        }

        if(v != null)
        {
            L.i("Found the elusive version json!");
            try {
                getForgeVersionFile().getParentFile().mkdirs();
                IO.writeAll(getForgeVersionFile(), v.toString(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validateAssets() throws IOException {
        validateMinecraftVersion();
        JSONObject version = new JSONObject(IO.readAll(getMinecraftVersionFile()));
        JSONObject assetIndex = version.getJSONObject("assetIndex");
        assetsIndexId = assetIndex.getString("id");
        File index = new File(getAssetsIndexesFolder(), assetsIndexId + ".json");

        if(!index.exists())
        {
            download(assetIndex.getString("url"), index);
        }

        JSONObject assetManifest = new JSONObject(IO.readAll(index));
        JSONObject objects = assetManifest.getJSONObject("objects");
        Iterator<String> it = objects.keys();

        while(it.hasNext())
        {
            String key = it.next();
            JSONObject object = objects.getJSONObject(key);
            String hash = object.getString("hash");
            String microhash = hash.substring(0, 2);
            File objectFile = new File(getAssetsObjectsFolder(), microhash + "/" + hash);

            if(objectFile.exists())
            {
                continue;
            }

            download(url_asset_download.replaceAll("\\Q{microhash}\\E", microhash).replaceAll("\\Q{hash}\\E", hash), objectFile);
        }
    }

    private void validateMinecraftVersion() throws IOException {
        L.v("Validating Minecraft Version " + getGameVersion());
        validateManifest();
        JSONArray versions = new JSONObject(IO.readAll(getVersionManifestFile())).getJSONArray("versions");

        for(int i = 0; i < versions.length(); i++)
        {
            JSONObject version = versions.getJSONObject(i);

            if(version.getString("id").equals(getGameVersion()))
            {
                if(!version.getString("type").equals("release"))
                {
                    L.w("The version " + getGameVersion() + " is considered a " + version.getString("type") + ". The game may not be stable!");
                }

                versionType = version.getString("type");
                download(version.getString("url"), getMinecraftVersionFile());
                break;
            }
        }
    }

    private void validateManifest()
    {
        if(!getVersionManifestFile().exists())
        {
            L.v("Validating Version Manifest");
            download("https://launchermeta.mojang.com/mc/game/version_manifest.json", getVersionManifestFile());
        }
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
                        File artifactFile = new File(getLauncherLibrariesFolder(), artifact.getString("path"));

                        if(!artifactFile.exists())
                        {
                            download(artifact.getString("url"), artifactFile);
                        }
                    }
                }

                if(downloads.has("classifiers"))
                {
                    JSONObject classifiers = downloads.getJSONObject("classifiers");

                    if(classifiers.has("natives-" + platform))
                    {
                        JSONObject nativeClassifier = classifiers.getJSONObject("natives-" + platform);
                        File nativesArchive = new File(getLauncherLibrariesFolder(), nativeClassifier.getString("path"));

                        if(!nativesArchive.exists())
                        {
                            download(nativeClassifier.getString("url"), nativesArchive);
                        }
                    }
                }
            }
        }
    }

    private void validateLibrariesWithoutHelp(JSONArray libraries)
    {
        for(int i = 0; i < libraries.length(); i++)
        {
            JSONObject library = libraries.getJSONObject(i);

            if(library.has("name") && ((library.has("serverreq") && library.getBoolean("serverreq")) || (library.has("clientreq") && library.getBoolean("clientreq"))))
            {
                Artifact artifact = new Artifact(library.getString("name"), library.has("url") ? library.getString("url") : minecraft_repository);
                File file = artifact.getPath(getLauncherLibrariesFolder());

                if(!file.exists())
                {
                    download(filterArtifact(artifact).getFormalUrl(), file);
                }
            }
        }
    }

    private Artifact filterArtifact(Artifact a)
    {
        if(a.getGroupId().equals("com.typesafe") && a.getArtifactId().equals("config"))
        {
            a.setRepo(default_repository);
        }

        if(a.getGroupId().equals("com.typesafe.akka") && a.getArtifactId().equals("akka-actor_2.11"))
        {
            a.setRepo(default_repository);
        }

        return a;
    }

    private void validateNatives() throws IOException {
        getLauncherNativesFolder().mkdirs();
        JSONArray libraries = new JSONObject(IO.readAll(getMinecraftVersionFile())).getJSONArray("libraries");
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
                        File nativesArchive = new File(getLauncherLibrariesFolder(), nativeClassifier.getString("path"));

                        try
                        {
                            extractNatives(nativesArchive, getLauncherNativesFolder());
                        }

                        catch(IOException e)
                        {
                            e.printStackTrace();
                            L.f("Failed to extract native " + nativesArchive.getPath());
                        }
                    }
                }
            }
        }
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

        L.v("Extracted " + n + " from " + zip.getName());

        fos.close();

        return read;
    }
}
