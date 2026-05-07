package com.example.taldea5

object NetworkConfig {
    private const val PC_HOST = "192.168.10.5"

    val apiBaseUrl: String
        get() = "http://$PC_HOST:5093/"

    val chatHost: String
        get() = PC_HOST
}
