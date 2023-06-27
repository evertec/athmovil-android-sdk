package com.evertecinc.athmovil.sdk;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evertecinc.athmovil.sdk.checkout.objects.Items;

import java.util.ArrayList;

public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ViewHolder> {

    private ArrayList<Items> mDataSet;
    private boolean isFinalView;
    private final ItemButtonClickListener buttonClickListener;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvName;
        private final TextView tvQuantity;
        private final TextView tvDescription;
        private final TextView tvPrice;
        private final TextView tvMetadata;
        private final ImageView ivClose;

        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            tvName = v.findViewById(R.id.tvName);
            tvQuantity = v.findViewById(R.id.tvQuantity);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvMetadata = v.findViewById(R.id.tvMetadata);
            ivClose = v.findViewById(R.id.ivMetadata);
            v.setTag(v);
        }
    }

    /**
     * Initialize the Adapter.
     */
    public ItemsListAdapter( ItemButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_items_list,
                viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        Items item = mDataSet.get(position);
        if (item != null) {
            viewHolder.tvName.setText(item.getName());
            viewHolder.tvDescription.setText(item.getDesc());
            viewHolder.tvQuantity.setText(TextUtils.concat("x",String.valueOf(item.getQuantity())));
            viewHolder.tvPrice.setText(Utils.getBalanceString(item.getPrice().toString()));
            viewHolder.tvMetadata.setText(item.getMetadata());
            if(isFinalView){
                viewHolder.ivClose.setVisibility(View.GONE);
            }
            viewHolder.ivClose.setOnClickListener(view -> {
                if (buttonClickListener != null) {
                    buttonClickListener.onButtonPressed(position);
                }
            });
        }
    }

    public void loadData(final ArrayList<Items> allCards, boolean isFinalView) {
        this.mDataSet = allCards;
        this.isFinalView = isFinalView;
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataSet == null) {
            return 0;
        }
        return mDataSet.size();
    }

    public interface ItemButtonClickListener {
        void onButtonPressed(int position);
    }
}