package com.garzoopvt.garzoo.Util;

import android.os.Build;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveFile {

    public static void downloadFile(URL url, String fileName) throws Exception {
        try (InputStream in = url.openStream()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.copy(in, Paths.get(fileName));
            }
        }
    }
//    public static void main(String[] args) throws Exception {
//        downloadFile(new URL("https://homepages.cae.wisc.edu/~ece533/images/airplane.png"), "D:\\garzoo\\test.png");
//    }
}

