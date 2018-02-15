package com.engagetech.expenses.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.jackson.Jackson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Whitelist;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * General utils class
 *
 * @author N/A
 * @since 2018-02-10
 */
public class CommonUtils
{
    // Date formatter
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    // Preferred scale for BigDecimal amounts
    public static final int SCALE = 2;

    // Standard UK VAT rate
    public static final BigDecimal STANDARD_UK_VAT = BigDecimal.valueOf(0.2);

    // Base currency being for this application
    public static final String BASE_CURRENCY = "GBP";

    /**
     * Retrieve object mapper instance
     *
     * @return Object mapper
     */
    @VisibleForTesting
    public static ObjectMapper getObjectMapper()
    {
        return Jackson.newObjectMapper()
            .registerModule(new SimpleModule().addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_TIME_FORMATTER)))
            .registerModule(new SimpleModule().addSerializer(LocalDate.class, new LocalDateSerializer(DATE_TIME_FORMATTER)));
    }

    /**
     * Strips any potential XSS threats out of the value
     *
     * @param value Value to be analysed
     * @return Cleaned value
     */
    public static String stripXSS(String value)
    {
        if(value == null)
        {
            return null;
        }

        // Avoid null characters
        value = value.replaceAll("&quot;\0&quot;", "&quot;&quot;");

        // Clean out HTML
        Document.OutputSettings outputSettings = new Document.OutputSettings();
        outputSettings.escapeMode(EscapeMode.xhtml);
        outputSettings.prettyPrint(false);
        value = Jsoup.clean( value, "", Whitelist.none(), outputSettings );

        return value;
    }

    /**
     * Extract VAT portion from the original amount
     *
     * @param amount Amount
     * @param vatRate VAT rate
     * @return VAT potion
     */
    public static BigDecimal calculateVat(BigDecimal amount, BigDecimal vatRate)
    {
        return amount
            .subtract(amount.divide(vatRate.add(BigDecimal.ONE), SCALE, RoundingMode.HALF_UP))
            .setScale(SCALE, RoundingMode.HALF_UP);
    }
}
