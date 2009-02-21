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

import model.helper.DateHelper;

import jmunit.framework.cldc11.TestCase;

public class StatisticsTest extends TestCase {

	public StatisticsTest() {
		super(1, "StatisticsTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: testSaveAndLoad(); break;
		}
	}
	
	
	public void testSaveAndLoad() {
		String data[] = { "traditional", "simplified", "pinyin", "meaning",
				"false", "3", "4", "7", "12-18-08 10:19" };
		Statistics statistics = new Statistics(data.length, data);
		
		assertEquals("x1", false, statistics.getLearned());
		assertEquals("x2", 3, statistics.getCorrectStreak());
		assertEquals("x3", 4, statistics.getNumberCorrect());
		assertEquals("x4", 7, statistics.getTimesTested());
		assertEquals("x5", DateHelper.convertStringToDate("12-18-08 10:19").getTime(), statistics.getLastTested().getTime());
		
		try {
			// save data
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			statistics.serialize(dout);
			dout.close();
			// load data
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			DataInputStream din = new DataInputStream(bin);
			statistics = new Statistics(din);
			din.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertEquals("x6", false, statistics.getLearned());
		assertEquals("x7", 3, statistics.getCorrectStreak());
		assertEquals("x8", 4, statistics.getNumberCorrect());
		assertEquals("x9", 7, statistics.getTimesTested());
		assertEquals("x10", DateHelper.convertStringToDate("12-18-08 10:19").getTime(), statistics.getLastTested().getTime());
		
		

	}

}
