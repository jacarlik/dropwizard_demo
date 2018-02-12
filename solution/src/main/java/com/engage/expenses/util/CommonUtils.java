package com.engage.expenses.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.jackson.Jackson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Whitelist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * General utils class
 *
 * @author jklarica
 * @since 2018-02-10
 */
public class CommonUtils
{
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

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
}
