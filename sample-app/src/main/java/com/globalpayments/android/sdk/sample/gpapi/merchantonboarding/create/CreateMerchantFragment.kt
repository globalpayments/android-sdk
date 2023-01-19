package com.globalpayments.android.sdk.sample.gpapi.merchantonboarding.create

import android.app.ProgressDialog
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.global.api.entities.User
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.error.TransactionErrorDialog
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessDialog
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessModel
import com.globalpayments.android.sdk.utils.bindView
import kotlinx.coroutines.launch

class CreateMerchantFragment : BaseFragment() {

    private val viewModel: CreateMerchantViewModel by viewModels()

    private val toolbar: CustomToolbar by bindView(R.id.toolbar)
    private val etMerchantData: TextView by bindView(R.id.et_merchant_data)
    private val etProducts: TextView by bindView(R.id.et_products)

    //    private val etBankAccount: TextView by bindView(R.id.et_bank_account)
    private val etPaymentStatistics: TextView by bindView(R.id.et_payment_statistics)

    private val btnCreateMerchant: Button by bindView(R.id.btn_create_merchant)

    private lateinit var progressBar: ProgressDialog

    override fun getLayoutId(): Int = R.layout.fragment_create_merchant

    override fun initViews() {
        super.initViews()
        toolbar.setTitle(R.string.create_merchant)
        toolbar.setOnBackButtonListener { close() }
        etMerchantData.setOnClickListener { createMerchantData() }
        etProducts.setOnClickListener { createProducts() }
//        etBankAccount.setOnClickListener { createBankAccount() }
        etPaymentStatistics.setOnClickListener { createPaymentStatistics() }

        btnCreateMerchant.setOnClickListener { viewModel.onboardMerchant() }

        progressBar = ProgressDialog(requireContext()).apply {
            this.setTitle("Creating merchant in progress")
        }
    }

    override fun initSubscriptions() {
        super.initSubscriptions()
        viewModel.createdMerchant.observe(viewLifecycleOwner) { merchant ->
            lifecycleScope.launch {
                showTransactionCompletedDialog(merchant)
            }
        }
        viewModel.progressStatus.observe(viewLifecycleOwner) {
            if (it) progressBar.show() else progressBar.hide()
        }
        viewModel.error.observe(viewLifecycleOwner) {
            TransactionErrorDialog
                .newInstance(it)
                .show(childFragmentManager, TransactionErrorDialog.TAG)
        }
        viewModel.errors.observe(viewLifecycleOwner) { errors ->
            with(errors) {
                etMerchantData.error = if (merchantData) "Mandatory" else null
                etProducts.error = if (products) "Mandatory" else null
                etPaymentStatistics.error = if (paymentStatistics) "Mandatory" else null
            }
        }
    }

    private fun showTransactionCompletedDialog(transaction: User) {
        progressBar.dismiss()
        val model = TransactionSuccessModel(
            id = transaction.userReference.userId,
            resultCode = transaction.responseCode,
            timeCreated = "",
            status = transaction.userReference.userStatus.name,
            amount = "",
        )
        TransactionSuccessDialog.newInstance(model)
            .show(childFragmentManager, TransactionSuccessDialog.TAG)
    }

    private fun createMerchantData() {
        setFragmentResultListener(CreateMerchantDataFragment.MerchantDataResultKey) { resultKey, bundle ->
            if (resultKey != CreateMerchantDataFragment.MerchantDataResultKey) return@setFragmentResultListener
            val merchantData: MerchantDataModel =
                bundle.getParcelable(CreateMerchantDataFragment.MerchantDataResult) ?: return@setFragmentResultListener
            viewModel.merchantDataModel = merchantData
            etMerchantData.text = merchantData.toString()
            clearFragmentResultListener(CreateMerchantDataFragment.MerchantDataResultKey)
            clearFragmentResult(CreateMerchantDataFragment.MerchantDataResult)
        }
        show(R.id.gp_api_fragment_container, CreateMerchantDataFragment.newInstance(viewModel.merchantDataModel))
    }

    private fun createProducts() {

        setFragmentResultListener(CreateProductsFragment.ProductResultKey) { resultKey, bundle ->
            if (resultKey != CreateProductsFragment.ProductResultKey) return@setFragmentResultListener
            val products = bundle.get(CreateProductsFragment.ProductsKey) as? List<Product> ?: emptyList()
            viewModel.products = products
            etProducts.text = products.toString()
            clearFragmentResultListener(CreateProductsFragment.ProductResultKey)
            clearFragmentResult(CreateProductsFragment.ProductsKey)
        }
        show(R.id.gp_api_fragment_container, CreateProductsFragment.newInstance(viewModel.products))
    }

    private fun createBankAccount() {
        setFragmentResultListener(CreateBankAccountDataFragment.BankAccountResultKey) { resultKey, bundle ->
            if (resultKey != CreateBankAccountDataFragment.BankAccountResultKey) return@setFragmentResultListener
            val bankAccount: BankAccountDataModel =
                bundle.getParcelable(CreateBankAccountDataFragment.BankAccountResult) ?: return@setFragmentResultListener
            viewModel.bankAccountDataModel = bankAccount
//            etBankAccount.text = bankAccount.toString()
            clearFragmentResultListener(CreateBankAccountDataFragment.BankAccountResultKey)
            clearFragmentResult(CreateBankAccountDataFragment.BankAccountResult)
        }
        show(R.id.gp_api_fragment_container, CreateBankAccountDataFragment.newInstance(viewModel.bankAccountDataModel))
    }

    private fun createPaymentStatistics() {
        setFragmentResultListener(CreateMerchantPaymentStatisticFragment.PaymentStatisticsResultKey) { resultKey, bundle ->
            if (resultKey != CreateMerchantPaymentStatisticFragment.PaymentStatisticsResultKey) return@setFragmentResultListener
            val model: PaymentStatisticsModel =
                bundle.getParcelable(CreateMerchantPaymentStatisticFragment.PaymentStatisticsResult) ?: return@setFragmentResultListener
            viewModel.paymentStatistics = model
            etPaymentStatistics.text = model.toString()
            clearFragmentResultListener(CreateMerchantPaymentStatisticFragment.PaymentStatisticsResultKey)
            clearFragmentResult(CreateMerchantPaymentStatisticFragment.PaymentStatisticsResult)
        }
        show(R.id.gp_api_fragment_container, CreateMerchantPaymentStatisticFragment.newInstance(viewModel.paymentStatistics))
    }

    companion object {
        const val MerchantIdResultKey = "merchant_id_result_key"
        const val MerchantIdResult = "merchant_id_result"
    }
}
