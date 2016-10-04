package com.nyu.can.wristband;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Can on 2016/2/19.
 */
public class SleeptimeAnalyzeActivity extends Activity {
    TextView analyze;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleepanalyze);
        analyze = (TextView) findViewById(R.id.textView5);
        String text = "Your Deep sleep to light sleep ratio is 0.8. It’s a score of 8. [Optional: Warning: Sleep Deprivation Can Lead to Serious Health Problems Sleep disorders and chronic sleep loss can put you at risk for: Heart disease, Heart failure, High blood pressure, Stroke, Diabetes]\n";
        analyze.setText(text);
    }
}
