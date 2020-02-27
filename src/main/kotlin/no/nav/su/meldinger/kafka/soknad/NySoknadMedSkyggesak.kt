package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.KafkaMessage
import org.json.JSONObject

data class NySoknadMedSkyggesak(
        val sakId: String,
        val aktoerId: String,
        val soknadId: String,
        val soknad: String,
        val gsakId: String
) : KafkaMessage {
    override fun key(): String = sakId
    override fun value(): String = toJson()
    private fun toJson(): String {
        return """
            {
                "sakId":"$sakId",
                "aktoerId":"$aktoerId",
                "soknadId":"$soknadId",
                "soknad":$soknad,
                "gsakId":"$gsakId"
            }
        """.trimIndent()
    }

    companion object {
        fun fromJson(json: String): NySoknadMedSkyggesak {
            val jsonObject = JSONObject(json)
            return NySoknadMedSkyggesak(
                sakId = jsonObject.getString("sakId"),
                aktoerId = jsonObject.getString("aktoerId"),
                soknadId = jsonObject.getString("soknadId"),
                soknad = jsonObject.getJSONObject("soknad").toString(),
                gsakId = jsonObject.getString("gsakId").toString()
            )
        }
    }
}