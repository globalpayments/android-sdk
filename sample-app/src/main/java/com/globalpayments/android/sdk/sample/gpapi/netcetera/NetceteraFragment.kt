package com.globalpayments.android.sdk.sample.gpapi.netcetera

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.global.api.entities.ThreeDSecure
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.Constants.CHALLENGE_REQUIRED
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.error.TransactionErrorDialog
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessDialog
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessModel
import com.globalpayments.android.sdk.ui.hf.HostedFieldsDialog
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver
import com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent
import java.math.BigDecimal


class NetceteraFragment : BaseFragment() {

    private var transaction: Transaction? = null
    private lateinit var viewModel: NetceteraViewModel

    override fun getLayoutId(): Int = R.layout.fragment_hosted_fields

    private lateinit var progressBar: ProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customToolbar = findViewById<CustomToolbar>(R.id.toolbar)
        customToolbar.setTitle(R.string.hosted_fields)
        customToolbar.setOnBackButtonListener { close() }

        view.findViewById<View>(R.id.button_pay).setOnClickListener {
            val amount = view.findViewById<EditText>(R.id.amount).text.toString()
            if (amount.isBlank()) return@setOnClickListener
            viewModel.amount = BigDecimal(amount)
            viewModel.getAccessToken()
        }
        progressBar = ProgressDialog(requireContext()).apply {
            this.setTitle("Payment in progress")
        }
    }

    override fun initDependencies() {
        super.initDependencies()
        viewModel = ViewModelProvider(this)[NetceteraViewModel::class.java]
    }

    override fun initSubscriptions() {
        super.initSubscriptions()
        viewModel.progressStatus.observe(viewLifecycleOwner) { if (it) progressBar.show() else progressBar.hide() }
        viewModel.error.observe(viewLifecycleOwner, this::dismissProgressAndShowMessage)
        viewModel.startChallengeFlow.observe(viewLifecycleOwner) { startChallengeFlow(it) }
        viewModel.paymentCompleted.observe(viewLifecycleOwner, this::showTransactionCompletedDialog)
        viewModel.createNetceteraTransaction.observe(viewLifecycleOwner) {
            createTransaction(it)
        }
        viewModel.accessToken.observe(viewLifecycleOwner, this::openHFDialog)
    }

    private fun dismissProgressAndShowMessage(message: String) {
        progressBar.dismiss()
        TransactionErrorDialog.newInstance(message)
            .show(childFragmentManager, TransactionErrorDialog.TAG)
    }

    //1. Capture Cardholder Data
    private fun openHFDialog(accessToken: String) {
        HostedFieldsDialog.newInstance(accessToken).apply {
            onTokenReceived =
                { cardToken, cardType -> viewModel.checkEnrollment(cardToken, cardType) }
            show(this@NetceteraFragment.childFragmentManager, "HFDialog")
        }
    }

    private fun createTransaction(secureEcom: ThreeDSecure) {
        NetceteraHolder.getInstance(requireContext()) { threeDS2Service ->
            //10. Create transaction
            transaction = threeDS2Service.createTransaction(
                viewModel.getDsRidValuesForCard(),
                secureEcom.messageVersion
            )
            //11. Get authentication Params
            val netceteraParams = transaction?.authenticationRequestParameters ?: run {
                dismissProgressAndShowMessage("Netcetera Params are null")
                return@getInstance
            }
            viewModel.authenticateTransaction(secureEcom, netceteraParams)
        }
    }

    //14. Frictionless or Challenge
    private fun startChallengeFlow(threeDSecure: ThreeDSecure) {
        if (threeDSecure.status != CHALLENGE_REQUIRED) {
            viewModel.doAuth(threeDSecure.serverTransactionId)
            return
        } //no need for challenge
        val challengeParams = ChallengeParameters().apply {
            acsRefNumber = threeDSecure.acsReferenceNumber
            acsSignedContent = threeDSecure.payerAuthenticationRequest
            acsTransactionID = threeDSecure.acsTransactionId
            set3DSServerTransactionID(threeDSecure.providerServerTransRef)
        }
        //16. do challenge
        transaction?.doChallenge(
            requireActivity(),
            challengeParams,
            object : ChallengeStatusReceiver {
                override fun completed(p0: CompletionEvent?) {
                    Log.d("NetceteraSDK", "challenged completed, ${p0?.transactionStatus}")
                    if (p0?.transactionStatus != "Y") {
                        dismissProgressAndShowMessage("Challenge failed")
                        return
                    }
                    viewModel.doAuth(threeDSecure.serverTransactionId)
                }

                override fun cancelled() {
                    dismissProgressAndShowMessage("Authorization cancelled")
                    Log.i("NetceteraSDK", "challenged canceled")
                }

                override fun timedout() {
                    dismissProgressAndShowMessage("Timeout on authorization")
                    Log.e("NetceteraSDK", "challenged timeout")
                }

                override fun protocolError(p0: ProtocolErrorEvent?) {
                    dismissProgressAndShowMessage(
                        p0?.errorMessage?.errorDetails ?: "Error when authorizing"
                    )
                    Log.e(
                        "NetceteraSDK",
                        "challenged protocol error ${p0?.errorMessage?.errorDetails}"
                    )
                }

                override fun runtimeError(p0: RuntimeErrorEvent?) {
                    dismissProgressAndShowMessage(
                        p0?.errorMessage?.toString() ?: "Error when authorizing"
                    )
                    Log.e("NetceteraSDK", "challenged runtime error ${p0?.errorMessage}")
                }
            },
            5
        )
    }


    private fun showTransactionCompletedDialog(transaction: com.global.api.entities.Transaction) {
        progressBar.dismiss()
        val model = TransactionSuccessModel(
            id = transaction.transactionId,
            resultCode = transaction.responseCode,
            timeCreated = transaction.timestamp,
            status = transaction.responseMessage,
            amount = transaction.balanceAmount.toString(),
        )
        TransactionSuccessDialog.newInstance(model)
            .show(childFragmentManager, TransactionSuccessDialog.TAG)
    }
}
