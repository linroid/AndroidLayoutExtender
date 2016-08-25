package com.linroid.plugin;

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class LayoutProcessor {
    List<File> resources;
    String packageName;
    boolean isLibrary;

    LayoutProcessor(String packageName, List<File> resources, boolean isLibrary) {
        this.resources = resources
        this.packageName = packageName
        this.isLibrary = isLibrary
    }


    void processResources() {
        List<File> resourceFiles = LayoutProcessor.getLayoutFiles(resources)
        List<LayoutResource> layoutResources = new ArrayList<>()
        List<LayoutBundle> layoutBundles = new ArrayList<>()
        resourceFiles.each { File file ->
            Node node = new XmlParser().parse(file);

            Log.d("processing %s", file.absolutePath)

            if (!"layout".equals(node.name())) {
                return false;
            }
            Log.d("%s is an extendible layout file", file.absolutePath);

            LayoutResource res = new LayoutResource()
            res.dir = file.parentFile
            res.file = file
            res.layout = LayoutFileHelper.getLayoutName(file)
            res.qualifier = LayoutFileHelper.getQualifierName(file)
            if (node.children().size() == 1 && node.attribute("extends") == null) {
                println "is parent"
            } else {
                String parent = node.attribute("extends");
                if (parent != null) {
                    res.parentLayout = LayoutFileHelper.getLayoutName(parent);
                }
            }
            layoutResources.add(res);
        }
        layoutResources.each { layoutResource ->
            println layoutResource
        }
    }

    void writeLayoutInfoFiles(File dir) {

    }

    static List<File> getLayoutFiles(List<File> resources) {
        List<File> result = new ArrayList<>();
        resources.each { File resource ->
            if (!resource.exists() || !resource.canRead()) {
                return false;
            }
            if (resource.isDirectory()) {
                resource.listFiles(layoutFolderFilter).each { File layoutFolder ->
                    layoutFolder.listFiles(xmlFileFilter).each { File xmlFile ->
                        result.add(xmlFile);
                    }
                }
            } else if (xmlFileFilter.accept(resource.parentFile, resource.name)) {
                result.add(resource);
            }
        }
        return result;
    }


    private static final FilenameFilter layoutFolderFilter = { File dir, String name ->
        return name.startsWith("layout");
    };

    private static final FilenameFilter xmlFileFilter = { File dir, String name ->
        return name.toLowerCase().endsWith(".xml");
    };
}
