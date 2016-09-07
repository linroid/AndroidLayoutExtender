package com.linroid.plugin;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author linroid <linroid@gmail.com>
 * @since 9/7/16
 */
public class FileWriter {
    public void writeToFile(File exactPath, String contents) {
        File parent = exactPath.getParentFile();
        parent.mkdirs();
        try {
            Log.d("writing file %s", exactPath.getAbsoluteFile());
            FileUtils.writeStringToFile(exactPath, contents);
        } catch (IOException e) {
            Log.e(e, "Could not write to %s", exactPath);
        }
    }
}
