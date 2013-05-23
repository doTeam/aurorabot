package ms.aurora.core.script;

import ms.aurora.api.plugin.Plugin;
import ms.aurora.api.plugin.PluginManifest;
import ms.aurora.api.plugin.internal.InterfacePlugin;
import ms.aurora.api.plugin.internal.PaintDebug;
import ms.aurora.api.plugin.internal.TileUtilities;
import ms.aurora.api.random.Random;
import ms.aurora.api.random.RandomManifest;
import ms.aurora.api.script.Script;
import ms.aurora.api.script.ScriptManifest;
import ms.aurora.core.model.Source;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A jar loader for all scriptable entities supported by Aurora.
 *
 * @author rvbiljouw
 */
public final class EntityLoader {
    private static final Logger logger = Logger.getLogger(EntityLoader.class);
    private static final List<Class<? extends Random>> randoms = new ArrayList<>();
    private static final List<Class<? extends Plugin>> plugins = new ArrayList<>();
    private static final List<Class<? extends Script>> scripts = new ArrayList<>();

    public static void clear() {
        randoms.clear();
        plugins.clear();
        scripts.clear();

        logger.info("Entities cleared.");
        plugins.add(PaintDebug.class);
        plugins.add(TileUtilities.class);
        plugins.add(InterfacePlugin.class);
        logger.info("Default plugins added.");
    }

    public static void load() {
        List<Source> allSources = Source.getAll();
        for (Source source : allSources) {
            logger.info("Scanning source " + source.getSource());
            traverse(new File(source.getSource()));
        }
    }

    public static void reload() {
        clear();
        load();
    }

    private static void traverse(File root) {
        File[] files = root.listFiles();
        for (File file : files != null ? files : new File[0]) {
            if (file.isDirectory()) {
                traverse(file);
            } else if (!file.getName().endsWith(".jar")) {
                continue;
            }

            try {
                loadJar(file);
            } catch (Exception e) {
                logger.error("Failed to load jar " + file.getName(), e);
            }
        }
    }

    private static void loadJar(File rawFile) throws Exception {
        JarFile file = new JarFile(rawFile);
        URL[] classpathURLs = new URL[]{rawFile.toURI().toURL()};
        ClassLoader loader = new URLClassLoader(classpathURLs);

        Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            JarEntry current = entries.nextElement();
            String name = current.getName();

            if (name.endsWith(".class")) {
                String strip = formatClassName(name);
                loadClass(loader.loadClass(strip));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadClass(Class<?> clazz) throws Exception {
        if (!hasAnnotation(clazz)) {
            logger.info("Class " + clazz.getSimpleName() +
                    " has no annotation and was ignored.");
        } else if (Script.class.isAssignableFrom(clazz)) {
            scripts.add((Class<? extends Script>)clazz);
        } else if (Random.class.isAssignableFrom(clazz)) {
            randoms.add((Class<? extends Random>)clazz);
        } else if (Plugin.class.isAssignableFrom(clazz)) {
            plugins.add((Class<? extends Plugin>)clazz);
        }
    }

    private static boolean hasAnnotation(Class<?> clazz) {
        return clazz.getAnnotation(ScriptManifest.class) != null ||
                clazz.getAnnotation(RandomManifest.class) != null ||
                clazz.getAnnotation(PluginManifest.class) != null;
    }

    private static String formatClassName(String name) {
        return name.replaceAll("/", "\\.").replace(".class", "");
    }

    public static List<Class<? extends Script>> getScripts() {
        return scripts;
    }

    public static List<Class<? extends Plugin>> getPlugins() {
        return plugins;
    }

    public static List<Class<? extends Random>> getRandoms() {
        return randoms;
    }

    static {
        clear();
        load();
    }
}