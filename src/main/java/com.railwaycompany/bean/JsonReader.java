package com.railwaycompany.bean;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class JsonReader {

    private static String readAll(final Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray read(final String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        } finally {
            is.close();
        }
    }
}
