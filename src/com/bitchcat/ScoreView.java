package com.bitchcat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.TextView;

public class ScoreView extends Activity {

	private static TextView tvBest, tvLow;
	private static SharedPreferences getPreferences;

	public static TextView getBestTV(TextView tv) {
		tvBest = tv;
		return tv;
	}

	public static TextView getLowTV(TextView tv) {
		tvLow = tv;
		return tv;
	}

	public static void showBestScore() {
		tvBest.setText(ScoreView.getBestScore() + "");
	}

	public static void saveBestScore(int s) {
		getPreferences = MainActivity.getMainAct().getSharedPreferences(
				"bestScore", MODE_PRIVATE);
		Editor e = getPreferences.edit();
		e.putInt("bestScore", s);
		e.commit();
	}

	public static int getBestScore() {
		return MainActivity.getMainAct()
				.getSharedPreferences("bestScore", MODE_PRIVATE)
				.getInt("bestScore", 0);
	}

	public static void showLowestScore() {
		tvLow.setText(ScoreView.getLowestScore() + "");
	}

	public static void saveLowestScore(int s) {
		getPreferences = MainActivity.getMainAct().getSharedPreferences(
				"lowestScore", MODE_PRIVATE);
		Editor e = getPreferences.edit();
		e.putInt("lowestScore", s);
		e.commit();
	}

	public static int getLowestScore() {
		return MainActivity.getMainAct()
				.getSharedPreferences("lowestScore", MODE_PRIVATE)
				.getInt("lowestScore", 0);
	}
}
