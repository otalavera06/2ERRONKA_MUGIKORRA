package com.example.taldea5

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taldea5.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class LocalInfo(
    val short: String, 
    val ingredients: String,
    @DrawableRes val imageRes: Int = R.drawable.shusinelli
)
private fun normalizeName(s: String): String {
    val temp = Normalizer.normalize(s, Normalizer.Form.NFD)
    return temp.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .trim()
        .lowercase(Locale.getDefault())
        .replace(Regex("\\s+"), " ")
}

private fun localInfoMap(): Map<String, LocalInfo> = mapOf(
    normalizeName("Edamame Sushinelli") to LocalInfo(
        short = "Lurrunetan egositako edamamea, itsas gatzarekin eta sesamo-olio txigortuarekin.",
        ingredients = "Edamame, itsas gatza, sesamo-olio txigortua.",
        imageRes = R.drawable.edamame_sushinelli
    ),
    normalizeName("Edamame Trufa Blanca") to LocalInfo(
        short = "Edamame salteatua, noisette gurinarekin eta boilur-esentziarekin.",
        ingredients = "Edamame, noisette gurina, boilur-esentzia.",
        imageRes = R.drawable.edamame_trufa
    ),
    normalizeName("Gyozas Black Angus") to LocalInfo(
        short = "Black Angus txahalkiaz betetako gyoza artisauak, zuku murriztuarekin eta tipulinarekin.",
        ingredients = "Black Angus txahalkia, zuku murriztua, tipulina.",
        imageRes = R.drawable.gyozas_black_angus
    ),
    normalizeName("Tartar de Salmón Yuzu") to LocalInfo(
        short = "Labanaz moztutako izokin premium-a, yuzu freskoa, aguakatea eta sesamo-olioa.",
        ingredients = "Izokin premium-a, yuzu freskoa, aguakatea, sesamo-olioa.",
        imageRes = R.drawable.salmon_yazu
    ),

    normalizeName("Sashimi Selection Sushinelli") to LocalInfo(
        short = "Eguneko arrain premium aukeraketa (merkatuaren arabera).",
        ingredients = "Arrain premium-ak (askotarikoak)."
    ),
    normalizeName("Carpaccio Hamachi Citrus") to LocalInfo(
        short = "Hamachi ijeztua, ozpin-olio zitriko japoniarra eta gatz-ezkatak.",
        ingredients = "Hamachi, ozpin-olio zitriko japoniarra, gatz-ezkatak.",
        imageRes = R.drawable.carpaccio
    ),
    normalizeName("Usuzukuri de Lubina Mediterránea") to LocalInfo(
        short = "Lupia freskoa, ponzu leuna eta daikon errefaua.",
        ingredients = "Lupia freskoa, ponzu leuna, daikon errefaua.",
        imageRes = R.drawable.usuzukuri
    ),

    normalizeName("Nigiri Salmón Imperial") to LocalInfo(
        short = "Norvegiako izokin ondua, arroz epela eta wasabi freskoa.",
        ingredients = "Norvegiako izokina, arroza, wasabi freskoa.",
        imageRes = R.drawable.salmon_nigiri
    ),
    normalizeName("Nigiri Toro Sushinelli") to LocalInfo(
        short = "Hegaluzearen sabelaldea, mozketa premium-a.",
        ingredients = "Hegaluzearen sabelaldea, arroza.",
        imageRes = R.drawable.tuna_nigiri
    ),
    normalizeName("Nigiri Wagyu A5 Aburi") to LocalInfo(
        short = "A5 Wagyu japoniarra, Maldon gatzarekin arinki sugarretan errea.",
        ingredients = "A5 Wagyu japoniarra, Maldon gatza, arroza.",
        imageRes = R.drawable.unagi_nigiri

    ),
    normalizeName("Nigiri Vieira Miso") to LocalInfo(
        short = "Bieira zigilatua, miso zuriarekin eta lima ukituarekin.",
        ingredients = "Bieira, miso zuria, lima, arroza.",
        imageRes = R.drawable.scallop_nigiri
    ),
    normalizeName("Nigiri Berenjena Miso") to LocalInfo(
        short = "Berenjena errea miso gozoarekin.",
        ingredients = "Berenjena, miso gozoa, arroza.",
        imageRes = R.drawable.sea_bream_nigiri
    ),
    normalizeName("Inari Sushi") to LocalInfo(
        short = "Tofu frijituzko zakuak arrozarekin.",
        ingredients = "Tofu frijitua, arroza, sesamoa.",
        imageRes = R.drawable.inari_sushi
    ),
    normalizeName("Nigiri Saba") to LocalInfo(
        short = "Berdela ozpindua arroz gainean.",
        ingredients = "Berdela (Saba), arroza, wasabia.",
        imageRes = R.drawable.saba_nigiri
    ),
    normalizeName("Nigiri Pulpo") to LocalInfo(
        short = "Olagarro egosia arroz gainean.",
        ingredients = "Olagarroa (Tako), arroza, wasabia, nori zerrenda.",
        imageRes = R.drawable.octopus_nigiri
    ),

    normalizeName("California Gold Roll") to LocalInfo(
        short = "Errege karramarroa, aguakatea, pepinoa eta urre-koloreko tobiko-a.",
        ingredients = "Errege karramarroa, aguakatea, pepinoa, urre-koloreko tobiko-a, arroza, nori.",
        imageRes = R.drawable.california_roll
    ),
    normalizeName("Philadelphia Deluxe") to LocalInfo(
        short = "Izokin premium-a, gazta-krema arina eta tipulina.",
        ingredients = "Izokin premium-a, gazta-krema, tipulina, arroza, nori.",
        imageRes = R.drawable.philadelphia_roll
    ),
    normalizeName("Philadelphia Roll") to LocalInfo(
        short = "Izokin premium-a, gazta-krema arina eta tipulina.",
        ingredients = "Izokin premium-a, gazta-krema, tipulina, arroza, nori.",
        imageRes = R.drawable.philadelphia_roll
    ),
    normalizeName("Tekka Maki Tradicional") to LocalInfo(
        short = "Hegaluzea, Sushinelli arroza eta nori kurruskaria.",
        ingredients = "Hegaluzea, Sushinelli arroza, nori.",
        imageRes = R.drawable.tekka_maki
    ),

    normalizeName("Sushinelli Truffle Roll") to LocalInfo(
        short = "Atuna, aguakatea, izokin aburi topping-a eta boilur beltza.",
        ingredients = "Atuna, aguakatea, izokin aburi, boilur beltza, arroza, nori.",
        imageRes = R.drawable.unagi_roll

    ),
    normalizeName("Crispy Dragon Roll") to LocalInfo(
        short = "Otarrainxka tenpura, aguakatea, aingira glaseatua eta kabayaki saltsa.",
        ingredients = "Otarrainxka tenpura, aguakatea, aingira, kabayaki saltsa, arroza, nori.",
        imageRes = R.drawable.dragon_roll
    ),
    normalizeName("Volcano Sushinelli") to LocalInfo(
        short = "Atun pikantea, arroz kurruskaria eta chef-aren topping sugarretan errea.",
        ingredients = "Atun pikantea, arroz kurruskaria, topping sugarretan errea.",
        imageRes = R.drawable.scallop_nigiri


    ),
    normalizeName("Mediterranean Roll") to LocalInfo(
        short = "Lupia tenpura, tomate lehorra, albahaka japoniarra eta yuzu maionesa.",
        ingredients = "Lupia tenpura, tomate lehorra, albahaka japoniarra, yuzu maionesa, arroza, nori.",
        imageRes = R.drawable.mediterranean_roll
    ),

    normalizeName("Chirashi Premium Sushinelli") to LocalInfo(
        short = "Sushi arroza, askotariko sashimiarekin eta arrabekin.",
        ingredients = "Sushi arroza, askotariko sashimia, arrabak."
    ),
    normalizeName("Donburi Black Cod Miso") to LocalInfo(
        short = "Bakailao beltza, miso gozoan glaseatua, arroza eta ozpinetakoak.",
        ingredients = "Bakailao beltza, miso gozoa, arroza, ozpinetakoak.",
        imageRes = R.drawable.donburi_black_cod_miso
    ),
    normalizeName("Spicy Tuna Bowl") to LocalInfo(
        short = "Atun marinatu pikantea, aguakatea eta sesamoa.",
        ingredients = "Atun marinatu pikantea, aguakatea, sesamoa, arroza.",
        imageRes = R.drawable.spicy_tuna_bowl
    ),

    normalizeName("Tempura Royal de Langostinos") to LocalInfo(
        short = "Otarrainxka kurruskariak tentsuyu saltsarekin.",
        ingredients = "Otarrainxkak, tentsuyu saltsa."
    ),
    normalizeName("Bao Wagyu Teriyaki") to LocalInfo(
        short = "Lurrunetan egositako bao-a, wagyuz betea, teriyaki saltsarekin eta pepino ozpinduarekin.",
        ingredients = "Bao, wagyu, teriyaki saltsa, pepino ozpindua.",
        imageRes = R.drawable.bao_wagyu
    ),
    normalizeName("Yakisoba Sushinelli") to LocalInfo(
        short = "Fideo salteatuak barazki japoniarrekin eta aukerako proteinarekin.",
        ingredients = "Yakisoba fideoak, barazki japoniarrak, proteina."
    ),

    normalizeName("Green Dragon Roll") to LocalInfo(
        short = "Aguakatea, pepinoa, zainzuria eta sesamo zuriko maionesa.",
        ingredients = "Aguakatea, pepinoa, zainzuria, sesamo zuriko maionesa, arroza, nori.",
        imageRes = R.drawable.dragon_roll
    ),
    normalizeName("Vegan Chirashi") to LocalInfo(
        short = "Sushi arroza, barazki marinatuak eta tofu kurruskaria.",
        ingredients = "Sushi arroza, barazki marinatuak, tofu kurruskaria.",
        imageRes = R.drawable.veggie_roll


    ),

    normalizeName("Omakase Sushinelli") to LocalInfo(
        short = "Chef-aren nigiri eta roll aukeraketa, eguneko produktuaren arabera. 8 edo 12 plateretan eskuragarri.",
        ingredients = "Chef-aren aukeraketa."
    ),



    normalizeName("Mochi Artesano del Día") to LocalInfo(
        short = "Zapore txandakatuak, chef-ak aukeratuak.",
        ingredients = "Mochi (zapore txandakatuak).",
        imageRes = R.drawable.izozkia
    ),
    normalizeName("Cheesecake Matcha Sushinelli") to LocalInfo(
        short = "Testura krematsua sesamo kurruskaridun oinarriarekin.",
        ingredients = "Gazta-krema, matcha, sesamo oinarria.",
        imageRes = R.drawable.cheskake
    ),

    normalizeName("Bezoya") to LocalInfo(
        short = "Ur mineral naturala.",
        ingredients = "Ura.",
        imageRes = R.drawable.agua_bezoya
    ),
    normalizeName("Aquarius") to LocalInfo(
        short = "Edari isotonikoa.",
        ingredients = "Ura, azukrea, gatz mineralak.",
        imageRes = R.drawable.aquarius
    ),
    normalizeName("Vino") to LocalInfo(
        short = "Etxeko ardo beltza.",
        ingredients = "Mahatsa, sulfitoak.",
        imageRes = R.drawable.ardoa
    ),
    normalizeName("Ardoa") to LocalInfo(
        short = "Etxeko ardo beltza.",
        ingredients = "Mahatsa, sulfitoak.",
        imageRes = R.drawable.ardoa
    ),
    normalizeName("Estrella Galicia") to LocalInfo(
        short = "Garagardo berezia.",
        ingredients = "Ura, garagar-malta, artoa, lupulua.",
        imageRes = R.drawable.estrella
    ),
    normalizeName("Fanta") to LocalInfo(
        short = "Limoi zaporeko freskagarria.",
        ingredients = "Ura, azukrea, limoi zukua, gasa.",
        imageRes = R.drawable.fanta_limon
    ),
    normalizeName("Coca Cola") to LocalInfo(
        short = "Freskagarri klasikoa.",
        ingredients = "Ura, azukrea, kafeina.",
        imageRes = R.drawable.coca_cola
    ),
    normalizeName("Coca-Cola") to LocalInfo(
        short = "Freskagarri klasikoa.",
        ingredients = "Ura, azukrea, kafeina.",
        imageRes = R.drawable.coca_cola
    ),
    normalizeName("Red Bull") to LocalInfo(
        short = "Edari energetikoa.",
        ingredients = "Ura, azukrea, kafeina, taurina.",
        imageRes = R.drawable.red_bull
    ),
    normalizeName("Sagardoa") to LocalInfo(
        short = "Euskal sagardo naturala.",
        ingredients = "Sagarra.",
        imageRes = R.drawable.sagardo
    ),
    normalizeName("Sidra") to LocalInfo(
        short = "Euskal sagardo naturala.",
        ingredients = "Sagarra.",
        imageRes = R.drawable.sagardo
    ),
    normalizeName("Sagardo") to LocalInfo(
        short = "Euskal sagardo naturala.",
        ingredients = "Sagarra.",
        imageRes = R.drawable.sagardo
    ),
    normalizeName("Zumo Naranja") to LocalInfo(
        short = "Laranja zuku naturala.",
        ingredients = "Laranja.",
        imageRes = R.drawable.zumo_laranja
    ),
    normalizeName("Ebi Nigiri") to LocalInfo(
        short = "Otarrainxka egosia arrozaren gainean.",
        ingredients = "Otarrainxka, arroza.",
        imageRes = R.drawable.ebi_nigiri
    ),
    normalizeName("Maki Avocado") to LocalInfo(
        short = "Aguakatea eta arroza.",
        ingredients = "Aguakatea, arroza, nori.",
        imageRes = R.drawable.avocado_maki
    ),
    normalizeName("Kas Limon") to LocalInfo(
        short = "Limoi zaporeko freskagarria.",
        ingredients = "Ura, azukrea, limoi zukua.",
        imageRes = R.drawable.kas_limon
    ),
    normalizeName("Kas Naranja") to LocalInfo(
        short = "Laranja zaporeko freskagarria.",
        ingredients = "Ura, azukrea, laranja zukua.",
        imageRes = R.drawable.kas_laranja
    ),
    normalizeName("Kafea") to LocalInfo(
        short = "Kafe beltza, ehotu berria.",
        ingredients = "Kafea, ura.",
        imageRes = R.drawable.kafea
    ),
    normalizeName("Nestea Limon") to LocalInfo(
        short = "Limoi zaporeko te freskagarria.",
        ingredients = "Ura, azukrea, tea, limoi zukua.",
        imageRes = R.drawable.nestea
    ),
    normalizeName("Donuts") to LocalInfo(
        short = "Berlina klasikoa.",
        ingredients = "Irina, azukrea, arrautza.",
        imageRes = R.drawable.donuts
    ),
    normalizeName("Sugus") to LocalInfo(
        short = "Fruta zaporeko gozokiak.",
        ingredients = "Azukrea, glukosa, fruta zukua.",
        imageRes = R.drawable.sugus
    ),
    normalizeName("Inari Sushi") to LocalInfo(
        short = "Tofu frijituzko zakuak arrozarekin.",
        ingredients = "Tofu frijitua, arroza, sesamoa.",
        imageRes = R.drawable.inari_sushi
    ),
    normalizeName("Maki Salmon Avocado") to LocalInfo(
        short = "Izokina eta aguakatea.",
        ingredients = "Izokina, aguakatea, arroza, nori.",
        imageRes = R.drawable.salmon_avocado
    ),
    normalizeName("Nigiri Saba") to LocalInfo(
        short = "Berdela ozpindua arroz gainean.",
        ingredients = "Berdela (Saba), arroza, wasabia.",
        imageRes = R.drawable.saba_nigiri
    ),
    normalizeName("Nigiri Pulpo") to LocalInfo(
        short = "Olagarro egosia arroz gainean.",
        ingredients = "Olagarroa (Tako), arroza, wasabia, nori zerrenda.",
        imageRes = R.drawable.octopus_nigiri
    ),
    normalizeName("Rainbow Roll") to LocalInfo(
        short = "Hainbat arrain (izokina, atuna, arrain zuria) California Roll baten gainean.",
        ingredients = "Karramarroa, aguakatea, pepinoa, hainbat arrain, arroza.",
        imageRes = R.drawable.rainbow_roll
    ),
    normalizeName("Ebi Maki") to LocalInfo(
        short = "Otarrainxka makia.",
        ingredients = "Otarrainxka, arroza, nori.",
        imageRes = R.drawable.ebi_maki
    ),
    normalizeName("Kappa Maki") to LocalInfo(
        short = "Pepino makia.",
        ingredients = "Pepinoa, arroza, nori, sesamoa.",
        imageRes = R.drawable.kappa_maki
    ),
    normalizeName("Negi Toro Maki") to LocalInfo(
        short = "Hegaluze sabelaldea eta tipulina.",
        ingredients = "Toro, tipulina, arroza, nori.",
        imageRes = R.drawable.negi_toro_maki
    ),
    normalizeName("Sake Maki") to LocalInfo(
        short = "Izokin makia.",
        ingredients = "Izokina, arroza, nori.",
        imageRes = R.drawable.sake_maki
    ),
    normalizeName("Spicy Salmon Roll") to LocalInfo(
        short = "Izokin pikantea.",
        ingredients = "Izokina, saltsa pikantea, arroza, nori.",
        imageRes = R.drawable.spicy_salmon_roll
    ),
    normalizeName("Spicy Tuna Roll") to LocalInfo(
        short = "Atun pikantea.",
        ingredients = "Atuna, saltsa pikantea, arroza, nori.",
        imageRes = R.drawable.spicy_tuna_roll
    ),
    normalizeName("Squid Nigiri") to LocalInfo(
        short = "Txipiroi nigiria.",
        ingredients = "Txipiroia, arroza, wasabia.",
        imageRes = R.drawable.squid_nigiri
    ),
    normalizeName("Tamago Maki") to LocalInfo(
        short = "Tortilla japoniar makia.",
        ingredients = "Tamago, arroza, nori.",
        imageRes = R.drawable.tamago_maki
    ),
    normalizeName("Nigiri Calamar") to LocalInfo(
        short = "Txipiroi nigiria.",
        ingredients = "Txipiroia, arroza, wasabia.",
        imageRes = R.drawable.squid_nigiri
    ),
    normalizeName("Tamago Nigiri") to LocalInfo(
        short = "Tortilla japoniar nigiria.",
        ingredients = "Tamago, arroza, nori zerrenda.",
        imageRes = R.drawable.tamago_nigiri
    ),
    normalizeName("Nigiri Tamago") to LocalInfo(
        short = "Tortilla japoniar nigiria.",
        ingredients = "Tamago, arroza, nori zerrenda.",
        imageRes = R.drawable.tamago_nigiri
    ),
    normalizeName("Tempura Shrimp Roll") to LocalInfo(
        short = "Otarrainxka tenpura rolla.",
        ingredients = "Otarrainxka tenpura, arroza, nori.",
        imageRes = R.drawable.tempura_shrimp_roll
    ),
    normalizeName("Tuna Avocado Roll") to LocalInfo(
        short = "Atuna eta aguakatea.",
        ingredients = "Atuna, aguakatea, arroza, nori.",
        imageRes = R.drawable.tuna_avocado_roll
    ),
    normalizeName("Unagi Nigiri") to LocalInfo(
        short = "Aingira nigiria.",
        ingredients = "Aingira errea, arroza, nori zerrenda.",
        imageRes = R.drawable.unagi_nigiri
    ),
    normalizeName("Nigiri Unagi") to LocalInfo(
        short = "Aingira nigiria.",
        ingredients = "Aingira errea, arroza, nori zerrenda.",
        imageRes = R.drawable.unagi_nigiri
    ),
    normalizeName("Unagi Roll") to LocalInfo(
        short = "Aingira rolla.",
        ingredients = "Aingira, arroza, nori.",
        imageRes = R.drawable.unagi_roll
    ),
    normalizeName("Veggie Roll") to LocalInfo(
        short = "Barazki rolla.",
        ingredients = "Barazkiak, arroza, nori.",
        imageRes = R.drawable.veggie_roll
    ),
    normalizeName("Futomaki Vegetal") to LocalInfo(
        short = "Barazki freskoekin egindako roll handia.",
        ingredients = "Arroza, nori, aguakatea, pepinoa, azenarioa, kanpai-piperra.",
        imageRes = R.drawable.futomaki_vegetal
    ),
    normalizeName("Sea Bream Nigiri") to LocalInfo(
        short = "Bisigu nigiria.",
        ingredients = "Bisigua, arroza, wasabia.",
        imageRes = R.drawable.sea_bream_nigiri
    ),
    normalizeName("Nigiri Dorada") to LocalInfo(
        short = "Bisigu nigiria.",
        ingredients = "Bisigua, arroza, wasabia.",
        imageRes = R.drawable.sea_bream_nigiri
    )
)

