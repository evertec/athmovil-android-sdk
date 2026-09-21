package com.evertecinc.athmovil.sdk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.evertecinc.athmovil.sdk.checkout.PaymentResponse
import com.evertecinc.athmovil.sdk.checkout.interfaces.PaymentResponseListener
import com.evertecinc.athmovil.sdk.checkout.objects.Items
import com.evertecinc.athmovil.sdk.checkout.objects.PaymentReturnedData
import com.evertecinc.athmovil.sdk.databinding.ActivityPaymentResponseBinding
import java.util.*

class PaymentResponseActivity : AppCompatActivity(), PaymentResponseListener, View.OnClickListener {

    private lateinit var binding: ActivityPaymentResponseBinding
    private var items: ArrayList<Items>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_response)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.llShowItemsContainer.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        PaymentResponse.validatePaymentResponse(this, this, intent)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.llShowItemsContainer) {
            val currentItems = items
            if (currentItems != null && currentItems.isNotEmpty()) {
                val intent = Intent(this, ItemsListActivity::class.java)
                intent.putExtra("items", currentItems)
                startActivity(intent)
            }
        } else if (id == R.id.btnClose) {
            finish()
        }
    }

    override fun onCancelledPayment(date: Date, result: PaymentReturnedData) {
        binding.tvStatus.text = "CANCELLED"
        setAllData(date, result)
    }

    override fun onExpiredPayment(date: Date, result: PaymentReturnedData) {
        binding.tvStatus.text = "EXPIRED"
        setAllData(date, result)
    }

    override fun onFailedPayment(date: Date, result: PaymentReturnedData) {
        binding.tvStatus.text = "FAILED"
        setAllData(date, result)
    }

    override fun onCompletedPayment(date: Date, result: PaymentReturnedData) {
        binding.tvStatus.text = "COMPLETED"
        setAllData(date, result)
    }

    private fun setAllData(date: Date, result: PaymentReturnedData) {
        setData(
            date,
            result.referenceNumber,
            result.dailyTransactionID
        )

        setDataDos(
            result.total,
            result.tax,
            result.subtotal,
            result.fee
        )

        setDataTres(
            result.netAmount,
            result.metadata1,
            result.metadata2,
            result.sdkVersion,
            result.paymentId,
            result.getItemsSelectedList()
        )

        setOptionalContactFields(result.name, result.phoneNumber, result.email, result.ecommerceId, result.sdkVersion)
    }

    private fun setData(date: Date,referenceNumber: String?,dailyTransactionID: String?) {
        binding.tvReferenceNumber.text = referenceNumber
        binding.tvDate.text = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", date)
        binding.tvDailyTransactionID.text = dailyTransactionID
    }

    private fun setDataDos(total: Double, tax: Double, subtotal: Double, fee: Double) {
        binding.tvTotal.text = Utils.getBalanceString(total.toString())
        binding.tvTax.text = Utils.getBalanceString(tax.toString())
        binding.tvSubtotal.text = Utils.getBalanceString(subtotal.toString())
        binding.tvFee.text = Utils.getBalanceString(fee.toString())
    }

    private fun setDataTres(
        netAmount: Double,
        metadata1: String?,
        metadata2: String?,
        sdkVersion: String?,
        paymentId: String?,
        items: ArrayList<Items>
    ) {
        binding.tvNetAmount.text = Utils.getBalanceString(netAmount.toString())
        binding.tvMetadata1.text = metadata1
        binding.tvMetadata2.text = metadata2
        binding.tvSdkVersion.text = sdkVersion
        binding.tvPaymentID.text = paymentId
        this.items = items
    }

    override fun onPaymentException(error: String, message: String) {
        binding.tvStatus.text = "ERROR"
        binding.tvDate.text = error
        binding.tvDateTitle.text = "error"
        binding.tvReferenceNumber.text = message
        binding.tvReferenceNumberTitle.text = "message"
        binding.tvTotal.text = ""
        binding.tvTax.text = ""
        binding.tvSubtotal.text = ""
        binding.tvFee.text = ""
        binding.tvNetAmount.text = ""
        binding.tvMetadata1.text = ""
        binding.tvMetadata2.text = ""
        binding.tvEcommerceId.text = ""
        binding.tvSdkVersion.text = ""
        binding.tvEmail.text = ""
        binding.tvName.text = ""
        binding.tvDailyTransactionID.text = ""
        setOptionalContactFields(null, null, null, null, null)
    }

    private fun setOptionalContactFields(
        name: String?,
        phoneNumber: String?,
        email: String?,
        ecommerceId: String?,
        sdkVersion: String?
    ) {
        setOptionalField(binding.llNameRow, binding.vNameDivider, binding.tvName, name)
        setOptionalField(binding.llPhoneNumberRow, binding.vPhoneNumberDivider, binding.tvPhoneNumber, phoneNumber)
        setOptionalField(binding.llEmailRow, binding.vEmailDivider, binding.tvEmail, email)
        setOptionalField(binding.llEcommerceIDRow, binding.llEcommerceIDDivider, binding.tvEcommerceId, ecommerceId)
        setOptionalField(binding.llSdkVersionRow, binding.vSdkVersionDivider, binding.tvSdkVersion, sdkVersion)
    }

    private fun setOptionalField(row: View, divider: View, valueView: TextView, value: String?) {
        val hasValue = !value.isNullOrBlank()
        row.visibility = if (hasValue) View.VISIBLE else View.GONE
        divider.visibility = if (hasValue) View.VISIBLE else View.GONE
        valueView.text = value.orEmpty()
    }
}
