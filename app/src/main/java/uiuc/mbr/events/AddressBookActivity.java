package uiuc.mbr.events;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import uiuc.mbr.Alarm;
import uiuc.mbr.R;
import uiuc.mbr.serv.AlarmService;

/**
 * Displays a list of all addresses inputted into the AddressBook
 * Allows the user to edit or delete any entry
 *
 * Created by Scott on 3/10/2016.
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
		LinearLayout my_layout = (LinearLayout) findViewById(R.id.address_book);
		my_layout.removeAllViews();

		AddressBook.initIfNecessary(getApplicationContext());
		List<UserLocation> fullAddressBook = AddressBook.getAll(getApplicationContext());
		if(fullAddressBook == null) {
			TextView tv = new TextView(this);
			tv.setText("No addresses found.");
			my_layout.addView(tv);
			return;
		}
		//From: http://stackoverflow.com/questions/13226353/android-checkbox-dynamically
		for (int i = 0; i < fullAddressBook.size(); i++) {
			UserLocation current = fullAddressBook.get(i);
			if(current.address == null || current.address.length() == 0)
				continue;
			String baseAddress = current.name;

			TableRow row = new TableRow(this);
			row.setId(i);
			row.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

			TextView tv = new TextView(this);
			tv.setText(baseAddress + " -> " + current.address + " @ " + current.latitude + ", " + current.longitude);
			row.addView(tv, new TableRow.LayoutParams(0));

			//TODO: Improve button layout
			Button editButton = new Button(this);
			editButton.setText("Edit");
			editButton.setMinHeight(50);
			editButton.setMinWidth(50);
			editButton.setVisibility(View.VISIBLE);
			editButton.setOnClickListener(new EditButtonListener(current, this));
			//row.addView(editButton, new TableRow.LayoutParams(1));

			Button delButton = new Button(this);
			delButton.setText("Delete");
			delButton.setMinHeight(50);
			delButton.setMinWidth(50);
			delButton.setVisibility(View.VISIBLE);
			delButton.setOnClickListener(new DeleteButtonListener(current, this));
			//row.addView(delButton, new TableRow.LayoutParams(2));

			my_layout.addView(row);
			my_layout.addView(editButton);
			my_layout.addView(delButton);
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
			for(Alarm alarm : AlarmService.getUntriggeredAlarms())
			{
				if(location.name.equals(alarm.event.getLocation()))
					AlarmService.remove(alarm.event.getParentEventId(), getApplicationContext());
			}
			displayAddressBookList();
		}
	}
}
