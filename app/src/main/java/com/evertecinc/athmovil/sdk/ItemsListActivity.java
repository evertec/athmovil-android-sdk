package com.evertecinc.athmovil.sdk;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.databinding.ActivityItemsBinding;

import java.util.ArrayList;

public class ItemsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityItemsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_items);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);
        binding.ivItemsBack.setOnClickListener(v -> onBackPressed());
        ItemsListAdapter adapter = new ItemsListAdapter(null);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.rvItemsList.setLayoutManager(mLayoutManager);
        binding.rvItemsList.setAdapter(adapter);
        adapter.loadData((ArrayList<Items>) getIntent().getExtras().getSerializable("items"), true);
    }
}