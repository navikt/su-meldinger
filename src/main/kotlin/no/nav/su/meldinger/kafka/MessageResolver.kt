package no.nav.su.meldinger.kafka

import org.json.JSONObject

class MessageResolver {
    companion object {
        fun <T> compatible(clazz: Class<T>, jsonObject: JSONObject): Boolean {
            return jsonObject.propertyAmountEquals(clazz) && jsonObject.keySet().all {
                clazz.hasField(it)
            }
        }

        private fun <T> JSONObject.propertyAmountEquals(clazz: Class<T>): Boolean {
            return this.keySet().size == clazz.declaredFields.filter {
                it.name != "Companion" //Remove companion members from comparison
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
}