package com.nyu.can.wristband;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Can on 2016/2/20.
 */
public class FitbitDisplayActivity extends Activity {

    ListView listView;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fitbitdisplay);
        listView = (ListView)findViewById(R.id.listView2);

        byte[] fitbitdata = getIntent().getByteArrayExtra("data");
        byte[] batterydata = getIntent().getByteArrayExtra("battery");
        String data = bytesToHex(fitbitdata);
        String steps = data.substring(10,12) + data.substring(8,10);
        Log.d("step",steps);
        String cal = data.substring(26,28)+data.substring(24,26);
        String heart_rate = data.substring(36,38);
        String battery = bytesToHex(batterydata);
        int decimalbattery = Integer.parseInt(battery, 16);

        final ArrayList<String> list = new ArrayList<>();
        list.add("Steps:" + "               " + Integer.parseInt(steps,16));
        list.add("Calorie:" + "                 " + Integer.parseInt(cal,16));
        list.add("Heart rate:" + "              " + Integer.parseInt(heart_rate,16));
        list.add("Battery:" + "                 " + decimalbattery);
        list.add("Sleep time：");

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemposition = list.get(position);
                if (itemposition.contains("Steps")) {
                    Intent intent = new Intent(FitbitDisplayActivity.this, StepAnalyzeActivity.class);
                    startActivity(intent);
                } else if (itemposition.contains("Sleep")) {
                    Intent intent = new Intent(FitbitDisplayActivity.this, SleeptimeAnalyzeActivity.class);
                    startActivity(intent);
                }
            }

        });
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
