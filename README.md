# ATH Móvil Android SDK


## Introduction
The ATH Móvil SDK provides a simple, secure and fast checkout experience to customers using your Android application. After integrating our checkout process on your app you will be able to receive instant payments from more than a million ATH Móvil users.


## Prerequisites
Before you begin, please review the following prerequisites:

* An active ATH Móvil Business account is required to continue. *To sign up, download the ATH Móvil Business application on your iOS or Android on your mobile device.*

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
* Add the SDK to your application dependencies.
```java
dependencies {
    …
    compile 'com.github.evertecinc:athmovil-android-sdk:1.0.0'
}
```

## Usage
To integrate ATH Móvil’s checkout process to your Android application follow these steps:

### XML
Add the “Pay with ATH Móvil” button to your checkout XML view.
```xml
<com.evertecinc.athmovil.sdk.checkout.PayButton
    android:onClick="onClickPayButton"
    android:layout_width="match_parent"
    android:layout_height="60dp"
    app:style="light"
    app:language="espanol"
    />
```
* `app:style` defines the theme of the button.

| Styles  | Example |
| ------------- |-------------|
| default | ![alt text](https://image.ibb.co/e7883o/Default.png) |
| `light` | ![alt text](https://image.ibb.co/jAOaio/Light.png) |
| `dark` | ![alt text](https://image.ibb.co/kSmvio/Dark.png) |

* `app:language` defines the language of the button.

| Languages  | Example |
| ------------- |-------------|
| default |Uses the device language. |
| `english` | ![alt text](https://image.ibb.co/e7883o/Default.png) |
| `espanol` | ![alt text](https://image.ibb.co/mLyVG8/Default.png) |

----

### Java
Add all required imports to the java file of your checkout screen.
```java
import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidBusinessTokenException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.InvalidPurchaseTotalAmountException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullApplicationContextException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullCartReferenceIdException;
import com.evertecinc.athmovil.sdk.checkout.exceptions.NullPurchaseDataObjectException;
import com.evertecinc.athmovil.sdk.checkout.objects.ItemsSelected;
import com.evertecinc.athmovil.sdk.checkout.OpenATHM;
import com.evertecinc.athmovil.sdk.checkout.objects.PurchaseData;
import com.evertecinc.athmovil.sdk.checkout.objects.PurchaseReturnedData;
import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.google.gson.Gson;
```

Create a purchaseData object on the main class of the file.
```java
PurchaseData purchaseData = new PurchaseData();
```

Configure the payment values and execution on the onClick of the XML button that we recently created. *Specification of methods to set the payment details are provided below.*
```java
public void onClickPayButton(View view) {
    purchaseData.setContext(ExampleActivity.this);
    purchaseData.setBusinessToken("fb1f7ae2849a07da1545a89d997d8a435a5f21ac");
    purchaseData.setCartReferenceId("1002345-325410");
    purchaseData.setTotal("1.00");
    purchaseData.setSubtotal("1.00");
    purchaseData.setTax("1.00");
    purchaseData.setTimer("300000");
    purchaseData.setItemsSelectedList(itemsSelectedList);
    sendData(purchaseData);
}
```

| Method  | Data Type | Required | Description |
| ------------- |:-------------:|:-----:| ------------- |
| `setContext()` | N/A | Yes | Always set to current activity. |
| `setBusinessToken()` | String | Yes | Required for security purposes. |
| `setCartReferenceId()` | String | Yes | Provide an ID to identify the transaction on your end. This reference ID is sent back in the transaction completion response and its never presented to the end user. |
| `setTotal()` | String | Yes | The amount provided on this method is the final amount to be paid by the end user. |
| `setSubtotal()` | String | No | Use this optional method to display the payment subtotal. |
| `setTax()` | String | No | Use this optional method to display the payment tax. |
| `setTimer()` | Long | No | Use this optional method to limit the amount of time (in milliseconds) that the user has to complete the payment. Timer starts the moment ATH Móvil's instance is displayed. The default value of this method is set to 600000 (10 mins). |
| `setItemsSelectedList()` | List | No | Use this optional method to display the items that the user is purchasing on ATH Móvil. Items on the array are expected in the following order: (“Name”, “Description”, “Price”, “Amount”) |
| `sendData()` | PurchaseData | Yes | Sends ATH Móvil the payment details. |

When a transaction is completed a response is sent back to the main intent of your application. You will need to extract the transaction result from the main intent extras and serialize the json result to a PurchaseReturnedData object using the gson library.
```java
if (getIntent().getExtras() != null) {
            String jsonResponseValue = getIntent().getExtras().getString("paymentResult");

            if (jsonResponseValue != null) {
                Gson gson = new Gson();
                PurchaseReturnedData result = gson.fromJson(jsonResponseValue, PurchaseReturnedData.class);
}
```

Once the transaction response is serialized you can extract individual details of the response as Strings using the following methods:
```java
status = result.getStatus();
referenceID = result.getCartReferenceId();
transactionID = result.getDailyTransactionId();
transaction reference = result.getTransactionReference();
```

| Method  | Possible Results |
| ------------- | ------------- |
| `getStatus()` | `Success` - The payment was processed successfully;  `TimeOut` - Default (10 mins) or provided timeout expired; `Canceled` - The user canceled the payment; `BusinessUnavailable` - There is an issue with the provided API key or account; `UserHaveNoActiveCards` - The user does not have an active card to pay with. |
| `getCartReferenceId()` | Reference ID that was provided when the payment was initiated. |
| `getDailyTransactionId()` | ATH Móvil Business daily transaction ID. |
| `getTransactionReference()` | ATH Móvil’s reference number. *Required to verify or refund a payment.* |

Handle all the possible payment responses.

## Testing
Set the value of  `purchaseData.setBusinessToken` to:
* `ConstantUtil.TOKEN_FOR_SUCCESS`: When the "Pay with ATH Móvil " button is pressed an ATH Móvil instance will open and close instantly and a response to the payment request with the status `Success` will be sent to your app.
* `ConstantUtil.TOKEN_FOR_FAILURE`: When the "Pay with ATH Móvil " button is pressed an ATH Móvil instance will open and close instantly and a response to the payment request with the status `Canceled` will be sent to your app.

## User Experience
![alt text](https://preview.ibb.co/dX2cOz/API_Flow.png)

## Legal
### API Terms of Service
You have the option of using the API code described herein, free of charge, which will allow you to integrate the ATH Móvil Business service (the “Service”) as a method of payment in your webpages or applications. In order to use the API, you must (1) be registered in the Service and (2) comply with the Service’s terms and conditions of use and with the API documentation (as made available to you herein). You hereby acknowledge that any use, reproduction or distribution of the API or API documentation, or any derivatives or portions thereof, constitutes your acceptance of these terms and conditions, including all other sections within the API documentation. The API documentation may not be modified. No title to the intellectual property in the API or API documentation is transferred to you under these terms and conditions. Your use of the API or the API documentation, whether through a developer or otherwise, is made with the understanding that neither your financial institution nor Evertec will provide you with any technical support, customer support or maintenance in relation to the use of the API. You may discontinue the use of the API at any time. In the event that you are assisted by a developer, you understand and acknowledge that you will be solely responsible for your developer’s compliance with the terms and conditions of the Service, these terms and conditions, and the rest of the API documentation.

### Disclaimer of Warranty
You hereby understand and acknowledge that the API is provided “AS IS” and that your use of the API is completely voluntary and at your own risk. Both Evertec and your financial institution disclaim all warranties and make no representations of any kind, whether express or implied, as to (1) the merchantability or fitness of the API for any particular purpose; (2) the APIs performance or availability; (3) the APIs condition of titled; or (4) that the APIs use or process derived or produced therefrom will not infringe any patent, copyright or other third parties. You agree that in no event shall Evertec or your financial institution be liable for any direct, indirect, special, consequential or accidental damages or loss (including, but not limited to, loss of anticipated profits or data, or other commercial damage), however arising, or any kind with relation to the API and its use or inability to use with the Service.
