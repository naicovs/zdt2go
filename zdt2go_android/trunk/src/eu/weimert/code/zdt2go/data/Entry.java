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
package eu.weimert.code.zdt2go.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Entry implements Parcelable {
	
	private String[] items = new String[0];
	
	public Entry(Parcel in) {
		Object[] temp = in.readArray(String.class.getClassLoader());
		items = new String[temp.length];
		for (int i = 0; i < temp.length; i++) {
			items[i] = (String)temp[i];
		}
	}
	
	public Entry(String[] attributes, int numberOfAttributes) {
		items = new String[numberOfAttributes];
		for (int i = 0; i < numberOfAttributes; i++) {
			items[i] = attributes[i];
		}
	}

	public String getItem(int i) {
		if (i >= items.length) {
			return "<UNDEFINED>";
		}
		return new String(items[i]);
	}

    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
		public Entry createFromParcel(Parcel in) {
			return new Entry(in);
		}

		public Entry[] newArray(int size) {
			return new Entry[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeArray(items);
	}

}
