/*
 * This a modified version adopted of https://github.com/LuckPerms/LuckPerms/blob/04bb035a83af96d55e7dffea514c505c66ce0f54/common/src/main/java/me/lucko/luckperms/common/dependencies/
 * licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 */


package me.tippie.tippieutils.dependencies.classloader;

import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class ReflectionClassPathAppender {
    private final URLClassLoaderAccess classLoaderAccess;

    public ReflectionClassPathAppender(ClassLoader classLoader) throws IllegalStateException {
        if (classLoader instanceof URLClassLoader) {
            this.classLoaderAccess = URLClassLoaderAccess.create((URLClassLoader) classLoader);
        } else {
            throw new IllegalStateException("ClassLoader is not instance of URLClassLoader");
        }
    }

    public void addJarToClasspath(Path file) {
        try {
            this.classLoaderAccess.addURL(file.toUri().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
