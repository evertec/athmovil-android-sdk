package com.evertecinc.athmovil.sdk.checkout.utils;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    @VisibleForTesting
    public static Date getDateFormat(String newDate) {
        Date date = new Date();
        if (newDate != null && !newDate.equals("")) {
            DateFormat dateFormat = new SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            try {
                date = dateFormat.parse(newDate);
            } catch (ParseException e) {
                Log.e("Error", "Parse Exception");
            }
        }
        return date;
    }

    public static ATHMPayment trimData(ATHMPayment payment) {
        payment.setMetadata1(payment.getMetadata1().trim());
        payment.setMetadata2(payment.getMetadata2().trim());
        for (int i = 0; i < payment.getItems().size(); i++) {
            if (payment.getItems().get(i).getMetadata() != null) {
                payment.getItems().get(i).setMetadata(payment.getItems().get(i).getMetadata().trim());
            }
            if (payment.getItems().get(i).getName() != null) {
                payment.getItems().get(i).setName(payment.getItems().get(i).getName().trim());
            }
            if (payment.getItems().get(i).getDesc() != null) {
                payment.getItems().get(i).setDesc(payment.getItems().get(i).getDesc().trim());
            }
        }
        return payment;
    }
}
