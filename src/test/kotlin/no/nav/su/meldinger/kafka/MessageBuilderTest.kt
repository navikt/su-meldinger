package no.nav.su.meldinger.kafka

import no.nav.su.meldinger.kafka.MessageBuilder.Companion.fromConsumerRecord
import no.nav.su.meldinger.kafka.MessageResolverTest.Companion.consumerRecord
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MessageBuilderTest {
    @Test
    fun `should throw exception for unknown class`(){
        assertThrows<RuntimeException> {
            fromConsumerRecord(consumerRecord("key","value"), Unknown::class.java)
        }
    }

    class Unknown : KafkaMessage {
        override fun key(): String = "key"
        override fun value(): String = "value"

    }
}