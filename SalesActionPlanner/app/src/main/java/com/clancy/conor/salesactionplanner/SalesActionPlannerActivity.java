package com.clancy.conor.salesactionplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class SalesActionPlannerActivity extends AppCompatActivity {

    //private ImageView mPhotoBucketImageView;
    private TextView mOwnerTextView;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
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
                showEditDialog();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void showEditDialog() {
                   AlertDialog.Builder builder = new AlertDialog.Builder(this);

            View view = getLayoutInflater().inflate(R.layout.salesactionplanner_dialog, null, false);
            builder.setView(view);

            final TextView mOwnerDialogEditText = view.findViewById(R.id.dialog_owner_edittext);
            final TextView mTitleDialogEditText = view.findViewById(R.id.dialog_title_edittext);
            final TextView mDescriptionDialogEditText = view.findViewById(R.id.dialog_description_edittext);

            mOwnerDialogEditText.setText((String) mDocSnapShot.get(Constants.KEY_OWNER));
            mTitleDialogEditText.setText((String) mDocSnapShot.get(Constants.KEY_TITLE));
            mDescriptionDialogEditText.setText((String) mDocSnapShot.get(Constants.KEY_DESCRIPTION));

            builder.setTitle("Edit Sales Action Planner");

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Map<String, Object> mq = new HashMap<>();

                    if(mOwnerDialogEditText.getText().toString() != null && !mOwnerDialogEditText.getText().toString().isEmpty())
                    {
                        mq.put(Constants.KEY_OWNER, mOwnerDialogEditText.getText().toString());
                    }else{
                        Toast.makeText(getApplicationContext(), "Please fill out the owner field!", Toast.LENGTH_LONG).show();
                    }

                    if(mTitleDialogEditText.getText().toString() != null && !mTitleDialogEditText.getText().toString().isEmpty())
                    {
                        mq.put(Constants.KEY_TITLE, mTitleDialogEditText.getText().toString());
                    }else{
                        Toast.makeText(getApplicationContext(), "Please fill out the title field!", Toast.LENGTH_LONG).show();
                    }

                    if(mDescriptionDialogEditText.getText().toString() != null && !mDescriptionDialogEditText.getText().toString().isEmpty())
                    {
                        mq.put(Constants.KEY_TITLE, mDescriptionDialogEditText.getText().toString());
                    }else{
                        Toast.makeText(getApplicationContext(), "Please fill out the description field!", Toast.LENGTH_LONG).show();
                    }

                    mDocRef.update(mq);

                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);

            builder.create().show();

        }

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            mDocRef.delete();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
