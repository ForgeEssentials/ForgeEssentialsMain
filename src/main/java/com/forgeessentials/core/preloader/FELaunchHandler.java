package com.forgeessentials.core.preloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;

public class FELaunchHandler implements ITweaker
{

    protected static final Logger launchLog = LogManager.getLogger("ForgeEssentials");

    public static final String FE_DIRECTORY = "ForgeEssentials";

    public static final String FE_LIB_VERSION = "4";

    public static final FilenameFilter JAR_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name)
        {
            return name.endsWith(".jar");
        }
    };

    /* ------------------------------------------------------------ */

    private static File gameDirectory;

    private static File feDirectory;

    private static File libDirectory;

    private static File moduleDirectory;

    private static File jarLocation;

    /* ------------------------------------------------------------ */

    @Override
    public String getLaunchTarget()
    {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments()
    {
        return new String[] {};
    }

    @Override
    @SuppressWarnings({ "unchecked", "deprecation" })
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        // initialize mixin, if someone hasn't already done it for us
        ArrayList<String> tweaks = (ArrayList<String>) Launch.blackboard.get("TweakClasses");
        if (!tweaks.contains("org.spongepowered.asm.launch.MixinTweaker"))
        {
            tweaks.add("org.spongepowered.asm.launch.MixinTweaker");
        }

        MixinBootstrap.init();
        MixinEnvironment.setCompatibilityLevel(CompatibilityLevel.JAVA_8);

        try {
            URI uri = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            MixinBootstrap.getPlatform().addContainer(new ContainerHandleURI(uri));
        } catch (URISyntaxException e) {
            launchLog.error(e);
        }

        // Fix CoFH compatibility. Fixes #1903
        MixinEnvironment.getEnvironment(Phase.PREINIT).addTransformerExclusion("cofh.asm.CoFHAccessTransformer");

        // Enable FastCraft compatibility mode
        System.setProperty("fastcraft.asm.permissive", "true");

        // Setup directories
        gameDirectory = gameDir;

        feDirectory = new File(gameDir, FE_DIRECTORY);
        feDirectory.mkdirs();

        moduleDirectory = new File(feDirectory, "modules");
        moduleDirectory.mkdirs();

        libDirectory = new File(feDirectory, "lib");
        URI uri = null;
        try
        {
            uri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            jarLocation = new File(uri);
        }
        catch (URISyntaxException ex)
        {
            launchLog.error("Could not get JAR location for {}", uri);
            ex.printStackTrace();
        }
        catch (Exception ex) {
            launchLog.error("Unknown error attempting to get JAR location at {}",uri.getPath(), ex);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        if (shouldExtractLibraries())
            extractLibraries();
        loadLibraries(classLoader);
        loadModules(classLoader);
        classLoader.registerTransformer(EventTransformer.class.getName());
    }

    /* ------------------------------------------------------------ */

    public boolean shouldExtractLibraries()
    {
        // boolean runtimeDeobfEnabled = (!(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));
        if (!libDirectory.exists())
            return true;
        try
        {
            File versionFile = new File(libDirectory, "version.txt");
            String version = FileUtils.readFileToString(versionFile);
            return !FE_LIB_VERSION.equals(version);
        }
        catch (IOException e)
        {
            return true;
        }
    }

    public void extractLibraries()
    {
        try
        {
            try
            {
                FileUtils.deleteDirectory(libDirectory);
                libDirectory.mkdirs();
            } catch (IOException e)
            {
                launchLog.error("Unable to delete old libs directory! Will attempt to clean and extract files anyway.");

                FileUtils.cleanDirectory(libDirectory);
            }
            // TODO Check for other stuff like WorldEdit!

            InputStream libArchive = getClass().getResourceAsStream("/libraries.zip");
            if (libArchive == null)
            {
                launchLog.warn("Could not find libraries.zip. Running in dev env?");
                return;
            }

            launchLog.info("Extracting libraries");
            try (ZipInputStream zIn = new ZipInputStream(libArchive))
            {
                ZipEntry zEntry;
                while ((zEntry = zIn.getNextEntry()) != null)
                {
                    File file = new File(gameDirectory, zEntry.getName());
                    if (zEntry.isDirectory())
                    {
                        file.mkdirs();
                    }
                    else
                    {
                        file.getParentFile().mkdirs();
                        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file)))
                        {
                            IOUtils.copy(zIn, out);
                        }
                    }
                }
            }

            File versionFile = new File(libDirectory, "version.txt");
            try (FileWriter out = new FileWriter(versionFile))
            {
                out.write(FE_LIB_VERSION);
            }
        }
        catch (IOException e)
        {
            launchLog.error("Error extraction libraries!");
            e.printStackTrace();
        }
    }

    /* ------------------------------------------------------------ */

    private boolean checkZipForExclusionsAndFixInvalidClasses(File f, LaunchClassLoader loader) {
        boolean result = false;
        try (ZipFile zipFile = new ZipFile(f))
        {
            Field classLoaderExceptionsF = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            classLoaderExceptionsF.setAccessible(true);
            Set<String> classLoaderExceptions = (Set<String>) classLoaderExceptionsF.get(loader);
            Enumeration entries = zipFile.entries();
            Field invalidClassesF = LaunchClassLoader.class.getDeclaredField("invalidClasses");
            invalidClassesF.setAccessible(true);
            Set<String> invalidClasses = (Set<String>) invalidClassesF.get(loader);

            Field negativeResourceCacheF = LaunchClassLoader.class.getDeclaredField("negativeResourceCache");
            negativeResourceCacheF.setAccessible(true);
            Set<String> negativeResourceCache = (Set<String>) negativeResourceCacheF.get(loader);

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if (!result)
                {
                    for (String exclusion : classLoaderExceptions)
                    {
                        if (entry.getName().startsWith(exclusion.replace(".", "/")))
                        {
                            result = true;
                            break;
                        }
                    }
                }
                String name = entry.getName().replace("/", ".").replace(".class","");
                if (invalidClasses.contains(name)) {
                    invalidClasses.remove(name);
                    negativeResourceCache.remove(name);
                }

            }
        }
        catch (IOException | NoSuchFieldException | IllegalAccessException | ClassCastException e)
        {
            throw new RuntimeException(String.format("[ForgeEssentials] Checking library %s: %s", f.getAbsolutePath(), e.getMessage()));
        }
        return result;
    }
    private void loadLibraries(LaunchClassLoader classLoader)
    {
        File[] files = libDirectory.listFiles(JAR_FILTER);
        if (files == null)
            return;
        for (File f : files)
        {
            try
            {
                if (checkZipForExclusionsAndFixInvalidClasses(f, classLoader))
                {
                    Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    addURL.setAccessible(true);
                    addURL.invoke(classLoader.getClass().getClassLoader(), f.toURI().toURL());
                    launchLog.info(String.format("Added library %s to parent classpath", f.getAbsolutePath()));
                } else {
                    classLoader.addURL(f.toURI().toURL());

                    launchLog.info(String.format("Added library %s to classpath", f.getAbsolutePath()));
                }
            }
            catch (MalformedURLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException(String.format("[ForgeEssentials] Error adding library %s to classpath: %s", f.getAbsolutePath(), e.getMessage()));
            }
        }
    }

    private void loadModules(LaunchClassLoader classLoader)
    {
        for (File f : moduleDirectory.listFiles(JAR_FILTER))
        {
            try
            {
                classLoader.addURL(f.toURI().toURL());
                launchLog.info(String.format("Added module %s to classpath", f.getAbsolutePath()));
            }
            catch (MalformedURLException e)
            {
                throw new RuntimeException(String.format("[ForgeEssentials] Error adding module %s to classpath: %s", f.getAbsolutePath(), e.getMessage()));
            }
        }
    }

    /* ------------------------------------------------------------ */

    public static File getGameDirectory()
    {
        return gameDirectory;
    }

    public static File getJarLocation()
    {
        return jarLocation;
    }

    public static File getFeDirectory()
    {
        return feDirectory;
    }

    public static File getModuleDirectory()
    {
        return moduleDirectory;
    }

}
