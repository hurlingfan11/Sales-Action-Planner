package com.clancy.conor.salesactionplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    CalendarView commitDateCalendarView;
    private String firstname;
    private String lastname;
    private ArrayList mNotes = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* Searcher searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);
        InstantSearch helper = new InstantSearchHelper(this, searcher);
        helper.search();*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRecyclerView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFields();
                Snackbar.make(view, "Time to add an action", Snackbar.LENGTH_LONG)
                        .setAction("New Action!", null).show();

            }
        });
    }

    @Override
    protected void onDestroy() {
        //searcher.destroy();
        super.onDestroy();
    }

    private void showAddFields() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.salesactionplanner_dialog, null, false);
        builder.setView(view);

        final TextView ownerEditText = view.findViewById(R.id.dialog_owner_edittext);
        final TextView descriptionEditText = view.findViewById(R.id.dialog_description_edittext);
        final TextView titleEditText = view.findViewById(R.id.dialog_title_edittext);
        final TextView commitDateTextView = view.findViewById(R.id.dialog_date_textview);
        final CheckBox completeCheckBox = view.findViewById(R.id.chkWindows);
        final CalendarView commitDateCalendarView = view.findViewById(R.id.calendar_view);
        final GregorianCalendar calendar = new GregorianCalendar();


        //simpleCalendarView.setSelectedWeekBackgroundColor(Color.RED); // red color for the selected week's background
        //simpleCalendarView.setWeekSeparatorLineColor(Color.GREEN); // green color for the week separator line
        // perform setOnDateChangeListener event on CalendarView
        commitDateCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // display the selected date by using a toast
                String date = dayOfMonth + "/" + month + "/" + year;
                commitDateTextView.setText(date);

                Toast.makeText(getApplicationContext(), dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
            }
        });

        builder.setTitle("Add this Action!");

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Map<String, Object> insertData = new HashMap<>();

                insertData.put(Constants.KEY_OWNER, ownerEditText.getText().toString());
                insertData.put(Constants.KEY_DESCRIPTION, descriptionEditText.getText().toString());
                insertData.put(Constants.KEY_TITLE, titleEditText.getText().toString());
                insertData.put(Constants.KEY_DATE, commitDateTextView.getText().toString());
                insertData.put(Constants.KEY_STATUS, completeCheckBox.isChecked());
                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH).add(insertData);

                searchAlgolia(insertData);
                // For Algolia Search

            }
        });
        builder.setNegativeButton("Cancel?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                Toast toast = Toast.makeText(getApplicationContext(), "You clicked CANCEL.", Toast.LENGTH_LONG);
                toast.show();
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void searchAlgolia(Map<String, Object> insertData) {
        // For Algolia Search
        Client client = new Client(Constants.ALGOLIA_APP_IP, Constants.ALGOLIA_SEARCH_API_KEY);

        Index index = client.getIndex(Constants.ALGOLIA_INDEX_FOLDER_NAME);

        List<JSONObject> array = new ArrayList<JSONObject>();

        //array.add(new JSONObject(insertData));

        //index.addObjectsAsync(new JSONArray(array), null);

        try {
            index.addObjectAsync(new JSONObject()
                    .put("firstname", "Jimmie")
                    .put("lastname", "Barninger"), null);
        } catch (JSONException e) {
            //some exception handler code.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_reset:

                // TODO: Highlight to get rid of an itemview

                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout), "Item Cleared", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Someone has just clicked your button
                        // TODO: To do the undo

                        Snackbar.make(findViewById(R.id.coordinator_layout), "Item Restored", Snackbar.LENGTH_SHORT).show();
                    }
                });
                snackbar.show();
                Toast.makeText(this, "Reset button was clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_settings:
                android.app.AlertDialog.Builder alertDialogActionSettings = new android.app.AlertDialog.Builder(this);
                alertDialogActionSettings.setTitle(getString(R.string.remove_all));
                alertDialogActionSettings.setMessage("Are you sure you want to change settings?");
                alertDialogActionSettings.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                    }
                });
                // Add a cancel button
                alertDialogActionSettings.setNegativeButton(android.R.string.cancel, null);

                alertDialogActionSettings.create().show();

                Toast.makeText(this, "Settings was clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_search:
                // make a new method
                showSearchDialog();
                Toast.makeText(this, "Search button was clicked", Toast.LENGTH_LONG).show();
                break;

            case R.id.action_about:
                 Intent intent = new Intent(this, About_Activity.class);
                // We need to pass data in one direction, so use doc_id
                // starts the activity
                startActivity(intent);

                Toast.makeText(this, "Search button was clicked", Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showSearchDialog() {
        // show new dialog
        /*android.app.AlertDialog.Builder alertDialog1 = new android.app.AlertDialog.Builder(this);
        alertDialog1.setTitle(R.string.choose_an_item);
        // setItems can't be an arrayList, needs to be an array of strings
       // alertDialog1.setItems(getNames(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //sets the current item
                //mCurrentItem = mItems.get(which);
                // shows the current item
                //showCurrentItem();
            }
        });

        // Add a cancel button
        alertDialog1.setNegativeButton(android.R.string.cancel, null);

        alertDialog1.create().show();*/
    }

  /*  private String[] getNames() {
        String[] names = new String[mItems.size()];
        // for loop to go through mItems
        for(int i=0; i<mItems.size(); i++){
            // get it at location i and get its name
            names[i] = mItems.get(i).getName();

        }
        return names;
    }*/


    public void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        SalesActionPlannerAdapter salesActionPlannerAdapter = new SalesActionPlannerAdapter();
        recyclerView.setAdapter(salesActionPlannerAdapter);
    }

    public void getActions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Used document reference for inserting new note in to Firestore
        //Now using a collection reference object to retrieve all documents in salesplanner section

        CollectionReference actionsCollectionRef = db.collection("salesplanner");

         /*
    Query actionsQuery = actionsCollectionRef.whereEqualTo("user_id", FirebaseFirestore.getInstance().

    /*
    actionsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot task.getResult()){
                    ContactsContract.CommonDataKinds.Note note = document.toObject(ContactsContract.CommonDataKinds.Note.class);
                }

            }else{
                makeSnackBarMesage("Query failed, Check logs");
            }
        }
    });
}*/

    }
}
