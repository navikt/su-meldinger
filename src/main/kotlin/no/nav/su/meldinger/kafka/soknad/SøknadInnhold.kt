package no.nav.su.meldinger.kafka.soknad

import org.json.JSONArray
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
        val boforhold: Boforhold,
        val utenlandsopphold: Utenlandsopphold,
        val oppholdstillatelse: Oppholdstillatelse,
        val inntektPensjonFormue: InntektPensjonFormue
) : toJson<SøknadInnhold> {
    override fun toJson() = """
        {
            "$personopplysningerKey": ${personopplysninger.toJson()},
            "$boforholdKey": ${boforhold.toJson()},
            "$utenlandsoppholdKey": ${utenlandsopphold.toJson()},
            "$oppholdstillatelseKey": ${oppholdstillatelse.toJson()},
            "$inntektPensjonFormueKey": ${inntektPensjonFormue.toJson()}
        }
    """.trimIndent()

    companion object : fromJson<SøknadInnhold> {
        internal const val personopplysningerKey = "personopplysninger"
        internal const val boforholdKey = "boforhold"
        internal const val utenlandsoppholdKey = "utenlandsopphold"
        internal const val oppholdstillatelseKey = "oppholdstillatelse"
        internal const val inntektPensjonFormueKey = "inntektPensjonFormue"
        override fun fromJson(jsonObject: JSONObject) = SøknadInnhold(
                personopplysninger = Personopplysninger.fromJson(jsonObject.getJSONObject(personopplysningerKey)),
                boforhold = Boforhold.fromJson(jsonObject.getJSONObject(boforholdKey)),
                utenlandsopphold = Utenlandsopphold.fromJson(jsonObject.getJSONObject(utenlandsoppholdKey)),
                oppholdstillatelse = Oppholdstillatelse.fromJson(jsonObject.getJSONObject(oppholdstillatelseKey)),
                inntektPensjonFormue = InntektPensjonFormue.fromJson(jsonObject.getJSONObject(inntektPensjonFormueKey))
        )
    }

    override fun toString() = toJson()

}

