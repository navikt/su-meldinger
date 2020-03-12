package no.nav.su.meldinger.kafka.soknad

import com.google.gson.JsonParser
import no.nav.su.meldinger.kafka.soknad.Boforhold.Companion.borSammenMedKey
import no.nav.su.meldinger.kafka.soknad.Boforhold.Companion.delerBoligMedKey
import no.nav.su.meldinger.kafka.soknad.ForNav.Companion.merknaderKey
import no.nav.su.meldinger.kafka.soknad.InntektPensjonFormue.Companion.annenFormueKey
import no.nav.su.meldinger.kafka.soknad.InntektPensjonFormue.Companion.depositumBeløpKey
import no.nav.su.meldinger.kafka.soknad.InntektPensjonFormue.Companion.framsattKravAnnenYtelseBegrunnelseKey
import no.nav.su.meldinger.kafka.soknad.InntektPensjonFormue.Companion.pensjonsOrdningKey
import no.nav.su.meldinger.kafka.soknad.Oppholdstillatelse.Companion.søktOmForlengelseKey
import no.nav.su.meldinger.kafka.soknad.Oppholdstillatelse.Companion.utløpsDatoKey
import no.nav.su.meldinger.kafka.soknad.Personopplysninger.Companion.bruksenhetKey
import no.nav.su.meldinger.kafka.soknad.Personopplysninger.Companion.mellomnavnKey
import no.nav.su.meldinger.kafka.soknad.Utenlandsopphold.Companion.planlagtUtenlandsoppholdKey
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

        val inntektPensjonFormue = søknad.inntektPensjonFormue.toJson()
        assertTrue(inntektPensjonFormue.contains("\"Har søkt om foreldrepenger\""))
        assertFalse(inntektPensjonFormue.contains("\"[\""))
        assertFalse(inntektPensjonFormue.contains("\"]\""))
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
                flyktning = true,
                borFastINorge = true,
                statsborgerskap = "NOR"
        ).toJson())
        assertNull(jsonPersonopplysninger.optString(mellomnavnKey, null))
        assertNull(jsonPersonopplysninger.optString(bruksenhetKey, null))

        val personopplysninger = Personopplysninger.fromJson(jsonPersonopplysninger)
        assertNull(personopplysninger.mellomnavn)
        assertNull(personopplysninger.bruksenhet)

        val jsonBoforhold = JSONObject(Boforhold(
                delerBolig = false
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
                utenlandsopphold = false,
                planlagtUtenlandsopphold = false
        ).toJson())
        assertNull(jsonUtenlandsopphold.optJSONArray(registrertePerioderKey))
        assertNull(jsonUtenlandsopphold.optJSONArray(planlagtUtenlandsoppholdKey))

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

        val jsonInntektPensjonFormue = JSONObject(InntektPensjonFormue(
                framsattKravAnnenYtelse = false,
                harInntekt = true,
                inntektBeløp = 2500.0,
                harPensjon = false,
                sumInntektOgPensjon = 7000.0,
                harFormueEiendom = false,
                harFinansFormue = true,
                formueBeløp = 1000.0,
                harAnnenFormue = true,
                harDepositumskonto = false,
                harSosialStønad = true
        ).toJson())
        assertNull(jsonInntektPensjonFormue.optString(framsattKravAnnenYtelseBegrunnelseKey, null))
        assertNull(jsonInntektPensjonFormue.optJSONArray(pensjonsOrdningKey))
        assertNull(jsonInntektPensjonFormue.optJSONArray(annenFormueKey))
        assertNull(jsonInntektPensjonFormue.optNullableNumber(depositumBeløpKey))

        val inntektPensjonFormue = InntektPensjonFormue.fromJson(jsonInntektPensjonFormue)
        assertNull(inntektPensjonFormue.framsattKravAnnenYtelseBegrunnelse)
        assertNull(inntektPensjonFormue.pensjonsOrdning)
        assertNull(inntektPensjonFormue.annenFormue)
        assertNull(inntektPensjonFormue.depositumBeløp)

        val søknad = SøknadInnhold(personopplysninger, boforhold, utenlandsopphold, oppholdstillatelse, inntektPensjonFormue, forNav)

        assertDoesNotThrow {
            JSONObject(søknad.toJson())
            JsonParser.parseString(søknad.toJson()) //Strict check of json syntax - JSONObject helps us along a bit too much.
        }
        assertEquals(søknad.toJson(), SøknadInnhold.fromJson(JSONObject(søknad.toJson())).toJson())
    }
}