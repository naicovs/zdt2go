/*******************************************************************************
 * This file is part of zdt2go generator.
 * Copyright (c) 2009 Achim Weimert.
 * http://code.google.com/p/zdt2go/
 * 
 * zdt2go generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * zdt2go generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with zdt2go generator.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Achim Weimert - initial API and implementation
 ******************************************************************************/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Generator {

	private static final String PROJECT_NAME = "zdt2go";

	/**
	 * Directory in the jar archive where the resources are saved
	 */
	private static final String RESOURCE_DIRECTORY = "res/";

	/**
	 * Path in the jar archive to the MIDlet's jar file
	 */
	private static final String RESOURCE_JAR_FILE = RESOURCE_DIRECTORY
			+ PROJECT_NAME + ".jar";

	/**
	 * Path in the jar archive to the MIDlet's jad file
	 */
	private static final String RESOURCE_JAD_FILE = RESOURCE_DIRECTORY
			+ PROJECT_NAME + ".jad";

	/**
	 * Prefix for imported vocabulary files
	 */
	private static final String RESOURCES_PREFIX = "imported/";

	/**
	 * Directory in the MIDlet's jar file where new vocabulary files are saved
	 */
	private static final String RESOURCES_OUTPUT_DIRECTORY = "vocabulary/"
			+ RESOURCES_PREFIX;

	/**
	 * Path to the vocabulary index file in the MIDlet's jar file
	 */
	private static final String INDEX_FILE = "vocabulary/__index.txt";

	/**
	 * Name of the output jar file
	 */
	public static final String OUTPUT_JAR_FILE = PROJECT_NAME + ".jar";

	/**
	 * Name of the output jad file
	 */
	public static final String OUTPUT_JAD_FILE = PROJECT_NAME + ".jad";

	private Vector<String> vocabularyFiles;
	private Vector<String> additionalIndexFileEntries;
	private File temporaryResourceFile;
	private String outputDirectory;

	public Generator() {
		vocabularyFiles = new Vector<String>();
		outputDirectory = "";
		temporaryResourceFile = null;
	}

	/**
	 * Recursively adds all files in the given path to the output files.
	 * 
	 * @param path
	 *            path to a vocabulary file or a directory containing only
	 *            vocabulary files
	 */
	public void recursivelyAddFiles(String path) {
		File file = new File(path);
		if (file.isFile()) {
			vocabularyFiles.add(path);
		} else if (file.isDirectory()) {
			String[] children = file.list();
			sortArray(children);
			for (int i = 0; i < children.length; i++) {
				recursivelyAddFiles(file.getAbsolutePath() + File.separator
						+ children[i]);
			}
		}
	}

	private static final void sortArray(String[] strings) {
		Arrays.sort(strings, new Comparator<String>() {
			public int compare(final String o1, final String o2) {
				return o1.compareTo(o2);
			}
		});
	}

	/**
	 * Sets the output directory for the created files.
	 * 
	 * @param outputDirectory
	 *            path to the directory where the output files are to be put
	 */
	public void setOutputDirectory(String outputDirectory) {
		if (outputDirectory.length() > 0
				&& outputDirectory.charAt(outputDirectory.length() - 1) != File.separatorChar) {
			outputDirectory += File.separator;
		} else {
			outputDirectory = "";
		}
		this.outputDirectory = outputDirectory;
	}

	private String getJarFileName() {
		return outputDirectory + OUTPUT_JAR_FILE;
	}

	private String getJadFileName() {
		return outputDirectory + OUTPUT_JAD_FILE;
	}

	/**
	 * Create the output midlet files.
	 * @throws IOException 
	 */
	public void createJarAndJad() throws IOException {
		String jarFileName = getJarFileName();
		String jadFileName = getJadFileName();
		try {
			extractResourcesIntoTempFile();
			createMidlet(jarFileName);
			long fileSize = getFileSize(jarFileName);
			createDescriptionFile(jadFileName, fileSize);
		} finally {
			removeTemporaryResourceFile();
		}
	}

	private void removeTemporaryResourceFile() {
		if (temporaryResourceFile!=null) {
			temporaryResourceFile.delete();
			temporaryResourceFile = null;
		}
	}

	private void extractResourcesIntoTempFile() throws IOException {
		IOException exception = null;
		FileOutputStream out = null;
		// open input file
		InputStream in = getClass().getResourceAsStream(RESOURCE_JAR_FILE);
		if (in==null) {
			throw new FileNotFoundException("Included file not found: "+RESOURCE_JAD_FILE);
		}
		try {
			// create output file
			temporaryResourceFile = File.createTempFile(PROJECT_NAME, null);
			temporaryResourceFile.deleteOnExit();
			// open output file
			out = new FileOutputStream(temporaryResourceFile);
			// Copy data form resources into new file
			copyInputToOutput(in, out);
		} catch (IOException e) {
			exception = e;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					if (exception != null) {
						throw new IOException(
								"Nested error while closing InputStream: " + e,
								exception);
					} else {
						throw new IOException(
								"Error while closing InputStream", e);
					}
				}
			}
			try {
				in.close();
			} catch (IOException e) {
				if (exception != null) {
					throw new IOException(
							"Nested error while closing InputStream: " + e,
							exception);
				} else {
					throw new IOException(
							"Error while closing InputStream", e);
				}
			}
		}

	}

	private long getFileSize(String fileName) {
		File createdFile = new File(fileName);
		long fileSize = createdFile.length();
		for (int i = 0; i < 10 && fileSize <= 0; i++) {
			// try again after a short delay
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			fileSize = createdFile.length();
		}
		return fileSize;
	}

	private void createDescriptionFile(String outputFile, long fileSize) throws IOException {
		String size = "MIDlet-Jar-Size: " + fileSize;
		FileOutputStream out = null;
		IOException exception = null;
		InputStream in = getClass().getResourceAsStream(RESOURCE_JAD_FILE);
		if (in==null) {
			throw new FileNotFoundException("Included file not found: "+RESOURCE_JAD_FILE);
		}
		try {
			// Create new file
			out = new FileOutputStream(outputFile);
			// Copy data form resources into new file
			copyInputToOutput(in, out);
			// Append line with size of midlet to new file
			out.write(size.getBytes("UTF-8"));
		} catch (IOException e) {
			exception = e;
		} finally {
			// Finish writing
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					if (exception != null) {
						throw new IOException(
								"Nested error while closing InputStream: " + e,
								exception);
					} else {
						throw new IOException(
								"Error while closing InputStream", e);
					}
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Checks if an output file already exists.
	 * 
	 * @return true if one or more output files already exist
	 */
	public boolean existsOutputFile() {
		File jarFile = new File(getJarFileName());
		File jadFile = new File(getJadFileName());
		if (jarFile.exists() || jadFile.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private void createMidlet(String outputFileName) throws IOException {
		additionalIndexFileEntries = new Vector<String>();
		ZipOutputStream out = null;
		IOException ex = null;
		try {
			// Create the ZIP file
			out = new ZipOutputStream(new FileOutputStream(
					outputFileName));
			// Add the vocabulary files
			addCustomVocabularyFiles(out);
			// Open the resources file
			addResourceFiles(out);
		} catch (IOException e) {
			ex = e;
		} finally {
			// Complete the ZIP file
			if (out != null) {
				try {
					out.close();
				} catch (IOException closeException) {
					if (ex == null) {
						throw closeException;
					} else {
						throw new IOException("Nested Error: "+closeException.getMessage(), ex);
					}
				}
			}
			if (ex != null) {
				throw new IOException(ex);
			}
		}
	}

	private void addResourceFiles(ZipOutputStream outputStream)
			throws IOException {
		JarFile jarFile = new JarFile(temporaryResourceFile);
		// Add all entries of resources file to new archive
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			ZipEntry entry = enumeration.nextElement();
			if (entry.getName().compareTo(INDEX_FILE) == 0) {
				writeIndexZipEntryToZipStream(jarFile.getInputStream(entry),
						outputStream);
			} else {
				writeZipEntryToZipStream(entry, jarFile.getInputStream(entry),
						outputStream);
			}
		}
	}

	private void addCustomVocabularyFiles(ZipOutputStream outputStream)
			throws FileNotFoundException, IOException {
		for (int i = 0; i < vocabularyFiles.size(); i++) {
			// Get file names
			String inputFileName = vocabularyFiles.elementAt(i);
			String outputFileName = new File(inputFileName).getName();
			// Write file to the stream
			writeFileToZipStream(inputFileName, RESOURCES_OUTPUT_DIRECTORY
					+ outputFileName, outputStream);
			// Save entry for index file
			additionalIndexFileEntries.add(outputFileName);
		}
	}

	private void writeZipEntryToZipStream(ZipEntry entry, InputStream in,
			ZipOutputStream out) throws IOException {
		// Start a new entry in the ZIP file
		out.putNextEntry(new ZipEntry(entry));
		// Transfer bytes from the file to the ZIP file
		copyInputToOutput(in, out);
		// Complete the entry
		out.closeEntry();
		in.close();
	}

	private void writeIndexZipEntryToZipStream(InputStream in,
			ZipOutputStream out) throws IOException {
		// Start a new entry in the ZIP file
		out.putNextEntry(new ZipEntry(INDEX_FILE));
		// Add custom vocabulary files to index file
		writeCustomEntries(out);
		// Copy existing entries to index file
		copyInputToOutput(in, out);
		// Complete the entry
		out.closeEntry();
		in.close();
	}

	private void writeCustomEntries(ZipOutputStream out) throws IOException,
			UnsupportedEncodingException {
		for (int i = 0; i < additionalIndexFileEntries.size(); i++) {
			// Get file names
			String fileName = additionalIndexFileEntries.elementAt(i);
			String name = fileName.substring(0, fileName.indexOf('.'));
			fileName = RESOURCES_PREFIX + fileName;
			// Create entry line
			String line = name + "\t" + fileName + "\n";
			// Write entry line to stream
			out.write(line.getBytes("UTF-8"));
		}
	}

	private void writeFileToZipStream(String inputFileName,
			String outputFileName, ZipOutputStream out)
			throws FileNotFoundException, IOException {
		// Open the vocabulary file for reading
		FileInputStream in = new FileInputStream(inputFileName);
		// Start a new entry in the ZIP file
		out.putNextEntry(new ZipEntry(outputFileName));
		// Transfer bytes from the file to the ZIP file
		copyInputToOutput(in, out);
		// Complete the entry
		out.closeEntry();
		in.close();
	}

	private void copyInputToOutput(InputStream inputStream,
			OutputStream outputStream) throws IOException {
		byte[] buf = new byte[1024];
		int len;
		while ((len = inputStream.read(buf)) > 0) {
			outputStream.write(buf, 0, len);
		}
	}
}
