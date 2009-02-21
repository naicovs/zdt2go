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

import javax.microedition.lcdui.*;

import model.Data;

/**
 * The Controller interface specifies all methods that a controller needs to
 * provide to the view.
 * 
 * @author Achim Weimert
 *
 */
public interface Controller
{
	public Data getModel();
	public Displayable currentScreen();
	public void nextScreen(Displayable display);
	public void lastScreen();
	public void exit();
	public void changeScreen(Displayable display);
	public String getMidletVersion();
	public String getMidletJarSize();
	public String getMidletName();
	public String getMidletVendor();
	public String getMidletInfoUrl();
}
