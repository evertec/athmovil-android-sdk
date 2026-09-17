package com.evertecinc.athmovil.sdk

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evertecinc.athmovil.sdk.checkout.objects.Items

class ItemsListAdapter(private val buttonClickListener: ItemButtonClickListener?) :
    RecyclerView.Adapter<ItemsListAdapter.ViewHolder>() {

    private var mDataSet: ArrayList<Items>? = null
    private var isFinalView = false

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvQuantity: TextView = v.findViewById(R.id.tvQuantity)
        val tvDescription: TextView = v.findViewById(R.id.tvDescription)
        val tvPrice: TextView = v.findViewById(R.id.tvPrice)
        val tvMetadata: TextView = v.findViewById(R.id.tvMetadata)
        val ivClose: ImageView = v.findViewById(R.id.ivMetadata)

        init {
            v.tag = v
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.item_items_list,
            viewGroup, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = mDataSet?.get(position)
        if (item != null) {
            viewHolder.tvName.text = item.name
            viewHolder.tvDescription.text = item.desc
            viewHolder.tvQuantity.text = TextUtils.concat("x", item.quantity.toString())
            viewHolder.tvPrice.text = Utils.getBalanceString(item.price.toString())
            viewHolder.tvMetadata.text = item.metadata
            if (isFinalView) {
                viewHolder.ivClose.visibility = View.GONE
            }
            viewHolder.ivClose.setOnClickListener {
                buttonClickListener?.onButtonPressed(position)
            }
        }
    }

    fun loadData(allCards: ArrayList<Items>?, isFinalView: Boolean) {
        this.mDataSet = allCards
        this.isFinalView = isFinalView
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mDataSet?.size ?: 0
    }

    interface ItemButtonClickListener {
        fun onButtonPressed(position: Int)
    }
}
