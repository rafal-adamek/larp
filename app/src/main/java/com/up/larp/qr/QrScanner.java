package com.up.larp.qr;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;

public class QrScanner {

    FirebaseVisionBarcodeDetectorOptions options;

    public QrScanner() {
        initFirebaseScanner();
    }

    private void initFirebaseScanner() {
        options = new FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(
                FirebaseVisionBarcode.FORMAT_QR_CODE
        ).build();
    }

    public void parse(FirebaseVisionImage visionImage, final ResultCallback resultCallback) {
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(visionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        for (FirebaseVisionBarcode barcode : barcodes) {
                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            switch (valueType) {
                                case FirebaseVisionBarcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    resultCallback.onComplete(url);
                                    break;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        resultCallback.onFailed("Bummer");
                    }
                });
    }

    public interface ResultCallback {
        void onComplete(String url);

        void onFailed(String error);
    }
}
