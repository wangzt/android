package com.tomsky.gldemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tomsky.gldemo.view.AqiBallView;

public class ImageActivity extends Activity {

	private AqiBallView ballView;
	
	private static final int BALL_MODE_NORMAL = 0;
	private static final int BALL_MODE_READY = 1;
	private static final int BALL_MODE_CLEAN = 2;
	
	private int ballMode = BALL_MODE_NORMAL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_layout);
		
		
		ballView = (AqiBallView) findViewById(R.id.ball_view);
		ballView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ballMode == BALL_MODE_NORMAL) {
					ballView.startReadyAnimation();
					ballMode = BALL_MODE_READY;
				} else if (ballMode == BALL_MODE_READY) {
					ballView.stopReadyAnimation();
					ballMode = BALL_MODE_NORMAL;
				}
			}
		});
		
		Button cleanBtn = (Button)findViewById(R.id.clean_btn);
		cleanBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ballView.startClean();
			}
		});
		
		Button cleanBtn2 = (Button)findViewById(R.id.clean_btn2);
		cleanBtn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				ballView.startClean();
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (ballView != null) {
			ballView.stopReadyAnimation();
		}
		ballMode = BALL_MODE_NORMAL;
	}
}
