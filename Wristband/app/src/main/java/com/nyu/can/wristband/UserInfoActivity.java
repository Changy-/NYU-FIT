package com.nyu.can.wristband;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by Can on 2016/2/19.
 */
public class UserInfoActivity extends Activity {
    EditText weight, height, name, gender,ID,age;
    SharedPreferences sharedPreferences;
    Button save;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);
        height = (EditText)findViewById(R.id.editText);
        weight = (EditText)findViewById(R.id.editText2);
        name = (EditText)findViewById(R.id.editText3);
        gender = (EditText)findViewById(R.id.editText4);
        ID = (EditText)findViewById(R.id.editText5);
        age = (EditText)findViewById(R.id.editText7);
        save = (Button)findViewById(R.id.button3);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("userinfo", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("height", height.getText().toString());
                editor.putString("weight", weight.getText().toString());
                editor.putString("name", name.getText().toString());
                editor.putString("gender",name.getText().toString());
                editor.putString("ID", ID.getText().toString());
                editor.putString("age", age.getText().toString());
                editor.commit();
                Intent intent = new Intent(UserInfoActivity.this,ChooseBandActivity.class);
                startActivity(intent);
            }
        });



    }
}
