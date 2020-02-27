package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.KafkaMessage
import org.json.JSONObject

data class NySoknad(
    val sakId: String,
    val aktoerId: String,
    val soknadId: String,
    val soknad: String
) : KafkaMessage {
    override fun key() = sakId
    override fun value() = toJson()
    private fun toJson(): String {
        return """
            {
                "sakId":"$sakId",
                "aktoerId":"$aktoerId",
                "soknadId":"$soknadId",
                "soknad":$soknad
            }
        """.trimIndent()
    }

    companion object {
        fun fromJson(json: String): NySoknad {
            val jsonObject = JSONObject(json)
            return NySoknad(
                sakId = jsonObject.getString("sakId"),
                aktoerId = jsonObject.getString("aktoerId"),
                soknadId = jsonObject.getString("soknadId"),
                soknad = jsonObject.getJSONObject("soknad").toString()
            )
        }
    }

}

