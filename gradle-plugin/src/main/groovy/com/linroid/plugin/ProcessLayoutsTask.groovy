package com.linroid.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.xml.sax.SAXException

import javax.xml.bind.JAXBException
import javax.xml.parsers.ParserConfigurationException
import javax.xml.xpath.XPathExpressionException

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class ProcessLayoutsTask extends DefaultTask {
    def LayoutProcessor layoutProcessor;
    def File sdkDir;
    def File xmlOutFolder;
    def int minSdk;

    @TaskAction
    void processResources()
            throws ParserConfigurationException, SAXException, XPathExpressionException,
                    IOException {
        Log.d("running process layouts task %s", getName());
        layoutProcessor.processResources();
    }

    void writeLayoutXmls() throws JAXBException {
        layoutProcessor.writeLayoutInfoFiles(xmlOutFolder);
    }

}
