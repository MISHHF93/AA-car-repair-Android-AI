package com.aa.carrepair.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setUp() {
        converters = Converters()
    }

    // ── Instant conversion ──────────────────────────────────────────────

    @Test
    fun `fromInstant converts to epoch millis`() {
        val instant = Instant.ofEpochMilli(1700000000000L)
        assertEquals(1700000000000L, converters.fromInstant(instant))
    }

    @Test
    fun `toInstant converts from epoch millis`() {
        val expected = Instant.ofEpochMilli(1700000000000L)
        assertEquals(expected, converters.toInstant(1700000000000L))
    }

    @Test
    fun `fromInstant null returns null`() {
        assertNull(converters.fromInstant(null))
    }

    @Test
    fun `toInstant null returns null`() {
        assertNull(converters.toInstant(null))
    }

    @Test
    fun `round-trip Instant preserves value`() {
        val original = Instant.now()
        val millis = converters.fromInstant(original)
        val restored = converters.toInstant(millis!!)
        assertEquals(original.toEpochMilli(), restored!!.toEpochMilli())
    }

    // ── String list conversion ──────────────────────────────────────────

    @Test
    fun `fromStringList serializes list to JSON`() {
        val list = listOf("brake", "engine", "oil")
        val json = converters.fromStringList(list)
        assertEquals("[\"brake\",\"engine\",\"oil\"]", json)
    }

    @Test
    fun `toStringList deserializes JSON to list`() {
        val json = "[\"brake\",\"engine\",\"oil\"]"
        val list = converters.toStringList(json)
        assertEquals(listOf("brake", "engine", "oil"), list)
    }

    @Test
    fun `round-trip string list preserves data`() {
        val original = listOf("P0301", "P0420", "B0100")
        val json = converters.fromStringList(original)
        val restored = converters.toStringList(json!!)
        assertEquals(original, restored)
    }

    @Test
    fun `fromStringList null returns null`() {
        assertNull(converters.fromStringList(null))
    }

    @Test
    fun `toStringList null returns null`() {
        assertNull(converters.toStringList(null))
    }

    @Test
    fun `empty list serializes correctly`() {
        val json = converters.fromStringList(emptyList())
        assertEquals("[]", json)
    }

    @Test
    fun `empty list deserializes correctly`() {
        val list = converters.toStringList("[]")
        assertEquals(emptyList<String>(), list)
    }

    // ── Map conversion ──────────────────────────────────────────────────

    @Test
    fun `fromMap serializes map to JSON`() {
        val map = mapOf("hours" to 2.5, "rate" to 100.0)
        val json = converters.fromMap(map)
        // Parse back to verify
        val restored = converters.toMap(json!!)
        assertEquals(2.5, restored!!["hours"]!!, 0.01)
        assertEquals(100.0, restored["rate"]!!, 0.01)
    }

    @Test
    fun `round-trip map preserves data`() {
        val original = mapOf("labor_cost" to 250.0, "parts_cost" to 150.0, "total" to 400.0)
        val json = converters.fromMap(original)
        val restored = converters.toMap(json!!)
        assertEquals(original.size, restored!!.size)
        original.forEach { (key, value) ->
            assertEquals(value, restored[key]!!, 0.001)
        }
    }

    @Test
    fun `fromMap null returns null`() {
        assertNull(converters.fromMap(null))
    }

    @Test
    fun `toMap null returns null`() {
        assertNull(converters.toMap(null))
    }

    @Test
    fun `empty map serializes correctly`() {
        val json = converters.fromMap(emptyMap())
        assertEquals("{}", json)
    }
}
