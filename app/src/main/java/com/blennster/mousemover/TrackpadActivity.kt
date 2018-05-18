package com.blennster.mousemover

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_trackpad.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.concurrent.thread

const val MOVE_MOUSE:Byte = 0x00
const val TOUCH_START:Byte = 0x01
const val LEFT_DOWN:Byte = 0x03
const val LEFT_UP:Byte = 0x04
const val RIGHT_CLICK:Byte = 0x05

class TrackpadActivity : AppCompatActivity() {

    @Volatile var client:TCPClient? = null
    @Volatile var w = 1
    @Volatile var h = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trackpad)

        btnLeft.setOnTouchListener { _, motionEvent ->
            val buffer = ByteArray(9)

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    buffer[0] = LEFT_DOWN
                    thread { client?.Write(buffer) }
                }
                MotionEvent.ACTION_UP -> {
                    buffer[0] = LEFT_UP
                    thread { client?.Write(buffer) }
                }

            }
            true
        }

        btnRight.setOnClickListener {
            val buffer = ByteArray(9)
            buffer[0] = RIGHT_CLICK
            thread { client?.Write(buffer) }
        }
        trackArea.setOnTouchListener { view, motionEvent -> onTrackTouch(view, motionEvent) }

        val data = intent.extras["conf"] as TCPData
        thread {
            client = TCPClient(data.ip, data.port)
            val size = client?.Read()
            val buff = ByteBuffer.wrap(size).order(ByteOrder.LITTLE_ENDIAN)
            w = buff.int
            h = buff.int

            Log.i("INFO", "$w:$h")
        }
    }

    fun handleClick(str:String) {
        val buffer = ByteArray(1)

        when (str) {
            "right" -> buffer[0] = RIGHT_CLICK
        }
        thread { client?.Write(buffer) }
    }

    fun onTrackTouch(view: View, event: MotionEvent): Boolean {
        val buffer = ByteArray(9)
        buffer[0] = MOVE_MOUSE
        val buff = ByteBuffer.wrap(buffer, 1, 8)
        buff.order(ByteOrder.LITTLE_ENDIAN)
        val x = view.x - event.x
        val y = view.y - event.y
        buff.putInt(x.toInt())
        buff.putInt(y.toInt())
        buff.position(0)

        if (event.action == MotionEvent.ACTION_DOWN)  buffer[0] = TOUCH_START

        Log.i("INFO", "Sending data: ${"0x%02X".format(buffer[0])};${buff.int};${buff.int}")
        thread {
            client?.Write(buffer)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        client?.Close()
    }
}
