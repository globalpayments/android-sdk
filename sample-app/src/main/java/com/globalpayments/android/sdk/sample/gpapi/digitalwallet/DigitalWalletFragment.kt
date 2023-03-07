package com.globalpayments.android.sdk.sample.gpapi.digitalwallet

import android.app.ProgressDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.error.TransactionErrorDialog
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessDialog
import com.globalpayments.android.sdk.ui.clicktopay.ClickToPayFragment
import com.globalpayments.android.sdk.ui.clicktopay.ClickToPayModel
import com.globalpayments.android.sdk.utils.bindView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.PaymentData
import org.json.JSONException
import org.json.JSONObject

class DigitalWalletFragment : BaseFragment() {

    private lateinit var googlePayButton: View
    private val viewModel: DigitalWalletViewModel by viewModels()

    private val customToolbar: CustomToolbar by bindView(R.id.toolbar)
    private val clickToPay: Button by bindView(R.id.btn_click_to_pay)

    private val etPrice by bindView<EditText>(R.id.priceEditText)
    private val progressBar by lazy { ProgressDialog(requireContext()).apply { setTitle("Payment in progress") } }

    // Handle potential conflict from calling loadPaymentData
    private val resolvePaymentForResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            AppCompatActivity.RESULT_OK -> result.data?.let { intent ->
                PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
            }

            AppCompatActivity.RESULT_CANCELED -> {
                // The user cancelled the payment attempt
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_digital_wallet

    override fun initViews() {
        googlePayButton = requireView().findViewById(R.id.googlePayButton)
        customToolbar.setTitle(R.string.digital_wallet)
        customToolbar.setOnBackButtonListener { close() }
        clickToPay.setOnClickListener {
            viewModel.getAccessToken()
        }
    }

    override fun initSubscriptions() {

        viewModel.progressStatus.observe(viewLifecycleOwner) { if (it) progressBar.show() else progressBar.dismiss() }
        viewModel.canUseGooglePay.observe(viewLifecycleOwner, Observer(::setGooglePayAvailable))
        googlePayButton.setOnClickListener { requestPayment() }

        viewModel.transactionSuccessModel.observe(viewLifecycleOwner) {
            TransactionSuccessDialog.newInstance(it).show(childFragmentManager, TransactionSuccessDialog.TAG)
        }
        viewModel.paymentError.observe(viewLifecycleOwner) {
            TransactionErrorDialog.newInstance(it).show(childFragmentManager, TransactionErrorDialog.TAG)
        }
        viewModel.accessToken.observe(viewLifecycleOwner) { token ->
            showClickToPay(token)
        }
    }

    private fun showClickToPay(token: String) {
        val amount = etPrice.text.toString().takeIf { it.isNotBlank() } ?: "10"
        val clickToPayModel =
            ClickToPayModel(
                amount = amount,
                accessKey = token,
                javaScriptToEvaluate = "initGlobalPayments('sandbox','$token', '$amount', 'd83e8615-9d0a-46fe-9677-8040887e27fa')"
            )
        val clickToPayDialog = ClickToPayFragment.newInstance(clickToPayModel)
        setFragmentResultListener(ClickToPayFragment.ClickToPayResultKey) { _, bundle ->
            val cardToken = bundle.getString(ClickToPayFragment.ClickToPayTokenKey)
            if (cardToken != null) {
                viewModel.captureTransaction(cardToken, amount)
                return@setFragmentResultListener
            }
            val error = bundle.getString(ClickToPayFragment.ClickToPayErrorKey) ?: "Error"
            TransactionErrorDialog.newInstance(error).show(childFragmentManager, TransactionErrorDialog.TAG)
        }
        clickToPayDialog.show(parentFragmentManager, "ClickToPayDialog")
    }

    /**
     * If isReadyToPay returned `true`, show the button. Otherwise,
     * notify the user that Google Pay is not available.
     *
     * @param available isReadyToPay API response.
     */
    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            googlePayButton.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                requireContext(), R.string.google_pay_status_unavailable, Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun requestPayment() {

        // Disables the button to prevent multiple clicks.
        googlePayButton.isClickable = false
        val task = viewModel.getLoadPaymentDataTask(PRICE.toString())

        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result.let(::handlePaymentSuccess)
            } else {
                when (val exception = completedTask.exception) {
                    is ResolvableApiException -> {
                        resolvePaymentForResult.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    }
                    is ApiException -> {
                        handleError(exception.statusCode, exception.message)
                    }
                    else -> {
                        handleError(
                            CommonStatusCodes.INTERNAL_ERROR,
                            "Unexpected non API" + " exception when trying to deliver the task result to an activity!"
                        )
                    }
                }
            }

            // Re-enables the Google Pay payment button.
            googlePayButton.isClickable = true
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     * WalletConstants.ERROR_CODE_* constants.
     * @see [
     * Wallet Constants Library](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants.constant-summary)
     */
    private fun handleError(statusCode: Int, message: String?) {
        Log.e("Google Pay API error", "Error code: $statusCode, Message: $message")
    }


    /**
     * PaymentData response object contains the payment information.
     * Make the transaction request to the GP SDK.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see [PaymentData](https://developers.google.com/pay/api/android/reference/object.PaymentData)
     */
    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")

            val token = paymentMethodData.getJSONObject("tokenizationData").getString("token")

            viewModel.makeTransactionWithGooglePay(token, PRICE)

        } catch (error: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $error")
        }
    }

}
