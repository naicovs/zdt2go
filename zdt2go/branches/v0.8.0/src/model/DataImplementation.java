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

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import model.CategoryList.FileCategory;

/**
 * The DataImplementation class represents the applications main data object. 
 * 
 * @author Achim Weimert
 *
 */
public class DataImplementation implements Data {
	
	private Settings settings;
	private CategoryList categoryList;
	private Category category;
	private FileCategory[] currentCategories = null;
	private Session session;
	private Entry currentEntry;
	private Filter filter = new Filter();
	private boolean[] cardDisplayAttributes;
	private Thread loadSessionThread;
	private Thread saveSessionThread;
	
	/**
	 * Names for vocabulary types (order as in ZDT-file format) 
	 */
	static final int TRADITIONAL = 0;
	static final int SIMPLIFIED = 1;
	static final int PINYIN = 2;
	static final int MEANING = 3;
	/**
	 * Names for statistic data (order as in ZDT-file format)
	 */
	static final int STATS_LEARNED = 4;
	static final int STATS_CORRECT_STREAK = 5;
	static final int STATS_NUMBER_CORRECT = 6;
	static final int STATS_TIMES_TESTED = 7;
	static final int STATS_LAST_TESTED = 8;
	/**
	 * Maximum number of data per entry
	 */
	static final int STATS_MAX_ATTRIBUTES = 9;

	public void loadSettings(String midletVersion, String midletSize)
			throws RecordStoreFullException, RecordStoreNotFoundException,
			RecordStoreException, IOException {
		settings = new Settings(midletVersion, midletSize);
	}
	
	public void loadCategoryList() throws IOException {
		categoryList = new CategoryList();
		categoryList.loadCategories();
	}

	public String getCategoryName(int i) {
		if (categoryList!=null && categoryList.getCategory(i)!=null) {
			return categoryList.getCategory(i).getName();
		}
		return null;
	}

	public int getCountCategories() {
		if (categoryList!=null) {
			return categoryList.countCategories();
		}
		return 0;
	}
	
	public void selectCategories(int index[]) throws IOException {
		if (categoryList==null) {
			return;
		}
		filter = new Filter();
		category = new Category();
		currentCategories = new FileCategory[index.length];
		for (int i=0; i<index.length; i++) {
			FileCategory fileCategory = categoryList.getCategory(index[i]);
			if (fileCategory==null) {
				return;
			}
			try {
				category.loadCategoryFromFile(i, fileCategory.getFile());
				currentCategories[i] = fileCategory;
			} catch (IOException e) {
				currentCategories[i] = null;
				throw e;
			}
		}
	}

	public int getSessionRemainingCardsCount() {
		if (session==null) {
			return -1;
		}
		return session.getRemainingCards();
	}

	public int getSessionTotalCards() {
		if (session==null) {
			return -1;
		}
		return session.getTotalCards();
	}

	public void reAddCurrentEntry() {
		session.pushIncorrectCard(currentEntry);
	}
	
	public void addFinishedEntry() {
		session.pushCorrectCard(currentEntry);
	}

	public int getSessionCardNumber() {
		if (session==null) {
			return 0;
		}
		return session.getCurrentCardNumber();
	}

	public String getCurrentSessionEntryAttribute(int attributeID) {
		if (currentEntry==null) return null;
		switch (attributeID) {
		case Attribute.TRADITIONAL:
			return currentEntry.getElement(TRADITIONAL);

		case Attribute.SIMPLIFIED:
			return currentEntry.getElement(SIMPLIFIED);

		case Attribute.PINYIN:
			return currentEntry.getElement(PINYIN);

		case Attribute.MEANING:
			return currentEntry.getElement(MEANING);

		default:
			break;
		}
		return null;
	}

	public boolean loadNextSessionEntry() {
		if (session==null) return false;
		currentEntry = session.popCard();
		return true;
	}

