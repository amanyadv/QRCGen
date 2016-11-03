package com.qrcg.aman;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;

/**
 * Created by aman on 11/2/16.
 */
public class QRGenerator {
    public static final String LOG_TAG = "QRGenerator";

    public static void generateQrCode(String qr_content, String image_save_path, int minSize) throws Exception {
        if (qr_content.length() < 1) throw new Exception("QR code content is empty String");
        if (image_save_path == null || (image_save_path.length() < 1) || FileCreator.getOrCreateFile(image_save_path) == null) {
            throw new Exception("Invalid save path");
        }

        try {
            //Encode with a QR Code image
            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qr_content,
                    null,
                    Contents.Type.TEXT,
                    BarcodeFormat.QR_CODE.toString(),
                    minSize);
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileCreator.getOrCreateFile(image_save_path));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception : " + e);
        }

    }
}
