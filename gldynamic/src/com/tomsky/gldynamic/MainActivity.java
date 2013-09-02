package com.tomsky.gldynamic;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.Spinner;


public class MainActivity extends Activity {

	private GLSurfaceView mGLView;
	
	private String[] arrays;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mGLView = new GLSurfaceView(this);
		mGLView.setEGLConfigChooser(false);
		mGLView.setRenderer(new WeatherRenderer());
		mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); //必须在setRenderer之后
		setContentView(R.layout.activity_main);
		
		FrameLayout fLayout = (FrameLayout) findViewById(R.id.background_layout);
		fLayout.addView(mGLView);
		register();
	}

	private void register() {
		
		CheckBox dayCheck = (CheckBox)findViewById(R.id.dayCheck);
		dayCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
		});
		CheckBox dynamicCheck = (CheckBox)findViewById(R.id.dynamicCheck);
//		dynamicCheck.setChecked(AnimationUtil.isDynamic());
		dynamicCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
//					mSurfaceView.startAnimation();
				} else {
//					mSurfaceView.stopAnimation();
				}
			}
		}); 
		
		arrays = getResources().getStringArray(R.array.weather_array);
		Spinner weatherSpinner = (Spinner)findViewById(R.id.weather_spinner);
		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.weather_array, R.layout.weather_spinner_item);
		arrayAdapter.setDropDownViewResource(R.layout.weather_item_single_choice);
		weatherSpinner.setAdapter(arrayAdapter);
		weatherSpinner.setSelection(0);
		weatherSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
//				if (SceneUtil.getCurrCategory() != position) {
//					Toast.makeText(RainActivity.this, arrays[position], Toast.LENGTH_LONG).show();
//					SceneUtil.setCurrCategory(position);
//					mSurfaceView.switchSceneFromCityMgr(position, AnimationUtil.isDynamic());
//				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}
}
