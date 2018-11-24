package com.clancy.conor.salesactionplanner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SalesActionPlannerAdapter extends RecyclerView.Adapter<SalesActionPlannerAdapter.SalesActionPlannerViewHolder>
{

    //Firebase set up, make an array of Document
    private List<DocumentSnapshot> mSalesActionPlannerSnapShots = new ArrayList<>();

    public SalesActionPlannerAdapter(){
        // Runs once as the adapter is made
        CollectionReference pbRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH);

        // Adds snapshotlistener  which receives documentsnapshots and an exception
        //pbRef.orderBy(Constants.KEY_CREATED, Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
        pbRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot DocumentSnapshots, FirebaseFirestoreException e) {
                if(e!=null){
                    Log.w(Constants.TAG, "Listening Failed");
                    return;
                }
                // Need to set our array to get documents which is a list
                mSalesActionPlannerSnapShots= DocumentSnapshots.getDocuments();
                notifyDataSetChanged();// Whenever it changes will notify the adapater

            }
        });
    }


    @NonNull
    @Override
    public SalesActionPlannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.salesactionplanner_itemview, parent,
             false);
       return new SalesActionPlannerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesActionPlannerViewHolder salesActionPlannerViewHolder, int i) {
        DocumentSnapshot ds = mSalesActionPlannerSnapShots.get(i);
        // Get() returns a generic object so we need to cast it to Strings as we know that is what they are
        // Get the caption
        String owner = (String) ds.get(Constants.KEY_OWNER);
        // Get the Image URL
        String description = (String) ds.get(Constants.KEY_DESCRIPTION);

        String title = (String) ds.get(Constants.KEY_TITLE);

        // want to set the two texts
        //
        salesActionPlannerViewHolder.mTitleTextView.setText(title);
        salesActionPlannerViewHolder.mOwnerTextView.setText(owner);
        salesActionPlannerViewHolder.mDescriptionTextView.setText(description);


    }

    @Override
    public int getItemCount() {

        return mSalesActionPlannerSnapShots .size();
    }

    class SalesActionPlannerViewHolder extends RecyclerView.ViewHolder{

        private TextView mOwnerTextView;
        private TextView mDescriptionTextView;
        private TextView mTitleTextView;

        //Default Constructor
        public SalesActionPlannerViewHolder(@NonNull View itemView) {
            super(itemView);

           // only looking inside itemview
            mOwnerTextView = itemView.findViewById(R.id.itemview_owner_TextView);
            mTitleTextView = itemView.findViewById(R.id.itemview_title_Textview);
            mDescriptionTextView = itemView.findViewById(R.id.itemview_title_Textview);
            //mDescriptionTextView = itemView.findViewById(R.id.itemview_description_Textview);

            // when someone selects an itemview, take an action
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Want to start an activity, to start an activity, need the context
                    DocumentSnapshot ds = mSalesActionPlannerSnapShots.get(getAdapterPosition());
                    Context c = v.getContext();
                    // to start an activity need an intent, so make an intent

                    Intent intent = new Intent(c, SalesActionPlannerActivity.class);
                    // We need to pass data in one direction, so use doc_id
                    intent.putExtra(Constants.EXTRA_DOC_ID, ds.getId());
                    // starts the activity
                    c.startActivity(intent);

                }
            });
        }
    }

}
