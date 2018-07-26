package com.example.panaf.cryptorezf;

/**
 * Created by panafana on 21-Apr-17.
 */

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.util.encoders.Base64;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class KeyGenerator extends Activity{

    SharedPreferences SP;
    SharedPreferences SP2;
    SharedPreferences.Editor SPE;
    PublicKey pubKey;
    PrivateKey privKey;
    Context context ;

    public KeyGenerator(Context context){
        this.context = context;
        SP = context.getSharedPreferences("KeyPair", MODE_PRIVATE);
        SP2 = context.getSharedPreferences("KeySize", MODE_PRIVATE);
    }

    public void generateKeys(String name){
        try {
            int keysize =SP2.getInt("KeySize",2048);
            KeyPairGenerator generator;
            generator = KeyPairGenerator.getInstance("RSA", "BC");
            generator.initialize(keysize, new SecureRandom());
            KeyPair pair = generator.generateKeyPair();
            pubKey = pair.getPublic();
            privKey = pair.getPrivate();
            byte[] publicKeyBytes = pubKey.getEncoded();
            String pubKeyStr = new String(Base64.encode(publicKeyBytes));
            byte[] privKeyBytes = privKey.getEncoded();
            String privKeyStr = new String(Base64.encode(privKeyBytes));
            SPE = SP.edit();
            SPE.putString("PublicKey"+name, pubKeyStr);
            SPE.putString("PrivateKey"+name, privKeyStr);
            SPE.commit();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    public PublicKey getPublicKey(String name){
        String pubKeyStr = SP.getString(name, "");
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
            return  keyFact.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getPublicKeyAsString(String name){
        return SP.getString(name, "");
    }

    public PrivateKey getPrivateKey(String name){
        String privKeyStr = SP.getString(name, "");
        byte[] sigBytes = Base64.decode(privKeyStr);
        PKCS8EncodedKeySpec x509KeySpec = new PKCS8EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            return  keyFact.generatePrivate(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getPrivateKeyAsString(String name){
        return SP.getString(name, "");
    }


    public  PublicKey getKey(String key){
        try{
            byte[] byteKey = Base64.decode(key.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}