package com.example.panaf.cryptorezf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.bouncycastle.util.encoders.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
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

import static org.apache.commons.codec.CharEncoding.UTF_8;

public class SendMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Context context = this;

        Button send = findViewById(R.id.send);
        final TextInputEditText message = findViewById(R.id.message);
        TextInputLayout layout = findViewById(R.id.textContainer);
        layout.setCounterEnabled(true);
        layout.setCounterMaxLength(250);
        final String myprivKeyName= getIntent().getStringExtra("MyKeyName");
        final String pubKeyStr = getIntent().getStringExtra("publicK");
        String contact = getIntent().getStringExtra("contact");
        TextView showKeyName = findViewById(R.id.mykey);
        TextView showcontact = findViewById(R.id.contactkey);
        showKeyName.setText("Key used: "+myprivKeyName.substring(10));
        showcontact.setText("Contact to send to: "+contact);

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String msg1= message.getText().toString();
                PublicKey publicKey = null;

                Cipher cipher = null;
                String encrypted = null;
                KeyGenerator keyz = new KeyGenerator(context);
                PrivateKey privateKey=keyz.getPrivateKey(myprivKeyName);
                System.out.println("priv key name: "+myprivKeyName);
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

                //Encode to Base64
                byte[] encryptedBytes2 = Base64.encode(encryptedBytes3);
                encrypted = new String(encryptedBytes2);

                //String decryptedStr = new String(decrypted);

                //SIGN
                Signature privateSignature = null;
                try {
                    privateSignature = Signature.getInstance("SHA1withRSA");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                    privateSignature.initSign(privateKey);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                try {
                    privateSignature.update(msg1.getBytes(UTF_8));
                } catch (SignatureException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                byte[] signature = new byte[0];
                try {
                    signature = privateSignature.sign();
                } catch (SignatureException e) {
                    e.printStackTrace();
                }

                byte[] signBytes2 = Base64.encode(signature);
                String signString = new String(signBytes2);

                /*
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Public Key", pubKeyStr);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Public Key Copied", Toast.LENGTH_LONG).show();
                */

                String method = "register";
                BackgroundTask backgroundTask = new BackgroundTask(context);
                backgroundTask.execute(method, encrypted, signString);
               Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });



    }

}