	public boolean isEntryAttributeShown(int attributeID) {
		if (attributeID<0 || attributeID>=4) throw new IllegalArgumentException();
		if (session==null) throw new IllegalStateException();
		return cardDisplayAttributes[attributeID];
	}

	public void saveSessionResult(final ThreadCallback callback) {
		if (session==null) throw new IllegalStateException();
		Runnable runnable = new Runnable() {
			public void run() {
				RecordStore recordStores[] = null;
				try {
					recordStores = openRecordstores(true);
					session.saveData(recordStores);
					callback.threadFinished(null);
				} catch (Exception e) {
					callback.threadFinished(e);
				} finally {
					try {
						// make sure RecordStore is always closed
						closeOpenRecordstores(recordStores);
					} catch (RecordStoreNotOpenException e) {
					} catch (RecordStoreException e) {
					}					
				}
			}
		};
		saveSessionThread = new Thread(runnable);
		saveSessionThread.start();
	}

	public void setFilterMaximumNumberOfEntries(int maximumNumber) {
		filter.maximumNumber = maximumNumber;
	}

	public void setFilterMaximumPercentage(int maximumPercentage) {
		filter.maximumPercentage = maximumPercentage;
	}

	public void setFilterMaximumStreak(int maximumStreak) {
		filter.maximumStreak = maximumStreak;
	}

	public void setFilterMaximumTimesTested(int maximumTimesTested) {
		filter.maximumTimesTested = maximumTimesTested;
	}

	public void setFilterMinimumDaysAgo(int minimumDaysAgo) {
		filter.minimumDaysAgo = minimumDaysAgo;
	}

	public int getFilterMaximumNumberOfEntries() {
		return filter.maximumNumber;
	}

	public int getFilterMaximumPercentage() {
		return filter.maximumPercentage;
	}

	public int getFilterMaximumStreak() {
		return filter.maximumStreak;
	}

	public int getFilterMaximumTimesTested() {
		return filter.maximumTimesTested;
	}

	public int getFilterMinimumDaysAgo() {
		return filter.minimumDaysAgo;
	}

	public void setCardDisplayAttributes(boolean traditional, boolean simplified,
			boolean pinyin, boolean meaning) {
		cardDisplayAttributes = new boolean[4];
		cardDisplayAttributes[Attribute.TRADITIONAL] = traditional;
		cardDisplayAttributes[Attribute.SIMPLIFIED] = simplified;
		cardDisplayAttributes[Attribute.PINYIN] = pinyin;
		cardDisplayAttributes[Attribute.MEANING] = meaning;
	}

	public void loadSession(final ThreadCallback callback) {
		Runnable runnable = new Runnable() {
			public void run() {
				RecordStore recordStores[] = null;
				try {
					recordStores = openRecordstores(false);
					session = new Session();
					session.setFilter(filter);
					session.loadData(category, recordStores);
					callback.threadFinished(null);
				} catch (Exception e) {
					callback.threadFinished(e);
				} finally {
					try {
						// always close record stores
						closeOpenRecordstores(recordStores);
					} catch (RecordStoreNotOpenException e) {
					} catch (RecordStoreException e) {
					}
				}
			}
		};
		loadSessionThread = new Thread(runnable);
		loadSessionThread.start();
	}
	
