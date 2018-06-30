package com.example.panaf.cryptorezf;

import android.app.ListActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowPK extends ListActivity {
    private List<String> listValues;
    SharedPreferences SP;

    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_show_pk);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        SP = this.getSharedPreferences("KeyPair", MODE_PRIVATE);
        Map<String, ?> keys = SP.getAll();

        listValues = new ArrayList<String>();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getKey().startsWith("PublicKey")) {
                String temp = entry.getKey().toString();
                String finaltemp = temp.substring(9);
                listValues.add(finaltemp);
                System.out.println(finaltemp);
            }
        }

        // initiate the listadapter
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                R.layout.rowlayout2, R.id.listText, listValues);
        // assign the list adapter
        setListAdapter(myAdapter);

    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        KeyGenerator keyz = new KeyGenerator(context);
        String selectedItem = (String) getListView().getItemAtPosition(position);
        System.out.println("selectedItem: "+selectedItem);
        String pubKeyStr = keyz.getPublicKeyAsString("PublicKey"+selectedItem);
        System.out.println("pubKeyStr: "+pubKeyStr);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Public Key", pubKeyStr);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Public Key Copied", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.putExtra("MyPKKeyName",selectedItem);
        i.putExtra("MyPKValue",pubKeyStr);
        startActivity(i);
    }
}
