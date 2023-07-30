package com.aliens.friendship.global.util;

public class ImageUtil {

    public static String getImageExtension(String imageFileName) {
        return imageFileName.substring(imageFileName.lastIndexOf(".") + 1);
    }
}