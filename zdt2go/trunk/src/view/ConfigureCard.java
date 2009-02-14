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

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;

import controller.Controller;

/**
 * The ConfigureCard class implements a Screen where a user can choose what
 * data to display on a card.
 * 
 * @author Achim Weimert
 * 
 */
public class ConfigureCard extends Screen implements CommandListener,
		ItemStateListener {
	
	private ChoiceGroup cardItems;
	private int startItemId = -1;
	private int traditionalItemId = -1;
	private int simplifiedItemId = -1;
	private int pinyinItemId = -1;
	private int meaningItemId = -1;

	public ConfigureCard(Controller controller) {
		super("Card", controller);
		
		cardItems = new ChoiceGroup("Items to show on front:", Choice.MULTIPLE);
		if (data.getSettingsShowTraditionalCharacters()) {
			traditionalItemId = cardItems.append("Traditional", null);
		}
		if (data.getSettingsShowSimplifiedCharacters()) {
			simplifiedItemId = cardItems.append("Simplified", null);
		}
		pinyinItemId = cardItems.append("Pinyin", null);
		meaningItemId = cardItems.append("Meaning", null);
		append(cardItems);
		addCommand(backCommand);

		setCommandListener(this);
		setItemStateListener(this);
	}

	public void commandAction(Command command, Displayable arg1) {
		if (command==backCommand) {
			controller.changeScreen(new SelectFilter(controller));
		}
	}

	public void itemStateChanged(Item item) {
		if (item==cardItems) {
			handleMenuEvent();
		}
	}

	private void handleMenuEvent() {
		int countChecked = 0;
		int countUnchecked = 0;

		// check if [START] was selected
		if (startItemId>=0 && cardItems.isSelected(startItemId)) {
			boolean showCardItems[] = new boolean[4];
			for (int i=0; i<showCardItems.length; i++) {
				showCardItems[i] = false;
			}
			if (traditionalItemId>=0) {
				showCardItems[0] = cardItems.isSelected(traditionalItemId);
			}
			if (simplifiedItemId>=0) {
				showCardItems[1] = cardItems.isSelected(simplifiedItemId);
			}
			if (pinyinItemId>=0) {
				showCardItems[2] = cardItems.isSelected(pinyinItemId);
			}
			if (meaningItemId>=0) {
				showCardItems[3] = cardItems.isSelected(meaningItemId);
			}

			data.setCardDisplayAttributes(showCardItems[0], showCardItems[1], showCardItems[2], showCardItems[3]);
			if (data.getSessionRemainingCardsCount()>0) {
				controller.changeScreen(new Session(controller));
			} else {
				controller.changeScreen(new SelectFilter(controller));
			}
			cardItems.setSelectedIndex(startItemId, false);
			return;
		}
		
		// count checked items
		for (int i=0; i<cardItems.size(); i++) {
			if (i==startItemId) {
				// ignore Start-button
				continue;
			}
			if (cardItems.isSelected(i)) {
				countChecked++;
			} else {
				countUnchecked++;
			}
		}
		
		// show [START] if there are checked AND unchecked items
		if (countChecked>0 && countUnchecked>0) {
			if (startItemId>=0) {
				// abort if [START] is already being displayed
				return;
			}
			startItemId = cardItems.append("[START]", null);
		} else {
			if (startItemId<0) {
				// abort if [START] is not being displayed
				return;
			}
			cardItems.delete(startItemId);
			startItemId = -1;
		}
	}

}
