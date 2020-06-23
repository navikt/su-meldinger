package no.nav.su.meldinger.kafka.soknad

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE

interface toJson<T> {
    fun toJson(): String
}

interface fromJson<T> {
    fun fromJson(jsonObject: JSONObject): T
}

data class SøknadInnhold(
        val personopplysninger: Personopplysninger,
        val flyktningsstatus: Flyktningsstatus,
        val boforhold: Boforhold,
        val utenlandsopphold: Utenlandsopphold,
        val oppholdstillatelse: Oppholdstillatelse,
        val inntektOgPensjon: InntektOgPensjon,
        val formue: Formue,
        val forNav: ForNav
) : toJson<SøknadInnhold> {
    override fun toJson() = """
        {
            "$personopplysningerKey": ${personopplysninger.toJson()},
            "$flyktningsstatusKey": ${flyktningsstatus},
            "$boforholdKey": ${boforhold.toJson()},
            "$utenlandsoppholdKey": ${utenlandsopphold.toJson()},
            "$oppholdstillatelseKey": ${oppholdstillatelse.toJson()},
            "$inntektOgPensjonKey": ${inntektOgPensjon.toJson()},
            "$formueKey": ${formue.toJson()},
            "$forNavKey": ${forNav.toJson()}
        }
    """.trimIndent()

    companion object : fromJson<SøknadInnhold> {
        internal const val personopplysningerKey = "personopplysninger"
        internal const val flyktningsstatusKey = "flyktningsstatus"
        internal const val boforholdKey = "boforhold"
        internal const val utenlandsoppholdKey = "utenlandsopphold"
        internal const val oppholdstillatelseKey = "oppholdstillatelse"
        internal const val inntektOgPensjonKey = "inntektOgPensjon"
        internal const val formueKey = "formue"
        internal const val forNavKey = "forNav"
        override fun fromJson(jsonObject: JSONObject) = SøknadInnhold(
                personopplysninger = Personopplysninger.fromJson(jsonObject.getJSONObject(personopplysningerKey)),
                flyktningsstatus = Flyktningsstatus.fromJson(jsonObject.getJSONObject(flyktningsstatusKey)),
                boforhold = Boforhold.fromJson(jsonObject.getJSONObject(boforholdKey)),
                utenlandsopphold = Utenlandsopphold.fromJson(jsonObject.getJSONObject(utenlandsoppholdKey)),
                oppholdstillatelse = Oppholdstillatelse.fromJson(jsonObject.getJSONObject(oppholdstillatelseKey)),
                inntektOgPensjon = InntektOgPensjon.fromJson(jsonObject.getJSONObject(inntektOgPensjonKey)),
                formue = Formue.fromJson(jsonObject.getJSONObject(formueKey)),
                forNav = ForNav.fromJson(jsonObject.getJSONObject(forNavKey))
        )
    }

    override fun toString() = toJson()
}

data class Flyktningsstatus(
        val registrertFlyktning: Boolean
) : toJson<Flyktningsstatus> {
    override fun toJson() = """
        {
              "$registrertFlyktningKey": "$registrertFlyktning"
        }
    """.trimIndent()

    companion object : fromJson<Flyktningsstatus> {
        internal const val registrertFlyktningKey = "registrertFlyktning"
        override fun fromJson(jsonObject: JSONObject) = Flyktningsstatus(
                registrertFlyktning = jsonObject.getBoolean(registrertFlyktningKey)
        )
    }

    override fun toString() = toJson()
}

