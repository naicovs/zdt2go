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
package view;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.rms.RecordStoreException;

import controller.Controller;

/**
 * The Settings class is the screen for adjusting the application's settings.
 * 
 * @author Achim Weimert
 *
 */
public class Settings extends Screen implements CommandListener, ItemStateListener {
	
	private ChoiceGroup saveSettings;
	private ChoiceGroup attributeSettings;
	private Alert errorAlert;
	private Alert deleteStatisticsAlert;
	private Alert doneAlert;

	public Settings(Controller controller) {
		super("Settings", controller);
		
		attributeSettings = new ChoiceGroup("Characters:", ChoiceGroup.MULTIPLE);
		attributeSettings.append("Show simplified", null);
		attributeSettings.append("Show traditional", null);
		attributeSettings.setSelectedIndex(0, data.getSettingsShowSimplifiedCharacters());
		attributeSettings.setSelectedIndex(1, data.getSettingsShowTraditionalCharacters());
		
		saveSettings = new ChoiceGroup("Storage:", ChoiceGroup.MULTIPLE);
		saveSettings.append("Save learning results", null);
		saveSettings.append("Delete outdated statistics", null);
		saveSettings.append("Delete saved statistics", null);
		saveSettings.setSelectedIndex(0, data.getSettingsSaveUpdatedStatistics());
		
		append(attributeSettings);
		append(saveSettings);
		
		addCommand(okCommand);
		addCommand(cancelCommand);
		setCommandListener(this);
		setItemStateListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (displayable==this) {
			if (command==okCommand) {
				data.setSettingsSaveUpdatedStatistics(saveSettings.isSelected(0));
				data.setSettingsShowSimplifiedCharacters(attributeSettings.isSelected(0));
				data.setSettingsShowTraditionalCharacters(attributeSettings.isSelected(1));
				try {
					data.saveSettings();
				} catch (Exception e) {
					showErrorAlert("Error while saving settings.\n\n"+e);
					return;
				}
			}
			// go back to SelectCategory-Screen
			controller.lastScreen();
		} else if (displayable==errorAlert) {
			controller.changeScreen(this);
		} else if (displayable==deleteStatisticsAlert) {
			if (command==yesCommand) {
				try {
					data.deleteSavedStatistics();
					showDoneAlert("Statistics have been deleted.");
				} catch (RecordStoreException e) {
					showErrorAlert("Error while deleting statistics.\n\n"+e);
				}
			} else {
				controller.changeScreen(this);
			}
		} else if (displayable==doneAlert) {
			controller.changeScreen(this);
		}
	}

	private void showErrorAlert(String message) {
		errorAlert = new Alert("Error", message, null, AlertType.ERROR);
		errorAlert.addCommand(okCommand);
		errorAlert.setCommandListener(this);
		errorAlert.setTimeout(Alert.FOREVER);
		controller.changeScreen(errorAlert);
	}

	public void itemStateChanged(Item item) {
		if (item==saveSettings) {
			if (saveSettings.isSelected(1)) {
				try {
					data.deleteOutdatedStatistics();
					showDoneAlert("Outdated statistics have been deleted.");
				} catch (RecordStoreException e) {
					e.printStackTrace();
					showErrorAlert("Error while deleting statistics.\n\n"+e);
				}
				saveSettings.setSelectedIndex(1, false);
			} else if (saveSettings.isSelected(2)) {
				showConfirmationAlert();
				saveSettings.setSelectedIndex(2, false);
			}
		}
	}

	private void showConfirmationAlert() {
		deleteStatisticsAlert = new Alert("Warning", "Delete all saved statistics now?", null, AlertType.CONFIRMATION);
		deleteStatisticsAlert.addCommand(yesCommand);
		deleteStatisticsAlert.addCommand(cancelCommand);
		deleteStatisticsAlert.setTimeout(Alert.FOREVER);
		deleteStatisticsAlert.setCommandListener(this);
		controller.changeScreen(deleteStatisticsAlert);
	}

	private void showDoneAlert(String message) {
		doneAlert = new Alert("Information", message, null, AlertType.INFO);
		doneAlert.addCommand(okCommand);
		doneAlert.setTimeout(Alert.FOREVER);
		doneAlert.setCommandListener(this);
		controller.changeScreen(doneAlert);
	}

}
