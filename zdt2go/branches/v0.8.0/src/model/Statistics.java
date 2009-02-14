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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import model.helper.DateHelper;

/**
 * The Statistics class holds statistics over vocabulary entries. Its data is
 * used to select a subset of entries into a learning session.
 * 
 * @author Achim Weimert
 * 
 */
public class Statistics {
	
	private boolean learned = false;
	private int correctStreak = 0;
	private int numberCorrect = 0;
	private int timesTested = 0;
	private Date lastTested = null;

	public Statistics() {
	}
	
	/**
	 * Loads statistics from the corresponding parts of the given array
	 * @param countAttributes
	 * @param data
	 */
	public Statistics(int countAttributes, String data[]) {
		if (DataImplementation.STATS_LEARNED<countAttributes && data[DataImplementation.STATS_LEARNED].compareTo("true")==0) {
			learned = true;
		}
		if (DataImplementation.STATS_CORRECT_STREAK<countAttributes) {
			correctStreak = Integer.parseInt(data[DataImplementation.STATS_CORRECT_STREAK]);
		}
		if (DataImplementation.STATS_NUMBER_CORRECT<countAttributes) {
			numberCorrect = Integer.parseInt(data[DataImplementation.STATS_NUMBER_CORRECT]);
		}
		if (DataImplementation.STATS_TIMES_TESTED<countAttributes) {
			timesTested = Integer.parseInt(data[DataImplementation.STATS_TIMES_TESTED]);
		}
		if (DataImplementation.STATS_LAST_TESTED<countAttributes) {
			lastTested = DateHelper.convertStringToDate(data[DataImplementation.STATS_LAST_TESTED]);
		}
	}
	
	/**
	 * Loads statistics from the given input stream
	 * @param din
	 * @throws IOException
	 */
	public Statistics(DataInputStream din) throws IOException {
		setLearned(din.readBoolean());
		setCorrectStreak(din.readInt());
		setNumberCorrect(din.readInt());
		setTimesTested(din.readInt());
		setLastTested(new Date(din.readLong()));
	}
	
	public void serialize(DataOutputStream dout) throws IOException {
		dout.writeBoolean(getLearned());
		dout.writeInt(getCorrectStreak());
		dout.writeInt(getNumberCorrect());
		dout.writeInt(getTimesTested());
		dout.writeLong(getLastTested().getTime());
	}

	public int getCorrectStreak() {
		return correctStreak;
	}
	
	private void setCorrectStreak(int number) {
		correctStreak = number;
	}
	
	public void resetCorrectStreak() {
		correctStreak = 0;
	}
	
	public void incrementCorrectStreak() {
		correctStreak++;
	}
	
	public int getTimesTested() {
		return timesTested;
	}
	
	private void setTimesTested(int number) {
		timesTested = number;
	}
	
	public void incrementTimesTested() {
		timesTested++;
	}
	
	public int getNumberCorrect() {
		return numberCorrect;
	}
	
	private void setNumberCorrect(int number) {
		numberCorrect = number;
	}
	
	public void incrementNumberCorrect() {
		numberCorrect++;
	}
	
	public boolean getLearned() {
		return learned;
	}
	
	private void setLearned(boolean learned) {
		this.learned = learned;
	}
	
	public Date getLastTested() {
		return lastTested;
	}
	
	private void setLastTested(Date date) {
		lastTested = date;
	}
	
	/**
	 * Set lastTested Date to current Date
	 */
	public void updatedLastTested() {
		lastTested = new Date();
	}
	


}
