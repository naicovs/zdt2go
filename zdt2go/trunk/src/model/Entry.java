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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * The Entry class represents one vocabulary entry with all its data.
 * 
 * @author Achim Weimert
 *
 */
class Entry {
	private int categoryId = -1;
	private String elements[] = new String[4];
	private Long hashValue = null;
	private int recordId = -1;
	private Statistics statistics;
	
	/**
	 * Creates a new Entry from the given data
	 * @param data data as string
	 * @param countAttributes number of data's data to consider
	 */
	public Entry(int categoryId, String data[], int countAttributes) {
		if (data.length<elements.length || data.length<countAttributes) {
			throw new IllegalArgumentException();
		}
		
		this.categoryId = categoryId;
		// Convert from Model/ZDT naming to Data Naming
		if (DataImplementation.TRADITIONAL<countAttributes) {
			elements[Attribute.TRADITIONAL] = new String(data[DataImplementation.TRADITIONAL]);
		}
		if (DataImplementation.SIMPLIFIED<countAttributes) {
			elements[Attribute.SIMPLIFIED] = new String(data[DataImplementation.SIMPLIFIED]);
		}
		if (DataImplementation.PINYIN<countAttributes) {
			elements[Attribute.PINYIN] = new String(data[DataImplementation.PINYIN]);
		}
		if (DataImplementation.MEANING<countAttributes) {
			elements[Attribute.MEANING] = new String(data[DataImplementation.MEANING]);
		}
		// Save statistics
		statistics = new Statistics(countAttributes, data);
	}
	
	/**
	 * Returns the specified attribute
	 * @param i is one of Data.TRADITIONAL, Data.SIMPLIFIED, Data.PINYIN, Data.MEANING
	 * @return
	 */
	public String getElement(int i) {
		if (i<0 || i>=elements.length) {
			throw new IllegalArgumentException();
		}
		if (i==Attribute.PINYIN) {
			return formatPinyin(elements[i]);
		}
		if (i==Attribute.MEANING) {
			return formatMeaning(elements[i]);
		}
		return elements[i];
	}
	
	public int getCorrectStreak() {
		return statistics.getCorrectStreak();
	}
	
	public int getTimesTested() {
		return statistics.getTimesTested();
	}
	
	public int getNumberCorrect() {
		return statistics.getNumberCorrect();
	}
	
	public boolean getLearned() {
		return statistics.getLearned();
	}
	
	public Date getLastTested() {
		return statistics.getLastTested();
	}
	
	public int getCategoryId() {
		return categoryId;
	}
	
	private void setRecordId(int id) {
		recordId = id;
	}
	
	/**
	 * Update statistics to include a new wrong answer
	 */
	public void markAsWronglyAnswered() {
		statistics.resetCorrectStreak();
	}
	
	/**
	 * Update statistics to include a new test
	 */
	public void startNewTest() {
		statistics.incrementTimesTested();
		statistics.updatedLastTested();
	}
	
	/**
	 * Updates statistics to include a new correct answer
	 */
	public void markAsCorrectlyAnswered() {
		statistics.incrementNumberCorrect();
		statistics.incrementCorrectStreak();
	}
	
	/**
	 * Returns the recordId of the entry
	 * @return value greater zero if entry's statistics has been saved before
	 */
	public int getRecordId() {
		return recordId;
	}
	
	/**
	 * Formats given pinyin for display.
	 * @param string
	 * @return
	 */
	public static final String formatPinyin(String string) {
		string = stringReplace(string, "5", "");
		string = stringReplace(string, "u:", "ü");
		return string;
	}
	
	/**
	 * Formats given meaning for display.
	 * @param string
	 * @return
	 */
	public static final String formatMeaning(String string) {
		if (string.charAt(0)=='/') {
			string = string.substring(1);
		}
		if (string.charAt(string.length()-1)=='/') {
			string = string.substring(0, string.length()-1);
		}
		string = charReplace(string, '/', "; ");
		return string;
	}
	
	/**
	 * Replace all occurrences of needle in haystack by replace
	 * @param haystack
	 * @param needle
	 * @param replace
	 * @return
	 */
	public static final String stringReplace(final String haystack, final String needle, final String replace) {
		StringBuffer stringBuffer = new StringBuffer();
		
		int searchStringPos;
		int startPos = 0;
		int searchStringLength = needle.length();
		
		while ( (searchStringPos = haystack.indexOf(needle, startPos)) != -1) {
			stringBuffer.append(haystack.substring(startPos, searchStringPos)).append(replace);
			startPos = searchStringPos + searchStringLength;
		}
		
		stringBuffer.append(haystack.substring(startPos, haystack.length()));
		
		return stringBuffer.toString();
	}
	
	/**
	 * Replace all occurrences of needle in haystack by replace
	 * @param haystack
	 * @param needle
	 * @param replace
	 * @return
	 */
	public static final String charReplace(String haystack, char needle, String replace) {
		int pos = 0;
		while ( (pos=haystack.indexOf(needle, pos))>=0) {
			haystack = haystack.substring(0, pos)+replace+haystack.substring(pos+1);
		}
		return haystack;
	}

	/**
	 * Return a byte representation of entry's hash and statistics
	 * @return
	 * @throws IOException
	 */
	public byte[] serialize() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		
		dout.writeLong(getHash());
		statistics.serialize(dout);
		dout.flush();

		byte[] ret = bout.toByteArray();

		dout.close();

		return ret;
	}

	/**
	 * Overwrites statistics of current entry with given data
	 * if the given data belongs to the current entry
	 * @param recordId
	 * @param data
	 * @return true if entry was updated, else false
	 * @throws IOException
	 */
	public boolean tryUpdatingStats(int recordId, byte[] data) throws IOException {
		boolean result = true;
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);

		// check if saved entry is the same as current entry by comparing hash values
		if (din.readLong()!=getHash()) {
			result = false;
		} else {
			statistics = new Statistics(din);
			setRecordId(recordId);
		}

		din.close();
		return result;
	}
	
	/**
	 * Calculate the percentage of correctly answered tests in total tests
	 * @return
	 */
	public int getPercentage() {
		if (getTimesTested()==0) {
			return 0;
		}
		return getNumberCorrect()*100/getTimesTested();
	}
	
	/**
	 * Calculate a hash value for the entry
	 * @return
	 */
	public long getHash() {
		if (hashValue!=null) {
			return hashValue.longValue();
		}
		long hash = 0;
		StringBuffer string = new StringBuffer();
		string.append(getElement(Attribute.TRADITIONAL));
		string.append(getElement(Attribute.SIMPLIFIED));
		string.append(getElement(Attribute.PINYIN));
		string.append(getElement(Attribute.MEANING));
		for (int i = 0; i < string.length(); i++) {
			hash = 31*hash + string.charAt(i);
		}
		hashValue = new Long(hash);
		return hashValue.longValue();
	}

}
