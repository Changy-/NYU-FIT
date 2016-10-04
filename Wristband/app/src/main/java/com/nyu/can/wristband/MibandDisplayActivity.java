package com.nyu.can.wristband;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Can on 2016/2/18.
 */
public class MibandDisplayActivity extends Activity {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    ListView datadisplayview;
    String TAG;
    TextView user_info;
    Button share;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mibanddisplay);
        datadisplayview = (ListView)findViewById(R.id.listView);
        share = (Button)findViewById(R.id.button4);
        //user_info = (TextView)findViewById(R.id.textView2);
        byte[] stepdata = getIntent().getByteArrayExtra("step");
        String steps = bytesToHex(stepdata).substring(2,4) + bytesToHex(stepdata).substring(0,2);
        long decimalstep = Long.parseLong(steps,16);
        Log.d(TAG, steps);
        final ArrayList<String> list = new ArrayList<String>();
        list.add("step" + "                         " + decimalstep);
        list.add("sleep time" + "                       " );
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,list);
        datadisplayview.setAdapter(adapter);
        datadisplayview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemposition = list.get(position);
                if (itemposition.contains("step")) {
                    Intent intent = new Intent(MibandDisplayActivity.this, StepAnalyzeActivity.class);
                    startActivity(intent);
                } else if (itemposition.contains("sleep")) {
                    Intent intent = new Intent(MibandDisplayActivity.this, SleeptimeAnalyzeActivity.class);
                    startActivity(intent);
                }
                Log.d(TAG, itemposition);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MibandDisplayActivity.this, RankActivity.class);
                startActivity(intent);
            }
        });



       // if(sharedPreferences != null){
        //user_info.setText("Name: " + sharedPreferences.getString("name","") + '\n' + "Gender: " + sharedPreferences.getString("gender","") + '\n'
                          // + "Weight: " + sharedPreferences.getString("weight","") + '\n' + "Height: " + sharedPreferences.getString("height","") + '\n' + "NYU NetID: " + sharedPreferences.getString("ID",""));}

        /*ArrayList list = getIntent().getStringArrayListExtra("char");
        ArrayList service_1 = (ArrayList)list.get(2);
        for(int i=0;i<service_1.size();i++){
            HashMap<String,byte[]> map = (HashMap<String,byte[]>)service_1.get(i);
            textView.append(map.toString()+'\n');
        }*/
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

    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char)decimal);
        }
        return sb.toString();
    }

}
