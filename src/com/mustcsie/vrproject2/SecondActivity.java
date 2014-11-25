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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends Activity implements OnClickListener {
	String bigPoint;
	CameraView cView;
	TagView tView;
	TextView tvContent;
	ImageView im,area1,area2,area3;
	LinkedList<TagData> dataList = new LinkedList<TagData>();
	private boolean is_exit = false;
	private boolean area1Close = false , area2Close = false , area3Close = false;

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
//		tView.setZOrderOnTop(true);
		tView.setZOrderMediaOverlay(true);
		im = (ImageView)findViewById(R.id.imageView1);
		area1 = (ImageView)findViewById(R.id.imageView2);
		area2 = (ImageView)findViewById(R.id.imageView3);
		area3 = (ImageView)findViewById(R.id.imageView4);
		tvContent = (TextView)findViewById(R.id.textView1);
		
		
		LinearLayout lLayout = (LinearLayout)findViewById(R.id.myLayout);
		lLayout.setVisibility(View.GONE);
		tView.setContentLayout(lLayout);
		tView.setTextContent(tvContent);
		
		
		
		
		im.setOnClickListener(this);
		area1.setOnClickListener(this);
		area2.setOnClickListener(this);
		area3.setOnClickListener(this);
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
		switch (arg0.getId()) {
		case R.id.imageView1:
			cView.takePicture();
			break;
		case R.id.imageView2:
			tView.changeArea1State();
			area1Close = !area1Close;
			if(area1Close)
				area1.setImageDrawable(getResources().getDrawable(R.drawable.button1false));
			else
				area1.setImageDrawable(getResources().getDrawable(R.drawable.button1));
			break;
		case R.id.imageView3:
			tView.changeArea2State();
			area2Close = !area2Close;
			if(area2Close)
				area2.setImageDrawable(getResources().getDrawable(R.drawable.button2false));
			else 
				area2.setImageDrawable(getResources().getDrawable(R.drawable.button2));
			break;
		case R.id.imageView4:
			tView.changeArea3State();
			area3Close = !area3Close;
			if(area3Close)
				area3.setImageDrawable(getResources().getDrawable(R.drawable.button3false));
			else 
				area3.setImageDrawable(getResources().getDrawable(R.drawable.button3));
			break;
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
	
	
	
	
/*	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.i("fff", "keydown back");
			tView.closeTextContent();;
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
*/
}
