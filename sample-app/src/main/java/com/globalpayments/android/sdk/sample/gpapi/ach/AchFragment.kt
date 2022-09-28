package com.globalpayments.android.sdk.sample.gpapi.ach

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.global.api.entities.Address
import com.global.api.entities.Customer
import com.global.api.entities.enums.AccountType
import com.global.api.entities.enums.SecCode
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.error.TransactionErrorDialog
import com.globalpayments.android.sdk.sample.gpapi.dialogs.transaction.success.TransactionSuccessDialog
import java.math.BigDecimal
import java.util.*


class AchFragment : Fragment(R.layout.fragment_ach) {

    private val achViewModel: AchViewModel by viewModels()

    //Bank
    private val etAccountHolderName: EditText by lazy { requireView().findViewById(R.id.et_account_holder) }
    private val etRoutingNumber: EditText by lazy { requireView().findViewById(R.id.et_routing_number) }
    private val etAccountNumber: EditText by lazy { requireView().findViewById(R.id.et_account_number) }

    //Customer
    private val etCustomerFirstName: EditText by lazy { requireView().findViewById(R.id.et_customer_first_name) }
    private val etCustomerLastName: EditText by lazy { requireView().findViewById(R.id.et_customer_first_name) }
    private val etCustomerMobilePhone: EditText by lazy { requireView().findViewById(R.id.et_customer_mobile_phone) }
    private val etCustomerHomePhone: EditText by lazy { requireView().findViewById(R.id.et_customer_home_phone) }

    private val etBillingAddressLine1: EditText by lazy { requireView().findViewById(R.id.et_customer_line_1) }
    private val etBillingAddressLine2: EditText by lazy { requireView().findViewById(R.id.et_customer_line_2) }
    private val etBillingCity: EditText by lazy { requireView().findViewById(R.id.et_customer_city) }
    private val etBillingState: EditText by lazy { requireView().findViewById(R.id.et_customer_state) }
    private val etBillingPostalCode: EditText by lazy { requireView().findViewById(R.id.et_customer_postal_code) }
    private val etBillingCountry: EditText by lazy { requireView().findViewById(R.id.et_customer_country) }

    private val etCustomerBirthDate: EditText by lazy { requireView().findViewById(R.id.et_customer_birth_date) }

    private val spAccountType: AutoCompleteTextView by lazy { requireView().findViewById(R.id.sp_account_type) }
    private val spSecCode: AutoCompleteTextView by lazy { requireView().findViewById(R.id.sp_sec_code) }
    private val etAmount: EditText by lazy { requireView().findViewById(R.id.et_amount) }

    private val rgTransactionType: RadioGroup by lazy { requireView().findViewById(R.id.rg_transaction_type) }

    private val btnPlaceOrder: Button by lazy { requireView().findViewById(R.id.btn_place_order) }

    private lateinit var progressBar: ProgressDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAccountTypeSpinner()
        setupSecCodeSpinner()
        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
        etCustomerBirthDate.setOnClickListener {
            openCalendar()
        }
        achViewModel.successTransaction.observe(viewLifecycleOwner) {
            TransactionSuccessDialog.newInstance(it)
                .show(childFragmentManager, TransactionSuccessDialog.TAG)
        }
        achViewModel.errorTransaction.observe(viewLifecycleOwner) {
            TransactionErrorDialog.newInstance(it)
                .show(childFragmentManager, TransactionErrorDialog.TAG)
        }
        progressBar = ProgressDialog(requireContext()).apply {
            this.setTitle("Operation in progress")
        }
        achViewModel.progressStatus.observe(viewLifecycleOwner) { if (it) progressBar.show() else progressBar.dismiss() }
    }

    private fun setupAccountTypeSpinner() {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            AccountType.values().map(AccountType::name)
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spAccountType.setAdapter(adapter)
        }
        spAccountType.setOnItemClickListener { _, _, position, _ ->
            achViewModel.onAccountTypeSelected(
                AccountType.values()[position]
            )
        }
    }

    private fun setupSecCodeSpinner() {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            SecCode.values().map(SecCode::name)
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spSecCode.setAdapter(adapter)
        }
        spSecCode.setOnItemClickListener { _, _, position, _ ->
            achViewModel.onSecCodeSelected(
                SecCode.values()[position]
            )
        }
    }

    private fun openCalendar() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val timePickerDialog = DatePickerDialog(
            requireContext(),
            { _, y, m, d -> showDate(y, m, d) },
            year, month, dayOfMonth
        )
        timePickerDialog.show()
    }

    private fun showDate(year: Int, month: Int, dayOfMonth: Int) {
        etCustomerBirthDate.setText("$year-$month-$dayOfMonth")
    }

    private fun placeOrder() {
        val amount = etAmount.text.toString().let { BigDecimal(it) }
        val accountHolderName = etAccountHolderName.text.toString()
        val routingNumber = etRoutingNumber.text.toString()
        val accountNumber = etAccountNumber.text.toString()

        val billingAddress = Address().apply {
            streetAddress1 = etBillingAddressLine1.text.toString()
            streetAddress2 = etBillingAddressLine2.text.toString()
            city = etBillingCity.text.toString()
            state = etBillingState.text.toString()
            postalCode = etBillingPostalCode.text.toString()
            country = etBillingCountry.text.toString()
        }

        val customer = Customer().apply {
            firstName = etCustomerFirstName.text.toString()
            lastName = etCustomerLastName.text.toString()
            mobilePhone = etCustomerMobilePhone.text.toString()
            homePhone = etCustomerHomePhone.text.toString()
            dateOfBirth = etCustomerBirthDate.text.toString()
        }

        val orderType = when (rgTransactionType.checkedRadioButtonId) {
            R.id.rb_charge -> AchViewModel.OrderType.Charge
            else -> AchViewModel.OrderType.Refund
        }

        achViewModel.makePayment(
            amount,
            accountHolderName,
            routingNumber,
            accountNumber,
            billingAddress,
            customer,
            orderType
        )
    }
}