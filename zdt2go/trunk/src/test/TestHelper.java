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
package test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import jmunit.framework.cldc11.TestCase;

public class TestHelper extends TestCase {
	
	public static final String RECORD_STORE_TEST_NAME = "TEST_TEST_TEST_STORE";

	public TestHelper() {
		super(0, "TestHelper");
		fail("cannot be instantiated");
	}
	
	public void test(int testNumber) throws Throwable {
		fail("cannot be called");
	}

	public static final InputStream getInputStreamFromString(String data) {
		InputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(data.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail("UnsupportedEncodingException: "+e.getMessage());
		}
		return inputStream;
	}
	
	/**
	 * Helper for Hammock mock objects
	 */
//	public static final void addMethodHandlerForInputStream_MTHD_READ(CompositeHandler handler, InputStream inputStream) {
//		int c = 0;
//		do {
//			try {
//				c = inputStream.read();
//			} catch (IOException e) {
//				e.printStackTrace();
//				fail("IOException: "+e.getMessage());
//			}
//			handler.addMethodHandler(MockInputStream.MTHD_READ).setReturnValue(new Integer(c));
//		} while (c != -1);
//	}


}
