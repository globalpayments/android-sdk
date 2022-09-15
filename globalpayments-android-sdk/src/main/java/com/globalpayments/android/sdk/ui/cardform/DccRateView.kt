package com.globalpayments.android.sdk.ui.cardform

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.globalpayments.android.sdk.R
import com.globalpayments.android.sdk.model.DccRateModel

class DccRateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val title: TextView
    private val merchantPrice: TextView
    private val payerPrice: TextView
    private val markupCommission: TextView
    private val exchangeRate: TextView
    private val swDccRate: SwitchCompat
    private val ivHelp: ImageView

    var onDccRateSelected: (Boolean) -> Unit = {}

    private var dccRate: DccRateModel? = null

    init {
        inflate(context, R.layout.view_dcc_rate, this)
        title = findViewById(R.id.tv_select_dcc_title)
        merchantPrice = findViewById(R.id.tv_merchant_price)
        payerPrice = findViewById(R.id.tv_payer_price)
        markupCommission = findViewById(R.id.tv_exchange_rate_explanation)
        exchangeRate = findViewById(R.id.tv_exchange_rate)
        swDccRate = findViewById(R.id.sw_dcc_rate)
        ivHelp = findViewById(R.id.iv_tooltip)


        swDccRate.setOnCheckedChangeListener { _, isChecked ->
            onDccRateSelected.invoke(isChecked)
            merchantPrice.setTypeface(null, if (isChecked) NORMAL else BOLD)
            payerPrice.setTypeface(null, if (isChecked) BOLD else NORMAL)
        }

        ivHelp.setOnClickListener { showExchangeRateDescription() }

        attrs?.let { initAttrs(it) }
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DccRateView)

        val titleColor = typedArray.getColor(R.styleable.DccRateView_titleColor, Color.BLACK)
        val titleSize = typedArray.getDimension(R.styleable.DccRateView_titleSize, 40f)

        val priceColor = typedArray.getColor(R.styleable.DccRateView_priceColor, Color.BLACK)
        val priceSize = typedArray.getDimension(R.styleable.DccRateView_priceSize, 46f)

        val textColor = typedArray.getColor(R.styleable.DccRateView_textColor, Color.GRAY)
        val textSize = typedArray.getDimension(R.styleable.DccRateView_textSize, 32f)

        val switchColor = typedArray.getColor(R.styleable.DccRateView_switchColor, Color.BLUE)

        title.apply {
            setTextColor(titleColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
        }
        merchantPrice.apply {
            setTextColor(priceColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, priceSize)
        }
        payerPrice.apply {
            setTextColor(priceColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, priceSize)
        }
        markupCommission.apply {
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
        exchangeRate.apply {
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
        swDccRate.thumbTintList = ColorStateList.valueOf(switchColor)

        typedArray.recycle()
    }

    fun fillViews(dccRate: DccRateModel) {
        this.merchantPrice.text = dccRate.getMerchantPrice()
        this.payerPrice.text = dccRate.getPayerPrice()
        this.exchangeRate.text = resources.getString(
            R.string.exchange_rate_used,
            dccRate.getMerchantRate(),
            dccRate.getPayerRate()
        )
        this.markupCommission.text =
            resources.getString(
                R.string.exchange_rate_mark_up_commission,
                dccRate.marginRatePercentage,
                dccRate.commissionPercentage
            )

        this.dccRate = dccRate
    }

    private fun showExchangeRateDescription() {
        val dccRate = this.dccRate ?: return
        val description = if (swDccRate.isChecked) {
            resources.getString(
                R.string.tooltip_dcc_checked,
                dccRate.merchantCurrency,
                dccRate.exchangeRateSource
            )
        } else {
            resources.getString(R.string.tooltip_dcc_unchecked, dccRate.exchangeRateSource)
        }
        AlertDialog.Builder(context)
            .setMessage(description)
            .setCancelable(true)
            .show()
    }
}

private fun DccRateModel.getMerchantPrice() = "$merchantAmount$merchantCurrency"
private fun DccRateModel.getPayerPrice() = "$payerAmount$payerCurrency"
private fun DccRateModel.getMerchantRate() = "1$merchantCurrency"
private fun DccRateModel.getPayerRate() = "$exchangeRate$payerCurrency"