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

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * The Settings class provides functionality to load, represent and save user
 * settings.
 * 
 * @author Achim Weimert
 * 
 */
public class Settings {
	
	public static final int SETTINGS_RECORD_ID = 1; 

	// application status
	private final String midletVersion;
	private final String midletSize;
	// settings status
	private String loadedVersion = null;
	private String loadedSize = null;
	private boolean isRunForTheFirstTime = false;
	private boolean hasApplicationDataChanged = false;
	private boolean hasBeenUpgraded = false;
	// settings
	private boolean saveUpdatedStatistics;
	private boolean showTradidionalCharacters;
	private boolean showSimplifiedCharacters;
	
	/**
	 * Creates settings
	 * @param midletVersion is the current version of the midlet
	 * @param midletSize is the current size of the midlet
	 * @throws IOException 
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 * @throws RecordStoreFullException 
	 */
	public Settings(String midletVersion, String midletSize)
			throws RecordStoreFullException, RecordStoreNotFoundException,
			RecordStoreException, IOException {
		if (midletVersion==null) midletVersion = "";
		if (midletSize==null) midletSize = "";
		this.midletVersion = midletVersion;
		this.midletSize = midletSize;
		loadDefaultSettings();
		loadSettings();
	}
	
	/**
	 * Replaces current settings with default settings
	 */
	public void loadDefaultSettings() {
		saveUpdatedStatistics = true;
		showSimplifiedCharacters = true;
		showTradidionalCharacters = false;
	}
	
	public boolean getSaveUpdatedStatistics() {
		return saveUpdatedStatistics;
	}
	
	public void setSaveUpdatedStatistics(boolean save) {
		saveUpdatedStatistics = save;
	}
	
	public boolean getShowSimplifiedCharacters() {
		return showSimplifiedCharacters;
	}
	
	public void setShowSimplifiedCharacters(boolean show) {
		showSimplifiedCharacters = show;
	}
	
	public boolean getShowTraditionalCharacters() {
		return showTradidionalCharacters;
	}
	
	public void setShowTraditionalCharacters(boolean show) {
		showTradidionalCharacters = show;
	}
	
	/**
	 * Check if application is executed for the first time by looking for saved settings
	 * @return
	 */
	public boolean appIsRunForTheFirstTime() {
		return isRunForTheFirstTime;
	}
	
	/**
	 * Check if application size has changed
	 * @return
	 */
	public boolean hasApplicationDataChanged() {
		return hasApplicationDataChanged;
	}
	
	/**
	 * Check if application version has changed
	 * @return
	 */
	public boolean hasApplicationBeenUpgraded() {
		return hasBeenUpgraded;
	}
	
	/**
	 * Save settings in the RecordStore
	 * 
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreException
	 * @throws IOException
	 * @throws IOException
	 */
	public void saveSettings() throws RecordStoreNotFoundException, RecordStoreException, IOException {
		RecordStore recordStore = null;
		deleteSettings();
		try {
			
			recordStore = RecordStore.openRecordStore(Data.SETTINGS_RECORDSTORE, true, 0, false);

			byte[] bytes = getSerializedSettings();

			try {
				recordStore.setRecord(SETTINGS_RECORD_ID, bytes, 0, bytes.length);
			} catch (InvalidRecordIDException e) {
				recordStore.addRecord(bytes, 0, bytes.length);
			}
			
		} finally {
			// make sure recordStore is always closed
			if (recordStore==null) {
				return;
			}
			try {
				recordStore.closeRecordStore();
			} catch (RecordStoreNotOpenException e) {
			} catch (RecordStoreException e) {
			}
		}
	}

	private byte[] getSerializedSettings() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		
		// write header to stream
		dout.writeUTF(midletVersion);
		dout.writeUTF(midletSize);
		// write settings to stream
		dout.writeBoolean(saveUpdatedStatistics);
		dout.writeBoolean(showSimplifiedCharacters);
		dout.writeBoolean(showTradidionalCharacters);
		// convert stream to byte array
		dout.flush();
		byte[] bytes = bout.toByteArray();
		dout.close();
		return bytes;
	}

	/**
	 * Deletes saved settings from the RecordStore
	 * @return
	 */
	public static boolean deleteSettings() {
		try {
			RecordStore.deleteRecordStore(Data.SETTINGS_RECORDSTORE);
		} catch (RecordStoreNotFoundException e) {
			// ignore this exception
		} catch (RecordStoreException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Loads saved settings from the RecordStore.
	 * 
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 * @throws RecordStoreFullException 
	 * @throws IOException 
	 */
	private void loadSettings() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore(Data.SETTINGS_RECORDSTORE, false, 0, false);
			
			byte[] data = recordStore.getRecord(SETTINGS_RECORD_ID);
			setSerializedData(data);
			
		} finally {
			if (recordStore==null) return;
			try {
				// make sure RecordStore always gets closed
				recordStore.closeRecordStore();
			} catch (RecordStoreNotOpenException e) {
			} catch (RecordStoreException e) {
			}
		}
	}

	private void setSerializedData(byte[] data) throws IOException {
		ByteArrayInputStream bin;
		DataInputStream din;
		bin = new ByteArrayInputStream(data);
		din = new DataInputStream(bin);
		
		// read headers
		loadedVersion = din.readUTF();
		loadedSize = din.readUTF();
		// check if loaded and midlet's headers are the same
		if (loadedVersion.compareTo(midletVersion)!=0) {
			hasBeenUpgraded = true;
			if (!tryMigratingSettings(din)) {
				// restore default settings
				loadDefaultSettings();
			}
		} else if (loadedSize.compareTo(midletSize)!=0) {
			hasApplicationDataChanged = true;
		}
		
		if (!hasBeenUpgraded) {
			// read other settings
			saveUpdatedStatistics = din.readBoolean();
			showSimplifiedCharacters = din.readBoolean();
			showTradidionalCharacters = din.readBoolean();
		}
		
		din.close();
	}
	
	/**
	 * Tries to migrate outdated saved settings to the new version of the application
	 * @param din
	 * @return
	 */
	private boolean tryMigratingSettings(DataInputStream din) {
		// add migrations as new versions are released
		return false;
	}

	/**
	 * Returns the size of the midlet
	 * @return
	 */
	public final String getMidletSize() {
		return midletSize;
	}
	
	/**
	 * Returns the version of the midlet
	 * @return
	 */
	public final String getMidletVersion() {
		return midletVersion;
	}
}
