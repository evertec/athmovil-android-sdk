# ATH Móvil Android SDK


## Introduction
The ATH Móvil SDK provides a simple, secure and fast checkout experience to customers paying on your Android application. After integrating our Payment Button on your app you will be able to receive instant payments from more than a million ATH Móvil users.


## Prerequisites
Before you begin, please review the following prerequisites:

* An active ATH Móvil Business account is required to continue. *To sign up, download "ATH Móvil Business" on the App Store if you have an iOS device or on the Play Store if you have an Android device.*

* Your ATH Móvil Business account needs to have a registered, verified and active ATH® card.

* Have the API key of your Business account at hand. You can view your API key on the settings section of the ATH Móvil Business application for iOS or Android.

If you need help signing up, adding a card or have any other question please refer to https://athmovilbusiness.com/preguntas or contact our support team at (787) 773-5466.


## Installation
Before we get started, let’s configure your project:

* Add the JitPack repository to your gradle.build file.
```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
* Add the SDK and the GSON library to your application dependencies.
```java
dependencies {
    …
    implementation 'com.github.evertec:athmovil-android-sdk:2.0.0'
	implementation 'com.google.code.gson:gson:2.8.2'
}
```

## Usage
To integrate ATH Móvil’s Payment Button to your Android application follow these steps:

### XML
Add the “Pay with ATH Móvil” button to your checkout XML view.
```xml
<com.evertecinc.athmovil.sdk.checkout.PayButton
    android:onClick="onClickPayButton"
    android:layout_width="match_parent"
    android:layout_height="60dp"
    app:theme="light"
    app:lang="en"
    />
```
* `app:theme` defines the theme of the button.

| Styles  | Example |
| ------------- |-------------|
| default | ![alt text](https://image.ibb.co/e7883o/Default.png) |
| `light` | ![alt text](https://image.ibb.co/jAOaio/Light.png) |
| `dark` | ![alt text](https://image.ibb.co/kSmvio/Dark.png) |

* `app:lang` defines the language of the button.

| Languages  | Example |
| ------------- |-------------|
| default |Uses the device language. |
| `english` | ![alt text](https://image.ibb.co/e7883o/Default.png) |
| `espanol` | ![alt text](https://image.ibb.co/mLyVG8/Default.png) |

----
### Manifest
Configure the activity where the payment response will be sent to on your manifest.

```java
<activity
    android:name=".Activity">
    <intent-filter>
        <!--Schema with app bundle Configuration-->
        <action android:name="appbundle.schema" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

----
### Java
#### Configure the payment.
Add all required imports to the java file of your checkout screen.
```java
import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidBusinessTokenException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidPurchaseTotalAmountException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullApplicationContextException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullCartReferenceIdException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullPurchaseDataObjectException;
import com.evertecinc.athmovil.sdk.checkout.objects.ItemsSelected;
import com.evertecinc.athmovil.sdk.checkout.OpenATHM;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.google.gson.Gson;
```

Create an `ATHMPayment` object on the main class of the file.
```java
ATHMPayment athmPayment = new ATHMPayment(this);
```

Configure the payment values and execution on the onClick of the XML button that we recently created. *Details of the methods used to configure the payment details are provided below.*
```java
public void onClickPayButton(View view) {
	athmPayment.setCallbackSchema("scheme");
	athmPayment.setPublicToken("fb1f7ae2849a07da1545a89d997d8a435a5f21ac");
	athmPayment.setTimeout(600);
    athmPayment.setTotal(1.00);
    athmPayment.setSubtotal(1.00);
	athmPayment.setTax(1.00);
	athmPayment.setMetadata1("metadata1 test");
	athmPayment.setMetadata2("metadata2 test");
    athmPayment.setItems(items);
    sendData(purchaseData);
}
```

| Method  | Data Type | Required | Description |
| ------------- |:-------------:|:-----:| ------------- |
| `setPublicToken()` | String | Yes | Determines the Business account where the payment will be sent to. |
| `setTimeout()` | Long | No | Expires the payment process if the payment hasn't been completed by the user after the provided amount of time (in seconds). Countdown starts immediately after the user presses the Payment Button. Default value is set to 600 seconds (10 mins). |
| `setTotal()` | Double | Yes | Total amount to be paid by the end user. |
| `setSubtotal()` | Double | No | Optional  variable to display the payment subtotal (if applicable) |
| `setTax()` | Double | No | Optional variable to display the payment tax (if applicable). |
| `setMetadata1()` | String | No | Optional variable to attach key-value data to the payment object. |
| `setMetadata2()` | String | No | Optional variable to attach key-value data to the payment object. |
| `setItems()` | Array | No | Optional variable to display the items that the user is purchasing on ATH Móvil's payment screen. Items on the array are expected in the following order: (“name”, “desc”, "quantity", “price”, “metadata”) ||

