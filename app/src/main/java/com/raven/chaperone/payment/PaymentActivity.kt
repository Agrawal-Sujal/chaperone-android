package com.raven.chaperone.payment

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.raven.chaperone.domain.model.payment.VerifyOrderRequest
import com.raven.chaperone.services.remote.PaymentServices
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import kotlin.jvm.java

class PaymentActivity : Activity(), PaymentResultWithDataListener {

    @Inject
    lateinit var paymentServices: PaymentServices
    lateinit var orderId: String

    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("id",-1)
        val orderId = intent.getStringExtra("order_id")
        val key = intent.getStringExtra("key")
        val amount = intent.getIntExtra("amount", 0)
        val currency = intent.getStringExtra("currency") ?: "INR"
        this.orderId = orderId ?: ""
        this.id = id.toString()
        val checkout = Checkout()
        checkout.setKeyID(key)
        try {
            val options = JSONObject()
            options.put("name", "Chaperone")
            options.put("description", "Purchase Credits")
            options.put("order_id", orderId)
            options.put("currency", currency)
            options.put("amount", amount)

            checkout.open(this, options)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toast.makeText(this, "Payment Success: $p0", Toast.LENGTH_LONG).show()
        Log.d("TAG", (p0 ?: "") + p1.toString())

        val signature = p1?.signature ?: ""
        val payment_id = p1?.paymentId ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    paymentServices.verifyOrder(VerifyOrderRequest(orderId, payment_id, signature,id.toInt()))
                Log.d("TAG", response.body().toString())
            } catch (e: Exception) {

            }

        }
        val resultIntent = Intent()
        resultIntent.putExtra("payment_id", id)
        setResult(RESULT_OK)
        finish()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this, "Payment Failed: $p1", Toast.LENGTH_LONG).show()
        val resultIntent = Intent()
        resultIntent.putExtra("error", "Payment Failed: $p1")
        setResult(RESULT_CANCELED,resultIntent)
        finish()
    }

}
