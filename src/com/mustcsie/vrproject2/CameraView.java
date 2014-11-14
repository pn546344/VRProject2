package com.mustcsie.vrproject2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.ImageView;

public class CameraView extends SurfaceView implements Callback,AutoFocusCallback, SensorEventListener{

	SurfaceHolder holder;
	Context context;
	Camera camera;
	ImageView imageView;
	private SensorManager sm;
	private float senserAngleData=0;
	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		//使用surfaceholder控制surfaceview
		holder=getHolder();
		holder.addCallback(this);
		
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		camera.startPreview();					//啟用相機擷取畫面
		camera.autoFocus(this);					//自動對焦(只會對焦一次)
		sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_FASTEST);
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			camera = Camera.open();				//開啟照相機
			camera.setPreviewDisplay(holder);	//設定相機視頻的顯示位置
			camera.setDisplayOrientation(0);	//設定擷取的畫面旋轉角度
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();					//停止相機擷取畫面
		camera.release();						//釋放相機資源
	}
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		Log.i("ddd", "onAutoFocus");		
	}
	
	public void doAutoFocus() {
		camera.autoFocus(this);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
		{
			float degree = event.values[0];
			float degree1 = event.values[1];
			float degree2 = event.values[2];
			if(senserAngleData != degree && (senserAngleData-degree<-30 || senserAngleData-degree>30))
			   {
				   senserAngleData = degree;
				   Log.i("ddd", "Camera View degree"+degree);
				   camera.autoFocus(this);
			   }
		}
	}

	public void takePicture() {
		if(camera !=null)
		{
			camera.autoFocus(this);
			camera.takePicture(shutter, raw, jpeg);
			camera.startPreview();
		}
			
		
	}
	
	private void savePicture() {
		
	}
	
	private ShutterCallback shutter = new ShutterCallback() {
		
		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			Log.i("fff", "shutter");
		}
	};
	
	private PictureCallback raw = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i("fff", "row");
		}
	};
	
	private PictureCallback jpeg = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i("fff", "jpeg");
		}
	};
	
}
