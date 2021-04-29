package org.cyberpwn.launchkit.multimc;

import art.arcane.quill.collections.KList;
import art.arcane.quill.io.IO;
import lombok.Data;

import java.io.File;
import java.io.IOException;

@Data
public class MMCInstance {
    private String jvmArgs;
    private int maxMemAlloc;
    private int minMemAlloc;
    private String name;

    public MMCInstance()
    {

    }

    public MMCInstance(File file) throws IOException {
        KList<String> m = KList.from(IO.readAll(file).split("\\Q\n\\E")).convert(String::trim);

        for(String i : m)
        {
            try
            {
                KList<String> mx = KList.from(i.split("\\Q=\\E"));
                String key = mx.pop();
                String value = mx.toString("=");

                if(key.equals("JvmArgs"))
                {
                    jvmArgs = value;
                }

                if(key.equals("MaxMemAlloc"))
                {
                    maxMemAlloc = Integer.parseInt(value);
                }

                if(key.equals("MinMemAlloc"))
                {
                    minMemAlloc = Integer.parseInt(value);
                }

                if(key.equals("name"))
                {
                    name = value;
                }
            }

            catch(Throwable e)
            {

            }
        }
    }
}
