package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser
import no.nav.su.meldinger.kafka.soknad.Boforhold.Companion.borSammenMedKey
import no.nav.su.meldinger.kafka.soknad.Boforhold.Companion.delerBoligMedKey
import no.nav.su.meldinger.kafka.soknad.ForNav.Companion.merknaderKey
import no.nav.su.meldinger.kafka.soknad.Formue.Companion.annenFormueKey
import no.nav.su.meldinger.kafka.soknad.Formue.Companion.depositumBeløpKey
import no.nav.su.meldinger.kafka.soknad.Formue.Companion.formueBeløpKey
import no.nav.su.meldinger.kafka.soknad.InntektOgPensjon.Companion.framsattKravAnnenYtelseBegrunnelseKey
import no.nav.su.meldinger.kafka.soknad.InntektOgPensjon.Companion.inntektBeløpKey
import no.nav.su.meldinger.kafka.soknad.InntektOgPensjon.Companion.pensjonsOrdningKey
import no.nav.su.meldinger.kafka.soknad.Oppholdstillatelse.Companion.søktOmForlengelseKey
import no.nav.su.meldinger.kafka.soknad.Oppholdstillatelse.Companion.utløpsDatoKey
import no.nav.su.meldinger.kafka.soknad.Personopplysninger.Companion.bruksenhetKey
import no.nav.su.meldinger.kafka.soknad.Personopplysninger.Companion.mellomnavnKey
import no.nav.su.meldinger.kafka.soknad.Utenlandsopphold.Companion.planlagtePerioderKey
import no.nav.su.meldinger.kafka.soknad.Utenlandsopphold.Companion.registrertePerioderKey
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class SøknadInnholdTest {

    private val søknad = SøknadInnholdTestdataBuilder.build()

    @Test
    fun `should convert to and from json`() {
        assertDoesNotThrow() {
            JSONObject(søknad.toJson())
            JsonParser.parseString(søknad.toJson()) //Strict check of json syntax - JSONObject helps us along a bit too much.
        }
        assertEquals(søknad.toJson(), SøknadInnhold.fromJson(JSONObject(søknad.toJson())).toJson())
    }

    @Test
    fun `should put " " if appropriate around conditional values`() {
        val personopplysninger = søknad.personopplysninger.toJson()
        assertTrue(personopplysninger.contains("\"Erik\""))
        assertTrue(personopplysninger.contains("\"U1H20\""))

        val boforhold = søknad.boforhold.toJson()
        assertTrue(boforhold.contains("\"Kari Nordmann\""))
        assertTrue(boforhold.contains("\"Ektefelle/Partner/Samboer\""))
        assertTrue(boforhold.contains("\"Per Nordmann\""))
        assertTrue(boforhold.contains("\"Andre personer over 18 år\""))

        val oppholdstillatelse = søknad.oppholdstillatelse.toJson()
        assertTrue(oppholdstillatelse.contains("\"2020-12-31\""))
        assertFalse(oppholdstillatelse.contains("\"true\""))

        val utenlandsopphold = søknad.utenlandsopphold.toJson()
        assertFalse(utenlandsopphold.contains("\"[\""))
        assertFalse(utenlandsopphold.contains("\"]\""))

        val forNav = søknad.forNav.toJson()
        assertTrue(forNav.contains("\"Intet å bemerke\""))

        val formue = søknad.formue.toJson()

        val inntektOgPensjon = søknad.inntektOgPensjon.toJson()
        assertTrue(inntektOgPensjon.contains("\"Har søkt om foreldrepenger\""))
        assertFalse(inntektOgPensjon.contains("\"[\""))
        assertFalse(inntektOgPensjon.contains("\"]\""))
    }

    @Test
    fun `should handle optional values as null`() {
        val jsonPersonopplysninger = JSONObject(Personopplysninger(
                fnr = "12345678910",
                fornavn = "fornavn",
                etternavn = "etternavn",
                telefonnummer = "12345678",
                gateadresse = "gateadresse",
                postnummer = "0050",
                poststed = "Oslo",
                bokommune = "Oslo",
                statsborgerskap = "NOR"
        ).toJson())
        assertNull(jsonPersonopplysninger.optString(mellomnavnKey, null))
        assertNull(jsonPersonopplysninger.optString(bruksenhetKey, null))

        val personopplysninger = Personopplysninger.fromJson(jsonPersonopplysninger)
        assertNull(personopplysninger.mellomnavn)
        assertNull(personopplysninger.bruksenhet)

        val jsonFlyktningsstatus = JSONObject(Flyktningsstatus(
                registrertFlyktning = false
        ))
        val flyktningsstatus = Flyktningsstatus.fromJson(jsonFlyktningsstatus)


        val jsonBoforhold = JSONObject(Boforhold(
                delerBolig = false,
                borFastINorge = false
        ).toJson())
        assertNull(jsonBoforhold.optString(borSammenMedKey, null))
        assertNull(jsonBoforhold.optString(delerBoligMedKey, null))

        val boforhold = Boforhold.fromJson(jsonBoforhold)
        assertNull(boforhold.borSammenMed)
        assertNull(boforhold.delerBoligMed)

        val jsonOppholdstillatelse = JSONObject(Oppholdstillatelse(
                harVarigOpphold = false
        ).toJson())

        assertNull(jsonOppholdstillatelse.optString(utløpsDatoKey, null))
        assertNull(jsonOppholdstillatelse.optNullableBoolean(søktOmForlengelseKey))

        val oppholdstillatelse = Oppholdstillatelse.fromJson(jsonOppholdstillatelse)
        assertNull(oppholdstillatelse.utløpsDato)
        assertNull(oppholdstillatelse.søktOmForlengelse)

        val jsonUtenlandsopphold = JSONObject(Utenlandsopphold(
        ).toJson())
        assertNull(jsonUtenlandsopphold.optJSONArray(registrertePerioderKey))
        assertNull(jsonUtenlandsopphold.optJSONArray(planlagtePerioderKey))

        val utenlandsopphold = Utenlandsopphold.fromJson(jsonUtenlandsopphold)
        assertNull(utenlandsopphold.registrertePerioder)
        assertNull(utenlandsopphold.planlagtePerioder)

        val jsonForNav = JSONObject(ForNav(
                målform = "målform",
                søkerMøttPersonlig = true,
                harFullmektigMøtt = false,
                erPassSjekket = true
        ).toJson())
        assertNull(jsonUtenlandsopphold.optString(merknaderKey, null))

        val forNav = ForNav.fromJson(jsonForNav)
        assertNull(forNav.merknader)


        val jsonFormue = JSONObject(Formue(
                harFormueEiendom = false,
                harFinansformue = true,
                harAnnenFormue = true,
                harDepositumskonto = false
        ).toJson())
        assertNull(jsonFormue.optJSONArray(annenFormueKey))
        assertNull(jsonFormue.optNullableNumber(formueBeløpKey))
        assertNull(jsonFormue.optNullableNumber(depositumBeløpKey))

        val formue = Formue.fromJson(jsonFormue)
        assertNull(formue.annenFormue)
        assertNull(formue.formueBeløp)
        assertNull(formue.depositumBeløp)


        val jsonInntektOgPensjon = JSONObject(InntektOgPensjon(
                framsattKravAnnenYtelse = false,
                framsattKravAnnenYtelseBegrunnelse = null,
                harInntekt = false,
                inntektBeløp = null,
                harPensjon = false,
                pensjonsOrdning = null,
                sumInntektOgPensjon = 0,
                harSosialStønad = false
        ).toJson())
        assertNull(jsonInntektOgPensjon.optJSONArray(framsattKravAnnenYtelseBegrunnelseKey))
        assertNull(jsonInntektOgPensjon.optNullableNumber(inntektBeløpKey))
        assertNull(jsonInntektOgPensjon.optJSONArray(pensjonsOrdningKey))

        val inntektOgPensjon = InntektOgPensjon.fromJson(jsonInntektOgPensjon)
        assertNull(inntektOgPensjon.framsattKravAnnenYtelseBegrunnelse)
        assertNull(inntektOgPensjon.pensjonsOrdning)
        assertNull(inntektOgPensjon.inntektBeløp)

        val søknad = SøknadInnhold(personopplysninger, flyktningsstatus, boforhold, utenlandsopphold, oppholdstillatelse, inntektOgPensjon, formue, forNav)

        assertDoesNotThrow {
            JSONObject(søknad.toJson())
            JsonParser.parseString(søknad.toJson()) //Strict check of json syntax - JSONObject helps us along a bit too much.
        }
        assertEquals(søknad.toJson(), SøknadInnhold.fromJson(JSONObject(søknad.toJson())).toJson())
    }
}