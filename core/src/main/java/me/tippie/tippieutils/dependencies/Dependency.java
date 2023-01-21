/*
 * This a modified version adopted of https://github.com/LuckPerms/LuckPerms/blob/04bb035a83af96d55e7dffea514c505c66ce0f54/common/src/main/java/me/lucko/luckperms/common/dependencies/
 * licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 */

package me.tippie.tippieutils.dependencies;
import com.google.common.collect.ImmutableList;
import lombok.*;
import me.tippie.tippieutils.dependencies.relocations.Relocation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Dependency {
    private static final HashMap<String, Dependency> KNOWN_DEPENDENCIES = new HashMap<>();

    private final String mavenRepoPath;
    private final String artifactId;
    private final String version;
    private final byte[] checksum;
    private final List<Relocation> relocations;
    @Getter
    private final Properties properties;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";


    public static Dependency of(String groupId, String artifactId, String version, String checksum, Properties properties, Relocation... relocations) {
        String repoPath = String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                version,
                rewriteEscaping(artifactId),
                version
        );

        Dependency existing = KNOWN_DEPENDENCIES.get(repoPath);
        if (existing != null)
            return existing;


        Dependency dependency = new Dependency(groupId, artifactId, version, checksum,properties, relocations);
        KNOWN_DEPENDENCIES.put(repoPath, dependency);
        return dependency;
    }

    private Dependency(String groupId, String artifactId, String version, String checksum, Properties properties, Relocation... relocations) {
        this.artifactId = rewriteEscaping(artifactId);
        this.mavenRepoPath = String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                version,
                rewriteEscaping(artifactId),
                version
        );
        this.version = version;
        this.checksum = Base64.getDecoder().decode(checksum);
        this.relocations = ImmutableList.copyOf(relocations);
        this.properties = properties;
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

    public String getFileName(String classifier) {
        String name = this.artifactId.toLowerCase(Locale.ROOT).replace('_', '-');
        String extra = classifier == null || classifier.isEmpty()
                ? ""
                : "-" + classifier;

        return name + "-" + this.version + extra + ".jar";
    }

    String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

    public byte[] getChecksum() {
        return this.checksum;
    }

    public boolean checksumMatches(byte[] hash) {
        return Arrays.equals(this.checksum, hash);
    }

    public List<Relocation> getRelocations() {
        return this.relocations;
    }

    /**
     * Creates a {@link MessageDigest} suitable for computing the checksums
     * of dependencies.
     *
     * @return the digest
     */
    public static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Setter @Getter @Builder
    public static class Properties {
        boolean autoload;
    }
}
