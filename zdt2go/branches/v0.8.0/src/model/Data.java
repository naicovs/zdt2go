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

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

/**
 * The Data interface specifies all information that needs to be accessible by
 * model, view and controller.
 * 
 * @author Achim Weimert
 * 
 */
public interface Data extends Attribute {
	
	/**
	 * Directory where vocabulary resources are stored
	 */
	public static final String VOCABULARY_DIR = "/vocabulary/";
	
	/**
	 * File that includes relevant information of included categories
	 */
	public static final String DIRECTORY_LISTING_FILE = "__index.txt";
	
	/**
	 * Prefix for RecordStores that hold categories
	 */
	public static final String CATEGORY_PREFIX = "CAT_";
	
	/**
	 * Name for RecordStore that holds settings
	 */
	public static final String SETTINGS_RECORDSTORE = "SETTINGS";
	
	/**
	 * Returns the number of categories that are available in the index file.
	 * 
	 * @return the number of categories
	 */
	int getCountCategories();

	/**
	 * Returns the name of the given category.
	 * 
	 * @param categoryNumber	the number of the category to return
	 * @return					the name of the specified category or null
	 */
	String getCategoryName(int categoryNumber);

	/**
	 * Loads the categories with the given numbers into the internal data
	 * structures.
	 * 
	 * @param categoryNumbers
	 *            the array of categories that will be loaded
	 */
	void selectCategories(int categoryNumbers[]) throws IOException;

	/**
	 * Loads entries that have been specified by selectCategories.
	 * 
	 * @param callback
	 *            gets called when thread is finished; a status smaller zero
	 *            indicates an error
	 */
	void loadSession(final ThreadCallback callback);
	
	/**
	 * Configures which data are shown on the front side of a card.
	 * 
	 * @param traditional	specifies if traditional characters are displayed
	 * @param simplified	specifies if simplified characters are displayed
	 * @param pinyin		specifies if pinyin is displayed
	 * @param meaning		specifies if the meaning is displayed
	 */
	void setCardDisplayAttributes(boolean traditional, boolean simplified, boolean pinyin, boolean meaning);

	/**
	 * Writes updated learning statistics into the RecordStore.
	 * 
	 * @param callback
	 *            gets called when thread is finished; a status smaller 0
	 *            indicates an error
	 */
	void saveSessionResult(final ThreadCallback callback);
	
	/**
	 * Deletes outdated RecordStores by checking if they have an corresponding
	 * entry in the index file.
	 * 
	 * @return the number of deleted categories
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 */
	int deleteOutdatedStatistics() throws RecordStoreNotFoundException, RecordStoreException;

	/**
	 * Deletes all saved statistics.
	 * 
	 * @return the number of deleted categories
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 */
	int deleteSavedStatistics() throws RecordStoreNotFoundException, RecordStoreException;

	/**
	 * Loads the next session entry into the internal data structures.
	 * 
	 * @return	true on success and false on failure
	 */
	boolean loadNextSessionEntry();
	
	/**
	 * Returns the currently loaded session entry data.
	 * 
	 * @return	the currently loaded entry data or null
	 */
	String getCurrentSessionEntryAttribute(int attributeId);

	/**
	 * Re-adds the currently loaded session entry to the list of unlearned
	 * vocabulary.
	 */
	void reAddCurrentEntry();
	
	/**
	 * Marks the current entry as finished.
	 */
	void addFinishedEntry();
	
	/**
	 * Returns the total number of available cards in the current session.
	 * 
	 * @return	the total number of cards
	 */
	int getSessionTotalCards();

	/**
	 * Return the number of the current card. This number gets incremented with
	 * each new card.
	 * 
	 * @return the number of the current card
	 */
	int getSessionCardNumber();

	/**
	 * Returns the number of cards that are left for learning in the current
	 * session.
	 * 
	 * @return the number of remaining cards
	 */
	int getSessionRemainingCardsCount();
	
	/**
	 * Checks if an attribute is to be shown on the frontside of a card.
	 * 
	 * @param attributeId one of {@link Attribute}
	 * @return true for show, false for hide
	 */
	boolean isEntryAttributeShown(int attributeId);

