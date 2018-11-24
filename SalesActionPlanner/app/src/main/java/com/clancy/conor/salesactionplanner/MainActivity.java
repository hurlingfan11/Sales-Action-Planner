package com.clancy.conor.salesactionplanner;

import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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
        final CheckBox completeCheckBox = view.findViewById(R.id.chkWindows);

        builder.setTitle("Add this Action!");

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Map<String, Object> mq = new HashMap<>();

                mq.put(Constants.KEY_OWNER, ownerEditText.getText().toString());
                mq.put(Constants.KEY_DESCRIPTION, descriptionEditText.getText().toString());
                mq.put(Constants.KEY_TITLE, titleEditText.getText().toString());
                //mq.put(Constants.KEY_CREATED, new Date());

                FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH).add(mq);

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
