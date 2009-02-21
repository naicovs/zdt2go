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
package model.helper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

/**
 * The InputStreamReaderIterator class provides functions to read from an
 * InputStreamReader using the iterator interface.
 * 
 * @author Achim Weimert
 * 
 */
public class InputStreamReaderIterator {
	private static final int END_OF_FILE = -1;
	private InputStreamReader inputStreamReader;
	private int nextCharacter;

	public InputStreamReaderIterator(InputStreamReader inputStreamReader)
			throws IOException {
		this.inputStreamReader = inputStreamReader;
		loadNextCharacter();
	}

	public boolean hasNext() {
		return nextCharacter != END_OF_FILE;
	}

	private void loadNextCharacter() throws IOException {
		nextCharacter = inputStreamReader.read();
	}

	public int next() throws IOException {
		int currentCharacter = nextCharacter;
		if (currentCharacter == END_OF_FILE) {
			throw new NoSuchElementException();
		} else {
			loadNextCharacter();
		}
		return currentCharacter;
	}
}
