package com.mustcsie.vrproject2;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class SecondActivity extends Activity {
	String bigPoint;
	CameraView cView;
	TagView tView;
	LinkedList<TagData> dataList = new LinkedList<TagData>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,                  
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_second);
		cView = (CameraView)findViewById(R.id.cameraView1);
		tView = (TagView)findViewById(R.id.tagView1);
		tView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		tView.setZOrderOnTop(true);
		Intent intent = getIntent();
		bigPoint = intent.getStringExtra("BigPoint");  //取得大項的名稱
		GetSmallJson sJson = new GetSmallJson(bigPoint);
		sJson.start();
		try {
			sJson.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataList = sJson.getList();
		tView.setTagDataList(dataList);
		
		
		DisplayMetrics metrics = new DisplayMetrics(); 
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.i("ttt", "螢幕寬"+metrics.widthPixels);
        Log.i("ttt", "螢幕高"+metrics.heightPixels);
        tView.setScanHeight(metrics.heightPixels);	//將螢幕高傳遞給tView
        tView.setScanWidth(metrics.widthPixels); 		//將螢幕寬傳遞給tView
        
        tView.resume();
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		tView.destory();
		tView = null;
		finish();
		super.onDestroy();
		
	}

}
