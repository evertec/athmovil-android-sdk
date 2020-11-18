# ATH Móvil Payment Button - Android SDK


## Introduction
ATH Móvil's Payment Button SDK provides a simple, secure and fast checkout experience to customers paying on your Android application. After integrating our Payment Button on your app you will be able to receive real time payments from more than 1.5 million ATH Móvil users.


## Prerequisites
Before you begin, please review the following prerequisites:
1. An active ATH Móvil Business account is required to continue. To sign up, download "ATH Móvil Business" on the App Store or Play Store of your iOS or Android device.
2. Your ATH Móvil Business account needs to have a registered, verified and active ATH® card.
3. Have the public and private API keys of your Business account at hand. **You can view your API keys on the settings section of ATH Móvil Business for iOS or Android.**


## Support
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

Configure the payment values and execution on the onClick of the XML button that we recently created. Details of the methods used to configure the payment details are provided below.
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
| `setMetadata1()` | String | No | Optional variable to attach data to the payment object. |
| `setMetadata2()` | String | No | Optional variable to attach data to the payment object. |
| `setItems()` | Array | No | Optional variable to display the items that the user is purchasing on ATH Móvil's payment screen. Items on the array are expected in the following order: (“name”, “desc”, "quantity", “price”, “metadata”) |
| `setBuildType()` | String | Yes | Identifies the application's build type. Should always be configured as an empty string. ||

* In the request make sure you comply with the following requirements for the `ATHMPayment` object, otherwise you will receive an exception on the callback:
| Variable  | Expeted Value |
| ------------- |:-------------:|
| `total` | Positive value |
| `subtotal` | Positive value or zero |
| `tax` | Positive value or zero |
| `metadata1` | A string with characters, digits or spaces |
| `metadata2` | A string with characters, digits or spaces |
| `token` | A string with characters |
| `urlScheme` | A string with characters. **Do not use the urlscheme in the example❗️** |
| `timeout` | Integer between 60 and 600 |

* If you provide items in the request make sure you comply with these requirements for the `ATHMPaymentItem` object:
| Variable  | Expeted Value |
| ------------- |:-------------:|
| `name` | A string with characters |
| `price` | Positive value greater than zero |
| `quantity` | Positive value greater than zero |
| `metadata` | A string with characters, digits or spaces |

* *Note: the payment information and items on the response are the same objects that were received in the request. Values and data types are identical. The response includes the following additional variables:*
| Variable  | Data Type | Description |
| ------------- |:-------------:|------------- |
| `dailyTransactionID` | Int | Daily ID of the transaction. If the transaction was cancelled o expired value will be 0. |
| `referenceNumber` | String | Unique transaction identifier. If the transaction was cancelled o expired value will be 0. |
| `date` | Date | Transaction's date. |
| `name` | String | Name registered on ATH Móvil of user that paid for the transaction. |
| `phoneNumber` | String | Phone number registered on ATH Móvil of user that paid for the transaction. |
| `email` | String | 	Email address registered on ATH Móvil of user that paid for the transaction. |

The SDK validates the information that is sent and received on all requests and responses.

* If unexpected data is sent on the request of the payment the SDK will throw an exception. Your application must be able to handle these exceptions.

  * `NullATHMPaymentObjectException`: error returned when the `ATHMPayment` object is null.
  * `NullApplicationContextException`: error returned when the initial application context is null.
  * `InvalidPaymentRequestException`: error returned when request variables are invalid.
  * `JsonEncoderException`: error returned when JSON encoding the request data fails.

  ```java
  try {
      OpenATHM.validateData(ATHMPayment);
  } catch (Exception e) {
      //Handle error
  }
  ```

* If unexpected data is sent on the response of the payment the SDK will call the closure `onPaymentException` and you will get a title and a message that describes the error. Your application must be able to handle these errors.
```java
@Override
    public void onPaymentException(String error, String description) {
        //Handle error
    }
```

#### Handle all payment responses.

When a transaction is completed, canceled or expired a response is sent back to the URL scheme that was configured on the payment object. Implement the `PaymentResponseListener` on the activity of the configured scheme.
```java
public class Activity extends AppCompatActivity
implements PaymentResponseListener, View.OnClickListener {
}
```

On the `OnCreate` method of the activity call `PaymentResponse.validatePaymentResponse`

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

* Exception
```java
@Override
void onPaymentException(String error, String description){
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
