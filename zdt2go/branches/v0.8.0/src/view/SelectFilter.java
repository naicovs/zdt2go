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
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;

import model.ThreadCallback;

import controller.Controller;

/**
 * The SelectFilter class is a screen where the user can configure filters.
 * These filters specify on what basis entries are included into a learning
 * session.
 * 
 * @author Achim Weimert
 * 
 */
public class SelectFilter extends Screen implements CommandListener, ItemStateListener, ThreadCallback {
	
	private TextField maximumNumber;
	private TextField minimumLastTestedDaysAgo;
	private TextField maximumPercentage;
	private TextField maximumStreak;
	private TextField maximumTimesTested;
	private ChoiceGroup menuChoiceGroup;
	private Alert confirmationAlert;
	private Alert errorAlert;
	private Alert waitAlert;


	public SelectFilter(Controller controller) {
		super("Filter", controller);
		
		menuChoiceGroup = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
		menuChoiceGroup.append("Start learning...", null);
		menuChoiceGroup.append("Filter vocabulary", null);
		maximumNumber = new TextField("Total number<=", getMaximumNumber(), 3, TextField.NUMERIC);
		maximumStreak = new TextField("Streak<=", getMaximumStreak(), 3, TextField.NUMERIC);
		maximumTimesTested = new TextField("Tested<=", getMaximumTimesTested(), 3, TextField.NUMERIC);
		maximumPercentage = new TextField("Percentage<=", getMaximumPercentage(), 3, TextField.NUMERIC);
		minimumLastTestedDaysAgo = new TextField("Days ago>=", getMinimumDaysAgo(), 3, TextField.NUMERIC);
		appendFormItems(false);
		
		addCommand(backCommand);
		addCommand(okCommand);

		setCommandListener(this);
		setItemStateListener(this);
	}

	private String getMinimumDaysAgo() {
		int value = data.getFilterMinimumDaysAgo();
		return ""+value;
	}

	private String getMaximumPercentage() {
		int value = data.getFilterMaximumPercentage();
		return ""+value;
	}

	private String getMaximumTimesTested() {
		int value = data.getFilterMaximumTimesTested();
		if (value>=0) {
			return ""+value;
		}
		return "";
	}

	private String getMaximumStreak() {
		int value = data.getFilterMaximumStreak();
		if (value>=0) {
			return ""+value;
		}
		return "";
	}

	private String getMaximumNumber() {
		int value = data.getFilterMaximumNumberOfEntries();
		if (value>=0) {
			return ""+value;
		}
		return "";
	}

	private void appendFormItems(boolean showAdvancedFilter) {
		append(menuChoiceGroup);
		if (!showAdvancedFilter) {
			menuChoiceGroup.setSelectedIndex(1, false);
			return;
		}
		menuChoiceGroup.setSelectedIndex(1, true);
		append(maximumNumber);
		append(maximumStreak);
		append(maximumTimesTested);
		append(maximumPercentage);
		append(minimumLastTestedDaysAgo);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (displayable==this) {
			if (command==backCommand) {
				controller.lastScreen();
			} else if (command==okCommand) {
				handleContinue();
			}
		} else if (displayable==confirmationAlert) {
			controller.changeScreen(this);
		} else if (displayable==errorAlert) {
			controller.changeScreen(this);
		} else if (displayable==waitAlert) {
			// do nothing
		}
	}

	private void handleContinue() {
		// display working indicator
		showWaitingAlert();

		// set filters
		if (menuChoiceGroup.isSelected(1)) {
			data.setFilterMaximumNumberOfEntries(readMaximumNumber());
			data.setFilterMaximumStreak(readMaximumStreak());
			data.setFilterMaximumTimesTested(readMaximumTimesTested());
			data.setFilterMaximumPercentage(readMaximumPercentage());
			data.setFilterMinimumDaysAgo(readMinimumDaysAgo());
		}
		
		// load session data
		data.loadSession(this);
	}

	private void showWaitingAlert() {
		Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
		waitAlert = new Alert("Loading");
		waitAlert.setString("Loading data, please wait...");
		waitAlert.setIndicator(gauge);
		waitAlert.setTimeout(Alert.FOREVER);
		waitAlert.setCommandListener(this);
		waitAlert.removeCommand(Alert.DISMISS_COMMAND);
		controller.changeScreen(waitAlert);
	}

	private int readMinimumDaysAgo() {
		String value = minimumLastTestedDaysAgo.getString();
		if (value.length()==0) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	private int readMaximumPercentage() {
		String value = maximumPercentage.getString();
		if (value.length()==0) {
			return 100;
		}
		return Integer.parseInt(value);
	}

	private int readMaximumTimesTested() {
		String value = maximumTimesTested.getString();
		if (value.length()==0) {
			return -1;
		}
		return Integer.parseInt(value);
	}

	private int readMaximumStreak() {
		String value = maximumStreak.getString();
		if (value.length()==0) {
			return -1;
		}
		return Integer.parseInt(value);
	}

	private int readMaximumNumber() {
		String value = maximumNumber.getString();
		if (value.length()==0) {
			return -1;
		}
		return Integer.parseInt(value);
	}

	public void threadFinished(Exception exception) {
		if (exception!=null) {
			showError("Error while loading vocabulary.\n\n" + exception);
			exception.printStackTrace();
			return;
		}
		if (data.getSessionRemainingCardsCount()>0) {			
			controller.changeScreen(new ConfigureCard(controller));
		} else {
			// warn user that lesson is empty
			showConfirmation(
					"Warning",
					"Your session contains no vocabulary. Please change your filters or select different categories.");
		}
		
	}

	private void showConfirmation(String title, String message) {
		confirmationAlert = new Alert(title, message, null,
				AlertType.CONFIRMATION);
		confirmationAlert.setTimeout(Alert.FOREVER);
		confirmationAlert.addCommand(okCommand);
		confirmationAlert.setCommandListener(this);
		controller.changeScreen(confirmationAlert);
	}

	private void showError(String message) {
		errorAlert = new Alert("Error", message, null,
				AlertType.ERROR);
		errorAlert.setTimeout(Alert.FOREVER);
		errorAlert.addCommand(okCommand);
		errorAlert.setCommandListener(this);
		controller.changeScreen(errorAlert);
	}

	public void itemStateChanged(Item item) {
		if (item==menuChoiceGroup) {
			if (menuChoiceGroup.isSelected(0)) {
				handleContinue();
				menuChoiceGroup.setSelectedIndex(0, false);
			}
			if (menuChoiceGroup.isSelected(1)) {
				deleteAll();
				appendFormItems(true);
			} else if (!menuChoiceGroup.isSelected(1)) {
				deleteAll();
				appendFormItems(false);
			}
		}
	}
}