data class Personopplysninger(
        val fnr: String,
        val fornavn: String,
        val mellomnavn: String? = null,
        val etternavn: String,
        val telefonnummer: String,
        val gateadresse: String,
        val postnummer: String,
        val poststed: String,
        val bruksenhet: String? = null,
        val bokommune: String,
        val statsborgerskap: String
) : toJson<Personopplysninger> {
    override fun toJson() = """
        {
            "$fnrKey": "$fnr",
            "$fornavnKey": "$fornavn",
            "$mellomnavnKey": ${jsonStringOrNull(mellomnavn)},
            "$etternavnKey": "$etternavn",
            "$telefonnummerKey": "$telefonnummer",
            "$gateadresseKey": "$gateadresse",
            "$postnummerKey": "$postnummer",
            "$poststedKey": "$poststed",
            "$bruksenhetKey": ${jsonStringOrNull(bruksenhet)},
            "$bokommuneKey": "$bokommune",
            "$statsborgerskapKey": "$statsborgerskap"
        }
    """.trimIndent()

    companion object : fromJson<Personopplysninger> {
        internal const val fnrKey = "fnr"
        internal const val fornavnKey = "fornavn"
        internal const val mellomnavnKey = "mellomnavn"
        internal const val etternavnKey = "etternavn"
        internal const val telefonnummerKey = "telefonnummer"
        internal const val gateadresseKey = "gateadresse"
        internal const val postnummerKey = "postnummer"
        internal const val poststedKey = "poststed"
        internal const val bruksenhetKey = "bruksenhet"
        internal const val bokommuneKey = "bokommune"
        internal const val statsborgerskapKey = "statsborgerskap"

        override fun fromJson(jsonObject: JSONObject) = Personopplysninger(
                fnr = jsonObject.getString(fnrKey),
                fornavn = jsonObject.getString(fornavnKey),
                mellomnavn = jsonObject.optString(mellomnavnKey, null),
                etternavn = jsonObject.getString(etternavnKey),
                telefonnummer = jsonObject.getString(telefonnummerKey),
                gateadresse = jsonObject.getString(gateadresseKey),
                postnummer = jsonObject.getString(postnummerKey),
                poststed = jsonObject.getString(poststedKey),
                bruksenhet = jsonObject.optString(bruksenhetKey, null),
                bokommune = jsonObject.getString(bokommuneKey),
                statsborgerskap = jsonObject.getString(statsborgerskapKey)
        )
    }

    override fun toString() = toJson()
}

data class Oppholdstillatelse(
        val harVarigOpphold: Boolean,
        val utløpsDato: LocalDate? = null,
        val søktOmForlengelse: Boolean? = null
) : toJson<Oppholdstillatelse> {
    override fun toJson() = """
        {
            "$harVarigOppholdKey": $harVarigOpphold,
            "$utløpsDatoKey" : ${jsonStringOrNull(utløpsDato)},
            "$søktOmForlengelseKey": ${getOrNull(søktOmForlengelse)} 
        }
    """.trimIndent()

    companion object : fromJson<Oppholdstillatelse> {
        internal const val harVarigOppholdKey = "harVarigOpphold"
        internal const val utløpsDatoKey = "utløpsdato"
        internal const val søktOmForlengelseKey = "søktOmForlengelse"
        override fun fromJson(jsonObject: JSONObject) = Oppholdstillatelse(
                harVarigOpphold = jsonObject.getBoolean(harVarigOppholdKey),
                utløpsDato = localDateOrNull(jsonObject.optString(utløpsDatoKey, null)),
                søktOmForlengelse = jsonObject.optNullableBoolean(søktOmForlengelseKey)
        )
    }

    override fun toString() = toJson()
}

