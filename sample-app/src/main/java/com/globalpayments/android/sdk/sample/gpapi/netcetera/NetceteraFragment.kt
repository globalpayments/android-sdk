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
import com.globalpayments.android.sdk.sample.common.Constants.DEFAULT_GPAPI_CONFIG
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.gpapi.netcetera.dialog.error.PaymentErrorDialog
import com.globalpayments.android.sdk.sample.gpapi.netcetera.dialog.success.PaymentSuccessDialog
import com.globalpayments.android.sdk.sample.gpapi.netcetera.dialog.success.PaymentSuccessModel
import com.globalpayments.android.sdk.ui.cardform.CardFormDialogFragment
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
        view.findViewById<View>(R.id.button_pay).setOnClickListener {
            val amount = view.findViewById<EditText>(R.id.amount).text.toString()
            if (amount.isBlank()) return@setOnClickListener
            viewModel.amount = BigDecimal(amount)
            openForm()
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
        viewModel.progressStatus.observe(viewLifecycleOwner) { if (it) progressBar.show() else progressBar.show() }
        viewModel.error.observe(viewLifecycleOwner, this::dismissProgressAndShowMessage)
        viewModel.startChallengeFlow.observe(viewLifecycleOwner) { startChallengeFlow(it) }
        viewModel.paymentCompleted.observe(viewLifecycleOwner, this::showTransactionCompletedDialog)
        viewModel.createNetceteraTransaction.observe(viewLifecycleOwner) {
            createTransaction(it)
        }
    }

    private fun dismissProgressAndShowMessage(message: String) {
        progressBar.dismiss()
        PaymentErrorDialog.newInstance(message).show(childFragmentManager, "ERROR_DIALOG")
    }

    private fun openForm() {
        CardFormDialogFragment.newInstance(DEFAULT_GPAPI_CONFIG).apply {
            onSubmitClicked = {
                viewModel.tokenizeCard(it)
            }
            show(this@NetceteraFragment.childFragmentManager, "CARD_FORM_DIALOG")
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
        val model = PaymentSuccessModel(
            id = transaction.transactionId,
            resultCode = transaction.responseCode,
            timeCreated = transaction.timestamp,
            status = transaction.responseMessage,
            amount = transaction.balanceAmount.toString(),
        )
        PaymentSuccessDialog.newInstance(model).show(childFragmentManager, "PaymentSuccessDialog")
    }
}
