package com.codingwithmitch.googlemaps2018.models

import android.os.Parcel
import android.os.Parcelable

class User(): Parcelable {

    var email: String? = null
    var user_id: String? = null
    var username: String? = null
    var avatar: String? = null

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object: Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User {
                return User(source)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(source: Parcel): this() {
        this.email = source.readString()
        this.user_id = source.readString()
        this.username = source.readString()
        this.avatar = source.readString()
    }

    constructor(email: String, user_id: String, username: String, avatar: String): this() {
        this.email = email
        this.user_id = user_id
        this.username = username
        this.avatar = avatar
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(email)
        dest?.writeString(user_id)
        dest?.writeString(username)
        dest?.writeString(avatar)
    }

    override fun describeContents(): Int {
        return 0
    }
}