package org.cyberpwn.launchkit.multimc;

import art.arcane.quill.collections.KList;
import art.arcane.quill.io.IO;
import com.google.gson.Gson;
import lombok.Data;

import java.io.File;
import java.io.IOException;

@Data
public class MMCPack {
    private int formatVersion;
    private KList<MMCComponent> components;

    public MMCPack()
    {

    }

    public String findGameVersion()
    {
        for(MMCComponent i : components)
        {
            if(i.getCachedName().equals("Minecraft"))
            {
                return i.getVersion();
            }
        }
        return null;
    }

    public String findForgeVersion()
    {
        for(MMCComponent i : components)
        {
            if(i.getCachedName().equals("Forge"))
            {
                return i.getVersion();
            }
        }
        return null;
    }

    public static MMCPack read(File j) throws IOException {
        return new Gson().fromJson(IO.readAll(j), MMCPack.class);
    }
}
