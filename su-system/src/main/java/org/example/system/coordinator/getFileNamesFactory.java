package org.example.system.coordinator;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class getFileNamesFactory {


    public static List<String> getFileName() throws FileNotFoundException {
        List<String> fileNames = new ArrayList<>();

        File folder = new File(ResourceUtils.getURL("classpath:").getPath()+"src/main/resources");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().contains(".bpmn")) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }
}
