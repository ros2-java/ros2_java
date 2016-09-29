package org.ros2.rcljava;

import java.util.Vector;

public abstract class NativeUtils {

    private static java.lang.reflect.Field LIBRARIES;

    static {
        try {
            LIBRARIES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
            LIBRARIES.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static String[] getLoadedLibraries(final ClassLoader loader) {
        Vector<String> libraries = new Vector<String>();

        try {
            libraries = (Vector<String>) LIBRARIES.get(loader);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return libraries.toArray(new String[] {});
    }
}
