package com.up.larp.json;


import android.util.Log;

import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class LarpJsonParser {

    public void fetchJson() { //To był zwykły void ale nie mogłem stworzyć instancji klasy w mainactivity.java XDDDDDDD chuj go wie dlaczego
        try {                           //I jest na dole return null.
            String sURL = "https://gist.githubusercontent.com/rafal-adamek/d9aa004f5acedfdb52236720d6f77012/raw/ce045acce56b7edffced3a1f1ea91440a4e76518/.json";
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonArray rootobj = root.getAsJsonArray(); //May be an array, may be an object.
            String latitude = rootobj.get(0).getAsJsonObject().get("lat").getAsString(); // Just grab latitude
            Log.d(latitude, "chuj");
        } catch (Exception e) {
            System.out.println("wyjątek eheh");

        }

    }
}

