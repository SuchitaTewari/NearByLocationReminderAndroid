package project.suchita.com.nearbyreminder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class AddReminderActivity extends Activity {
    CustomAdapterReminderActivity customAdapterReminderActivity;
    EditText addTitle, reminder_note, place_name, address;
    Button btnReminder;
    ArrayList<ReminderList> reminderLists;
    ArrayAdapter<ReminderList> adapter;
    ListView lv;
    Place  place;
    SharedPreferences sharedpreferences;
    AddReminderActivity aaReminder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list_contents);
        place = (Place) getIntent().getSerializableExtra("placeData");
        addTitle = (EditText) findViewById(R.id.tvAddTitle);
        reminder_note = (EditText)findViewById(R.id.tvReminderNote);
        place_name = (EditText)findViewById(R.id.tvPLaceName);
        place_name.setText(place.getName());

        address = (EditText)findViewById(R.id.tvAddress);
        address.setText(place.getAddress());

        btnReminder = (Button)findViewById(R.id.btnReminder);

       sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();


    }
}
