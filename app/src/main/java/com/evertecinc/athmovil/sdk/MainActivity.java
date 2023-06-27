package com.evertecinc.athmovil.sdk;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.evertecinc.athmovil.sdk.checkout.objects.Items;
import com.evertecinc.athmovil.sdk.checkout.utils.JsonUtil;
import com.evertecinc.athmovil.sdk.databinding.ActivityMainBinding;

import java.util.ArrayList;

import static com.evertecinc.athmovil.sdk.Constants.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ItemsListAdapter.ItemButtonClickListener {

    ActivityMainBinding binding;
    ArrayList<Items> items = new ArrayList<>();
    ItemsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);
        binding.btnGotToCart.setOnClickListener(this);
        binding.ivSettings.setOnClickListener(this);
        binding.btnAddDefaultItem.setOnClickListener(this);
        binding.btnAddCustomItem.setOnClickListener(this);
        binding.executePendingBindings();
        adapter = new ItemsListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.rvItemsList.setLayoutManager(mLayoutManager);
        binding.rvItemsList.setAdapter(adapter);
        if (Utils.getPrefsString(ITEMS_PREF_KEY, this) != null) {
            ArrayList<Items> savedItems =
                    Utils.decodeJSON(Utils.getPrefsString(ITEMS_PREF_KEY, this));
            items.addAll(savedItems);
        }
        adapter.loadData(items, false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnGotToCart) {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("items", items);
            startActivity(intent);
        } else if (id == R.id.ivSettings) {
            startActivity(new Intent(this, ConfigActivity.class));
        } else if (id == R.id.btnAddDefaultItem) {
            addItem("Item", "Description", "1",
                    "1", "Metadata");
        } else if (id == R.id.btnAddCustomItem) {
            showAddItemDialog();
        }
    }

    public void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");

        final EditText name = new EditText(this);
        final EditText price = new EditText(this);
        final EditText description = new EditText(this);
        final EditText quantity = new EditText(this);
        final EditText metadata = new EditText(this);

        name.setInputType(InputType.TYPE_CLASS_TEXT);
        name.setHint("Name");
        price.setInputType(InputType.TYPE_CLASS_PHONE);
        price.addTextChangedListener((new Utils.CurrencyTextWatcher()));
        price.setHint("Price");
        description.setInputType(InputType.TYPE_CLASS_TEXT);
        description.setHint("Description");
        quantity.setInputType(InputType.TYPE_CLASS_PHONE);
        quantity.setHint("Quantity");
        metadata.setInputType(InputType.TYPE_CLASS_TEXT);
        metadata.setHint("Metadata");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(name);
        linearLayout.addView(price);
        linearLayout.addView(description);
        linearLayout.addView(quantity);
        linearLayout.addView(metadata);
        builder.setView(linearLayout);

        builder.setPositiveButton("OK", (dialog, which) -> addItem(name.getText().toString(),
                description.getText().toString(), quantity.getText().toString(),
                price.getText().toString().replaceAll(",", ""),
                metadata.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void addItem(String name, String description, String quantity, String price,
                        String metadata) {

        if (name == null || name.isEmpty())
            name = "";
        if (quantity == null || quantity.isEmpty())
            quantity = "1";
        if (price == null || price.isEmpty())
            price = "1";
        items.add(new Items(name, description, Double.parseDouble(price),
                Long.parseLong(quantity), metadata));
        adapter.loadData(items, false);
        Utils.setPrefsString(ITEMS_PREF_KEY,
                JsonUtil.itemsToJson(items).toString(), this);
    }

    @Override
    public void onButtonPressed(int position) {
        items.remove(items.get(position));
        adapter.loadData(items, false);
        Utils.setPrefsString(ITEMS_PREF_KEY,
                JsonUtil.itemsToJson(items).toString(), this);
    }

    InputFilter alphaNumericFilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {

            for (int i = start; i < end; i++) {
                if (!Character.toString(source.charAt(i)).matches("[a-zA-Z0-9 ]+")) {
                    return "";
                }
            }
            return null;
        }
    };
}
