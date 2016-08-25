package com.linroid.plugin;

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class LayoutFileHelper {
    private static final String LAYOUT_PREFIX = "@layout/";

    /**
     * Get Layout name, such as R.layout.name
     * @param file
     * @return
     */
    static String getLayoutName(File file) {
        String fileName = file.name;
        final int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }

    /**
     * Get Layout name, such as R.layout.name
     * @param name reference layout name
     * @return
     */
    static String getLayoutName(String name) {
        final int dot = name.lastIndexOf('/');
        return dot < 0 ? name : name.substring(dot, name.length());
    }

    /**
     * Get the android qualifier directory name;
     * @param file layout file
     * @return
     */
    static String getQualifierName(File file) {
        return file.parentFile.name;
    }
}
