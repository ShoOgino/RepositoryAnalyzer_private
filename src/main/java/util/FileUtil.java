package util;

import data.Commit;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {
    public static String readFile(final String path){
        String value=null;
        try {
            value = Files.lines(Paths.get(path), StandardCharsets.UTF_8).collect(Collectors.joining(System.getProperty("line.separator")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
    public static void copyDirectory(final String pathOriginal, String pathCopy){
        File fileOriginal = new File(pathOriginal);
        File fileCopy = new File(pathCopy);
        try {
            FileUtils.deleteDirectory(fileCopy);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            FileUtils.copyDirectory(fileOriginal, fileCopy);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public static void deleteDirectory(String pathRepositoryFileCopy) {
        try {
            File fileRepositoryFileCopy = new File(pathRepositoryFileCopy);
            FileUtils.deleteDirectory(fileRepositoryFileCopy);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static List<String> findPathsFile(String dirRoot, String ext, String subStringToIgnore) {
        List<String> pathsFile = new ArrayList<String>();
        try {
            pathsFile.addAll(
                    Files.walk(Paths.get(dirRoot))
                            .map(Path::toString)
                            .filter(p -> p.endsWith(ext))
                            .filter(p -> !p.contains(subStringToIgnore))
                            .map(p -> p.replace("\\", "/"))
                            .collect(Collectors.toList())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathsFile;
    }
    public static List<String> findPathsFile(String dirRoot, String ext) {
        List<String> pathsFile = new ArrayList<String>();
        try {
            pathsFile.addAll(
                    Files.walk(Paths.get(dirRoot))
                            .map(Path::toString)
                            .filter(p -> p.endsWith(ext))
                            .filter(p -> !p.endsWith("ConcatenationWithStringValueOfCheck.java"))
                            .filter(p -> !p.endsWith("Scope.java"))
                            .filter(p -> !p.endsWith("EmptyBlockCheck.java"))
                            .collect(Collectors.toList())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathsFile;
    }
    public static List<String> findPathsFile(String[] dirsRoot, String ext, String subStringToIgnore) {
        List<String> pathsFile = new ArrayList<String>();
        try {
            for(String dirRoot: dirsRoot) {
                pathsFile.addAll(
                        Files.walk(Paths.get(dirRoot))
                                .map(Path::toString)
                                .filter(p -> p.endsWith(ext))
                                .filter(p -> !p.contains(subStringToIgnore))
                                .collect(Collectors.toList())
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathsFile;
    }

}
