package com.up.larp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.up.larp.qr.QrScanner;
import com.up.larp.json.JsonParse;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final JsonParse janiewiem = new JsonParse();

        new Thread(new Runnable() {
            @Override
            public void run() {
                janiewiem.fetchJson();
            }
        }).start();




        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission is not granted", Toast.LENGTH_SHORT).show();

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(getApplicationContext(), "Toast 2", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                Toast.makeText(getApplicationContext(), "Request", Toast.LENGTH_SHORT).show();

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                Toast.makeText(getApplicationContext(), "Request permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                Toast.makeText(getApplicationContext(), "Request", Toast.LENGTH_SHORT).show();

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permissions already granted", Toast.LENGTH_SHORT).show();
        }


        FloatingActionButton fab = findViewById(R.id.fabCamera);
        FloatingActionButton qrFab = findViewById(R.id.fabQr);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
            }
        });

        qrFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(REQUEST_QR_SCAN);
            }
        });
    }

    private void dispatchTakePictureIntent(int request) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, request);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = findViewById(R.id.img);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getOnDeviceImageLabeler();

            labeler.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<
                                FirebaseVisionImageLabel> labels) {
                            Toast.makeText(getApplicationContext(), "Działa", Toast.LENGTH_SHORT).show();
                            for (FirebaseVisionImageLabel label : labels) {
                                String text = label.getText();
                                String entityId = label.getEntityId();
                                float confidence = label.getConfidence();
                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Nie działa", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (requestCode == REQUEST_QR_SCAN && resultCode == RESULT_OK) {
            parseQr((Bitmap) data.getExtras().get("data"));
        }
    }

    private void parseQr(Bitmap image) {
        FirebaseVisionImage qr = FirebaseVisionImage.fromBitmap(image);
        QrScanner qrScanner = new QrScanner();
        qrScanner.parse(qr, new QrScanner.ResultCallback() {
            @Override
            public void onComplete(String url) {
                // load barcode url
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(MainActivity.this, "Failed to read qr", Toast.LENGTH_SHORT).show();
            }
        });

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_QR_SCAN = 2;
}
