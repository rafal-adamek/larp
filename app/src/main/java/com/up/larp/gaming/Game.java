package com.up.larp.gaming;

import com.up.larp.geo.SimpleLocation;
import com.up.larp.json.LarpObject;
import io.reactivex.rxjava3.core.Observable;

import java.text.DecimalFormat;
import java.util.List;

public class Game {

    private List<LarpObject> larpObjects;
    private int progress = 0;
    private int points = 0;
    private String username;
    private GameCallback callback;
    private SimpleLocation currentLocation;

    public Game(GameCallback callback) {
        this.callback = callback;
    }

    public void attachGeoProvider(Observable<SimpleLocation> locationObservable) {
        locationObservable.subscribe(location -> this.currentLocation = location);
    }

    public List<LarpObject> getLarpObjects() {
        return larpObjects;
    }

    public void setLarpObjects(List<LarpObject> larpObjects) {
        this.larpObjects = larpObjects;
    }

    public int getProgress() {
        return progress;
    }

    public boolean verifyLabels(List<String> labels) {
        LarpObject larpObject = larpObjects.get(progress);

        DecimalFormat format = new DecimalFormat("#.##");

        if (labels.contains(larpObject.getLabel())) {
            if (format.format(currentLocation.lat).equals(format.format(larpObject.getLat()))) {
                advance();
                return true;
            }
        }

        sendHint(larpObject);

        return false;
    }

    private void sendHint(LarpObject larpObject) {
        String hint = "Wrong object";

        double larpLat = truncate(larpObject.getLat());
        double larpLon = truncate(larpObject.getLon());
        double lat = truncate(currentLocation.lat);
        double lon = truncate(currentLocation.lon);

        if (lat > larpLat) hint = "Go west"; else if(lat < larpLat) hint = "Go east";
        if (lon > larpLon) hint = "Go south"; else if(lon < larpLon) hint = "Go north";

        callback.onFailedScan(hint);
    }

    private double truncate(double value) {
        return Math.floor(value * 100) / 100;
    }


    private void advance() {
        progress++;
        points += 10;
        callback.onNextLevel(this);
        if(progress == larpObjects.size()) {
            callback.onGameFinished(username, points);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    /**
     * Interface responsible for informing its clients about game state changes.
     */
    public interface GameCallback {
        void onNextLevel(Game game);
        void onFailedScan(String hint);
        void onGameFinished(String username, int points);
    }

}
