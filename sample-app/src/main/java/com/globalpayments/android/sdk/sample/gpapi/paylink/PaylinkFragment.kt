package com.globalpayments.android.sdk.sample.gpapi.paylink

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.global.api.entities.enums.PayLinkStatus
import com.global.api.entities.enums.PaymentMethodUsageMode
import com.global.api.entities.reporting.PayLinkSummary
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.sample.utils.bindView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import org.joda.time.DateTime
import java.math.BigDecimal
import java.util.*

class PaylinkFragment : BaseFragment() {

    private val viewModel: PaylinkViewModel by viewModels()

    private val customToolbar by bindView<CustomToolbar>(R.id.toolbar)

    private val llRequest by bindView<LinearLayout>(R.id.ll_request)
    private val btgPayerMode by bindView<MaterialButtonToggleGroup>(R.id.btg_payer_mode)
    private val etDescription by bindView<EditText>(R.id.et_description)
    private val etUsage by bindView<EditText>(R.id.et_usage)
    private val etAmount by bindView<EditText>(R.id.et_amount)
    private val etExpirationDate by bindView<EditText>(R.id.et_expiration_date)
    private val btnSubmit by bindView<MaterialButton>(R.id.btn_submit)

    private val llResponse by bindView<LinearLayout>(R.id.ll_response)
    private val etPaylink by bindView<EditText>(R.id.et_paylink)
    private val btnOpenLink by bindView<Button>(R.id.btn_open_link)
    private val etPaylinkStatus by bindView<EditText>(R.id.et_paylink_status)
    private val btnReset by bindView<Button>(R.id.btn_reset)

    override fun onResume() {
        super.onResume()
        viewModel.refreshPaylinkSummary()
    }

    override fun getLayoutId(): Int = R.layout.fragment_paylink

    override fun initViews() {
        customToolbar.setTitle(R.string.paylink)
        customToolbar.setOnBackButtonListener { close() }
        etExpirationDate.setOnClickListener { openCalendar() }

        btnSubmit.setOnClickListener { checkFieldsAndDoRequest() }
        btnReset.setOnClickListener { viewModel.paylinkSummary.postValue(null) }
        btnOpenLink.setOnClickListener { openLink() }

        btgPayerMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when {
                checkedId == R.id.btn_single && isChecked -> etUsage.setText("1")
                checkedId == R.id.btn_multiple && isChecked -> etUsage.setText("2")
            }
        }
    }

    override fun initSubscriptions() {
        viewModel.paylinkSummary.observe(viewLifecycleOwner, ::onPaylinkReceived)
        viewModel.error.observe(viewLifecycleOwner) { showToast(it) }
    }

    private fun onPaylinkReceived(payLink: PayLinkSummary?) {
        llRequest.isVisible = payLink == null
        llResponse.isVisible = payLink != null

        payLink ?: return

        etPaylink.setText(payLink.url)
        etPaylinkStatus.setText(payLink.status.toString())
    }

    private fun openLink() {
        val payLink = viewModel.paylinkSummary.value ?: return
        if (payLink.status != PayLinkStatus.ACTIVE) return
        val url = payLink.url?.takeIf { it.isNotBlank() } ?: return
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun openCalendar() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val timePickerDialog = DatePickerDialog(
            requireContext(), { _, y, m, d -> showDate(y, m + 1, d) }, year, month, dayOfMonth
        )
        timePickerDialog.show()
    }

    private fun showDate(year: Int, month: Int, day: Int) {
        etExpirationDate.setText("$day/$month/$year")
    }

    private fun checkFieldsAndDoRequest() {
        resetFields()
        val description = etDescription.textIfNotBlank() ?: run { etDescription.error = "Empty field";return }
        val amount = etAmount.textIfNotBlank() ?: run { etAmount.error = "Empty field";return }
        val expirationDate = etExpirationDate.textIfNotBlank() ?: run { etExpirationDate.error = "Empty field";return }
        val usageLimit = etUsage.textIfNotBlank() ?: run { etUsage.error = "Empty field";return }

        val usageMode = when (btgPayerMode.checkedButtonId) {
            R.id.btn_single -> PaymentMethodUsageMode.SINGLE
            else -> PaymentMethodUsageMode.MULTIPLE
        }
        val (day, month, year) = expirationDate.split("/").map(String::toInt)
        val dateTime = DateTime(year, month, day, 0, 0)
        val model = PaylinkDataModel(usageMode, usageLimit.toInt(), description, BigDecimal(amount), dateTime)
        viewModel.getPaylink(model)
    }

    private fun resetFields() {
        etDescription.error = null
        etAmount.error = null
        etExpirationDate.error = null
        etUsage.error = null
    }

    private fun EditText.textIfNotBlank(): String? = this.text.toString().takeIf { it.isNotBlank() }

}
