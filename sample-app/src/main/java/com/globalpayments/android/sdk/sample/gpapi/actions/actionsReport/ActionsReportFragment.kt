package com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport

import android.app.ProgressDialog
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.global.api.entities.reporting.ActionSummary
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport.model.ActionsReportParametersModel
import com.globalpayments.android.sdk.utils.ViewUtils
import com.globalpayments.android.sdk.utils.bindView

class ActionsReportFragment : BaseFragment(), ActionsDialog.Callback {
    private val errorTextView: TextView by bindView(R.id.errorTextView)
    private val recyclerView: RecyclerView by bindView(R.id.recyclerView)
    private var progressDialog: ProgressDialog? = null
    private val actionsReportAdapter: ActionsReportAdapter = ActionsReportAdapter()
    private val actionsReportViewModel: ActionsReportViewModel by viewModels()
    override fun getLayoutId(): Int {
        return R.layout.actions_report_list_fragment
    }

    override fun initViews() {
        val customToolbar = findViewById<CustomToolbar>(R.id.toolbar)
        customToolbar.setTitle(R.string.actions_report)
        customToolbar.setOnBackButtonListener { close() }
        val btGetActionList = findViewById<Button>(R.id.btGetActionList)
        btGetActionList.setOnClickListener { showActionsReportDialog(ActionsDialog.TYPE.ACTIONS_REPORT_LIST) }
        val btGetActionById = findViewById<Button>(R.id.btGetActionById)
        btGetActionById.setOnClickListener { showActionsReportDialog(ActionsDialog.TYPE.ACTIONS_REPORT_BY_ID) }

        progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("Retrieving actions")
        }

        recyclerView.adapter = actionsReportAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (actionsReportViewModel.progressStatus.value == true) {
                    return
                }
                val linearLayoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == actionsReportAdapter.itemCount - 2) {
                    actionsReportViewModel.loadMore()
                }
            }
        })
    }

    private fun showActionsReportDialog(type: ActionsDialog.TYPE) {
        val actionsDialog = ActionsDialog.newInstance(this, type)
        actionsDialog.show(requireFragmentManager(), ActionsDialog::class.java.simpleName)
    }

    override fun initSubscriptions() {
        actionsReportViewModel.progressStatus.observe(this) { show: Boolean ->
            if (show) {
                ViewUtils.hideViews(errorTextView)
                progressDialog?.show()
            } else {
                progressDialog?.hide()
            }
        }
        actionsReportViewModel.error.observe(this) { errorMessage: String ->
            ViewUtils.hideViews(recyclerView)
            ViewUtils.showView(errorTextView)
            if (errorMessage.contains("Empty") && errorMessage.contains("List")) {
                errorTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorBlack
                    )
                )
                errorTextView.setText(R.string.empty_list)
            } else {
                errorTextView.text = errorMessage
            }
        }
        actionsReportViewModel.getActionsLiveData().observe(this) { actions: List<ActionSummary> ->
            ViewUtils.hideViews(errorTextView)
            ViewUtils.showView(recyclerView)
            submitActions(actions)
        }
    }

    private fun submitActions(actionSummaries: List<ActionSummary>) {
        actionsReportAdapter.isExpandedByDefault = actionSummaries.size == 1
        actionsReportAdapter.addItems(actionSummaries)
    }

    override fun onSubmitActionsReportListParametersModel(actionsReportParametersModel: ActionsReportParametersModel) {
        actionsReportAdapter.clearItems()
        actionsReportViewModel.getActionsList(actionsReportParametersModel)
    }

    override fun onSubmitActionsReportId(actionId: String) {
        actionsReportAdapter.clearItems()
        actionsReportViewModel.getActionsById(actionId)
    }

    override fun onDestroyView() {
        progressDialog?.dismiss()
        super.onDestroyView()
    }
}
