package com.blennster.mousemover

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex() : String{
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs = getSharedPreferences("conf", Context.MODE_PRIVATE)

        hostIp.setText(prefs.getString("hostIP", ""))
        hostPort.setText(prefs.getInt("hostPort", 0).toString())

        button.setOnClickListener { handleClick() }
    }

    fun handleClick() {
        val intent = Intent(this, TrackpadActivity::class.java)
        val ip = hostIp.text.toString()
        val port = hostPort.text.toString().toInt()
        val tcpData = TCPData(ip, port)
        intent.putExtra("conf", tcpData)

        val prefs = getSharedPreferences("conf", Context.MODE_PRIVATE).edit()
        prefs.putString("hostIP", ip)
        prefs.putInt("hostPort", port)
        prefs.apply()

        startActivity(intent)
    }
}
