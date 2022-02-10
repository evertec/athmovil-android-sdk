# ATH Móvil Android SDK


## Introduction
ATH Móvil's Payment Button SDK provides a simple, secure and fast checkout experience to customers paying on your Android application. After integrating our Payment Button on your app you will be able to receive real time payments from more than 1.5 million ATH Móvil users.


## Prerequisites
Before you begin, please review the following prerequisites:
1. An active ATH Móvil Business account is required to continue. To sign up, download "ATH Móvil Business" on the App Store or Play Store of your iOS or Android device.
2. Your ATH Móvil Business account needs to have a registered, verified and active ATH® card.
3. Have the public and private API keys of your Business account at hand. **You can view your API keys on the settings section of ATH Móvil Business for iOS or Android.**


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

* Add the Payment Button SDK dependency.
```java
dependencies {
    …
    implementation 'com.github.evertec:athmovil-android-sdk:4.0.0'
}
```

* Add the Payment Button SDK application dependencies.
```java
dependencies {
    …
    implementation 'androidx.annotation:annotation:1.2.0'
	implementation 'com.google.code.gson:gson:2.8.6'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.0'
}
```

* Install the Payment Button SDK as an .aar (Optional). 
Download the athmovil-checkout-release.aar file which is located in the libs folder. 
Copy it to a folder called libs in your project and add it as a dependency

```java
//Add everything that is in libs (optional)
repositories {
    flatDir {
        dirs 'libs'
    }
}
//Or add the direct dependency
dependencies {
    …
    releaseImplementation files('../libs/athmovil-checkout-release.aar')
}
```

## Usage
To integrate ATH Móvil’s Payment Button to your Android application follow these steps:

### XML
Add the “Pay with ATH Móvil” button to your XML view.
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
(**Note: If your app targets Android 11 (API Level 30) or higher, you must include the QUERY_ALL_PACKAGES permission❗️**)

