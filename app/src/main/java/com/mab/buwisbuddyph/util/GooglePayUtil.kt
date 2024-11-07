package com.mab.buwisbuddyph.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import org.json.JSONArray
import org.json.JSONObject

object GooglePayUtil {

    private const val TAG = "GooglePayUtil"
    private const val GOOGLE_PAY_API_VERSION = 2
    private const val GOOGLE_PAY_API_VERSION_MINOR = 0

    private fun createIsReadyToPayRequest(): IsReadyToPayRequest {
        return IsReadyToPayRequest.fromJson(getBaseRequest().apply {
            put("allowedPaymentMethods", getAllowedPaymentMethods())
        }.toString())
    }

    private fun getBaseRequest(): JSONObject {
        return JSONObject().apply {
            put("apiVersion", GOOGLE_PAY_API_VERSION)
            put("apiVersionMinor", GOOGLE_PAY_API_VERSION_MINOR)
        }
    }

    fun createPaymentDataRequest(price: String): PaymentDataRequest {
        val paymentDataRequestJson = getBaseRequest().apply {
            put("allowedPaymentMethods", getAllowedPaymentMethods())
            put("transactionInfo", getTransactionInfo(price))
            put("merchantInfo", getMerchantInfo())
        }
        return PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
    }

    private fun getAllowedPaymentMethods(): JSONArray {
        val cardPaymentMethod = JSONObject().apply {
            put("type", "CARD")
            put("parameters", JSONObject().apply {
                put("allowedAuthMethods", JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS")))
                put("allowedCardNetworks", JSONArray(listOf("AMEX", "DISCOVER", "JCB", "MASTERCARD", "VISA")))
            })
            put("tokenizationSpecification", getTokenizationSpecification())
        }
        return JSONArray().put(cardPaymentMethod)
    }

    private fun getTokenizationSpecification(): JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put("parameters", JSONObject().apply {
                put("gateway", "acpay")
                put("gatewayMerchantId", "BCR2DN4TS7JZJLD2")
            })
        }
    }

    private fun getTransactionInfo(price: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("currencyCode", "USD")
        }
    }

    private fun getMerchantInfo(): JSONObject {
        return JSONObject().apply {
            put("merchantName", "BuwisBuddyPH")
        }
    }

    fun isReadyToPay(context: Context, callback: (Boolean) -> Unit) {
        val paymentsClient = Wallet.getPaymentsClient(context,
            Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build())

        val isReadyToPayRequest = createIsReadyToPayRequest()
        val task = paymentsClient.isReadyToPay(isReadyToPayRequest)

        task.addOnCompleteListener { completedTask ->
            try {
                val result = completedTask.getResult(ApiException::class.java)
                callback(result)
            } catch (exception: ApiException) {
                Log.e(TAG, "isReadyToPay failed: ${exception.statusCode} ${exception.message}")
                handleGooglePayError(exception)
                callback(false)
            }
        }
    }

    fun requestPayment(activity: Activity, price: String, requestCode: Int) {
        val paymentsClient = Wallet.getPaymentsClient(activity,
            Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build())

        val paymentDataRequest = createPaymentDataRequest(price)
        val futurePaymentData = paymentsClient.loadPaymentData(paymentDataRequest)

        AutoResolveHelper.resolveTask(futurePaymentData, activity, requestCode)
    }

    private fun handleGooglePayError(exception: ApiException) {
        when (exception.statusCode) {
            WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE -> {
                Log.e(TAG, "Google Pay is unavailable. Please check your internet connection or try again later.")
            }
            WalletConstants.ERROR_CODE_AUTHENTICATION_FAILURE -> {
                Log.e(TAG, "Authentication failed. Please check your Google account and try again.")
            }
            WalletConstants.ERROR_CODE_UNSUPPORTED_API_VERSION -> {
                Log.e(TAG, "Unsupported API version. Please update your Google Play services.")
            }
            WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR -> {
                Log.e(TAG, "Google Pay account error. Please check your account settings.")
            }
            WalletConstants.ERROR_CODE_DEVELOPER_ERROR -> {
                Log.e(TAG, "Developer error. Please check your configuration.")
            }
            else -> {
                Log.e(TAG, "Unknown error occurred: ${exception.statusCode} ${exception.message}. Please try again.")
            }
        }
    }
}
