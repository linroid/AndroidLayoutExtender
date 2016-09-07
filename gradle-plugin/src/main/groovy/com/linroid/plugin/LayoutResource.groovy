package com.linroid.plugin;

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class LayoutResource {
    File dir;
    File file;
    /** layout name **/
    String layoutName;
    String parentLayout;
    String qualifier;
    Node rootNode;
    Map<String, Node> sections
    LayoutBundle bundle;

    LayoutResource() {
    }

    @Override
    public String toString() {
        return "LayoutResource{" +
                "dir=" + dir +
                ", file=" + file +
                ", layoutName='" + layoutName + '\'' +
                ", parentLayout='" + parentLayout + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", rootNode=" + rootNode +
                ", sections=" + sections +
                '}';
    }
}
