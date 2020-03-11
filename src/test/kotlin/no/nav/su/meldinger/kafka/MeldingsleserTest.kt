package no.nav.su.meldinger.kafka

import no.nav.common.KafkaEnvironment
import no.nav.su.meldinger.kafka.EmbeddedKafka.Companion.embeddedKafka
import no.nav.su.meldinger.kafka.Topics.SØKNAD_TOPIC
import no.nav.su.meldinger.kafka.soknad.NySøknad
import no.nav.su.meldinger.kafka.soknad.NySøknadMedSkyggesak
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MeldingsleserTest {
    private lateinit var embeddedKafka: KafkaEnvironment
    val sendtNySøknad = NySøknad(correlationId = "corr", søknad = """{"json": "it is"}""", søknadId = "6", aktørId = "aktie", fnr = "01010112345", sakId = "75")
    val medSkygge = sendtNySøknad.medSkyggesak("skuggfax")
    val medJournalId = medSkygge.medJournalId("alpha niner bogus")

    @Test
    fun `meldingsleseren skal kunne lese meldinger av den typen vi ber den om og ignorere meldinger av feil type`() {
        val miljø = KafkaMiljø(groupId = "testgruppe", bootstrap = "${embeddedKafka.brokersURL}", commitInterval = "5", trustStorePassword = "", trustStorePath = "", password = "kafkaPassword", username = "kafkaUser")
        val producer = KafkaProducer(KafkaConsumerConfigBuilder(miljø).producerConfig(), StringSerializer(), StringSerializer())
        producer.send(sendtNySøknad.toProducerRecord(SØKNAD_TOPIC)).get()
        producer.send(medSkygge.toProducerRecord(SØKNAD_TOPIC)).get()
        producer.send(medJournalId.toProducerRecord(SØKNAD_TOPIC)).get()
        val forventninger = Forventning()
        val leser = Meldingsleser(miljø, forventninger)
        (1..10).forEach {
        leser.lesMelding<NySøknadMedSkyggesak> { msg ->
            assertEquals(medSkygge, msg)
        }}
        forventninger.sjekkForventninger(3, 1)
    }

    @BeforeEach
    fun beforeEach() {
        embeddedKafka = embeddedKafka()
    }

    @AfterEach
    fun afterEach() {
        embeddedKafka.tearDown()
    }
}

private class Forventning : Meldingsleser.Meldingrapport {
    private var leseTeller = 0
    private var behandleTeller = 0
    override fun meldingLest(): Unit {
        leseTeller += 1
    }
    override fun meldingBehandlet(): Unit {
        behandleTeller += 1
    }
    fun sjekkForventninger(antallLest: Int, antallBehandlet: Int) {
        assertEquals(antallLest, leseTeller, "Leste feil antall meldinger")
        assertEquals(antallBehandlet, behandleTeller, "Behandlet feil antall meldinger")
    }
}