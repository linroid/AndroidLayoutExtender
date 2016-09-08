package com.linroid.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author linroid <linroid@gmail.com>
 * @since 8/24/16
 */
class MergeLayoutsTask extends DefaultTask {
    def File outputDir;
    def File inputDir;

    @TaskAction
    void mergeLayouts() throws IOException {
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
            from inputDir
            into outputDir

            Log.d("copy %s to %s", inputDir.absolutePath, outputDir.absolutePath);
        }
    }

}