data class Personopplysninger(
        val fnr: String,
        val fornavn: String,
        val mellomnavn: String,
        val etternavn: String,
        val telefonnummer: String,
        val gateadresse: String,
        val postnummer: String,
        val poststed: String,
        val bruksenhet: String,
        val bokommune: String,
        val flyktning: Boolean,
        val borFastINorge: Boolean,
        val statsborgerskap: String
) : toJson<Personopplysninger> {
    override fun toJson() = """
        {
            "$fnrKey": "$fnr",
            "$fornavnKey": "$fornavn",
            "$mellomnavnKey": "$mellomnavn",
            "$etternavnKey": "$etternavn",
            "$telefonnummerKey": "$telefonnummer",
            "$gateadresseKey": "$gateadresse",
            "$postnummerKey": "$postnummer",
            "$poststedKey": "$poststed",
            "$bruksenhetKey": "$bruksenhet",
            "$bokommuneKey": "$bokommune",
            "$flyktningKey": $flyktning,
            "$borFastINorgeKey": $borFastINorge,
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
        internal const val flyktningKey = "flyktning"
        internal const val borFastINorgeKey = "borFastINorge"
        internal const val statsborgerskapKey = "statsborgerskap"

        override fun fromJson(jsonObject: JSONObject) = Personopplysninger(
                fnr = jsonObject.getString(fnrKey),
                fornavn = jsonObject.getString(fornavnKey),
                mellomnavn = jsonObject.getString(mellomnavnKey),
                etternavn = jsonObject.getString(etternavnKey),
                telefonnummer = jsonObject.getString(telefonnummerKey),
                gateadresse = jsonObject.getString(gateadresseKey),
                postnummer = jsonObject.getString(postnummerKey),
                poststed = jsonObject.getString(poststedKey),
                bruksenhet = jsonObject.getString(bruksenhetKey),
                bokommune = jsonObject.getString(bokommuneKey),
                flyktning = jsonObject.getBoolean(flyktningKey),
                borFastINorge = jsonObject.getBoolean(borFastINorgeKey),
                statsborgerskap = jsonObject.getString(statsborgerskapKey)
        )
    }
}

data class Oppholdstillatelse(
        val harVarigOpphold: Boolean,
        val utløpsDato: LocalDate,
        val søktOmForlengelse: Boolean
) : toJson<Oppholdstillatelse> {
    override fun toJson() = """
        {
            "$harVarigOppholdKey": $harVarigOpphold,
            "$utløpsDatoKey" : "$utløpsDato",
            "$søktOmForlengelseKey": $søktOmForlengelse
        }
    """.trimIndent()

    companion object : fromJson<Oppholdstillatelse> {
        internal const val harVarigOppholdKey = "harVarigOpphold"
        internal const val utløpsDatoKey = "utløpsdato"
        internal const val søktOmForlengelseKey = "søktOmForlengelse"
        override fun fromJson(jsonObject: JSONObject) = Oppholdstillatelse(
                harVarigOpphold = jsonObject.getBoolean(harVarigOppholdKey),
                utløpsDato = LocalDate.parse(jsonObject.getString(utløpsDatoKey), ISO_DATE),
                søktOmForlengelse = jsonObject.getBoolean(søktOmForlengelseKey)
        )
    }
}

data class Boforhold(
        val delerBolig: Boolean,
        val borSammenMed: List<String>,
        val delerBoligMed: List<DelerBoligMedPerson>
) : toJson<Boforhold> {
    override fun toJson() = """
        {
            "$delerBoligKey": $delerBolig,
            "$borSammenMedKey": [${borSammenMed.joinToString(",")}],
            "$delerBoligMedKey": ${delerBoligMed.listToJson()}
    }
    """.trimIndent()

    companion object : fromJson<Boforhold> {
        internal const val delerBoligKey = "delerBolig"
        internal const val borSammenMedKey = "borSammenMed"
        internal const val delerBoligMedKey = "delerBoligMed"
        override fun fromJson(jsonObject: JSONObject) = Boforhold(
                delerBolig = jsonObject.getBoolean(delerBoligKey),
                borSammenMed = jsonObject.getJSONArray(borSammenMedKey).toList() as List<String>,
                delerBoligMed = DelerBoligMedPerson.fromJsonArray(jsonObject.getJSONArray(delerBoligMedKey))
        )
    }

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

            fun fromJsonArray(jsonArray: JSONArray): List<DelerBoligMedPerson> {
                var list = mutableListOf<DelerBoligMedPerson>()
                for (i in 0 until jsonArray.count()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    list.add(fromJson(jsonObject))
                }
                return list
            }

        }
    }
}

data class Utenlandsopphold(
        val utenlandsopphold: Boolean,
        val registrertePerioder: List<UtenlandsoppholdPeriode>,
        val planlagtUtenlandsopphold: Boolean,
        val planlagtePerioder: List<UtenlandsoppholdPeriode>
) : toJson<Utenlandsopphold> {
    override fun toJson() = """
        {
            "$utenlandsoppholdKey": $utenlandsopphold,
            "$registrertePerioderKey": ${registrertePerioder.listToJson()},
            "$planlagtUtenlandsoppholdKey": $planlagtUtenlandsopphold,
            "$planlagtePerioderKey": ${planlagtePerioder.listToJson()}
        }
    """.trimIndent()

    companion object : fromJson<Utenlandsopphold> {
        internal const val utenlandsoppholdKey = "utenlandsopphold"
        internal const val registrertePerioderKey = "registrertePerioder"
        internal const val planlagtUtenlandsoppholdKey = "planlagteUtenlandsopphold"
        internal const val planlagtePerioderKey = "planlagtePerioder"
        override fun fromJson(jsonObject: JSONObject) = Utenlandsopphold(
                utenlandsopphold = jsonObject.getBoolean(utenlandsoppholdKey),
                registrertePerioder = UtenlandsoppholdPeriode.fromJsonArray(jsonObject.getJSONArray(registrertePerioderKey)),
                planlagtUtenlandsopphold = jsonObject.getBoolean(planlagtUtenlandsoppholdKey),
                planlagtePerioder = UtenlandsoppholdPeriode.fromJsonArray(jsonObject.getJSONArray(planlagtePerioderKey))
        )
    }
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

        fun fromJsonArray(jsonArray: JSONArray): List<UtenlandsoppholdPeriode> {
            var list = mutableListOf<UtenlandsoppholdPeriode>()
            for (i in 0 until jsonArray.count()) {
                val jsonObject = jsonArray.getJSONObject(i)
                list.add(fromJson(jsonObject))
            }
            return list
        }
    }
}

data class ForNav(
        val målform: String,
        val søkerMøttPersonlig: Boolean,
        val harFullmektigMøtt: Boolean,
        val erPassSjekket: Boolean,
        val forNAVMerknader: String
) : toJson<ForNav> {
    override fun toJson() = """
        {
            "$målformKey": "$målform",
            "$søkerMøttPersonligKey": $søkerMøttPersonlig,
            "$harFullmektigMøttKey": $harFullmektigMøtt,
            "$erPassSjekketKey": $erPassSjekket,
            "$forNAVMerknaderKey": "$forNAVMerknader"
        }
    """.trimIndent()

    companion object : fromJson<ForNav> {
        internal const val målformKey = "målform"
        internal const val søkerMøttPersonligKey = "søkerMøttPersonlig"
        internal const val harFullmektigMøttKey = "harFullmektigMøtt"
        internal const val erPassSjekketKey = "erPassSjekket"
        internal const val forNAVMerknaderKey = "forNAVMerknader"
        override fun fromJson(jsonObject: JSONObject) = ForNav(
                målform = jsonObject.getString(målformKey),
                søkerMøttPersonlig = jsonObject.getBoolean(søkerMøttPersonligKey),
                harFullmektigMøtt = jsonObject.getBoolean(harFullmektigMøttKey),
                erPassSjekket = jsonObject.getBoolean(erPassSjekketKey),
                forNAVMerknader = jsonObject.getString(forNAVMerknaderKey)
        )
    }
}

data class InntektPensjonFormue(
        val framsattKravAnnenYtelse: Boolean,
        val framsattKravAnnenYtelseBegrunnelse: String,
        val harInntekt: Boolean,
        val inntektBeløp: Double,
        val harPensjon: Boolean,
        val pensjonsOrdning: List<PensjonsOrdningBeløp>,
        val sumInntektOgPensjon: Double,
        val harFormueEiendom: Boolean,
        val harFinansFormue: Boolean,
        val formueBeløp: Double,
        val harAnnenFormue: Boolean,
        val annenFormue: List<AnnenFormue>
) : toJson<InntektPensjonFormue> {
    override fun toJson() = """
        {
            "$framsattKravAnnenYtelseKey": $framsattKravAnnenYtelse,
            "$framsattKravAnnenYtelseBegrunnelseKey": "$framsattKravAnnenYtelseBegrunnelse",
            "$harInntektKey": $harInntekt,
            "$inntektBeløpKey": $inntektBeløp,
            "$harPensjonKey": $harPensjon,
            "$pensjonsOrdningKey": ${pensjonsOrdning.listToJson()},
            "$sumInntektOgPensjonKey": $sumInntektOgPensjon,
            "$harFormueEiendomKey": $harFormueEiendom,
            "$harFinansFormueKey": $harFinansFormue,
            "$formueBeløpKey": $formueBeløp,
            "$harAnnenFormueKey": $harAnnenFormue,
            "$annenFormueKey": ${annenFormue.listToJson()}
        }
    """.trimIndent()

    companion object : fromJson<InntektPensjonFormue> {
        internal const val framsattKravAnnenYtelseKey = "framsattKravAnnenYtelse"
        internal const val framsattKravAnnenYtelseBegrunnelseKey = "framsattKravAnnenYtelseBegrunnelse"
        internal const val harInntektKey = "harInntekt"
        internal const val inntektBeløpKey = "inntektBeløp"
        internal const val harPensjonKey = "harPensjon"
        internal const val pensjonsOrdningKey = "pensjonsOrdning"
        internal const val sumInntektOgPensjonKey = "sumInntektOgPensjon"
        internal const val harFormueEiendomKey = "harFormueEiendom"
        internal const val harFinansFormueKey = "harFinansFormue"
        internal const val formueBeløpKey = "formueBeløp"
        internal const val harAnnenFormueKey = "harAnnenFormue"
        internal const val annenFormueKey = "annenFormue"
        override fun fromJson(jsonObject: JSONObject) = InntektPensjonFormue(
                framsattKravAnnenYtelse = jsonObject.getBoolean(framsattKravAnnenYtelseKey),
                framsattKravAnnenYtelseBegrunnelse = jsonObject.getString(framsattKravAnnenYtelseBegrunnelseKey),
                harInntekt = jsonObject.getBoolean(harInntektKey),
                inntektBeløp = jsonObject.getDouble(inntektBeløpKey),
                harPensjon = jsonObject.getBoolean(harPensjonKey),
                pensjonsOrdning = PensjonsOrdningBeløp.fromJsonArray(jsonObject.getJSONArray(pensjonsOrdningKey)),
                sumInntektOgPensjon = jsonObject.getDouble(sumInntektOgPensjonKey),
                harFormueEiendom = jsonObject.getBoolean(harFormueEiendomKey),
                harFinansFormue = jsonObject.getBoolean(harFinansFormueKey),
                formueBeløp = jsonObject.getDouble(formueBeløpKey),
                harAnnenFormue = jsonObject.getBoolean(harAnnenFormueKey),
                annenFormue = AnnenFormue.fromJsonArray(jsonObject.getJSONArray(annenFormueKey)))
    }
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

        fun fromJsonArray(jsonArray: JSONArray): List<AnnenFormue> {
            var list = mutableListOf<AnnenFormue>()
            for (i in 0 until jsonArray.count()) {
                val jsonObject = jsonArray.getJSONObject(i)
                list.add(fromJson(jsonObject))
            }
            return list
        }
    }
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

        fun fromJsonArray(jsonArray: JSONArray): List<PensjonsOrdningBeløp> {
            var list = mutableListOf<PensjonsOrdningBeløp>()
            for (i in 0 until jsonArray.count()) {
                val jsonObject = jsonArray.getJSONObject(i)
                list.add(fromJson(jsonObject))
            }
            return list
        }
    }
}

fun <T : toJson<T>> List<T>.listToJson() = "[${this.joinToString(",") { it.toJson() }}]"