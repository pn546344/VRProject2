package com.mustcsie.vrproject2;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class CameraView extends SurfaceView implements Callback,AutoFocusCallback{

	SurfaceHolder holder;
	Context context;
	Camera camera;
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
	}
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		
	}
}
