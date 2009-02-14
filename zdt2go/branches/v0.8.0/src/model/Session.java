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
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * The Session class represents a learning session.
 * 
 * @author Achim Weimert
 *
 */
class Session {

	/**
	 * Specifies the position where to add a wrongly answered card.
	 * 
	 * A value of 0 means adding the card at the end, 1 means adding the card to
	 * the end or inserting it before the current last card, and so on.
	 */
	static final int INSERT_LAST_POSITIONS = 2;

	/**
	 * Instance of a random number generator.
	 */
	private static Random random = new Random(System.currentTimeMillis());
	private Vector cards = new Vector();
	private Vector finishedCards = new Vector();
	private int cardCount = 0;
	private Filter filter = new Filter();
	
	/**
	 * Create an empty session
	 */
	public Session() {
		setFilter(null);
	}
	
	/**
	 * Creates a session without updating statistics
	 * @param filter
	 * @param data
	 */
	public Session(Filter filter, Category data) {
		setFilter(filter);
		try {
			loadData(data, null);
		} catch (RecordStoreException e) {
			// can't occur as recordStores is null
			e.printStackTrace();
		} catch (IOException e) {
			// can't occur as recordStores is null
			e.printStackTrace();
		}
	}

	/**
	 * Creates a session and optionally updates statistics.
	 * 
	 * @param filter
	 * @param data
	 * @param recordStores may be null
	 * @throws IOException 
	 * @throws RecordStoreException 
	 */
	public Session(Filter filter, Category data, RecordStore recordStores[]) throws RecordStoreException, IOException {
		setFilter(filter);
		loadData(data, recordStores);
	}
	
	/**
	 * Loads a session from the given data and optionally updates it statistics
	 * from the given recordStores.
	 * 
	 * @param data
	 * @param recordStores
	 *            may be null
	 * @return values smaller zero indicate errors
	 * @throws IOException
	 * @throws RecordStoreException
	 */
	public void loadData(Category data, RecordStore recordStores[]) throws RecordStoreException, IOException {
		int numberOfEntries = data.getEntriesCount();
		for (int i=0; i<numberOfEntries; i++) {
			// get current entry
			Entry entry = data.getEntry(i);
			// load updated statistics
			loadUpdatedStatistics(entry, recordStores);
			// add entry
			addEntry(entry);
		}
	}

	/**
	 * Try to update statistics of current entry with data from RecordStore.
	 * 
	 * @param entry
	 * @param recordStores
	 * @return negative values on error, else 0 or positive values
	 * @throws RecordStoreException 
	 * @throws IOException 
	 */
	private void loadUpdatedStatistics(Entry entry, RecordStore recordStores[]) throws RecordStoreException, IOException {
		// check if statistics are available
		if (recordStores==null) {
			return;
		}
		// check if statistics for the entry's category are available
		RecordStore rs = recordStores[entry.getCategoryId()];
		if (rs==null) {
			return;
		}
		// find entry
		RecordEnumeration enum = rs.enumerateRecords(null, null, false);
		while (enum.hasNextElement()) {
			int recordId = enum.nextRecordId();
			byte bytes[] = rs.getRecord(recordId);
			if (entry.tryUpdatingStats(recordId, bytes)) {
				break;
			}
		}
	}
	
	/**
	 * Replace the current filter
	 * @param filter
	 */
	public void setFilter(Filter filter) {
		if (filter==null) {
			this.filter = new Filter();
		} else {
			this.filter = filter;
		}
	}

	/**
	 * Adds the given entry to the lesson if it passes all filters
	 * @param entry
	 */
	private void addEntry(Entry entry) {
		
		// check filters
		if (filter.maximumStreak >= 0
				&& entry.getCorrectStreak() > filter.maximumStreak) {
			return;
		}
		if (entry.getPercentage() > filter.maximumPercentage) {
			return;
		}
		if (filter.maximumTimesTested >= 0
				&& entry.getTimesTested() > filter.maximumTimesTested) {
			return;
		}

		// entries without a data have never been tested and therefore are
		// always included
		if (filter.minimumDaysAgo > 0 && entry.getLastTested() != null) {

			// reset hour/min/sec/millisec to zero to only compare days
			Calendar calendarToday = Calendar.getInstance();
			calendarToday.set(Calendar.HOUR_OF_DAY, 0);
			calendarToday.set(Calendar.MINUTE, 0);
			calendarToday.set(Calendar.SECOND, 0);
			calendarToday.set(Calendar.MILLISECOND, 0);

			// reset hour/min/sec/millisec to zero to only compare days
			Calendar calendarEntry = Calendar.getInstance();
			calendarEntry.setTime(entry.getLastTested());
			calendarEntry.set(Calendar.HOUR_OF_DAY, 0);
			calendarEntry.set(Calendar.MINUTE, 0);
			calendarEntry.set(Calendar.SECOND, 0);
			calendarEntry.set(Calendar.MILLISECOND, 0);

			if (calendarToday.getTime().getTime()
					- calendarEntry.getTime().getTime() < filter.minimumDaysAgo
					* 24 * 60 * 60 * 1000) {
				return;
			}
		}
		
		entry.startNewTest();
		
		if (filter.maximumNumber<=0 || cards.size()<filter.maximumNumber) {
			// copy vocabulary-entry to a random position in cards
			cards.insertElementAt(entry, random.nextInt(cards.size()+1));
		} else {
			if (random.nextInt(2)==0) return;
			// replace a random card
			cards.setElementAt(entry, random.nextInt(cards.size()));
		}
	}
	
