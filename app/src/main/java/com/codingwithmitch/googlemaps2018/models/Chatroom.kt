package com.codingwithmitch.googlemaps2018.models

import android.os.Parcel
import android.os.Parcelable

class Chatroom(): Parcelable {

    var title: String? = null
    var chatroom_id: String? = null

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Chatroom> = object : Parcelable.Creator<Chatroom> {
            override fun createFromParcel(source: Parcel?): Chatroom {
                return Chatroom(source!!)
            }

            override fun newArray(size: Int): Array<Chatroom?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(source: Parcel): this() {
        title = source.readString()
        chatroom_id = source.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(chatroom_id)
    }

    override fun describeContents(): Int {
        return 0
    }
}