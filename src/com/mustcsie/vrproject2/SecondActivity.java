package com.mustcsie.vrproject2;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.ListIterator;

import com.google.android.gms.internal.bu;

import android.R.animator;
import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MonthDisplayHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
	ImageView im,backProperty , closeimage;
	LinkedList<TagData> dataList = new LinkedList<TagData>();
	LinkedList<ButtonStatus> buttonlist = new LinkedList<ButtonStatus>(); //屬性欄button的狀態
	LinkedList<PropertyData> propertyList = new LinkedList<PropertyData>();
	private int zoom = 0 , beforeZoom = 0;
	private boolean is_exit = false;
	private boolean area1Close = false , area2Close = false , area3Close = false;
	float upX , upY , downX , downY;
	SeekBar seekBar;
	ScrollView propertyView ;
	LinearLayout propertyLinearView , smallPhoto;
	ImageView itemPhoto1 , itemPhoto2 ;

	
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
		im.setVisibility(View.GONE);
		tvName = (TextView)findViewById(R.id.textView1);
		tvContent = (TextView)findViewById(R.id.textView5);
		tvClass = (TextView)findViewById(R.id.textView3);
		closeimage = (ImageView)findViewById(R.id.imageView5);
		seekBar = (SeekBar)findViewById(R.id.seekBar1);
		propertyView = (ScrollView)findViewById(R.id.scrollView1); //屬性視窗變數
		propertyLinearView = (LinearLayout)findViewById(R.id.propertyLayout);
		smallPhoto = (LinearLayout)findViewById(R.id.smallPhoto);
		itemPhoto1 = (ImageView)findViewById(R.id.itemPhoto1);
		itemPhoto2 = (ImageView)findViewById(R.id.itemPhoto2);
		
		ScrollView lLayout = (ScrollView)findViewById(R.id.myLayout);
		lLayout.setVisibility(View.GONE);
		tView.setContentLayout(lLayout);
		tView.setTextContent(tvContent);
		tView.setTextName(tvName);
		tView.setTextClass(tvClass);
		tView.setItemPhoto1(itemPhoto1);
		tView.setItemPhoto2(itemPhoto2);
		seekBar.setOnSeekBarChangeListener(this);
		propertyView.setVisibility(View.GONE);
		
		im.setOnClickListener(this);
		closeimage.setOnClickListener(this);
		
		
		Intent intent = getIntent();
		bigPoint = intent.getStringExtra("BigPoint");  //取得大項的名稱
		tView.setBigPoint(bigPoint);
		GetSmallJson sJson = new GetSmallJson(bigPoint);
		GetPropertyJson propertyJson = new GetPropertyJson(bigPoint);
		propertyJson.start();
		sJson.start();
		try {
			sJson.join();
			propertyJson.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataList = sJson.getList();
		propertyList = propertyJson.getList();  //取得屬性資料
		
		for (int i = 0; i < propertyList.size(); i++) {
			TextView abc = new TextView(this);
			abc.setText(propertyList.get(i).getName());
			abc.setTextSize(20);
			final ImageView ibutton = new ImageView(this);
			ibutton.setImageBitmap(propertyList.get(i).getOnBitmap());
			ButtonStatus bStatus = new ButtonStatus(propertyList.get(i).getName(), true);
			buttonlist.add(bStatus);
			ibutton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			ibutton.setAdjustViewBounds(true);
			ibutton.setMaxHeight(200);
			ibutton.setMaxWidth(200);
			ibutton.setId(i);
			ibutton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.i("ttt", "id = "+v.getId());
					if (buttonlist.get(v.getId()).getStatus() == true) {
						ibutton.setImageBitmap(propertyList.get(v.getId()).getOffBitmap());
						buttonlist.get(v.getId()).setStatus();
						tView.setButtonStatusList(buttonlist);
					}
					else
					{
						ibutton.setImageBitmap(propertyList.get(v.getId()).getOnBitmap());
						buttonlist.get(v.getId()).setStatus();
						tView.setButtonStatusList(buttonlist);
					}
					
				}
			});
			Log.i("fff", "ibutton id = "+ibutton.getId());
			propertyLinearView.addView(abc);
			propertyLinearView.addView(ibutton);
				
		}
		
		
		
		DisplayMetrics metrics = new DisplayMetrics(); 
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.i("ttt", "螢幕寬"+metrics.widthPixels);
        Log.i("ttt", "螢幕高"+metrics.heightPixels);
        tView.setScanHeight(metrics.heightPixels);	//將螢幕高傳遞給tView
        tView.setScanWidth(metrics.widthPixels); 		//將螢幕寬傳遞給tView
        
		tView.setButtonStatusList(buttonlist);
		tView.setSmallPhoto(smallPhoto);
		
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
                if(downX >= 0 && downX <800){
                	Animation am = new TranslateAnimation(0, -500, 0, 0);
        			am.setDuration(200);
        			propertyView.setAnimation(am);
        			propertyView.setVisibility(View.GONE);
                }
            }else if(upX > downX && jiaodu<=45) {//右
                Log.d("onTouchEvent-ACTION_UP","角度:"+jiaodu+", 動作:右");
                // 原方向不是向左時，方向向右
                if(downX >= 0 && downX <100){
                	Animation am = new TranslateAnimation(-500, 0, 0, 0);
        			am.setDuration(200);
        			propertyView.setAnimation(am);
        			propertyView.setVisibility(View.VISIBLE);
                }
                	
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
	
		zoom = arg1;
		Log.i("ttt", "zoom = "+zoom);
		if (arg1 == 0) {
			tv.setText(100+"M");
		}else
		tv.setText(arg1*100+"M");
		tView.setZoom(arg1);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// 當SeekBar被使用者點選做調整時,此方法會被執行
		Log.i("aaa", "onStartTrackingTouch");
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// 當SeekBar被使用者停止調整時,此方法會被執行
		Log.i("aaa", "onStopTrackingTouch");
		if (beforeZoom < zoom) {
			tView.setMatrixZoomOut(zoom-beforeZoom);//縮小
		}else if (beforeZoom>zoom) {
				tView.setMatrixZoomIn(beforeZoom-zoom);
		}
		beforeZoom = zoom;
	}
	
		

}
