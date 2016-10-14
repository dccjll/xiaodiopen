package com.dsm.xiaodiopen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dessmann on 16/10/13.
 * 主页
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView mainListView = (ListView) findViewById(R.id.mainListView);
        final List<Map<String, String>> mainList = new ArrayList<>();

        Map<String, String> mainData1 = new HashMap<>();
        mainData1.put("deviceName", "德施曼-3楼前门");
        mainData1.put("deviceMac", "C3:FA:58:31:80:65");
        mainData1.put("deviceType", "13");
        mainList.add(mainData1);

        Map<String, String> mainData2 = new HashMap<>();
        mainData2.put("deviceName", "德施曼-3楼后门");
        mainData2.put("deviceMac", "CA:12:9E:22:2D:87");
        mainData2.put("deviceType", "13");
        mainList.add(mainData2);

        Map<String, String> mainData3 = new HashMap<>();
        mainData3.put("deviceName", "德施曼-1楼前门");
        mainData3.put("deviceMac", "C5:22:AB:EB:46:46");
        mainData3.put("deviceType", "13");
        mainList.add(mainData3);

        Map<String, String> mainData4 = new HashMap<>();
        mainData4.put("deviceName", "T700_A6EB");
        mainData4.put("deviceMac", "ED:83:5C:50:A6:EB");
        mainData4.put("deviceType", "11");
        mainList.add(mainData4);

        Map<String, String> mainData5 = new HashMap<>();
        mainData5.put("deviceName", "T700_978E");
        mainData5.put("deviceMac", "FE:0A:A3:D2:97:8E");
        mainData5.put("deviceType", "11");
        mainList.add(mainData4);

        mainListView.setAdapter(new SimpleAdapter(this, mainList, android.R.layout.simple_list_item_2, new String[]{"deviceName", "deviceType"}, new int[]{android.R.id.text1, android.R.id.text2}));

        mainListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        );
    }
}
