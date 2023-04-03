package com.miras.cclearner.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ServiceUtils {

    public static String splitString(String value) {
        String[] args = value.split(" --> ", 2);
        return args[1];
    }

    public static String getFormattedDate() {
        SimpleDateFormat formattedDate = new SimpleDateFormat("dd-MM-yyyy");
        return formattedDate.format(new Date());
    }

    public static String getFormattedDateWithTime() {
        SimpleDateFormat formattedDate = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        return formattedDate.format(new Date());
    }

    public static String getRequestName(String name){
        if(name.contains("-->")){
            return splitString(name);
        }
        return name;
    }

    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            assert children != null;
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
