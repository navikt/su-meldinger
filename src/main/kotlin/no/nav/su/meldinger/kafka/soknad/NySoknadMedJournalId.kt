package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.KafkaMessage
import org.json.JSONObject

data class NySoknadMedJournalId(
        val sakId: String,
        val aktoerId: String,
        val soknadId: String,
        val soknad: String,
        val gsakId: String,
        val journalId: String

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
                "gsakId":"$gsakId",
                "journalId":"$journalId"
            }
        """.trimIndent()
    }

    companion object {
        fun fromJson(json: String): NySoknadMedJournalId {
            val jsonObject = JSONObject(json)
            return NySoknadMedJournalId(
                sakId = jsonObject.getString("sakId"),
                aktoerId = jsonObject.getString("aktoerId"),
                soknadId = jsonObject.getString("soknadId"),
                soknad = jsonObject.getJSONObject("soknad").toString(),
                gsakId = jsonObject.getString("gsakId").toString(),
                journalId = jsonObject.getString("journalId").toString()
            )
        }
    }
}