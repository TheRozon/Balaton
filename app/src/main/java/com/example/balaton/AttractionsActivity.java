package com.example.balaton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AttractionsActivity extends AppCompatActivity {
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<Place> mPlaceList;
    private PlaceAdapter mAdapter;
    private int gridNum = 1;
    private boolean viewRow = true;
    private FirebaseFirestore mFirestore;
    private CollectionReference mPlaces;
    private AlarmManager malarmManager;
    private String orderName = "true";
    private String orderRate = "false";

    private SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);
        user = FirebaseAuth.getInstance().getCurrentUser();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
        } else {
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNum));
        mPlaceList = new ArrayList<>();

        mAdapter = new PlaceAdapter(this, mPlaceList);

        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mPlaces = mFirestore.collection("Places");
        queryData();


        malarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        setAlarmManager();

    }

    private void queryData(){
        mPlaceList.clear();
        if (this.orderName == "true") {
            mPlaces.orderBy("placeName").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Place place = doc.toObject(Place.class);
                    mPlaceList.add(place);
                }

                if (mPlaceList.size() == 0) {
                    initializeData();
                    queryData();
                }
                mAdapter.notifyDataSetChanged();
            });
        }
        if (this.orderRate == "true") {
            mPlaces.orderBy("ratedInfo", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Place place = doc.toObject(Place.class);
                    mPlaceList.add(place);
                }

                if (mPlaceList.size() == 0) {
                    initializeData();
                    queryData();
                }
                mAdapter.notifyDataSetChanged();
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.place_manu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.orderName:
                this.orderRate = "false";
                this.orderName = "true";
                queryData();
                break;
            case R.id.orderRate:
                this.orderName = "false";
                this.orderRate = "true";
                queryData();
                break;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
            case R.id.view_select:
                if(viewRow){
                    changeSpanCount(item, R.drawable.ic_view_col, 2);
                    break;
                }else{
                    changeSpanCount(item, R.drawable.ic_view_row, 1);
                    break;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    private void initializeData() {
        String[] placeList = getResources().getStringArray(R.array.place_names);
        String[] placeShortDetail = getResources().getStringArray(R.array.place_details);
        TypedArray placeImageResource = getResources().obtainTypedArray(R.array.place_images);
        TypedArray placeRate = getResources().obtainTypedArray(R.array.place_rates);
        for(int i = 0; i < placeList.length; i++){

            mPlaces.add(new Place(placeList[i], placeShortDetail[i], placeRate.getFloat(i, 0), placeImageResource.getResourceId(i, 0)));
        }
        placeImageResource.recycle();
    }

    private void setAlarmManager() {
        long repeatInterval = AlarmManager.INTERVAL_HALF_HOUR;
        long trigTime = SystemClock.elapsedRealtime() + repeatInterval;
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        malarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                trigTime,
                repeatInterval,
                pendingIntent);
    }

    public void openGPS(View view){
    }

}