/* Adapted from https://github.com/LuckPerms/LuckPerms/blob/04bb035a83af96d55e7dffea514c505c66ce0f54/common/src/main/java/me/lucko/luckperms/common/dependencies/classloader/IsolatedClassLoader.java
    Copyright (c) lucko (Luck) <luck@lucko.me>
    Copyright (c) contributors
    licensed under the MIT License.
*/
package me.tippie.tippieutils.dependencies.relocations;

import org.bukkit.plugin.Plugin;

import java.net.URLClassLoader;
import java.util.Objects;

public final class Relocation {
    private static String RELOCATION_PREFIX = null;
    static boolean MAVEN_OUTPUT = false;

    public static void init(String prefix){
        RELOCATION_PREFIX = prefix;
    }

    public static void setMavenOutput(boolean mavenOutput){
        MAVEN_OUTPUT = mavenOutput;
    }

    public static Relocation of(String id, String pattern) {
        if (RELOCATION_PREFIX == null) throw new IllegalStateException("Relocation prefix not initialized, initialize with Relocation.init(plugin)");

        return new Relocation(pattern.replace("{}", "."), RELOCATION_PREFIX + id);
    }

    private final String pattern;
    private final String relocatedPattern;

    private Relocation(String pattern, String relocatedPattern) {
        this.pattern = pattern;
        this.relocatedPattern = relocatedPattern;
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getRelocatedPattern() {
        return this.relocatedPattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relocation that = (Relocation) o;
        return Objects.equals(this.pattern, that.pattern) &&
                Objects.equals(this.relocatedPattern, that.relocatedPattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.relocatedPattern);
    }
}
