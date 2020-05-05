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


public class GeoProvider {

    private FusedLocationProviderClient fusedLocationClient;

    private Subject<SimpleLocation> locationSubject = PublishSubject.create();

    public GeoProvider(Activity activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        
        Observable.interval(20, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        if (location != null) {
                                            locationSubject.onNext(new SimpleLocation(location.getLatitude(), location.getLongitude()));
                                        }
                                    }
                                });
                    }
                });

    }

    public Observable<SimpleLocation> subscribe() {
        return locationSubject;
    }
}
