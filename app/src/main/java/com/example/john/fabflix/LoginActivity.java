package com.example.john.fabflix;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
    }

    public void login(View view){
        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
        sendPostRequest(email, password);
    }

    private void sendPostRequest(String email, String password){

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... params) {

                String paramEmail = params[0];
                String paramPassword = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                //HttpPost httpPost = new HttpPost("http://192.168.1.129:8080/fabflix/loginMobile");
                HttpPost httpPost = new HttpPost("http://54.67.80.64:8080/fabflix/loginMobile");
                //HttpPost httpPost = new HttpPost("http://54.183.227.19/fabflix/loginMobile");

                BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("email", paramEmail);
                BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("password", paramPassword);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(usernameBasicNameValuePair);
                nameValuePairList.add(passwordBasicNameValuePAir);

                try {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                    httpPost.setEntity(urlEncodedFormEntity);

                    try {
                        HttpResponse httpResponse = httpClient.execute(httpPost);

                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while((bufferedStrChunk = bufferedReader.readLine()) != null){
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
                    }

                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(result.equals("Validated")){
                    Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(email, password);
    }

}
