# ATH Móvil Android SDK


## Introduction
The ATH Móvil SDK provides a simple, secure and fast checkout experience to customers paying on your Android application. After integrating our Payment Button on your app you will be able to receive instant payments from more than a million ATH Móvil users.


## Prerequisites
Before you begin, please review the following prerequisites:

* An active ATH Móvil Business account is required to continue. *To sign up, download "ATH Móvil Business" on the App Store if you have an iOS device or on the Play Store if you have an Android device.*

* Your ATH Móvil Business account needs to have a registered, verified and active ATH® card.

* Have the API key of your Business account at hand. You can view your API key on the settings section of the ATH Móvil Business application for iOS or Android.

If you need help signing up, adding a card or have any other question please refer to https://athmovilbusiness.com/preguntas or contact our support team at (787) 773-5466. For technical support please complete the following form:  https://forms.gle/ZSeL8DtxVNP2K2iDA.


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
	athmPayment.setBuildType("");
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
| `setItems()` | Array | No | Optional variable to display the items that the user is purchasing on ATH Móvil's payment screen. Items on the array are expected in the following order: (“name”, “desc”, "quantity", “price”, “metadata”) |
| `setBuildType()` | String | Yes | Identifies the application's build type. Should always be configured as an empty string. ||

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
![paymentux](paymentux.png)

## Legal
The use of this API and any related documentation is governed by and must be used in accordance with the Terms and Conditions of Use of ATH Móvi Business ®, which may be found at: https://athmovilbusiness.com/terminos.
