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
package model.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import test.TestHelper;

import model.helper.TextStreamLineParser.ParseTextFilesCallback;
import jmunit.framework.cldc11.TestCase;

public class TextStreamLineParserTest extends TestCase implements ParseTextFilesCallback {

	private Vector entries;
	private int tooManyAttributes;
	private TextStreamLineParser streamParser;
	
	private class Entry {
		String[] attributes;
		int numberOfAttributes;
		public Entry(String[] attributes, int numberOfAttributes) {
			this.attributes = attributes;
			this.numberOfAttributes = numberOfAttributes;
		}
	}
	
	public TextStreamLineParserTest() {
		super(3, "TextStreamLineParserTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: testLoadFromStream1(); break;
		case 1: testLoadFromStream2(); break;
		case 2: testLoadFromStream3(); break;
		}
	}
	
	public void setUp() {
		entries = new Vector();
		tooManyAttributes = 0;
	}
	
	public void testLoadFromStream1() {
		String data = "";
		InputStream inputStream = TestHelper.getInputStreamFromString(data);
		streamParser = new TextStreamLineParser(3, this);
		try {
			streamParser.loadFromStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected IOException: "+e);
		}
		assertEquals("x0", 0, tooManyAttributes);
		assertEquals("x1", 0, entries.size());
	}

	public void testLoadFromStream2() {
		String data = "hallo1\thallo2\thallo3\thallo4\n"
				+ "data1\tdata2\tdata3";
		InputStream inputStream = TestHelper.getInputStreamFromString(data);
		streamParser = new TextStreamLineParser(3, this);
		try {
			streamParser.loadFromStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected IOException: "+e);
		}
		assertEquals("x0", 2, entries.size());
		assertEquals("x1", 1, tooManyAttributes);
	}

	public void testLoadFromStream3() {
		String data = "1\t2\t3\r\n"
				+ "4\t5\t6\n"
				+ "\t\t\n"
				+ "\n";
		InputStream inputStream = TestHelper.getInputStreamFromString(data);
		streamParser = new TextStreamLineParser(3, this);
		try {
			streamParser.loadFromStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected IOException: "+e);
		}
		assertEquals("x0", 4, entries.size());
		assertEquals("x1", 0, tooManyAttributes);
		assertEquals("x2", 3, getEntry(0).numberOfAttributes);
		assertEquals("x3", "1", getEntry(0).attributes[0]);
		assertEquals("x4", "2", getEntry(0).attributes[1]);
		assertEquals("x5", "3", getEntry(0).attributes[2]);
		assertEquals("x6", 3, getEntry(1).numberOfAttributes);
		assertEquals("x7", "4", getEntry(1).attributes[0]);
		assertEquals("x8", "5", getEntry(1).attributes[1]);
		assertEquals("x9", "6", getEntry(1).attributes[2]);
		assertEquals("x10", 3, getEntry(2).numberOfAttributes);
		assertEquals("x11", "", getEntry(2).attributes[0]);
		assertEquals("x12", "", getEntry(2).attributes[1]);
		assertEquals("x13", "", getEntry(2).attributes[2]);
		assertEquals("x14", 1, getEntry(3).numberOfAttributes);
		assertEquals("x15", "", getEntry(3).attributes[0]);
	}

	public void addEntry(String[] attributes, int numberOfAttributes) {
		entries.addElement(new Entry(attributes, numberOfAttributes));
	}

	public void tooManyAttributes(String string) {
		tooManyAttributes++;
	}
	
	private Entry getEntry(int i) {
		return (Entry) entries.elementAt(i);
	}

}
