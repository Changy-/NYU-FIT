package com.nyu.can.wristband;

import java.util.HashMap;

/**
 * Created by Can on 2016/2/20.
 */
public class FitbitGattAttributes {

    private static HashMap<String, String> attributes = new HashMap();

    static{
        //Service
        attributes.put("558dfa00-4fa8-4105-9f02-4eaa93e62980", "FitbitDataService");
        attributes.put("00001800-0000-1000-8000-00805f9b34fb","Generic Access");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb","Battery Service");

        //Characteristic
        attributes.put("558dfa01-4fa8-4105-9f02-4eaa93e62980", "Data");
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb","Battery Level");
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb","Device Name");
    }
}
