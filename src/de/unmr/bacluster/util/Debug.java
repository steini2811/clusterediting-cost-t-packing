package de.unmr.bacluster.util;

public class Debug {
    private static boolean isDebug = false;
    private static boolean isEclipseDebug = false;
    private static String filename = null;

    public static String getFileName() {
        if (filename == null) {
            throw new RuntimeException("No filename given although in eclipse debug mode.");
        }
        return filename;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static boolean isEclipseDebug() {
        return isEclipseDebug;
    }

    public static void log(final Object message) {
        log(message, 0);
    }

    public static void log(final Object message, final int depth) {
        if (isDebug || isEclipseDebug) {
            for (int i = 0; i < depth; i++) {
                System.out.print("  ");
            }
            System.out.println(message.toString());
        }
    }

    public static void setDebug(final boolean isDebug) {
        Debug.isDebug = isDebug;
    }

    public static void setEclipseDebug(final boolean isEclipseDebug) {
        Debug.isEclipseDebug = isEclipseDebug;
    }

    public static void setFileName(final String filename) {
        Debug.filename = filename;
    }
}
