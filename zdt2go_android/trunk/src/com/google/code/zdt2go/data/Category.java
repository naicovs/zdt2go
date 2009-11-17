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
package com.google.code.zdt2go.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.google.code.zdt2go.data.helper.TextStreamLineParser;
import com.google.code.zdt2go.data.helper.TextStreamLineParser.ParseTextFilesCallback;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable, ParseTextFilesCallback {
	
	private String path;
	private String itemNames[];
	private Vector<Entry> entries;
	
	private static int ATTRIBUTES_PER_LINE = 4;
	
	public Category(Parcel in) {
		path = in.readString();
		Object[] tempItemNames = in.readArray(String.class.getClassLoader());
		itemNames = new String[tempItemNames.length];
		for (int i = 0; i < tempItemNames.length; i++) {
			itemNames[i] = (String) tempItemNames[i];
		}

		Object[] tempEntries = in.readArray(Entry.class.getClassLoader());
		entries = new Vector<Entry>();
		for (Object entry : tempEntries) {
			entries.add((Entry)entry);
		}
	}
	
	public Category(String path) throws IOException {
		this.path = path;
		itemNames = new String[ATTRIBUTES_PER_LINE];
		itemNames[0] = "traditional";
		itemNames[1] = "simplified";
		itemNames[2] = "pinyin";
		itemNames[3] = "meaning";
		loadCategoryFromFile();
	}
	
	
	/**
	 * Loads the given category from the package
	 * @param categoryId
	 * @param fileName
	 * @return true on success
	 * @throws IOException 
	 */
	private void loadCategoryFromFile() throws IOException {
		InputStream inputStream;
		inputStream = new FileInputStream(path);
		try {
			loadCategoryFromStream(inputStream);
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Loads the category from the given stream
	 * @param inputStream
	 * @return true on success
	 * @throws IOException 
	 */
	private void loadCategoryFromStream(InputStream inputStream) throws IOException {
		this.entries = new Vector<Entry>();
		TextStreamLineParser streamParser;
		streamParser = new TextStreamLineParser(ATTRIBUTES_PER_LINE, this);
		streamParser.loadFromStream(inputStream);
	}
	
	/**
	 * Returns the entry with the given ID
	 * @param entryId
	 * @return
	 */
	public Entry getEntry(int entryId) {
		return (Entry) entries.elementAt(entryId);
	}
	
	/**
	 * Returns the number of entries in the category
	 * @return
	 */
	public int getEntriesCount() {
		return entries.size();
	}

	public void addEntry(String[] attributes, int numberOfAttributes) {
		boolean isCommentLine = numberOfAttributes>=1 && attributes[0].trim().startsWith("#");
		if (isCommentLine) {
			return;
		}
		if (numberOfAttributes<ATTRIBUTES_PER_LINE) {
			//System.err.println("warning: CategoryList::loadCategories(): too few attributes");
		}
		entries.addElement(new Entry(attributes, numberOfAttributes));
	}

	public void tooManyAttributes(String string) {
		//System.err.println("CategoryList::tooManyAttributes(): >"+string+"<");
	}

	public String getItemName(int i) {
		return itemNames[i];
	}
	
	public int getCountItems() {
		return itemNames.length;
	}
	
	public String getPathName() {
		return path;
	}
	
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(path);
		dest.writeArray(itemNames);
		dest.writeArray(entries.toArray());
	}

}
