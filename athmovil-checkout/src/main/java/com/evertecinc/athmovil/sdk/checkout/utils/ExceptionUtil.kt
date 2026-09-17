package com.evertecinc.athmovil.sdk.checkout.utils

import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment
import com.evertecinc.athmovil.sdk.checkout.objects.Items

class ExceptionUtil {

    companion object {
        private val callbackSchemaRegex = Regex(ConstantUtil.Validation.CALLBACK_SCHEMA_REGEX)
    }

    var exceptionMessage: String? = null
        private set

    fun setExceptionMessage(exceptionMessage: String?) {
        this.exceptionMessage = exceptionMessage
    }

    fun validateRequest(request: ATHMPayment): Boolean {
        setExceptionMessage(null)
        val publicToken = request.publicToken
        return if (publicToken.isNullOrBlank()) {
            setExceptionMessage(ConstantUtil.ExceptionsLogs.NULL_PUBLICTOKEN_LOG_MESSAGE)
            false
        } else {
            validateItems(request.items) &&
                validateAmountFields(request.subtotal, request.total, request.tax) &&
                validateTokenSchema(request.publicToken, request.callbackSchema)
        }
    }

    fun validateItems(items: ArrayList<Items>?): Boolean {
        items?.forEach { item ->
            if (!validateItemName(item.name) || !validateItemPrice(item.price) || !validateItemQuantity(item.quantity)) {
                return false
            }
        }
        return true
    }

    fun validateItemName(name: String?): Boolean {
        return if (name.isNullOrBlank()) {
            setExceptionMessage(ConstantUtil.ExceptionsLogs.ITEM_NAME_ERROR_LOG_MESSAGE)
            false
        } else {
            true
        }
    }

    fun validateItemPrice(price: Double?): Boolean {
        return if (price == null || price <= 0) {
            setExceptionMessage(ConstantUtil.ExceptionsLogs.ITEM_TOTAL_ERROR_LOG_MESSAGE)
            false
        } else {
            true
        }
    }

    fun validateItemQuantity(quantity: Long?): Boolean {
        return if (quantity == null || quantity <= 0) {
            setExceptionMessage(ConstantUtil.ExceptionsLogs.ITEM_QUANTITY_ERROR_LOG_MESSAGE)
            false
        } else {
            true
        }
    }

    fun validateAmountFields(subTotal: Double, total: Double, tax: Double): Boolean {
        return when {
            subTotal < 0 -> {
                setExceptionMessage(ConstantUtil.ExceptionsLogs.SUBTOTAL_ERROR_LOG_MESSAGE)
                false
            }
            total < 1 -> {
                setExceptionMessage(ConstantUtil.ExceptionsLogs.TOTAL_ERROR_LOG_MESSAGE)
                false
            }
            tax < 0 -> {
                setExceptionMessage(ConstantUtil.ExceptionsLogs.TAX_NULL_LOG_MESSAGE)
                false
            }
            else -> true
        }
    }

    fun validateTokenSchema(token: String?, schema: String?): Boolean {
        return when {
            token.isNullOrBlank() -> {
                setExceptionMessage(ConstantUtil.ExceptionsLogs.NULL_PUBLICTOKEN_LOG_MESSAGE)
                false
            }
            schema.isNullOrBlank() -> {
                setExceptionMessage(ConstantUtil.ExceptionsLogs.SCHEMA_ERROR_MESSAGE)
                false
            }
            !schema.matches(callbackSchemaRegex) -> {
                setExceptionMessage(ConstantUtil.ExceptionsLogs.SCHEMA_ERROR_MESSAGE)
                false
            }
            else -> true
        }
    }
}


