package org.cyberpwn.launchkit;

import art.arcane.quill.collections.KList;
import art.arcane.quill.io.IO;
import art.arcane.quill.logging.L;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RepoManager {
    private final String repository; // https://github.com/cyberpwnn/Auram.git
    private final File repositoryLocation;
    private final Git git;
    private final CredentialsProvider credentials;
    private KList<String> bb = new KList<>();

    public RepoManager(String repository, File repositoryRoot, String gitUser, String gitAccessToken) throws IOException, GitAPIException {
        this.repository = repository;
        this.repositoryLocation = repositoryRoot;
        credentials = new UsernamePasswordCredentialsProvider(gitUser, gitAccessToken);

        loadbb();

        if (repositoryLocation.exists() && repositoryLocation.listFiles().length > 0) {
            git = new Git(new FileRepositoryBuilder()
                    .setGitDir(new File(repositoryLocation, ".git"))
                    .build());
        } else {
            L.i("Cloning to " + repositoryLocation);
            git = Git.cloneRepository()
                    .setCloneAllBranches(true)
                    .setURI(repository)
                    .setCredentialsProvider(credentials)
                    .setDirectory(repositoryLocation).call();
        }

        pull();
    }

    private void loadbb() throws IOException {
        File f = new File(repositoryLocation, ".minecraft/launchkit/bounceback.txt");

        if(f.exists())
        {
            bb = KList.from(IO.readAll(f).split("\\Q\n\\E"));
        }
    }

    public void pull() {
        PullResult r = call(git.pull());
        if (r.isSuccessful()) {
            L.i("Pulled Successfully");
        } else {
            L.f("Failed to pull");
        }
    }

    private <T> T call(GitCommand<?> c) {
        if (c instanceof TransportCommand) {
            TransportCommand<?, T> t = (TransportCommand<?, T>) c;
            t.setCredentialsProvider(credentials);
        }

        try {
            return (T) c.call();
        } catch (Throwable e) {
            e.printStackTrace();

            if(e instanceof CheckoutConflictException)
            {
                KList<File> conflicts = new KList<>();
                CheckoutConflictException cx = (CheckoutConflictException) e;

                for(String co : cx.getConflictingPaths())
                {
                    conflicts.add(new File(repositoryLocation, co));
                }

                KList<File> bounceback = new KList<>();
                File bouncebackdir = new File(repositoryLocation, ".bounceback");
                bouncebackdir.mkdirs();

                for(File i : conflicts)
                {
                    if(bb.contains(i.getName()))
                    {
                        L.w("Bounce Back Conflicted File: " + i.getAbsolutePath());
                        bounceback.add(i);
                        try {
                            FileUtils.copyFile(i, new File(bouncebackdir, IO.hash(i.getAbsolutePath())));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    else
                    {
                        L.w("Rebasing File " + i.getAbsoluteFile());
                    }
                    i.delete();
                }

                L.w("Re-attempting to pull after conflicts were resolved...");
                try {
                    T t = (T) c.call();

                    L.i("Assuming pull was successful, re-injecting bounceback files...");

                    for(File i : bounceback)
                    {
                        FileUtils.copyFile(new File(bouncebackdir, IO.hash(i.getAbsolutePath())), i);
                        L.i("Bounced Back " + i.getAbsolutePath());
                    }

                    return t;
                } catch (Throwable e2) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
