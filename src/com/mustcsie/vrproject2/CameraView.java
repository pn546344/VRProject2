package com.mustcsie.vrproject2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Formatter.BigDecimalLayoutForm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
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
import android.os.Environment;
import android.provider.ContactsContract.Directory;
import android.text.Layout.Directions;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraView extends SurfaceView implements Callback,AutoFocusCallback, SensorEventListener{

	SurfaceHolder holder;
	Context context;
	Camera camera;
	ImageView imageView;
	private SensorManager sm;
	private float senserAngleData=0;
	private final String PREFS_NAME="PICTURECOUNT";
	private final String COUNT = "COUNT";
	private float mLastX;                    //x�b��P(Sensor)����
	private float mLastY;                    //y�b��P(Sensor)����
	private float mLastZ;                    //z�b��P(Sensor)����
	private double mSpeed;                 //�ϰʤO�D�ƫ�
	private long mLastUpdateTime;           //Ĳ�o�ɶ�
	//�ϰʤO�D�ƫ׳]�w�� (�ƭȶV�j�ݥϰʶV�j�O�A�ƭȶV�p�����ϰʧY�|Ĳ�o)
	private static final int SPEED_SHRESHOLD = 800;
	//Ĳ�o���j�ɶ�
	private static final int UPTATE_INTERVAL_TIME = 70;
	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		//�ϥ�surfaceholder����surfaceview
		holder=getHolder();
		holder.addCallback(this);
		
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		camera.startPreview();					//�ҥά۾��^���e��
		camera.autoFocus(this);					//�۰ʹ�J(�u�|��J�@��)
		sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			camera = Camera.open();				//�}�ҷӬ۾�
			camera.setPreviewDisplay(holder);	//�]�w�۾����W����ܦ�m
			camera.setDisplayOrientation(0);	//�]�w�^�����e�����ਤ��
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();					//����۾��^���e��
		camera.release();						//����۾��귽
		sm.unregisterListener(this);
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
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && camera != null)
		{
			//��eĲ�o�ɶ�
            long mCurrentUpdateTime = System.currentTimeMillis();
            
            //Ĳ�o���j�ɶ� = ��eĲ�o�ɶ� - �W��Ĳ�o�ɶ�
            long mTimeInterval = mCurrentUpdateTime - mLastUpdateTime;

            //�YĲ�o���j�ɶ�< 70 �hreturn;
            if (mTimeInterval < UPTATE_INTERVAL_TIME) return;
			
            mLastUpdateTime = mCurrentUpdateTime;
			//���oxyz��P(Sensor)����
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
          //�ϰʰ����t�� = xyz��P(Sensor)���� - �W��xyz��P(Sensor)����
            float mDeltaX = x - mLastX;
            float mDeltaY = y - mLastY;
            float mDeltaZ = z - mLastZ;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            //��P(Sensor)�ϰʤO�D�t�פ���
            mSpeed = Math.sqrt(mDeltaX * mDeltaX + mDeltaY * mDeltaY + mDeltaZ * mDeltaZ)/ mTimeInterval * 10000;
			
          //�Y��P(Sensor)�ϰʳt�פj�󵥩�ϰʳ]�w�ȫh�i�J (�F��ϰʤO�D�γt��)
            if (mSpeed >= SPEED_SHRESHOLD)
            {
                    //�F��n�@�n�ϰʫ�n�����Ʊ�
            	camera.autoFocus(this);
                    Log.i("fff","�n�@�n��...");
            } 
            
            
            
			//camera.autoFocus(this);
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
		int count;
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i("fff", "jpeg");
			SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			if (sp.getInt(COUNT, 0) == 0) {
				Editor editor = sp.edit();
				editor.putInt(COUNT, 1);
				count =1;
				editor.commit();
			}
			else
			{
				Editor editor = sp.edit();
				count = sp.getInt(COUNT, 0);
				editor.putInt(COUNT, count+1);
				editor.commit();
			}
			
			String filename = "APP"+count+".jpg";
			
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),filename);
				try {
					Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
					
					OutputStream os = new FileOutputStream(file,true);
					bmp.compress(CompressFormat.JPEG, 100, os);
//					os.write(bmp);
					os.flush();
					os.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Toast.makeText(context, "�Ӥ��H�x�s", Toast.LENGTH_SHORT).show();
		}
	};
	
}
