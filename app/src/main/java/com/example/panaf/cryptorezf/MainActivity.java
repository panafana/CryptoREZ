package com.example.panaf.cryptorezf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.ClipboardManager;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import com.example.panaf.cryptorezf.KeyGenerator;
import android.content.SharedPreferences;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;

import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Text;

import static android.R.attr.id;
import static android.R.attr.name;
import static android.R.attr.scaleType;
import static org.apache.commons.codec.CharEncoding.UTF_8;


public class MainActivity extends AppCompatActivity {


    Context context = this;
    public String input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button next =  findViewById(R.id.next);
        ImageView qr = findViewById(R.id.imageView);
        Button button2 =  findViewById(R.id.button2);
        Button store =  findViewById(R.id.store);
        Button display =  findViewById(R.id.display);
        Button sendmessage =  findViewById(R.id.sendmessage);
        TextView showPKName = findViewById(R.id.pkName);
        final EditText editText =  findViewById(R.id.editText);
        final EditText editText2 =  findViewById(R.id.editText2);



        //Show PK QR code if it was selected
        Intent intent = getIntent();

        if(intent.hasExtra("MyPKKeyName")){
            String pkName = intent.getStringExtra("MyPKKeyName");
            String pkValue =intent.getStringExtra("MyPKValue");
            Bitmap myBitmap = QRCode.from(pkValue).bitmap();

            qr.setImageBitmap(myBitmap);
            //qr.setScaleType(ImageView.ScaleType.MATRIX);
            showPKName.setText("Showing "+pkName);
        }





        //Show Public Key
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                Intent i = new Intent(getApplicationContext(),ShowPK.class);
                startActivity(i);
            }

        });

        //Store new Contact
        store.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){

                SharedPreferences SP;
                SharedPreferences.Editor SPE;
                String keytostore = editText2.getText().toString();
                SP = context.getSharedPreferences("KeyChain", MODE_PRIVATE);
                SPE = SP.edit();
                SPE.putString(editText.getText().toString(), keytostore);
                SPE.commit();
                editText.setText("");
                editText2.setText("");


            }

        });



        //Go to Display Class
        display.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                Intent i = new Intent(getApplicationContext(),Display.class);
                startActivity(i);
            }

        });

        //Go to your Messages Class
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                Intent i = new Intent(getApplicationContext(),PublicMessages.class);
                startActivity(i);
            }

        });
       sendmessage.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                Intent i = new Intent(getApplicationContext(),PublicKeys.class);
                startActivity(i);
            }

        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), KeyList.class);
            startActivity(i);
        }else if(id== R.id.action_clear){
            alertMessage();
        }else if(id== R.id.public_keys){
            Intent i = new Intent(getApplicationContext(), PublicKeys.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        SharedPreferences SP;
                        SharedPreferences.Editor SPE;
                        SP = context.getSharedPreferences("KeyChain", MODE_PRIVATE);
                        SPE = SP.edit();
                        SPE.clear();
                        SPE.commit();
                        SharedPreferences SP2;
                        SharedPreferences.Editor SPE2;
                        SP2 = context.getSharedPreferences("KeyPair", MODE_PRIVATE);
                        SPE2 = SP2.edit();
                        SPE2.clear();
                        SPE2.commit();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all  Keys?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
