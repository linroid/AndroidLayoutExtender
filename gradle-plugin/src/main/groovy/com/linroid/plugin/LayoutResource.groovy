package com.linroid.plugin;

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class LayoutResource {
    File dir;
    File file;
    String layout;
    String parentLayout;
    String qualifier;
    Node node;

    LayoutResource() {
    }

    @Override
    String toString() {
        return "LayoutResource{" +
                "dir=" + dir +
                ", file=" + file +
                ", layout='" + layout + '\'' +
                ", parentLayout='" + parentLayout + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", node=" + node +
                '}';
    }
}