```xml
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"/>
...
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

Configure the payment values and execution on the onClick of the XML button that we recently created. Details of the methods used to configure the payment details are provided below.
```java
public void onClickPayButton(View view) {
	athmPayment.setCallbackSchema("schema"); //Replace this value with the Callback Schema of your app.
	athmPayment.setPublicToken("fb1f7ae2849a07da1545a89d997d8a435a5f21ac"); //Replace this value with the Public Token of your ATH Móvil Business account.
	athmPayment.setTimeout(600);
	athmPayment.setTotal(1.00);
	athmPayment.setSubtotal(1.00);
	athmPayment.setTax(1.00);
	athmPayment.setMetadata1("metadata1 test");
	athmPayment.setMetadata2("metadata2 test");
	athmPayment.setItems(items);
	athmPayment.setBuildType("");
	OpenATHM.validateData(payment, context);
}
```

| Method  | Data Type | Required | Description |
| ------------- |:-------------:|:-----:| ------------- |
| `setPublicToken()` | String | Yes | Determines the Business account where the payment will be sent to. |
| `setCallbackSchema()` | String | Yes | Schema configuration name of manifest. 
| `setTimeout()` | Long | No | Expires the payment process if the payment hasn't been completed by the user after the provided amount of time (in seconds). Countdown starts immediately after the user presses the Payment Button. Default value is set to 600 seconds (10 mins). |
| `setTotal()` | Double | Yes | Total amount to be paid by the end user. |
| `setSubtotal()` | Double | No | Optional  variable to display the payment subtotal (if applicable) |
| `setTax()` | Double | No | Optional variable to display the payment tax (if applicable). |
| `setMetadata1()` | String | Yes | Required variable that can be left empty or filled with additional transaction information. Max length 40 characters. |
| `setMetadata2()` | String | Yes | Required variable that can be left empty or filled with additional transaction information. Max length 40 characters. |
| `setItems()` | Array | No | Optional variable to display the items that the user is purchasing on ATH Móvil's payment screen. Items on the array are expected in the following order: (“name”, “desc”, "quantity", “price”, “metadata”) |
| `setBuildType()` | String | Yes | Identifies the application's build type. `Should always be configured as an empty string.` ||

In the request make sure you comply with the following requirements for `ATHMPayment` object, otherwise you will receive an exception on the callback:

| Variable  | Expeted Value |
| ------------- |:-------------:|
| `total` | Positive value |
| `subtotal` | Positive value or zero |
| `tax` | Positive value or zero |
| `metadata1` | Spaces, letters and numbers, max length 40 |
| `metadata2` | Spaces, letters and numbers, max length 40 |
| `publicToken` | String |
| `callbackSchema` |String (**avoid using the callbackSchema provided in the example❗️**) |
| `timeout` | Integer between 60 and 600 |

If you provide items in the request make sure you comply with these requirements for the `ATHMPaymentItem` object:

| Variable  | Expected Value |
| ------------- |:-------------:|
| `name` | Spaces, letters and numbers |
| `price` | Positive value greater than zero |
| `description` | Spaces, letters and numbers |
| `quantity` | Positive value greater than zero |
| `metadata` | Spaces, letters and numbers |

Note the request and items are the same objects in the response so the values and types are identical in request and response, but the response includes the following additional variables:

| Variable  | Data Type | Description |
| ------------- |:-------------:|------------- |
| `dailyTransactionID` | Int | Consecutive of the transaction, when the transaction is cancelled o expired the value will be zero. |
| `referenceNumber` | String | Unique transaction identifier, when the transaction is cancelled o expired the value will be an empty string. |
| `date` | Date | Date of Transaction. |
| `name` | String | Name of customer. |
| `phoneNumber` | String | Phone number of customer. |
| `email` | String | Email of customer. |
| `fee` | Double | Fee paid in the transaction. |
| `netAmount` | Double | Total amount paid by the end user without the fee. |

If there is unexpected data in the request or response the SDK will call the closure `onPaymentException` and you will get a title and a message with information of the error. Your application must manage these error cases. For example:

```java
@Override
    public void onPaymentException(String error, String description) {
        //handle the error
    }
```
#### Validate the status of Pending Payments.
In some error cases payment responses may not be sent back to your application, for example when end users close the ATH Móvil application from the multitasking view of their device in the middle of the payment process.

To mitigate these cases you can implement a payment validator on the `onResume()` of your checkout view where the payment button logic was implemented (`OpenATHM.validateData (payment, context);`). This method verifies the status of the transaction if the payment process was interrupted. It can also be used anywhere you want to validate whether a payment was completed or cancelled. The method can take a maximum of 30 seconds to respond, so consider managing this wait time from a user experience perspective.

```java                
@Override
    protected void onResume() {
        super.onResume();
        // Manage a loader to wait for the response
        showLoader();

        // Call to validate transaction if payment process was interrupted
        OpenATHM.verifyPaymentStatus(this);
    }
```

#### Handle all payment responses.
When a transaction is completed, canceled or expired a response is sent back to the URL scheme that was configured on the payment. Implement the `PaymentResponseListener` on the activity of the configured scheme.
```java
public class Activity extends AppCompatActivity
implements PaymentResponseListener {}
```

On the onCreate method of the activity (configured in the manifest for the callback) call `PaymentResponse.validatePaymentResponse`

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PaymentResponse.validatePaymentResponse(getIntent(), this);
}
```

Handle the payment response using the following methods:
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

* Exception
```java
@Override
void onPaymentException(String error, String description){
		//Handle response
}
```

## Testing
To test your Payment Button integration you can make payments in production using the Private and Public tokens of your ATH Móvil Business account or you can use the public token "dummy" to make simulated payments. When you use the token "dummy":
* The ATH Movil production application will simulate a payment.
* No end user credentials need to be provided to interact with the simulated payment.
* Completed, cancelled and expired payments can be tested.

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
