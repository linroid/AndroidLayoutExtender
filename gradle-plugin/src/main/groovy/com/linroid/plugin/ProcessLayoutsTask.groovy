package com.linroid.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException
import javax.xml.xpath.XPathExpressionException

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class ProcessLayoutsTask extends DefaultTask {
    def LayoutProcessor layoutProcessor;
//    def File xmlOutFolder;
    def int minSdk;
    def File generateDir;
    def File mergeDir;

    @TaskAction
    void processResources()
            throws ParserConfigurationException, SAXException, XPathExpressionException,
                    IOException {
        Log.d("running process layouts task %s", getName());
        layoutProcessor.processResources();
        generateLayouts();
        mergeLayouts();
    }

    void generateLayouts() throws Exception {
        layoutProcessor.generateLayoutFiles(generateDir);
    }

    void mergeLayouts() throws Exception {
        Log.d("running merge layouts task %s", getName());
//        inputDir.listFiles().each { file ->
//            project.copy {
//                from file
//                into outputDir
//
//                Log.d("copy %s to %s", file.absolutePath, outputDir.absolutePath);
//            }
//        };
        project.copy {
            from generateDir
            into mergeDir
            Log.d("copy %s to %s", generateDir.absolutePath, mergeDir.absolutePath);
        }
    }
}
