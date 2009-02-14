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

/**
 * The Filter class saves information about what vocabulary to include into a
 * learning session.
 * 
 * @author Achim Weimert
 * 
 */
class Filter {
	int maximumNumber;
	int maximumStreak;
	int maximumPercentage;
	int maximumTimesTested;
	int minimumDaysAgo;
	Filter() {
		maximumNumber = -1;
		maximumStreak = -1;
		maximumPercentage = 100;
		maximumTimesTested = -1;
		minimumDaysAgo = 0;
	}
}
