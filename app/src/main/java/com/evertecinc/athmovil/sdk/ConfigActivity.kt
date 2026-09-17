package com.evertecinc.athmovil.sdk

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.evertecinc.athmovil.sdk.checkout.PayButton
import com.evertecinc.athmovil.sdk.checkout.BuildConfig as SdkBuildConfig
import com.evertecinc.athmovil.sdk.databinding.ActivityConfigBinding

class ConfigActivity : AppCompatActivity(), CustomDialog.DialogResponseListener {

    private lateinit var binding: ActivityConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config)
        binding.executePendingBindings()
        binding.lifecycleOwner = this

        setUpInitialValues()
        setUpButtonThemeSelection()
        setUpButtonLanguageSelection()
        setUpBuildTypeSelection()
        setOnClickListeners()
    }

    private fun setUpInitialValues() {
        val defaultPublicToken = "dummy"
        var publicToken = Utils.getPrefsString(Constants.PUBLIC_TOKEN_PREF_KEY, this)

        if (publicToken.isEmpty()) {
            publicToken = defaultPublicToken
            Utils.setPrefsString(Constants.PUBLIC_TOKEN_PREF_KEY, defaultPublicToken, this)
        }

        binding.tvPublicToken.text = publicToken

        setupConfigs()
        setupAmounts()
        setUpMetadata()
    }

    private fun setupConfigs() {
        val savedTimeout = Utils.getPrefsInt(Constants.TIMEOUT_PREF_KEY, this)
        val timeout = if (savedTimeout >= 0) savedTimeout else 60
        binding.tvTimeout.text = TextUtils.concat(timeout.toString(), "s")

        val savedTheme = Utils.getPrefsString(Constants.THEME_PREF_KEY, this)
        val theme = if (savedTheme.isNotEmpty()) savedTheme else "Original"
        binding.tvTheme.text = theme

        val savedLanguage = Utils.getPrefsString(Constants.LANGUAGE_PREF_KEY, this)
        binding.tvLanguage.text = getLanguageLabel(savedLanguage)

        val savedBuildType = Utils.getPrefsString(Constants.BUILD_TYPE_PREF_KEY, this)
        val buildType = if (savedBuildType.isNotEmpty()) savedBuildType else getString(R.string.production)
        binding.tvBuildType.text = buildType

        binding.tvSdkVersion.text = SdkBuildConfig.SDK_VERSION
    }

    private fun setupAmounts() {
        val savedPaymentAmount = Utils.getPrefsString(Constants.PAYMENT_AMOUNT_PREF_KEY, this)
        val paymentAmount = if (savedPaymentAmount.isNotEmpty()) savedPaymentAmount else "0.00"
        binding.tvPaymentAmount.text = TextUtils.concat("$", paymentAmount)

        val savedSubtotal = Utils.getPrefsString(Constants.SUBTOTAL_PREF_KEY, this)
        val subtotal = if (savedSubtotal.isNotEmpty()) savedSubtotal else "0.00"
        binding.tvSubtotal.text = TextUtils.concat("$", subtotal)

        val savedTax = Utils.getPrefsString(Constants.TAX_PREF_KEY, this)
        val tax = if (savedTax.isNotEmpty()) savedTax else "0.00"
        binding.tvTax.text = TextUtils.concat("$", tax)
    }

    private fun setUpMetadata() {
        val savedMetadata1 = Utils.getPrefsString(Constants.METADATA1_PREF_KEY, this)
        binding.tvMetadata1.text = savedMetadata1

        val savedMetadata2 = Utils.getPrefsString(Constants.METADATA2_PREF_KEY, this)
        binding.tvMetadata2.text = savedMetadata2

        val savedPhoneNumber = Utils.getPrefsString(Constants.PHONE_NUMBER_PREF_KEY, this)
        binding.tvPhoneNumber.text = savedPhoneNumber
    }

    private fun setOnClickListeners() {
        binding.ivClose.setOnClickListener { finish() }

        binding.llPublicTokenContainer.setOnClickListener {
            CustomDialog.show(
                this,
                getString(R.string.public_token_alert_title),
                getString(R.string.public_token),
                getString(R.string.public_token_alert_message),
                Constants.RequestId.PUBLIC_TOKEN
            )
        }

        binding.llTimeoutContainer.setOnClickListener {
            CustomDialog.show(
                this,
                getString(R.string.timeout_alert_title),
                getString(R.string.timeout),
                getString(R.string.timeout_alert_message),
                Constants.RequestId.TIMEOUT
            )
        }

        binding.llPaymentAmountContainer.setOnClickListener {
            CustomDialog.show(
                this,
                getString(R.string.payment_alert_title),
                getString(R.string.payment_amount),
                getString(R.string.payment_alert_message),
                Constants.RequestId.PAYMENT_AMOUNT
            )
        }

        binding.llSubtotalContainer.setOnClickListener {
            CustomDialog.show(
                this,
                getString(R.string.subtotal_alert_title),
                getString(R.string.subtotal),
                getString(R.string.subtotal_alert_message),
                Constants.RequestId.SUBTOTAL
            )
        }

        binding.llTaxContainer.setOnClickListener {
            CustomDialog.show(
                this,
                getString(R.string.tax_alert_title),
                getString(R.string.tax),
                getString(R.string.tax_alert_message),
                Constants.RequestId.TAX
            )
        }

        binding.llMetadata1Container.setOnClickListener {
            CustomDialog.show(
                this,
                getString(R.string.metadata1_alert_title),
                getString(R.string.metadata_1),
                getString(R.string.metadata1_alert_message),
                Constants.RequestId.METADATA1
            )
        }
        binding.llMetadata2Container.setOnClickListener {
            CustomDialog.show(
                this,
                getString(R.string.metadata2_alert_title),
                getString(R.string.metadata_2),
                getString(R.string.metadata2_alert_message),
                Constants.RequestId.METADATA2
            )
        }

        binding.llPhoneNumberContainer.setOnClickListener {
            CustomDialog.show(
                this,
                getString(R.string.phoneNumber_alert_title),
                getString(R.string.phone_number),
                getString(R.string.phoneNumber_alert_message),
                Constants.RequestId.PHONE_NUMBER
            )
        }
    }

    override fun onDialogResponse(data: String, id: Constants.RequestId) {
        when (id) {
            Constants.RequestId.PUBLIC_TOKEN -> {
                binding.tvPublicToken.text = data
                Utils.setPrefsString(Constants.PUBLIC_TOKEN_PREF_KEY, data, this)
            }
            Constants.RequestId.TIMEOUT -> {
                if (data.isNotEmpty()) {
                    binding.tvTimeout.text = data
                    Utils.setPrefsInt(Constants.TIMEOUT_PREF_KEY, data.toInt(), this)
                } else {
                    Utils.setPrefsInt(Constants.TIMEOUT_PREF_KEY, 600, this)
                    binding.tvTimeout.text = ""
                }
            }
            Constants.RequestId.PAYMENT_AMOUNT -> {
                binding.tvPaymentAmount.text = TextUtils.concat("$", data)
                Utils.setPrefsString(Constants.PAYMENT_AMOUNT_PREF_KEY, data, this)
            }
            Constants.RequestId.SUBTOTAL -> {
                binding.tvSubtotal.text = TextUtils.concat("$", data)
                Utils.setPrefsString(Constants.SUBTOTAL_PREF_KEY, data, this)
            }
            else -> onDialogResponseDos(data, id)
        }
    }

    private fun updateFieldAndPref(field: TextView, prefKey: String, data: String) {
        field.text = data.ifEmpty { null }
        Utils.setPrefsString(prefKey, data.ifEmpty { null }, this)
    }

    private fun onDialogResponseDos(data: String, id: Constants.RequestId) {
        when (id) {
            Constants.RequestId.TAX -> {
                binding.tvTax.text = TextUtils.concat("$", data)
                Utils.setPrefsString(Constants.TAX_PREF_KEY, data, this)
            }
            Constants.RequestId.METADATA1 -> {
                updateFieldAndPref(binding.tvMetadata1, Constants.METADATA1_PREF_KEY, data)
            }
            Constants.RequestId.METADATA2 -> {
                updateFieldAndPref(binding.tvMetadata2, Constants.METADATA2_PREF_KEY, data)
            }
            Constants.RequestId.PHONE_NUMBER -> {
                updateFieldAndPref(binding.tvPhoneNumber, Constants.PHONE_NUMBER_PREF_KEY, data)
            }
            else -> {}
        }
    }

    private fun setUpButtonThemeSelection() {
        binding.llThemeContainer.setOnClickListener {
            val themeSelector = PopupMenu(this, binding.llThemeContainer)
            themeSelector.menuInflater.inflate(R.menu.theme_filter, themeSelector.menu)
            themeSelector.gravity = Gravity.END
            themeSelector.setOnMenuItemClickListener { item ->
                binding.tvTheme.text = item.title?.toString()
                Utils.setPrefsString(Constants.THEME_PREF_KEY, item.title.toString(), this)
                false
            }
            themeSelector.show()
        }
    }

    private fun setUpButtonLanguageSelection() {
        binding.llLanguageContainer.setOnClickListener {
            val languageSelector = PopupMenu(this, binding.llLanguageContainer)
            languageSelector.menuInflater.inflate(R.menu.language_filter, languageSelector.menu)
            languageSelector.gravity = Gravity.END
            languageSelector.setOnMenuItemClickListener { item ->
                val selectedLanguage = when (item.itemId) {
                    R.id.en -> PayButton.ButtonLanguage.EN
                    R.id.es -> PayButton.ButtonLanguage.ES
                    else -> PayButton.ButtonLanguage.DEFAULT
                }
                binding.tvLanguage.text = getLanguageLabel(selectedLanguage.name)
                Utils.setPrefsString(Constants.LANGUAGE_PREF_KEY, selectedLanguage.name, this)
                false
            }
            languageSelector.show()
        }
    }

    private fun setUpBuildTypeSelection() {
        binding.llBuildTypeContainer.setOnClickListener {
            val buildType = PopupMenu(this, binding.llBuildTypeContainer)
            buildType.menuInflater.inflate(R.menu.build_type_filter, buildType.menu)
            buildType.gravity = Gravity.END
            buildType.setOnMenuItemClickListener { item ->
                binding.tvBuildType.text = item.title?.toString()
                Utils.setPrefsString(Constants.BUILD_TYPE_PREF_KEY, item.title.toString(), this)
                false
            }
            buildType.show()
        }
    }

    private fun getLanguageLabel(language: String?): String {
        return when (language?.uppercase()) {
            PayButton.ButtonLanguage.EN.name -> getString(R.string.english)
            PayButton.ButtonLanguage.ES.name -> getString(R.string.spanish)
            else -> getString(R.string.default_language)
        }
    }
}
