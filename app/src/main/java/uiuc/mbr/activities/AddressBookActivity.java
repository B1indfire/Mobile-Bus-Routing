package uiuc.mbr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import uiuc.mbr.alarm.Alarm;
import uiuc.mbr.R;
import uiuc.mbr.event_selection.AddressBook;
import uiuc.mbr.event_selection.LocationLookup;
import uiuc.mbr.event_selection.UserLocation;
import uiuc.mbr.alarm.AlarmService;

/**
 * Displays a list of all addresses inputted into the AddressBook
 * Allows the user to edit or delete any entry
 */
public class AddressBookActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_book);
	}

	@Override
	protected void onResume() {
		super.onResume();
		displayAddressBookList();
	}

	/**
	 * Loads and displays a scrollable list of Saved Addresses
	 * Provides buttons for editing and deleting each address
	 */
	private void displayAddressBookList() {
		LinearLayout myLayout = (LinearLayout) findViewById(R.id.address_book);
		myLayout.removeAllViews();

		Button addButton = (Button) findViewById(R.id.addAddress);
		addButton.setOnClickListener(new AddAddressButtonListener(this));

		AddressBook.initIfNecessary(getApplicationContext());
		List<UserLocation> fullAddressBook = AddressBook.getAll(getApplicationContext());
		if(fullAddressBook == null || fullAddressBook.size() == 0) {
			TextView tv = new TextView(this);
			tv.setText("No addresses found.");
			myLayout.addView(tv);
			return;
		}

		//From: http://stackoverflow.com/questions/13226353/android-checkbox-dynamically
		for (int i = 0; i < fullAddressBook.size(); i++) {
			UserLocation curUserLoc = fullAddressBook.get(i);

			TableRow row = new TableRow(this);
			row.setId(i);
			row.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

			TextView tv = new TextView(this);
			String baseAddress = curUserLoc.name;
			String mapAddress = (curUserLoc.address == null) ? "" : curUserLoc.address;
			tv.setText(baseAddress + " -> " + mapAddress + " @ " + curUserLoc.latitude + ", " + curUserLoc.longitude);
			row.addView(tv, new TableRow.LayoutParams(0));

			//TODO: Improve button layout
			Button editButton = new Button(this);
			editButton.setId(i*100);
			editButton.setText("Edit");
			editButton.setMinHeight(50);
			editButton.setMinWidth(50);
			editButton.setVisibility(View.VISIBLE);
			editButton.setOnClickListener(new EditButtonListener(curUserLoc, this));

			Button delButton = new Button(this);
			editButton.setId((i*100)+1);
			delButton.setText("Delete");
			delButton.setMinHeight(50);
			delButton.setMinWidth(50);
			delButton.setVisibility(View.VISIBLE);
			delButton.setOnClickListener(new DeleteButtonListener(curUserLoc, this));

			myLayout.addView(row);
			myLayout.addView(editButton);
			myLayout.addView(delButton);
		}
	}

	/**
	 * OnClickListener implementation for the Edit button
	 */
	private class EditButtonListener implements View.OnClickListener {

		private UserLocation location;
		private Activity parent;

		private String addressInput;

		public EditButtonListener(UserLocation loc, Activity a) {
			this.location = loc;
			parent = a;
		}

		@Override
		public void onClick(View v) {
			promptForNewAddress();
		}

		/**
		 * Prompts the user for a new address target
		 * Changes the AddressBook entry if valid
		 * Rejects the user's input if invalid
		 */
		private void promptForNewAddress() {
			final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
			builder.setTitle("Enter a new Address");

			// Set up the input
			final EditText input = new EditText(parent);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(input);

			addressInput = "";

			// Set up the buttons
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					addressInput = input.getText().toString();
					LatLng ll = LocationLookup.lookupLocation(addressInput, getApplicationContext());
					if (ll != null) { //Valid address
						location.address = addressInput;
						location.latitude = ll.latitude;
						location.longitude = ll.longitude;
						if(AddressBook.getByName(location.name, getApplicationContext()) == null)
							AddressBook.create(location, getApplicationContext());
						else
							AddressBook.update(location, getApplicationContext());
					} else { //Invalid Address
						Toast toast = Toast.makeText(parent, "Invalid Address", Toast.LENGTH_SHORT);
						toast.show();
					}
					displayAddressBookList();
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			parent.runOnUiThread(new Runnable() {
				public void run() {
					builder.show();
				}
			});
		}

	}


	/**
	 * OnClickListener implementation for the Delete button
	 */
	private class DeleteButtonListener implements View.OnClickListener {

		private UserLocation location;
		private Activity parent;

		public DeleteButtonListener(UserLocation location, Activity c) {
			this.location = location;
			parent = c;
		}

		/**
		 * Removes the Address entry from AddressBook
		 * Removes all events using the given address from the Schedule (since they're all now invalid)
		 * Refreshes the display
		 */
		@Override
		public void onClick(View v) {
			AddressBook.delete(location.name, parent);
			if(AlarmService.getUntriggeredAlarms()!=null) {
				for (Alarm alarm : AlarmService.getUntriggeredAlarms()) {
					if (location.name.equals(alarm.event.getLocation()))
						AlarmService.remove(alarm.event.getParentEventId(), getApplicationContext());
				}
			}
			displayAddressBookList();
		}

	}

	/**
	 * OnClickListener implementation for the Add Address button
	 */
	private class AddAddressButtonListener implements View.OnClickListener {
		private UserLocation location;
		private Activity parent;

		private String addressInput;
		private String locInput;

		public AddAddressButtonListener(Activity a) {
			parent = a;
		}

		@Override
		public void onClick(View v) {
			promptForNewLocation();
		}

		/**
		 * Prompts the user for a new location
		 * Continues to promptForLocAddress if valid string.
		 */
		private void promptForNewLocation() {
			final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
			builder.setTitle("Enter a new location");

			// Set up the input
			final EditText input = new EditText(parent);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(input);

			locInput = "";

			// Set up the buttons
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					locInput = input.getText().toString();

					if (locInput!=null && locInput.length() > 0 && locInput.length()<100) { //Valid location string length
						if(AddressBook.getByName(locInput, getApplicationContext())==null) { //Valid location string
							UserLocation temp = new UserLocation(locInput);
							promptForLocAddress(temp);
						}
						else {
							Toast.makeText(parent, "There's already a saved location with this name!", Toast.LENGTH_SHORT).show();
						}
					}
					else { //Invalid location string
						if(!(locInput.length()>0))
							Toast.makeText(parent, "You need at least one character", Toast.LENGTH_SHORT).show();
						if(locInput.length()>100)
							Toast.makeText(parent, "Too many characters! You've only got 100.", Toast.LENGTH_SHORT).show();
					}
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			parent.runOnUiThread(new Runnable() {
				public void run() {
					builder.show();
				}
			});
		}

		/**
		 * Only called when the user enters a valid string for a new location
		 * Prompts the user for a valid address string to map their location name to
		 */
		private void promptForLocAddress(UserLocation loc) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
			builder.setTitle("Enter a new address");

			// Set up the input
			final EditText input = new EditText(parent);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(input);

			addressInput = "";

			// Set up the buttons
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					addressInput = input.getText().toString();
					LatLng ll = LocationLookup.lookupLocation(addressInput, getApplicationContext());
					if (ll != null) { //Valid address
						location = new UserLocation(locInput, addressInput, ll.latitude, ll.longitude);
						if (AddressBook.getByName(location.name, getApplicationContext()) == null)
							AddressBook.create(location, getApplicationContext());
						else
							AddressBook.update(location, getApplicationContext());
					} else { //Invalid Address
						Toast.makeText(parent, "Invalid Address", Toast.LENGTH_SHORT).show();
					}
					displayAddressBookList();
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			parent.runOnUiThread(new Runnable() {
				public void run() {
					builder.show();
				}
			});
		}
	}

}