private fun groupTitleForProductName(nameRaw: String): String {
    val n = normalizeName(nameRaw)

    fun inAny(vararg names: String): Boolean {
        val set = names.map { normalizeName(it) }.toSet()
        return set.contains(n)
    }

    return when {
        inAny("Edamame Sushinelli", "Edamame Trufa Blanca", "Gyozas Black Angus", "Tartar de Salmón Yuzu") -> "🌸 Start & Share"
        inAny("Sashimi Selection Sushinelli", "Carpaccio Hamachi Citrus", "Usuzukuri de Lubina Mediterránea") -> "🐟 Raw Bar"
        inAny("Nigiri Salmón Imperial", "Nigiri Toro Sushinelli", "Nigiri Wagyu A5 Aburi", "Nigiri Vieira Miso", "Nigiri Berenjena Miso", "Inari Sushi", "Nigiri Saba", "Nigiri Pulpo") -> "🍣 Nigiri Collection"
        inAny("California Gold Roll", "Philadelphia Deluxe", "Philadelphia Roll", "Tekka Maki Tradicional", "Ebi Nigiri", "Maki Avocado", "Maki Salmon Avocado", "Futomaki Vegetal") -> "🍙 Classic Rolls"
        inAny("Sushinelli Truffle Roll", "Crispy Dragon Roll", "Volcano Sushinelli", "Mediterranean Roll", "Green Dragon Roll", "Rainbow Roll",) -> "⭐ Signature Rolls"
        inAny("Chirashi Premium Sushinelli", "Donburi Black Cod Miso", "Spicy Tuna Bowl", "Vegan Chirashi") -> "🍚 Bowls & Rice"
        inAny("Tempura Royal de Langostinos", "Bao Wagyu Teriyaki", "Yakisoba Sushinelli") -> "🔥 Hot Kitchen"
        inAny("Omakase Sushinelli") -> "🎎 Omakase Experience"
        inAny("Mochi Artisaua", "Cheesecake Matcha Sushinelli") -> "🍰 Sweet Ending"
        inAny("Bezoya", "Aquarius", "Vino", "Estrella Galicia", "Fanta", "Kas Limon", "Kas Naranja", "Kafea", "Nestea Limon") -> "🥤 Edariak"
        else -> "Besteak"
    }
}

