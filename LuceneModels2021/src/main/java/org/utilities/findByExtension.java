package org.utilities;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class findByExtension {

    public static List<File> getFilesByEndingValue(File rootFolder, final String extension) {
        List<File> result = Lists.newArrayList();

        if (rootFolder.isDirectory())
            result.addAll(Arrays.asList(rootFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File f = new File(dir.getAbsolutePath() + "/" + filename);
                    return filename.toLowerCase().endsWith(extension) && !f.isDirectory();
                }
            })));
        List<File> listFolder = Arrays.asList(rootFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                File f = new File(dir.getAbsolutePath() + File.separator + filename);
                return f.isDirectory();
            }
        }));

        for (File file : listFolder) {
            result.addAll(getFilesByEndingValue(file, extension));
        }
        return result;
    }

}
