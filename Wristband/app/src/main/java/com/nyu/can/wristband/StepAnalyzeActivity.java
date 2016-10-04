package com.nyu.can.wristband;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Can on 2016/2/19.
 */
public class StepAnalyzeActivity extends Activity {
    TextView analyze;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stepanalyze);
        analyze = (TextView)findViewById(R.id.textView2);
        String text = "Your BMI score is ##. The recommended steps is ** [ based on your BMI, age and gender] Your average steps in the past 7 days is 8000, which is 20% below the recommendation.  We are suggesting to take more exercise as a sedentary lifestyle increases the risk for heart disease, diabetes, high blood pressure, etc.";
        analyze.setText(text);
    }
}
