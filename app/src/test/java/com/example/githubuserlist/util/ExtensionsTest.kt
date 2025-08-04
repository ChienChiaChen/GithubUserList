package com.example.githubuserlist.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsTest {
    
    @Test
    fun `formatCount should format numbers correctly`() {
        // Test small numbers
        assertEquals("0", 0.formatCount())
        assertEquals("5", 5.formatCount())
        assertEquals("99", 99.formatCount())
        assertEquals("999", 999.formatCount())
        
        // Test thousands
        assertEquals("1K", 1000.formatCount())
        assertEquals("2K", 2000.formatCount())
        assertEquals("10K", 10000.formatCount())
        assertEquals("999K", 999000.formatCount())
        
        // Test millions
        assertEquals("1M", 1000000.formatCount())
        assertEquals("2M", 2000000.formatCount())
        assertEquals("10M", 10000000.formatCount())
        assertEquals("999M", 999000000.formatCount())
    }
    
    @Test
    fun `formatCount should handle large numbers`() {
        assertEquals("100M", 100000000.formatCount())
        assertEquals("1000M", 1000000000.formatCount())
        assertEquals("1500M", 1500000000.formatCount())
    }
} 