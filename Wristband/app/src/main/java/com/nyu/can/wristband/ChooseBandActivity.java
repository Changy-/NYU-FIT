package com.nyu.can.wristband;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Can on 2016/2/19.
 */
public class ChooseBandActivity extends Activity {
    ImageView Miband, Fitbit,Jawbone,Garmin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectband);
        Miband = (ImageView) findViewById(R.id.s001_band);
        Fitbit = (ImageView) findViewById(R.id.s002_band);
        Jawbone = (ImageView)findViewById(R.id.s003_band);
        Garmin = (ImageView)findViewById(R.id.s004_band);
        Miband.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseBandActivity.this, DeviceScanActivity.class);
                String filtername = "MI";
                intent.putExtra("name",filtername);
                startActivity(intent);
            }
        });

        Fitbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseBandActivity.this,DeviceScanActivity.class);
                String filtername = "Charge";
                intent.putExtra("name",filtername);
                startActivity(intent);
            }
        });
        Jawbone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(),"To be continued...",Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Garmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(),"To be continued...",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
