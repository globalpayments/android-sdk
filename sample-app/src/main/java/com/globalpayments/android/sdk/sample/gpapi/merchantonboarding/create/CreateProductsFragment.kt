package com.globalpayments.android.sdk.sample.gpapi.merchantonboarding.create

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.common.base.BaseFragment
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar
import com.globalpayments.android.sdk.utils.bindView
import kotlinx.parcelize.Parcelize
import kotlin.properties.Delegates


@Parcelize
data class Product(
    val productId: String, val quantity: Int
) : Parcelable {
    override fun toString(): String {
        return "$productId|$quantity"
    }
}

class CreateProductsFragment : BaseFragment() {

    private val toolbar: CustomToolbar by bindView(R.id.toolbar)

    private val etProductId: EditText by bindView(R.id.et_product_id)
    private val etProductQuantity: EditText by bindView(R.id.et_quantity)
    private val btnAdd: Button by bindView(R.id.btn_add_product)
    private val rvProducts: RecyclerView by bindView(R.id.rv_products)
    private val btnFinish: Button by bindView(R.id.btn_finish)

    private val productsAdapter = ProductsAdapter()

    override fun getLayoutId(): Int = R.layout.fragment_create_products

    override fun initViews() {
        super.initViews()
        rvProducts.adapter = productsAdapter
        productsAdapter.items = requireArguments().getParcelableArrayList(ProductsKey) ?: listOf(
            //This product is MANDATORY
            Product(
                productId = "PRO_FMA_PUSH-FUNDS_PP", quantity = 1
            )
        )

        toolbar.setTitle(R.string.products)
        toolbar.setOnBackButtonListener { close() }

        btnAdd.setOnClickListener { addProductToList() }
        btnFinish.setOnClickListener { sendProducts() }

        initWithModel()
    }

    private fun initWithModel() {
        val items = arguments?.getParcelableArrayList<Product>(ProductsKey)?.takeIf { it.isNotEmpty() } ?: return
        productsAdapter.items = items
    }

    private fun addProductToList() {
        val productId = etProductId.text.toString()
        val productQuantity = etProductQuantity.text.toString()
        if (productId.isBlank() || productQuantity.isBlank()) return
        val product = Product(productId, productQuantity.toInt())
        etProductId.setText("")
        etProductQuantity.setText("")
        productsAdapter.items = productsAdapter.items + product
    }

    private fun sendProducts() {
        val products = productsAdapter.items
        setFragmentResult(ProductResultKey, bundleOf(ProductsKey to products))
        close()
    }

    companion object {
        const val ProductResultKey = "products_result_key"
        const val ProductsKey = "products_key"
        fun newInstance(items: List<Product>): CreateProductsFragment {
            return CreateProductsFragment().apply {
                arguments = bundleOf(ProductsKey to items)
            }
        }
    }
}

class ProductsAdapter : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    var items: List<Product> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position]) {
            items = items - it
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvProduct = view.findViewById<TextView>(R.id.tv_product)
        private val ivDelete = view.findViewById<ImageView>(R.id.iv_delete)

        fun bindItem(product: Product, onDeleteClick: (Product) -> Unit) {
            tvProduct.text = "${product.productId} : ${product.quantity}"
            ivDelete.isVisible = product.productId != "PRO_FMA_PUSH-FUNDS_PP"
            ivDelete.setOnClickListener {
                onDeleteClick(product)
            }
        }
    }
}



