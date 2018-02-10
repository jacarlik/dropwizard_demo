package com.engage.expenses.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Whitelist;

public class XSSUtils
{
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
