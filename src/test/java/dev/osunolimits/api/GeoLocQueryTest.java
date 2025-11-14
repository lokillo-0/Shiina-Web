package dev.osunolimits.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.osunolimits.modules.queries.GeoLocQuery;

public class GeoLocQueryTest {

    private GeoLocQuery geoLocQuery;

    @BeforeEach
    void setUp() {
        geoLocQuery = new GeoLocQuery();
    }

    @Test
    void testGetCountryCodeSuccess() {
        String countryCode = geoLocQuery.getCountryCode("8.8.8.8");
        assertNotNull(countryCode);
    }

    @Test
    void testGetCountryCodeFail() {
        String countryCode = geoLocQuery.getCountryCode("0.0.0.0");
        assertEquals("XX", countryCode);
    }
}
