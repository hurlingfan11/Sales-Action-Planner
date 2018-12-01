package com.clancy.conor.salesactionplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    CalendarView commitDateCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        SalesActionPlannerAdapter salesActionPlannerAdapter = new SalesActionPlannerAdapter();
        recyclerView.setAdapter(salesActionPlannerAdapter);

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

    private void showAddFields() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH).add(insertData);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
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

        return super.onOptionsItemSelected(item);
    }
}
