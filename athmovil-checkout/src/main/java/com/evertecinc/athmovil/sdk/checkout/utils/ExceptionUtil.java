package com.evertecinc.athmovil.sdk.checkout.utils;

import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;

import java.util.ArrayList;

public class ExceptionUtil {

    private String exceptionMessage;

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public boolean validateRequest(ATHMPayment request) {
        if (request.getPublicToken() == null || request.getPublicToken().trim().isEmpty()) {
            setExceptionMessage(ConstantUtil.NULL_PUBLICTOKEN_LOG_MESSAGE);
            return false;
        } else if (!validateItems(request.getItems()) ||
                !validateDataFields(exceptionMessage) ||
                !validateAmountFields(request.getSubtotal(), request.getTotal(), request.getTax())
                || !validateTokenSchema(request.getPublicToken(), request.getCallbackSchema())) {
            return false;
        }
        return true;
    }

    public boolean validateItems(ArrayList<Items> items) {
        if (items != null) {
            for (Items item : items) {
                if (!validateItemName(item.getName()) ||
                        !validateItemPrice(item.getPrice()) ||
                        !validateItemQuantity(item.getQuantity())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateItemName(String name) {
        if (name == null || name.trim().isEmpty() || name.equals("")) {
            setExceptionMessage(ConstantUtil.ITEM_NAME_ERROR_LOG_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean validateItemPrice(Double price) {
        if (price <= 0) {
            setExceptionMessage(ConstantUtil.ITEM_TOTAL_ERROR_LOG_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean validateItemQuantity(Long quantity) {
        if (quantity <= 0) {
            setExceptionMessage(ConstantUtil.ITEM_QUANTITY_ERROR_LOG_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean validateDataFields(String exceptionMessage) {
        if (exceptionMessage != null) {
            setExceptionMessage(ConstantUtil.PAYMENT_VALIDATION_FAILED);
            return false;
        } else {
            return true;
        }
    }

    public boolean validateAmountFields(double subTotal, double total, double tax) {
        if (subTotal < 0) {
            setExceptionMessage(ConstantUtil.SUBTOTAL_ERROR_LOG_MESSAGE);
            return false;
        } else if (total < 1) {
            setExceptionMessage(ConstantUtil.TOTAL_ERROR_LOG_MESSAGE);
            return false;
        } else if (tax < 0) {
            setExceptionMessage(ConstantUtil.TAX_NULL_LOG_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    public boolean validateTokenSchema(String token, String schema) {
        if (token == null || token.trim().isEmpty()) {
            setExceptionMessage(ConstantUtil.NULL_PUBLICTOKEN_LOG_MESSAGE);
            return false;
        } else if (schema == null || schema.trim().isEmpty()) {
            setExceptionMessage(ConstantUtil.SCHEMA_ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }
    }
}
