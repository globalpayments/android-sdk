package com.globalpayments.android.sdk.sample.gpapi.merchantonboarding.create

import android.os.Parcelable
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.global.api.entities.enums.AccountType
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.utils.bindView
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankAccountDataModel(
    val accountHolderName: String, val accountNumber: String, val accountOwnership: String, val accountType: String, val routingNumber: String
) : Parcelable {
    override fun toString(): String {
        return "$accountHolderName|$accountNumber"
    }
}

class CreateBankAccountDataFragment : BaseFragment() {

    private val toolbar: CustomToolbar by bindView(R.id.toolbar)

    private val etAccountHolder: EditText by bindView(R.id.et_account_holder)
    private val etAccountNumber: EditText by bindView(R.id.et_account_number)
    private val etAccountOwnership: EditText by bindView(R.id.et_account_ownership)
    private val spAccountType: Spinner by bindView(R.id.sp_account_type)
    private val etRoutingNumber: EditText by bindView(R.id.et_routing_number)

    private val btnCreateBankAccount: Button by bindView(R.id.btn_create_bank_account)

    private val accountTypeAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            AccountType.values()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_create_bank_account

    override fun initViews() {
        super.initViews()
        toolbar.setTitle(R.string.bank_account)
        toolbar.setOnBackButtonListener { close() }
        btnCreateBankAccount.setOnClickListener { createBankAccount() }

        initWithArgument()
    }

    private fun initWithArgument() {
        val model = arguments?.getParcelable<BankAccountDataModel>(BankAccountResult)
        etAccountHolder.setText(model?.accountHolderName ?: "Bank Account Holder Name")
        etAccountNumber.setText(model?.accountNumber ?: "123456788")
        etAccountOwnership.setText(model?.accountOwnership ?: "Personal")
        etRoutingNumber.setText(model?.routingNumber ?: "102000076")
        spAccountType.adapter = accountTypeAdapter
    }

    private fun createBankAccount() {
        resetFields()
        if (!fieldsAreCompleted()) return
        val bankAccountDataModel = BankAccountDataModel(
            accountHolderName = etAccountHolder.text.toString(),
            accountNumber = etAccountNumber.text.toString(),
            accountOwnership = etAccountOwnership.text.toString(),
            accountType = (spAccountType.selectedItem as AccountType).value,
            routingNumber = etRoutingNumber.text.toString()
        )
        setFragmentResult(BankAccountResultKey, bundleOf(BankAccountResult to bankAccountDataModel))
        close()
    }

    private fun fieldsAreCompleted(): Boolean {
        if (etAccountHolder.text.toString().isBlank()) {
            etAccountHolder.error = "Mandatory"
            return false
        }
        if (etAccountNumber.text.toString().isBlank()) {
            etAccountNumber.error = "Mandatory"
            return false
        }
        if (etAccountOwnership.text.toString().isBlank()) {
            etAccountOwnership.error = "Mandatory"
            return false
        }
        if (etRoutingNumber.text.toString().isBlank()) {
            etRoutingNumber.error = "Mandatory"
            return false
        }
        return true
    }

    private fun resetFields() {
        etAccountHolder.error = null
        etAccountNumber.error = null
        etAccountOwnership.error = null
        etRoutingNumber.error = null
    }

    companion object {
        const val BankAccountResultKey = "bank_account_result_key"
        const val BankAccountResult = "bank_account_result"
        fun newInstance(bankAccountDataModel: BankAccountDataModel?): CreateBankAccountDataFragment {
            return CreateBankAccountDataFragment().apply {
                arguments = bundleOf(BankAccountResult to bankAccountDataModel)
            }
        }
    }
}
