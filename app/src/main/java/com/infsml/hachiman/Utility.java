package com.infsml.hachiman;

import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utility {
    public static JSONObject postJSON(String url_link,String jsonString) throws Exception{
        Log.i("PostJSON","Init");
        URL url = new URL(url_link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        Log.i("PostJSON","connecting");
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(jsonString.getBytes());
        outputStream.flush();
        outputStream.close();
        InputStream inputStream = connection.getInputStream();
        Log.i("PostJSON","downloading");
        int i;StringBuffer stringBuffer=new StringBuffer();
        while ((i=inputStream.read())!=-1){
            stringBuffer.append((char)i);
        }
        String download = stringBuffer.toString();
        return new JSONObject(download);
    }
}
