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
package model;


import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import model.helper.TextStreamLineParser;
import model.helper.TextStreamLineParser.ParseTextFilesCallback;

/**
 * The Category class provides functions to load a category from a file.
 * 
 * @author Achim Weimert
 *
 */
class Category implements ParseTextFilesCallback {
	
	private static final int ATTRIBUTES_PER_LINE = DataImplementation.STATS_MAX_ATTRIBUTES;
	private Vector entries = new Vector();
	private int categoryId;
	
	/**
	 * Loads the given category from the package
	 * @param categoryId
	 * @param fileName
	 * @return true on success
	 * @throws IOException 
	 */
	public void loadCategoryFromFile(int categoryId, String fileName) throws IOException {
		InputStream inputStream;
		inputStream = getClass().getResourceAsStream(Data.VOCABULARY_DIR+fileName);
		if (inputStream==null) {
			throw new IOException("File not found: "+fileName);
		}
		try {
			loadCategoryFromStream(categoryId, inputStream);
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
	public void loadCategoryFromStream(int categoryId, InputStream inputStream) throws IOException {
		this.categoryId = categoryId;
		this.entries = new Vector();
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
		entries.addElement(new Entry(categoryId, attributes, numberOfAttributes));
	}

	public void tooManyAttributes(String string) {
		//System.err.println("CategoryList::tooManyAttributes(): >"+string+"<");
	}

}
