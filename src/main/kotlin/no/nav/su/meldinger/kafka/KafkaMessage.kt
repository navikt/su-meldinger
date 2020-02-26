package no.nav.su.meldinger.kafka

interface KafkaMessage {
    fun key(): String
    fun value(): String
}