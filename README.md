# ATH Móvil Android SDK


## Introduction
The ATH Móvil SDK provides a simple, secure and fast checkout experience to customers paying on your Android application. After integrating our Payment Button on your app you will be able to receive instant payments from more than a million ATH Móvil users.
Disclaimer: The Payment Button ATH Móvil is not compatible with any major Ecommerce platform. This includes Shopify, Wix, Woocommerce or Stripe.

Disclaimer: We currently **do not** have a **Testing environment**. You need to have an active ATH Business account and a active ATH Móvil account.

## Prerequisites
Before using the ATH Móvil’s payment you need to have:

### ATH Business

1\. An active ATH Business account.

2\. A card registered in your ATH Business profile. 

3\. The public and private key assigned to your business.

For instructions on how to open a ATH Business account please refer to: [ATHB flyer eng letter 1.pdf](https://github.com/user-attachments/files/16267504/ATHB.flyer.eng.letter.1.pdf)

For more information related to ATH Business and how it works please refer to:[ATH BUSINESS_Apr2024.pptx](https://github.com/user-attachments/files/16267585/ATH.BUSINESS_Apr2024.pptx)

### ATH Móvil

To complete the payment for testing purposes you need to have:

1\. An active ATH Móvil account.

2\. A card registered in your ATH Móvil profile. It can not be the same card that is registered in ATH Business.

For more information related to ATH Móvil and how it works please refer to:[ATH Móvil_Apr2024.pptx](https://github.com/user-attachments/files/16267592/ATH.Movil_Apr2024.pptx)

If you need help signing up, adding a card or have any other question please refer to https://athmovilbusiness.com/preguntas or contact our support team at (787) 773-5466. For technical support please complete the following form:  https://forms.gle/ZSeL8DtxVNP2K2iDA.

## Support
If you need help signing up, adding a card or have any other question please refer to https://ath.business.com/preguntas. For technical support please complete the following form:  https://ath.business/botondepago.

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
    implementation 'com.github.evertec:athmovil-android-sdk:3.0.0'
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
Configure the activity where the payment response will be sent to on your manifest. In order to open the ATH Móvil app on Android version 11 or higher, include the following querie objetc.

```java
<queries>
    <package android:name="com.evertec.athmovil.android" />
    
    <intent>
        <action android:name="android.intent.action.SEND" />
        <data android:mimeType="text/plain" />
    </intent>
</queries>
```
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
import com.evertecinc.athmovil.sdk.checkout.OpenATHM;
import com.evertecinc.athmovil.sdk.checkout.PayButton;
import com.evertecinc.athmovil.sdk.checkout.objects.ATHMPayment;
import com.evertecinc.athmovil.sdk.checkout.objects.Items;
```

Create an `ATHMPayment` object on the main class of the file.
```java
ATHMPayment athmPayment = new ATHMPayment(this);
```

Configure the payment values and execution on the onClick of the XML button that we recently created. *Details of the methods used to configure the payment details are provided below.*
```java
public void onClickPayButton(View view) {
    // CallbackSchema example, this should be changed with your application CallbackSchema.
	athmPayment.setCallbackSchema("scheme"); 
    // PublicToken example, this should be changed with your public token.
	athmPayment.setPublicToken("fb1f7ae2849a07da1545a89d997d8a435a5f21ac"); 
	athmPayment.setTimeout(600);
	athmPayment.setTotal(1.00);
	athmPayment.setSubtotal(1.00);
	athmPayment.setTax(1.00);
	athmPayment.setMetadata1("metadata1 test");
	athmPayment.setMetadata2("metadata2 test");
	athmPayment.setItems(items);
	athmPayment.setBuildType("");

    //In case the customer number exists, replace this value
    athmPayment.setPhoneNumber("4052955384");

    //If you want to activate the new payment flow, set to true
    athmPayment.setNewFlow(true);

	OpenATHM.validateData(payment, context);
}
```

| Method  | Data Type | Required | Description |
| ------------- |:-------------:|:-----:| ------------- |
| `setPublicToken()` | String | Yes | Determines the Business account where the payment will be sent to. |
| `setTimeout()` | Long | No | Expires the payment process if the payment hasn't been completed by the user after the provided amount of time (in seconds). Countdown starts immediately after the user presses the Payment Button. Default value is set to 600 seconds (10 mins). |
| `setTotal()` | Double | Yes | Total amount to be paid by the end user. |
| `setSubtotal()` | Double | No | Optional  variable to display the payment subtotal (if applicable) |
| `setTax()` | Double | No | Optional variable to display the payment tax (if applicable). |
| `setMetadata1()` | String | No | Optional variable to attach data to the payment object. |
| `setMetadata2()` | String | No | Optional variable to attach data to the payment object. |
| `setItems()` | Array | No | Optional variable to display the items that the user is purchasing on ATH Móvil's payment screen. Items on the array are expected in the following order: (“name”, “desc”, "quantity", “price”, “metadata”) |
| `setBuildType()` | String | Yes | Identifies the application's build type. `Should always be configured as an empty string.` |
| `setPhoneNumber()` | String | NO | Identify the customer's phone number. `Should always be configured as an empty string.` |
| `setNewFlow()` | Boolean | Yes | Identifies if the payment is made from the new flow. `By default it will have the value false.` ||

In the request you must make sure that you are following the next rules for the payment ATHMPayment object otherwise you will receive an exception on the callback

| Variable  | Expeted Value |
| ------------- |:-------------:|
| `total` | Positive value |
| `subtotal` | Positive value or zero |
| `tax` | Positive value or zero |
| `metadata1` | Only allows space, letters and numbers |
| `metadata2` | Only allows space, letters and numbers |
| `publicToken` | A string with characters |
| `callbackSchema` | A string with characters, `avoid to use the callbackSchema of the example` | 
| `timeout` | Integer between 60 and 600 | 

If you provide items in the request you must make sure that you are following the next rules for the ATHMPaymentItem object:

| Variable  | Expeted Value |
| ------------- |:-------------:|
| `name` | Only allows space, letters and numbers |
| `price` | Positive value greater than zero |
| `description` | Only allows space, letters and numbers |
| `quantity` | Positive value greater than zero |
| `metadata` | Only allows space, letters and numbers |

Note the request and items are the same objects in the response so the values and types are identical in request and response, but he response includes the following additional variables

| Variable  | Data Type | Description |
| ------------- |:-------------:|------------- |
| `dailyTransactionID` | Int | Consecutive of the transaction, when the transaction is cancelled o expired is zero. |
| `referenceNumber` | String | Unique transaction identifier, when the transaction is cancelled o expired is an empty string. |
| `date` | Date | Transaction's date. |
| `name` | String | ATHM Customer's name, no matter the status of the transaction it always has the name. |
| `phoneNumber` | String | ATHM Customer's phone, no matter the status of the transaction it always has telephone number in format (xxx) xxx-xxxx. |
| `email` | String | ATHM Customer's email, no matter the status of the transaction it always has email. |

If the data is unexpected in the response or request SDK will call the closure onPaymentException and you will get a title and a message referring to the error obtained. Then your application must handle the error depending on the case.

```java
@Override
    public void onPaymentException(String error, String description) {
        //handle the error
    }
```

Create the payment validator in the `onResume()` in the class where it was called `OpenATHM.validateData (payment, context);`. This method validates the transaction if the flow was interrupted (it can be used anywhere you want to validate whether a payment was successful or canceled). The method can take a maximum of 30 seconds until you get a response, so consider adding code to wait for it to finish.

```java
@Override
    protected void onResume() {
        super.onResume();
        // Manage a loader to await for response
        showLoader();

        // Call to validate the transaction if flow was broken
        OpenATHM.verifyPaymentStatus(this);

    }
```

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
public void onCompletedPayment(Date date, String referenceNumber, String dailyTransactionID,
                            String name, String phoneNumber, String email,
                            Double total, Double tax, Double subtotal,
                            String metadata1, String metadata2, ArrayList<Items> items) {
		//Handle response
}
```

* Cancelled
```java
@Override
public void onCancelledPayment(Date date, String referenceNumber, String dailyTransactionID,
                            String name, String phoneNumber, String email,
                            Double total, Double tax, Double subtotal,
                            String metadata1, String metadata2, ArrayList<Items> items) {
		//Handle response
}
```

* Expired
```java
@Override
public void onExpiredPayment(Date date, String referenceNumber, String dailyTransactionID,
                            String name, String phoneNumber, String email,
                            Double total, Double tax, Double subtotal,
                            String metadata1, String metadata2, ArrayList<Items> items) {
		//Handle response
}
```

* Failed
```java
@Override
public void onFailedPayment(Date date, String referenceNumber, String dailyTransactionID,
                            String name, String phoneNumber, String email,
                            Double total, Double tax, Double subtotal,
                            String metadata1, String metadata2, ArrayList<Items> items) {
        //Handle response
}
```

* Exception
```java
@Override
void onPaymentException(String error, String description){
		//Handle response
}
```

## Testing
* You could test the integration with ATH Movil Personal Application so you can use your public token or set the public as "dummy".
* Setting public token as "dummy"  ATH Movil Personal Application will simulate a payment, in that way you can test all the features such as
completed, expired or cancelled payments. You need the latest version of ATH Movil Personal Application.

```java
public void onClickPayButton(View view) {
    ...
	athmPayment.setPublicToken("dummy");
    ...
	OpenATHM.validateData(payment, context);
}
```

## User Experience
![paymentux](paymentux.png)

## Legal
The use of this API and any related documentation is governed by and must be used in accordance with the Terms and Conditions of Use of ATH Móvi Business ®, which may be found at: https://athmovilbusiness.com/terminos.
