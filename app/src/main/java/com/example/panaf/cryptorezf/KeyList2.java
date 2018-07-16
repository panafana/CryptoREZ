package com.example.panaf.cryptorezf;

/**
 * Created by panafana on 22-Apr-17.
 */

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeyList2 extends ListActivity {

    private List<String> listValues;
    SharedPreferences SP;
    SharedPreferences.Editor SPE;
    private String m_Text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keylist);
        ListView list = findViewById(android.R.id.list);
        SP = this.getSharedPreferences("KeyChain", MODE_PRIVATE);
        Map<String, ?> keys = SP.getAll();
        listValues = new ArrayList<>();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {

            listValues.add(entry.getKey());
            //Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());

        }

        // initiate the listadapter
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this,
                R.layout.rowlayout, R.id.listText, listValues);
        // assign the list adapter
        setListAdapter(myAdapter);
        registerForContextMenu(list);
    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        //String privKey = getIntent().getExtras().getString("MyPrivKey");

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
        SP = this.getSharedPreferences("KeyChain", MODE_PRIVATE);
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

                    String tempContact = SP.getString(listItemName, "");
                    SPE.remove(listItemName);
                    SPE.putString(m_Text, tempContact);
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
        SP = this.getSharedPreferences("KeyChain", MODE_PRIVATE);
        Map<String, ?> keys = SP.getAll();
        listValues = new ArrayList<>();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            listValues.add(entry.getKey());
        }
        // initiate the listadapter
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this,
                R.layout.rowlayout, R.id.listText, listValues);
        // assign the list adapter
        setListAdapter(myAdapter);
    }
    public void alertMessage2(final String listItemName) {
        SP = this.getSharedPreferences("KeyChain", MODE_PRIVATE);
        SPE= SP.edit();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        SPE.remove(listItemName);
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
