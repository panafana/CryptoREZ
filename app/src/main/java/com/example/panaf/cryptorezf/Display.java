package com.example.panaf.cryptorezf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Display extends AppCompatActivity {
    SharedPreferences SP;
    Context context;
    int newmessagecount =0;

    private String jsonResult;
    private String url = "http://83.212.84.230/getdata.php";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        listView =  findViewById(R.id.listView1);


        accessWebService();

    }



    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            SP = getSharedPreferences("messages", MODE_PRIVATE);
            String id;
            Gson gson4 = new Gson();
            //System.out.println(SP.getString("ids", null));
            if(SP.contains("ids")) {
                String json4 = SP.getString("ids", null);
                Type type4 = new TypeToken<ArrayList<String>>() {
                }.getType();
                ArrayList<String> set4 = gson4.fromJson(json4, type4);
                ArrayList<String> ids = new ArrayList<>(set4);
                 id = new String(ids.get(ids.size() - 1));
            }else {
                 id = "0";
            }

            try {
                List<NameValuePair> sendparams = new ArrayList<>();
                sendparams.add(new BasicNameValuePair("id", id));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(sendparams, HTTP.UTF_8);
                httppost.setEntity(ent);

                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();

                InputStream inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                jsonResult = sb.toString();

                //jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
            }

            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            }

            catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();

            Toast.makeText(getApplicationContext(), newmessagecount+" new messages", Toast.LENGTH_LONG).show();
        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
    }

    // build hash set for list view
    public void ListDrwaer() {
        List<Map<String, String>> employeeList = new ArrayList<Map<String, String>>();

        SP = getSharedPreferences("messages", MODE_PRIVATE);
            ArrayList<String> messages = new ArrayList<>();
            ArrayList<String> signatures = new ArrayList<>();
            ArrayList<String> timestamps = new ArrayList<>();
            ArrayList<String> ids = new ArrayList<>();
            Gson gson = new Gson();
            String json = SP.getString("messages", null);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> set = gson.fromJson(json, type);
            Gson gson2 = new Gson();
            String json2 = SP.getString("signatures", null);
            Type type2 = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> set2 = gson2.fromJson(json2, type2);
            Gson gson3 = new Gson();
            String json3 = SP.getString("timestamps", null);
            Type type3 = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> set3 = gson3.fromJson(json3, type3);
            Gson gson4 = new Gson();
            String json4 = SP.getString("ids", null);
            Type type4 = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> set4 = gson4.fromJson(json4, type4);

            if(set!=null) {
                 messages = new ArrayList<>(set);
                 signatures = new ArrayList<>(set2);
                 timestamps = new ArrayList<>(set3);
                 ids = new ArrayList<>(set4);
            }


            try {


                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("result");
                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String name = jsonChildNode.optString("message");
                    String sign = jsonChildNode.optString("signature");
                    String timestamp = jsonChildNode.optString("timestamp");
                    String id = jsonChildNode.optString("id");

                    String outPut = name;
                    String outPut2 = sign;
                    messages.add(outPut);
                    signatures.add(outPut2);
                    timestamps.add(timestamp);
                    ids.add(id);
                    System.out.println("New data: "+ (i+1));
                    newmessagecount =(i+1);

                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error JSON Parser" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
            }


            SharedPreferences.Editor editor = SP.edit();
            ArrayList<String> set1 = new ArrayList<>();
            ArrayList<String> set12 = new ArrayList<>();
            ArrayList<String> set13 = new ArrayList<>();
            ArrayList<String> set14 = new ArrayList<>();

            set1.addAll(messages);
            set12.addAll(signatures);
            set13.addAll(timestamps);
            set14.addAll(ids);
            Gson gson1 = new Gson();
            String json1 = gson1.toJson(set1);
            Gson gson12 = new Gson();
            String json12 = gson12.toJson(set12);
            Gson gson13 = new Gson();
            String json13 = gson13.toJson(set13);
            Gson gson14 = new Gson();
            String json14 = gson14.toJson(set14);

            editor.putString("messages", json1);
            editor.putString("signatures", json12);
            editor.putString("timestamps", json13);
            editor.putString("ids", json14);
            editor.apply();
            editor.commit();
            System.out.println("stored");
            //System.out.println("Messages: " + messages);
            //System.out.println("Signatures: " + signatures);
            //System.out.println("timestamps: " + timestamps);
            System.out.println("ids: " + ids);

            for (int i = 0; i < messages.size(); i++) {
                String temp = messages.get(i);
                employeeList.add(createEmployee("whiteboard", temp));
            }


        SimpleAdapter simpleAdapter = new SimpleAdapter(this, employeeList,
                android.R.layout.simple_list_item_1,
                new String[] { "whiteboard" }, new int[] { android.R.id.text1 });
        listView.setAdapter(simpleAdapter);


    }

    private HashMap<String, String> createEmployee(String name, String number) {
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put(name, number);
        return employeeNameNo;
    }


}