#### Handle all payment responses.

When a transaction is completed, canceled or expired a response is sent back to the URL scheme that was configured on the payment.

Implement the PaymentResponseListener on the activity of the configured scheme.
```java
public class Activity extends AppCompatActivity
implements PaymentResponseListener, View.OnClickListener {
}
```

On the OnCreate method of the activity call `PaymentResponse.validatePaymentResponse`

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PaymentResponse.validatePaymentResponse(getIntent(), this);
}
```

Handle the payment response with using the following methods:
* Completed
```java
@Override
public void onCompletedPayment(String referenceNumber, Double total, Double tax, Double subtotal,
                               String metadata1, String metadata2, ArrayList<Items> items) {
		//Handle response
}
```

* Cancelled
```java
@Override
public void onCancelledPayment(String referenceNumber, Double total, Double tax, Double subtotal,
                               String metadata1, String metadata2, ArrayList<Items> items) {
		 //Handle response
}
```

* Expired
```java
@Override
public void onExpiredPayment(String referenceNumber, Double total, Double tax, Double subtotal,
                             String metadata1, String metadata2, ArrayList<Items> items) {
		 //Handle response
}
```

## Testing
* To test a "Completed" payment response set the value of  `ATHMPayment.setPublicToken` to:`ConstantUtil.TOKEN_FOR_SUCCESS`. When the "Pay with ATH Móvil " button is pressed an ATH Móvil instance will open and close instantly and a "Completed" payment response will be automatically sent to your app.
* To test a "Cancelled" payment response set the value of  `ATHMPayment.setPublicToken` to:`ConstantUtil.TOKEN_FOR_FAILURE`. When the "Pay with ATH Móvil " button is pressed an ATH Móvil instance will open and close instantly and a "Cancelled" payment response will be automatically sent to your app.
* To test an "Expired" payment response:
	* Set the value of `ATHMPayment.setPublicToken` to your public token.
	* Open the payment process.
	* Wait for the payment to expire

## User Experience
![alt text](https://preview.ibb.co/dX2cOz/API_Flow.png)

## Legal
### API Terms of Service
You have the option of using the API code described herein, free of charge, which will allow you to integrate the ATH Móvil Business service (the “Service”) as a method of payment in your webpages or applications. In order to use the API, you must (1) be registered in the Service and (2) comply with the Service’s terms and conditions of use and with the API documentation (as made available to you herein). You hereby acknowledge that any use, reproduction or distribution of the API or API documentation, or any derivatives or portions thereof, constitutes your acceptance of these terms and conditions, including all other sections within the API documentation. The API documentation may not be modified. No title to the intellectual property in the API or API documentation is transferred to you under these terms and conditions. Your use of the API or the API documentation, whether through a developer or otherwise, is made with the understanding that neither your financial institution nor Evertec will provide you with any technical support, customer support or maintenance in relation to the use of the API. You may discontinue the use of the API at any time. In the event that you are assisted by a developer, you understand and acknowledge that you will be solely responsible for your developer’s compliance with the terms and conditions of the Service, these terms and conditions, and the rest of the API documentation.

### Disclaimer of Warranty
You hereby understand and acknowledge that the API is provided “AS IS” and that your use of the API is completely voluntary and at your own risk. Both Evertec and your financial institution disclaim all warranties and make no representations of any kind, whether express or implied, as to (1) the merchantability or fitness of the API for any particular purpose; (2) the APIs performance or availability; (3) the APIs condition of titled; or (4) that the APIs use or process derived or produced therefrom will not infringe any patent, copyright or other third parties. You agree that in no event shall Evertec or your financial institution be liable for any direct, indirect, special, consequential or accidental damages or loss (including, but not limited to, loss of anticipated profits or data, or other commercial damage), however arising, or any kind with relation to the API and its use or inability to use with the Service.
