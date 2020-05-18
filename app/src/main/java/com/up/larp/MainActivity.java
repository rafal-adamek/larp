package com.up.larp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.up.larp.gaming.Game;
import com.up.larp.geo.GeoProvider;
import com.up.larp.json.LarpJsonParser;
import com.up.larp.json.LarpObject;
import com.up.larp.labeling.ImageLabeler;
import com.up.larp.leaderboard.FirebaseUserRepository;
import com.up.larp.leaderboard.User;
import com.up.larp.qr.QrScanner;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageLabeler.LabelCallback, LarpJsonParser.JsonResultCallback, Game.GameCallback, FirebaseUserRepository.Callback {

    private Game game;

    FloatingActionButton fab;
    FloatingActionButton qrFab;

    private final FirebaseUserRepository userRepository = new FirebaseUserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        fab = findViewById(R.id.fabCamera);
        qrFab = findViewById(R.id.fabQr);


        fab.setOnClickListener(v -> dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE));

        qrFab.setOnClickListener(v -> dispatchTakePictureIntent(REQUEST_QR_SCAN));

        GeoProvider geoProvider = new GeoProvider(this);

        game = new Game(this);
        game.attachGeoProvider(geoProvider.subscribe());

        promptUsername();

        userRepository.attachCallback(this);
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
            ImageLabeler imageLabeler = new ImageLabeler();
            imageLabeler.label(imageBitmap, this);
        } else if (requestCode == REQUEST_QR_SCAN && resultCode == RESULT_OK) {
            parseQr((Bitmap) data.getExtras().get("data"));
        }
    }

    @Override
    public void onLabelsReady(List<String> labels) {
        runOnUiThread(() -> {
            Toast.makeText(this, labels.toString(), Toast.LENGTH_LONG).show();
            game.verifyLabels(labels);
        });
    }

    @Override
    public void onJsonObjectReceived(List<LarpObject> larpObjects) {
        runOnUiThread(() -> {
            game.setLarpObjects(larpObjects);
            fab.setVisibility(View.VISIBLE);
            qrFab.setVisibility(View.GONE);
            if (larpObjects.size() > 0) Toast.makeText(this, "Objects fetched", Toast.LENGTH_LONG).show();
        });
    }

    private void parseQr(Bitmap image) {
        FirebaseVisionImage qr = FirebaseVisionImage.fromBitmap(image);
        QrScanner qrScanner = new QrScanner();
        qrScanner.parse(qr, new QrScanner.ResultCallback() {
            @Override
            public void onComplete(String url) {
                LarpJsonParser larpJsonParser = new LarpJsonParser();

                larpJsonParser.fetchJson(url, MainActivity.this);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(MainActivity.this, "Failed to read qr", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onGameFinished(String username, int points) {
        TextView textView = findViewById(R.id.endgame);
        textView.setText("You win, " + username);
        fab.setVisibility(View.GONE);
//        qrFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNextLevel(Game game) {
        TextView name = findViewById(R.id.name);
        TextView points = findViewById(R.id.points);
        TextView progress = findViewById(R.id.progress);
        TextView labels = findViewById(R.id.labels);

        name.setText(game.getUsername());
        points.setText("Points: " + game.getPoints());
        progress.setText("Progress: " + game.getProgress() + " / " + game.getLarpObjects().size());
        labels.setText("Label: " + game.getLarpObjects().get(game.getProgress() - 1).getLabel());
    }

    @Override
    public void onFailedScan(String hint) {
        TextView endgame = findViewById(R.id.endgame);
        endgame.setText(hint);
    }

    @Override
    public void usersFetched(List<User> users) {

    }

    @Override
    public void userUpdated() {
    }

    @Override
    public void userFetched(User user) {

    }

    @Override
    public void requestFailed(String message) {

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_QR_SCAN = 2;

    private void promptUsername() {
        EditText et = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Set username")
                .setView(et)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    game.setUsername(et.getText().toString().isEmpty() ? "Unknown" : et.getText().toString());

                    TextView name = findViewById(R.id.name);
                    name.setText(game.getUsername());

                    userRepository.getUsers();
                }).show();
    }
}
