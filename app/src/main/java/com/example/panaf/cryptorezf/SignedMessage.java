package com.example.panaf.cryptorezf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.bouncycastle.util.encoders.Base64;

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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static com.example.panaf.cryptorezf.R.id.display;

public class SignedMessage extends AppCompatActivity {
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_messages);

        Button test = (Button) findViewById(R.id.test);
        Button button2 = (Button) findViewById(R.id.button2);
        Button display = (Button) findViewById(R.id.display2);
        //final TextView show = (TextView)  findViewById(R.id.show);
        //final TextView show2 = (TextView)  findViewById(R.id.show2);
        final EditText message = (EditText) findViewById(R.id.message);
        final KeyGenerator keys = new KeyGenerator(context);



        display.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                Intent i = new Intent(getApplicationContext(),Display.class);
                startActivity(i);
            }

        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                Intent i = new Intent(getApplicationContext(),PublicMessages.class);
                startActivity(i);
            }

        });





        test.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {
                String msg1 = message.getText().toString();
                PrivateKey privateKey = keys.getPrivateKey("");
                PublicKey pbk = keys.getPublicKey("");
                PublicKey publicKey = null;
                byte[] encryptedBytes = new byte[0], decryptedBytes = new byte[0];
                Cipher cipher = null, cipher1 = null;
                String encrypted = null, decrypted;


                String pubKeyStr = getIntent().getStringExtra("publicK");
                byte[] sigBytes = Base64.decode(pubKeyStr);
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);

                KeyFactory keyFact = null;
                try {
                    keyFact = KeyFactory.getInstance("RSA", "BC");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                }
                try {
                    publicKey = keyFact.generatePublic(x509KeySpec);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }


                try {
                    cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }
                try {
                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }

                byte[] encryptedBytes3 = new byte[0];

                try {
                    encryptedBytes3 = cipher.doFinal(msg1.getBytes());
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }


                byte[] encryptedBytes2 = Base64.encode(encryptedBytes3);
                encrypted = new String(encryptedBytes2);


                byte[] data = msg1.getBytes();
                Signature signature = null;
                byte[] signedData = new byte[0];
                try {
                    signature = Signature.getInstance("SHA256withRSA");
                    signature.initSign(privateKey);
                    signature.update(data);
                    signedData = signature.sign();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (SignatureException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }


                System.out.println("hash: " + signedData);
                byte[] signedData2 = Base64.encode(signedData);



                try {
                    signature.initVerify(pbk);
                    signature.update(data);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (SignatureException e) {
                    e.printStackTrace();
                }

                try {
                    if(signature.verify(signedData2)){
                        System.out.println("Verified");
                    }else{
                        System.out.println("Something is wrong");
                    }
                } catch (SignatureException e) {
                    e.printStackTrace();
                }





                String method = "register";
                BackgroundTask backgroundTask = new BackgroundTask(context);
                backgroundTask.execute(method, encrypted,signedData.toString());


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
            Intent i = new Intent(getApplicationContext(), KeyList2.class);
            startActivity(i);
        }else if(id== R.id.action_clear){
            alertMessage();
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

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all Public Keys?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }



}
