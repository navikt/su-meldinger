package no.nav.su.meldinger.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.json.JSONObject

@Deprecated("Will be deleted, start using sealed class Soknadmelding instead")
class MessageResolver {
    fun <T> compatible(consumerRecord: ConsumerRecord<String, String>, clazz: Class<T>): Boolean {
        val jsonObject = JSONObject(consumerRecord.value())
        return jsonObject.propertyAmountEquals(clazz) && jsonObject.keySet().all {
            clazz.hasField(it)
        }
    }

    private fun <T> JSONObject.propertyAmountEquals(clazz: Class<T>): Boolean {
        return this.keySet().size == clazz.declaredFields.filter {
            it.name != "Companion" && it.name != "requiredFields" && it.name != "forbiddenFields" //Remove companion members from comparison
        }.size
    }

    private fun <T> Class<T>.hasField(fieldName: String): Boolean {
        try {
            this.getDeclaredField(fieldName)
        } catch (e: NoSuchFieldException) {
            return false
        }
        return true
    }
}