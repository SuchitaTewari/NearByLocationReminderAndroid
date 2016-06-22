package project.suchita.com.nearbyreminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoryActivity extends Activity {
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.lvCategoryItems );

        // Create and populate a List of planet names.
        final String[] items = new String[] { "Amusement Park", "ATM", "Bakery", "Bank",
                "Bar", "Beauty Salon", "Bus Station", "Cafe","Car Repair","Coaching"};
        ArrayList<String> categoryList = new ArrayList<String>();
        categoryList.addAll(Arrays.asList(items));

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this,R.layout.category_list_item, categoryList);
        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String selecteditem = items[+position];
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MyPref",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("placeType",selecteditem.toLowerCase());
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}