	// TODO: remove
	public void DEV_deleteAllData() {
		String recordStores[] = RecordStore.listRecordStores();
		if (recordStores==null) {
			return;
		}
		for (int i=0; i<recordStores.length; i++) {
			try {
				RecordStore.deleteRecordStore(recordStores[i]);
			} catch (RecordStoreNotFoundException e) {
				e.printStackTrace();
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Delete all saved statistics now
	 * 
	 * @return number of deleted categories
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 */
	public final int deleteSavedStatistics() throws RecordStoreNotFoundException, RecordStoreException {
		return deleteStatistics(false);
	}
	
	/**
	 * Deletes outdated RecordStores by checking if they have an corresponding
	 * entry in the index file
	 * 
	 * @return number of deleted category statistics
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 */
	public final int deleteOutdatedStatistics() throws RecordStoreNotFoundException, RecordStoreException {
		return deleteStatistics(true);
	}
	
	/**
	 * Deletes category statistics from the RecordStore.
	 * 
	 * @param ignoreExistingCategories true if categories that have an corresponding vocabulary file should not be deleted
	 * @return number of deleted category statistics
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreException
	 */
	private final int deleteStatistics(boolean ignoreExistingCategories) throws RecordStoreNotFoundException, RecordStoreException {
		int countDeleted = 0;
		String recordStores[] = RecordStore.listRecordStores();
		if (recordStores==null) {
			return countDeleted;
		}
		for (int i=0; i<recordStores.length; i++) {
			if (!recordStores[i].startsWith(Data.CATEGORY_PREFIX)) {
				continue;
			}
			if (ignoreExistingCategories && categoryList.doesCategoryExist(recordStores[i])) {
				continue;
			}
			RecordStore.deleteRecordStore(recordStores[i]);
			countDeleted++;
		}
		return countDeleted;
	}
	
	// TODO: remove
	public void DEV_printAllData() {
		String recordStores[] = RecordStore.listRecordStores();
		if (recordStores==null) {
			System.out.println("There is no saved data available.");
			return;
		}
		for (int i=0; i<recordStores.length; i++) {
			try {
				RecordStore rs = RecordStore.openRecordStore(recordStores[i], false);
				System.out.println(recordStores[i] + " has "+rs.getNumRecords()+" records");
				rs.closeRecordStore();
			} catch (RecordStoreNotFoundException e) {
				e.printStackTrace();
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean getSettingsSaveUpdatedStatistics() {
		return settings.getSaveUpdatedStatistics();
	}

	public void setSettingsSaveUpdatedStatistics(boolean saveUpdatedStatistics) {
		settings.setSaveUpdatedStatistics(saveUpdatedStatistics);
	}

	/**
	 * Opens all existing record stores for the selected categoryList
	 * @return array of RecordStore or null if settings prohibit loading
	 * @throws RecordStoreException
	 * @throws RecordStoreFullException
	 */
	private RecordStore[] openRecordstores(boolean createIfNecessary) throws RecordStoreException,
			RecordStoreFullException {
		// make sure record stores should be used
		if (!settings.getSaveUpdatedStatistics()) {
			return null;
		}
		RecordStore[] recordStores;
		recordStores = new RecordStore[currentCategories.length];
		for (int i=0; i<recordStores.length; i++) {
			try {
				recordStores[i] = RecordStore.openRecordStore(currentCategories[i].getRecordStoreName(), createIfNecessary, 0, false);
			} catch (RecordStoreNotFoundException e) {
				if (createIfNecessary) {
					throw e;
				} else {
					// ignore as there is no data to load
				}
			}
		}
		return recordStores;
	}

	/**
	 * Closes all given RecordStores
	 * @param recordStores
	 * @throws RecordStoreNotOpenException
	 * @throws RecordStoreException
	 */
	private void closeOpenRecordstores(RecordStore[] recordStores)
			throws RecordStoreNotOpenException, RecordStoreException {
		if (recordStores==null) {
			return;
		}
		for (int i=0; i<recordStores.length; i++) {
			if (recordStores[i]==null) continue;
			recordStores[i].closeRecordStore();
		}
	}

	public boolean getSettingsShowSimplifiedCharacters() {
		return settings.getShowSimplifiedCharacters();
	}

	public boolean getSettingsShowTraditionalCharacters() {
		return settings.getShowTraditionalCharacters();
	}

	public void setSettingsShowSimplifiedCharacters(boolean show) {
		settings.setShowSimplifiedCharacters(show);
	}

	public void setSettingsShowTraditionalCharacters(boolean show) {
		settings.setShowTraditionalCharacters(show);
	}

	public void saveSettings() throws RecordStoreNotFoundException,
			RecordStoreException, IOException {
		settings.saveSettings();
	}

}
