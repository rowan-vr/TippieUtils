/*
 * This a modified version adopted of https://github.com/LuckPerms/LuckPerms/blob/04bb035a83af96d55e7dffea514c505c66ce0f54/common/src/main/java/me/lucko/luckperms/common/dependencies/DependencyManager.java
 * licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 */

package me.tippie.tippieutils.dependencies;


import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import me.tippie.tippieutils.dependencies.classloader.IsolatedClassLoader;
import me.tippie.tippieutils.dependencies.classloader.ReflectionClassPathAppender;
import me.tippie.tippieutils.dependencies.relocations.Relocation;
import me.tippie.tippieutils.dependencies.relocations.RelocationHandler;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Loads and manages runtime dependencies for the plugin.
 */
public class DependencyManager {
    /**
     * The plugin instance
     */
    @Getter
    private final Plugin plugin;

    private final ReflectionClassPathAppender classPathAppender;

    /**
     * The path where library jars are cached.
     */
    private final Path cacheDirectory;

    /**
     * A map of dependencies which have already been loaded.
     */
    private final Map<Dependency, Path> loaded = new HashMap<>();
    /**
     * A map of isolated classloaders which have been created.
     */
    private final Map<ImmutableSet<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
    /**
     * Cached relocation handler instance.
     */
    private RelocationHandler relocationHandler = null;

    public DependencyManager(Plugin plugin) {
        this.plugin = plugin;
//        this.registry = new DependencyRegistry(plugin);
        this.cacheDirectory = setupCacheDirectory(plugin);
        this.classPathAppender = new ReflectionClassPathAppender(plugin.getClass().getClassLoader());

    }

    private synchronized RelocationHandler getRelocationHandler() {
        if (this.relocationHandler == null) {
            this.relocationHandler = new RelocationHandler(this);
        }
        return this.relocationHandler;
    }

    public <T> CompletableFuture<T> supplyIsolated(Set<Dependency> dependencies, Supplier<T> supplier){
        CompletableFuture<T> future = new CompletableFuture<>();

        Thread t = new Thread(() -> {
          future.complete(supplier.get());
        });
        t.setContextClassLoader(obtainClassLoaderWith(dependencies));
        t.setUncaughtExceptionHandler((thread, throwable) -> {
            plugin.getLogger().log(Level.SEVERE, "An error occurred in an isolated thread of plugin " +plugin.getName()+"!", throwable);
        });
        t.start();

        return future;
    }

    public void runIsolated(Set<Dependency> dependencies, Runnable runnable){
        Thread t = new Thread(runnable);
        t.setContextClassLoader(obtainClassLoaderWith(dependencies));
        t.setUncaughtExceptionHandler((thread, throwable) -> {
            plugin.getLogger().log(Level.SEVERE, "An error occurred in an isolated thread of plugin " +plugin.getName()+"!", throwable);
        });
        t.start();
    }

    public IsolatedClassLoader obtainClassLoaderWith(Set<Dependency> dependencies) {
        ImmutableSet<Dependency> set = ImmutableSet.copyOf(dependencies);

        for (Dependency dependency : dependencies) {
            if (!this.loaded.containsKey(dependency)) {
                throw new IllegalStateException("Dependency " + dependency + " is not loaded.");
            }
        }

        synchronized (this.loaders) {
            IsolatedClassLoader classLoader = this.loaders.get(set);
            if (classLoader != null) {
                return classLoader;
            }

            URL[] urls = set.stream()
                    .map(this.loaded::get)
                    .map(file -> {
                        try {
                            return file.toUri().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(URL[]::new);

            classLoader = new IsolatedClassLoader(urls);
            this.loaders.put(set, classLoader);
            return classLoader;
        }
    }

    public void loadDependencies(Set<Dependency> dependencies) {
        CountDownLatch latch = new CountDownLatch(dependencies.size());

        for (Dependency dependency : dependencies) {
                try {
                    this.plugin.getLogger().log(Level.INFO, "Loading dependency " + dependency.getMavenRepoPath() + "...");
                    loadDependency(dependency);
                } catch (Throwable e) {
                    this.plugin.getLogger().log(Level.SEVERE, "Unable to load dependency " + dependency.getMavenRepoPath() + ".", e);
                } finally {
                    latch.countDown();
                }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void loadDependency(Dependency dependency) throws Exception {
        if (this.loaded.containsKey(dependency)) {
            return;
        }

        Path file = remapDependency(dependency, downloadDependency(dependency));

        this.loaded.put(dependency, file);

        if (dependency.getProperties().isAutoload()) {
            classPathAppender.addJarToClasspath(file);
        }
    }

    private Path downloadDependency(Dependency dependency) throws DependencyDownloadException {
        Path file = this.cacheDirectory.resolve(dependency.getFileName(null));

        // if the file already exists, don't attempt to re-download it.
        if (Files.exists(file)) {
            return file;
        }

        DependencyDownloadException lastError = null;

        // attempt to download the dependency from each repo in order.
        for (DependencyRepository repo : DependencyRepository.REPOSITORIES) {
            try {
                repo.download(dependency, file);
                return file;
            } catch (DependencyDownloadException e) {
                if (lastError != null)
                    e.addSuppressed(lastError);
                else lastError = e;
            }
        }

        throw Objects.requireNonNull(lastError);
    }

    private Path remapDependency(Dependency dependency, Path normalFile) throws Exception {
        List<Relocation> rules = new ArrayList<>(dependency.getRelocations());

        if (rules.isEmpty()) {
            return normalFile;
        }

        Path remappedFile = this.cacheDirectory.resolve(dependency.getFileName("remapped"));

        // if the remapped source exists already, just use that.
        if (Files.exists(remappedFile)) {
            return remappedFile;
        }

        getRelocationHandler().remap(normalFile, remappedFile, rules);
        return remappedFile;
    }

    private static Path setupCacheDirectory(Plugin plugin) {
        Path cacheDirectory = new File(plugin.getDataFolder(), "libs").toPath();
        try {
            Files.createDirectories(cacheDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create libs directory", e);
        }
        return cacheDirectory;
    }

}
