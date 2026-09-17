package com.evertecinc.athmovil.sdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.evertecinc.athmovil.sdk.checkout.objects.Items
import com.evertecinc.athmovil.sdk.databinding.ActivityItemsBinding
import java.util.ArrayList

class ItemsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityItemsBinding = DataBindingUtil.setContentView(this, R.layout.activity_items)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.ivItemsBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        
        val adapter = ItemsListAdapter(null)
        binding.rvItemsList.layoutManager = LinearLayoutManager(this)
        binding.rvItemsList.adapter = adapter

        val serializable = getIntent().getSerializableExtra("items")

        if (serializable is ArrayList<*>) {
            @Suppress("UNCHECKED_CAST")
            adapter.loadData(serializable as ArrayList<Items>, true)
        }
    }
}
