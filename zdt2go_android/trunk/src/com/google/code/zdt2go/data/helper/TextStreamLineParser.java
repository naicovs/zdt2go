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
package com.google.code.zdt2go.data.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * The TextStreamLineParser class provides functions to load and parse a tab separated
 * text file from a stream.
 * 
 * @author Achim Weimert
 * 
 */
public class TextStreamLineParser {

	public interface ParseTextFilesCallback {
		void addEntry(final String[] attributes, int numberOfAttributes);

		void tooManyAttributes(final String string);
	}

	private static final char CARRIAGE_RETURN = '\r';
	private static final char TAB = '\t';
	private static final char NEWLINE = '\n';
	
	private class Attributes {
		String[] data;
		int counter;
		private int numberOfAttributes;
		public Attributes(int numberOfAttributes) {
			this.numberOfAttributes = numberOfAttributes;
			reset();
		}
		public void addAttribute(String value) {
			data[counter] = value;
			counter++;
		}
		public boolean canAddAttribute() {
			return counter<data.length;
		}
		public void reset() {
			counter = 0;
			data = new String[numberOfAttributes];
		}
	}

	private Attributes attributes;
	private InputStreamReader inStreamReader;
	private ParseTextFilesCallback callback;

	public TextStreamLineParser(int attributesPerLine, ParseTextFilesCallback callback) {
		inStreamReader = null;
		attributes = new Attributes(attributesPerLine);
		this.callback = callback;
	}

	/**
	 * Loads entries from the given stream
	 * 
	 * @param inputStream
	 * @throws IOException
	 */
	public void loadFromStream(InputStream inputStream) throws IOException {
		inStreamReader = new InputStreamReader(inputStream, "UTF-8");
		loadData();
	}

	private void loadData() throws IOException {
		StringBuffer currentData = new StringBuffer();
		InputStreamReaderIterator inputIterator = new InputStreamReaderIterator(
				inStreamReader);
		// Read until the end of the stream
		while (inputIterator.hasNext()) {
			int character = inputIterator.next();
			currentData = handleCharacter(currentData, character);
		}
		// parse rest
		boolean isEntryAvailable = currentData.length() > 0
				|| attributes.counter > 0;
		if (isEntryAvailable) {
			addAttribute(currentData);
			callback.addEntry(attributes.data, attributes.counter);
		}
	}

	private StringBuffer handleCharacter(final StringBuffer currentData, int character) {
		StringBuffer result = currentData;
		switch (character) {
		case CARRIAGE_RETURN:
			// ignore carriage return
			break;
		case TAB:
			addAttribute(currentData);
			result = new StringBuffer();
			break;
		case NEWLINE:
			addAttribute(currentData);
			result = new StringBuffer();
			callback.addEntry(attributes.data, attributes.counter);
			attributes.reset();
			break;
		default:
			result.append((char) character);
			break;
		}
		return result;
	}

	private void addAttribute(StringBuffer currentData) {
		if (attributes.canAddAttribute()) {
			attributes.addAttribute(currentData.toString().trim());
		} else {
			callback.tooManyAttributes(currentData.toString());
		}
	}
}
