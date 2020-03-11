package no.nav.su.meldinger.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import no.nav.su.meldinger.kafka.soknad.SøknadMelding
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Forenklet kafka-configurering og instansiering av diverse kafka-tjenester
 */
class KafkaMiljø(
    val groupId: String,
    val commitInterval: String,
    val bootstrap: String,
    val username: String,
    val password: String,
    val trustStorePath: String,
    val trustStorePassword: String
) {
    fun producer(): KafkaProducer<String, String> = KafkaProducer(KafkaConsumerConfigBuilder(this).producerConfig(), StringSerializer(), StringSerializer())
    fun consumer(): KafkaConsumer<String, String> = KafkaConsumer(KafkaConsumerConfigBuilder(this).consumerConfig(), StringDeserializer(), StringDeserializer())
    fun meldingsleser(rapport: Meldingsleser.Meldingrapport): Meldingsleser = Meldingsleser(this, rapport)
}

/**
 * Kan lese meldinger fra kafka og reagere på enkelte meldinger bestemt av
 * brukeren.
 */
class Meldingsleser(miljø: KafkaMiljø, val rapport: Meldingrapport) {
    /**
     * Callbacks for forskjellige tilstander i meldingsleseren
     */
    interface Meldingrapport {
        /** kalles hver eneste gang en hvilken som helst melding leses, uavhengig av type eller validitet */
        fun meldingLest()
        /** kalles om en melding har blitt behandlet av Meldingsleseren */
        fun meldingBehandlet()
    }
    private val config = KafkaConsumerConfigBuilder(miljø)
    val kafkaConsumer: KafkaConsumer<String, String> = KafkaConsumer(
        config.consumerConfig(),
        StringDeserializer(),
        StringDeserializer()
    ).apply {
        subscribe(listOf(Topics.SØKNAD_TOPIC))
    }

    /**
     * Leser neste melding på topic, og behandler den
     * med gitt lambda, om meldingen kan tolkes som den
     * oppgitte meldingstypen.
     *
     * Om meldingen ikke kan behandles så blir den ignorert.
     */
    inline fun <reified T: SøknadMelding> lesMelding(behandling: (T) -> Unit) {
        kafkaConsumer.poll(Duration.of(100, ChronoUnit.MILLIS))
            .onEach {
                it.logMessage()
                rapport.meldingLest()
            }
            .filter { SøknadMelding.fromConsumerRecord(it) is T }
            .map { SøknadMelding.fromConsumerRecord(it) as T }
            .forEach {message ->
                behandling(message)
                rapport.meldingBehandlet()
            }
    }

    fun ConsumerRecord<String, String>.logMessage() {
        LOG.info("Polled message: topic:${this.topic()}, key:${this.key()}, value:${this.value()}: $xCorrelationId:${this.headersAsString()[xCorrelationId]}")
    }
}

private const val xCorrelationId = "X-Correlation-ID"

private val LOG = LoggerFactory.getLogger(Meldingsleser::class.java)

internal class KafkaConsumerConfigBuilder(
    private val miljø: KafkaMiljø
) {
    internal fun consumerConfig() = kafkaBaseConfig().apply {
        put(ConsumerConfig.GROUP_ID_CONFIG, miljø.groupId)
        put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
        put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, miljø.commitInterval)
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    }

    internal fun producerConfig() = kafkaBaseConfig().apply {
        put(ProducerConfig.ACKS_CONFIG, "all")
        put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1")
    }

    private fun kafkaBaseConfig() = Properties().apply {
        put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, miljø.bootstrap)
        put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT")
        val username = miljø.username
        val password = miljø.password
        put(
            SaslConfigs.SASL_JAAS_CONFIG,
            "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$username\" password=\"$password\";"
        )
        put(SaslConfigs.SASL_MECHANISM, "PLAIN")

        val truststorePath = miljø.trustStorePath
        val truststorePassword = miljø.trustStorePassword
        if (truststorePath != "" && truststorePassword != "")
            try {
                put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL")
                put(
                    SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, File(
                        truststorePath
                    ).absolutePath
                )
                put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword)
                LOG.info("Configured '${SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG}' location ")
            } catch (ex: Exception) {
                LOG.error("Failed to set '${SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG}' location", ex)
            }
    }
}