data class Boforhold(
        val delerBolig: Boolean,
        val borSammenMed: List<String>? = null,
        val delerBoligMed: List<DelerBoligMedPerson>? = null,
        val borFastINorge: Boolean
) : toJson<Boforhold> {
    override fun toJson() = """
        {
            "$delerBoligKey": $delerBolig,
            "$borSammenMedKey": ${if (borSammenMed != null) "${borSammenMed.map { "\"$it\"" }.toList()}" else null},
            "$delerBoligMedKey": ${delerBoligMed?.listToJson()},
            "$borFastINorgeKey": $borFastINorge
        }
    """.trimIndent()

    companion object : fromJson<Boforhold> {
        internal const val delerBoligKey = "delerBolig"
        internal const val borSammenMedKey = "borSammenMed"
        internal const val delerBoligMedKey = "delerBoligMed"
        internal const val borFastINorgeKey = "borFastINorge"
        override fun fromJson(jsonObject: JSONObject) = Boforhold(
                delerBolig = jsonObject.getBoolean(delerBoligKey),
                borSammenMed = if (jsonObject.optJSONArray(borSammenMedKey) != null) jsonObject.getJSONArray(borSammenMedKey).toList() as List<String> else null,
                delerBoligMed = DelerBoligMedPerson.fromJsonArray(jsonObject.optJSONArray(delerBoligMedKey)),
                borFastINorge = jsonObject.getBoolean(borFastINorgeKey)
        )
    }

    override fun toString() = toJson()

    data class DelerBoligMedPerson(
            val fnr: String,
            val navn: String
    ) : toJson<DelerBoligMedPerson> {
        override fun toJson() = """
            {
                "$fnrKey": "$fnr",
                "$navnKey": "$navn"
            }
        """.trimIndent()

        companion object : fromJson<DelerBoligMedPerson> {
            internal const val fnrKey = "fnr"
            internal const val navnKey = "navn"
            override fun fromJson(jsonObject: JSONObject) = DelerBoligMedPerson(
                    fnr = jsonObject.getString(fnrKey),
                    navn = jsonObject.getString(navnKey)
            )
        }

        override fun toString() = toJson()
    }
}

data class Utenlandsopphold(
        val registrertePerioder: List<UtenlandsoppholdPeriode>? = null,
        val planlagtePerioder: List<UtenlandsoppholdPeriode>? = null
) : toJson<Utenlandsopphold> {
    override fun toJson() = """
        {
            "$registrertePerioderKey": ${registrertePerioder?.listToJson()},
            "$planlagtePerioderKey": ${planlagtePerioder?.listToJson()}
        }
    """.trimIndent()

    companion object : fromJson<Utenlandsopphold> {
        internal const val registrertePerioderKey = "registrertePerioder"
        internal const val planlagtePerioderKey = "planlagtePerioder"
        override fun fromJson(jsonObject: JSONObject) = Utenlandsopphold(
                registrertePerioder = UtenlandsoppholdPeriode.fromJsonArray(jsonObject.optJSONArray(registrertePerioderKey)),
                planlagtePerioder = UtenlandsoppholdPeriode.fromJsonArray(jsonObject.optJSONArray(planlagtePerioderKey))
        )
    }

    override fun toString() = toJson()
}

data class UtenlandsoppholdPeriode(
        val utreisedato: LocalDate,
        val innreisedato: LocalDate
) : toJson<UtenlandsoppholdPeriode> {
    override fun toJson(): String = """
          {
            "$utreisedatoKey": "$utreisedato",
            "$innreisedatoKey": "$innreisedato"
          }
      """.trimIndent()

    companion object : fromJson<UtenlandsoppholdPeriode> {
        internal const val utreisedatoKey = "utreisedato"
        internal const val innreisedatoKey = "innreisedato"
        override fun fromJson(jsonObject: JSONObject) = UtenlandsoppholdPeriode(
                utreisedato = LocalDate.parse(jsonObject.getString(utreisedatoKey), ISO_DATE),
                innreisedato = LocalDate.parse(jsonObject.getString(innreisedatoKey), ISO_DATE)
        )
    }

    override fun toString() = toJson()
}

data class ForNav(
        val målform: String,
        val søkerMøttPersonlig: Boolean,
        val harFullmektigMøtt: Boolean,
        val erPassSjekket: Boolean,
        val merknader: String? = null
) : toJson<ForNav> {
    override fun toJson() = """
        {
            "$målformKey": "$målform",
            "$søkerMøttPersonligKey": $søkerMøttPersonlig,
            "$harFullmektigMøttKey": $harFullmektigMøtt,
            "$erPassSjekketKey": $erPassSjekket,
            "$merknaderKey": ${jsonStringOrNull(merknader)}
        }
    """.trimIndent()

    companion object : fromJson<ForNav> {
        internal const val målformKey = "målform"
        internal const val søkerMøttPersonligKey = "søkerMøttPersonlig"
        internal const val harFullmektigMøttKey = "harFullmektigMøtt"
        internal const val erPassSjekketKey = "erPassSjekket"
        internal const val merknaderKey = "merknader"
        override fun fromJson(jsonObject: JSONObject) = ForNav(
                målform = jsonObject.getString(målformKey),
                søkerMøttPersonlig = jsonObject.getBoolean(søkerMøttPersonligKey),
                harFullmektigMøtt = jsonObject.getBoolean(harFullmektigMøttKey),
                erPassSjekket = jsonObject.getBoolean(erPassSjekketKey),
                merknader = jsonObject.optString(merknaderKey, null)
        )
    }

    override fun toString() = toJson()
}

