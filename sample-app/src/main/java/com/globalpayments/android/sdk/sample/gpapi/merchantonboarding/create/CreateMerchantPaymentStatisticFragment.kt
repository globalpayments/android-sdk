package com.globalpayments.android.sdk.sample.gpapi.merchantonboarding.create

import android.os.Parcelable
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.utils.bindView
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentStatisticsModel(
    val totalMonthlySales: Long,
    val averageTicketSales: Long,
    val highestTicketSales: Long,
) : Parcelable {
    override fun toString(): String {
        return "$totalMonthlySales|$averageTicketSales|$highestTicketSales"
    }
}

class CreateMerchantPaymentStatisticFragment : BaseFragment() {

    private val toolbar: CustomToolbar by bindView(R.id.toolbar)
    private val etTotalMonthlySales: EditText by bindView(R.id.et_total_monthly_sales)
    private val etAverageTicketSales: EditText by bindView(R.id.et_average_ticket_sales)
    private val etHighestTicketSales: EditText by bindView(R.id.et_highest_ticket_sales)

    private val btnCreateMerchantPaymentStatistics: Button by bindView(R.id.btn_create_merchant_payment_statistics)

    override fun getLayoutId(): Int = R.layout.fragment_create_merchant_payment_statistics

    override fun initViews() {
        super.initViews()
        toolbar.setTitle(R.string.payment_statistics)
        toolbar.setOnBackButtonListener { close() }
        btnCreateMerchantPaymentStatistics.setOnClickListener { createPaymentStatisticsModel() }
        val model: PaymentStatisticsModel? = arguments?.getParcelable(PaymentStatisticsResult)
        etTotalMonthlySales.setText(model?.totalMonthlySales?.toString() ?: "3000000")
        etAverageTicketSales.setText(model?.averageTicketSales?.toString() ?: "50000")
        etHighestTicketSales.setText(model?.highestTicketSales?.toString() ?: "60000")
    }

    private fun createPaymentStatisticsModel() {
        resetFields()
        val totalMonthlySales = etTotalMonthlySales.text.toString().toLongOrNull() ?: run {
            etTotalMonthlySales.error = "Mandatory"
            return
        }
        val averageTicketSales = etAverageTicketSales.text.toString().toLongOrNull() ?: run {
            etAverageTicketSales.error = "Mandatory"
            return
        }
        val highestTicketSales = etHighestTicketSales.text.toString().toLongOrNull() ?: run {
            etHighestTicketSales.error = "Mandatory"
            return
        }
        val model = PaymentStatisticsModel(totalMonthlySales, averageTicketSales, highestTicketSales)
        setFragmentResult(PaymentStatisticsResultKey, bundleOf(PaymentStatisticsResult to model))
        close()
    }

    private fun resetFields() {
        etTotalMonthlySales.error = null
        etAverageTicketSales.error = null
        etHighestTicketSales.error = null
    }

    companion object {
        const val PaymentStatisticsResult = "payment_statistics_result"
        const val PaymentStatisticsResultKey = "payment_statistics_result_key"
        fun newInstance(paymentStatisticsModel: PaymentStatisticsModel?): CreateMerchantPaymentStatisticFragment {
            return CreateMerchantPaymentStatisticFragment().apply {
                arguments = bundleOf(PaymentStatisticsResult to paymentStatisticsModel)
            }
        }
    }

}
