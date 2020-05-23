package com.up.larp.labeling;

import android.graphics.Bitmap;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for labeling items on photos
 */
public class ImageLabeler {
    /**
     * @param imageBitmap Photo served as Bitmap
     * @param labelCallback interface informing us about image labeling process (fail or success)
     */
    public void label(Bitmap imageBitmap, LabelCallback labelCallback) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                .getOnDeviceImageLabeler();

        labeler.processImage(image)
                .addOnSuccessListener(labels -> {
                    List labelList = new ArrayList();

                    for (FirebaseVisionImageLabel label : labels) {
                        labelList.add(label.getText());
                    }

                    labelCallback.onLabelsReady(labelList);

                })
                .addOnFailureListener(e -> labelCallback.onLabelsReady(new ArrayList<>()));
    }

    public interface LabelCallback {
        void onLabelsReady(List<String> labels);
    }

}
