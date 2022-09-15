package com.globalpayments.android.sdk.ui.cardform

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.widget.*
import com.global.api.entities.Transaction
import com.global.api.paymentMethods.CreditCardData
import com.global.api.utils.CardUtils
import com.globalpayments.android.sdk.R
import com.globalpayments.android.sdk.model.DccRateModel
import com.globalpayments.android.sdk.model.FormValid
import com.globalpayments.android.sdk.utils.EndTextWatcher
import com.globalpayments.android.sdk.utils.PercentageFormatter
import java.math.BigDecimal
import java.util.*
import kotlin.properties.Delegates

class CardFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val cardNumberLabel: TextView
    private val cardNumberInput: EditText

    private val cardExpiryDateLabel: TextView
    private val cardExpiryDateInput: EditText
    private val cardExpiryDateError: TextView

    private val cardSecurityLabel: TextView
    private val cardSecurityInput: EditText
    private val cardHolderLabel: TextView
    private val cardHolderInput: EditText
    private val submitButton: Button

    private val cardError: TextView
    private val cardLogo: ImageView

    private val dccRateView: DccRateView
    private val pbLoading: ProgressBar

    private val currentDateCalendar = Calendar.getInstance()

    private val creditCardData = CreditCardData()
    private var formValid: FormValid by Delegates.observable(FormValid()) { _, _, _ ->
        checkFieldsValidity()
        tryToGetDccRates()
    }

    private var lastKnownValidCardNumber: String = ""

    var onCheckDccRate: (CreditCardData) -> Unit = {}
    var onSubmitClicked: (CreditCardData) -> Unit = {}

    init {
        inflate(context, R.layout.view_card_form, this)

        orientation = VERTICAL
        val padding = context.resources.getDimensionPixelSize(R.dimen.input_padding)
        setPadding(padding, padding, padding, padding)

        cardNumberLabel = findViewById(R.id.card_number_label)
        cardNumberInput = findViewById(R.id.card_number_input)

        cardExpiryDateLabel = findViewById(R.id.card_expiry_date_label)
        cardExpiryDateInput = findViewById(R.id.card_expiry_date_input)
        cardExpiryDateError = findViewById(R.id.card_expiry_error)

        cardSecurityLabel = findViewById(R.id.card_security_code_label)
        cardSecurityInput = findViewById(R.id.card_security_code_input)
        cardHolderLabel = findViewById(R.id.card_holder_label)
        cardHolderInput = findViewById(R.id.card_holder_input)
        submitButton = findViewById(R.id.submit_button)

        cardError = findViewById(R.id.card_number_error)
        cardLogo = findViewById(R.id.card_logo)

        dccRateView = findViewById(R.id.dcc_rate_view)
        pbLoading = findViewById(R.id.pb_loading)

        if (attrs != null) {
            initAttributes(attrs)
        }
    }

    private fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardFormView)

        val background = typedArray.getDrawable(R.styleable.CardFormView_formBackground)
        setBackground(background)

        val labelColor = typedArray.getColor(R.styleable.CardFormView_labelTextColor, Color.BLACK)
        val labelTextSize = typedArray.getDimension(R.styleable.CardFormView_labelTextSize, 18f)

        val hintColor = typedArray.getColor(R.styleable.CardFormView_inputHintColor, Color.GRAY)
        val inputColor = typedArray.getColor(R.styleable.CardFormView_inputTextColor, Color.BLACK)
        val inputTextSize = typedArray.getDimension(R.styleable.CardFormView_inputTextSize, 16f)

        val inputBackground = typedArray.getDrawable(R.styleable.CardFormView_inputBackground)

        val buttonBackground =
            typedArray.getDrawable(R.styleable.CardFormView_submitButtonBackground)
        val buttonText = typedArray.getString(R.styleable.CardFormView_submitButtonText)
        val buttonTextColor =
            typedArray.getColor(R.styleable.CardFormView_submitButtonTextColor, Color.WHITE)
        val buttonTextSize =
            typedArray.getDimension(R.styleable.CardFormView_submitButtonTextSize, 18f)

        cardNumberLabel.apply {
            setTextSize(COMPLEX_UNIT_PX, labelTextSize)
            setTextColor(labelColor)
        }
        cardNumberInput.apply {
            setBackground(inputBackground)
            setTextSize(COMPLEX_UNIT_PX, inputTextSize)
            setTextColor(inputColor)
            setHintTextColor(hintColor)
            addTextChangedListener(cardTypeTextWatcher())
        }

        cardExpiryDateLabel.apply {
            setTextSize(COMPLEX_UNIT_PX, labelTextSize)
            setTextColor(labelColor)
        }
        cardExpiryDateInput.apply {
            setBackground(inputBackground)
            setTextSize(COMPLEX_UNIT_PX, inputTextSize)
            setTextColor(inputColor)
            setHintTextColor(hintColor)
            addTextChangedListener(cardExpiryTextWatcher())
        }

        cardSecurityLabel.apply {
            setTextSize(COMPLEX_UNIT_PX, labelTextSize)
            setTextColor(labelColor)
        }
        cardSecurityInput.apply {
            setBackground(inputBackground)
            setTextSize(COMPLEX_UNIT_PX, inputTextSize)
            setTextColor(inputColor)
            setHintTextColor(hintColor)
            addTextChangedListener(cardCvvValid())
        }

        cardHolderLabel.apply {
            setTextSize(COMPLEX_UNIT_PX, labelTextSize)
            setTextColor(labelColor)
        }
        cardHolderInput.apply {
            setBackground(inputBackground)
            setTextSize(COMPLEX_UNIT_PX, inputTextSize)
            setTextColor(inputColor)
            setHintTextColor(hintColor)
            addTextChangedListener(cardHolderValid())
        }

        submitButton.apply {
            setBackground(buttonBackground)
            setTextColor(buttonTextColor)
            setTextSize(COMPLEX_UNIT_PX, buttonTextSize)
            text = buttonText
            setOnClickListener { onSubmitClicked.invoke(creditCardData) }
        }

        typedArray.recycle()
    }

    var onDccRateSelected: (Boolean) -> Unit
        set(value) {
            dccRateView.onDccRateSelected = value
        }
        get() = dccRateView.onDccRateSelected


    private fun checkFieldsValidity() {
        submitButton.isEnabled = formValid.cardValid
    }

    private fun showCard(cardType: CardType) {
        val cardLogo = when (cardType) {
            CardType.Visa -> R.drawable.ic_visa_card
            CardType.MasterCard -> R.drawable.ic_mastercard
            CardType.AmericanExpress -> R.drawable.ic_americanexpress
            CardType.DinersClub -> R.drawable.ic_dinersclub
            CardType.Discover -> R.drawable.ic_discover
            CardType.Unknown -> NO_ID
        }
        val filters = cardSecurityInput
            .filters
            .filter { it !is LengthFilter } + LengthFilter(if (cardType == CardType.AmericanExpress) 4 else 3)
        cardSecurityInput.filters = filters.toTypedArray()
        cardSecurityInput.hint = if (cardType == CardType.AmericanExpress) "****" else "***"

        if (cardLogo == NO_ID) {
            formValid = formValid.copy(numberValid = false)
            showCardError()
            return
        }
        formValid = formValid.copy(numberValid = true)
        this.cardLogo.apply {
            visibility = View.VISIBLE
            setImageResource(cardLogo)
        }
    }

    private fun showCardError() {
        cardLogo.visibility = View.GONE
        cardError.visibility = View.VISIBLE
    }

    private fun clearCardNumberError() {
        cardError.visibility = View.INVISIBLE
        cardLogo.visibility = View.GONE
    }

    private fun cardExpiryTextWatcher(): TextWatcher {
        return object : EndTextWatcher() {
            override fun textChanged(s: Editable) {
                var text = s.toString().replace("[^\\d]".toRegex(), "")
                if (text.length > 2) {
                    text = text.substring(0, 2) + "/" + text.substring(2)
                }
                if (!text.equals(s.toString(), ignoreCase = true)) {
                    cardExpiryDateInput.setText(text)
                }
                if (text.length != s.length) {
                    cardExpiryDateInput.setSelection(text.length)
                }
                if (text.length != 7) {
                    cardExpiryDateError.visibility = View.GONE
                    formValid = formValid.copy(dateValid = false)
                    return
                }
                val month = text.substring(0, 2).toIntOrNull()
                val year = text.substring(3, text.length).toIntOrNull()

                if (month == null || year == null) {
                    formValid = formValid.copy(dateValid = false)
                    return
                }

                val currentMonth = currentDateCalendar.get(Calendar.MONTH)
                val currentYear = currentDateCalendar.get(Calendar.YEAR)
                val isDateInvalid = year < currentYear
                        || (year == currentYear && month < currentMonth)
                        || month > 12
                formValid = formValid.copy(dateValid = !isDateInvalid)
                cardExpiryDateError.visibility = if (isDateInvalid) View.VISIBLE else View.GONE
                if (!isDateInvalid) {
                    creditCardData.apply {
                        expMonth = month
                        expYear = year
                    }
                }
            }
        }
    }

    private fun cardTypeTextWatcher(): TextWatcher {
        return object : EndTextWatcher() {
            override fun textChanged(s: Editable) {
                val cardNumber = s.toString()
                if (cardNumber.length < 16) {
                    clearCardNumberError()
                    return
                }
                creditCardData.number = cardNumber
                when (CardUtils.mapCardType(cardNumber)) {
                    "Visa" -> showCard(CardType.Visa)
                    "MC" -> showCard(CardType.MasterCard)
                    "Amex" -> showCard(CardType.AmericanExpress)
                    "DinersClub" -> showCard(CardType.DinersClub)
                    "Discover" -> showCard(CardType.Discover)
                    else -> showCard(CardType.Unknown)
                }
            }
        }
    }

    private fun tryToGetDccRates() {
        if (!formValid.cardValid) {
            dccRateView.visibility = View.GONE
            lastKnownValidCardNumber = ""
            return
        }
        if (creditCardData.number == lastKnownValidCardNumber) return
        lastKnownValidCardNumber = creditCardData.number

        post {
            pbLoading.apply {
                visibility = View.VISIBLE
                isIndeterminate = true
            }
        }
        onCheckDccRate.invoke(creditCardData)
    }

    fun onDccRateReceived(transaction: Transaction?) {
        val dccRate = transaction?.toDccRateModel()
        dccRateView.visibility = if (dccRate != null) View.VISIBLE else View.GONE
        postDelayed({ pbLoading.visibility = View.GONE }, 1_000)
        dccRate ?: return
        dccRateView.fillViews(dccRate)
    }

    private fun cardCvvValid(): TextWatcher {
        return object : EndTextWatcher() {
            override fun textChanged(s: Editable) {
                formValid = formValid.copy(cvvValid = s.length >= 3)
                creditCardData.cvn = s.toString()
            }
        }
    }

    private fun cardHolderValid(): TextWatcher {
        return object : EndTextWatcher() {
            override fun textChanged(s: Editable) {
                formValid = formValid.copy(holderValid = s.length >= 3)
                creditCardData.cardHolderName = s.toString()
            }
        }
    }

    private enum class CardType {
        Visa, MasterCard, AmericanExpress, DinersClub, Discover, Unknown
    }
}

private fun Transaction.toDccRateModel() = with(dccRateData) {
    if (this == null) null else
        DccRateModel(
            payerCurrency = this.cardHolderCurrency,
            merchantCurrency = this.merchantCurrency,
            payerAmount = this.cardHolderAmount?.toString() ?: "",
            merchantAmount = this.merchantAmount?.toString() ?: "",
            exchangeRate = cardHolderRate,
            marginRatePercentage = BigDecimal(marginRatePercentage).let {
                PercentageFormatter.format(
                    it
                )
            },
            commissionPercentage = BigDecimal(commissionPercentage).let {
                PercentageFormatter.format(
                    it
                )
            },
            exchangeRateSource = exchangeRateSourceName
        )
}