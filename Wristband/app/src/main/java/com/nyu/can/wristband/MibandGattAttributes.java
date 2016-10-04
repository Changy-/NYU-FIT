/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nyu.can.wristband;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class MibandGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("00001802-0000-1000-8000-00805f9b34fb", "Immediate Alert");
        attributes.put("0000fee0-0000-1000-8000-00805f9b34fb","Mi Service 1");
        attributes.put("0000fee1-0000-1000-8000-00805f9b34fb","Mi Service 2");
        attributes.put("0000fee7-0000-1000-8000-00805f9b34fb","Mi Service 3");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("0000ff01-0000-1000-8000-00805f9b34fb", "Device_Info");
        attributes.put("0000ff02-0000-1000-8000-00805f9b34fb", "Device_Name");
        attributes.put("0000ff03-0000-1000-8000-00805f9b34fb","Notification");
        attributes.put("0000ff04-0000-1000-8000-00805f9b34fb","User_Info");
        attributes.put("0000ff05-0000-1000-8000-00805f9b34fb","Control_Point");
        attributes.put("0000ff06-0000-1000-8000-00805f9b34fb","Realtime_Steps");
        attributes.put("0000ff07-0000-1000-8000-00805f9b34fb","Activity_Data");
        attributes.put("0000ff08-0000-1000-8000-00805f9b34fb","Firmware_Data");
        attributes.put("0000ff09-0000-1000-8000-00805f9b34fb","Le_Params");
        attributes.put("0000ff0a-0000-1000-8000-00805f9b34fb", "Date_Time");
        attributes.put("0000ff0b-0000-1000-8000-00805f9b34fb", "Statistics");
        attributes.put("0000ff0c-0000-1000-8000-00805f9b34fb", "Battery");
        attributes.put("0000ff0d-0000-1000-8000-00805f9b34fb","Test");
        attributes.put("0000ff0e-0000-1000-8000-00805f9b34fb","Sensor_data");
        attributes.put("00002a06-0000-1000-8000-00805f9b34fb", "Alert_level");


    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
