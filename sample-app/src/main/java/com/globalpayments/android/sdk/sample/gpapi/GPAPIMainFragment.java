package com.globalpayments.android.sdk.sample.gpapi;

import android.widget.Button;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.accesstoken.AccessTokenFragment;
import com.globalpayments.android.sdk.sample.gpapi.ach.AchFragment;
import com.globalpayments.android.sdk.sample.gpapi.actions.actionsReport.ActionsReportFragment;
import com.globalpayments.android.sdk.sample.gpapi.batch.closeBatch.CloseFragment;
import com.globalpayments.android.sdk.sample.gpapi.deposits.DepositsFragment;
import com.globalpayments.android.sdk.sample.gpapi.digitalwallet.DigitalWalletFragment;
import com.globalpayments.android.sdk.sample.gpapi.disputes.DisputesFragment;
import com.globalpayments.android.sdk.sample.gpapi.ebt.EbtFragment;
import com.globalpayments.android.sdk.sample.gpapi.netcetera.NetceteraFragment;
import com.globalpayments.android.sdk.sample.gpapi.paylink.PaylinkFragment;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.PaymentMethodsFragment;
import com.globalpayments.android.sdk.sample.gpapi.paypal.PaypalFragment;
import com.globalpayments.android.sdk.sample.gpapi.transaction.TransactionFragment;
import com.globalpayments.android.sdk.sample.gpapi.verifications.VerificationsFragment;

public class GPAPIMainFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_gp_api_main;
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.gp_api);

        setButtonClickListener(R.id.accessTokenButton, new AccessTokenFragment());
        setButtonClickListener(R.id.transactionsButton, new TransactionFragment());
        setButtonClickListener(R.id.verificationsButton, new VerificationsFragment());
        setButtonClickListener(R.id.paymentMethodsButton, new PaymentMethodsFragment());
        setButtonClickListener(R.id.depositsButton, new DepositsFragment());
        setButtonClickListener(R.id.disputesButton, new DisputesFragment());
        setButtonClickListener(R.id.actionsButton, new ActionsReportFragment());
        setButtonClickListener(R.id.closeBatchButton, new CloseFragment());
        setButtonClickListener(R.id.hosted_fields_button, new NetceteraFragment());
        setButtonClickListener(R.id.digitalWallet, new DigitalWalletFragment());
        setButtonClickListener(R.id.paypal_button, new PaypalFragment());
        setButtonClickListener(R.id.ach_button, new AchFragment());
        setButtonClickListener(R.id.ebt_button, new EbtFragment());
        setButtonClickListener(R.id.paylink_button, new PaylinkFragment());
    }

    private void setButtonClickListener(@IdRes int buttonId, Fragment fragment) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> show(R.id.gp_api_fragment_container, fragment));
    }
}
