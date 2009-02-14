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
package model;

import java.io.IOException;
import java.io.InputStream;

import jmunit.framework.cldc11.TestCase;
import test.TestHelper;

public class CategoryListTest extends TestCase {
	
	private CategoryList categoryList;
	
	public CategoryListTest() {
		super(2, "CategoryListTest");
	}
	
	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
		case 0: testCountCategories(); break;
		case 1: testLoadCategoriesFromInputStream(); break;
		}
	}
	
	public void setUp() {
		categoryList = new CategoryList();
	}
	
	public void testCountCategories() {
		assertEquals(0, categoryList.countCategories());
	}
	
	public void testLoadCategoriesFromInputStream() {
		String data = "XXXXXXX\n" +
		"mini	mini.txt";

		InputStream inputStream = TestHelper.getInputStreamFromString(data);

		// loading of null stream fails
		try {
			categoryList.loadCategoriesFromStream(null);
			fail("Loading of empty category has not failed.");
		} catch (IOException e) {
			fail("Loading of empty category failed with IOException: "+e);
		} catch (NullPointerException e) {
			// this one is expected
		}
		// loading of given stream works
		try {
			categoryList.loadCategoriesFromStream(inputStream);
		} catch (IOException e) {
			fail(e.toString());
		}
		// now we have two categoryList with the given data
		assertEquals(2, categoryList.countCategories());
		assertEquals("XXXXXXX", categoryList.getCategory(0).getName());
		assertNull(categoryList.getCategory(0).getFile());
		assertEquals("mini", categoryList.getCategory(1).getName());
		assertEquals("mini.txt", categoryList.getCategory(1).getFile());
	}

	
}
