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
package com.google.code.zdt2go;

import com.google.code.zdt2go.data.Entry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EntryAdapter extends BaseAdapter {
	
	private final Entry entry;
	private final int indices[];
	
	public EntryAdapter(Entry entry, int indices[]) {
		this.entry = entry;
		this.indices = indices;
	}

	@Override
	public int getCount() {
		return indices.length;
	}

	@Override
	public Object getItem(int index) {
		return entry.getItem(indices[index]);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view;
		if (convertView != null) {
			view = (TextView) convertView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = (TextView) inflater.inflate(R.layout.entry_row, null);
		}
		final String item = entry.getItem(indices[position]);
		view.setText(formatItem(item));
		return view;
	}
	
	public static final String formatItem(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		if (string.charAt(0) == '/') {
			string = string.substring(1);
		}
		if (string.charAt(string.length() - 1) == '/') {
			string = string.substring(0, string.length() - 1);
		}
		string = string.replaceAll("/", "; ");
		string = string.trim();
		return string;
	}

}
