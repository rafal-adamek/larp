package com.up.larp.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.up.larp.MainActivity;

public class PermissionChecker {

    static final int REQUEST__IMAGE_CAPTURE = 1;

    public boolean checkCamera(Activity context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.CAMERA}, REQUEST__IMAGE_CAPTURE);

            return true; // Do poprawy - Komunikat sie wyswietla ale nic nie sprawdza czy na pewno zostala udzielona zgoda, napraw to.
        }


        return false;


    }

}
