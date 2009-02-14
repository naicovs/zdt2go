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

import java.util.TimeZone;

import jmunit.framework.cldc11.TestCase;

public class DateHelperTest extends TestCase {

	public DateHelperTest() {
		super(1, "DateHelperTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: testConvertStringToDate(); break;
		}
	}
	
	
	public void testConvertStringToDate() {
		long timeZoneOffset = TimeZone.getDefault().getRawOffset();

		assertNotNull("x1", DateHelper.convertStringToDate("03-20-99 19:45"));
		assertEquals("x2", 921959100000L-timeZoneOffset, DateHelper.convertStringToDate("03-20-99 19:45").getTime());
		
		assertNotNull("x3", DateHelper.convertStringToDate("12-18-08 10:19"));
		assertEquals("x4", 1229595540000L-timeZoneOffset, DateHelper.convertStringToDate("12-18-08 10:19").getTime());
		
		assertNotNull("x5", DateHelper.convertStringToDate("3-1-01 9:1"));
		assertEquals("x6", 983437260000L-timeZoneOffset, DateHelper.convertStringToDate("3-1-01 9:1").getTime());
		
		assertNull("x7", DateHelper.convertStringToDate("03-20-99 99:45"));
		assertNull("x8", DateHelper.convertStringToDate("03/20/99 09:45"));
	}

}
