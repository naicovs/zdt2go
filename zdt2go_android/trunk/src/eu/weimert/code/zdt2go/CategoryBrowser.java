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
package eu.weimert.code.zdt2go;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import eu.weimert.code.zdt2go.data.Category;

public class CategoryBrowser extends ListActivity {
	
	private File currentDirectory;
	private List<String> items = null;
	private static final int REQUEST_CONFIGURE_SESSION = 0;
	private static final int REQUEST_RUN_SESSION = 1;
	
	private static final int DIALOG_MESSAGE = 0;
	
	private static final String PATH_SDCARD = "/sdcard";
	private static final String PATH_ROOT = "/";
	
	private String messageTitle;
	private String messageText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        
        ((Button) findViewById(R.id.ButtonCard))
				.setOnClickListener(clickListener);
		((Button) findViewById(R.id.ButtonParent))
				.setOnClickListener(clickListener);
		((Button) findViewById(R.id.ButtonRoot))
				.setOnClickListener(clickListener);
        
        fillWithCard();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	try {
    		dismissDialog(DIALOG_MESSAGE);
    	} catch (IllegalArgumentException e) {
		}
    }
    
    private void fill(final File parent) {
    	currentDirectory = parent;
		updateNavigationButtonState();
		items = new ArrayList<String>();
		List<String> view = new ArrayList<String>();
		for (File file : parent.listFiles()) {
			String path = file.getPath();
			String name = file.getName();
			boolean isCategoryFile = isCategoryFile(file);
			if (file.isDirectory()) {
				path += File.separator;
				name += File.separator;
			} else if (!isCategoryFile) {
				continue;
			}
			items.add(path);
			view.add(name);
		}
		Collections.sort(items, String.CASE_INSENSITIVE_ORDER);
		Collections.sort(view, String.CASE_INSENSITIVE_ORDER);
		ArrayAdapter<String> fileList;
		if (findViewById(R.id.PathView).getVisibility() == View.GONE) {
			fileList = new ArrayAdapter<String>(this, R.layout.category_row, items);
		} else {
			fileList = new ArrayAdapter<String>(this, R.layout.category_row, view);
		}
		setListAdapter(fileList);
		((TextView) findViewById(R.id.PathView)).setText(getString(
				R.string.title_current_path, parent.getPath()));
    }
    
	private void updateNavigationButtonState() {
		if (currentDirectory != null
				&& currentDirectory.getPath().equals(PATH_ROOT)) {
			((Button) findViewById(R.id.ButtonRoot)).setEnabled(false);
		} else {
			((Button) findViewById(R.id.ButtonRoot)).setEnabled(true);
		}
		if (currentDirectory != null
				&& currentDirectory.getPath().equals(PATH_SDCARD)) {
			((Button) findViewById(R.id.ButtonCard)).setEnabled(false);
		} else {
			((Button) findViewById(R.id.ButtonCard)).setEnabled(true);
		}
		if (currentDirectory != null && currentDirectory.getParent() == null) {
			((Button) findViewById(R.id.ButtonParent)).setEnabled(false);
		} else {
			((Button) findViewById(R.id.ButtonParent)).setEnabled(true);
		}
	}

	private boolean isCategoryFile(File file) {
		return file.getName().endsWith(".txt");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		File file = new File(items.get(position));
		if (file.isDirectory()) {
			fill(file);
		} else if (isCategoryFile(file)) {
			Category category;
			try {
				category = new Category(file.getPath());
			} catch (IOException e) {
			    Toast.makeText(getBaseContext(), 
			            "Failed to load category.\n" + e, 
			            Toast.LENGTH_LONG).show();
			    return;
			}
			Intent intent = new Intent(getBaseContext(), ConfigureSession.class);
			intent.putExtra(ConfigureSession.BUNDLE_CATEGORY, category);
			startActivityForResult(intent, REQUEST_CONFIGURE_SESSION);
		} else {
		    Toast.makeText(getBaseContext(), 
		            R.string.msg_no_category, 
		            Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONFIGURE_SESSION:
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(getBaseContext(), Session.class);
				Parcelable parcelable = data.getParcelableExtra(Session.BUNDLE_SESSION);
				intent.putExtra(Session.BUNDLE_SESSION, parcelable);
				startActivityForResult(intent, REQUEST_RUN_SESSION);
			}
			break;
			
		case REQUEST_RUN_SESSION:
			if (resultCode == RESULT_OK) {
				float countCards = data.getIntExtra(Session.BUNDLE_COUNT_CARDS, -1);
				float countTries = data.getIntExtra(Session.BUNDLE_COUNT_TRIES, -1);
				messageTitle = "Info";
				messageText = "Session finished.";
				if (countCards > 0 && countTries > 0) {
					int percentage = (int) (countCards / countTries * 100.0);
					messageText = "Session finished\n\nResult: " + percentage + "%";
				}
				showDialog(DIALOG_MESSAGE);
			}
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void fillWithRoot() {
		fill(new File(PATH_ROOT));
	}
	
    private void fillWithCard() {
		fill(new File(PATH_SDCARD));
	}
    
    private void fillWithParent() {
		File file = currentDirectory.getParentFile();
		if (file != null && file.isDirectory()) {
			fill(file);
		} else {
		    Toast.makeText(getBaseContext(), 
		    		R.string.msg_no_category, 
		            Toast.LENGTH_LONG).show();
		}
	}

	private OnClickListener clickListener = new OnClickListener() {
        public void onClick(final View button) {
        	switch (button.getId()) {
			case R.id.ButtonCard:
				fillWithCard();
				break;
				
			case R.id.ButtonParent:
				fillWithParent();
				break;
				
			case R.id.ButtonRoot:
				fillWithRoot();
				break;

			default:
				break;
			}
        }
    };
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	if (id == DIALOG_MESSAGE) {
    		AlertDialog alert = (AlertDialog) dialog;
    		alert.setTitle(messageTitle);
    		alert.setMessage(messageText);
    	} else {
    		super.onPrepareDialog(id, dialog);
    	}
    }
    
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_MESSAGE) {
			Builder alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle(messageTitle);
			alertBuilder.setMessage(messageText);
			alertBuilder.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,
								final int whichButton) {
							dialog.cancel();
						}
					});
			return alertBuilder.create();
		}
		return super.onCreateDialog(id);
	}
    
}