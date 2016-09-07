package com.linroid.plugin

import groovy.xml.XmlUtil

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class LayoutProcessor {
    private List<File> resources;
    private String packageName;
    private boolean isLibrary;
    private Map<String, Map<String, LayoutResource>> mapper;
    private List<LayoutResource> allLayoutResources;
    private List<LayoutResource> parentLayoutResources;

    LayoutProcessor(String packageName, List<File> resources, boolean isLibrary) {
        this.resources = resources
        this.packageName = packageName
        this.isLibrary = isLibrary
    }

    def processResources() {
        List<File> resourceFiles = getLayoutFiles(resources)
        allLayoutResources = new ArrayList<>()
        parentLayoutResources = new ArrayList<>()
        resourceFiles.each { File file ->
            Node node = new XmlParser().parse(file);

//            Log.d("processing %s", file.absolutePath)

            if (!"layout".equals(node.name())) {
                return false;
            }

            LayoutResource resource = new LayoutResource()
            resource.dir = file.parentFile
            resource.file = file
            resource.layoutName = LayoutFileHelper.getLayoutName(file)
            resource.qualifier = LayoutFileHelper.getQualifierName(file)
            if (node.children().size() == 1 && node.attribute("extends") == null) {
                Log.d("(PARENT)\t\t%s/%s.xml", resource.qualifier, resource.layoutName);
                parentLayoutResources.add(resource);
            } else {
                String parent = node.attribute("extends");
                if (parent != null) {
                    resource.parentLayoutName = LayoutFileHelper.getLayoutName(parent);
                }
                Log.d("(CHILD:%s)\t%s/%s.xml", resource.parentLayoutName, resource.qualifier, resource.layoutName);
            }
            resource.rootNode = node;
            resource.sections = parseSections(node);
            this.allLayoutResources.add(resource);
        }

        /** mapper[layout][qualifier] */
        /** Map<String, Map<String, LayoutResource>>  **/
        mapper = new HashMap<>()
        allLayoutResources.each { resource ->
//            println resource
            Map<String, LayoutResource> layoutMapper = this.mapper[resource.layoutName];
            if (layoutMapper == null) {
                layoutMapper = new HashMap<>();
                this.mapper[resource.layoutName] = layoutMapper;
            }
            layoutMapper[resource.qualifier] = resource;
        }
        allLayoutResources.each { resource ->
            if (resource.parentLayoutName != null) {
                if (!mapper.containsKey(resource.parentLayoutName)) {
                    throw new IllegalStateException("parent not exists: " + resource.parentLayoutName);
                }
                def parentLayouts = mapper.get(resource.parentLayoutName);
                parentLayouts.each { parentEntry ->
                    def parent = parentEntry.value;
                    if (parent.children == null) {
                        parent.children = new ArrayList<>();
                    }
                    parent.children.add(resource);
                }
            }
        }

        /** traverse from parent to child **/
        parentLayoutResources.each { parent ->
            LayoutBundle bundle = new LayoutBundle();
            bundle.layoutName = parent.layoutName;
            bundle.qualifier = parent.qualifier;
            bundle.rootResource = parent;
            traverseGenerateBundle(parent, bundle, false);
        }
    }

    /**
     *
     * @param resource
     * @param bundle
     * @param hasFission parent has fission?
     * @return
     */
    def traverseGenerateBundle(LayoutResource resource, LayoutBundle bundle, boolean hasFission) {
        if (bundle.sections == null) {
            bundle.sections = new HashMap<>();
        }
        bundle.sections.putAll(resource.sections);
        outputBundle(bundle);
        // leaf node
        if (resource.children == null || resource.children.size() == 0) {
            return;
        }
        List<LayoutResource> children = resource.children;
        boolean needFission = false;
        if (mapper.get(resource.layoutName).size() == 1 && children.size() > 1 && !hasFission) {
            needFission = true;
        }

        if (needFission) {
            bundle.layoutName = children.get(0).layoutName;
            children.each { child ->
                def childBundle = bundle.clone();
                childBundle.qualifier = child.qualifier;
                traverseGenerateBundle(child, childBundle, true);
            }
        } else {
            boolean hasChild = false;
            if (children.size() == 1) {
                LayoutResource child = children.get(0);
                bundle.layoutName = child.layoutName;
                traverseGenerateBundle(child, bundle, hasFission);
                hasChild = true;
            } else {
                resource.children.each { child ->
                    if (child.qualifier.equals(bundle.qualifier)) {
                        bundle.layoutName = child.layoutName;
                        traverseGenerateBundle(child, bundle, hasFission);
                        hasChild = true;
                        return true;
                    }
                }
            }
            if (!hasChild) {
                throw new IllegalStateException("not support");
            }
        }
    }

    def outputBundle(LayoutBundle bundle) {
        Node node = new XmlParser().parse(bundle.rootResource.file);
        def sections = parseSections(node);
        sections.each { entity ->
            if (bundle.sections.containsKey(entity.key)) {
                replaceNodeWithChildren(entity.value, bundle.sections[entity.key]);
            } else {
                replaceNodeWithChildren(entity.value, entity.value);
            }
        }
        println bundle.qualifier + "/" + bundle.layoutName + " >>>>";
        println XmlUtil.serialize(node.children().get(0) as Node)
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
//                Node node = traverseNode(resource, null);
            }
        }
    }

    def Node traverseNode(LayoutResource layoutResource, Map<String, Node> sections) {
        /** parent */
        if (layoutResource.parentLayoutName == null) {
            Node node = layoutResource.rootNode.clone() as Node;
            Map<String, Node> nodeSections = parseSections(node);
            sections.each { entity ->
                replaceNodeWithChildren(nodeSections[entity.key], entity.value);
            }
            return node;
        }
        /** child */
        Map<String, LayoutResource> parentMapper = mapper[layoutResource.parentLayoutName];
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
            layoutResource.sections.each { entity ->
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
