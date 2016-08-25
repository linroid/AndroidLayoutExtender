package com.linroid.plugin;

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class LayoutBundle {
    Node rootNode;
    Map<String, Node> sections;
    LayoutBundle parent;

    File resDir;
    LayoutBundle() {
    }
}
