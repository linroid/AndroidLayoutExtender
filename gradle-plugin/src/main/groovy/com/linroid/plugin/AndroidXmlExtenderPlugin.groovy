package com.linroid.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.api.TestVariant
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.api.LibraryVariantImpl
import com.android.build.gradle.internal.api.TestVariantImpl
import com.android.build.gradle.internal.core.GradleVariantConfiguration
import com.android.build.gradle.internal.variant.ApplicationVariantData
import com.android.build.gradle.internal.variant.BaseVariantData
import com.android.build.gradle.internal.variant.LibraryVariantData
import com.android.build.gradle.internal.variant.TestVariantData
import com.android.build.gradle.tasks.ProcessAndroidResources
import com.android.builder.model.ApiVersion
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import java.lang.reflect.Field

/**
 * @author linroid <linroid@gmail.com>
 * @since 2016/08/24
 */
class AndroidXmlExtenderPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        Log.setupLogger(project.logger);
        project.afterEvaluate() { Project pro ->
            try {
                createXmlProcessor(pro);
            } catch (Throwable t) {
                Log.e(t, "failed to setup data binding");
            }
        }
    }

    private void createXmlProcessor(Project project)
            throws NoSuchFieldException, IllegalAccessException {
        Log.d("creating xml processor for " + project);
        Object androidExt = project.extensions.getByName("android");
        if (!(androidExt instanceof BaseExtension)) {
            return;
        }
        if (androidExt instanceof AppExtension) {
            createXmlProcessorForApp(project, (AppExtension) androidExt);
        } else if (androidExt instanceof LibraryExtension) {
            createXmlProcessorForLibrary(project, (LibraryExtension) androidExt);
        } else {
            Log.e(new UnsupportedOperationException("cannot understand android ext"),
                    "unsupported android extension. What is it? %s", androidExt);
        }
    }

    private void createXmlProcessorForLibrary(Project project, LibraryExtension lib)
            throws NoSuchFieldException, IllegalAccessException {
        File sdkDir = lib.sdkDirectory;
        Log.d("create xml processor for " + lib);
        lib.testVariants.each { TestVariant variant ->
            Log.d("test variant %s. dir name %s", variant, variant.dirName);
            BaseVariantData variantData = getVariantData(variant);
            attachXmlProcessor(project, variantData, sdkDir, false);//tests extend apk variant
        }
        lib.libraryVariants.each { LibraryVariant variant ->
            Log.d("library variant %s. dir name %s", variant, variant.dirName);
            BaseVariantData variantData = getVariantData(variant);
            attachXmlProcessor(project, variantData, sdkDir, true);
        }
    }

    private void createXmlProcessorForApp(Project project, AppExtension appExt)
            throws NoSuchFieldException, IllegalAccessException {
        Log.d("create xml processor for " + appExt);
        File sdkDir = appExt.sdkDirectory;
        for (TestVariant testVariant : appExt.testVariants) {
            TestVariantData variantData = getVariantData(testVariant);
            attachXmlProcessor(project, variantData, sdkDir, false);
        }
        for (ApplicationVariant appVariant : appExt.applicationVariants) {
            ApplicationVariantData variantData = getVariantData(appVariant);
            attachXmlProcessor(project, variantData, sdkDir, false);
        }
    }

    private LibraryVariantData getVariantData(LibraryVariant variant)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = LibraryVariantImpl.class.getDeclaredField("variantData");
        field.setAccessible(true);
        return (LibraryVariantData) field.get(variant);
    }

    private TestVariantData getVariantData(TestVariant variant)
            throws IllegalAccessException, NoSuchFieldException {
        Field field = TestVariantImpl.class.getDeclaredField("variantData");
        field.setAccessible(true);
        return (TestVariantData) field.get(variant);
    }

    private ApplicationVariantData getVariantData(ApplicationVariant variant)
            throws IllegalAccessException, NoSuchFieldException {
        Field field = ApplicationVariantImpl.class.getDeclaredField("variantData");
        field.setAccessible(true);
        return (ApplicationVariantData) field.get(variant);
    }

    private void attachXmlProcessor(Project project, final BaseVariantData variantData,
                                    final File sdkDir,
                                    final Boolean isLibrary) {
        final GradleVariantConfiguration configuration = variantData.variantConfiguration;
        final ApiVersion minSdkVersion = configuration.minSdkVersion;
        ProcessAndroidResources generateRTask = variantData.generateRClassTask;
        final String packageName = generateRTask.packageForR;
        String fullName = configuration.fullName;
        List<File> resourceFolders = Arrays.asList(variantData.mergeResourcesTask.outputDir);

        FileWriter fileWriter = new FileWriter();
        final LayoutProcessor processor = new LayoutProcessor(packageName, resourceFolders, isLibrary, fileWriter);
        final ProcessAndroidResources processResTask = generateRTask;
        final File xmlOutDir = new File(project.buildDir.absolutePath + "/layout-extender-generated/" + configuration.dirName);
        Log.d("xml output for %s is %s", variantData, xmlOutDir);
        String layoutTaskName = "generateExtenderLayouts" + StringUtils.capitalize(processResTask.name);

        project.tasks.create(layoutTaskName,
                ProcessLayoutsTask.class,
                new Action<ProcessLayoutsTask>() {
                    @Override
                    void execute(final ProcessLayoutsTask task) {
                        task.layoutProcessor = processor;
                        task.xmlOutFolder = xmlOutDir;
                        task.minSdk = minSdkVersion.apiLevel;

                        Log.d("TASK adding dependency on %s for %s", task, processResTask);
                        processResTask.dependsOn(task);
                        processResTask.getInputs().dir(xmlOutDir);
                        for (Object dep : processResTask.dependsOn) {
                            if (dep == task) {
                                continue;
                            }
                            Log.d("adding dependency on %s for %s", dep, task);
                            task.dependsOn(dep);
                        }
                        processResTask.doLast(new Action<Task>() {
                            @Override
                            void execute(Task unused) {
                                Log.d("now, output generated layout xml file");
                                try {
                                    task.writeLayoutXmls();
                                } catch (Exception e) {
                                    // gradle sometimes fails to resolve JAXBException.
                                    // We get stack trace manually to ensure we have the log
                                    Log.e(e, "cannot write layout xmls %s", ExceptionUtils.getStackTrace(e));
                                }
                            }
                        });
                    }
                });
    }
}
