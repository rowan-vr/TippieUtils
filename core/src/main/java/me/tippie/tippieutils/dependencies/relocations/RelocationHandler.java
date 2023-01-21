package me.tippie.tippieutils.dependencies.relocations;

import me.tippie.tippieutils.dependencies.Dependency;
import me.tippie.tippieutils.dependencies.DependencyManager;
import me.tippie.tippieutils.dependencies.classloader.IsolatedClassLoader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles class runtime relocation of packages in downloaded dependencies
 */
public class RelocationHandler {
    public static final Set<Dependency> DEPENDENCIES = Set.of(
            Dependency.of("org.ow2.asm",
                    "asm",
                    "9.1",
                    "zaTeRV+rSP8Ly3xItGOUR9TehZp6/DCglKmG8JNr66I=",
                    Dependency.Properties.builder().autoload(false).build()
            ),
            Dependency.of("org.ow2.asm",
                    "asm-commons",
                    "9.1",
                    "r8sm3B/BLAxKma2mcJCN2C4Y38SIyvXuklRplrRwwAw=",
                    Dependency.Properties.builder().autoload(false).build()
            ),
            Dependency.of("me.lucko",
                    "jar-relocator",
                    "1.4",
                    "1RsiF3BiVztjlfTA+svDCuoDSGFuSpTZYHvUK8yBx8I=",
                    Dependency.Properties.builder().autoload(false).build()
            ));
    private static final String JAR_RELOCATOR_CLASS = "me.lucko.jarrelocator.JarRelocator";
    private static final String JAR_RELOCATOR_RUN_METHOD = "run";

    private final Constructor<?> jarRelocatorConstructor;
    private final Method jarRelocatorRunMethod;

    public RelocationHandler(DependencyManager dependencyManager) {
        try {
            // download the required dependencies for remapping
            dependencyManager.loadDependencies(DEPENDENCIES);
            // get a classloader containing the required dependencies as sources
            IsolatedClassLoader classLoader = dependencyManager.obtainClassLoaderWith(DEPENDENCIES);

            // load the relocator class
            Class<?> jarRelocatorClass = classLoader.loadClass(JAR_RELOCATOR_CLASS);

            // prepare the the reflected constructor & method instances
            this.jarRelocatorConstructor = jarRelocatorClass.getDeclaredConstructor(File.class, File.class, Map.class);
            this.jarRelocatorConstructor.setAccessible(true);

            this.jarRelocatorRunMethod = jarRelocatorClass.getDeclaredMethod(JAR_RELOCATOR_RUN_METHOD);
            this.jarRelocatorRunMethod.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void remap(Path input, Path output, List<Relocation> relocations) throws Exception {
        Map<String, String> mappings = new HashMap<>();
        for (Relocation relocation : relocations) {
            mappings.put(relocation.getPattern(), relocation.getRelocatedPattern());
        }

        // create and invoke a new relocator
        Object relocator = this.jarRelocatorConstructor.newInstance(input.toFile(), output.toFile(), mappings);
        this.jarRelocatorRunMethod.invoke(relocator);
    }
}
