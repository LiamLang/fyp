package com.liamlang.fyp.Utils;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class FileUtils {

    public static void saveToFile(Serializable obj, String path) throws Exception {
        byte[] bytes = Utils.serialize(obj);
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(bytes);
            fos.flush();
        }
    }

    public static Serializable readFromFile(String path) throws Exception {
        RandomAccessFile f = new RandomAccessFile(path, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        return Utils.deserialize(bytes);
    }
}
