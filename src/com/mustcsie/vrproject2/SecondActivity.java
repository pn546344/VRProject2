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
import android.util.MonthDisplayHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class SecondActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {
	String bigPoint;
	CameraView cView;
	TagView tView;
	TextView tvContent , tvName , tvClass;
	ImageView im,area1 , closeimage ;
	LinkedList<TagData> dataList = new LinkedList<TagData>();
	private boolean is_exit = false;
	private boolean area1Close = false , area2Close = false , area3Close = false;
	float upX , upY , downX , downY;
	SeekBar seekBar;
	ScrollView propertyView ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//設定顯示畫面(全螢幕)
		getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,                  
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_second);
		
		//宣告變數
		cView = (CameraView)findViewById(R.id.cameraView1);  //照相機
		tView = (TagView)findViewById(R.id.tagView1);		//畫圖畫面
		tView.getHolder().setFormat(PixelFormat.TRANSLUCENT);	//設置畫面背景為透明
//		tView.setZOrderOnTop(true);
		tView.setZOrderMediaOverlay(true);
		im = (ImageView)findViewById(R.id.imageView1);
//		area1 = (ImageView)findViewById(R.id.imageView2);
		tvName = (TextView)findViewById(R.id.textView1);
		tvContent = (TextView)findViewById(R.id.textView5);
		tvClass = (TextView)findViewById(R.id.textView3);
		closeimage = (ImageView)findViewById(R.id.imageView5);
		seekBar = (SeekBar)findViewById(R.id.seekBar1);
		propertyView = (ScrollView)findViewById(R.id.scrollView1);
		
		
		
		
		
		ScrollView lLayout = (ScrollView)findViewById(R.id.myLayout);
		lLayout.setVisibility(View.GONE);
		tView.setContentLayout(lLayout);
		tView.setTextContent(tvContent);
		tView.setTextName(tvName);
		tView.setTextClass(tvClass);
		seekBar.setOnSeekBarChangeListener(this);
		propertyView.setVisibility(View.GONE);
		
		
		im.setOnClickListener(this);
//		area1.setOnClickListener(this);
	
		closeimage.setOnClickListener(this);
		
		
		
		
		
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
	

		case R.id.imageView5:
			tView.closeTextContent();
			break;
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float xx = event.getX();
		float yy = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			Log.i("ttt", "downX = "+downX);
			Log.i("ttt", "downY = "+downY);
			return true;
		case MotionEvent.ACTION_UP:
			upX = event.getX();
			upY = event.getY();
			Log.i("ttt", "UPX = "+xx);
			Log.i("ttt", "UpY = "+yy);
			return true;
		case MotionEvent.ACTION_MOVE:
			Log.d("onTouchEvent-ACTION_UP","UP");
            upX = event.getX();
            upY = event.getY();
            float x=Math.abs(upX-downX);
            float y=Math.abs(upY-downY);
            double z=Math.sqrt(x*x+y*y);
            int jiaodu=Math.round((float)(Math.asin(y/z)/Math.PI*180));//角度
             
            if (upY < downY && jiaodu>45) {//上
                Log.d("onTouchEvent-ACTION_UP","角度:"+jiaodu+", 動作:上");
            }else if(upY > downY && jiaodu>45) {//下
                Log.d("onTouchEvent-ACTION_UP","角度:"+jiaodu+", 動作:下");
            }else if(upX < downX && jiaodu<=45) {//左
                Log.d("onTouchEvent-ACTION_UP","角度:"+jiaodu+", 動作:左");
                // 原方向不是向右時，方向轉右
                if(downX >= 0 && downX <800)
                	propertyView.setVisibility(View.GONE);
            }else if(upX > downX && jiaodu<=45) {//右
                Log.d("onTouchEvent-ACTION_UP","角度:"+jiaodu+", 動作:右");
                // 原方向不是向左時，方向向右
                if(downX >= 0 && downX <100)
                	propertyView.setVisibility(View.VISIBLE);
            }
            
            return true;
		}
//		return super.onTouchEvent(event);
		return true;
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

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		TextView tv = (TextView)findViewById(R.id.textView7);
		if(arg1 == 0)
			arg1 = 1;
		tv.setText(arg1*100+"M");
		tView.setZoom(arg1);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// 當SeekBar被使用者點選做調整時,此方法會被執行
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// 當SeekBar被使用者停止調整時,此方法會被執行
		
	}
	

}
