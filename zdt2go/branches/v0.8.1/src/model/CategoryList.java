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
 * The CategoryList class collects information about available categories.
 * 
 * @author Achim Weimert
 * 
 */
class CategoryList implements ParseTextFilesCallback {
	
	private static final int ATTRIBUTES_PER_LINE = 2;
	private static final String TABLE_OF_CONTENT = Data.VOCABULARY_DIR+Data.DIRECTORY_LISTING_FILE;
	private Vector categories = null;

	public class FileCategory {
		private String name;
		private String file;
		public FileCategory(String name, String file) {
			this.file = file;
			setName(name);
		}
		private void setName(String name) {
			this.name = name;
			// make sure name is visible
			if (this.name==null || this.name.length()==0 || this.name.trim().length()==0) {
				this.name = "<EMPTY>";
			}
		}
		public String getName() {
			return name;
		}
		public String getFile() {
			return file;
		}
		public String getRecordStoreName() {
			return Data.CATEGORY_PREFIX+getName();
		}
	}

	/**
	 * Load categories from the index file
	 * 
	 * @throws IOException 
	 */
	public void loadCategories() throws IOException {
		InputStream inputStream = getClass().getResourceAsStream(
				TABLE_OF_CONTENT);
		if (inputStream==null) {
			throw new IOException("Index file not found: "+TABLE_OF_CONTENT);
		}
		try {
			loadCategoriesFromStream(inputStream);
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Load categories from the given input stream
	 * 
	 * @param inStream
	 * @throws IOException 
	 */
	public void loadCategoriesFromStream(InputStream inputStream) throws IOException {
		categories = new Vector();
		TextStreamLineParser streamParser;
		streamParser = new TextStreamLineParser(ATTRIBUTES_PER_LINE, this);
		streamParser.loadFromStream(inputStream);
	}
	
	/**
	 * Number of categories in the index file
	 * @return
	 */
	public int countCategories() {
		if (categories == null) {
			return 0;
		}
		return categories.size();
	}
	
	/**
	 * Returns information about the category with the given id
	 * @param i
	 * @return FileCategory of given category
	 */
	public FileCategory getCategory(int i) {
		return (FileCategory)categories.elementAt(i);
	}
	
	/**
	 * Checks if a given RecordStore has a corresponding entry in the index file
	 * @param recordStoreName
	 * @return
	 */
	public boolean doesCategoryExist(String recordStoreName) {
		for (int i=0; i<countCategories(); i++) {
			FileCategory category = getCategory(i);
			if (recordStoreName.compareTo(category.getRecordStoreName())==0) {
				return true;
			}
		}
		return false;
	}

	public void addEntry(String[] attributes, int numberOfAttributes) {
		boolean isCommentLine = numberOfAttributes>=1 && attributes[0].trim().startsWith("#");
		if (isCommentLine) {
			return;
		}
		if (numberOfAttributes<ATTRIBUTES_PER_LINE) {
			//System.err.println("warning: CategoryList::loadCategories(): too few attributes");
		}
		categories.addElement(new FileCategory(attributes[0], attributes[1]));
	}

	public void tooManyAttributes(String string) {
		//System.err.println("CategoryList::tooManyAttributes(): >"+string+"<");
	}

}
