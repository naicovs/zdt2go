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
package test;

import model.CategoryListTest;
import model.EntryTest;
import model.CategoryTest;
import model.SessionTest;
import model.SettingsTest;
import model.StatisticsTest;
import model.helper.DateHelperTest;
import model.helper.InputStreamReaderIteratorTest;
import model.helper.TextStreamLineParserTest;
import jmunit.framework.cldc11.TestSuite;

/**
 * The ApplicationTestSuite class is responsible for calling each of the
 * application's unit tests.
 * 
 * @author Achim Weimert
 * 
 */
public class ApplicationTestSuite extends TestSuite {
	
	public ApplicationTestSuite() {
		super("All Tests");
		add(new CategoryListTest());
		add(new DateHelperTest());
		add(new EntryTest());
		add(new SessionTest());
		add(new SettingsTest());
		add(new CategoryTest());
		add(new StatisticsTest());
		add(new TextStreamLineParserTest());
		add(new InputStreamReaderIteratorTest());
	}

}
