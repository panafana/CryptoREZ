package com.example.panaf.cryptorezf;

/**
 * Created by panafana on 22-Apr-17.
 */

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PublicKeys extends ListActivity {

    private List<String> listValues;
    SharedPreferences SP;
    SharedPreferences.Editor SPE;
    Context context = this;
    private String m_Text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keylistpub);
        Button generate =  findViewById(R.id.generate);
        ListView list = findViewById(android.R.id.list);
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
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this,
                R.layout.rowlayout, R.id.listText, listValues);

        // assign the list adapter
        setListAdapter(myAdapter);

        registerForContextMenu(list);
        generate.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){

                String contactName = contactname.getText().toString();
                if(!contactName.equals("")) {
                    keyz.generateKeys(contactName);
                    contactname.setText("");
                    refresh();
                }
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==android.R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(listValues.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = listValues.get(info.position);
        System.out.println(String.format("Selected %s for item %s", menuItemName, listItemName));

        switch(menuItemName){
            case "Edit Name":
                alertMessage(listItemName);
                //alertMessage();

                break;
            case "Delete":
                alertMessage2(listItemName);
                break;

        }

        return true;
    }
    public void alertMessage(final String listItemName ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new name");
        SP = this.getSharedPreferences("KeyPair", MODE_PRIVATE);
        SPE= SP.edit();
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();

                if(m_Text!="") {

                    String tempPub = SP.getString("PublicKey" + listItemName, "");
                    String tempPriv = SP.getString("PrivateKey" + listItemName, "");
                    SPE.remove("PublicKey" + listItemName);
                    SPE.remove("PrivateKey" + listItemName);
                    SPE.putString("PublicKey" + m_Text, tempPub);
                    SPE.putString("PrivateKey" + m_Text, tempPriv);
                    SPE.apply();
                    refresh();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void refresh(){
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
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this,
                R.layout.rowlayout, R.id.listText, listValues);
        // assign the list adapter
        setListAdapter(myAdapter);

    }
    public void alertMessage2(final String listItemName) {
        SP = this.getSharedPreferences("KeyPair", MODE_PRIVATE);
        SPE= SP.edit();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        SPE.remove("PublicKey"+listItemName);
                        SPE.remove("PrivateKey"+listItemName);
                        SPE.apply();
                        refresh();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Key?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
