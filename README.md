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
### Terms for the use of the API
1. General. You have the option of using the API code described herein, free of charge, which will allow you to integrate the ATH Móvil®  as a method of payment in your business’ webpages or applications through the ATH Móvil Business® service (the “Service”) provided by Evertec Group, LLC (“Evertec,” “we,” or “us”). You can also find the API code and related API documentation (the “Licensed Materials”) free of charge in www.athmovilbusiness.com under the “Botón de Pago” section. For purposes of these terms and conditions, references to “you” or “your” refer to you as a user of the service who operates a business and wishes to receive payments for goods and services through the Service.
To use the API, you must:
(1) be registered in the Service,
(2) comply with these terms and conditions of use, and 
(3) comply with the API documentation. 
By accessing and using the Licensed Materials, you are agreeing to these terms and all sections within the API documentation, which shall also form a part of these terms and conditions of use. You will only access (or attempt to access) the API by the means described in the API documentation, which may not be modified by you or a third party on your behalf. No title to the intellectual property in the Licensed Materials is transferred to you hereunder. In the event that you are assisted by a developer, you will be solely responsible for your developer’s compliance with these terms and conditions of use and the API documentation. You may discontinue the use of the API at any time. We reserve the right to terminate these terms and conditions of use with you or discontinue the API or any portion or feature or your access to the Licensed Materials at any time by without liability or other obligation to you if we reasonably believe that you are in violation of these terms and conditions of use. We will notify upon any such termination by Evertec by sending you an email to your email address registered in the Service. Upon any termination of these terms and conditions or discontinuation of your access to the Licensed Materials, you will immediately stop using the API. Your use of the Licensed Materials, whether through a developer or otherwise, is made with the understanding that neither your financial institution or credit union (altogether from now on, “Financial Institution”) will provide you with any technical, customer support or maintenance support in relation to the use of the API. For technical support in the integration of the API in your business’ webpages or applications, you may call us at (787) 773-5466.

2. License Grant and Restrictions. 
•	We grant you a non-exclusive, non-transferable, non-sublicensable, worldwide, revocable right and license to use the Licensed Materials only for commercial purposes in accordance with these terms and conditions, to integrate ATH Móvil as a method of payment in your business’ webpages or applications for the duration of these terms and conditions of use.
•	We can set and enforce limits on your use of the API in our sole discretion. You agree to, and will not attempt to circumvent, such limitations (if any), whether documented or not, in the API documentation. 
•	You will not make any statement or representation regarding your use of the API which suggests partnership, teaming, sponsorship by, or endorsement by us or your Financial Institution.
•	You may not (and will not allow those acting on your behalf to): sublicense the API for use by a third party; create an API that functions substantially the same as the API provided hereunder and offer it for use by third parties; interfere with or disrupt the API; reverse engineer, decompile, disassemble or attempt to extract the source code from the API; use the Licensed Materials in any manner that could potentially undermine the security of the Licensed Materials; or use the Licensed Materials to carry out, encourage or promote any illegal activity.
•	Any derivative works created or invented by you or your developer based on the licensed materials hereunder shall remain the sole property of Evertec.

3. Obligations. 
•	You agree to comply with any and all applicable laws and regulations.
•	You agree to comply with Payment Card Industry Data Security Standards (PCI DSS).
•	You agree that Evertec may monitor use of the API to ensure quality, improve its services, and verify your compliance with these terms and conditions of use and the Licensed Materials.
•	You will use commercially reasonable efforts to protect user information collected by your website, including personally identifiable information, from unauthorized access or use, and will be responsible for reporting to your users any unauthorized access or use of such information to the extent required by applicable law. 
•	By using the API, Evertec may use submitted information in accordance with its privacy policy as published in www.athmovilbusiness.com.
•	If you provide feedback or suggestions about the API, then Evertec may use such information without obligation to you.

4. Disclaimer of Warranty, Limitation of Liability and Indemnity. Except as otherwise stated in these terms and conditions of use, neither Evertec nor your Financial Institution make any specific promises about the API. We provide the API “AS IS.” Your use of the API is completely voluntary and at your own risk. Evertec and your Financial Institution disclaim all warranties and make no representations of any kind, whether express or implied, as to (1) the merchantability or fitness of the API for any particular purpose; (2) the API’s performance or availability; (3) the API’s condition of title; or (4) that the API’s use or process derived or produced therefrom will not infringe any patent, copyright or other third parties intellectual property rights. You agree that in no event shall Evertec or your Financial Institution be liable for any direct, indirect, special, consequential or accidental damages or loss (including, but not limited to, loss of anticipated profits or data, or other commercial damage), of any kind, with relation to the API and its use or inability to use with the Service. Furthermore, you agree to defend and indemnify us and your Financial Institution and each of their respective directors, employees and representatives against all liabilities, damages, losses, costs, fees and expenses relating to any allegation or third-party legal proceeding to the extent arising from: (a) your use of the API, (b) your violation of these terms and conditions of use (including the Licensed Materials), (c) your violation of any and all applicable laws and regulations, (d) your failure to comply PCI DSS, (e) your infringement or other violation of a third-party’s or Evertec’s intellectual property rights, or (f) any content or data routed into or used with the API by you or those acting on your behalf (including your developer, if any).

### Use of pATH
By using the Service, you allow Evertec to use the pATH and name of your business to advertise or promote the Service or offer information regarding the Service in Evertec’s website and social media webpages. If at any time you decide that you don’t want Evertec to use the pATH and/or name of your business for this purpose, you can opt out by writing an email to marketing@evertecinc.com requesting that your pATH and/or name no longer be used for this purpose.
