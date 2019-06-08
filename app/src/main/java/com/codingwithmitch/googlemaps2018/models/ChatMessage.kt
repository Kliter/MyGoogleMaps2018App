package com.codingwithmitch.googlemaps2018.models

import com.google.firebase.Timestamp


class ChatMessage() {
    var user: User? = null
    var message: String? = null
    var message_id: String? = null
    var timestamp: Timestamp? = null

    constructor(user: User, message: String, message_id: String, timestamp: Timestamp): this() {
        this.user = user
        this.message = message
        this.message_id = message_id
        this.timestamp = timestamp
    }
}