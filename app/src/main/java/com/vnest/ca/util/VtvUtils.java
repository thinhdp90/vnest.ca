package com.vnest.ca.util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.vnest.ca.api.model.VTVRequest;
import com.vnest.ca.api.model.VTVResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class VtvUtils {
    public static class FetchVideoUrl extends AsyncTask<String, VTVRequest, VTVResponse> {
        private final String url = "https://vtvgo.vn/ajax-get-stream";

        @Override
        protected VTVResponse doInBackground(String... strings) {
            try {
                URL url = new URL(this.url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("accept", "*/*");
                urlConnection.addRequestProperty("accept-encoding", "gzip, deflate, br");
                urlConnection.addRequestProperty("accept-language", "vi-VN,vi;q=0.9,fr-FR;q=0.8,fr;q=0.7,en-US;q=0.6,en;q=0.5,am;q=0.4,en-AU;q=0.3");
                urlConnection.addRequestProperty("origin", "https://vtvgo.vn");
                urlConnection.addRequestProperty("referer", "https://vtvgo.vn/xem-truc-tuyen-kenh-vtv2-2.html");
                urlConnection.setDoOutput(true);
                VTVRequest vtvRequest = new VTVRequest(1, 2, System.currentTimeMillis(), System.currentTimeMillis() + "." + UUID.randomUUID());
                String jsonBody = new Gson().toJson(vtvRequest);
                Log.e("Body", jsonBody);
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.close();
                }

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                    return new Gson().fromJson(response.toString(), VTVResponse.class);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(VTVResponse vtvResponse) {
            super.onPostExecute(vtvResponse);
        }
    }

}
