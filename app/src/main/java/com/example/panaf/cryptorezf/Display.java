package com.example.panaf.cryptorezf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class Display extends AppCompatActivity {
    SharedPreferences SP;

    SharedPreferences.Editor SPE;

    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> signatures = new ArrayList<>();
    ArrayList<String> timestamps = new ArrayList<>();
    private String jsonResult;
    private String url = "http://83.212.84.230/getdata.php";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        listView = (ListView) findViewById(R.id.listView1);
        SP = getSharedPreferences("messages", MODE_PRIVATE);
        //textv1=(TextView)findViewById(R.id.textView1);
        accessWebService();

    }



    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
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

        try {


            JSONObject jsonResponse = new JSONObject(jsonResult);

            JSONArray jsonMainNode = jsonResponse.optJSONArray("result");
            for (int i = 0; i <  jsonMainNode.length() ; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String name = jsonChildNode.optString("message");
                String sign = jsonChildNode.optString("signature");
                String timestamp =jsonChildNode.optString("timestamp");
                //String number = jsonChildNode.optString("employee_no");
                String outPut = name ;
                String outPut2 = sign;
                messages.add(outPut);
                signatures.add(outPut2);
                timestamps.add(timestamp);
                //System.out.println(outPut.length());
                //textv1.setText(name);
                //textv1.setText(jsonResult);
                employeeList.add(createEmployee("whiteboard", outPut));
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error JSON Parser" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e){
            Toast.makeText(this , "No internet",Toast.LENGTH_LONG).show();
        }
        SharedPreferences.Editor editor = SP.edit();
        ArrayList<String> set = new ArrayList<>();
        ArrayList<String> set2 = new ArrayList<>();
        ArrayList<String> set3 = new ArrayList<>();

        set.addAll(messages);
        set2.addAll(signatures);
        set3.addAll(timestamps);
        Gson gson = new Gson();
        String json = gson.toJson(set);
        Gson gson2 = new Gson();
        String json2 = gson2.toJson(set2);
        Gson gson3 = new Gson();
        String json3 = gson3.toJson(set3);
        
        editor.putString("messages", json);
        editor.putString("signatures", json2);
        editor.putString("timestamps", json3);
        editor.apply();
        editor.commit();
        System.out.println("stored");
        System.out.println("Messages: "+messages);
        System.out.println("Signatures: "+signatures);
        System.out.println("timestamps: "+timestamps);


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
