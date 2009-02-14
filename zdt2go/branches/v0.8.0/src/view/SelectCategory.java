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

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;

import controller.Controller;

/**
 * The SelectCategory class is a screen class for selecting multiple categories
 * to be included in a learning session. It is the starting screen for the
 * application and additionally gives access to the settings and about screen.
 * 
 * @author Achim Weimert
 * 
 */
public class SelectCategory extends Screen implements CommandListener,
		ItemStateListener {
	
	private ChoiceGroup categoryList = null;
	private Command startCommand = null;
	private Command settingsCommand = null;
	private Command aboutCommand = null;
	private Alert alert;

	public SelectCategory(Controller controller) {
		super("Categories", controller);
		
		if (data.getCountCategories()>0) {
			categoryList = new ChoiceGroup("", Choice.MULTIPLE);
			for (int i=0; i<data.getCountCategories(); i++) {
				categoryList.append(data.getCategoryName(i), null);
			}
			append(categoryList);
		}
		
		// will be added when a category has been selected
		startCommand = new Command("Start", Command.SCREEN, 0);

		aboutCommand = new Command("About", Command.SCREEN, 10);
		settingsCommand = new Command("Settings", Command.SCREEN, 5);
		addCommand(aboutCommand);
		addCommand(settingsCommand);
		addCommand(exitCommand);

		setCommandListener(this);
		setItemStateListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (displayable==this) {
			if (command==startCommand) {
				handleLoadCategories();
			} else if (command==exitCommand) {
				controller.exit();
			} else if (command==settingsCommand) {
				controller.nextScreen(new Settings(controller));
			} else if (command==aboutCommand) {
				controller.nextScreen(new About(controller));
			}
		} else if (displayable==alert) {
			controller.lastScreen();
		}
	}

	private void handleLoadCategories() {
		// find out how many categories are selected
		int countSelected = getNumberOfSelectedCategories();
		if (countSelected==0) {
			showAlert("Please select at least one category.");
			return;
		}
		// save selected categories
		int[] selected = getSelectedCategories(countSelected);
		// load selected categories
		try {
			data.selectCategories(selected);
			resetSelection();
			controller.nextScreen(new SelectFilter(controller));
		} catch (IOException e) {
			e.printStackTrace();
			showAlert("Error while loading category.\n\n"+e);
		}
	}

	private void showAlert(String message) {
		alert = new Alert("Error", message, null, AlertType.ERROR);
		alert.setCommandListener(this);
		alert.setTimeout(Alert.FOREVER);
		controller.nextScreen(alert);
	}

	private void resetSelection() {
		for (int i=0; i<categoryList.size(); i++) {
			categoryList.setSelectedIndex(i, false);
		}
	}

	private int[] getSelectedCategories(int countSelected) {
		int selected[] = new int[countSelected];
		int savedSelected = 0;
		for (int i=0; i<categoryList.size(); i++) {
			if (!categoryList.isSelected(i)) {
				continue;
			}
			selected[savedSelected] = i;
			savedSelected++;
		}
		return selected;
	}

	private int getNumberOfSelectedCategories() {
		int countSelected = 0;
		for (int i=0; i<categoryList.size(); i++) {
			if (categoryList.isSelected(i)) {
				countSelected++;
			}
		}
		return countSelected;
	}

	public void itemStateChanged(Item item) {
		if (item==categoryList) {
			// only show start button if a category is selected
			removeCommand(startCommand);
			if (getNumberOfSelectedCategories()>0) {
				addCommand(startCommand);
			}
		}
	}


}