data class InntektOgPensjon(
        val framsattKravAnnenYtelse: Boolean,
        val framsattKravAnnenYtelseBegrunnelse: String? = null,
        val harInntekt: Boolean,
        val inntektBeløp: Number? = null,
        val harPensjon: Boolean,
        val pensjonsOrdning: List<PensjonsOrdningBeløp>? = null,
        val sumInntektOgPensjon: Number,
        val harSosialStønad: Boolean
) : toJson<InntektOgPensjon> {
    override fun toJson() = """
        {
            "$framsattKravAnnenYtelseKey": $framsattKravAnnenYtelse,
            "$framsattKravAnnenYtelseBegrunnelseKey": ${jsonStringOrNull(framsattKravAnnenYtelseBegrunnelse)},
            "$harInntektKey": $harInntekt,
            "$inntektBeløpKey": ${getOrNull(inntektBeløp)},
            "$harPensjonKey": $harPensjon,
            "$pensjonsOrdningKey": ${pensjonsOrdning?.listToJson()},
            "$sumInntektOgPensjonKey": $sumInntektOgPensjon,
            "$harSosialStønadKey": $harSosialStønad
        }
    """.trimIndent()

    companion object : fromJson<InntektOgPensjon> {
        internal const val framsattKravAnnenYtelseKey = "framsattKravAnnenYtelse"
        internal const val framsattKravAnnenYtelseBegrunnelseKey = "framsattKravAnnenYtelseBegrunnelse"
        internal const val harInntektKey = "harInntekt"
        internal const val inntektBeløpKey = "inntektBeløp"
        internal const val harPensjonKey = "harPensjon"
        internal const val pensjonsOrdningKey = "pensjonsOrdning"
        internal const val sumInntektOgPensjonKey = "sumInntektOgPensjon"
        internal const val harSosialStønadKey = "harSosialStønad"
        override fun fromJson(jsonObject: JSONObject) = InntektOgPensjon(
                framsattKravAnnenYtelse = jsonObject.getBoolean(framsattKravAnnenYtelseKey),
                framsattKravAnnenYtelseBegrunnelse = jsonObject.optString(framsattKravAnnenYtelseBegrunnelseKey, null),
                harInntekt = jsonObject.getBoolean(harInntektKey),
                inntektBeløp = jsonObject.optNullableNumber(inntektBeløpKey),
                harPensjon = jsonObject.getBoolean(harPensjonKey),
                pensjonsOrdning = PensjonsOrdningBeløp.fromJsonArray(jsonObject.optJSONArray(pensjonsOrdningKey)),
                sumInntektOgPensjon = jsonObject.getDouble(sumInntektOgPensjonKey),
                harSosialStønad = jsonObject.getBoolean(harSosialStønadKey)
        )
    }

    override fun toString() = toJson()
}


