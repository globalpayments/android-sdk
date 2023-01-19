package com.globalpayments.android.sdk.sample.gpapi.merchantonboarding.create

import android.os.Parcelable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.sample.gpapi.address.AddressFragment
import com.globalpayments.android.sdk.sample.gpapi.address.AddressModel
import com.globalpayments.android.sdk.utils.bindView
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime

@Parcelize
data class MerchantDataModel(
    val merchantName: String,
    val fullLegalName: String,
    val dba: String,
    val merchantCategory: Int,
    val website: String,
    val email: String,
    val currency: String,
    val taxIdReference: String,
    val businessAddress: AddressModel,
    val shippingAddress: AddressModel
) : Parcelable {
    override fun toString(): String {
        return "$merchantName|$merchantCategory"
    }
}

class CreateMerchantDataFragment : BaseFragment() {

    private val toolbar: CustomToolbar by bindView(R.id.toolbar)

    private val etUsername: EditText by bindView(R.id.et_merchant_name)
    private val etFullLegalName: EditText by bindView(R.id.et_full_legal_name)
    private val etDba: EditText by bindView(R.id.et_dba)
    private val etMerchantCategory: EditText by bindView(R.id.et_merchant_category)
    private val etWebsite: EditText by bindView(R.id.et_website)
    private val etEmail: EditText by bindView(R.id.et_email)
    private val etCurrency: EditText by bindView(R.id.et_currency)
    private val etTaxIdReference: EditText by bindView(R.id.et_taxIdReference)
    private val etBusinessAddress: TextView by bindView(R.id.et_business_address)
    private val etShippingAddress: TextView by bindView(R.id.et_shipping_address)
    private val btnCreateMerchantData: Button by bindView(R.id.btn_create_merchant_data)

    private var businessAddress: AddressModel? = null
    private var shippingAddress: AddressModel? = null

    override fun getLayoutId(): Int = R.layout.fragment_create_merchant_data

    override fun initViews() {
        super.initViews()
        toolbar.setTitle(R.string.merchant_data)
        toolbar.setOnBackButtonListener { close() }
        etBusinessAddress.setOnClickListener { createBusinessAddress() }
        etShippingAddress.setOnClickListener { createShippingAddress() }
        btnCreateMerchantData.setOnClickListener { createMerchantData() }
        initWithModel()
    }

    private fun initWithModel() {
        val model = arguments?.getParcelable<MerchantDataModel>(MerchantDataResult)
        etUsername.setText(model?.merchantName ?: ("CERT_Propay_" + DateTime.now().toString("yyyyMMddHHmmss")))
        etFullLegalName.setText(model?.fullLegalName ?: "Business Legal Name")
        etDba.setText(model?.dba ?: "Doing Business As")
        etMerchantCategory.setText(model?.merchantCategory?.toString() ?: "5999")
        etWebsite.setText(model?.website ?: "https://example.com/")
        etEmail.setText(model?.email ?: "merchant@example.com")
        etCurrency.setText(model?.currency ?: "USD")
        etTaxIdReference.setText(model?.taxIdReference ?: "123456789")
        etBusinessAddress.setText(model?.businessAddress?.toString())
        model?.businessAddress?.let { businessAddress = it }
        etShippingAddress.setText(model?.shippingAddress?.toString())
        model?.shippingAddress?.let { shippingAddress = it }
    }

    private fun createBusinessAddress() {
        val requestKey = "BusinessAddress"
        setFragmentResultListener(requestKey) { resultKey, bundle ->
            if (resultKey == requestKey) {
                val address: AddressModel = bundle.getParcelable(AddressFragment.AddressKey) ?: return@setFragmentResultListener
                businessAddress = address
                etBusinessAddress.text = address.toString().take(15)
                clearFragmentResultListener(requestKey)
                clearFragmentResult(AddressFragment.AddressKey)
            }
        }
        show(R.id.gp_api_fragment_container, AddressFragment.newInstance("BusinessAddress", businessAddress))
    }

    private fun createShippingAddress() {
        val requestKey = "ShippingAddress"
        setFragmentResultListener(requestKey) { resultKey, bundle ->
            if (resultKey == requestKey) {
                val address: AddressModel = bundle.getParcelable(AddressFragment.AddressKey) ?: return@setFragmentResultListener
                shippingAddress = address
                etShippingAddress.text = address.toString().take(15)
                clearFragmentResultListener(requestKey)
                clearFragmentResult(AddressFragment.AddressKey)
            }
        }
        show(R.id.gp_api_fragment_container, AddressFragment.newInstance(requestKey, shippingAddress))
    }

    private fun createMerchantData() {
        resetFields()
        if (!areFieldsCompleted()) return
        val businessAddress = businessAddress ?: return
        val shippingAddress = shippingAddress ?: return
        val merchantData = MerchantDataModel(
            merchantName = etUsername.text.toString(),
            fullLegalName = etFullLegalName.text.toString(),
            dba = etDba.text.toString(),
            merchantCategory = etMerchantCategory.text.toString().toInt(),
            website = etWebsite.text.toString(),
            email = etEmail.text.toString(),
            currency = etCurrency.text.toString(),
            taxIdReference = etTaxIdReference.text.toString(),
            businessAddress = businessAddress,
            shippingAddress = shippingAddress
        )
        setFragmentResult(MerchantDataResultKey, bundleOf(MerchantDataResult to merchantData))
        close()
    }

    private fun resetFields() {
        etUsername.error = null
        etFullLegalName.error = null
        etDba.error = null
        etMerchantCategory.error = null
        etWebsite.error = null
        etEmail.error = null
        etCurrency.error = null
        etTaxIdReference.error = null
        etBusinessAddress.error = null
        etShippingAddress.error = null
    }

    private fun areFieldsCompleted(): Boolean {
        if (etUsername.text.toString().isBlank()) {
            etUsername.error = "Mandatory"
            return false
        }
        if (etFullLegalName.text.toString().isBlank()) {
            etFullLegalName.error = "Mandatory"
            return false
        }
        if (etDba.text.toString().isBlank()) {
            etDba.error = "Mandatory"
            return false
        }
        if (etMerchantCategory.text.toString().isBlank()) {
            etMerchantCategory.error = "Mandatory"
            return false
        }
        if (etWebsite.text.toString().isBlank()) {
            etWebsite.error = "Mandatory"
            return false
        }
        if (etEmail.text.toString().isBlank()) {
            etEmail.error = "Mandatory"
            return false
        }
        if (etCurrency.text.toString().isBlank()) {
            etCurrency.error = "Mandatory"
            return false
        }
        if (etTaxIdReference.text.toString().isBlank()) {
            etTaxIdReference.error = "Mandatory"
            return false
        }
        if (etBusinessAddress.text.toString().isBlank()) {
            etBusinessAddress.error = "Mandatory"
            return false
        }
        if (etShippingAddress.text.toString().isBlank()) {
            etShippingAddress.error = "Mandatory"
            return false
        }
        return true
    }

    companion object {
        const val MerchantDataResult = "merchant_data_result"
        const val MerchantDataResultKey = "merchant_data_result_key"
        fun newInstance(merchantDataModel: MerchantDataModel?): CreateMerchantDataFragment {
            return CreateMerchantDataFragment().apply {
                arguments = bundleOf(MerchantDataResult to merchantDataModel)
            }
        }
    }


}
