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

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import eu.weimert.code.zdt2go.data.Category;
import eu.weimert.code.zdt2go.data.Session;

public class ConfigureSession extends Activity {
	
	public static final String BUNDLE_CATEGORY = "category";
	private Category category = null;
	private Vector<CheckBox> frontCheckboxes;
	private Vector<CheckBox> backCheckboxes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_session);
		
		Bundle data = getIntent().getExtras();
		Object value = data.getParcelable(BUNDLE_CATEGORY);
		if (value instanceof Category) {
			category = (Category) value;
		}
		if (category == null) {
			Toast.makeText(getBaseContext(), "Failed to load category.",
					Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
			finish();
			return;
		}
		
		createTable();
		
		Button start = (Button)findViewById(R.id.ButtonStart);
		start.setOnClickListener(startListener);
		start.setEnabled(false);
	}
	
	private int getCheckedCount(Vector<CheckBox> checkboxes) {
		int countChecked = 0;
		for (CheckBox checkbox : checkboxes) {
			if (checkbox.isChecked()) {
				countChecked++;
			}
		}
		return countChecked;
	}
	
	private int[] getCheckedIndices(Vector<CheckBox> checkboxes) {
		int[] result = new int[getCheckedCount(checkboxes)];
		int nextIndex = 0;
		for (int i = 0; i < checkboxes.size(); i++) {
			if (checkboxes.get(i).isChecked()) {
				result[nextIndex] = i;
				nextIndex++;
			}
		}
		return result;
	}

	private OnClickListener startListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == findViewById(R.id.ButtonStart)) {
				int[] frontIndices = getCheckedIndices(frontCheckboxes);
				int[] backIndices = getCheckedIndices(backCheckboxes);
				
				Session session = new Session(category, frontIndices, backIndices);
				Intent data = new Intent();
				data.putExtra(eu.weimert.code.zdt2go.Session.BUNDLE_SESSION, session);
				setResult(RESULT_OK, data);
				finish();
			}
		}
		
	};
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			final int countCheckedFront = getCheckedCount(frontCheckboxes);
			final int countCheckedBack = getCheckedCount(backCheckboxes);
			final Button start = (Button) findViewById(R.id.ButtonStart);
			if (countCheckedFront == 0 || countCheckedBack == 0) {
				start.setEnabled(false);
			} else {
				start.setEnabled(true);
			}
		}
		
	};
	
	private void createTable() {
		frontCheckboxes = new Vector<CheckBox>();
		backCheckboxes = new Vector<CheckBox>();
		LayoutInflater inflater = LayoutInflater.from(findViewById(R.id.TableLayoutItems).getContext());
		TableLayout table = (TableLayout) findViewById(R.id.TableLayoutItems);
		for (int i = 0; i < category.getCountItems(); i++) {
			TableRow row = (TableRow) inflater.inflate(R.layout.table_row, null);
			// set description text
			TextView item = (TextView) row.findViewById(R.id.TextViewItem);
			item.setText(category.getItemName(i));
			// add change listener and check all items on back
			CheckBox back = (CheckBox) row.findViewById(R.id.CheckBoxBack);
			back.setOnCheckedChangeListener(checkedChangeListener);
			back.setChecked(true);
			CheckBox front = (CheckBox) row.findViewById(R.id.CheckBoxFront);
			front.setOnCheckedChangeListener(checkedChangeListener);
			// cache boxes pointer
			frontCheckboxes.add(front);
			backCheckboxes.add(back);
			// add boxes to table
			table.addView(row);
		}
//		table.setColumnStretchable(0, true);
	}

}
