package no.nav.su.meldinger.kafka.soknad

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE

interface ToJson<T> {
    fun toJson(): String
}

interface FromJson<T> {
    fun fromJson(jsonObject: JSONObject): T
}

data class SøknadInnhold(
        val uførevedtak: Uførevedtak,
        val personopplysninger: Personopplysninger,
        val flyktningsstatus: Flyktningsstatus,
        val boforhold: Boforhold,
        val utenlandsopphold: Utenlandsopphold,
        val oppholdstillatelse: Oppholdstillatelse,
        val inntektOgPensjon: InntektOgPensjon,
        val formue: Formue,
        val forNav: ForNav
) : ToJson<SøknadInnhold> {

    override fun toJson() = """
        {
            "$uførevedtakKey": ${uførevedtak.toJson()},
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

    companion object : FromJson<SøknadInnhold> {
        internal const val uførevedtakKey = "uførevedtak"
        internal const val personopplysningerKey = "personopplysninger"
        internal const val flyktningsstatusKey = "flyktningsstatus"
        internal const val boforholdKey = "boforhold"
        internal const val utenlandsoppholdKey = "utenlandsopphold"
        internal const val oppholdstillatelseKey = "oppholdstillatelse"
        internal const val inntektOgPensjonKey = "inntektOgPensjon"
        internal const val formueKey = "formue"
        internal const val forNavKey = "forNav"
        override fun fromJson(jsonObject: JSONObject) = SøknadInnhold(
                uførevedtak = Uførevedtak.fromJson(jsonObject.getJSONObject(uførevedtakKey)),
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

data class Uførevedtak(
        val harUførevedtak: Boolean
) : ToJson<Flyktningsstatus> {
    override fun toJson() = """
        {
            "$harUførevedtakKey": $harUførevedtak
        }
    """.trimIndent()

    companion object : FromJson<Uførevedtak> {
        internal const val harUførevedtakKey = "harUførevedtak"
        override fun fromJson(jsonObject: JSONObject) = Uførevedtak(
                harUførevedtak = jsonObject.getBoolean(harUførevedtakKey)
        )
    }

    override fun toString() = toJson()
}

data class Flyktningsstatus(
        val registrertFlyktning: Boolean
) : ToJson<Flyktningsstatus> {
    override fun toJson() = """
        {
            "$registrertFlyktningKey": $registrertFlyktning
        }
    """.trimIndent()

    companion object : FromJson<Flyktningsstatus> {
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
) : ToJson<Personopplysninger> {
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

    companion object : FromJson<Personopplysninger> {
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
                mellomnavn = jsonObject.optNullableString(mellomnavnKey),
                etternavn = jsonObject.getString(etternavnKey),
                telefonnummer = jsonObject.getString(telefonnummerKey),
                gateadresse = jsonObject.getString(gateadresseKey),
                postnummer = jsonObject.getString(postnummerKey),
                poststed = jsonObject.getString(poststedKey),
                bruksenhet = jsonObject.optNullableString(bruksenhetKey),
                bokommune = jsonObject.getString(bokommuneKey),
                statsborgerskap = jsonObject.getString(statsborgerskapKey)
        )
    }

    override fun toString() = toJson()
}

data class Oppholdstillatelse(
        val erNorskStatsborger: Boolean,
        val harOppholdstillatelse: Boolean? = null,
        val oppholdstillatelseType: OppholdstillatelseType? = null,
        val oppholdstillatelseMindreEnnTreMåneder: Boolean? = null,
        val oppholdstillatelseForlengelse: Boolean? = null,
        val statsborgerskapAndreLand: Boolean,
        val statsborgerskapAndreLandFritekst: String? = null
) : ToJson<Oppholdstillatelse> {
    override fun toJson() = """
        {
            "$erNorskStatsborgerKey": $erNorskStatsborger,
            "$harOppholdstillatelseKey": $harOppholdstillatelse,
            "$oppholdstillatelseTypeKey": ${jsonStringOrNull(oppholdstillatelseType?.value)},
            "$oppholdstillatelseMindreEnnTreMånederKey": $oppholdstillatelseMindreEnnTreMåneder,
            "$oppholdstillatelseForlengelseKey": $oppholdstillatelseForlengelse,
            "$statsborgerskapAndreLandKey": $statsborgerskapAndreLand,
            "$statsborgerskapAndreLandFritekstKey": ${jsonStringOrNull(statsborgerskapAndreLandFritekst)}
        }
    """.trimIndent()

    companion object : FromJson<Oppholdstillatelse> {
        internal const val erNorskStatsborgerKey = "erNorskStatsborger"
        internal const val harOppholdstillatelseKey = "harOppholdstillatelse"
        internal const val oppholdstillatelseTypeKey = "typeOppholdstillatelse"
        internal const val oppholdstillatelseMindreEnnTreMånederKey = "oppholdstillatelseMindreEnnTreMåneder"
        internal const val oppholdstillatelseForlengelseKey = "oppholdstillatelseForlengelse"
        internal const val statsborgerskapAndreLandKey = "statsborgerskapAndreLand"
        internal const val statsborgerskapAndreLandFritekstKey = "statsborgerskapAndreLandFritekst"
        override fun fromJson(jsonObject: JSONObject) = Oppholdstillatelse(
                erNorskStatsborger = jsonObject.getBoolean(erNorskStatsborgerKey),
                harOppholdstillatelse = jsonObject.optNullableBoolean(harOppholdstillatelseKey),
                oppholdstillatelseType = jsonObject.optNullableString(oppholdstillatelseTypeKey)?.let {
                    OppholdstillatelseType.fromString(it)
                },
                oppholdstillatelseMindreEnnTreMåneder = jsonObject.optNullableBoolean(oppholdstillatelseMindreEnnTreMånederKey),
                oppholdstillatelseForlengelse = jsonObject.optNullableBoolean(oppholdstillatelseForlengelseKey),
                statsborgerskapAndreLand = jsonObject.getBoolean(statsborgerskapAndreLandKey),
                statsborgerskapAndreLandFritekst = jsonObject.optNullableString(statsborgerskapAndreLandFritekstKey)
        )
    }

    override fun toString() = toJson()

    enum class OppholdstillatelseType(val value: String) {
        MIDLERTIG("midlertidig"),
        PERMANENT("permanent");

        override fun toString(): String {
            return value
        }

        companion object {
            fun fromString(value: String): OppholdstillatelseType? {
                return values().firstOrNull {
                    it.value == value.trim().toLowerCase()
                }
            }
        }
    }
}

data class Boforhold(
        val borOgOppholderSegINorge: Boolean,
        val delerBolig: Boolean,
        val delerBoligMed: DelerBoligMed? = null,
        val ektemakeEllerSamboerUnder67År: Boolean? = null,
        val ektemakeEllerSamboerUførFlyktning: Boolean? = null

) : ToJson<Boforhold> {
    override fun toJson() = """
        {
            "$borOgOppholderSegINorgeKey": $borOgOppholderSegINorge,
            "$delerBoligKey": $delerBolig,
            "$delerBoligMedKey": ${jsonStringOrNull(delerBoligMed?.value)},
            "$ektemakeEllerSamboerUnder67ÅrKey": $ektemakeEllerSamboerUnder67År,
            "$ektemakeEllerSamboerUførFlyktningKey": $ektemakeEllerSamboerUførFlyktning
        }
    """.trimIndent()

    companion object : FromJson<Boforhold> {
        internal const val borOgOppholderSegINorgeKey = "borOgOppholderSegINorge"
        internal const val delerBoligKey = "delerBoligMedVoksne"
        internal const val delerBoligMedKey = "delerBoligMed"
        internal const val ektemakeEllerSamboerUnder67ÅrKey = "ektemakeEllerSamboerUnder67År"
        internal const val ektemakeEllerSamboerUførFlyktningKey = "ektemakeEllerSamboerUførFlyktning"
        override fun fromJson(jsonObject: JSONObject) = Boforhold(
                borOgOppholderSegINorge = jsonObject.getBoolean(borOgOppholderSegINorgeKey),
                delerBolig = jsonObject.getBoolean(delerBoligKey),
                delerBoligMed = jsonObject.optNullableString(delerBoligMedKey)?.let {
                    DelerBoligMed.fromString(it)
                },
                ektemakeEllerSamboerUnder67År = jsonObject.optNullableBoolean(ektemakeEllerSamboerUnder67ÅrKey),
                ektemakeEllerSamboerUførFlyktning = jsonObject.optNullableBoolean(ektemakeEllerSamboerUførFlyktningKey)
        )
    }

    override fun toString() = toJson()

    enum class DelerBoligMed(val value: String) {
        EKTEMAKE_SAMBOER("ektemake-eller-samboer"),
        VOKSNE_BARN("voksne-barn"),
        ANNEN_VOKSEN("andre");

        override fun toString(): String {
            return value
        }

        companion object {
            fun fromString(value: String): DelerBoligMed? {
                return values().firstOrNull {
                    it.value == value.trim().toLowerCase()
                }
            }
        }
    }
}

data class Utenlandsopphold(
        val registrertePerioder: List<UtenlandsoppholdPeriode>? = null,
        val planlagtePerioder: List<UtenlandsoppholdPeriode>? = null
) : ToJson<Utenlandsopphold> {
    override fun toJson() = """
        {
            "$registrertePerioderKey": ${registrertePerioder?.listToJson()},
            "$planlagtePerioderKey": ${planlagtePerioder?.listToJson()}
        }
    """.trimIndent()

    companion object : FromJson<Utenlandsopphold> {
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
) : ToJson<UtenlandsoppholdPeriode> {
    override fun toJson(): String = """
          {
            "$utreisedatoKey": "$utreisedato",
            "$innreisedatoKey": "$innreisedato"
          }
      """.trimIndent()

    companion object : FromJson<UtenlandsoppholdPeriode> {
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
        val harFullmektigEllerVerge: Vergemål? = null
) : ToJson<ForNav> {
    override fun toJson() = """
        {
            "$harFullmektigEllerVergeKey": ${jsonStringOrNull(harFullmektigEllerVerge)}
        }
    """.trimIndent()

    companion object : FromJson<ForNav> {
        internal const val harFullmektigEllerVergeKey = "harFullmektigEllerVerge"
        override fun fromJson(jsonObject: JSONObject) = ForNav(
                harFullmektigEllerVerge = jsonObject.optNullableString(harFullmektigEllerVergeKey)?.let {
                    Vergemål.fromString(it)
                }
        )
    }

    override fun toString() = toJson()

    enum class Vergemål(val value: String) {
        FULLMEKTIG("fullmektig"),
        VERGE("verge");

        override fun toString(): String {
            return value
        }

        companion object {
            fun fromString(value: String): Vergemål? {
                return values().firstOrNull {
                    it.value == value.trim().toLowerCase()
                }
            }
        }
    }
}

data class InntektOgPensjon(
        val forventetInntekt: Number? = null,
        val tjenerPengerIUtlandetBeløp: Number? = null,
        val andreYtelserINav: String? = null,
        val andreYtelserINavBeløp: Number? = null,
        val søktAndreYtelserIkkeBehandletBegrunnelse: String? = null,
        val sosialstønadBeløp: Number? = null,
        val trygdeytelserIUtlandetBeløp: Number? = null,
        val trygdeytelserIUtlandet: String? = null,
        val trygdeytelserIUtlandetFra: String? = null,
        val pensjon: List<PensjonsOrdningBeløp>? = null
) : ToJson<InntektOgPensjon> {

    override fun toJson() = """
        {
            "$forventetInntektKey": ${jsonStringOrNull(forventetInntekt)},
            "$tjenerPengerIUtlandetBeløpKey": $tjenerPengerIUtlandetBeløp,
            "$andreYtelserINavKey": ${jsonStringOrNull(andreYtelserINav)},
            "$andreYtelserINavBeløpKey": $andreYtelserINavBeløp,
            "$søktAndreYtelserIkkeBehandletBegrunnelseKey": ${jsonStringOrNull(søktAndreYtelserIkkeBehandletBegrunnelse)},
            "$sosialstønadBeløpKey": $sosialstønadBeløp,
            "$trygdeytelserIUtlandetBeløpKey": $trygdeytelserIUtlandetBeløp,
            "$trygdeytelserIUtlandetKey": ${jsonStringOrNull(trygdeytelserIUtlandet)},
            "$trygdeytelserIUtlandetFraKey": ${jsonStringOrNull(trygdeytelserIUtlandetFra)},
            "$pensjonKey":  ${pensjon?.listToJson()}
        }
    """.trimIndent()

    companion object : FromJson<InntektOgPensjon> {
        internal const val forventetInntektKey = "forventetInntekt"
        internal const val tjenerPengerIUtlandetBeløpKey = "tjenerPengerIUtlandetBeløp"
        internal const val andreYtelserINavKey = "andreYtelserINav"
        internal const val andreYtelserINavBeløpKey = "andreYtelserINavBeløp"
        internal const val søktAndreYtelserIkkeBehandletBegrunnelseKey = "søktAndreYtelserIkkeBehandletBegrunnelse"
        internal const val sosialstønadBeløpKey = "sosialstønadBeløp"
        internal const val trygdeytelserIUtlandetBeløpKey = "trygdeytelserIUtlandetBeløp"
        internal const val trygdeytelserIUtlandetKey = "trygdeytelserIUtlandet"
        internal const val trygdeytelserIUtlandetFraKey = "trygdeytelserIUtlandetFra"
        internal const val pensjonKey = "pensjon"

        override fun fromJson(jsonObject: JSONObject) = InntektOgPensjon(
                forventetInntekt = jsonObject.optNullableNumber(forventetInntektKey),
                tjenerPengerIUtlandetBeløp = jsonObject.optNullableNumber(tjenerPengerIUtlandetBeløpKey),
                andreYtelserINav = jsonObject.optNullableString(andreYtelserINavKey),
                andreYtelserINavBeløp = jsonObject.optNullableNumber(andreYtelserINavBeløpKey),
                søktAndreYtelserIkkeBehandletBegrunnelse = jsonObject.optNullableString(søktAndreYtelserIkkeBehandletBegrunnelseKey),
                sosialstønadBeløp = jsonObject.optNullableNumber(sosialstønadBeløpKey),
                trygdeytelserIUtlandetBeløp = jsonObject.optNullableNumber(trygdeytelserIUtlandetBeløpKey),
                trygdeytelserIUtlandet = jsonObject.optNullableString(trygdeytelserIUtlandetKey),
                trygdeytelserIUtlandetFra = jsonObject.optNullableString(trygdeytelserIUtlandetFraKey),
                pensjon = PensjonsOrdningBeløp.fromJsonArray(jsonObject.optJSONArray(pensjonKey))
        )
    }
    override fun toString() = toJson()
}

data class Formue(
        val borIBolig: Boolean? = null,
        val verdiPåBolig: Number? = null,
        val boligBrukesTil: String? = null,
        val depositumsBeløp: Number? = null,
        val Kontonummer: String? = null,
        val verdiPåEiendom: Number? = null,
        val eiendomBrukesTil: String? = null,
        val verdiPåKjøretøy: Number? = null,
        val kjøretøyDeEier: String? = null,
        val innskuddsBeløp: Number? = null,
        val verdipapirBeløp: Number? = null,
        val skylderNoenMegPengerBeløp: Number? = null,
        val kontanterBeløp: Number? = null
) : ToJson<Formue> {
    override fun toJson() = """
        {
            "$borIBoligKey": $borIBolig,
            "$verdiPåBoligKey": $verdiPåBolig,
            "$boligBrukesTilKey": ${jsonStringOrNull(boligBrukesTil)},
            "$depositumsBeløpKey": $depositumsBeløp,
            "$KontonummerKey": ${jsonStringOrNull(Kontonummer)},
            "$verdiPåEiendomKey": $verdiPåEiendom,
            "$eiendomBrukesTilKey": ${jsonStringOrNull(eiendomBrukesTil)},
            "$verdiPåKjøretøyKey": $verdiPåKjøretøy,
            "$kjøretøyDeEierKey": ${jsonStringOrNull(kjøretøyDeEier)},
            "$innskuddsBeløpKey": $innskuddsBeløp,
            "$verdipapirBeløpKey": $verdipapirBeløp,
            "$skylderNoenMegPengerBeløpKey": $skylderNoenMegPengerBeløp,
            "$kontanterBeløpKey": $kontanterBeløp
        }
    """.trimIndent()

    companion object : FromJson<Formue> {
        internal const val borIBoligKey = "borIBolig"
        internal const val verdiPåBoligKey = "verdiPåBolig"
        internal const val boligBrukesTilKey = "boligBrukesTil"
        internal const val depositumsBeløpKey = "depositumsBeløp"
        internal const val KontonummerKey = "Kontonummer"
        internal const val verdiPåEiendomKey = "verdiPåEiendom"
        internal const val eiendomBrukesTilKey = "eiendomBrukesTil"
        internal const val verdiPåKjøretøyKey = "verdiPåKjøretøy"
        internal const val kjøretøyDeEierKey = "kjøretøyDeEier"
        internal const val innskuddsBeløpKey = "innskuddsBeløp"
        internal const val verdipapirBeløpKey = "verdipapirBeløp"
        internal const val skylderNoenMegPengerBeløpKey = "skylderNoenMegPengerBeløp"
        internal const val kontanterBeløpKey = "kontanterBeløp"
        override fun fromJson(jsonObject: JSONObject) = Formue(
                borIBolig = jsonObject.optNullableBoolean(borIBoligKey),
                verdiPåBolig = jsonObject.optNullableNumber(verdiPåBoligKey),
                boligBrukesTil = jsonObject.optNullableString(boligBrukesTilKey),
                depositumsBeløp = jsonObject.optNullableNumber(depositumsBeløpKey),
                Kontonummer = jsonObject.optNullableString(KontonummerKey),
                verdiPåEiendom = jsonObject.optNullableNumber(verdiPåEiendomKey),
                eiendomBrukesTil = jsonObject.optNullableString(eiendomBrukesTilKey),
                verdiPåKjøretøy = jsonObject.optNullableNumber(verdiPåKjøretøyKey),
                kjøretøyDeEier = jsonObject.optNullableString(kjøretøyDeEierKey),
                innskuddsBeløp = jsonObject.optNullableNumber(innskuddsBeløpKey),
                verdipapirBeløp = jsonObject.optNullableNumber(verdipapirBeløpKey),
                skylderNoenMegPengerBeløp = jsonObject.optNullableNumber(skylderNoenMegPengerBeløpKey),
                kontanterBeløp = jsonObject.optNullableNumber(kontanterBeløpKey)
        )
    }

    override fun toString() = toJson()
}

data class PensjonsOrdningBeløp(
        val ordning: String,
        val beløp: Double
) : ToJson<PensjonsOrdningBeløp> {
    override fun toJson() = """
        {
            "$ordningKey": "$ordning",
            "$beløpKey": $beløp
        }
    """.trimIndent()

    companion object : FromJson<PensjonsOrdningBeløp> {
        internal const val ordningKey = "ordning"
        internal const val beløpKey = "beløp"
        override fun fromJson(jsonObject: JSONObject) = PensjonsOrdningBeløp(
                ordning = jsonObject.getString(ordningKey),
                beløp = jsonObject.getDouble(beløpKey)
        )
    }

    override fun toString() = toJson()
}

fun <T : ToJson<T>> List<T>.listToJson() = "[${this.joinToString(",") { it.toJson() }}]"

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

fun JSONObject.optNullableString(key: String): String? {
    return try {
        this.getString(key)
    } catch (e: JSONException) {
        null
    }
}

fun <T> FromJson<T>.fromJsonArray(jsonArray: JSONArray?): List<T>? = jsonArray?.let { array ->
    array.mapIndexed { index, _ -> fromJson(array.getJSONObject(index)) }
}

fun jsonStringOrNull(any: Any?): Any? = when (any) {
    null -> null
    else -> "\"$any\""
}
