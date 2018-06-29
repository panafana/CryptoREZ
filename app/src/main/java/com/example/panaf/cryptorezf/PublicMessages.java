package com.example.panaf.cryptorezf;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.widget.ArrayAdapter;
//import org.bouncycastle.util.encoders.Base64;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
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
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.util.encoders.Base64;

import static org.apache.commons.codec.CharEncoding.UTF_8;


public class PublicMessages  extends ListActivity {

    SharedPreferences SP;
    SharedPreferences SP2;
    SharedPreferences SP3;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_messages);

        PublicKey publicKey = null;
        final KeyGenerator keyz = new KeyGenerator(context);

        SP = getSharedPreferences("messages", MODE_PRIVATE);
        SP2 = getSharedPreferences("KeyChain",MODE_PRIVATE);
        KeyGenerator keyzz= new KeyGenerator(this);
        SP3 =getSharedPreferences("KeyPair", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = SP.getString("messages", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String>  set = gson.fromJson(json, type);
        Gson gson2 = new Gson();
        String json2 = SP.getString("signatures", null);
        Type type2 = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String>  set2 = gson2.fromJson(json2, type2);

        Map<String, ?> temp = SP2.getAll();
        Map<String, ?> temp2 = SP3.getAll();


        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> mess = new ArrayList<>(set);
        ArrayList<String> sign = new ArrayList<>(set2);
        ArrayList<String> privkeys = new ArrayList<>();

        for (Map.Entry<String, ?> entry : temp.entrySet()) {
            keys.add(entry.getValue().toString());
            names.add(entry.getKey().toString());
        }

        for (Map.Entry<String, ?> entry : temp2.entrySet()) {
            if(entry.getKey().startsWith("PrivateKey")) {
                privkeys.add(entry.getKey().toString());
                System.out.println(entry.getKey().toString());
            }
        }

        ArrayList<String> decr = new ArrayList<>();
        for (int i=mess.size()-1; i>=0; i--) {
            for(int z=0;z<privkeys.size();z++){
                System.out.println("z=" + z);
                boolean verification = false;
                boolean flag = true;
                String privkeyString= privkeys.get(z);
                System.out.println("private key "+privkeyString);
                //PrivateKey privateKey = GeneratePrivateKey(privkeyString);
                //System.out.println("private key after "+Base64.encode(privateKey.getEncoded()));
                System.out.println("i=" + i);
                String tempmess = mess.get(i);
                byte[] encryptedBytes = new byte[0];
                byte[] encryptedBytestemp = tempmess.getBytes();
                encryptedBytes = org.bouncycastle.util.encoders.Base64.decode(encryptedBytestemp);
                byte[] decryptedBytes = new byte[0];
                Cipher cipher1 = null;

                try {
                    cipher1 = Cipher.getInstance("RSA/None/PKCS1Padding");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }
                try {
                    cipher1.init(Cipher.DECRYPT_MODE, keyz.getPrivateKey(privkeyString));
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }

                try {
                    decryptedBytes = cipher1.doFinal(encryptedBytes);
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                    flag = false;
                }
                String decrypted = null;
                try {
                    decrypted = new String(decryptedBytes, UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //System.out.println(decrypted);
                //System.out.println(decryptedBytes2);


                if (flag) {
                    int k = 0;
                    for (int j = 0; j < keys.size(); j++) {
                        System.out.println("j=" + j);
                  /*
                try {
                    keyFactory = KeyFactory.getInstance("RSA");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                     pubKey = keyFactory.generatePublic(keySpec);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                System.out.println("Received Sig "+sign.get(i));
                //System.out.println("Received Sig2 "+sign.get(i).getBytes());

                try {
                    System.out.println(verify(decrypted,sign.get(i),pubKey));
                } catch (SignatureException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                */

                        PublicKey pubKey = genPubKey(keys.get(j));

                        try {
                            if (verify(decrypted, sign.get(i), pubKey)) {
                                System.out.println("Verified");
                                verification = true;
                                k = j;
                                break;
                            } else {
                                System.out.println("Something is wrong");
                            }
                        } catch (SignatureException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    if (verification) {
                        decr.add(decrypted + " by " + names.get(k));
                        System.out.println("Success");
                    } else {
                        decr.add(decrypted);
                        System.out.println(decrypted + "aaaaa");
                    }
                }

            }

        }
        System.out.println("total messages: "+mess.size());
        System.out.println("total signatures: "+sign.size());
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                R.layout.rowlayout, R.id.listText, decr);

        // assign the list adapter
        setListAdapter(myAdapter);


    }



    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA1withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        byte[] signatureBytes = Base64.decode(signature);

        return publicSignature.verify(signatureBytes);
    }

    public static PublicKey genPubKey(String pubKeyStr){
        PublicKey publicKey=null;
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
        return publicKey;
    }

    public static PrivateKey GeneratePrivateKey(String privkeystr){
        PrivateKey privateKey = null;
        byte[] sigBytespriv = Base64.decode(privkeystr);
        PKCS8EncodedKeySpec x509KeySpec2 = new PKCS8EncodedKeySpec(sigBytespriv);

        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFact.generatePrivate(x509KeySpec2);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }
}
