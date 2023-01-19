package com.globalpayments.android.sdk.sample.gpapi.merchantonboarding

import android.app.ProgressDialog
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.global.api.entities.User
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.sample.gpapi.merchantonboarding.create.CreateMerchantFragment
import com.globalpayments.android.sdk.utils.bindView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MerchantOnBoardingFragment : BaseFragment() {

    private val toolbar: CustomToolbar by bindView(R.id.toolbar)
    private val addMerchantButton: FloatingActionButton by bindView(R.id.btn_add_merchant)
    private val etSearch: EditText by bindView(R.id.et_search)
    private val btnSearch: Button by bindView(R.id.btn_search)
    private val etUserId: EditText by bindView(R.id.et_id)
    private val etUserStatus: EditText by bindView(R.id.et_status)
    private val etUserType: EditText by bindView(R.id.et_type)
    private val svMerchant:ScrollView by bindView(R.id.sv_merchant)
    private val viewModel: MerchantOnBoardingViewModel by viewModels()

    private lateinit var progressBar: ProgressDialog

    override fun getLayoutId(): Int = R.layout.fragment_merchant_onboarding

    override fun initViews() {
        super.initViews()
        toolbar.setTitle(R.string.merchant_onboarding)
        toolbar.setOnBackButtonListener { close() }
        addMerchantButton.setOnClickListener { onAddMerchantClicked() }
        btnSearch.setOnClickListener { onSearchMerchantClicked() }

        progressBar = ProgressDialog(requireContext()).apply {
            this.setTitle("Retrieve merchant")
        }
    }

    override fun initSubscriptions() {
        super.initSubscriptions()
        viewModel.merchants.observe(viewLifecycleOwner) { merchant ->
            svMerchant.isVisible = true
            showMerchantData(merchant)
        }
        viewModel.error.observe(viewLifecycleOwner) { error -> showToast(error) }
        viewModel.progressStatus.observe(viewLifecycleOwner) {
            if (it) progressBar.show() else progressBar.hide()
        }
    }

    private fun showMerchantData(merchant: User) {
        with(merchant.userReference) {
            etUserId.setText(userId)
            etUserType.setText(userStatus.name)
            etUserStatus.setText(userType.name)
        }
    }

    private fun onSearchMerchantClicked() {
        val merchantId = etSearch.text.toString().takeIf { it.isNotBlank() } ?: return
        viewModel.searchMerchant(merchantId)
    }

    private fun onAddMerchantClicked() {
        show(R.id.gp_api_fragment_container, CreateMerchantFragment())
    }
}
