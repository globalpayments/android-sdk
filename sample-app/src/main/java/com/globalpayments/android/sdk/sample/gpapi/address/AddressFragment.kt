package com.globalpayments.android.sdk.sample.gpapi.address

import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.utils.bindView

class AddressFragment : BaseFragment() {

    private val toolbar: CustomToolbar by bindView(R.id.toolbar)
    private val etStreetAddress1: EditText by bindView(R.id.et_street_address_1)
    private val etStreetAddress2: EditText by bindView(R.id.et_street_address_2)
    private val etStreetAddress3: EditText by bindView(R.id.et_street_address_3)
    private val etCity: EditText by bindView(R.id.et_city)
    private val etState: EditText by bindView(R.id.et_state)
    private val etPostalCode: EditText by bindView(R.id.et_postal_code)
    private val etCountryCode: EditText by bindView(R.id.et_country_code)

    private val btnCreateAddress: Button by bindView(R.id.btn_create_address)

    override fun getLayoutId(): Int = R.layout.fragment_address

    override fun initViews() {
        super.initViews()
        toolbar.setTitle(R.string.address)
        toolbar.setOnBackButtonListener { close() }
        btnCreateAddress.setOnClickListener {
            createAddressModel()
        }
        initWithModel()
    }

    private fun initWithModel() {
        val model = arguments?.getParcelable<AddressModel>(AddressKey)
        etStreetAddress1.setText(model?.streetAddress1 ?: "Apartment 852")
        etStreetAddress2.setText(model?.streetAddress2 ?: "Complex 741")
        etStreetAddress3.setText(model?.streetAddress3 ?: "Unit 4")
        etCity.setText(model?.city ?: "Chicago")
        etState.setText(model?.state ?: "IL")
        etPostalCode.setText(model?.postalCode ?: "50001")
        etCountryCode.setText(model?.countryCode ?: "840")
    }

    private fun createAddressModel() {
        resetFields()
        if (!areFieldsCompleted()) return
        val addressModel = AddressModel(
            streetAddress1 = etStreetAddress1.text.toString(),
            streetAddress2 = etStreetAddress2.text.toString(),
            streetAddress3 = etStreetAddress3.text.toString(),
            city = etCity.text.toString(),
            state = etState.text.toString(),
            postalCode = etPostalCode.text.toString(),
            countryCode = etCountryCode.text.toString()
        )
        val addressResultKey = requireArguments().getString(AddressResultKey) ?: run { close();return }
        setFragmentResult(addressResultKey, bundleOf(AddressKey to addressModel))
        close()
    }

    private fun resetFields() {
        etStreetAddress1.error = null
        etStreetAddress2.error = null
        etStreetAddress3.error = null
        etCity.error = null
        etState.error = null
        etPostalCode.error = null
        etCountryCode.error = null
    }

    private fun areFieldsCompleted(): Boolean {
        if (etStreetAddress1.text.toString().isBlank()) {
            etStreetAddress1.error = "Mandatory"
            return false
        }
        if (etStreetAddress2.text.toString().isBlank()) {
            etStreetAddress2.error = "Mandatory"
            return false
        }
        if (etStreetAddress3.text.toString().isBlank()) {
            etStreetAddress3.error = "Mandatory"
            return false
        }
        if (etCity.text.toString().isBlank()) {
            etCity.error = "Mandatory"
            return false
        }
        if (etState.text.toString().isBlank()) {
            etState.error = "Mandatory"
            return false
        }
        if (etPostalCode.text.toString().isBlank()) {
            etPostalCode.error = "Mandatory"
            return false
        }
        if (etCountryCode.text.toString().isBlank()) {
            etCountryCode.error = "Mandatory"
            return false
        }
        return true
    }

    companion object {

        fun newInstance(resultKey: String, address: AddressModel? = null): AddressFragment {
            val fragment = AddressFragment()
            fragment.arguments = bundleOf(AddressResultKey to resultKey, AddressKey to address)
            return fragment
        }

        private const val AddressResultKey = "address_result_key"
        const val AddressKey = "address_key"
    }
}