	/**
	 * Sets the maximum number of cards that are to be loaded into the session.
	 * 
	 * @param maximumNumber maximum number of cards
	 */
	void setFilterMaximumNumberOfEntries(int maximumNumber);

	/**
	 * Sets the maximum number a loaded card may have been answered correctly in
	 * a row.
	 * 
	 * @param maximumStreak
	 *            maximum number of correct answers in a row
	 */
	void setFilterMaximumStreak(int maximumStreak);
	
	/**
	 * Sets the maximum percentage that a loaded card may have.
	 * 
	 * @param maximumPercentage maximum percentage of correct answers over all answers
	 */
	void setFilterMaximumPercentage(int maximumPercentage);

	/**
	 * Sets the maximum number of times a loaded card may have been answered by
	 * the user.
	 * 
	 * @param maximumTimesTested
	 *            the maximum number a loaded card has been answered by the user
	 */
	void setFilterMaximumTimesTested(int maximumTimesTested);

	/**
	 * Sets the minimum period of time since last testing of a loaded card.
	 * 
	 * @param minimumDaysAgo
	 *            minimum number of days since the last test of a tested
	 */
	void setFilterMinimumDaysAgo(int minimumDaysAgo);

	/**
	 * Returns the maximum number of cards that are to be loaded into the session.
	 * 
	 * @return maximum number of cards
	 */
	int getFilterMaximumNumberOfEntries();

	/**
	 * Returns the maximum number a loaded card may have been answered correctly
	 * in a row.
	 * 
	 * @return maximum number of correct answers in a row
	 */
	int getFilterMaximumStreak();
	
	/**
	 * Returns the maximum percentage that a loaded card may have.
	 * 
	 * @return maximum percentage of correct answers over all answers.
	 */
	int getFilterMaximumPercentage();

	/**
	 * Returns the maximum number of times a loaded card may have been answered
	 * by the user.
	 * 
	 * @return the maximum number of times a loaded card may have been answered
	 *         by the user.
	 */
	int getFilterMaximumTimesTested();
	
	/**
	 * Returns the minimum period of time since last testing of a loaded card.
	 * 
	 * @return minimum number of days since the last test of a card
	 */
	int getFilterMinimumDaysAgo();

	/**
	 * Loads the application's settings.
	 * 
	 * @param midletVersion
	 * @param midletSize
	 * @throws RecordStoreFullException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreException
	 * @throws IOException
	 */
	public void loadSettings(String midletVersion, String midletSize)
			throws RecordStoreFullException, RecordStoreNotFoundException,
			RecordStoreException, IOException;

	/**
	 * Loads the list of available categories.
	 * 
	 * @throws IOException
	 */
	public void loadCategoryList() throws IOException;

	/**
	 * Save current settings to the RecordStore.
	 * 
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreException
	 * @throws IOException
	 * @throws IOException
	 */
	void saveSettings() throws RecordStoreNotFoundException, RecordStoreException, IOException;

	/**
	 * Returns if the application is configured to save updated statistics.
	 * 
	 * @return true if updated statistics should be saved
	 */
	boolean getSettingsSaveUpdatedStatistics();

	/**
	 * Set if updated statistics should be saved.
	 * 
	 * @param saveUpdatedStatistics specifies if updated statistics should be saved
	 */
	void setSettingsSaveUpdatedStatistics(boolean saveUpdatedStatistics);
	
	/**
	 * Returns if the application is configured to show traditional characters.
	 * 
	 * @return true if traditional characters are to be shown
	 */
	boolean getSettingsShowTraditionalCharacters();
	
	/**
	 * Set if traditional characters should be displayed.
	 * 
	 * @param show specifies if updated statistics should be saved
	 */
	void setSettingsShowTraditionalCharacters(boolean show);
	
	/**
	 * Returns if the application is configured to show simplified characters.
	 * 
	 * @return true if simplified characters are to be shown
	 */
	boolean getSettingsShowSimplifiedCharacters();
	
	/**
	 * Set if simplified characters should be displayed.
	 * 
	 * @param show specifies if simplified characters should be shown
	 */
	void setSettingsShowSimplifiedCharacters(boolean show);
}
