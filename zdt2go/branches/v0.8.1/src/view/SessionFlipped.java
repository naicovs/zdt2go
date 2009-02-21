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
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;

import model.Attribute;
import model.ThreadCallback;

import controller.Controller;

/**
 * The SessionFlipped class represents the back side of a flash card. It shows
 * the missing data and asks the user to specify if he correctly remembered
 * them.
 * 
 * @author Achim Weimert
 * 
 */
public class SessionFlipped extends Screen implements CommandListener,
		ItemStateListener, ThreadCallback {

	private ChoiceGroup commandList;
	private StringItem cardContentFlipped;
	private Alert errorAlert;
	private Alert waitAlert;
	private Alert cancelAlert;

	public SessionFlipped(Controller controller) {
		this(controller, false);
	}

	public SessionFlipped(Controller controller, boolean showAll) {
		super("Flipped", controller);

		cardContentFlipped = new StringItem("", "");
		cardContentFlipped.setLayout(StringItem.LAYOUT_CENTER
				| StringItem.LAYOUT_EXPAND);
		init(showAll);
		append(cardContentFlipped);

		commandList = new ChoiceGroup("", Choice.MULTIPLE);
		commandList.append(">I got it right", null);
		commandList.append(">Ask again", null);
		commandList.append("Show all data", null);
		commandList.append(">Cancel", null);
		commandList.setSelectedIndex(2, showAll);
		append(commandList);

		setCommandListener(this);
		setItemStateListener(this);
	}

	private void init(boolean showAll) {
		setTitle("Flipped - " + data.getSessionCardNumber() + "/"
				+ data.getSessionRemainingCardsCount());
		StringBuffer showString = new StringBuffer();

		for (int i = 0; i < 4; i++) {
			if (!showAll && !displayAttribute(i)) {
				continue;
			}
			String attribute = data.getCurrentSessionEntryAttribute(i);
			showString.append(attribute + "\n");
		}

		cardContentFlipped.setText(showString.toString());
	}

	private boolean displayAttribute(int attributeId) {
		if (attributeId == Attribute.TRADITIONAL
				&& !data.getSettingsShowTraditionalCharacters()) {
			return false;
		}
		if (attributeId == Attribute.SIMPLIFIED
				&& !data.getSettingsShowSimplifiedCharacters()) {
			return false;
		}
		return !data.isEntryAttributeShown(attributeId);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (displayable == errorAlert) {
			showSessionFinishedScreen();
		} else if (displayable == waitAlert) {
			// do nothing as operation cannot be canceled
		} else if (displayable == cancelAlert) {
			if (command == yesCommand) {
				controller.lastScreen();
			} else if (command == noCommand) {
				controller.changeScreen(new SessionFlipped(controller, false));
			}
		}
	}

	public void itemStateChanged(Item item) {
		if (item == commandList) {
			if (commandList.isSelected(0)) {
				handleCorrectAnswer();
			} else if (commandList.isSelected(1)) {
				handleWrongAnser();
			} else if (commandList.isSelected(3)) {
				showAlert();
			} else if (commandList.isSelected(2)) {
				// treat this option last as it may be selected all the time
				controller.changeScreen(new SessionFlipped(controller,
						commandList.isSelected(2)));
			}
			// reset selection
			commandList.setSelectedIndex(0, false);
			commandList.setSelectedIndex(1, false);
			commandList.setSelectedIndex(2, false);
			commandList.setSelectedIndex(3, false);
		}
	}

	private void showAlert() {
		cancelAlert = new Alert("Cancel?", "Cancel?", null,
				AlertType.CONFIRMATION);
		cancelAlert.addCommand(yesCommand);
		cancelAlert.addCommand(noCommand);
		cancelAlert.setCommandListener(this);
		controller.changeScreen(cancelAlert);
	}

	/**
	 * Handles the case when user knew the correct answer
	 */
	private void handleWrongAnser() {
		// re-add current card
		data.reAddCurrentEntry();
		// get next card
		controller.changeScreen(new Session(controller));
	}

	/**
	 * Handles the case when user did not know the correct answer
	 */
	private void handleCorrectAnswer() {
		// add card as finished
		data.addFinishedEntry();

		// show next card if there are cards left
		if (data.getSessionRemainingCardsCount() > 0) {
			controller.changeScreen(new Session(controller));
			return;
		}

		// only save session result if a special setting is set to true
		if (!data.getSettingsSaveUpdatedStatistics()) {
			showSessionFinishedScreen();
			return;
		}

		// display working indicator while saving results
		Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE,
				Gauge.CONTINUOUS_RUNNING);
		waitAlert = new Alert("Saving");
		waitAlert.setString("Saving data, please wait...");
		waitAlert.setIndicator(gauge);
		waitAlert.setTimeout(Alert.FOREVER);
		waitAlert.removeCommand(Alert.DISMISS_COMMAND);
		waitAlert.setCommandListener(this);
		controller.changeScreen(waitAlert);

		// save session result
		data.saveSessionResult(this);

	}

	public void threadFinished(Exception exception) {
		if (exception != null) {
			showAlert("Error while saving session result.\n\n" + exception);
			exception.printStackTrace();
			return;
		}
		showSessionFinishedScreen();
	}

	private void showAlert(String message) {
		errorAlert = new Alert("Error", message, null, AlertType.ERROR);
		errorAlert.addCommand(okCommand);
		errorAlert.setTimeout(Alert.FOREVER);
		errorAlert.setCommandListener(this);
		controller.changeScreen(errorAlert);
	}

	private void showSessionFinishedScreen() {
		controller.changeScreen(new Finished(controller));
	}

}
