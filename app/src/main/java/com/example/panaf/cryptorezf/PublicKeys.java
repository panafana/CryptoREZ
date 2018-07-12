package com.example.panaf.cryptorezf;

/**
 * Created by panafana on 22-Apr-17.
 */

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PublicKeys extends ListActivity {

    private List<String> listValues;
    SharedPreferences SP;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keylistpub);
        Button generate =  findViewById(R.id.generate);
        final EditText contactname = findViewById(R.id.contactname);
        final KeyGenerator keyz = new KeyGenerator(context);
        SP = this.getSharedPreferences("KeyPair", MODE_PRIVATE);
        final Map<String, ?> keys = SP.getAll();

        listValues = new ArrayList<>();


        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if(entry.getKey().startsWith("PrivateKey")==false) {
                String temp= entry.getKey().toString();
                String finaltemp = temp.substring(9);
                listValues.add(finaltemp);
                //Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
            }


        }


        // initiate the listadapter
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                R.layout.rowlayout2, R.id.listText, listValues);

        // assign the list adapter
        setListAdapter(myAdapter);

        generate.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){

                String contactName = contactname.getText().toString();
                keyz.generateKeys(contactName);
                contactname.setText("");
                myAdapter.add(contactName);
                myAdapter.notifyDataSetChanged();

            }
        });
    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        //KeyGenerator keyzglobal = new KeyGenerator(context);

        String selectedItem = (String) getListView().getItemAtPosition(position);
        //String selectedItem = (String) getListAdapter().getItem(position);

        //text.setText("You clicked " + selectedItem + " at position " + position);
        Intent i = new Intent(getApplicationContext(), KeyList.class);

        //i.putExtra("MyPrivKey", keyzglobal.getPrivateKeyAsString("PrivateKey"+selectedItem));
        i.putExtra("MyKeyName","PrivateKey"+selectedItem);
        startActivity(i);
    }


}
