package com.mustcsie.vrproject2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.bu;
import com.google.android.gms.internal.ho;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class TagView extends SurfaceView implements	Runnable, LocationListener, SensorEventListener{

	private SurfaceHolder holder;
	private Context context	;
	private boolean stop = false;
	private Thread t;
	private LocationManager lManager;
	private String bestGPS = null;
	private double scanHeight=0,scanWidth=0 , angle=0;	//�ù���,�ù��e  , �ù���
	private double latiude=0,longitude=0;	//�n��,�g��
	private boolean loopStop = false , area1 = true ,area2 = true ,area3 = true ,contentLayoutState = false;
	private SensorManager sm ; // ��V�P���� 
	private float currentDegree = 0f;  //�q�lù�L�����ܼ�
	private LinkedList<TagData> dataList;
	private LinkedList<ButtonStatus> buttonStatus;
	private Canvas canvas;
	private TagData tag ;
	private float senserAngleData=0;
	private Location loc = new Location("");
	private Location myLoc = new Location("");
	private LinkedList<TagDetail> tagDetailList = new LinkedList<TagDetail>(); //�ؼЦb�ù��W����m
	private LinkedList<DeviceTagData> deviceTag = new LinkedList<DeviceTagData>(); //tag���ݩ�
	private ScrollView contentLayout;
	private TextView tv , tvName , tvClass;    //tv�O���I���e������ , tvName�O���I�W�� , tvClass�O���I���O
	final AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
	final AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
	private Matrix matrix = new Matrix();
	private float startSensor = 0.0f;
	private int zoom = 10;
	private LinearLayout smallPhoto;
	private String bigPoint = "";
	private ImageView itemPhoto1 , itemPhoto2;  //icon²������
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				itemPhoto1.setImageBitmap((Bitmap)msg.obj);
				
			}
			if (msg.what == 2) {
				itemPhoto2.setImageBitmap((Bitmap)msg.obj);
			}
		};
	};
	
	public TagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		holder = getHolder();
		
		
		DisplayMetrics metrics = new DisplayMetrics();		//���o�ù��ؤo
		WindowManager wManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		wManager.getDefaultDisplay().getMetrics(metrics);
		scanHeight = metrics.heightPixels;					//���o�ù���
		scanWidth  = metrics.widthPixels;					//���o�ù��e
		
		lManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);		//GPS
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		bestGPS = lManager.getBestProvider(criteria, true);
		if(bestGPS != null){
			Location loc = lManager.getLastKnownLocation(bestGPS);
			showLocation(loc);
			lManager.requestLocationUpdates(bestGPS, 1000, 5, this);
			//��bestGPS�w���k,1�����w��@�� , �ζW�L5���ةw��@�� 
		}
		
		sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_NORMAL);
	
	
	}

	private void showLocation(Location loc) {
		// TODO Auto-generated method stub
		latiude = loc.getLatitude();
		longitude = loc.getLongitude();
		myLoc.setLatitude(latiude);
		myLoc.setLongitude(longitude);
//		myLoc.setLatitude(24.863918);			//��m�g��
//		myLoc.setLongitude(120.988015);
		Log.i("fff", "latiude ="+latiude);
		Log.i("fff", "longitude ="+longitude);
		
	}
	
	
	protected void resume() {
		t = new Thread(this);
		loopStop = false;
		t.start();
	}
	
	protected void pause() {
		loopStop = true;
		while (!loopStop) {
			try {
				t.join();
				lManager.removeUpdates(this);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float x = event.getX();
			float y = event.getY();
			Log.i("fff", "x ="+x + " y = "+y);
			
			//�P�_�I�諸�ϥܶ��ج���,�O�_�s�b
			for(int i=0;i<tagDetailList.size();i++)
			{
				if((x>tagDetailList.get(i).getX0() && x<tagDetailList.get(i).getX1()) 
						&& 
						(y>tagDetailList.get(i).getY0() && y < tagDetailList.get(i).getY1())
						&& tagDetailList.get(i).getIsSurvival() && contentLayoutState != true)
				{
					itemPhoto1.setImageResource(R.drawable.wait);
					itemPhoto2.setImageResource(R.drawable.wait);
					Log.i("fff", "tagDetailList ID ="+tagDetailList.get(i).getId());
					String str="";
					str = dataList.get(i).getContent();		//������U�ؼЪ����e��r
					String str1 = "";
					str1 = dataList.get(i).getName();
					String[] strClass ;
					strClass = dataList.get(i).getStr();
					String classname ="";
					for (int j = 0; j < strClass.length; j++) {
						if (classname.equals("")) {
							classname = strClass[j];
						}
						else
						classname = classname+","+strClass[j];
					}
					
					tv.setText(str);  //�]�w²����r
					tvName.setText(str1);
					tvClass.setText(classname);
					alphaAnimation.setDuration(1000);
					contentLayout.startAnimation(alphaAnimation);
					contentLayout.setVisibility(View.VISIBLE);
					
					contentLayoutState = true;
					
					
					GetItemPhoto2 itemPhoto = new GetItemPhoto2(bigPoint, str1);
					itemPhoto.start();
					
				    
				}
			}
			break;

		}
		return super.onTouchEvent(event);
	}
	//�]�wTagDataList�����
	public void setTagDataList(LinkedList<TagData> dataList) {
		this.dataList = dataList;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.i("fff", "scanHeight="+scanHeight );
		Log.i("fff", "scanWidth="+scanWidth);
		while(!loopStop)
		{
			if(!holder.getSurface().isValid())
				continue;
				
			canvas = holder.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR); //���s�e����TagView�����z��
			
			Paint myPaint = new Paint();
			myPaint.setColor(Color.RED);
			myPaint.setStrokeCap(Paint.Cap.ROUND);
			Paint myPaint2 = new Paint();
			myPaint2.setColor(Color.GREEN);
			myPaint2.setStrokeCap(Paint.Cap.ROUND);
			myPaint2.setStrokeWidth(10f);
			canvas.drawCircle((float)scanWidth/2, (float)(scanHeight*0.95), (float)40, myPaint);
			canvas.drawLine((float)scanWidth/2, (float)(scanHeight*0.95), (float)scanWidth, (float)(scanHeight*0.95-100), myPaint2);
			canvas.drawLine((float)scanWidth/2, (float)(scanHeight*0.95), (float)0, (float)(scanHeight*0.95-100), myPaint2);
			for(int i=0;i<dataList.size();i++)
			{
				tag = dataList.get(i);
				String[] property = tag.getStr();
				Log.i("ttt", "Str size"+property.length);
				boolean jump = false;
				for (int j = 0 , k = 0; j < buttonStatus.size(); j++) {
					for (int j2 = 0; j2 < property.length; j2++) {
						if (buttonStatus.get(j).getName().equals(property[j2]) 
								&&
								!buttonStatus.get(j).getStatus()) {
							k++;
						}
					}
					if(k == property.length)
					{
						jump= true;
					}
				}
				
				if (jump) {
					continue;  //���e����
				}
				
				Bitmap tagImage = tag.getImage();
				if(tagImage == null)
					tagImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher); //�p�G�S������,��R.drawable.ic_Launcher�N��
				loc.setLatitude(tag.getLatitude());
				loc.setLongitude(tag.getLongitude());
				angle = (int) (currentDegree-myLoc.bearingTo(loc));
//				float dest =  myLoc.distanceTo(loc)*10;
				float dest =  myLoc.distanceTo(loc)*zoom;
				int w = tagImage.getWidth();
				int h = tagImage.getHeight();
				tagImage = Bitmap.createBitmap(tagImage,0,0,w,h,matrix,true);
				/*���p�q�lù�L������-�ؼЪ�����쨤>180,�N���ؼЧڻݭn���ॻ��W�L�b��,�]���i�H�P�w���骺��m�b�t�~�@��
				�]���N��X������-360���o�ϦV���઺����
				angle ���צp�G���� , �N����b���骺���� , �Ϥ��p�G���׬��t , �N����b���骺����
				*/
				if(angle > 180 )		
					angle -= 360;    
				canvas.drawBitmap(tagImage, (float) (scanWidth/2-angle*10),(float)(-(dest-scanHeight)),null); //�elogo
				
				float x = tagImage.getWidth();
				float y = tagImage.getHeight();
				TagDetail tagDetail = new TagDetail(i, (float) (scanWidth/2-angle*10),
						(float) (scanWidth/2-angle*10)+x, (float)(-(dest-scanHeight)), (float)(-(dest-scanHeight)+y));
				
				if(tagDetailList.size()<=dataList.size()){
					tagDetailList.add(tagDetail);
				}
				else
				{
					tagDetailList.get(i).setX0((float) (scanWidth/2-angle*10));
					tagDetailList.get(i).setX1((float) (scanWidth/2-angle*10)+x);
					tagDetailList.get(i).setY0((float)(-(dest-scanHeight)));
					tagDetailList.get(i).setY1((float)(-(dest-scanHeight)+y));
					tagDetailList.get(i).setIsSurvival(true);
				}
				
				

			}

			holder.unlockCanvasAndPost(canvas);
		}
	}
	@Override
	public void onLocationChanged(Location location) {
		// ���m�o�ͧ��ܮ�
		showLocation(location);
	}
	@Override
	public void onProviderDisabled(String provider) {
		// ��������m��������
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// ��ҥΦ�m��������
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// ��w�쪬�A���ܮ�
		
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// �ǷP�����i�s����(��V����)
		if (arg0.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			   float degree = arg0.values[0];
			   float result = 0.0f;
			   
			   if (degree - startSensor < 0) {
				result = (degree - startSensor)*-1;
			}else
				result = degree - startSensor;
			   if (result > 30) {  
				//�p�G�̰ʨS���W�L30��,���ʧ@
				   startSensor = arg0.values[0]	;
			   }else
				   startSensor = degree;
			   if(senserAngleData != degree && (senserAngleData-degree<-30 || senserAngleData-degree>30))
			   {
				   
				   senserAngleData = degree;
			   }
			   /*
			   RotateAnimation���O�G�����ܤưʵe��
			    
			   �Ѽƻ���:
			   fromDegrees�G���઺�}�l���סC
			   toDegrees�G���઺�������סC
			   pivotXType�GX�b�����Y�Ҧ��A�i�H���Ȭ�ABSOLUTE�BRELATIVE_TO_SELF�BRELATIVE_TO_PARENT�C
			   pivotXValue�GX���Ъ����Y�ȡC
			   pivotYType�GY�b�����Y�Ҧ��A�i�H���Ȭ�ABSOLUTE�BRELATIVE_TO_SELF�BRELATIVE_TO_PARENT�C
			   pivotYValue�GY���Ъ����Y�ȡC
			   */
//			   RotateAnimation ra = new RotateAnimation(
//			     currentDegree, // �ʵe�_�l�ɪ��󪺨���
//			     -degree,       // �ʵe�����ɪ�����઺����(�i�j��360��)-��ܰf�ɰw����,+��ܶ��ɰw����
//			     Animation.RELATIVE_TO_SELF, 0.5f, //�ʵe�۹�󪫥�X�y�Ъ��}�l��m, �q0%~100%������, 50%������X��V���ФW�����I��m
//			     Animation.RELATIVE_TO_SELF, 0.5f); //�ʵe�۹�󪫥�Y�y�Ъ��}�l��m, �q0%~100%������, 50%������Y��V���ФW�����I��m

//			   ra.setDuration(200); // ����L�{����ɶ�
//			   ra.setRepeatCount(-1); // �ʵe���Ʀ��� (-1 ��ܤ@������)
//			   img.startAnimation(ra); // ù�L�Ϥ��ϥα���ʵe
			   currentDegree = degree+90; // �O�s����᪺�׼�, currentDegree�O�@�Ӧb�����w�q��float�����ܶq
			   if(currentDegree<=-360)
				   currentDegree = currentDegree+360;
			   
			   }
		
	//	}
	}
	
	public void setScanHeight(int heightPixels) {
		// TODO Auto-generated method stub
		scanHeight = heightPixels;
	}
	
	public void setScanWidth(int widthPixels) {
		// TODO Auto-generated method stub
		scanWidth = widthPixels;
	}
	
	public void setContentLayout(ScrollView layout) {
		contentLayout = layout;
	}
	
	public void setTextContent(TextView tv) {
		this.tv = tv;
	}
	
	public void closeTextContent() {
		//�������еe��
		alphaAnimation2.setDuration(1000);
		contentLayout.startAnimation(alphaAnimation2);
		contentLayout.setVisibility(View.GONE);
		contentLayoutState = false;
	}
	
	public boolean getTextContentState() {
		return contentLayoutState;
	}

	public void setTextName(TextView tvName) {
		// TODO Auto-generated method stub
		this.tvName = tvName	;
	}
	
	public void setTextClass(TextView tvClass) {
		this.tvClass = tvClass;
	}
	
	public void setMatrixZoomIn(int loop) {
		for (int i = 0; i < loop; i++) {
			
			matrix.postScale(1.04f, 1.04f);
		}
	}
	
	public void setMatrixZoomOut(int loop) {
		for (int i = 0; i < loop; i++) {
			matrix.postScale(0.96f, 0.96f);
			
		}
	}
	public void setZoom(int x) {
		//x�ǤJ���Ƚd��1~10

		zoom = 11-x;
	}
	public void setButtonStatusList(LinkedList<ButtonStatus> list) {
		buttonStatus = list;
		Log.d("ttt","change");
	}
	
	public void setSmallPhoto(LinearLayout smallPhoto) {
		this.smallPhoto = smallPhoto;
	}
	
	public void setBigPoint(String bigPoint) {
		this.bigPoint = bigPoint;
	}
	
	public void setItemPhoto1(ImageView item) {
		itemPhoto1 = item;
	}
	
	public void setItemPhoto2(ImageView item) {
		itemPhoto2 = item;
	}
	
	public class GetItemPhoto2 extends Thread{
		private String address = "";
		private String result = "";
		public GetItemPhoto2(String viewname , String devicename)
		{
			try {
				viewname = URLEncoder.encode(viewname,"utf-8");
				devicename = URLEncoder.encode(devicename,"utf-8");
				address = "http://120.105.81.47/login/device_picture.php?viewname="+viewname+"&devicename="+devicename;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				URL url = new URL(address);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				if(connection.getResponseCode()==HttpURLConnection.HTTP_OK)
				{
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(connection.getInputStream(), "utf-8"));
					String str;
					while((str=reader.readLine())!=null)
					{
						result = result +str;
					}
					reader.close();
					connection.disconnect();
					JSONArray jsonArray = new JSONArray(result);
					Log.d("ttt", "jsonarray.length ===="+jsonArray.length());
					for(int i=0;i<jsonArray.length();i++)
					{
							JSONObject json = jsonArray.getJSONObject(i);
							String name , pic1Url,pic2Url;
							pic1Url = json.getString("Device_Picture1");
							SmallBitmap sBitmap = new SmallBitmap(pic1Url);
							
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.obj = sBitmap.getBitmap();
							handler.sendMessage(msg);
							
							pic2Url = json.getString("Device_Picture2");
							SmallBitmap ofBitmap = new SmallBitmap(pic2Url);
							Message msg2 = handler.obtainMessage();
							msg2.what = 2;
							msg2.obj = ofBitmap.getBitmap();
							handler.sendMessage(msg2);
							
							Log.d("ttt", "datalist add 2");
					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("ttt", "datalist add 3");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("ttt", "datalist add 4");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("ttt", "datalist add 5");
			}
			super.run();
		}
	}
	
}

