package com.evertecinc.athmovil.sdk

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.evertecinc.athmovil.sdk.checkout.OpenATHM
import com.evertecinc.athmovil.sdk.checkout.PayButton
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment
import com.evertecinc.athmovil.sdk.checkout.objects.Items
import com.evertecinc.athmovil.sdk.databinding.ActivityCartBinding
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private var paymentAmount: String = "0.0"
    private var items = ArrayList<Items>()
    private var buildType: String = ""
    private val payment by lazy { ATHMPayment(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        initView()
    }

    fun initView() {
        val savedPaymentAmount = Utils.getPrefsString(Constants.PAYMENT_AMOUNT_PREF_KEY, this)
        val savedTax = Utils.getPrefsString(Constants.TAX_PREF_KEY, this)
        val savedSubtotal = Utils.getPrefsString(Constants.SUBTOTAL_PREF_KEY, this)
        
        paymentAmount = if (savedPaymentAmount.isNotEmpty()) savedPaymentAmount else "0.0"
        val tax = if (savedTax.isNotEmpty()) savedTax else "0.0"
        val subtotal = if (savedSubtotal.isNotEmpty()) savedSubtotal else "0.0"
        
        binding.tvTotal.text = TextUtils.concat("$", paymentAmount)
        binding.tvSubtotal.text = TextUtils.concat("$", subtotal)
        binding.tvTax.text = TextUtils.concat("$", tax)

        setUpTheme(Utils.getPrefsString(Constants.THEME_PREF_KEY, this))
        setUpLanguage(Utils.getPrefsString(Constants.LANGUAGE_PREF_KEY, this))
        setBuildType(Utils.getPrefsString(Constants.BUILD_TYPE_PREF_KEY, this))
        setUpItems()

        binding.ivBack.setOnClickListener { finish() }
        binding.btnAthmCheckout.setOnClickListener { sendData() }
    }

    private fun setUpTheme(savedTheme: String?) {
        val theme = if (TextUtils.isEmpty(savedTheme)) "Original" else savedTheme
        when (theme) {
            "Dark" -> binding.btnAthmCheckout.setTheme(PayButton.ButtonTheme.DARK)
            "Light" -> binding.btnAthmCheckout.setTheme(PayButton.ButtonTheme.LIGHT)
            else -> binding.btnAthmCheckout.setTheme(PayButton.ButtonTheme.ORIGINAL)
        }
    }

    private fun setUpLanguage(savedLanguage: String?) {
        val language = runCatching {
            if (savedLanguage.isNullOrBlank()) {
                PayButton.ButtonLanguage.DEFAULT
            } else {
                PayButton.ButtonLanguage.valueOf(savedLanguage.uppercase(Locale.US))
            }
        }.getOrDefault(PayButton.ButtonLanguage.DEFAULT)

        binding.btnAthmCheckout.setLanguage(language)
    }

    override fun onResume() {
        super.onResume()
        showLoader()
        hideLoader()
    }

    private fun setUpItems() {
        val serializable = intent.getSerializableExtra("items")
        if (serializable is ArrayList<*>) {
            @Suppress("UNCHECKED_CAST")
            items = serializable as ArrayList<Items>
        }
    }

    private fun showLoader() {
        binding.btnAthmCheckout.background?.alpha = 50
        binding.loadingProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.loadingProgressBar.visibility = View.GONE
        binding.btnAthmCheckout.background?.alpha = 255
    }

    private fun setBuildType(savedBuildType: String?) {
        var build = savedBuildType
        if (TextUtils.isEmpty(build)) {
            build = getString(R.string.production)
        }

        buildType = ""
    }

    private fun sendData() {
        val token = Utils.getPrefsString(Constants.PUBLIC_TOKEN_PREF_KEY, this)

        payment.publicToken = token
        payment.items = items
        payment.phoneNumber = Utils.getPrefsString(Constants.PHONE_NUMBER_PREF_KEY, this)

        sendAmounts()
        sendMetadata()
        sendConfigs()
    }

    private fun sendMetadata() {
        var metadata1 = Utils.getPrefsString(Constants.METADATA1_PREF_KEY, this)
        if (TextUtils.isEmpty(metadata1)) {
            metadata1 = ""
        }
        payment.metadata1 = metadata1

        var metadata2 = Utils.getPrefsString(Constants.METADATA2_PREF_KEY, this)
        if (TextUtils.isEmpty(metadata2)) {
            metadata2 = ""
        }
        payment.metadata2 = metadata2
    }

    private fun sendConfigs() {
        val timeout = Utils.getPrefsInt(Constants.TIMEOUT_PREF_KEY, this)
        payment.timeout = timeout.toLong()

        //Need the Schema without the app bundle.
        payment.callbackSchema = "ATHMSDK"

        //For Evertec Test Only
        setBuildType(Utils.getPrefsString(Constants.BUILD_TYPE_PREF_KEY, this))
        payment.buildType = buildType

        makePayment(payment, this)
    }

    private fun parseAmount(value: String): Double {
        if (TextUtils.isEmpty(value)) return 0.0
        val trimmedValue = value.trim()
        return try {
            NumberFormat.getInstance(Locale.US).parse(trimmedValue)?.toDouble() ?: 0.0
        } catch (e: ParseException) {
            try {
                NumberFormat.getInstance(Locale("es", "ES")).parse(trimmedValue)?.toDouble() ?: 0.0
            } catch (ex: ParseException) {
                0.0
            }
        }
    }

    private fun sendAmounts() {
        val subtotal = Utils.getPrefsString(Constants.SUBTOTAL_PREF_KEY, this)
        payment.subtotal = parseAmount(subtotal)

        val tax = Utils.getPrefsString(Constants.TAX_PREF_KEY, this)
        payment.tax = parseAmount(tax)

        val amount = Utils.getPrefsString(Constants.PAYMENT_AMOUNT_PREF_KEY, this)
        payment.total = parseAmount(amount)
    }

    /**
     * Method to make a payment with ATHM SDK
     *
     * @param payment - object that contains all the data needed to do a payment
     * @param context - application context
     */
    private fun makePayment(payment: ATHMPayment, context: Context) {
        OpenATHM.validateData(payment, context)
    }
}
