package com.example.taldea5

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(
    val erabiltzailea: String,
    val pasahitza: String
)

data class MahaiaLoginResponse(
    val id: Int,
    val izena: String? = null,
    val erabiltzailea: String? = null,
    val chatBaimena: Boolean? = null
)

val MahaiaLoginResponse.displayName: String
    get() {
        return when {
            !izena.isNullOrBlank() -> izena
            !erabiltzailea.isNullOrBlank() -> erabiltzailea
            else -> "Mahaia"
        }
    }

data class ProduktuaDto(
    val id: Int,
    val izena: String?,
    val prezioa: Float?,
    val stock: Int? = null,
    val irudiaPath: String? = null,
    val produktuenMotakId: Int
)

data class EskaeraLineRequest(
    val produktuaId: Int,
    val izena: String?,
    val prezioa: Double?,
    val data: String,
    val egoera: Int,
    val isPlatera: Boolean = false
)

data class ZerbitzuaCreateRequest(
    val prezioTotala: Double?,
    val data: String?,
    val erreserbaId: Int?,
    val mahaiakId: Int?,
    val eskaerak: List<EskaeraLineRequest>
)

data class ZerbitzuaResponse(
    val id: Int
)

data class PlateraOsagaiaDto(
    val id: Int,
    val izena: String?,
    val stock: Int?
)

data class PlateraDto(
    val id: Int,
    val izena: String?,
    val mota: String?,
    val prezioa: Float?,
    val argazkia: String?,
    val argazkiaUrl: String?,
    val osagaiak: List<PlateraOsagaiaDto>? = null
)

data class ErantzunaDto<T>(
    val code: Int,
    val message: String?,
    val datuak: List<T>?
)

data class ChatBaimenaResponse(
    val chatBaimena: Boolean
)

interface ApiService {

    @POST("api/mahaiak/login")
    suspend fun login(@Body body: LoginRequest): Response<MahaiaLoginResponse>

    @GET("api/mahaiak/{id}/txat-baimena")
    suspend fun checkChatBaimena(@Path("id") id: Int): Response<ChatBaimenaResponse>

    @GET("api/produktuak")
    suspend fun getProduktuak(): Response<List<ProduktuaDto>>

    @GET("api/platerak")
    suspend fun getPlaterak(): Response<ErantzunaDto<PlateraDto>>

    @POST("api/zerbitzua")
    suspend fun createZerbitzua(@Body body: ZerbitzuaCreateRequest): Response<ZerbitzuaResponse>

    @POST("api/fakturak/{zerbitzuaId}/sortu")
    suspend fun createFaktura(@Path("zerbitzuaId") zerbitzuaId: Int): Response<Unit>
}
