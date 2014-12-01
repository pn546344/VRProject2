package com.mustcsie.vrproject2;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class SecondActivity extends Activity implements OnClickListener {
	String bigPoint;
	CameraView cView;
	TagView tView;
	TextView tvContent , tvName , tvClass;
	ImageView im,area1,area2,area3 , closeimage;
	LinkedList<TagData> dataList = new LinkedList<TagData>();
	private boolean is_exit = false;
	private boolean area1Close = false , area2Close = false , area3Close = false;
	private ZoomControls zoomBar;
	LinkedList<ImageItemButton> buttonDataList = new LinkedList<ImageItemButton>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,                  
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_second);
		cView = (CameraView)findViewById(R.id.cameraView1);
		tView = (TagView)findViewById(R.id.tagView1);
		tView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//		tView.setZOrderOnTop(true);
		tView.setZOrderMediaOverlay(true);
		im = (ImageView)findViewById(R.id.imageView1);
		area1 = (ImageView)findViewById(R.id.imageView2);
		area2 = (ImageView)findViewById(R.id.imageView3);
		area3 = (ImageView)findViewById(R.id.imageView4);
		tvName = (TextView)findViewById(R.id.textView1);
		tvContent = (TextView)findViewById(R.id.textView5);
		tvClass = (TextView)findViewById(R.id.textView3);
		closeimage = (ImageView)findViewById(R.id.imageView5);
		zoomBar = (ZoomControls)findViewById(R.id.zoomControls1);
		
		
		ScrollView lLayout = (ScrollView)findViewById(R.id.myLayout);
		lLayout.setVisibility(View.GONE);
		tView.setContentLayout(lLayout);
		tView.setTextContent(tvContent);
		tView.setTextName(tvName);
		tView.setTextClass(tvClass);
		
		
		
		im.setOnClickListener(this);
		area1.setOnClickListener(this);
		area2.setOnClickListener(this);
		area3.setOnClickListener(this);
		closeimage.setOnClickListener(this);
		zoomBar.setOnZoomInClickListener(new View.OnClickListener(){
			//比例尺放大
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("fff", "zoomin");
				tView.setMatrixZoomIn();
			}
			
		});
		zoomBar.setOnZoomOutClickListener(new View.OnClickListener() {
			//比例尺縮小
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("fff", "zoomout");
				tView.setMatrixZoomOut();
			}
		});
		
		Intent intent = getIntent();
		bigPoint = intent.getStringExtra("BigPoint");  //取得大項的名稱
		GetSmallJson sJson = new GetSmallJson(bigPoint);
		sJson.start();
		GetImageButton gImageButton = new GetImageButton(bigPoint);
		gImageButton.start();
		try {
			sJson.join();
			gImageButton.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataList = sJson.getList();
		buttonDataList = gImageButton.getDataList();
		
		
		area1.setImageBitmap(buttonDataList.getFirst().getEnableImage());
		area2.setImageBitmap(buttonDataList.get(1).getEnableImage());
		area3.setImageBitmap(buttonDataList.get(2).getEnableImage());
		
		DisplayMetrics metrics = new DisplayMetrics(); 
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.i("ttt", "螢幕寬"+metrics.widthPixels);
        Log.i("ttt", "螢幕高"+metrics.heightPixels);
        tView.setScanHeight(metrics.heightPixels);	//將螢幕高傳遞給tView
        tView.setScanWidth(metrics.widthPixels); 		//將螢幕寬傳遞給tView
        
		
		
	}
	
	@Override
	protected void onResume() {
		
		tView.setTagDataList(dataList);
		tView.resume();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		//活動暫停
		tView.pause();
		cView = null;
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// 活動銷毀
		tView = null;
		cView = null;
		finish();
		super.onDestroy();
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.imageView1:
			cView.takePicture();
			break;
		case R.id.imageView2:
			tView.changeArea1State();
			area1Close = !area1Close;
			if(area1Close)
				area1.setImageBitmap(buttonDataList.getFirst().getDisableImage());
			else
				area1.setImageBitmap(buttonDataList.getFirst().getEnableImage());
			break;
		case R.id.imageView3:
			tView.changeArea2State();
			area2Close = !area2Close;
			if(area2Close)
				area2.setImageBitmap(buttonDataList.get(1).getDisableImage());
			else 
				area2.setImageBitmap(buttonDataList.get(1).getEnableImage());
			break;
		case R.id.imageView4:
			tView.changeArea3State();
			area3Close = !area3Close;
			if(area3Close)
				area3.setImageBitmap(buttonDataList.get(2).getDisableImage());
			else 
				area3.setImageBitmap(buttonDataList.get(2).getEnableImage());
			break;
		case R.id.imageView5:
			tView.closeTextContent();
		}
		
		
	}
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	  boolean returnValue = false;
	  if (keyCode == KeyEvent.KEYCODE_BACK && tView.getTextContentState()==true) {
			Log.i("fff", "keydown back");
			tView.closeTextContent();
			return false;
		}
	  if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0 && is_exit == false){
	   Toast.makeText(this, "再點一下返回鍵返回主頁", Toast.LENGTH_SHORT).show();
	   is_exit = true;
	   //一開始 先設定 返回的 flag = true ,若使用者兩秒內沒有動作，則將該 flag 恢復為 false
	   new Thread(new Runnable() {
	    public void run() {
	     try {
	      Thread.sleep(2000);
	      is_exit = false;
	     } catch (InterruptedException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	     }
	    }
	   }).start();
	   
	   returnValue = true;   
	  }else{
	   returnValue = super.onKeyDown(keyCode, event);
	  }
	  return returnValue;
	 }
	

}
