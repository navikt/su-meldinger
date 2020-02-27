package no.nav.su.meldinger.kafka.soknad

import no.nav.su.meldinger.kafka.KafkaMessage

data class NySoknadHentGsak(
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
}