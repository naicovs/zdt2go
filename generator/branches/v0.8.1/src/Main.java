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
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;



public class Main {
	
	public static void main(String[] args) {
		int result;
		Generator generator = new Generator();
		String outputDirectory;
		
		if (args.length==0) {
			JFileChooser directoryChoose = new JFileChooser();
			directoryChoose.setDialogTitle("Select output directory");
			directoryChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			result = directoryChoose.showOpenDialog(null);
			if (result == JFileChooser.CANCEL_OPTION) {
				return;
			}
	
			outputDirectory = directoryChoose.getSelectedFile().getAbsolutePath();
			generator.setOutputDirectory(outputDirectory);
	
			if (generator.existsOutputFile()) {
		    	result = JOptionPane.showConfirmDialog(null, "Output file exists.\n\nOverwrite?", "Warning", JOptionPane.YES_NO_OPTION);
		    	if (result==JOptionPane.NO_OPTION) {
		    		return;
		    	}
			}
			
			JFileChooser fileChoose = new JFileChooser();
			fileChoose.setDialogTitle("Select vocabulary files or directories. Press Ctrl to select multiple files.");
			fileChoose.setMultiSelectionEnabled(true);
			fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			result = fileChoose.showOpenDialog(null);
			if (result == JFileChooser.CANCEL_OPTION) {
				return;
			}
	
			File[] files = fileChoose.getSelectedFiles();
	
			for (int i=0; i<files.length; i++) {
				generator.recursivelyAddFiles(files[i].getAbsolutePath());
			}
		} else {
			outputDirectory = System.getProperty("user.dir");
			for (int i=0; i<args.length; i++) {
				generator.recursivelyAddFiles(args[i]);
			}
		}

		String message = "Done.\n\nCheck the output directory\n"
			+ outputDirectory + "\n"
			+ "for the files "+Generator.OUTPUT_JAR_FILE+" and "+Generator.OUTPUT_JAD_FILE+"\n\n"
			+ "To start studying transfer them to your mobile phone!";
		try {
			generator.createJarAndJad();
		} catch (IOException e) {
			message = "An IO exception occured, please retry. If the problem persists please file a bug report.\n\n"+e;
		} catch (RuntimeException e) {
			message = "A runtime exception occured, please retry. If the problem persists please file a bug report.\n\n"+e;
		} catch (Exception e) {
			message = "An exception occured, please retry. If the problem persists please file a bug report.\n\n"+e;
		}
		JOptionPane
				.showMessageDialog(
						null,
						message);
	}
}
