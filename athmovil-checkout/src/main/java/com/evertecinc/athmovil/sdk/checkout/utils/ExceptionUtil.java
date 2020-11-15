package com.evertecinc.athmovil.sdk.checkout.utils;

import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        } else if (request.getSubtotal() < 0) {
            setExceptionMessage(ConstantUtil.SUBTOTAL_ERROR_LOG_MESSAGE);
            return false;
        } else if (request.getTotal() < 1) {
            setExceptionMessage(ConstantUtil.TOTAL_ERROR_LOG_MESSAGE);
            return false;
        } else if (request.getMetadata1() != null && !request.getMetadata1().isEmpty() &&
                !request.getMetadata1().equals("") && validateMetadata(request.getMetadata1())) {
            setExceptionMessage(ConstantUtil.NULL_METADATA_LOG_MESSAGE);
            return false;
        } else if (request.getTax() < 0) {
            setExceptionMessage(ConstantUtil.TAX_NULL_LOG_MESSAGE);
            return false;
        } else if (request.getCallbackSchema() == null || request.getCallbackSchema().trim().isEmpty()) {
            setExceptionMessage(ConstantUtil.SCHEMA_ERROR_MESSAGE);
            return false;
        } else if (request.getMetadata2() != null && !request.getMetadata2().isEmpty() &&
                !request.getMetadata2().equals("") && validateMetadata(request.getMetadata2())) {
            setExceptionMessage(ConstantUtil.NULL_METADATA_LOG_MESSAGE);
            return false;
        } else if (request.getItems() != null) {
            for (Items item : request.getItems()) {
                if (item.getName() == null || item.getName().trim().isEmpty() || item.getName().equals("")) {
                    setExceptionMessage(ConstantUtil.ITEM_NAME_ERROR_LOG_MESSAGE);
                    return false;
                } else if (item.getPrice() <= 0) {
                    setExceptionMessage(ConstantUtil.ITEM_TOTAL_ERROR_LOG_MESSAGE);
                    return false;
                } else if (item.getQuantity() <= 0) {
                    setExceptionMessage(ConstantUtil.ITEM_QUANTITY_ERROR_LOG_MESSAGE);
                    return false;
                } else if (item.getMetadata() != null && !item.getMetadata().isEmpty()) {
                    if (item.getMetadata().trim().isEmpty() || validateMetadata(item.getMetadata())) {
                        setExceptionMessage(ConstantUtil.NULL_ITEM_METADATA_LOG_MESSAGE);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean validateMetadata(String metadata) {
        metadata = metadata.replaceAll("\\s", "");
        if(metadata.equals(""))
            return true;
        Pattern p = Pattern.compile("[^A-Za-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(metadata);
        return m.find();
    }
}
