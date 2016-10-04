package com.nyu.can.wristband;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class Login extends Activity {

    Button button;
    SharedPreferences sharedPreferences;
    String TAG;
    TextView getresponse;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("userinfo",0);
        button = (Button)findViewById(R.id.button);
        getresponse = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText6);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getString("ID","").equals(editText.getText().toString())){
                    Intent intent = new Intent(Login.this,ChooseBandActivity.class);
                    startActivity(intent);
            }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"NetID not found!",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        getresponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,UserInfoActivity.class);
                startActivity(intent);
            }
        });
    }

}
