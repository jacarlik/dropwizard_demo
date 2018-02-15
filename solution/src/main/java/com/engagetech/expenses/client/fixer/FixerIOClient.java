package com.engagetech.expenses.client.fixer;

import com.engagetech.expenses.util.CommonUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.engagetech.expenses.util.CommonUtils.SCALE;

/**
 * Fixer.io client used to retrieve foreign exchange rates and perform conversions
 *
 * @author N/A
 * @since 2018-02-14
 */
public class FixerIOClient
{
    private static final String BASE_URL = "http://api.fixer.io/";
    private static final DateTimeFormatter FIXER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Retrieve exchange rates from the Fixer.io API and convert the amount to the desired currency
     *
     * @param date Get exchange rates for the specific date
     * @param baseCurrency Base currency for the given value
     * @param targetCurrency Target/desired currency
     * @param originalAmount Provided amount (in base currency)
     * @return FixerIOExchangeRates instance
     */
    public BigDecimal convertCurrency(LocalDate date,
                                      String baseCurrency,
                                      String targetCurrency,
                                      BigDecimal originalAmount)
    {
        FixerIOExchangeRates rates = new JerseyClientBuilder()
            .build()
            .target(BASE_URL)
            .path(date.format(FIXER_DATE_FORMATTER))
            .queryParam("base", baseCurrency)
            .request(MediaType.APPLICATION_JSON)
            .get()
            .readEntity(FixerIOExchangeRates.class);

        return originalAmount
            .multiply(rates.getRates().get(targetCurrency))
            .setScale(SCALE, RoundingMode.HALF_UP);
    }
}
