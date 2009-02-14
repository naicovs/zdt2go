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
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import test.TestHelper;
import jmunit.framework.cldc11.TestCase;

public class InputStreamReaderIteratorTest extends TestCase {

	public InputStreamReaderIteratorTest() {
		super(4, "InputStreamReaderIteratorTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: testEmptyStream(); break;
		case 1: testFilledInputStream(); break;
		case 2: testNextAfterEndOfStream(); break;
		case 3: testNextOnEmptyStream(); break;
		}
	}
	
	public void testEmptyStream() {
		String data = "";
		InputStreamReader inputStream = new InputStreamReader(TestHelper.getInputStreamFromString(data));
		InputStreamReaderIterator iterator;
		try {
			iterator = new InputStreamReaderIterator(inputStream);
			assertEquals("x0", false, iterator.hasNext());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected IOException: "+e);
		}
	}

	public void testFilledInputStream() {
		String data = "asdf";
		InputStreamReader inputStream = new InputStreamReader(TestHelper.getInputStreamFromString(data));
		InputStreamReaderIterator iterator;
		try {
			iterator = new InputStreamReaderIterator(inputStream);
			assertEquals("x0", true, iterator.hasNext());
			assertEquals("x1", 'a', iterator.next());
			assertEquals("x2", true, iterator.hasNext());
			assertEquals("x3", 's', iterator.next());
			assertEquals("x4", true, iterator.hasNext());
			assertEquals("x5", 'd', iterator.next());
			assertEquals("x6", true, iterator.hasNext());
			assertEquals("x7", 'f', iterator.next());
			assertEquals("x8", false, iterator.hasNext());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected IOException: "+e);
		}
	}

	public void testNextAfterEndOfStream() {
		String data = "a";
		InputStreamReader inputStream = new InputStreamReader(TestHelper.getInputStreamFromString(data));
		InputStreamReaderIterator iterator;
		try {
			iterator = new InputStreamReaderIterator(inputStream);
			assertEquals("x0", true, iterator.hasNext());
			assertEquals("x1", 'a', iterator.next());
			assertEquals("x2", false, iterator.hasNext());
			try {
				iterator.next();
				fail("Expected NoSuchElementException");
			} catch (NoSuchElementException e) {
				// expected exception
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected IOException: "+e);
		}
	}

	public void testNextOnEmptyStream() {
		String data = "";
		InputStreamReader inputStream = new InputStreamReader(TestHelper.getInputStreamFromString(data));
		InputStreamReaderIterator iterator;
		try {
			iterator = new InputStreamReaderIterator(inputStream);
			assertEquals("x0", false, iterator.hasNext());
			try {
				iterator.next();
				fail("Expected NoSuchElementException");
			} catch (NoSuchElementException e) {
				// expected exception
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected IOException: "+e);
		}
	}
	
	
}
