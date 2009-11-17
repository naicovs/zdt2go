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
package com.google.code.zdt2go.data.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * The DateHelper class provides functions to convert a string to a Date.
 * 
 * @author Achim Weimert
 *
 */
public class DateHelper {
	
	private static final int MONTH = 0;
	private static final int DAY = 1;
	private static final int YEAR = 2;
	private static final int HOUR = 3;
	private static final int MINUTE = 4;
	private static final String seperator[] = {"-", "-", " ", ":"};


	/**
	 * Convert data of the form "MM-DD-YY HH:mm" to Date
	 * @param string
	 * @return Date or null
	 */
	public static final Date convertStringToDate(String string) {
		if (string==null) {
			return null;
		}
		
		// split string into parts
		String[] parts = splitStringIntoParts(string);
		if (parts==null) {
			return null;
		}

		// validation
		boolean result = validateDateParts(parts);
		if (!result) {
			return null;
		}
		
		// create data
		Calendar calendar = createCalendarFromParts(parts);
		
		return calendar.getTime();
	}


	private static Calendar createCalendarFromParts(String[] parts) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.set(Calendar.YEAR, Integer.parseInt(parts[YEAR]));
		calendar.set(Calendar.MONTH, Integer.parseInt(parts[MONTH])-1);
		calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[DAY]));
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[HOUR]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(parts[MINUTE]));
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}


	private static boolean validateDateParts(String[] parts) {
		// make sure all parts are numbers
		for (int j=0; j<parts.length; j++) {
			try {
				int number = Integer.parseInt(parts[j]);
				if (number<0) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		// check year
		if (parts[YEAR].length()!=2 && parts[YEAR].length()!=4) {
			return false;
		}
		if (parts[YEAR].length()==2) {
			// move year into the right century
			if (Integer.parseInt(parts[YEAR])>=50) {
				parts[YEAR] = "19" + parts[YEAR];
			} else {
				parts[YEAR] = "20" + parts[YEAR];
			}
		}
		// check month
		if (parts[MONTH].length()!=1 && parts[MONTH].length()!=2) {
			return false;
		}
		if (Integer.parseInt(parts[MONTH])>12) {
			return false;
		}
		// check day
		if (parts[DAY].length()!=1 && parts[DAY].length()!=2) {
			return false;
		}
		if (Integer.parseInt(parts[DAY])>31) {
			return false;
		}
		// check hour
		if (parts[HOUR].length()!=1 && parts[HOUR].length()!=2) {
			return false;
		}
		if (Integer.parseInt(parts[HOUR])>23) {
			return false;
		}
		// check minute
		if (parts[MINUTE].length()!=1 && parts[MINUTE].length()!=2) {
			return false;
		}
		if (Integer.parseInt(parts[MINUTE])>59) {
			return false;
		}

		return true;
	}


	private static String[] splitStringIntoParts(String string) {
		String parts[] = new String[seperator.length+1];
		int pos, startPos;
		
		// split string into parts
		startPos = 0;
		int i;
		try {
			for (i=0; i<seperator.length; i++) {
				pos = string.indexOf(seperator[i], startPos);
				if (pos<0) {
					return null;
				}
				parts[i] = string.substring(startPos, pos);
				parts[i] = parts[i].trim();
				startPos = pos+1;
			}
			parts[i] = string.substring(startPos);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		return parts;
	}
}
