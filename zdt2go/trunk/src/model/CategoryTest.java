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

import model.helper.DateHelper;

import jmunit.framework.cldc11.TestCase;
import test.TestHelper;

public class CategoryTest extends TestCase {
	
	private Category category;

	public CategoryTest() {
		super(1, "CategoryTest");
	}
	
	public void setUp() {
		category = new Category();
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: testLoadCategoryFromStream(); break;
		}
	}
	
	public void testLoadCategoryFromStream() {
		String data = "塊	块	kuai4	/Zählwort für Rechteckige Dinge, Währungseinheit (Zähl)/	false	3	4	5	01-07-09 22:11\n" +
		"岸	岸	an4	/Bank, Ufer/Küste/Strand (S)/groß/großartig (S);/	false	1	4	8	01-08-09 9:47\n" +
		"遊客	游客	you2 ke4	/Besucher (u.E.) (S)/Tourist (u.E.) (S)/touristisch (u.E.) (Adj)/	false	1	4	6	01-08-09 9:32\n" +
		"趕忙	赶忙	gan3 mang2	/eilen (u.E.)/	false	4	4	5	01-07-09 22:00\n" +
		"拉	拉	la1	/ziehen (u.E.) (V)/Geige spielen (u.E.) (V, Mus)/La (u.E.) (Eig, Fam)/	false	4	4	4	01-08-09 9:20\n" +
		"轉身	转身	zhuan3 shen1	/wenden (u.E.) (V)/dreht (u.E.)/kehrt (u.E.)/Dreh (u.E.) (S)/drehen (u.E.) (V)/kehren (u.E.) (V)/schwenken (u.E.) (V);/	false	1	4	6	01-08-09 9:45\n" +
		"石頭	石头	shi2 tou5	/Gestein, Stein,  Kern (u.E.) (S)/	false	1	4	5	01-08-09 9:42\n" +
		"懷	怀	huai2	/Brust/schwanger werden, ein Kind empfangen (u.E.)/gedenken (u.E.) (V)/schätzen, hegen (u.E.)/	false	3	4	6	01-08-09 9:20\n" +
		"似乎	似乎	si4 hu1	/als ob (u.E.)/anscheinend, als ob (u.E.)/scheinbar, anscheinende (u.E.)/auftreten, erscheinen (u.E.) (V)/scheinen, deuchen (u.E.) (V)/dem Anschein nach (u.E.) (Adv)/	false	1	4	8	01-08-09 9:43\n" +
		"死心	死心	si3 xin1	/Resignation (u.E.) (S)/	false	4	4	4	01-07-09 22:08\n" +
		"搖頭	摇头	yao2 tou2	/Kopf schütteln (S)/\n" +
		"打動	打动	da3 dong4	/mitreißen (u.E.) (V)/	false	1	4	7	01-08-09 9:42\n" +
		"價錢	价钱	jia4 qian5	/Kurs, Preis (u.E.) (S)/	false	0	3	9	01-08-09 9:40\n" +
		"便	便	bian2	/kurze Zeit später/billig, kostengünstig (u.E.)/	false	1	3	4	01-08-09 9:34";

		InputStream inputStream = TestHelper.getInputStreamFromString(data);
		
		// loading of null stream fails
		try {
			category.loadCategoryFromStream(0, null);
			fail("x0: Load from null stream has not failed.");
		} catch (NullPointerException e) {
			// expect this exception
		} catch (IOException e) {
			e.printStackTrace();
			fail("x0: Loading from null stream gave unexpected IOException: "+e);
		}

		// loading of given stream works
		try {
			category.loadCategoryFromStream(0, inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			fail("x1: Loading of given stream failed: "+e);
		}
		
		// now we have fourteen entries with the given data
		assertEquals("x2", 14, category.getEntriesCount());
		
		// 0th entry
		Entry entry = category.getEntry(0);
		assertEquals("x3", "塊", entry.getElement(Attribute.TRADITIONAL));
		assertEquals("x4", "块", entry.getElement(Attribute.SIMPLIFIED));
		assertEquals("x5", "kuai4", entry.getElement(Attribute.PINYIN));
		assertEquals("x6", "Zählwort für Rechteckige Dinge, Währungseinheit (Zähl)", entry.getElement(Attribute.MEANING));
		assertEquals("x7", false, entry.getLearned());
		assertEquals("x8", 3, entry.getCorrectStreak());
		assertEquals("x9", 4, entry.getNumberCorrect());
		assertEquals("x10", 5, entry.getTimesTested());
		assertEquals("x11", DateHelper.convertStringToDate("01-07-09 22:11"), entry.getLastTested());
		
		// 5th entry
		entry = category.getEntry(5);
		assertEquals("x12", "轉身", entry.getElement(Attribute.TRADITIONAL));
		assertEquals("x13", "转身", entry.getElement(Attribute.SIMPLIFIED));
		assertEquals("x14", "zhuan3 shen1", entry.getElement(Attribute.PINYIN));
		assertEquals("x15", "wenden (u.E.) (V); dreht (u.E.); kehrt (u.E.); Dreh (u.E.) (S); drehen (u.E.) (V); kehren (u.E.) (V); schwenken (u.E.) (V);", entry.getElement(Attribute.MEANING));
		assertEquals("x16", false, entry.getLearned());
		assertEquals("x17", 1, entry.getCorrectStreak());
		assertEquals("x18", 4, entry.getNumberCorrect());
		assertEquals("x19", 6, entry.getTimesTested());
		assertEquals("x20", DateHelper.convertStringToDate("01-08-09 9:45"), entry.getLastTested());		
		
		// 10th entry
		entry = category.getEntry(10);
		assertEquals("x21", false, entry.getLearned());
		assertEquals("x22", 0, entry.getCorrectStreak());
		assertEquals("x23", 0, entry.getNumberCorrect());
		assertEquals("x24", 0, entry.getTimesTested());
		assertNull("x25", entry.getLastTested());
		
		// 12th entry
		entry = category.getEntry(12);
		assertEquals("x26", "價錢", entry.getElement(Attribute.TRADITIONAL));

		// 13th entry
		entry = category.getEntry(13);
		assertEquals("x27", "便", entry.getElement(Attribute.TRADITIONAL));
	}

}
