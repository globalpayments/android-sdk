package com.globalpayments.android.sdk.sample.gpapi.transaction.operations

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.global.api.ServicesContainer
import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.FraudFilterMode
import com.global.api.gateways.GpApiConnector
import com.globalpayments.android.sdk.model.PaymentCardModel
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.Constants
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationModel
import com.globalpayments.android.sdk.sample.gpapi.transaction.operations.model.TransactionOperationType.Companion.getRoots
import com.globalpayments.android.sdk.sample.utils.FingerprintMethodUsageMode
import com.globalpayments.android.sdk.sample.utils.ManualEntryMethodUsageMode
import com.globalpayments.android.sdk.sample.utils.PaymentMethodUsageMode
import com.globalpayments.android.sdk.sample.utils.bindView
import com.globalpayments.android.sdk.utils.Utils
import java.math.BigDecimal

class TransactionOperationDialog : BaseDialogFragment() {
    private val currenciesSpinner: CustomSpinner by bindView(R.id.currenciesSpinner)
    private val fraudModeSpinner: CustomSpinner by bindView(R.id.fraudModeSpinner)
    private val transactionTypeSpinner: CustomSpinner by bindView(R.id.transactionTypeSpinner)
    private val cardModelsSpinner: CustomSpinner by bindView(R.id.cardModelsSpinner)
    private val multiTokenSpinner: CustomSpinner by bindView(R.id.multiTokenSpinner)
    private val manualEntryModeSpinner: CustomSpinner by bindView(R.id.manualEntryModeSpinner)
    private val cbRequestMultiUseToken: CheckBox by bindView(R.id.cbRequestMultiUseToken)
    private val cbFromFingerPrint: CheckBox by bindView(R.id.cbFromFingerPrint)
    private val etCardNumber: EditText by bindView(R.id.etCardNumber)
    private val etExpiryMonth: EditText by bindView(R.id.etExpiryMonth)
    private val etExpiryYear: EditText by bindView(R.id.etExpiryYear)
    private val etCvnCvv: EditText by bindView(R.id.etCvnCvv)
    private val etAmount: EditText by bindView(R.id.etAmount)
    private val etIdempotencyKey: EditText by bindView(R.id.etIdempotencyKey)
    private val etDynamicDescriptor: EditText by bindView(R.id.etDynamicDescriptor)
    private val tvFingerPrintId: TextView by bindView(R.id.tvFingerPrintId)
    private val etPaymentLinkId: TextView by bindView(R.id.etPaymentLinkId)
    private val tvManualEntryMode: TextView by bindView(R.id.tvManualEntryMode)
    private val multiFingerPrintUsageMode: CustomSpinner by bindView(R.id.multiFingerPrintUsageMode)

    override fun getLayoutId(): Int {
        return R.layout.dialog_fragment_transaction_operation
    }

    override fun initViews() {
        initSpinners()
        val btSubmit = findViewById<Button>(R.id.btSubmit)
        btSubmit.setOnClickListener { submitTransactionOperationModel() }
        cbFromFingerPrint.setOnCheckedChangeListener { _, isChecked: Boolean ->
            tvFingerPrintId.isVisible = isChecked
            multiFingerPrintUsageMode.isVisible = isChecked
        }
        val channel = (ServicesContainer.getInstance().getGateway(Constants.DEFAULT_GPAPI_CONFIG) as? GpApiConnector)?.gpApiConfig?.channel
        val isCardPresent = Channel.CardNotPresent.value == channel
        tvManualEntryMode.isVisible = isCardPresent
        manualEntryModeSpinner.isVisible = isCardPresent
        if (isCardPresent) {
            manualEntryModeSpinner.init(ManualEntryMethodUsageMode.values())
        }
    }

    private fun initSpinners() {
        transactionTypeSpinner.init(getRoots())
        currenciesSpinner.init(resources.getStringArray(R.array.currencies))
        multiTokenSpinner.init(PaymentMethodUsageMode.values())
        cardModelsSpinner.init(
            PaymentCardModel.values(),
            false
        ) { paymentCardModel: PaymentCardModel ->
            fillPaymentCardFields(paymentCardModel)
        }
        multiFingerPrintUsageMode.init(FingerprintMethodUsageMode.values())
        fraudModeSpinner.init(FraudFilterMode.values())
    }

    private fun fillPaymentCardFields(paymentCardModel: PaymentCardModel) {
        etCardNumber.setText(paymentCardModel.cardNumber)
        etExpiryMonth.setText(paymentCardModel.expiryMonth)
        etExpiryYear.setText(paymentCardModel.expiryYear)
        etCvnCvv.setText(paymentCardModel.cvnCvv)
    }

    private fun submitTransactionOperationModel() {
        val amount = Utils.safeParseBigDecimal(
            etAmount.text.toString()
        )
        val targetFragment = targetFragment
        if (targetFragment is Callback) {
            val callback = targetFragment as Callback
            val transactionOperationModel = TransactionOperationModel(
                cardNumber = etCardNumber.text.toString(),
                expiryMonth = etExpiryMonth.text.toString().toInt(),
                expiryYear = etExpiryYear.text.toString().toInt(),
                cvnCvv = etCvnCvv.text.toString(),
                amount = amount ?: BigDecimal(0),
                currency = currenciesSpinner.getSelectedOption(),
                transactionOperationType = transactionTypeSpinner.getSelectedOption(),
                paymentMethodUsageMode = multiTokenSpinner.getSelectedOption(),
                requestMultiUseToken = cbRequestMultiUseToken.isChecked,
                fingerprintMethodUsageMode = multiFingerPrintUsageMode.getSelectedOption(),
                idempotencyKey = etIdempotencyKey.text.toString(),
                fingerPrintSelection = cbFromFingerPrint.isChecked,
                paymentLinkId = etPaymentLinkId.text.toString(),
                dynamicDescriptor = etDynamicDescriptor.text.toString(),
                manualEntryMethodUsageMode = manualEntryModeSpinner.getSelectedOption(),
                fraudMode = fraudModeSpinner.getSelectedOption()
            )
            callback.onSubmitTransactionOperationModel(transactionOperationModel)
        }
        dismiss()
    }

    interface Callback {
        fun onSubmitTransactionOperationModel(transactionOperationModel: TransactionOperationModel)
    }

    companion object {
        fun newInstance(targetFragment: Fragment?): TransactionOperationDialog {
            val transactionOperationDialog = TransactionOperationDialog()
            transactionOperationDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
            transactionOperationDialog.setTargetFragment(targetFragment, 0)
            return transactionOperationDialog
        }
    }
}
