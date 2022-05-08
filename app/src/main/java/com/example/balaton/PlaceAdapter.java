package com.example.balaton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> implements Filterable {
    private ArrayList<Place> mPlacesData;
    private ArrayList<Place> mPlacesDataAll;
    private Context mContext;
    private int lastPosition = -1;



    PlaceAdapter(Context context, ArrayList<Place> placesData){
        this.mPlacesData = placesData;
        this.mPlacesDataAll = placesData;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_places, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place currentPlace = mPlacesData.get(position);

        holder.bindTo(currentPlace);

        float random = (float) Math.random();
        if (random < 0.5) {
            if(holder.getAbsoluteAdapterPosition() > lastPosition){
                Animation slide = AnimationUtils.loadAnimation(mContext, R.anim.slide);
                holder.itemView.startAnimation(slide);
                lastPosition = holder.getAbsoluteAdapterPosition();
            }
        } else {
            if(holder.getAbsoluteAdapterPosition() > lastPosition){
                Animation slide = AnimationUtils.loadAnimation(mContext, R.anim.slide_another);
                holder.itemView.startAnimation(slide);
                lastPosition = holder.getAbsoluteAdapterPosition();
            }
        }

    }

    @Override
    public int getItemCount() {
        return mPlacesData.size();
    }

    @Override
    public Filter getFilter() {
        return placeFilter;
    }

    private Filter placeFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Place> filteredPlaces = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0){
                results.count = mPlacesDataAll.size();
                results.values = mPlacesDataAll;
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Place place : mPlacesDataAll){
                    if(place.getPlaceName().toLowerCase().contains(filterPattern)){
                        filteredPlaces.add(place);
                    }
                }
                results.count = filteredPlaces.size();
                results.values = filteredPlaces;
            }

            return results;

        };
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mPlacesData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mPlaceName;
        private TextView mShortDetail;
        private RatingBar mRatingBar;
        private ImageView mPlaceImage;

        public ViewHolder(View placeView) {
            super(placeView);

            mPlaceName = placeView.findViewById(R.id.textViewPlaceName);
            mShortDetail = placeView.findViewById(R.id.textViewShortDetail);
            mRatingBar = placeView.findViewById(R.id.ratingBar);
            mPlaceImage = itemView.findViewById(R.id.itemViewPlaceImage);


        }
        public void bindTo(Place currentPlace) {
            mPlaceName.setText(currentPlace.getPlaceName());
            mShortDetail.setText(currentPlace.getShortDetail());
            mRatingBar.setRating(currentPlace.getRatedInfo());

            Glide.with(mContext).load(currentPlace.getImageResource()).into(mPlaceImage);
        }
    };





};


