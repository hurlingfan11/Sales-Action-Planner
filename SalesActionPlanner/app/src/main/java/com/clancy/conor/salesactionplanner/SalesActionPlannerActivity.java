package com.clancy.conor.salesactionplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class SalesActionPlannerActivity extends AppCompatActivity {

    //private ImageView mPhotoBucketImageView;
    private TextView mOwnerTextView;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mCommitDateTextView;
    private CheckBox mCompleteCheckBox;
    //private TextView mTitleTextView;

    // DocRef & DocSnapShots
    private DocumentReference mDocRef;
    private DocumentSnapshot mDocSnapShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_action_planner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mOwnerTextView = findViewById(R.id.detail_owner_edittext);
        mTitleTextView = findViewById(R.id.detail_title_edittext);
        mDescriptionTextView = findViewById(R.id.detail_description_edittext);
        mCommitDateTextView = findViewById(R.id.detail_calendar_edittext);
        mCompleteCheckBox = findViewById(R.id.detail_chkWindows);

        GregorianCalendar calendar = new GregorianCalendar();

        Intent receivedIntent = getIntent();
        //FRom received intent need to pull out the doc_id
        String docId = receivedIntent.getStringExtra(Constants.EXTRA_DOC_ID);

        // Where am I pointing to in Firebase, get the right reference, useful for read, update

        mDocRef = FirebaseFirestore.getInstance().
                collection(Constants.COLLECTION_PATH).document(docId);

        mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            // Will get either an exception or a docuement SnapShot
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(Constants.TAG, "listen failed");
                }
                //Check if the doc exists, will be using delete so want to know if it exists first
                if (documentSnapshot.exists()) {
                    mDocSnapShot = documentSnapshot; //Save document snapshot so can use it in other places as well
                    mOwnerTextView.setText((String) documentSnapshot.get(Constants.KEY_OWNER));
                    mTitleTextView.setText((String) documentSnapshot.get(Constants.KEY_TITLE));
                    mDescriptionTextView.setText((String) documentSnapshot.get(Constants.KEY_DESCRIPTION));
                    mCommitDateTextView.setText((String) documentSnapshot.get(Constants.KEY_DATE));
                    //mCompleteCheckBox.setText((String)documentSnapshot.get(Constants.KEY_STATUS));
                    if((String)mDocSnapShot.get(Constants.KEY_STATUS).toString() == "true"){
                        mCompleteCheckBox.setChecked(true);
                    }else{
                        mCompleteCheckBox.setChecked(false);
                    }
                    //
                    // Ion.with(mPhotoBucketImageView).load((String) documentSnapshot.get(Constants.KEY_IMAGE_URL));
                    //mPhotoBucketImageView.setImageResource((String)documentSnapshot.get(Constants.KEY_MOVIE));
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showEditFields();
                //finish();

                /*Snackbar.make(view, "Update Saved to Cloud!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    private void saveData() {

        Map<String, Object> insertData = new HashMap<>();

        if (mOwnerTextView.getText().toString() != null && !mOwnerTextView.getText().toString().isEmpty()) {
            insertData.put(Constants.KEY_OWNER, mOwnerTextView.getText().toString());
        } else {
            Toast.makeText(getApplicationContext(), "Please fill out the owner field!", Toast.LENGTH_LONG).show();
        }

        if (mTitleTextView.getText().toString() != null && !mTitleTextView.getText().toString().isEmpty()) {
            insertData.put(Constants.KEY_TITLE, mTitleTextView.getText().toString());
        } else {
            Toast.makeText(getApplicationContext(), "Please fill out the title field!", Toast.LENGTH_LONG).show();
        }

        if (mDescriptionTextView.getText().toString() != null && !mDescriptionTextView.getText().toString().isEmpty()) {
            insertData.put(Constants.KEY_DESCRIPTION, mDescriptionTextView.getText().toString());
        } else {
            Toast.makeText(getApplicationContext(), "Please fill out the description field!", Toast.LENGTH_LONG).show();
        }

        if (mCommitDateTextView.getText().toString() != null && !mCommitDateTextView.getText().toString().isEmpty()) {
            insertData.put(Constants.KEY_DATE, mCommitDateTextView.getText().toString());
        } else {
            Toast.makeText(getApplicationContext(), "Data Sasved to Cloud!", Toast.LENGTH_LONG).show();
        }
        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH).add(insertData);
        mDocRef.update(insertData);

    }

    private void showEditFields() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.salesactionplanner_dialog, null, false);
        builder.setView(view);
        builder.setTitle("Edit Action!");

        final TextView ownerEditText = view.findViewById(R.id.dialog_owner_edittext);
        final TextView descriptionEditText = view.findViewById(R.id.dialog_description_edittext);
        final TextView titleEditText = view.findViewById(R.id.dialog_title_edittext);
        final TextView commitDateTextView = view.findViewById(R.id.dialog_date_textview);
        final CheckBox completeCheckBox = view.findViewById(R.id.chkWindows);
        final CalendarView commitDateCalendarView = view.findViewById(R.id.calendar_view);
        final GregorianCalendar calendar = new GregorianCalendar();

       ownerEditText.setText((String)mDocSnapShot.get(Constants.KEY_OWNER));
        descriptionEditText.setText((String)mDocSnapShot.get(Constants.KEY_DESCRIPTION));
        titleEditText.setText((String)mDocSnapShot.get(Constants.KEY_TITLE));
        commitDateTextView.setText((String)mDocSnapShot.get(Constants.KEY_DATE));

        if((String)mDocSnapShot.get(Constants.KEY_STATUS).toString() == "true"){
            completeCheckBox.setChecked(true);
        }else{
            completeCheckBox.setChecked(false);
        }


        /*commitDateCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // display the selected date by using a toast
                String date = dayOfMonth + "/" + month + "/" + year;
                commitDateTextView.setText(date);

                Toast.makeText(getApplicationContext(), dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
            }
        });*/

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Map<String, Object> insertData = new HashMap<>();

                insertData.put(Constants.KEY_OWNER, ownerEditText.getText().toString());
                insertData.put(Constants.KEY_DESCRIPTION, descriptionEditText.getText().toString());
                insertData.put(Constants.KEY_TITLE, titleEditText.getText().toString());
                insertData.put(Constants.KEY_DATE, commitDateTextView.getText().toString());
                insertData.put(Constants.KEY_STATUS, completeCheckBox.isChecked());
                mDocRef.update(insertData);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                Toast toast = Toast.makeText(getApplicationContext(), "You clicked CANCEL.", Toast.LENGTH_LONG);
                toast.show();
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
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

            case R.id.action_delete:
                mDocRef.delete();
                finish();
                Toast.makeText(this, "Delete button was clicked", Toast.LENGTH_LONG).show();
                break;

            case R.id.action_return:
                finish();
                Toast.makeText(this, "Return button was clicked", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }



}
