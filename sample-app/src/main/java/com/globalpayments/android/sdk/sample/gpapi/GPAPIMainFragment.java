package com.globalpayments.android.sdk.sample.gpapi;

import android.widget.Button;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.accesstoken.AccessTokenFragment;
import com.globalpayments.android.sdk.sample.gpapi.configuration.GPAPIConfigurationFragment;
import com.globalpayments.android.sdk.sample.gpapi.deposits.DepositsFragment;
import com.globalpayments.android.sdk.sample.gpapi.disputes.DisputesFragment;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.PaymentMethodsFragment;
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
        customToolbar.addAdditionalButton(R.drawable.ic_settings_24, v ->
                show(R.id.gp_api_fragment_container, GPAPIConfigurationFragment.newInstance(true)));

        setButtonClickListener(R.id.accessTokenButton, new AccessTokenFragment());
        setButtonClickListener(R.id.transactionsButton, new TransactionFragment());
        setButtonClickListener(R.id.verificationsButton, new VerificationsFragment());
        setButtonClickListener(R.id.paymentMethodsButton, new PaymentMethodsFragment());
        setButtonClickListener(R.id.depositsButton, new DepositsFragment());
        setButtonClickListener(R.id.disputesButton, new DisputesFragment());
    }

    private void setButtonClickListener(@IdRes int buttonId, Fragment fragment) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> show(R.id.gp_api_fragment_container, fragment));
    }
}
