/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BergStudio
 */
public class CraftFile {

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    public static void copyFile(String oldPath, String newPath) {
        int bytesum = 0;
        int byteread = 0;
        File oldfile = new File(oldPath);
        if (oldfile.exists()) {
            InputStream inStream = null;
            try {
                inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            } catch (IOException ex) {
                Logger.getLogger(CraftFile.class.getName()).log(Level.SEVERE, "Cannot copy file", ex);
            } finally {
                try {
                    inStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(CraftFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (String file1 : file) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file1);
                } else {
                    temp = new File(oldPath + File.separator + file1);
                }
                if (temp.isFile()) {
                    try (FileInputStream input = new FileInputStream(temp)) {
                        FileOutputStream output = new FileOutputStream(newPath + "/"
                                + (temp.getName()));
                        byte[] b = new byte[1024 * 5];
                        int len;
                        while ((len = input.read(b)) != -1) {
                            output.write(b, 0, len);
                        }
                        output.flush();
                        output.close();
                    }
                }
                if (temp.isDirectory()) {
                    //如果是子文件夹
                    copyFolder(oldPath + "/" + file1, newPath + "/" + file1);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CraftFile.class.getName()).log(Level.SEVERE, "Cannot copy folder", ex);

        }

    }

    public static void copyFolder(File from, File to) {
        try {
            String[] file = from.list();
            File temp = null;
            for (String file1 : file) {
                if (from.getPath().endsWith(File.separator)) {
                    temp = new File(from + file1);
                } else {
                    temp = new File(from + File.separator + file1);
                }
                if (temp.isFile()) {
                    try (FileInputStream input = new FileInputStream(temp); FileOutputStream output = new FileOutputStream(to.getPath() + "/"
                            + (temp.getName()))) {
                        byte[] b = new byte[1024 * 5];
                        int len;
                        while ((len = input.read(b)) != -1) {
                            output.write(b, 0, len);
                        }
                        output.flush();
                    }
                }
                if (temp.isDirectory()) {
                    //如果是子文件夹
                    copyFolder(from + "/" + file1, to + "/" + file1);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CraftFile.class.getName()).log(Level.SEVERE, "Cannot copy folder", ex);
        }
    }

    public static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }
}
