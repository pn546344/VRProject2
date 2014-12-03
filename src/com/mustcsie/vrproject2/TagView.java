package com.mustcsie.vrproject2;

import java.util.GregorianCalendar;
import java.util.LinkedList;

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
import android.os.Bundle;
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
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
	private Canvas canvas;
	private TagData tag ;
	private float senserAngleData=0;
	private Location loc = new Location("");
	private Location myLoc = new Location("");
	private LinkedList<TagDetail> tagDetailList = new LinkedList<TagDetail>();
	private ScrollView contentLayout;
	private TextView tv , tvName , tvClass;    //tv�O���I���e������ , tvName�O���I�W�� , tvClass�O���I���O
	final AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
	final AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
	private Matrix matrix = new Matrix();
	
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
			lManager.requestLocationUpdates(bestGPS, 10000, 100, this);
			//��bestGPS�w���k,10�����w��@�� , �ζW�L100���ةw��@�� 
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
					Log.i("fff", "tagDetailList ID ="+tagDetailList.get(i).getId());
					String str="";
					str = dataList.get(i).getContent();		//������U�ؼЪ����e��r
					String str1 = "";
					str1 = dataList.get(i).getName();
//					String strClass="" ;
//					strClass = dataList.get(i).getArea();
					
					
					tv.setText(str);  //�]�w²����r
					tvName.setText(str1);
//					tvClass.setText(strClass);
					alphaAnimation.setDuration(1000);
					contentLayout.startAnimation(alphaAnimation);
					contentLayout.setVisibility(View.VISIBLE);
					contentLayoutState = true;
					
				}
			}
			break;

		}
		return super.onTouchEvent(event);
	}
	
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
				
			/*	�@�o
			 * //�]�w�O�_��ܹϼh
				String areaNO = tag.getArea();				
				if (areaNO.equals("0") && !area1) {
					tagDetailList.get(i).setIsSurvival(false);
					continue;
				}else if(areaNO.equals("1") && !area2){
					tagDetailList.get(i).setIsSurvival(false);
					continue;
				}else if (areaNO.equals("2") && !area3) {
					tagDetailList.get(i).setIsSurvival(false);
					continue;
				}*/
				
				Bitmap tagImage = tag.getImage();
				if(tagImage == null)
					tagImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher); //�p�G�S������,��R.drawable.ic_Launcher�N��
				loc.setLatitude(tag.getLatitude());
				loc.setLongitude(tag.getLongitude());
				angle = (int) (currentDegree-myLoc.bearingTo(loc));
				float dest =  myLoc.distanceTo(loc)*10;
				int w = tagImage.getWidth();
				int h = tagImage.getHeight();
				tagImage = Bitmap.createBitmap(tagImage,0,0,w,h,matrix,true);
				/*���p�q�lù�L������-�ؼЪ�����쨤>180,�N���ؼЧڻݭn���ॻ��W�L�b��,�]���i�H�P�w���骺��m�b�t�~�@��
				�]���N��X������-360���o�ϦV���઺����
				angle ���צp�G���� , �N����b���骺���� , �Ϥ��p�G���׬��t , �N����b���骺����
				*/
				if(angle > 180 )		
					angle -= 360;    
				canvas.drawBitmap(tagImage, (float) (scanWidth/2-angle*10),-(dest-1080),null); //�elogo
				
				float x = tagImage.getWidth();
				float y = tagImage.getHeight();
				TagDetail tagDetail = new TagDetail(i, (float) (scanWidth/2-angle*10),
						(float) (scanWidth/2-angle*10)+x, -(dest-1080), -(dest-1080)+y);
				
				if(tagDetailList.size()<=dataList.size()){
					tagDetailList.add(tagDetail);
				}
				else
				{
					tagDetailList.get(i).setX0((float) (scanWidth/2-angle*10));
					tagDetailList.get(i).setX1((float) (scanWidth/2-angle*10)+x);
					tagDetailList.get(i).setY0(-(dest-1080));
					tagDetailList.get(i).setY1(-(dest-1080)+y);
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
	}
	
	public void setScanHeight(int heightPixels) {
		// TODO Auto-generated method stub
		scanHeight = heightPixels;
	}
	
	public void setScanWidth(int widthPixels) {
		// TODO Auto-generated method stub
		scanWidth = widthPixels;
	}
	
	public void changeArea1State() {
		area1 = !area1;
		Log.i("fff", "area1="+area1);
	}
	
	public void changeArea2State() {
		area2 = !area2;
		Log.i("fff", "area2="+area2);
	}

	public void changeArea3State() {
		area3 = !area3;
		Log.i("fff", "area3="+area3);
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
	
	public void setMatrixZoomIn() {
		matrix.postScale(2, 2);
	}
	
	public void setMatrixZoomOut() {
		matrix.postScale(0.5f, 0.5f);
	}
}
