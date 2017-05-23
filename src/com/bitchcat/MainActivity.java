package com.bitchcat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button restart,addBitch,bomb;
	private TextView tvBest, tvLow;
	private static MainActivity mainActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		restart = (Button) findViewById(R.id.btnRestart);
		tvBest = (TextView) findViewById(R.id.tvScore);
		tvLow = (TextView) findViewById(R.id.tvLowestScore);
		addBitch = (Button) findViewById(R.id.btnAdd);
		bomb=(Button) findViewById(R.id.btnBomb);
		Playground.getBtnRestart(restart);
		Playground.getbtnAddBitch(addBitch);
		Playground.getbtnBomb(bomb);
		ScoreView.getBestTV(tvBest);
		ScoreView.getLowTV(tvLow);
	}

	public MainActivity() {
		mainActivity = this;
	}

	public static MainActivity getMainAct() {
		return mainActivity;
	}

}