	/**
	 * returns and removes the next card that should be asked
	 * @return
	 */
	public Entry popCard() {
		if (cards.isEmpty()) {
			return null;
		}
		// get element
		Entry card = (Entry)cards.firstElement();
		// remove element from list
		cards.removeElementAt(0);
		// increment card counter
		cardCount++;
		// return element
		return card;
	}
	
	/**
	 * re-adds an incorrectly answered card
	 * @param entry
	 */
	public void pushIncorrectCard(Entry entry) {
		// correct streak has been canceled
		entry.markAsWronglyAnswered();
		int position;
		if (INSERT_LAST_POSITIONS>=cards.size()) {
			position = cards.size();
		} else {
			// add element at random position of the INSERT_LAST_POSITIONS last positions
			int max = INSERT_LAST_POSITIONS + 1;
			position = cards.size() - max + 1 + random.nextInt(max);
		}
		cards.insertElementAt(entry, position);
	}
	
	/**
	 * adds correctly answered card to finished cards
	 * @param entry
	 */
	public void pushCorrectCard(Entry entry) {
		entry.markAsCorrectlyAnswered();
		finishedCards.addElement(entry);
	}
	
	/**
	 * checks if there are still cards left in the lesson
	 * @return
	 */
	public boolean hasFinished() {
		if (cards.size()==0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * returns the number of cards still remaining
	 * @return
	 */
	public int getRemainingCards() {
		return cards.size();
	}
	
	/**
	 * return the total number of cards
	 * @return
	 */
	public int getTotalCards() {
		return finishedCards.size()+cards.size();
	}
	
	/**
	 * return the number of the current card
	 * @return the number of the current card
	 */
	public int getCurrentCardNumber() {
		return cardCount;
	}
	
	/**
	 * Retrieves the card at the specified position.
	 * 
	 * @param cardId the id of the card to return
	 * @return the card entry
	 */
	public Entry getCard(int cardId) {
		return (Entry) cards.elementAt(cardId);
	}
	
	/**
	 * saves statistics to recordstore
	 * @param recordStores
	 * @throws IOException 
	 * @throws RecordStoreException 
	 * @throws RecordStoreFullException 
	 * @throws InvalidRecordIDException 
	 * @throws RecordStoreNotOpenException 
	 */
	public void saveData(RecordStore recordStores[]) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreFullException, RecordStoreException {
		for (int i = 0; i < finishedCards.size(); i++) {
			Entry entry = (Entry) finishedCards.elementAt(i);
			byte bytes[] = entry.serialize();
			// check if entry already exists in RecordStore
			if (entry.getRecordId() > 0) {
				updateExistingRecord(recordStores, entry.getCategoryId(), entry
						.getRecordId(), bytes);
			} else {
				addNewRecord(recordStores, entry.getCategoryId(), bytes);
			}
		}
	}

	/**
	 * Add a new record to the store to save new statistics
	 * @param recordStores
	 * @param categoryId
	 * @param bytes
	 * @throws RecordStoreNotOpenException
	 * @throws RecordStoreException
	 * @throws RecordStoreFullException
	 */
	private void addNewRecord(RecordStore[] recordStores, int categoryId,
			byte[] bytes) throws RecordStoreNotOpenException,
			RecordStoreException, RecordStoreFullException {
		recordStores[categoryId].addRecord(bytes, 0, bytes.length);
	}

	/**
	 * Update the existing record with new statistics
	 * @param recordStores
	 * @param categoryId
	 * @param recordId
	 * @param bytes
	 * @throws RecordStoreNotOpenException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreException
	 * @throws RecordStoreFullException
	 */
	private void updateExistingRecord(RecordStore[] recordStores,
			int categoryId, int recordId, byte[] bytes)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException, RecordStoreFullException {
		recordStores[categoryId].setRecord(recordId, bytes, 0, bytes.length);
	}

}
