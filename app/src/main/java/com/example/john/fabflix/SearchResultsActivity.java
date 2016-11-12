package com.example.john.fabflix;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchResultsActivity extends ActionBarActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        lv = (ListView) findViewById(R.id.searchResultsListView);
        lv.setClickable(true);

        Bundle bundle = getIntent().getExtras();
        String query = (String)bundle.get("query");
        sendGetRequest(query);

    }

    private void sendGetRequest(String query){

        class SendGetReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String query = params[0];

                HttpClient httpClient = new DefaultHttpClient();


                //String url = "http://192.168.1.129:8080/fabflix/searchMobile?title=";
                String url = "http://54.67.80.64:8080/fabflix/ftsMobile?q=";
                //String url = "http://54.183.227.19/fabflix/ftsMobile?q=";


                try {

                    String encodedQuery = URLEncoder.encode(query, "UTF-8");

                    url += encodedQuery + "&director=&f_n=&l_n=&order=t_asc&rpp=5&page=1";

                    HttpGet httpGet = new HttpGet(url);

                    HttpResponse httpResponse = httpClient.execute(httpGet);

                    InputStream inputStream = httpResponse.getEntity().getContent();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;

                    while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                        stringBuilder.append(bufferedStrChunk);
                    }

                    inputStream.close();
                    inputStreamReader.close();
                    bufferedReader.close();

                    return stringBuilder.toString();

                } catch (ClientProtocolException cpe) {
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                populateListView(result);
            }
        }
        SendGetReqAsyncTask sendGetReqAsyncTask = new SendGetReqAsyncTask();
        sendGetReqAsyncTask.execute(query);
    }

    public void populateListView(String result){
        if (result.equals("Empty")){
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    new ArrayList<String>());
            lv.setAdapter(arrayAdapter);
        }
        else {

            String jsonString = result;

            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                ArrayList<String> movies = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    System.out.print(jsonArray.getString(i) + " ");
                    movies.add(jsonArray.getString(i));
                }
                System.out.println();

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        movies);
                lv.setAdapter(arrayAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
