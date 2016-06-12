package com.sjtu.charles.test;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhuyifei on 2016/6/12.
 */
public class HttpManager {

    private static final int DEFAULT_TIMEOUT = 12000;
    private static final String TAG = "HttpManager";

    private static HttpURLConnection openConnection(String string, int timeout) throws IOException {
        URL url = new URL(string);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        String agent = System.getProperty("http.agent");
        con.setRequestProperty("User-Agent", agent);
        if (timeout > 0) {
            timeout = DEFAULT_TIMEOUT;
        }
        con.setConnectTimeout(timeout);
        return con;
    }

    public static void operation(String url, Ope ope) {
        operation(url,ope,0);
    }
    public static void operation(String url, Ope ope, int timeout) {
        try {
            HttpURLConnection con = openConnection(url, timeout);
            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {//200
                if (ope != null) {
                    ope.onResponseOk(con);
                }
            } else {
                if (ope != null) {
                    ope.onResponseFailed(con);
                }
            }
            Log.d(TAG, url + " ResponseCode: " + code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract static class Ope {
        public abstract void onResponseOk(HttpURLConnection con);
        public abstract void onResponseFailed(HttpURLConnection con);
    }

    public static String readContent(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        return sb.toString();
    }

}
