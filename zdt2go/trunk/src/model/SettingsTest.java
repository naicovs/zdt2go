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


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import jmunit.framework.cldc11.TestCase;

public class SettingsTest extends TestCase {

	private Settings settings;
	
	public SettingsTest() {
		super(3, "SettingsTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: testDeleteSettings(); break;
		case 1: testSetAndSaveSettings(); break;
		case 2: testManually(); break;
		}
	}
	
	public void testDeleteSettings() {
		assertTrue(Settings.deleteSettings());
	}
	
	public void testSetAndSaveSettings() {
		try {
			settings = new Settings("", "");
		} catch (RecordStoreFullException e) {
			e.printStackTrace();
			fail("x1: unexpected exception");
		} catch (RecordStoreNotFoundException e) {
			e.printStackTrace();
			fail("x2: unexpected exception");
		} catch (RecordStoreException e) {
			e.printStackTrace();
			fail("x3: unexpected exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("x4: unexpected exception");
		}
		settings.setSaveUpdatedStatistics(true);
	}
	
	public void testManually() {
		RecordStore recordStore = null;
		Settings.deleteSettings();
		try {
			
			recordStore = RecordStore.openRecordStore(Data.SETTINGS_RECORDSTORE, true, 0, false);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			
			// write header to stream
			dout.writeUTF("");
			dout.writeUTF("");
			// write settings to stream
			dout.writeBoolean(true);
			dout.writeBoolean(true);
			dout.writeBoolean(true);
			// convert stream to byte array
			dout.flush();
			byte[] bytes = bout.toByteArray();
			dout.close();

			try {
				recordStore.setRecord(1, bytes, 0, bytes.length);
			} catch (InvalidRecordIDException e) {
				recordStore.addRecord(bytes, 0, bytes.length);
			}
			
		} catch (RecordStoreFullException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (RecordStoreNotFoundException e) {
			// ignore that one
		} catch (RecordStoreException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			// make sure recordStore is always closed
			if (recordStore==null) return;
			try {
				recordStore.closeRecordStore();
			} catch (RecordStoreNotOpenException e) {
			} catch (RecordStoreException e) {
			}
		}

	}

}
