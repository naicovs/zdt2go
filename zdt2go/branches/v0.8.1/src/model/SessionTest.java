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
import java.util.Calendar;
import java.util.TimeZone;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import jmunit.framework.cldc11.TestCase;
import test.TestHelper;

public class SessionTest extends TestCase {

	public SessionTest() {
		super(8, "SessionTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: testSimple1(); break;
		case 1: testSimple2(); break;
		case 2: testSimple3(); break;
		case 3: testSimple4(); break;
		case 4: testSimple5(); break;
		case 5: testDateFilter(); break;
		case 6: testSaveAndLoadStatistics(); break;
		case 7: testWrongAnswersMultipleTimes(); break;
		}
	}
	
	private Category getSampleCategoryWith14Entries() {
		String data =
			"塊	块	kuai4	/Zählwort für Rechteckige Dinge, Währungseinheit (Zähl)/	false	3	4	5	01-07-09 22:11\n" +
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
		
		Category category = new Category();
		
		// loading of given stream works
		try {
			category.loadCategoryFromStream(0, inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail("yy1: Unexpected IOException: "+e);
		}
		
		// now we have fourteen entries with the given data
		assertEquals("yy2", 14, category.getEntriesCount());
		
		return category;
	}
	
	public Category getSampleCategoryWithFourRecentlyLearnedEntries() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		
		String data = "";
		data += "搖頭	摇头	yao2 tou2	/Kopf schütteln (S)/\n";
		data += "死心	死心	si3 xin1	/Resignation (u.E.) (S)/	false	4	4	4	"
				+ (calendar.get(Calendar.MONTH)+1)
				+ "-"
				+ calendar.get(Calendar.DAY_OF_MONTH)
				+ "-"
				+ calendar.get(Calendar.YEAR) + " 22:08\n";
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
		data += "打動	打动	da3 dong4	/mitreißen (u.E.) (V)/	false	1	4	7	"
				+ (calendar.get(Calendar.MONTH)+1)
				+ "-"
				+ calendar.get(Calendar.DAY_OF_MONTH)
				+ "-"
				+ calendar.get(Calendar.YEAR) + " 9:42\n";
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
		data += "價錢	价钱	jia4 qian5	/Kurs, Preis (u.E.) (S)/	false	0	3	9	"
				+ (calendar.get(Calendar.MONTH)+1)
				+ "-"
				+ calendar.get(Calendar.DAY_OF_MONTH)
				+ "-"
				+ calendar.get(Calendar.YEAR) + " 9:40\n";
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
		data += "便	便	bian2	/kurze Zeit später/billig, kostengünstig (u.E.)/	false	1	3	4	"
				+ (calendar.get(Calendar.MONTH)+1)
				+ "-"
				+ calendar.get(Calendar.DAY_OF_MONTH)
				+ "-"
				+ calendar.get(Calendar.YEAR) + " 9:34";
		
		InputStream inputStream = TestHelper.getInputStreamFromString(data);
		
		Category category = new Category();
		
		// loading of given stream works
		try {
			category.loadCategoryFromStream(0, inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			fail("yy1: Unexpected IOException: "+e);
		}
		
		// now we have five entries with the given data
		assertEquals("yy2", 5, category.getEntriesCount());
		
		return category;
	}
	
	public void testSimple1() {
		Category category = getSampleCategoryWith14Entries();

		Filter filter = new Filter();

		Session session = new Session(filter, category);
		
		assertEquals("x1", 14, session.getRemainingCards());
		assertEquals("x2", 0, session.getCurrentCardNumber());
		assertEquals("x3", 14, session.getTotalCards());
		assertEquals("x4", false, session.hasFinished());
		
		boolean occurence[] = new boolean[14];
		for (int i=0; i<occurence.length; i++) {
			occurence[i] = false;
		}

		Entry entry = session.popCard();
		session.pushIncorrectCard(entry);
		assertEquals("x5", 1, session.getCurrentCardNumber());

		while (!session.hasFinished()) {
			entry = session.popCard();
			session.pushCorrectCard(entry);
			// mark that element occurred
			String element = entry.getElement(Attribute.TRADITIONAL);
			for (int i=0; i<category.getEntriesCount(); i++) {
				String temp = ((Entry)category.getEntry(i)).getElement(Attribute.TRADITIONAL);
				if (temp.compareTo(element)==0) {
					occurence[i] = true;
					break;
				}
			}
		}
		assertEquals("x6", 15, session.getCurrentCardNumber());
		for (int i=0; i<occurence.length; i++) {
			assertEquals("x7."+i, true, occurence[i]);
		}
	}
	
	public void testSimple2() {
		Category category = getSampleCategoryWith14Entries();

		Filter filter = new Filter();
		filter.maximumNumber = 5;

		Session session = new Session(filter, category);

		assertEquals("x1", 5, session.getRemainingCards());
		assertEquals("x2", 0, session.getCurrentCardNumber());
		assertEquals("x3", 5, session.getTotalCards());
		assertEquals("x4", false, session.hasFinished());
	}
	
	public void testSimple3() {
		Category category = getSampleCategoryWith14Entries();

		Filter filter = new Filter();
		filter.maximumTimesTested = 5;

		Session session = new Session(filter, category);

		assertEquals("x1", 7, session.getRemainingCards());
		assertEquals("x2", 0, session.getCurrentCardNumber());
		assertEquals("x3", 7, session.getTotalCards());
		assertEquals("x4", false, session.hasFinished());
	}
	
	public void testSimple4() {
		Category category = getSampleCategoryWith14Entries();

		Filter filter = new Filter();
		filter.maximumNumber = 3;
		filter.maximumTimesTested = 5;

		Session session = new Session(filter, category);

		assertEquals("x1", 3, session.getRemainingCards());
		assertEquals("x2", 0, session.getCurrentCardNumber());
		assertEquals("x3", 3, session.getTotalCards());
		assertEquals("x4", false, session.hasFinished());
	}
	
	public void testSimple5() {
		Category category = getSampleCategoryWith14Entries();

		Filter filter = new Filter();
		filter.maximumStreak = 1;

		Session session = new Session(filter, category);

		assertEquals("x1", 9, session.getRemainingCards());
		assertEquals("x2", 0, session.getCurrentCardNumber());
		assertEquals("x3", 9, session.getTotalCards());
		assertEquals("x4", false, session.hasFinished());
	}
	
	public void testDateFilter() {
		Category category;
		Filter filter = new Filter();
		Session session;

		filter.minimumDaysAgo = 1;
		category = getSampleCategoryWithFourRecentlyLearnedEntries();
		session = new Session(filter, category);
		assertEquals("x1", 4, session.getTotalCards());

		filter.minimumDaysAgo = 2;
		category = getSampleCategoryWithFourRecentlyLearnedEntries();
		session = new Session(filter, category);
		assertEquals("x2", 3, session.getTotalCards());

		filter.minimumDaysAgo = 3;
		category = getSampleCategoryWithFourRecentlyLearnedEntries();
		session = new Session(filter, category);
		assertEquals("x3", 2, session.getTotalCards());

		filter.minimumDaysAgo = 4;
		category = getSampleCategoryWithFourRecentlyLearnedEntries();
		session = new Session(filter, category);
		assertEquals("x4", 1, session.getTotalCards());
	}
	
	public void testWrongAnswersMultipleTimes() {
		// execute 5 times to minimize effects of random numbers
		for (int i=0; i<5; i++) {
			wrongAnswers();
		}
	}
	
	public void wrongAnswers() {
		Category category;
		Filter filter = new Filter();
		Session session;
		
		assertEquals("INSERT_LAST_POSITIONS", 2, Session.INSERT_LAST_POSITIONS);
		
		category = getSampleCategoryWith14Entries();
		session = new Session(filter, category);
		assertEquals("x1", 14, session.getTotalCards());
		
		Entry entry = session.popCard();
		session.pushIncorrectCard(entry);
		assertTrue("x2", session.getCard(11).equals(entry) || session.getCard(12).equals(entry) || session.getCard(13).equals(entry));

		for (int i=0; i<10; i++) {
			entry = session.popCard();
			session.pushCorrectCard(entry);
		}
		assertEquals("x3", 4, session.getRemainingCards());
		
		// 4 cards remaining
		
		entry = session.popCard();
		assertTrue("x4", !session.getCard(0).equals(entry) && !session.getCard(1).equals(entry) && !session.getCard(2).equals(entry));
		session.pushIncorrectCard(entry);
		assertTrue("x5", session.getCard(1).equals(entry) || session.getCard(2).equals(entry) || session.getCard(3).equals(entry));

		// 4 cards remaining
		
		entry = session.popCard();
		session.pushCorrectCard(entry);
		assertEquals("x6", 3, session.getRemainingCards());

		// 3 cards remaining
		
		entry = session.popCard();
		session.pushIncorrectCard(entry);
		assertTrue("x7", session.getCard(2).equals(entry));

		// 3 cards remaining
		
		entry = session.popCard();
		session.pushCorrectCard(entry);
		assertEquals("x8", 2, session.getRemainingCards());
		
		// 2 cards remaining
		
		entry = session.popCard();
		session.pushIncorrectCard(entry);
		assertTrue("x9", session.getCard(1).equals(entry));
		
		// 2 cards remaining
		
		entry = session.popCard();
		session.pushCorrectCard(entry);
		assertEquals("x10", 1, session.getRemainingCards());
		
		// 1 card remaining
		
		entry = session.popCard();
		session.pushIncorrectCard(entry);
		assertTrue("x11", session.getCard(0).equals(entry));
		
		// 1 card remaining
		
		entry = session.popCard();
		session.pushCorrectCard(entry);
		assertEquals("x12", 0, session.getRemainingCards());
		
		assertEquals("x13", 14, session.getTotalCards());
	}
	
	public void testSaveAndLoadStatistics() {
		
		RecordStore recordStores[] = new RecordStore[1];
		RecordStore rs;

		// delete any data that may be left from previous test runs
		try {
			RecordStore.deleteRecordStore(TestHelper.RECORD_STORE_TEST_NAME);
		} catch (RecordStoreNotFoundException e) {
			// normally this record store does not exist
		} catch (RecordStoreException e) {
			e.printStackTrace();
			fail();
		}

	
		try {
			Category category = getSampleCategoryWith14Entries();
			Filter filter = new Filter();
			Session session = new Session(filter, category, null);
			assertEquals("x1", 14, session.getRemainingCards());
			
			// learn data
			while (!session.hasFinished()) {
				Entry entry = session.popCard();
				session.pushCorrectCard(entry);
			}
			
			rs = RecordStore.openRecordStore(TestHelper.RECORD_STORE_TEST_NAME, true);
			recordStores[0] = rs;
			assertNotNull("x2", rs);
			
			// save data
			session.saveData(recordStores);
			assertEquals("x3", 14, rs.getNumRecords());

			// load and check data
			session = loadAndCheckData(recordStores, 1);
			
			// load data again
			category = getSampleCategoryWith14Entries();
			session = new Session(filter, category, recordStores);
			
			// learn and save again
			while (!session.hasFinished()) {
				Entry entry = session.popCard();
				session.pushCorrectCard(entry);
			}
			session.saveData(recordStores);

			// make sure data got updated and not added again
			assertEquals("x11", 14, rs.getNumRecords());
			
			// load and check data again
			session = loadAndCheckData(recordStores, 2);
			
			rs.closeRecordStore();
			RecordStore.deleteRecordStore(TestHelper.RECORD_STORE_TEST_NAME);
			
		} catch (RecordStoreFullException e) {
			e.printStackTrace();
			fail("see previous exception: "+e.getMessage());
		} catch (RecordStoreNotFoundException e) {
			e.printStackTrace();
			fail("see previous exception: "+e.getMessage());
		} catch (RecordStoreException e) {
			e.printStackTrace();
			fail("see previous exception: "+e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail("see previous exception: "+e.getMessage());
		}

	}

	private Session loadAndCheckData(RecordStore[] recordStores, int run) {
		Category category;
		Session session;
		session = new Session();
		category = getSampleCategoryWith14Entries();
		try {
			session.loadData(category, recordStores);
		} catch (RecordStoreException e) {
			e.printStackTrace();
			fail("Unexpected exception: "+e);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected exception: "+e);
		}
		assertEquals("x4."+run, 14, session.getRemainingCards());
		
		// check if statistics were correctly saved and loaded
		int count = 0;
		while (!session.hasFinished()) {
			Entry loadedEntry = session.popCard();
			assertTrue("x5."+run, loadedEntry.getRecordId()>0);
			Category originalCategory = getSampleCategoryWith14Entries();
			for (int i=0; i<originalCategory.getEntriesCount(); i++) {
				Entry originalEntry = originalCategory.getEntry(i);
				if (originalEntry.getElement(Attribute.SIMPLIFIED).compareTo(loadedEntry.getElement(Attribute.SIMPLIFIED))==0) {
					assertEquals("x6."+run, originalEntry.getNumberCorrect()+run, loadedEntry.getNumberCorrect());
					assertEquals("x7."+run, originalEntry.getTimesTested()+1+run, loadedEntry.getTimesTested());
					assertEquals("x8."+run, originalEntry.getCorrectStreak()+run, loadedEntry.getCorrectStreak());
					if (originalEntry.getLastTested()!=null) {
						assertEquals("x9."+run, true, loadedEntry.getLastTested().getTime()>originalEntry.getLastTested().getTime());
					}
					count++;
					break;
				}
			}
		}
		assertEquals("x10."+run, 14, count);
		return session;
	}
	

}
