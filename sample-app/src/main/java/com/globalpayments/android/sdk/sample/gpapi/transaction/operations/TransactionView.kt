package com.globalpayments.android.sdk.sample.gpapi.transaction.operations

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.global.api.entities.ThreeDSecure
import com.global.api.entities.Transaction
import com.global.api.entities.enums.FraudFilterMode
import com.global.api.entities.enums.FraudFilterResult
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.views.ItemView
import com.globalpayments.android.sdk.sample.common.views.Position
import com.globalpayments.android.sdk.utils.Utils

class TransactionView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val idItemView: ItemView = ItemView.create(context, R.string.id, this, Position.TOP)
    private val timeCreatedItemView: ItemView = ItemView.create(context, R.string.time_created, this, Position.SECOND)
    private val statusItemView: ItemView = ItemView.create(context, R.string.status, this)
    private val amountItemView: ItemView = ItemView.create(context, R.string.amount, this)
    private val referenceItemView: ItemView = ItemView.create(context, R.string.reference, this)
    private val batchIdItemView: ItemView = ItemView.create(context, R.string.batch_id, this)
    private val resultCodeItemView: ItemView = ItemView.create(context, R.string.result_code, this)
    private val liabilityShifItemView: ItemView = ItemView.create(context, R.string.liability_shift, this)
    private val paymentMethodItemView: ItemView = ItemView.create(context, R.string.header_payment_method, this)
    private val tokenItemView: ItemView = ItemView.create(context, R.string.id, this)
    private val resultItemView: ItemView = ItemView.create(context, R.string.result, this)
    private val cardTypeItemView: ItemView = ItemView.create(context, R.string.card_type, this)
    private val maskedCardNumberItemView: ItemView = ItemView.create(context, R.string.masked_card_number, this)
    private val cvvResultItemView: ItemView = ItemView.create(context, R.string.cvv_result, this, Position.BOTTOM)
    private val filterModeView: ItemView = ItemView.create(context, R.string.filter_mode, this).apply { isVisible = false }
    private val filterResultView: ItemView = ItemView.create(context, R.string.filter_result, this).apply { isVisible = false }

    init {
        orientation = VERTICAL
        paymentMethodItemView.setAsGroupHeader()
    }

    fun bind(transaction: Transaction) {
        idItemView.setValue(transaction.transactionId)
        timeCreatedItemView.setValue(transaction.timestamp)
        statusItemView.setValue(transaction.responseMessage)
        amountItemView.setValue(Utils.safeParseBigDecimal(transaction.balanceAmount))
        referenceItemView.setValue(transaction.referenceNumber)
        batchIdItemView.setValue(transaction.batchSummary.batchReference)
        resultCodeItemView.setValue(transaction.responseCode)
        tokenItemView.setValueOrHide(transaction.token)
        resultItemView.setValue(transaction.authorizationCode)
        cardTypeItemView.setValue(transaction.cardType)
        maskedCardNumberItemView.setValue(transaction.cardLast4)
        cvvResultItemView.setValue(transaction.cvnResponseMessage)

        val filterMode = transaction
            .fraudFilterResponse
            ?.fraudResponseMode
            ?.let { mode -> FraudFilterMode.values().firstOrNull { it.value == mode } }
        filterModeView.apply {
            isVisible = filterMode != null
            setValue(filterMode?.value)
        }
        val filterResult = transaction
            .fraudFilterResponse
            ?.fraudResponseResult
            ?.let { result -> FraudFilterResult.values().firstOrNull { it.value == result } }
        filterResultView.apply {
            isVisible = filterResult != null
            setValue(filterResult?.value)
        }
    }

    fun bind(threeDSecure: ThreeDSecure) {
        idItemView.setValue(threeDSecure.serverTransactionId)
        statusItemView.setValue(threeDSecure.status)
        referenceItemView.setValue(threeDSecure.statusReason)
        liabilityShifItemView.setValue(threeDSecure.liabilityShift)
        resultItemView.setValue(threeDSecure.cardHolderResponseInfo)
        cvvResultItemView.setValue(threeDSecure.cavv)
    }
}
