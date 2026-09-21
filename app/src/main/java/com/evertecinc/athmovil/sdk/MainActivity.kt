package com.evertecinc.athmovil.sdk

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.evertecinc.athmovil.sdk.Constants.ITEMS_PREF_KEY
import com.evertecinc.athmovil.sdk.checkout.objects.Items
import com.evertecinc.athmovil.sdk.checkout.utils.JsonUtil
import com.evertecinc.athmovil.sdk.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener,
    ItemsListAdapter.ItemButtonClickListener {

    private lateinit var binding: ActivityMainBinding
    private val items = ArrayList<Items>()
    private lateinit var adapter: ItemsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.btnGotToCart.setOnClickListener(this)
        binding.ivSettings.setOnClickListener(this)
        binding.btnAddDefaultItem.setOnClickListener(this)
        binding.btnAddCustomItem.setOnClickListener(this)

        adapter = ItemsListAdapter(this)
        binding.rvItemsList.layoutManager = LinearLayoutManager(this)
        binding.rvItemsList.adapter = adapter

        val savedItemsJson = Utils.getPrefsString(ITEMS_PREF_KEY, this)
        if (savedItemsJson.isNotEmpty()) {
            val savedItems = Utils.decodeJSON(savedItemsJson)
            if (savedItems != null) {
                items.addAll(savedItems)
            }
        }
        adapter.loadData(items, false)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnGotToCart -> {
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("items", items)
                startActivity(intent)
            }
            R.id.ivSettings -> {
                startActivity(Intent(this, ConfigActivity::class.java))
            }
            R.id.btnAddDefaultItem -> {
                addItem("Item", "Description", "1", "1", "Metadata")
            }
            R.id.btnAddCustomItem -> {
                showAddItemDialog()
            }
        }
    }

    private fun showAddItemDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Item")

        val name = EditText(this)
        val price = EditText(this)
        val description = EditText(this)
        val quantity = EditText(this)
        val metadata = EditText(this)

        name.inputType = InputType.TYPE_CLASS_TEXT
        name.hint = "Name"
        price.inputType = InputType.TYPE_CLASS_PHONE
        price.addTextChangedListener(Utils.CurrencyTextWatcher())
        price.hint = "Price"
        description.inputType = InputType.TYPE_CLASS_TEXT
        description.hint = "Description"
        quantity.inputType = InputType.TYPE_CLASS_PHONE
        quantity.hint = "Quantity"
        metadata.inputType = InputType.TYPE_CLASS_TEXT
        metadata.hint = "Metadata"

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL

        linearLayout.addView(name)
        linearLayout.addView(price)
        linearLayout.addView(description)
        linearLayout.addView(quantity)
        linearLayout.addView(metadata)
        builder.setView(linearLayout)

        builder.setPositiveButton("OK") { _, _ ->
            addItem(
                name.text.toString(),
                description.text.toString(),
                quantity.text.toString(),
                price.text.toString().replace(",", ""),
                metadata.text.toString()
            )
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun addItem(
        name: String?,
        description: String?,
        quantity: String?,
        price: String?,
        metadata: String?
    ) {
        var finalName = name
        var finalQuantity = quantity
        var finalPrice = price

        if (finalName.isNullOrEmpty()) finalName = ""
        if (finalQuantity.isNullOrEmpty()) finalQuantity = "1"
        if (finalPrice.isNullOrEmpty()) finalPrice = "1"

        items.add(
            Items(
                finalName,
                description,
                finalPrice.toDouble(),
                finalQuantity.toLong(),
                metadata
            )
        )
        adapter.loadData(items, false)
        Utils.setPrefsString(
            ITEMS_PREF_KEY,
            JsonUtil.itemsToJson(items).toString(),
            this
        )
    }

    override fun onButtonPressed(position: Int) {
        if (position in 0 until items.size) {
            items.removeAt(position)
            adapter.loadData(items, false)
            Utils.setPrefsString(
                ITEMS_PREF_KEY,
                JsonUtil.itemsToJson(items).toString(),
                this
            )
        }
    }
}
