package com.linroid.plugin;

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/25/16
 */
public class LayoutBundle {
//    LayoutBundle child;
    String layoutName;
    String qualifier;
    Map<String, Node> sections;
    LayoutResource rootResource;
    File outputDir;

    @Override
    protected LayoutBundle clone() {
        LayoutBundle bundle = new LayoutBundle();
        bundle.layoutName = layoutName;
        bundle.qualifier = qualifier;
        bundle.sections = sections;
        bundle.rootResource = rootResource;
        bundle.outputDir = outputDir;
        return bundle;
    }
}
