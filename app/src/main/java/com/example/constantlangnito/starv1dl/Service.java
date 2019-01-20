package com.example.constantlangnito.starv1dl;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.constantlangnito.starv1dl.VariablesStatic.zipFileUrl;

public class Service {

    public void start(){
        getVersionsInfos();
    }

    public void getVersionsInfos() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(VariablesStatic.DATA_SOURCE_URL,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        try {
                            JSONArray reords = response.getJSONArray("records");
                            /**
                             *Traitement du fichier
                             */

                            JSONObject file1 = (JSONObject) reords.get(0);
                            JSONObject file12 = (JSONObject) file1.get("fields");
                            JSONObject fichier1 = (JSONObject) file12.get("fichier");
                            Log.d("file12",file12.toString() );
                            String last_sync1 = (String) fichier1.get("last_synchronized");
                            zipFileUrl = file12.get("url").toString();

                            Log.e("STAR-newData1", zipFileUrl);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Json object is returned as a response
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.e("STAR", "==> Erreur de telechargement <==");
                    }
                });
    }
}
