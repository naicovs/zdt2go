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

import controller.Controller;

/**
 * The About class represents the About screen. It displays general information
 * about the application, its author and website and reveals details about the
 * platform on which the application is running.
 * 
 * @author Achim Weimert
 * 
 */
public class About extends Screen implements CommandListener {
	
	public About(Controller controller) {
		super("About", controller);
		
		append("Thanks for using "+controller.getMidletName()+". ");
		append("Please visist\n"+controller.getMidletInfoUrl()+"\nfor bug reports and updates.\n");
		
		append("\nSystem Information:\n");
		append("Version: "+controller.getMidletVersion()+"\n");
		append("Platform: "+System.getProperty("microedition.platform")+"\n");
		append("Profiles: "+System.getProperty("microedition.profiles")+"\n");
		append("Size: "+controller.getMidletJarSize()+"\n");
		
		append("\nLicense Information:\n");
		append("This program is free software: you can redistribute it and/or modify "
				+ "it under the terms of the GNU General Public License as published by "
				+ "the Free Software Foundation, either version 3 of the License, or "
				+ "(at your option) any later version.");
		append("This program is distributed in the hope that it will be useful, but "
				+ "WITHOUT ANY WARRANTY; without even the implied warranty of "
				+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU "
				+ "General Public License for more details.");
		append("http://www.gnu.org/licenses/");
		
		addCommand(backCommand);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable arg1) {
		if (command==backCommand) {
			controller.lastScreen();
		}
	}

}
