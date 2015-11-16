package itworx.com.e_inspector;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by karim on 10/25/2015.
 */
public class WebServiceHandler {

    public final static int GET = 1;
    public final static int POST = 2;


    public static String getJSON(String url, int timeout, int methodTYpe, String json) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            //c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type", "application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);

            switch (methodTYpe) {
                case GET: {
                    c.setRequestMethod("GET");
                    break;
                }
                case POST: {
                    //c.setDoOutput(true);
                    //c.setDoInput(true);
                    //c.setInstanceFollowRedirects(false);
                    c.setRequestMethod("POST");

                    break;
                }
            }
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    switch (methodTYpe) {
                        case GET:
                            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();
                            return sb.toString();
                        case POST: {
                            DataOutputStream printout = new DataOutputStream(c.getOutputStream());
                            printout.writeBytes(URLEncoder.encode(json, "UTF-8"));
                            printout.flush();
                            printout.close();
                        }
                    }
            }

        } catch (MalformedURLException ex) {
            Log.d("", "einspector:" + ex.getMessage());
        } catch (IOException ex) {
            Log.d("", "einspector:" + ex.getMessage());
        } finally {
            if (c != null) {
                c.disconnect();
            }
        }
        return null;
    }

    public static void sendJSON(String url, int timeout, String json) {
        HttpURLConnection conn ;
        try {
            //constants
            URL u = new URL(Constants.CASES_URI);
            String message = new JSONObject().toString();

            conn = (HttpURLConnection) u.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(false);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(json.getBytes());
            //clean up
            os.flush();
            conn.connect();
            int status = conn.getResponseCode();
            Log.d("","einspector:status:"+status);

        } catch (MalformedURLException e) {
            Log.d("", "einspector:status:" + e.getMessage());
        } catch (ProtocolException e) {
            Log.d("", "einspector:status:" + e.getMessage());
        } catch (IOException e) {
            Log.d("", "einspector:status:" + e.getMessage());
        } finally {
            //clean up

        }

    }
}