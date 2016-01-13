package com.chrisgcasey.reco;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chrisgcasey.reco.APIs.Etsy;
import com.chrisgcasey.reco.APIs.GoogleServicesHelper;
import com.chrisgcasey.reco.model.ActiveListings;

public class MainActivity extends AppCompatActivity implements GoogleServicesHelper.GoogleServicesListener {
    //declare fields
    private static final String STATE_ACTIVE_LISTINGS = "StateActiveListings";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorText;
    private ListingAdapter adapter;
    private GoogleServicesHelper googleServicesHelper;
    public GoogleServicesHelper.GoogleServicesListener googleServicesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //instantiate views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        errorText = (TextView) findViewById(R.id.txtError);
        googleServicesHelper = new GoogleServicesHelper(this, this);

        //setup the recyclerview
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ListingAdapter(this);
        recyclerView.setAdapter(adapter);
        //if first time running app; make api call and get the listings
        if (savedInstanceState == null){
            showLoading();
            Etsy.getActiveListings(adapter);
        }
        else {//check if data is present
            if (savedInstanceState.containsKey(STATE_ACTIVE_LISTINGS)) {
                adapter.success((ActiveListings) savedInstanceState.getParcelable(
                        STATE_ACTIVE_LISTINGS), null);
                showList();
            }
            else {//otherwise make api call again
                showLoading();
                Etsy.getActiveListings(adapter);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleServicesHelper.handleActivityResult(requestCode, resultCode, data);

        if (requestCode == ListingAdapter.REQUEST_CODE_PLUS_ONE) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ActiveListings activeListings = adapter.getmActiveListings();
        if (activeListings != null){
            outState.putParcelable(STATE_ACTIVE_LISTINGS, activeListings);
        }
    }

    //method to set progressbar visible while loading data for list
    public void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);

    }
    //method to set list visible when server call is successful
    public void showList() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }
    //method to set error text visible if server call encounters an erro
    public void showError() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleServicesHelper.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleServicesHelper.disconnect();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }
}
