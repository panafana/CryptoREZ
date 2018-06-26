package com.example.panaf.cryptorezf;

/**
 * Created by panafana on 22-Apr-17.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ListActivity;

public class KeyList extends ListActivity {

    private TextView text;
    private List<String> listValues;
    SharedPreferences SP;
    SharedPreferences.Editor SPE;
    private Toolbar supportActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keylist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        SP = this.getSharedPreferences("KeyChain", MODE_PRIVATE);
        Map<String, ?> keys = SP.getAll();
        text = (TextView) findViewById(R.id.mainText);
        listValues = new ArrayList<String>();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {

            listValues.add(entry.getKey().toString());
            //Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());

        }

        // initiate the listadapter
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                R.layout.rowlayout, R.id.listText, listValues);
        // assign the list adapter
        setListAdapter(myAdapter);

    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        //String privKey = getIntent().getExtras().getString("MyPrivKey");
        String privKeyName = getIntent().getExtras().getString("MyKeyName");

        String selectedItem = (String) getListView().getItemAtPosition(position);
        //String selectedItem = (String) getListAdapter().getItem(position);

        //text.setText("You clicked " + selectedItem + " at position " + position);
        Intent i = new Intent(getApplicationContext(), Main2Activity.class);
        i.putExtra("publicK", SP.getString(selectedItem,""));
        i.putExtra("contact", selectedItem);
        //i.putExtra("MyPrivKey", privKey);
        i.putExtra("MyKeyName",privKeyName);
        startActivity(i);
    }


}
