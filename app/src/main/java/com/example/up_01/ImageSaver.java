package com.example.up_01;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ImageSaver {


    public static String generateImageFileName(String root, Bitmap.CompressFormat format){
       return root + "/image-" + new Date()+ "." + ((format == Bitmap.CompressFormat.PNG) ? "png" : "jpeg");
        //тернарная операция?(заменяет if & else)
        //png подставляется если левая часть кода до знака "?" прадива
        //jpeg подставляется если левая часть до "?" ложна
        // схема тернарной операции -- 1) уловие 2)подставляется при правдивости условия
        // 3)значение при ложном условии
    }
    public static void saveImage(Bitmap image, Bitmap.CompressFormat format, int quality,
                                 String outputFileName) throws IOException {
        File f = new File(outputFileName);
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);


        image.compress(format, quality, fos);

        fos.flush();
        fos.close();


    }
}
