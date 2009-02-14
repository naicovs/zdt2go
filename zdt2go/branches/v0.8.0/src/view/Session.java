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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;

import model.Attribute;
import controller.Controller;

/**
 * The Session class is the screen that represents the front side of a flash
 * card. Viewing this screen, the user is asked to remember the missing
 * data.
 * 
 * @author Achim Weimert
 * 
 */
public class Session extends Screen implements CommandListener {
	
	private StringItem cardContent;
	private Command flipCommand;
	private Alert cancelAlert;

	public Session(Controller controller, boolean loadNext) {
		super("Session", controller);
		
		cardContent = new StringItem("", "");
		
		cardContent.setLayout(StringItem.LAYOUT_CENTER | StringItem.LAYOUT_EXPAND | StringItem.LAYOUT_VCENTER | StringItem.LAYOUT_VEXPAND);
		
		append(cardContent);
		
		cancelAlert = new Alert("Cancel?", "Cancel?", null, AlertType.CONFIRMATION);
		cancelAlert.addCommand(yesCommand);
		cancelAlert.addCommand(noCommand);
		cancelAlert.setCommandListener(this);

		flipCommand = new Command("Flip card", Command.SCREEN, 1);
		addCommand(flipCommand);
		addCommand(cancelCommand);

		setCommandListener(this);
		
		init(loadNext);
		
	}

	public Session(Controller controller) {
		this(controller, true);
	}
	
	private void init(boolean loadNext) {
		if (loadNext) {
			data.loadNextSessionEntry();
		}
		String attributes[] = new String[4];
		attributes[Attribute.TRADITIONAL] = data.getCurrentSessionEntryAttribute(Attribute.TRADITIONAL);
		attributes[Attribute.SIMPLIFIED] = data.getCurrentSessionEntryAttribute(Attribute.SIMPLIFIED);
		attributes[Attribute.PINYIN] = data.getCurrentSessionEntryAttribute(Attribute.PINYIN);
		attributes[Attribute.MEANING] = data.getCurrentSessionEntryAttribute(Attribute.MEANING);

		StringBuffer showString = new StringBuffer();
		for (int i=0; i<attributes.length; i++) {
			if (data.isEntryAttributeShown(i)) {
				showString.append(attributes[i]+"\n");
			}
		}

		cardContent.setLabel(showString.toString());

	}


	public void commandAction(Command command, Displayable displayable) {
		if (displayable==this) {
			if (command==flipCommand) {
				controller.changeScreen(new SessionFlipped(controller));
			} else if (command==cancelCommand) {
				controller.changeScreen(cancelAlert);
			}
		} else if (displayable==cancelAlert) {
			if (command==yesCommand) {
				controller.lastScreen();
			} else if (command==noCommand) {
				controller.changeScreen(new Session(controller, false));
			}
		}
	}

}
