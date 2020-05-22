package com.up.larp.geo;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

/**
 * Class responsible for tracking user's location.
 */
public class GeoProvider {

    private FusedLocationProviderClient fusedLocationClient;

    private Subject<SimpleLocation> locationSubject = PublishSubject.create();

    /**
     * Constructor
     * @param activity to attach lifecycle
     */
    public GeoProvider(Activity activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        
        Observable.interval(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(aLong -> fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                locationSubject.onNext(new SimpleLocation(location.getLatitude(), location.getLongitude()));
                            }
                        }));
    }

    /**
     * Method used to subscribe for user's location.
     *
     * @return observable with user's location
     */
    public Observable<SimpleLocation> subscribe() {
        return locationSubject;
    }
}
