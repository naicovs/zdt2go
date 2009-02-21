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

import test.TestHelper;

import jmunit.framework.cldc11.TestCase;

public class EntryTest extends TestCase {
	
	public EntryTest() {
		super(7, "EntryTest");
	}
	
	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: testGetHash(); break;
		case 1: testCompleteDataConversion(); break;
		case 2: testPartsDataConversion(); break;
		case 3: testFormatMeaning(); break;
		case 4: testFormatPinyin(); break;
		case 5: testStringReplace(); break;
		case 6: testCharReplace(); break;
		}
	}
	
	public void testCompleteDataConversion() {
		String data[] = { "traditional", "simplified", "pinyin", "meaning",
				"false", "3", "4", "7", "12-18-08 10:19" };
		Entry entry = new Entry(12, data, 9);
		assertEquals(12, entry.getCategoryId());
		assertEquals(0, entry.getElement(Attribute.TRADITIONAL).compareTo("traditional"));
		assertEquals(0, entry.getElement(Attribute.SIMPLIFIED).compareTo("simplified"));
		assertEquals(0, entry.getElement(Attribute.PINYIN).compareTo("pinyin"));
		assertEquals(0, entry.getElement(Attribute.MEANING).compareTo("meaning"));
		assertEquals(false, entry.getLearned());
		assertEquals(3, entry.getCorrectStreak());
		assertEquals(4, entry.getNumberCorrect());
		assertEquals(7, entry.getTimesTested());
		assertEquals(DateHelper.convertStringToDate("12-18-08 10:19").getTime(), entry.getLastTested().getTime());
	}
	
	
	public void testPartsDataConversion() {
		String data[] = { "traditional", "simplified", "pinyin", "meaning",
				"false", "3", "4", "7", "12-18-08 10:19" };
		Entry entry = new Entry(1, data, 5);
		assertEquals(0, entry.getElement(Attribute.TRADITIONAL).compareTo("traditional"));
		assertEquals(0, entry.getElement(Attribute.SIMPLIFIED).compareTo("simplified"));
		assertEquals(0, entry.getElement(Attribute.PINYIN).compareTo("pinyin"));
		assertEquals(0, entry.getElement(Attribute.MEANING).compareTo("meaning"));
		assertEquals(false, entry.getLearned());
		assertEquals(0, entry.getCorrectStreak());
		assertEquals(0, entry.getNumberCorrect());
		assertEquals(0, entry.getTimesTested());
		assertNull(entry.getLastTested());
	}
	
	public void testFormatMeaning() {
		assertEquals("at last; in the end; finally; eventually", Entry.formatMeaning("/at last/in the end/finally/eventually/"));
		assertEquals("truth", Entry.formatMeaning("/truth/"));
	}
	
	public void testFormatPinyin() {
		assertEquals("x1", "da1 ying", Entry.formatPinyin("da1 ying5"));
		assertEquals("x2", "nü3 hai2", Entry.formatPinyin("nu:3 hai2"));
	}
	
	public void testStringReplace() {
		assertEquals("asdasdasdasdf", Entry.stringReplace("asdfasdfasdfasdf", "fa", "a"));
	}
	
	public void testCharReplace() {
		assertEquals("asdaasdaasdaasda", Entry.charReplace("asdfasdfasdfasdf", 'f', "a"));
	}
	
	
	public void testGetHash() {
		Category category = new Category();

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

		// loading of given stream works
		try {
			category.loadCategoryFromStream(0, inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail("x1: Unexpected IOException: "+e);
		}
		
		// now we have fourteen entries with the given data
		assertEquals("x2", 14, category.getEntriesCount());
		
		//for (int i=0; i<14; i++) {
		//	System.out.println("assertEquals(\"x"+(3+i)+"\", "+loadCategory.getEntry(i).getHash()+"L, loadCategory.getEntry("+i+").getHash());");
		//}
		
		// 0th entry
		assertEquals("x3", 5160263390228886343L, category.getEntry(0).getHash());
		assertEquals("x4", -1226928685609727906L, category.getEntry(1).getHash());
		assertEquals("x5", -3763044595089470060L, category.getEntry(2).getHash());
		assertEquals("x6", -41533355443112839L, category.getEntry(3).getHash());
		assertEquals("x7", 2751936999819311711L, category.getEntry(4).getHash());
		assertEquals("x8", -3830499639762806749L, category.getEntry(5).getHash());
		assertEquals("x9", -5341781406701724392L, category.getEntry(6).getHash());
		assertEquals("x10", -3233910702769060052L, category.getEntry(7).getHash());
		assertEquals("x11", 8113182401606027305L, category.getEntry(8).getHash());
		assertEquals("x12", 2556662420537265631L, category.getEntry(9).getHash());
		assertEquals("x13", 8177995039756453924L, category.getEntry(10).getHash());
		assertEquals("x14", 3358449200623622399L, category.getEntry(11).getHash());
		assertEquals("x15", 5589470510569479263L, category.getEntry(12).getHash());
		assertEquals("x16", 3063879314406736620L, category.getEntry(13).getHash());

	}
	
}
