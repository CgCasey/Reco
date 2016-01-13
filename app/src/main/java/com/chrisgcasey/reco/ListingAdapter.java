package com.chrisgcasey.reco;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chrisgcasey.reco.APIs.GoogleServicesHelper;
import com.chrisgcasey.reco.model.ActiveListings;
import com.chrisgcasey.reco.model.Listing;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chris on 1/4/2016.
 */
public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
        implements Callback<ActiveListings> {
    public static final int REQUEST_CODE_PLUS_ONE = 10;
    public static final int REQUEST_CODE_SHARE = 11;
    private MainActivity mMainActivity;
    private LayoutInflater mInflater;
    private ActiveListings mActiveListings;
    private boolean isGooglePlayServicesAvailable;
    //create constructor that takes a context
    public ListingAdapter(MainActivity mMainActivity){
        this.mMainActivity = mMainActivity;
        //create a layoutinflater object
        mInflater = LayoutInflater.from(mMainActivity);
        isGooglePlayServicesAvailable = GoogleServicesHelper.isGooglePlayServicesAvailable();

    }

    @Override
    public ListingHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //create a new viewholder
        return new ListingHolder(mInflater.inflate(R.layout.layout_listing, null));
    }

    @Override
    public void onBindViewHolder(ListingHolder listingHolder, int i) {
        final Listing listing = mActiveListings.results[i];
        listingHolder.titleView.setText(listing.title);
        listingHolder.priceView.setText(listing.price);
        listingHolder.shopNameView.setText(listing.Shop.shop_name);

        listingHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(listing.url));
                mMainActivity.startActivity(intent);
            }
        });

        if (isGooglePlayServicesAvailable){
            listingHolder.plusOneButton.setVisibility(View.VISIBLE);
            listingHolder.plusOneButton.initialize(listing.url, REQUEST_CODE_PLUS_ONE);
            listingHolder.plusOneButton.setAnnotation(PlusOneButton.ANNOTATION_NONE);

            listingHolder.plusOneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new PlusShare.Builder(mMainActivity)
                            .setType("text/plain")
                            .setText("Checkout this item on Etsy " + listing.title)
                            .setContentUrl(Uri.parse(listing.url))
                            .getIntent();
                    mMainActivity.startActivityForResult(intent, REQUEST_CODE_SHARE);
                }
            });
        }
        else {
            listingHolder.plusOneButton.setVisibility(View.INVISIBLE);


        }

        listingHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Checkout this item on Etsy " + listing.url);
                intent.setType("text/plain");

                mMainActivity.startActivityForResult(Intent.createChooser(intent, "Share"), REQUEST_CODE_SHARE);
            }
        });

        Picasso.with(listingHolder.imageView.getContext())
                .load(listing.Images[0].url_570xN)
                .into(listingHolder.imageView);

    }


    @Override
    public int getItemCount() {
        if (mActiveListings == null) {
            return 0;
        }
        if (mActiveListings.results == null){
            return 0;
        }
        else return mActiveListings.results.length;
    }

    @Override
    public void success(ActiveListings activeListings, Response response) {
        this.mActiveListings = activeListings;
        notifyDataSetChanged();
        mMainActivity.showList();

    }

    @Override
    public void failure(RetrofitError error) {
        mMainActivity.showError();

    }

    public ActiveListings getmActiveListings() {
        return mActiveListings;
    }

    public class ListingHolder extends RecyclerView.ViewHolder{
        //declare the view fields
        ImageView imageView;
        TextView titleView;
        TextView shopNameView;
        TextView priceView;
        PlusOneButton plusOneButton;
        ImageButton shareButton;

        public ListingHolder(View itemView) {
            super(itemView);
            //instantiate the views
            imageView = (ImageView) itemView.findViewById(R.id.listing_image);
            titleView = (TextView) itemView.findViewById(R.id.listing_title);
            shopNameView = (TextView) itemView.findViewById(R.id.listing_shop_name);
            priceView = (TextView) itemView.findViewById(R.id.listing_price);
            plusOneButton = (PlusOneButton) itemView.findViewById(R.id.btn_plus_one);
            shareButton = (ImageButton) itemView.findViewById(R.id.btn_share);
        }
    }
}