data class Formue(
        val harFormueEiendom: Boolean,
        val harFinansformue: Boolean,
        val formueBeløp: Number? = null,
        val harAnnenFormue: Boolean,
        val annenFormue: List<AnnenFormue>? = null,
        val harDepositumskonto: Boolean,
        val depositumBeløp: Number? = null
) : toJson<Formue> {
    override fun toJson() = """
        {
            "$harFormueEiendomKey": $harFormueEiendom,
            "$harFinansFormueKey": $harFinansformue,
            "$formueBeløpKey": ${getOrNull(formueBeløp)},
            "$harAnnenFormueKey": $harAnnenFormue,
            "$annenFormueKey": ${annenFormue?.listToJson()},
            "$harDepositumskontoKey": $harDepositumskonto,
            "$depositumBeløpKey": ${getOrNull(depositumBeløp)}
        }
    """.trimIndent()

    companion object : fromJson<Formue> {
        internal const val harFormueEiendomKey = "harFormueEiendom"
        internal const val harFinansFormueKey = "harFinansformue"
        internal const val formueBeløpKey = "formueBeløp"
        internal const val harAnnenFormueKey = "harAnnenFormue"
        internal const val annenFormueKey = "annenFormue"
        internal const val harDepositumskontoKey = "harDepositumskonto"
        internal const val depositumBeløpKey = "depositumBeløp"
        override fun fromJson(jsonObject: JSONObject) = Formue(
                harFormueEiendom = jsonObject.getBoolean(harFormueEiendomKey),
                harFinansformue = jsonObject.getBoolean(harFinansFormueKey),
                formueBeløp = jsonObject.optNullableNumber(formueBeløpKey),
                harAnnenFormue = jsonObject.getBoolean(harAnnenFormueKey),
                annenFormue = AnnenFormue.fromJsonArray(jsonObject.optJSONArray(annenFormueKey)),
                harDepositumskonto = jsonObject.getBoolean(harDepositumskontoKey),
                depositumBeløp = jsonObject.optNullableNumber(depositumBeløpKey)
        )
    }

    override fun toString() = toJson()
}

data class AnnenFormue(
        val typeFormue: String,
        val skattetakst: Double
) : toJson<AnnenFormue> {
    override fun toJson() = """
        {
            "$typeFormueKey": "$typeFormue",
            "$skattetakstKey": $skattetakst
        }
    """.trimIndent()

    companion object : fromJson<AnnenFormue> {
        internal const val typeFormueKey = "typeFormue"
        internal const val skattetakstKey = "skattetakst"
        override fun fromJson(jsonObject: JSONObject) = AnnenFormue(
                typeFormue = jsonObject.getString(typeFormueKey),
                skattetakst = jsonObject.getDouble(skattetakstKey)
        )
    }

    override fun toString() = toJson()
}


data class PensjonsOrdningBeløp(
        val ordning: String,
        val beløp: Double
) : toJson<PensjonsOrdningBeløp> {
    override fun toJson() = """
        {
            "$ordningKey": "$ordning",
            "$beløpKey": $beløp
        }
    """.trimIndent()

    companion object : fromJson<PensjonsOrdningBeløp> {
        internal const val ordningKey = "ordning"
        internal const val beløpKey = "beløp"
        override fun fromJson(jsonObject: JSONObject) = PensjonsOrdningBeløp(
                ordning = jsonObject.getString(ordningKey),
                beløp = jsonObject.getDouble(beløpKey)
        )
    }

    override fun toString() = toJson()
}

fun <T : toJson<T>> List<T>.listToJson() = "[${this.joinToString(",") { it.toJson() }}]"

fun JSONObject.optNullableBoolean(key: String): Boolean? {
    return try {
        this.getBoolean(key)
    } catch (e: JSONException) {
        null
    }
}

fun JSONObject.optNullableNumber(key: String): Number? {
    return try {
        this.getNumber(key)
    } catch (e: JSONException) {
        null
    }
}

fun <T> fromJson<T>.fromJsonArray(jsonArray: JSONArray?): List<T>? {
    if (jsonArray == null) return null
    var list = mutableListOf<T>()
    for (i in 0 until jsonArray.count()) {
        val jsonObject = jsonArray.getJSONObject(i)
        list.add(fromJson(jsonObject))
    }
    return list
}

fun localDateOrNull(any: String?): LocalDate? = when (getOrNull(any)) {
    null -> null
    else -> LocalDate.parse(any, ISO_DATE)
}

fun jsonStringOrNull(any: Any?): Any? = when (getOrNull(any)) {
    null -> null
    else -> "\"$any\""
}

fun getOrNull(any: Any?): Any? = when (any) {
    null -> null
    else -> any
}