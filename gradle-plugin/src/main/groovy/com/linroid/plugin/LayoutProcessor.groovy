package com.linroid.plugin

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class LayoutProcessor {
    private List<File> resources;
    private String packageName;
    private boolean isLibrary
    private Map<String, Map<String, LayoutResource>> mapper
    private List<LayoutResource> layoutResources

    LayoutProcessor(String packageName, List<File> resources, boolean isLibrary) {
        this.resources = resources
        this.packageName = packageName
        this.isLibrary = isLibrary
    }


    def processResources() {
        List<File> resourceFiles = getLayoutFiles(resources)
        layoutResources = new ArrayList<>()
        resourceFiles.each { File file ->
            Node node = new XmlParser().parse(file);

            Log.d("processing %s", file.absolutePath)

            if (!"layout".equals(node.name())) {
                return false;
            }
            Log.d("%s is an AndroidLayoutExtender layout file", file.absolutePath);

            LayoutResource resource = new LayoutResource()
            resource.dir = file.parentFile
            resource.file = file
            resource.layoutName = LayoutFileHelper.getLayoutName(file)
            resource.qualifier = LayoutFileHelper.getQualifierName(file)
            if (node.children().size() == 1 && node.attribute("extends") == null) {
                println "is parent"
            } else {
                String parent = node.attribute("extends");
                if (parent != null) {
                    resource.parentLayout = LayoutFileHelper.getLayoutName(parent);
                }
            }
            resource.rootNode = node;
            resource.sections = parseSections(node);
            this.layoutResources.add(resource);
        }

        /** mapper[layout][qualifier] */
        mapper = new HashMap<>()
        this.layoutResources.each { resource ->
//            println resource
            Map<String, LayoutResource> layoutMapper = this.mapper[resource.layoutName];
            if (layoutMapper == null) {
                layoutMapper = new HashMap<>();
                this.mapper[resource.layoutName] = layoutMapper;
            }
            layoutMapper[resource.qualifier] = resource;
        }
//        List<LayoutBundle> layoutBundles = new ArrayList<>();
//        mapper.each { entity ->
//            Map<String, LayoutResource> layoutMapper = entity.value;
//        }




//        mapper.each { entry ->
//            def list = entry.value;
//            list.each { resource ->
//                if (!mapper.containsKey(resource.parentLayout)) {
//                    throw new IllegalStateException(resource.parentLayout + "not exists! File:" + resource.f.absolutePath);
//                }
//                List<LayoutResource> parents = mapper.get(resource.parentLayout);
//            }
//        }
//        layoutResources.each { resource ->
//            Map<String, LayoutResource> layoutMapper = mapper[resource.layoutName];
//        }
    }

    def Map<String, Node> parseSections(Node node) {
        Map<String, Node> sections = new HashMap<>();
        parseSections(node, sections)
        return sections;
    }

    def parseSections(Node node, Map<String, Node> sections) {
        if (node == null) {
            return;
        }
        if ('section'.equals(node.name())) {
            sections.put(node.attribute('name') as String, node);
        }
        node.children().each { Node child ->
            parseSections(child, sections);
        }
    }

    def writeLayoutInfoFiles(File dir) {
        mapper.each { entity ->
            String layout = entity.value;
            Map<String, LayoutResource> layoutMapper = entity.value;
            layoutMapper.each { qualifierMapper ->
                String qualifier = qualifierMapper.value;
                LayoutResource resource = qualifierMapper.value;
                Node node = traverseNode(resource, null);
            }
        }
    }

    def Node traverseNode(LayoutResource layoutResource, Map<String, Node> sections) {
        /** parent */
        if (layoutResources.parentLayout == null) {
            Node node = layoutResources.rootNode.clone() as Node;
            Map<String, Node> nodeSections = parseSections(node);
            sections.each { entity ->
                replaceNodeWithChildren(nodeSections[entity.key], entity.value);
            }
            return node;
        }
        /** child */
        Map<String, LayoutResource> parentMapper = mapper[layoutResource.parentLayout];
        LayoutResource parent;
        if (parentMapper.containsKey(layoutResource.qualifier)) {
            parent = parentMapper[layoutResource.qualifier];
        } else if (parentMapper.size() == 1) {
            parent = parentMapper.entrySet().getAt(0).value;
        }
        if (parent == null) {
            throw new IllegalStateException("null parent!")
        }

        if (sections == null) {
            sections = new HashMap<>();
            sections.putAll(layoutResource.sections);
        } else {
            layoutResource.sctions.each { entity ->
                if (!sections.containsKey(entity.key)) {
                    sections[entity.key] = entity.value;
                }
            }
        }
        return traverseNode(parent, sections);
    }

    private static void replaceNodeWithChildren(Node source, Node target) {
        Node parent = source.parent();
        def index = parent.children().indexOf(source);
        parent.remove(source)
        parent.children().addAll(index, target.children())
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