private fun nowIso(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    return sdf.format(Date())
}

private fun mmss(ms: Long): String {
    val sec = (ms / 1000).toInt()
    val m = sec / 60
    val s = sec % 60
    return "%02d:%02d".format(m, s)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    workerName: String?,
    workerMahaiId: Int?,
    chatBaimena: Boolean,
    serviceNumber: Int,
    serviceActive: Boolean,
    onServiceStarted: () -> Unit,
    onServiceFinished: () -> Unit,
    onLogout: () -> Unit,
    onOpenChat: () -> Unit,
    sharedCart: MutableMap<Int, Int>? = null,
    sharedAccumulated: MutableList<EskaeraLineRequest>? = null
) {
    val mahaiId = workerMahaiId

    val maxTotal = 10
    val cooldownMs = 20L * 60L * 1000L
    fun nowMs() = System.currentTimeMillis()

    var cooldownEndAt by rememberSaveable { mutableStateOf(0L) }
    var orderedInQuota by rememberSaveable { mutableStateOf(0) }

    var tick by remember { mutableStateOf(0) }
    
    LaunchedEffect(cooldownEndAt) {
        while (cooldownEndAt > nowMs()) {
            delay(1000)
            tick++
        }
        if (cooldownEndAt > 0L && cooldownEndAt <= nowMs()) {
            cooldownEndAt = 0L
            orderedInQuota = 0
        }
    }

    fun remainingCooldownMs(): Long {
        tick // forzar recomposición
        return (cooldownEndAt - nowMs()).coerceAtLeast(0L)
    }

    fun isCooldownActive(): Boolean = remainingCooldownMs() > 0L

    fun remainingQuota(): Int {
        if (!isCooldownActive() && cooldownEndAt > 0L) {
            cooldownEndAt = 0L
            orderedInQuota = 0
        }
        if (isCooldownActive()) return 0
        return (maxTotal - orderedInQuota).coerceAtLeast(0)
    }

    val scope = rememberCoroutineScope()
    val infoMap = remember { localInfoMap() }

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var menu by remember { mutableStateOf<List<MenuItem>>(emptyList()) }

    val cart = remember { mutableStateMapOf<Int, Int>().apply { sharedCart?.let { putAll(it) } } }
    val accumulatedOrders = remember { mutableStateListOf<EskaeraLineRequest>().apply { sharedAccumulated?.let { addAll(it) } } }

    LaunchedEffect(cart.toMap()) {
        sharedCart?.clear()
        sharedCart?.putAll(cart)
    }
    
    LaunchedEffect(accumulatedOrders.toList()) {
        sharedAccumulated?.clear()
        sharedAccumulated?.addAll(accumulatedOrders)
    }

    fun committedQtyOf(id: Int) = accumulatedOrders.count { it.produktuaId == id }
    fun draftQtyOf(id: Int) = cart[id] ?: 0
    fun qtyOf(id: Int) = draftQtyOf(id) + committedQtyOf(id)
    fun draftQty() = cart.values.sum()
    fun draftPlaterakQty() = cart.entries.sumOf { (id, qty) ->
        if (menu.firstOrNull { it.id == id }?.isPlatera == true) qty else 0
    }

    var section by remember { mutableStateOf(MenuSection.Platerak) }
    var ordersExpanded by remember { mutableStateOf(true) }

    var selectedItem by remember { mutableStateOf<MenuItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var ordering by remember { mutableStateOf(false) }
    var orderMsg by remember { mutableStateOf<String?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            val resProd = RetrofitClient.api.getProduktuak()
            val resPlat = RetrofitClient.api.getPlaterak()
            
            if (resProd.isSuccessful && resProd.body() != null && resPlat.isSuccessful && resPlat.body() != null) {
                val prodDto = resProd.body()!!
                val platDto = resPlat.body()!!.datuak ?: emptyList()

                fun sectionOfProd(typeId: Int): MenuSection? = when (typeId) {
                    6 -> MenuSection.Edariak
                    14 -> MenuSection.Postreak
                    else -> null
                }

                val menuList = mutableListOf<MenuItem>()

                // 1. Cargamos las bebidas y postres desde Produktuak
                prodDto.forEach { p ->
                    val sec = sectionOfProd(p.produktuenMotakId) ?: return@forEach
                    val name = p.izena ?: "Izenik gabe"
                    val normName = normalizeName(name)
                    var li = infoMap[normName]
                    
                    if (li == null) {
                        val normTokens = normName.split(" ").filter { it.length > 2 }.toSet()
                        var bestKey: String? = null
                        var bestScore = 0
                        for (key in infoMap.keys) {
                            val keyTokens = key.split(" ").filter { it.length > 2 }.toSet()
                            val score = keyTokens.intersect(normTokens).size
                            if (score > bestScore) {
                                bestScore = score
                                bestKey = key
                            }
                        }
                        if (bestKey != null && bestScore > 0) li = infoMap[bestKey]
                    }

                    menuList.add(MenuItem(
                        id = p.id,
                        name = name,
                        price = (p.prezioa?.toDouble() ?: -1.0),
                        stock = (p.stock ?: 0),
                        section = sec,
                        shortInfo = li?.short ?: "",
                        ingredientsText = li?.ingredients ?: "",
                        imageRes = li?.imageRes ?: R.drawable.shusinelli,
                        isPlatera = false
                    ))
                }

                // 2. Cargamos los platos desde Platerak
                platDto.forEach { p ->
                    val sec = MenuSection.Platerak
                    val name = p.izena ?: "Izenik gabe"
                    val normName = normalizeName(name)
                    var li = infoMap[normName]
                    
                    if (li == null) {
                        val normTokens = normName.split(" ").filter { it.length > 2 }.toSet()
                        var bestKey: String? = null
                        var bestScore = 0
                        for (key in infoMap.keys) {
                            val keyTokens = key.split(" ").filter { it.length > 2 }.toSet()
                            val score = keyTokens.intersect(normTokens).size
                            if (score > bestScore) {
                                bestScore = score
                                bestKey = key
                            }
                        }
                        if (bestKey != null && bestScore > 0) li = infoMap[bestKey]
                    }

                    val ingredientsStr = p.osagaiak?.mapNotNull { it.izena }?.joinToString(", ") ?: li?.ingredients ?: ""

                    menuList.add(MenuItem(
                        id = p.id,
                        name = name,
                        price = (p.prezioa?.toDouble() ?: -1.0),
                        stock = 99, // Platerak no tienen stock directo en esta vista
                        section = sec,
                        shortInfo = li?.short ?: p.mota ?: "",
                        ingredientsText = ingredientsStr,
                        imageRes = li?.imageRes ?: R.drawable.shusinelli,
                        isPlatera = true
                    ))
                }

                menu = menuList.sortedBy { it.name }
            } else {
                error = "Errorea datuak kargatzean"
            }
        } catch (e: Exception) {
            error = "Errorea: ${e.message}"
        } finally {
            loading = false
        }
    }

    fun inc(item: MenuItem) {
        if (item.isPlatera) {
            if (isCooldownActive()) return
            if (draftPlaterakQty() >= remainingQuota()) return
        }

        val current = qtyOf(item.id)
        if (current >= item.stock) return
        cart[item.id] = draftQtyOf(item.id) + 1
    }

    fun dec(item: MenuItem) {
        val draftQty = draftQtyOf(item.id)
        if (draftQty > 0) {
            if (draftQty == 1) cart.remove(item.id) else cart[item.id] = draftQty - 1
        }
    }

    fun canOrder(): Boolean {
        if (ordering) return false
        if (cart.isEmpty()) return false

        if (isCooldownActive()) {
            if (draftPlaterakQty() > 0) return false
        } else {
            if (draftPlaterakQty() > remainingQuota()) return false
        }
        return true
    }

    fun canPay(): Boolean {
        if (cart.isEmpty() && accumulatedOrders.isEmpty()) return false
        if (cart.isNotEmpty() && !canOrder()) return false
        return true
    }

    suspend fun doOrder() {
        if (!canOrder()) return
        if (mahaiId == null) {
            orderMsg = "Langile honek ez dauka mahaia esleituta"
            return
        }

        ordering = true
        orderMsg = null

        val orderedPlaterakNowQty = draftPlaterakQty()

        try {
            val lines = cart.entries.flatMap { (id, qty) ->
                val item = menu.firstOrNull { it.id == id }
                List(qty) { _ ->
                    EskaeraLineRequest(
                        produktuaId = id,
                        izena = item?.name,
                        prezioa = item?.price?.let { if (it < 0) null else it },
                        data = nowIso(),
                        egoera = 0,
                        isPlatera = item?.isPlatera == true
                    )
                }
            }

            val currentPrice = lines.sumOf { it.prezioa ?: 0.0 }
            val req = ZerbitzuaCreateRequest(
                prezioTotala = currentPrice,
                data = nowIso(),
                erreserbaId = null,
                mahaiakId = mahaiId,
                eskaerak = lines
            )

            val res = RetrofitClient.api.createZerbitzua(body = req)
            if (!res.isSuccessful) {
                orderMsg = "Errorea eskaera bidaltzean: ${res.code()}"
                ordering = false
                return
            }

            val requestedMap = cart.toMap()
            menu = menu.map { m ->
                val take = requestedMap[m.id] ?: 0
                if (take <= 0) m else m.copy(stock = (m.stock - take).coerceAtLeast(0))
            }

            orderedInQuota += orderedPlaterakNowQty
            if (orderedInQuota >= maxTotal && cooldownEndAt == 0L) {
                orderedInQuota = maxTotal
                cooldownEndAt = nowMs() + cooldownMs
            }

            accumulatedOrders.addAll(lines)
            cart.clear()
            if (!serviceActive) {
                onServiceStarted()
                orderMsg = "Zerbitzua $serviceNumber hasi da. Eskaera sukaldera bidali da ✅"
            } else {
                orderMsg = "Eskaera sukaldera bidali da ✅"
            }

        } catch (e: Exception) {
            orderMsg = "Errorea: ${e.message}"
        } finally {
            ordering = false
        }
    }

    suspend fun doPayment() {
        if (cart.isEmpty() && accumulatedOrders.isEmpty()) return
        
        if (cart.isNotEmpty() && !canOrder()) return

        if (mahaiId == null) {
            orderMsg = "Langile honek ez dauka mahaia esleituta"
            return
        }

        ordering = true
        orderMsg = null

        try {
            var zerbitzuaId: Int? = null

            // Si hay algo en el carrito sin enviar, lo enviamos primero
            val currentLines = cart.entries.flatMap { (id, qty) ->
                val item = menu.firstOrNull { it.id == id }
                List(qty) { _ ->
                    EskaeraLineRequest(
                        produktuaId = id,
                        izena = item?.name,
                        prezioa = item?.price?.let { if (it < 0) null else it },
                        data = nowIso(),
                        egoera = 0,
                        isPlatera = item?.isPlatera == true
                    )
                }
            }

            if (currentLines.isNotEmpty()) {
                val currentPrice = currentLines.sumOf { it.prezioa ?: 0.0 }
                val req = ZerbitzuaCreateRequest(
                    prezioTotala = currentPrice,
                    data = nowIso(),
                    erreserbaId = null,
                    mahaiakId = mahaiId,
                    eskaerak = currentLines
                )
                val res = RetrofitClient.api.createZerbitzua(body = req)
                if (res.isSuccessful) {
                    zerbitzuaId = res.body()?.id
                } else {
                    orderMsg = "Errorea ordainketa (eskaerak bidaltzean): ${res.code()}"
                    ordering = false
                    return
                }
            } else {
                // Si el carrito estaba vacío, necesitamos obtener el zerbitzuaId de alguna manera.
                // Como createZerbitzua devuelve el ID del servicio activo si le enviamos 0 eskaerak...
                val req = ZerbitzuaCreateRequest(
                    prezioTotala = 0.0,
                    data = nowIso(),
                    erreserbaId = null,
                    mahaiakId = mahaiId,
                    eskaerak = emptyList()
                )
                val res = RetrofitClient.api.createZerbitzua(body = req)
                if (res.isSuccessful) {
                    zerbitzuaId = res.body()?.id
                }
            }

            if (zerbitzuaId != null) {
                try {
                    val resFaktura = RetrofitClient.api.createFaktura(zerbitzuaId)
                    if (!resFaktura.isSuccessful) {
                        println("Errorea faktura sortzean: ${resFaktura.code()}")
                    }
                } catch (e: Exception) {
                    println("Errorea faktura deian: ${e.message}")
                }

                cart.clear()
                accumulatedOrders.clear()
                orderedInQuota = 0
                cooldownEndAt = 0L
                onServiceFinished()
                orderMsg = "Ordainketa eginda. Zerbitzua itxita eta hurrengoa prest ✅"
            } else {
                orderMsg = "Errorea ordainketa: zerbitzuaren IDa ez da aurkitu"
            }

        } catch (e: Exception) {
            orderMsg = "Errorea: ${e.message}"
        } finally {
            ordering = false
        }
    }

    val shown = when (section) {
        MenuSection.Platerak -> menu.filter { it.section == MenuSection.Platerak }
        MenuSection.Edariak -> menu.filter { it.section == MenuSection.Edariak }
        MenuSection.Postreak -> menu.filter { it.section == MenuSection.Postreak }
    }

    val groupedPlaterak: List<Pair<String, List<MenuItem>>> = remember(shown, section) {
        if (section != MenuSection.Platerak) emptyList()
        else {
            val m = shown.groupBy { groupTitleForProductName(it.name) }
            val keys = m.keys.sortedWith(compareBy<String> { if (it == "Besteak") 1 else 0 }.thenBy { it })
            keys.map { k -> k to (m[k]!!.sortedBy { it.name }) }
        }
    }

    Row(Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .width(240.dp)
                .fillMaxHeight()
                .background(Color.White)
                .border(1.dp, BrandGold.copy(alpha = 0.25f))
                .padding(12.dp)
        ) {
            Text("Ongi etorri", fontWeight = FontWeight.SemiBold, color = BrandGold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Mahaia ${mahaiId ?: "-"}",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (serviceActive) "Zerbitzua $serviceNumber aktibo" else "Zerbitzua $serviceNumber prest",
                color = if (serviceActive) SuccessGreen else BrandGold,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            SidebarItemRow("Platerak", section == MenuSection.Platerak) { section = MenuSection.Platerak }
            Spacer(Modifier.height(8.dp))
            SidebarItemRow("Edariak", section == MenuSection.Edariak) { section = MenuSection.Edariak }
            Spacer(Modifier.height(8.dp))
            SidebarItemRow("Postreak", section == MenuSection.Postreak) { section = MenuSection.Postreak }

            Spacer(Modifier.weight(1f))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, BrandGold.copy(alpha = 0.25f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Menua", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = BrandGold)
                Spacer(Modifier.weight(1f))

                Text(
                    if (serviceActive) "Zerbitzua $serviceNumber aktibo" else "Zerbitzua $serviceNumber prest",
                    color = if (serviceActive) SuccessGreen else BrandGold,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.width(12.dp))

                TextButton(onClick = {
                    scope.launch {
                        if (workerMahaiId != null) {
                            try {
                                val res = RetrofitClient.api.checkChatBaimena(workerMahaiId)
                                if (res.isSuccessful) {
                                    val baimena = res.body()?.chatBaimena ?: false
                                    if (baimena) {
                                        onOpenChat()
                                    } else {
                                        orderMsg = "Mahai honek ez dauka txata erabiltzeko baimenik."
                                    }
                                } else {
                                    if (!chatBaimena) {
                                        orderMsg = "Mahai honek ez dauka txata erabiltzeko baimenik."
                                    } else {
                                        onOpenChat()
                                    }
                                }
                            } catch (e: Exception) {
                                if (!chatBaimena) {
                                    orderMsg = "Mahai honek ez dauka txata erabiltzeko baimenik."
                                } else {
                                    onOpenChat()
                                }
                            }
                        }
                    }
                }) {
                    Text("Txata", color = BrandGold, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.width(8.dp))

                TextButton(onClick = { ordersExpanded = !ordersExpanded }) {
                    Icon(
                        imageVector = if (ordersExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = BrandGold
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (ordersExpanded) "Eskaerak ezkutatu" else "Eskaerak erakutsi", color = BrandGold)
                }
            }

            if (ordersExpanded) {
                OrdersBar(menu = menu, cart = cart, accumulated = accumulatedOrders, modifier = Modifier.fillMaxWidth())

                val quota = remainingQuota()
                val active = isCooldownActive()
                val remMs = remainingCooldownMs()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { scope.launch { doOrder() } },
                        enabled = canOrder(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGold, contentColor = Color.White)
                    ) {
                        if (ordering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                        }
                        Text("Eskatu")
                    }

                    Spacer(Modifier.width(12.dp))

                    Button(
                        onClick = { showPaymentDialog = true },
                        enabled = canPay(),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White)
                    ) {
                        Text("Ordaindu")
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        "Platerak: ${draftPlaterakQty()}/$quota",
                        color = BrandGold,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.width(12.dp))

                    if (active) {
                        Text(
                            "Itxaron: ${mmss(remMs)}",
                            color = DangerRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (active && draftPlaterakQty() > 0) {
                    Text(
                        "Ezin da eskatu platerik. Itxaron ${mmss(remMs)} minutu.",
                        color = DangerRed,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                orderMsg?.let {
                    Text(
                        it,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = if (it.contains("✅")) SuccessGreen else DangerRed
                    )
                }
            }

            HorizontalDivider(color = BrandGold.copy(alpha = 0.25f))

            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGold)
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = DangerRed)
                }
                else -> {
                    when (section) {
                        MenuSection.Platerak -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                groupedPlaterak.forEach { (groupTitle, itemsList) ->
                                    item {
                                        Text(
                                            groupTitle,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandGold
                                        )
                                        Spacer(Modifier.height(6.dp))
                                    }

                                    items(itemsList) { item ->
                                        MenuCard(
                                            item = item,
                                            qty = qtyOf(item.id),
                                            canRemove = draftQtyOf(item.id) > 0,
                                            canAddMore = if (item.isPlatera) (!isCooldownActive() && draftPlaterakQty() < remainingQuota()) else true,
                                            onPlus = { inc(item) },
                                            onMinus = { dec(item) },
                                            onImageClick = {
                                                selectedItem = item
                                                scope.launch { sheetState.show() }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(shown.sortedBy { it.name }) { item ->
                                    MenuCard(
                                        item = item,
                                        qty = qtyOf(item.id),
                                        canRemove = draftQtyOf(item.id) > 0,
                                        canAddMore = if (item.isPlatera) (!isCooldownActive() && draftPlaterakQty() < remainingQuota()) else true,
                                        onPlus = { inc(item) },
                                        onMinus = { dec(item) },
                                        onImageClick = {
                                            selectedItem = item
                                            scope.launch { sheetState.show() }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPaymentDialog) {
        val currentLines = cart.entries.flatMap { (id, qty) ->
            val item = menu.firstOrNull { it.id == id }
            List(qty) { _ ->
                EskaeraLineRequest(
                    produktuaId = id,
                    izena = item?.name,
                    prezioa = item?.price?.let { if (it < 0) null else it },
                    data = nowIso(),
                    egoera = 0,
                    isPlatera = item?.isPlatera == true
                )
            }
        }
        val allLines = accumulatedOrders + currentLines
        val totalPrice = allLines.sumOf { it.prezioa ?: 0.0 }
        
        val groupedItems = allLines.groupBy { it.izena ?: "Ezezaguna" }
            .map { (name, list) -> 
                val qty = list.size
                val itemPrice = list.firstOrNull()?.prezioa ?: 0.0
                Triple(name, qty, qty * itemPrice)
            }.sortedBy { it.first }

        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Ordaindu", fontWeight = FontWeight.Bold, color = BrandGold) },
            text = {
                Column {
                    Text("Hau da zure eskaera:", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(groupedItems) { (name, qty, price) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("$name x$qty", modifier = Modifier.weight(1f), fontSize = 14.sp)
                                Text("€%.2f".format(price), fontSize = 14.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Guztira:", fontWeight = FontWeight.Bold)
                        Text("€%.2f".format(totalPrice), fontWeight = FontWeight.Bold, color = BrandGold)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Ziur ordaindu nahi duzula?")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPaymentDialog = false
                        scope.launch { doPayment() }
                    }
                ) { Text("Bai", color = SuccessGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentDialog = false }) {
                    Text("Ez", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }

    if (selectedItem != null) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }.invokeOnCompletion { selectedItem = null }
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            val item = selectedItem!!
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(item.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                Spacer(Modifier.height(10.dp))

                Text("Informazioa:", fontWeight = FontWeight.SemiBold, color = BrandGold)
                Spacer(Modifier.height(6.dp))
                Text(item.shortInfo)

                Spacer(Modifier.height(14.dp))
                Text("Osagaiak:", fontWeight = FontWeight.SemiBold, color = BrandGold)
                Spacer(Modifier.height(6.dp))
                Text(item.ingredientsText)

                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { selectedItem = null } },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGold, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Itxi") }
            }
        }
    }
}

@Composable
private fun SidebarItemRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) BrandGold.copy(alpha = 0.08f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Text(title, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = BrandGold)
    }
}

@Composable
private fun MenuCard(
    item: MenuItem,
    qty: Int,
    canRemove: Boolean,
    canAddMore: Boolean,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    onImageClick: () -> Unit
) {
    val noMoreBecauseStock = item.stock <= 0 || qty >= item.stock

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, BrandGold.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .clickable { onImageClick() }
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(22.dp)
                        .background(BrandGold.copy(alpha = 0.85f), CircleShape)
                        .padding(4.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = BrandGold)
                
                Text(
                    text = item.shortInfo,
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = item.ingredientsText,
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                val priceText = if (item.price < 0) "—" else "€%.2f".format(item.price)
                Text(priceText, color = Color.Black.copy(alpha = 0.65f), fontSize = 13.sp)

                Spacer(Modifier.height(4.dp))

                if (noMoreBecauseStock) {
                    Text(
                        "Ez dago stock gehiago produktu honetarako.",
                        color = DangerRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onMinus,
                    enabled = canRemove,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DangerRed.copy(alpha = if (canRemove) 0.12f else 0.05f))
                ) {
                    Icon(Icons.Default.Remove, null, tint = DangerRed)
                }

                Spacer(Modifier.width(8.dp))
                Text(qty.toString(), fontWeight = FontWeight.SemiBold, modifier = Modifier.width(22.dp))

                Spacer(Modifier.width(8.dp))

                val plusEnabled = canAddMore && !noMoreBecauseStock

                IconButton(
                    onClick = onPlus,
                    enabled = plusEnabled,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (!plusEnabled) Color.Gray.copy(alpha = 0.2f)
                            else SuccessGreen.copy(alpha = 0.12f)
                        )
                ) {
                    Icon(
                        Icons.Default.Add,
                        null,
                        tint = if (!plusEnabled) Color.Gray else SuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun OrdersBar(menu: List<MenuItem>, cart: Map<Int, Int>, accumulated: List<EskaeraLineRequest>, modifier: Modifier = Modifier) {
    val ordered = remember(menu, cart) {
        cart.entries.mapNotNull { (id, qty) ->
            val item = menu.firstOrNull { it.id == id } ?: return@mapNotNull null
            item to qty
        }.sortedBy { it.first.name }
    }

    val prevOrdered = remember(accumulated) {
        accumulated.groupBy { it.izena ?: "?" }
            .map { (name, list) -> Triple(name, list.size, list.sumOf { it.prezioa ?: 0.0 }) }
            .sortedBy { it.first }
    }

    val cartTotal = ordered.sumOf { (item, qty) -> (item.price.coerceAtLeast(0.0)) * qty }
    val accumulatedTotal = prevOrdered.sumOf { it.third }
    val totalGuztira = cartTotal + accumulatedTotal

    Column(
        modifier = modifier
            .background(Color.White)
            .border(1.dp, BrandGold.copy(alpha = 0.25f))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Eskatutako produktuak", fontWeight = FontWeight.Bold, color = BrandGold)
            if (ordered.isNotEmpty() || prevOrdered.isNotEmpty()) {
                Text("Guztira: €%.2f".format(totalGuztira), fontWeight = FontWeight.Bold, color = BrandGold)
            }
        }
        Spacer(Modifier.height(8.dp))

        if (ordered.isEmpty() && prevOrdered.isEmpty()) {
            Text("Oraindik ez dago ezer.", color = Color.Black.copy(alpha = 0.6f))
        } else {
            if (prevOrdered.isNotEmpty()) {
                Text("Bidalita (Zerbitzarian gordetzeko zain):", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                prevOrdered.forEach { (name, qty, _) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("• $name", modifier = Modifier.weight(1f), color = Color.Gray)
                        Text("x$qty", fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                if (ordered.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BrandGold.copy(alpha = 0.1f))
                }
            }

            if (ordered.isNotEmpty()) {
                if (prevOrdered.isNotEmpty()) {
                     Text("Saskia (Berriak):", fontSize = 12.sp, color = BrandGold, fontWeight = FontWeight.Bold)
                }
                ordered.forEach { (item, qty) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("• ${item.name}", modifier = Modifier.weight(1f))
                        Text("x$qty", fontWeight = FontWeight.SemiBold, color = BrandGold)
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}
