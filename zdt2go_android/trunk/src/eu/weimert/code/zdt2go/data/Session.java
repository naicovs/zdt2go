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
package eu.weimert.code.zdt2go.data;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable {
	
	/**
	 * Specifies the position where to add a wrongly answered card.
	 * 
	 * A value of 0 means adding the card at the end, 1 means adding the card to
	 * the end or inserting it before the current last card, and so on.
	 */
	static final int INSERT_LAST_POSITIONS = 2;
	
	private static Random random = new Random(System.currentTimeMillis());
	private LinkedList<Entry> initialCards = new LinkedList<Entry>();
	private LinkedList<Entry> currentCards = new LinkedList<Entry>();
	private Vector<Entry> finishedCards = new Vector<Entry>();
	private Entry currentEntry = null;
	private int cardCount = 0;
	private int[] frontIndices;
	private int[] backIndices;
	private Category category = null;
	
	public Session(Category category, int[] frontIndices, int[] backIndices) {
		this.category = category;
		this.frontIndices = frontIndices;
		this.backIndices = backIndices;
		
		for (int i = 0; i < category.getEntriesCount(); i++) {
			Entry entry = category.getEntry(i);
			initialCards.add(random.nextInt(initialCards.size()+1), entry);
		}
	}
	
	public Session(Parcel in) {
		in.readList(initialCards, Entry.class.getClassLoader());
		in.readList(currentCards, Entry.class.getClassLoader());
		in.readList(finishedCards, Entry.class.getClassLoader());
		cardCount = in.readInt();
		currentEntry = in.readParcelable(Entry.class.getClassLoader());
		category = in.readParcelable(Category.class.getClassLoader());
		frontIndices = new int[in.readInt()];
		in.readIntArray(frontIndices);
		backIndices = new int[in.readInt()];
		in.readIntArray(backIndices);
	}

	public Entry getCurrentEntry() {
		if (currentEntry == null) {
			loadNextCard();
		}
		return currentEntry;
	}

	public void loadNextCard() {
		if (initialCards.size() > 0) {
			currentEntry = initialCards.poll();
		} else {
			currentEntry = currentCards.poll();
		}
		cardCount++;
	}
	
	public void handleCorrectAnswer() {
		// TODO mark card as correctly answered
		finishedCards.add(currentEntry);
		currentEntry = null;
	}
	
	public void handleIncorrectAnswer() {
		int position;
		if (INSERT_LAST_POSITIONS >= currentCards.size()) {
			position = currentCards.size();
		} else {
			// add element at random position of the INSERT_LAST_POSITIONS last positions
			int max = INSERT_LAST_POSITIONS + 1;
			position = currentCards.size() - max + 1 + random.nextInt(max);
		}
		currentCards.add(position, currentEntry);
		currentEntry = null;
	}
	
	public int[] getFrontIndices() {
		return frontIndices;
	}
	
	public int[] getBackIndices() {
		return backIndices;
	}
	
	public int getCountItems() {
		return category.getCountItems();
	}
	
	public int getCountCards() {
		return category.getEntriesCount();
	}
	
	public int getCountTries() {
		return cardCount;
	}
	
	public boolean isFinished() {
		if (initialCards.size() > 0) {
			return false;
		}
		if (currentCards.size() > 0) {
			return false;
		}
		if (currentEntry != null) {
			return false;
		}
		return true;
	}
	
    public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {
		public Session createFromParcel(Parcel in) {
			return new Session(in);
		}

		public Session[] newArray(int size) {
			return new Session[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(initialCards);
		dest.writeList(currentCards);
		dest.writeList(finishedCards);
		dest.writeInt(cardCount);
		dest.writeParcelable(currentEntry, flags);
		dest.writeParcelable(category, flags);
		dest.writeInt(frontIndices.length);
		dest.writeIntArray(frontIndices);
		dest.writeInt(backIndices.length);
		dest.writeIntArray(backIndices);
	}
	

}
