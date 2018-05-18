package com.blennster.mousemover

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.net.Socket

/**
 * Created by Emil Blennow on 2018-03-26.
 */

@Parcelize
data class TCPData (val ip:String, val port:Int): Parcelable

class TCPClient(IP:String, port:Int) {

    private val sock = Socket(IP, port)

    fun Read():ByteArray {
        val buffer = ByteArray(8)
        sock.getInputStream().read(buffer)
        return buffer
    }

    fun Write(bytes:ByteArray) {
        sock.getOutputStream().write(bytes)
        sock.getOutputStream().flush()
    }

    fun Close() {
        sock?.close()
    }
}