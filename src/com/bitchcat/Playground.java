package com.bitchcat;

import java.util.HashMap;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by LCL on 2015/10/22.
 */

public class Playground extends SurfaceView implements OnTouchListener,
		OnClickListener {

	private static int WIDTH;
	private static final int ROW = 9, COL = 9;
	private static int BLOCKS = 10;
	private Bitmap bm_normal, bm_win, bm_lose, bm_impossible, bm_no_choose,
			bm_no_fair, bm_what, bm_yo;
	private static int over = 0, frist = 0;
	private static Button btnRestart, btnAddBitch, btnBomb;
	private int score;
	private static int more_bitch = 1, which_bitch = 0;
	private boolean iswin = false, bombMode = false;

	private Card matrix[][];
	private Card bitch, bitch2, bitch3;
	/**
	 * 存放婊子的队列
	 */
	private Vector<Card> allBitch = new Vector<Card>();
	private Vector<Bitmap> allBitmap = new Vector<Bitmap>();

	@SuppressLint("ClickableViewAccessibility")
	public Playground(Context context) {
		super(context);
		initGame();
	}

	@SuppressLint("NewApi")
	public Playground(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initGame();
	}

	public Playground(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initGame();
	}

	public Playground(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGame();
	}

	/**
	 * 初始化界面布局
	 */
	Callback callback = new Callback() {

		public void surfaceCreated(SurfaceHolder holder) {
			redraw();
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			WIDTH = width / (COL + 1);
			redraw();
			View bitchView;

			// 下面这段是把这个playground大小搞成刚刚好那么高
			bitchView = findViewById(R.id.bitchView);
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bitchView
					.getLayoutParams();
			lp.width = width;
			lp.height = WIDTH * ROW;
			bitchView.setLayoutParams(lp);
			holder.setSizeFromLayout();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {

		}
	};

	/**
	 * 获取MainActivity的两个按钮View
	 */
	public static Button getBtnRestart(Button btn) {
		btnRestart = btn;
		return btn;
	}

	public static Button getbtnAddBitch(Button btn) {
		btnAddBitch = btn;
		return btn;
	}

	public static Button getbtnBomb(Button btn) {
		btnBomb = btn;
		return btn;
	}

	/**
	 * 计算婊子数目,最多3个
	 */
	public static void setMoreBitch() {
		if (more_bitch < 3) {
			more_bitch++;
			if (more_bitch == 2) {
				btnAddBitch.setText("三婊模式");
			} else if (more_bitch == 3) {
				btnAddBitch.setText("单婊模式");
			}
		} else {
			more_bitch = 1;
			btnAddBitch.setText("双婊模式");
		}
	}

	/**
	 * 初始化游戏
	 */
	@SuppressLint("ClickableViewAccessibility")
	public void initGame() {
		initValue();
		initBitmap();
		initBBB();
		getHolder().addCallback(callback);
		setOnTouchListener(this);
		// 设置透明背景
		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}

	private void initValue() {
		over = 0;
		frist = 0;
		score = 0;
		iswin = false;
		// bombMode = false;
	}

	private void initBBB() {
		// 添加背景 background
		matrix = new Card[ROW][COL];
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				matrix[i][j] = new Card(j, i);
			}
		}
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				matrix[i][j].setStatus(Card.STATUS_OFF);
			}
		}
		// 添加婊子 bitch
		switch (more_bitch) {
		case 3:
			bitch3 = new Card(3, 4);
			getDot(3, 4).setStatus(Card.STATUS_IN);
			allBitch.add(bitch3);
		case 2:
			bitch2 = new Card(5, 4);
			getDot(5, 4).setStatus(Card.STATUS_IN);
			allBitch.add(bitch2);
		case 1:
			bitch = new Card(4, 4);
			allBitch.add(bitch);
			getDot(4, 4).setStatus(Card.STATUS_IN);
			break;
		default:
			break;
		}
		// 添加路障 blocks
		BLOCKS = 12 - (int) (Math.random() * 100) % 9;
		for (int i = 0; i < BLOCKS;) {
			int x = (int) ((Math.random() * 1000) % COL);
			int y = (int) ((Math.random() * 1000) % ROW);
			if (getDot(x, y).getStatus() == Card.STATUS_OFF) {
				getDot(x, y).setStatus(Card.STATUS_ON);
				i++;
			}
		}
	}

	private void initBitmap() {
		bm_normal = BitmapFactory.decodeResource(getResources(),
				R.drawable.bitch);
		bm_win = BitmapFactory.decodeResource(getResources(),
				R.drawable.bitch_win);
		bm_lose = BitmapFactory.decodeResource(getResources(),
				R.drawable.bitch_lose);
		bm_no_choose = BitmapFactory.decodeResource(getResources(),
				R.drawable.no_choose);
		bm_no_fair = BitmapFactory.decodeResource(getResources(),
				R.drawable.no_fair);
		bm_what = BitmapFactory.decodeResource(getResources(),
				R.drawable.what_can_i_do);
		bm_impossible = BitmapFactory.decodeResource(getResources(),
				R.drawable.impossible);
		bm_yo = BitmapFactory.decodeResource(getResources(), R.drawable.yo);
		allBitmap.add(bm_yo);
		allBitmap.add(bm_no_fair);
		allBitmap.add(bm_impossible);
		allBitmap.add(bm_what);
		allBitmap.add(bm_no_choose);
		allBitmap.add(bm_normal);
		allBitmap.add(bm_lose);
		allBitmap.add(bm_win);
	}

	/**
	 * 画图 改透明背景 显示分数
	 */
	public void redraw() {
		ScoreView.showBestScore();
		ScoreView.showLowestScore();
		Canvas c = getHolder().lockCanvas();
		c.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		btnRestart.setOnClickListener(this);
		btnAddBitch.setOnClickListener(this);
		btnBomb.setOnClickListener(this);
		Paint paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		if (WIDTH > 0) {
			for (int i = 0; i < ROW; i++) {
				int offset = 0;
				if (i % 2 != 0) {
					offset = WIDTH / 2;
				}
				for (int j = 0; j < COL; j++) {
					paint.setMaskFilter(null);
					paint.setColorFilter(null);
					Card one = getDot(j, i);
					float x = one.getX() * WIDTH + offset + WIDTH / 4;
					float y = one.getY() * WIDTH;
					float x1 = (one.getX() + 1) * WIDTH + offset + WIDTH / 4;
					float y1 = (one.getY() + 1) * WIDTH;

					switch (one.getStatus()) {
					case Card.STATUS_IN:
						paint.setColor(0xFFFFFFFF);
						if (over == 0) {
							if (iswin) {
								c.drawBitmap(resize(allBitmap.get(0)), x, y,
										paint);
							} else if (score > 40) {
								c.drawBitmap(resize(allBitmap.get(1)), x, y,
										paint);
							} else if (score > 30) {
								c.drawBitmap(resize(allBitmap.get(2)), x, y,
										paint);
							} else if (score > 20) {
								c.drawBitmap(resize(allBitmap.get(3)), x, y,
										paint);
							} else if (score > 10) {
								c.drawBitmap(resize(allBitmap.get(4)), x, y,
										paint);
							} else {
								c.drawBitmap(resize(allBitmap.get(5)), x, y,
										paint);
							}
						} else if (over == 1) {
							paint.setColor(0xFFAA0000);
							paint.setColorFilter(new LightingColorFilter(
									Color.WHITE, 0xFF8D0BA3));
							c.drawBitmap(resize(allBitmap.get(6)), x, y, paint);
						} else {
							paint.setColor(0xFFAA0000);
							paint.setColorFilter(new LightingColorFilter(
									Color.WHITE, Color.RED));
							c.drawBitmap(resize(allBitmap.get(7)), x, y, paint);
						}
						break;
					case Card.STATUS_ON:
						paint.setColor(0xFFEEAA00);
						if (bombMode) {
							c.drawBitmap(resize(allBitmap.get((int) ((Math
									.random() * 100) % 8))), x, y, paint);
						}
						break;
					case Card.STATUS_OFF:
						paint.setColor(0x88EEEEEE);
						break;
					default:
						break;
					}
					if (one.getStatus() == Card.STATUS_OFF
							|| (!bombMode && one.getStatus() == Card.STATUS_ON)) {
						float direction[] = { 1, 1, 1 };
						paint.setMaskFilter(new EmbossMaskFilter(direction,
								0.5f, 8f, 3f));
						c.drawOval(new RectF(x, y, x1, y1), paint);
					}
				}
			}
		}
		getHolder().unlockCanvasAndPost(c);
	}

	/**
	 * 把婊子的头缩放到刚刚好
	 */
	private static Bitmap resize(Bitmap bitmap) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) WIDTH / w);
		float scaleHeight = ((float) WIDTH / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap
				.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return resizeBmp;
	}

	/**
	 * 把x和y倒过来 因为... 不知道怎么解释
	 */
	private Card getDot(int x, int y) {
		return matrix[y][x];
	}

	/**
	 * 看看有没有到边缘 到了返回true
	 */
	private boolean isAtEdge(Card d) {
		if (d.getX() * d.getY() == 0 || d.getX() + 1 == COL
				|| d.getY() + 1 == ROW) {
			return true;
		}
		return false;
	}

	/**
	 * 获取周围元素
	 */

	private Card getNeighbour(Card d, int dir) {
		switch (dir) {
		case 1:
			return getDot(d.getX() - 1, d.getY());
		case 2:
			if (d.getY() % 2 == 0) {
				return getDot(d.getX() - 1, d.getY() - 1);
			} else {
				return getDot(d.getX(), d.getY() - 1);
			}
		case 3:
			if (d.getY() % 2 == 0) {
				return getDot(d.getX(), d.getY() - 1);
			} else {
				return getDot(d.getX() + 1, d.getY() - 1);
			}
		case 4:
			return getDot(d.getX() + 1, d.getY());
		case 5:
			if (d.getY() % 2 == 0) {
				return getDot(d.getX(), d.getY() + 1);
			} else {
				return getDot(d.getX() + 1, d.getY() + 1);
			}
		case 6:
			if (d.getY() % 2 == 0) {
				return getDot(d.getX() - 1, d.getY() + 1);
			} else {
				return getDot(d.getX(), d.getY() + 1);
			}
		default:
			break;
		}
		return d;
	}

	/**
	 * 获取到边缘距离
	 */
	private int getDistance(Card one, int dir) {
		int distance = 0;
		if (isAtEdge(one)) {
			return 1;
		}
		Card ori = one, next;
		while (true) {
			next = getNeighbour(ori, dir);
			if (next.getStatus() == Card.STATUS_ON) {
				return distance * -1;
			}
			if (isAtEdge(next)) {
				distance++;
				return distance;
			}
			distance++;
			ori = next;
		}
	}

	/**
	 * 当前婊子可以往哪走 走不走的了 成败输赢看这里
	 */
	private void move(Card now_bitch) {
		score++;
		Vector<Card> avaliable = new Vector<Card>();// 周围阻碍的个数
		Vector<Card> postive = new Vector<Card>();// 周围到阻碍的距离
		HashMap<Card, Integer> al = new HashMap<Card, Integer>();
		for (int i = 1; i < 7; i++) {
			Card n = getNeighbour(now_bitch, i);
			if (n.getStatus() == Card.STATUS_OFF) {
				avaliable.add(n);
				al.put(n, i);
				if (getDistance(n, i) > 0) {
					postive.add(n);
				}
			}
		}
		if (frist == 0 && avaliable.size() != 0) {
			frist = 1;
			MoveTo(avaliable.get((int) ((Math.random() * 1000) % avaliable
					.size())), now_bitch);
		} else if (avaliable.size() == 0) {
			switch (more_bitch) {
			case 1:
				if (check(allBitch.get(0))) {
					win();
				}
				break;
			case 2:
				if (check(allBitch.get(0)) && check(allBitch.get(1))) {
					win();
				}
				break;
			case 3:
				if (check(allBitch.get(0)) && check(allBitch.get(1))
						&& check(allBitch.get(2))) {
					win();
				}
				break;
			default:
				break;
			}
		} else if (avaliable.size() == 1) {
			MoveTo(avaliable.get(0), now_bitch);
		} else {
			iswin = false;
			Card best = null;
			if (postive.size() != 0) {
				int min = 99;
				for (int i = 0; i < postive.size(); i++) {
					int a = getDistance(postive.get(i), al.get(postive.get(i)));
					if (a < min) {
						min = a;
						best = postive.get(i);
					}
				}
				if (min == 1) {
					iswin = true;
				}
			} else {
				int max = 0;
				for (int i = 0; i < avaliable.size(); i++) {
					int b = getDistance(avaliable.get(i),
							al.get(avaliable.get(i)));
					if (b <= max) {
						max = b;
						best = avaliable.get(i);
					}
				}
			}
			MoveTo(best, now_bitch);
		}
		if (isAtEdge(now_bitch)) {
			lose();
			score = 0;
		}
	}

	private boolean check(Card b) {
		Vector<Card> avaliable = new Vector<Card>();// 周围阻碍的个数
		for (int i = 1; i < 7; i++) {
			Card n = getNeighbour(b, i);
			if (n.getStatus() == Card.STATUS_OFF) {
				avaliable.add(n);
			}
		}
		if (avaliable.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 婊子在移动
	 */
	private void MoveTo(Card one, Card now_bitch) {
		one.setStatus(Card.STATUS_IN);
		getDot(now_bitch.getX(), now_bitch.getY()).setStatus(Card.STATUS_OFF);
		now_bitch.setXY(one.getX(), one.getY());
	}

	private void win() {
		Toast.makeText(getContext(), "成功围住小咪咪", Toast.LENGTH_LONG).show();
		over = 1;
		if (score < ScoreView.getBestScore() || ScoreView.getBestScore() == 0) {
			ScoreView.saveBestScore(score);
		}
		if (score > ScoreView.getLowestScore()) {
			ScoreView.saveLowestScore(score);
		}
		if (allBitch.size() != 0)
			allBitch.clear();
		redraw();
	}

	private void lose() {
		Toast.makeText(getContext(), "你个傻叼", Toast.LENGTH_LONG).show();
		over = 2;
		redraw();
		if (allBitch.size() != 0)
			allBitch.clear();
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP && over == 0) {
			int x = 0, y = 0;
			y = (int) (event.getY() / WIDTH);
			if (y % 2 == 0) {
				x = (int) ((event.getX()) / WIDTH);
			} else {
				x = (int) ((event.getX() - WIDTH / 2 - WIDTH / 4) / WIDTH);
			}
			if (x + 1 > ROW || y + 1 > COL) {
				return true;
			} else if (getDot(x, y).getStatus() == Card.STATUS_OFF) {
				getDot(x, y).setStatus(Card.STATUS_ON);
				if (allBitch.size() != 0) {
					if (which_bitch < more_bitch) {
						int num = 0;
						while (check(allBitch.get(which_bitch))) {
							num++;
							if (num == more_bitch) {
								break;
							}
							which_bitch++;
							if (which_bitch == more_bitch) {
								which_bitch = 0;
							}
						}
						move(allBitch.get(which_bitch));
						which_bitch++;
						if (which_bitch == more_bitch) {
							which_bitch = 0;
						}
					}
				}
				redraw();
			}
		}
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRestart:
			more_bitch = 1;
			which_bitch = 0;
			if (allBitch.size() != 0)
				allBitch.clear();
			initGame();
			redraw();
			break;
		case R.id.btnAdd:
			which_bitch = 0;
			if (allBitch.size() != 0)
				allBitch.clear();
			setMoreBitch();
			initGame();
			redraw();
			break;
		case R.id.btnBomb:
			if (bombMode) {
				bombMode = false;
				btnBomb.setText("爆炸模式");
			} else {
				bombMode = true;
				btnBomb.setText("正常模式");
			}
			if (allBitch.size() != 0)
				allBitch.clear();
			initGame();
			redraw();
			break;
		default:
			break;
		}
	}
}
