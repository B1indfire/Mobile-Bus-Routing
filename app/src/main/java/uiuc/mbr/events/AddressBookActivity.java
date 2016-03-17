package uiuc.mbr.events;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import uiuc.mbr.R;
import uiuc.mbr.calendar.Calendar;
import uiuc.mbr.calendar.CalendarService;

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
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "EventSelectionActivity Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://uiuc.mbr.events/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "EventSelectionActivity Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://uiuc.mbr.events/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
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

        HashMap<String, LatLong> fullAddressBook = AddressBook.getFullAddressBook(this);
        Object[] baseAddresses = fullAddressBook.keySet().toArray();

        //From: http://stackoverflow.com/questions/13226353/android-checkbox-dynamically
        for (int i = 0; i < baseAddresses.length; i++) {
            String baseAddress = (String) baseAddresses[i];

            TableRow row = new TableRow(this);
            row.setId(i);
            row.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

            TextView tv = new TextView(this);
            tv.setText(baseAddress + " -> " + fullAddressBook.get(baseAddress).getStreetAddress());
            row.addView(tv, new TableRow.LayoutParams(0));

            //TODO: Improve button layout
            Button editButton = new Button(this);
            editButton.setText("Edit");
            editButton.setMinHeight(50);
            editButton.setMinWidth(50);
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(new EditButtonListener(baseAddress, this));
            //row.addView(editButton, new TableRow.LayoutParams(1));

            Button delButton = new Button(this);
            delButton.setText("Delete");
            delButton.setMinHeight(50);
            delButton.setMinWidth(50);
            delButton.setVisibility(View.VISIBLE);
            delButton.setOnClickListener(new DeleteButtonListener(baseAddress, this));
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

        private String key;
        private Activity parent;

        private String addressInput;

        public EditButtonListener(String k, Activity a) {
            key = k;
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
                    LatLong a = LatLong.getEventLocation(addressInput, parent);
                    if (a != null) { //Valid address
                        AddressBook.saveAddress(key, a, parent);
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

        private String key;
        private Activity parent;

        public DeleteButtonListener(String k, Activity c) {
            key = k;
            parent = c;
        }

        /**
         * Removes the Address entry from AddressBook
         * Removes all events using the given address from the Schedule (since they're all now invalid)
         * Refreshes the display
         */
        @Override
        public void onClick(View v) {
            AddressBook.remove(key, parent);
            Schedule.removeEventsWithAddress(key);
            displayAddressBookList();
        }
    }
}
