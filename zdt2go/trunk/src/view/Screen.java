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
import javax.microedition.lcdui.Form;



import model.Data;
import controller.Controller;

/**
 * Screen is an abstract base class for conveniently implementing a view class.
 * 
 * @author Achim Weimert
 *
 */
abstract class Screen extends Form {
	/**
	 * commands that can be used by all implementations of Screen
	 */
	protected Command exitCommand = new Command("Exit", Command.EXIT, 10);
	protected Command backCommand = new Command("Back", Command.BACK, 10);
	protected Command cancelCommand = new Command("Cancel", Command.CANCEL, 10);
	protected Command yesCommand = new Command("Yes", Command.OK, 10);
	protected Command okCommand = new Command("Ok", Command.OK, 10);
	protected Command noCommand = new Command("No", Command.CANCEL, 10);
	protected Command menuCommand = new Command("Menu", Command.ITEM, 1);
	
	protected Controller controller = null;
	protected Data data = null;

	/**
	 * Class constructor specifying the screen title and the controller to use.
	 * 
	 * @param title the title of the screen
	 * @param controller the controller to use
	 */
	public Screen(String title, Controller controller) {
		super(title);
		
		this.controller = controller;
		this.data = controller.getModel();
	}
}
