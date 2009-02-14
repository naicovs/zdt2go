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
package controller;

import java.io.IOException;
import java.util.Stack;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import model.Data;
import model.DataImplementation;
import view.SelectCategory;

/**
 * The Main class is the starting point of the application.
 * 
 * @author Achim Weimert
 * 
 */
public class Main extends MIDlet implements Controller, CommandListener {

	private Display display;
	private Stack screenStack;
	private Data data;
	private Alert errorAlert;

	public Main() {
		screenStack = new Stack();
		data = new DataImplementation();
		try {
			data.loadSettings(getAppProperty("MIDlet-Version"),
					getAppProperty("MIDlet-Jar-Size"));
		} catch (Exception e) {
			e.printStackTrace();
			showError("Error while loading settings.\n\n"+e);
		}

		try {
			data.loadCategoryList();
		} catch (IOException e) {
			e.printStackTrace();
			showError("Error while loading categories index.\n\n"+e);
			return;
		}
		if (data.getCountCategories() == 0) {
			showError("No categories found! Please add some vocabulary to the jar archive before installing it.");
		} else {
			defaultAction();
		}
	}

	/**
	 * Shows an error message.
	 * 
	 * @param message the error message to display
	 */
	private void showError(String message) {
		errorAlert = new Alert(
				"Error",
				message,
				null, AlertType.ERROR);
		errorAlert.addCommand(Alert.DISMISS_COMMAND);
		errorAlert.setTimeout(Alert.FOREVER);
		errorAlert.setCommandListener(this);
		changeScreen(errorAlert);
	}

	/**
	 * Shows the default screen of the application.
	 */
	private void defaultAction() {
		nextScreen(new SelectCategory(this));
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
	}

	public Displayable currentScreen() {
		return getDisplay().getCurrent();
	}

	public void exit() {
		notifyDestroyed();
	}

	public Data getModel() {
		return data;
	}

	public void lastScreen() {
		Displayable display = null;

		if (!screenStack.empty()) {
			display = (Displayable) screenStack.pop();
		} else {
			display = new SelectCategory(this);
		}

		getDisplay().setCurrent(display);
	}

	public void nextScreen(Displayable display) {
		Displayable currentScreen = getDisplay().getCurrent();
		if (currentScreen != null) {
			screenStack.push(currentScreen);
		}

		getDisplay().setCurrent(display);
	}

	public void changeScreen(Displayable display) {
		getDisplay().setCurrent(display);
	}

	private Display getDisplay() {
		if (display == null) {
			display = Display.getDisplay(this);
		}
		return display;
	}

	public String getMidletInfoUrl() {
		return getAppProperty("MIDlet-Info-URL");
	}

	public String getMidletJarSize() {
		return getAppProperty("MIDlet-Jar-Size");
	}

	public String getMidletName() {
		return getAppProperty("MIDlet-Name");
	}

	public String getMidletVendor() {
		return getAppProperty("MIDlet-Vendor");
	}

	public String getMidletVersion() {
		return getAppProperty("MIDlet-Version");
	}

	public void commandAction(Command command, Displayable displayable) {
		if (displayable == errorAlert) {
			defaultAction();
		}
	}

}
