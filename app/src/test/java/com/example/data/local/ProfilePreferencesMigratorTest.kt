package com.example.data.local

import org.junit.Assert.*
import org.junit.Test

class ProfilePreferencesMigratorTest {
    @Test fun emptyDateIsNull() {
        assertNull(ProfilePreferencesMigrator.normalizeBirthDate(null))
        assertNull(ProfilePreferencesMigrator.normalizeBirthDate(""))
        assertNull(ProfilePreferencesMigrator.normalizeBirthDate("  "))
    }
    @Test fun existingIsoAndLegacyDateNormalize() {
        assertEquals("2000-02-29", ProfilePreferencesMigrator.normalizeBirthDate("2000-02-29"))
        assertEquals("2000-02-29", ProfilePreferencesMigrator.normalizeBirthDate("29/02/2000"))
    }
    @Test fun invalidDateIsNull() {
        assertNull(ProfilePreferencesMigrator.normalizeBirthDate("31/02/2000"))
        assertNull(ProfilePreferencesMigrator.normalizeBirthDate("2000-13-01"))
        assertNull(ProfilePreferencesMigrator.normalizeBirthDate("yesterday"))
    }
    @Test fun dayFirstDatesWithAndWithoutLeadingZerosNormalize() {
        assertEquals("1984-02-11", ProfilePreferencesMigrator.normalizeBirthDate("11-2-1984"))
        assertEquals("1984-02-05", ProfilePreferencesMigrator.normalizeBirthDate("05-02-1984"))
        assertEquals("1984-02-01", ProfilePreferencesMigrator.normalizeBirthDate("1/2/1984"))
        assertEquals("1984-02-11", ProfilePreferencesMigrator.normalizeBirthDate("11.2.1984"))
    }
    @Test fun arabicIndicDigitsNormalize() {
        assertEquals("1984-02-11", ProfilePreferencesMigrator.normalizeBirthDate("\u0661\u0661-\u0662-\u0661\u0669\u0668\u0664"))
    }
    @Test fun impossibleDayFirstDatesAreNull() {
        assertNull(ProfilePreferencesMigrator.normalizeBirthDate("30-2-1984"))
        assertNull(ProfilePreferencesMigrator.normalizeBirthDate("11-13-1984"))
    }
}
