package com.example.john.fabflix;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lv = (ListView) findViewById(R.id.searchListView);
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = lv.getItemAtPosition(position);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);

                intent.putExtra("query", s);
                searchView.clearFocus();

                startActivity(intent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                sendGetRequest(s);
                return true;
            }
        });

        return true;
    }


    private void sendGetRequest(String query){

        class SendGetReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String query = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                //String url = "http://192.168.1.129:8080/fabflix/ftsMobile?q=";
                String url = "http://54.67.80.64:8080/fabflix/ftsMobile?q=";
                //String url = "http://54.183.227.19/fabflix/ftsMobile?q=";


                try {

                    String encodedQuery = URLEncoder.encode(query, "UTF-8");

                    url += encodedQuery;

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
                ArrayList<String> suggestions = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    suggestions.add(jsonArray.getString(i));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        suggestions);
                lv.setAdapter(arrayAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
