/* Adapted from https://github.com/LuckPerms/LuckPerms/blob/04bb035a83af96d55e7dffea514c505c66ce0f54/common/src/main/java/me/lucko/luckperms/common/dependencies/classloader/IsolatedClassLoader.java
    Copyright (c) lucko (Luck) <luck@lucko.me>
    Copyright (c) contributors
    licensed under the MIT License.
*/

package me.tippie.tippieutils.dependencies.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A classloader "isolated" from the rest of the Minecraft server.
 *
 * <p>Used to load specific LuckPerms dependencies without causing conflicts
 * with other plugins, or libraries provided by the server implementation.</p>
 */
public class IsolatedClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public IsolatedClassLoader(URL[] urls) {
        /*
         * ClassLoader#getSystemClassLoader returns the AppClassLoader
         *
         * Calling #getParent on this returns the ExtClassLoader (Java 8) or
         * the PlatformClassLoader (Java 9). Since we want this classloader to
         * be isolated from the Minecraft server (the app), we set the parent
         * to be the platform class loader.
         */
        super(urls, ClassLoader.getSystemClassLoader().getParent());
    }
}