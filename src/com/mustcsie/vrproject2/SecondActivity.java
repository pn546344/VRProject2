package com.mustcsie.vrproject2;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SecondActivity extends Activity implements OnClickListener {
	String bigPoint;
	CameraView cView;
	TagView tView;
	TextView tv;
	ImageView im;
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
		im = (ImageView)findViewById(R.id.imageView1);
		im.setOnClickListener(this);
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
	protected void onPause() {
		// TODO Auto-generated method stub
		tView.destory();
		tView = null;
		cView = null;
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		finish();
		super.onDestroy();
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		cView.takePicture();
	}

}
