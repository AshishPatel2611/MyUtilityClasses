package com.abc.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class GraphicsUtil {

    Context context;

    private static GraphicsUtil instance;

    private GraphicsUtil() {
    }

    public static GraphicsUtil getInstance() {
        if (instance == null) {
            instance = new GraphicsUtil();
        }
        return instance;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public String saveImage(Bitmap finalBitmap, String rootDir) {
        String timeStamp, imageName;
        File mediaStorageDir = null;
        if (rootDir != null && rootDir.length() > 0) {
            mediaStorageDir = new File(rootDir);
        } else {
            mediaStorageDir = new File(rootDir, "temp");
        }

        // deletFile(mediaStorageDir);
        mediaStorageDir.mkdirs();

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        imageName = "IMG_" + timeStamp + ".jpg";

        File file = new File(mediaStorageDir, imageName);
        if (file.exists()) {
            file.delete();
        }
        try {

            if (finalBitmap != null) {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // FILENAME = file.getName();
        return file.getPath();
    }

    /*Convert Bitmap to ByteArray*/
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method that accept bitmap, apply rotation on bitmap.
     *
     * @param src    - source bitmap.
     * @param degree - at degree bitmap is rotated.
     * @return Bitmap - final processed bitmap.
     */
    public static Bitmap makeRotate(Bitmap src, float degree) {
        // FINAL TESTED
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);

        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}