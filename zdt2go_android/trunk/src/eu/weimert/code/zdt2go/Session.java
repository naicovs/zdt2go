/*******************************************************************************
 * This file is part of zdt2go.
 * Copyright (c) 2009 Achim Weimert.
 * http://code.google.com/p/zdt2go/
 * 
 * zdt2go is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * zdt2go is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with zdt2go.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Achim Weimert - initial API and implementation
 ******************************************************************************/
package eu.weimert.code.zdt2go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import eu.weimert.code.zdt2go.data.Entry;

public class Session extends Activity {
	
	public static final String BUNDLE_COUNT_CARDS = "countCards";
	public static final String BUNDLE_COUNT_TRIES = "countTries";
	private static final String BUNDLE_SHOW_BACK = "showBack";
	public static final String BUNDLE_SESSION = "session";
	eu.weimert.code.zdt2go.data.Session session = null;
	
	private GestureDetector gestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session);
		
		// initialize session
		if (savedInstanceState != null) {
			session = savedInstanceState.getParcelable(BUNDLE_SESSION);
		}
		if (session == null) {
			session = getIntent().getParcelableExtra(BUNDLE_SESSION);
		}
		if (session == null) {
			Toast.makeText(getBaseContext(), "Failed to load session.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		// check if already done
		if (session.isFinished()) {
			exitWithResult();
			return;
		}
		
		if (savedInstanceState != null && savedInstanceState.getBoolean(BUNDLE_SHOW_BACK)) {
			showBackSide();
		} else {
			showFrontSide();
		}
		
		initViews();
		
		ImageButton frontFlip = (ImageButton)findViewById(R.id.ButtonFrontFlip);
		ImageButton backFlip = (ImageButton)findViewById(R.id.ButtonBackFlip);
		frontFlip.setOnClickListener(flipListener);
		backFlip.setOnClickListener(flipListener);
		
		ImageButton correctAnswer = (ImageButton)findViewById(R.id.ButtonBackCorrect);
		ImageButton wrongAnswer = (ImageButton)findViewById(R.id.ButtonBackWrong);
		correctAnswer.setOnClickListener(answerListener);
		wrongAnswer.setOnClickListener(answerListener);
		
		gestureDetector = new GestureDetector(getBaseContext(), gestureListener);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		boolean backVisible = findViewById(R.id.LinearLayoutBack).getVisibility() == View.VISIBLE;
		outState.putBoolean(BUNDLE_SHOW_BACK, backVisible);
		outState.putParcelable(BUNDLE_SESSION, session);
	}
	
	private void initViews() {
		ListView back = (ListView) findViewById(R.id.ListViewBack);
		ListView front = (ListView) findViewById(R.id.ListViewFront);
		
		back.setOnTouchListener(touchListener);
		front.setOnTouchListener(touchListener);
		
		final Entry currentEntry = session.getCurrentEntry();
		back.setAdapter(new EntryAdapter(currentEntry, session.getBackIndices()));
		front.setAdapter(new EntryAdapter(currentEntry, session.getFrontIndices()));
	}
	
	private OnClickListener flipListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == findViewById(R.id.ButtonFrontFlip)) {
				showBackSide();
			} else {
				showFrontSide();
			}
		}
		
	};
	
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}
		
	};
	
	private boolean isFront() {
		LinearLayout front = (LinearLayout) findViewById(R.id.LinearLayoutFront);
		return front.getVisibility() == View.VISIBLE;
	}
	
	private boolean isBack() {
		LinearLayout back = (LinearLayout) findViewById(R.id.LinearLayoutBack);
		return back.getVisibility() == View.VISIBLE;
	}
	
	private void showFrontSide() {
		LinearLayout back = (LinearLayout) findViewById(R.id.LinearLayoutBack);
		LinearLayout front = (LinearLayout) findViewById(R.id.LinearLayoutFront);
		front.setVisibility(View.VISIBLE);
		back.setVisibility(View.GONE);
		
		setTitle("Session");
	}
	
	private void showBackSide() {
		LinearLayout back = (LinearLayout) findViewById(R.id.LinearLayoutBack);
		LinearLayout front = (LinearLayout) findViewById(R.id.LinearLayoutFront);
		back.setVisibility(View.VISIBLE);
		front.setVisibility(View.GONE);

		String title = String.format("Session: %d/%d", session.getCountTries(), session.getCountCards());
		setTitle(title);
	}
	
	private void exitWithResult() {
		Intent data = new Intent();
		data.putExtra(BUNDLE_COUNT_CARDS, session.getCountCards());
		data.putExtra(BUNDLE_COUNT_TRIES, session.getCountTries());
		setResult(RESULT_OK, data);
		finish();
	}

	private OnClickListener answerListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == findViewById(R.id.ButtonBackCorrect)) {
				session.handleCorrectAnswer();
			} else {
				session.handleIncorrectAnswer();
			}
			
			if (session.isFinished()) {
				exitWithResult();
				return;
			}
			
			initViews();
			showFrontSide();
		}
		
	};
	
	private OnGestureListener gestureListener = new OnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (Math.abs(velocityY) > Math.abs(velocityX) || Math.abs(velocityX) < 10) {
				return false;
			}
			if (isFront() && velocityX < 0) {
				showBackSide();
			} else if (isBack() && velocityX > 0) {
				showFrontSide();
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
	};

}
