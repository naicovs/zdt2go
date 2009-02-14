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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;

import controller.Controller;

/**
 * The Finished class is the screen that is shown after the last entry of a
 * learning session has been correctly answered. On this screen statistics about
 * the learning progress are displayed.
 * 
 * @author Achim Weimert
 * 
 */
public class Finished extends Screen implements CommandListener {

	private StringItem finishedLabel;
	private StringItem scoreLabel;
	private Command newLessonCommand;

	public Finished(Controller controller) {
		super("Finished", controller);

		finishedLabel = new StringItem("Learning of "
				+ data.getSessionTotalCards() + " cards finished.", "");
		scoreLabel = new StringItem("Score: "
				+ data.getSessionTotalCards()
				+ "/"
				+ data.getSessionCardNumber()
				+ "="
				+ (int) (data.getSessionTotalCards() * 100.0 / data
						.getSessionCardNumber()) + "%", "");
		
		newLessonCommand = new Command("New...", Command.OK, 1);

		append(finishedLabel);
		append(scoreLabel);
		addCommand(newLessonCommand);
		addCommand(exitCommand);

		setCommandListener(this);
		
	}

	public void commandAction(Command command, Displayable displayable) {
		if (displayable==this) {
			if (command == exitCommand) {
				controller.exit();
			} else if (command == newLessonCommand) {
				controller.lastScreen();
			}
		}
	}

}
