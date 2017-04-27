package com.ywanhzy.demo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.ywanhzy.demo.AppConfig;
import com.ywanhzy.demo.AppContext;
import com.ywanhzy.demo.R;
import com.ywanhzy.demo.adapter.IndexDataAdapter;
import com.ywanhzy.demo.entity.MenuEntity;
import com.ywanhzy.demo.widget.LineGridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static AppContext appContext;
    private LineGridView gridView;
    private List<MenuEntity> indexDataAll = new ArrayList<MenuEntity>();
    private List<MenuEntity> indexDataList = new ArrayList<MenuEntity>();
    private IndexDataAdapter adapter;
    private final static String fileName = "menulist";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appContext = (AppContext) getApplication();
        Logger
                .init("YwanhzyLog")                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                //.hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);                // default 0
                //.logAdapter(new AndroidLogAdapter()); //default AndroidLogAdapter

        gridView = (LineGridView) findViewById(R.id.gv_lanuch_start);
        gridView.setFocusable(false);
        String strByJson=getJson(this,fileName);

        //Json的解析类对象
        JsonParser parser = new JsonParser();
        //将JSON的String 转成一个JsonArray对象
        JsonArray jsonArray = parser.parse(strByJson).getAsJsonArray();
        Gson gson = new Gson();
        //加强for循环遍历JsonArray
        for (JsonElement indexArr : jsonArray) {
            //使用GSON，直接转成Bean对象
            MenuEntity menuEntity = gson.fromJson(indexArr, MenuEntity.class);
            indexDataAll.add(menuEntity);
        }
        //appContext.delFileData(AppConfig.KEY_All);

        String key = AppConfig.KEY_All;
        String keyUser = AppConfig.KEY_USER;
        appContext.saveObject((Serializable) indexDataAll, AppConfig.KEY_All);

        List<MenuEntity> indexDataUser = (List<MenuEntity>) appContext.readObject(AppConfig.KEY_USER);
        if(indexDataUser==null||indexDataUser.size()==0) {
            appContext.saveObject((Serializable) indexDataAll, AppConfig.KEY_USER);
        }
        indexDataList = (List<MenuEntity>) appContext.readObject(AppConfig.KEY_USER);

        MenuEntity allMenuEntity=new MenuEntity();
        allMenuEntity.setIco("");
        allMenuEntity.setId("all");
        allMenuEntity.setTitle("全部");
        indexDataList.add(allMenuEntity);
        adapter = new IndexDataAdapter(MainActivity.this, indexDataList);
        gridView.setAdapter(adapter);
        Logger.e(strByJson);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                String title = indexDataList.get(position).getTitle();
                String strId = indexDataList.get(position).getId();
                Logger.i(title + strId);
                if (strId.equals("all")) {// 更多
                    intent.setClass(MainActivity.this, MenuManageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e(e.toString());
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        indexDataList.clear();
        indexDataList = (List<MenuEntity>) appContext.readObject(AppConfig.KEY_USER);
        MenuEntity allMenuEntity=new MenuEntity();
        allMenuEntity.setIco("all_big_ico");
        allMenuEntity.setId("all");
        allMenuEntity.setTitle("全部");
        indexDataList.add(allMenuEntity);
        adapter = new IndexDataAdapter(MainActivity.this, indexDataList);
        gridView.setAdapter(adapter);
    }
}
