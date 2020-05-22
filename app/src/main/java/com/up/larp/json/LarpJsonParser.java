package com.up.larp.json;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for fetching json file and taking out labels
 */
public class LarpJsonParser {

    /**
     * @param sUrl URL to .json file
     * @param callback callback for json parsing results
     */
    public void fetchJson(String sUrl, JsonResultCallback callback) {
        Thread thread = new Thread(() -> {
            URL url;
            try {
                url = new URL(sUrl);
                URLConnection request = url.openConnection();
                request.connect();

                JsonParser jp = new JsonParser(); //from gson
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                JsonArray rootArray = root.getAsJsonArray(); //May be an array, may be an object.

                List<LarpObject> larpObjectList = new ArrayList<>();

                for (JsonElement element : rootArray) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    LarpObject larpObject = new LarpObject(
                            jsonObject.get("label").getAsString(),
                            jsonObject.get("lat").getAsDouble(),
                            jsonObject.get("lon").getAsDouble()
                    );

                    larpObjectList.add(larpObject);

                    callback.onJsonObjectReceived(larpObjectList);

                }

            } catch (Exception e) {
                e.printStackTrace();
                callback.onJsonObjectReceived(new ArrayList());
            }
        });

        thread.start();
    }

    /**
     * Callback for json parsing results.
     */
    public interface JsonResultCallback {
        void onJsonObjectReceived(List<LarpObject> location);
    }
}

