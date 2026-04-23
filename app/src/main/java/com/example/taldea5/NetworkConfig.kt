package com.example.taldea5

object NetworkConfig {
    // Para el emulador, usa "10.0.2.2"
    // Para tablet fisica por Wi-Fi o red local, pon la IP de tu ordenador (ej. "192.168.1.XX" o "172.16.242.198")
    private const val PC_HOST = "172.16.243.20"

    val apiBaseUrl: String
        get() = "http://$PC_HOST:5005/"

    val chatHost: String
        get() = PC_HOST
}
