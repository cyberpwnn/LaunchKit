package org.cyberpwn.launchkit.multimc;

import lombok.Data;

@Data
public class MMCComponent {
    private String cachedName;
    private String cachedVersion;
    private boolean cachedVolatile;
    private boolean dependencyOnly;
    private String uid;
    private String version;
}
