package com.engagetech.expenses.util;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static com.engagetech.expenses.util.CommonUtils.STANDARD_UK_VAT;
import static com.engagetech.expenses.util.CommonUtils.calculateVat;

/**
 * Common utils tests
 *
 * @author N/A
 * @since 2018-02-10
 */
@RunWith(DataProviderRunner.class)
public class CommonUtilsTest
{
    @Test
    public void testStripXSS()
    {
        Assert.assertEquals("xss example&lt;XSS ROCKSclick m\n", CommonUtils.stripXSS("<html><head><title>xss example</title><script>function a(){alert(1)}</script></head><body><x onclick=alert(1)><b onkeyup=alert(1)><x onclick=a()><b onkeyup=a()><body onload=a()><<h1>XSS ROCKS</h1><p>click m</p><form><input value='try typing in here'></form></body>\n"));
        Assert.assertEquals("function a(){alert(123)}", CommonUtils.stripXSS("<body onload=alert(1)><x onclick=alert(1)><b onkeyup=alert(1)>function a(){alert(123)}<x onclick=a()><b onkeyup=a()><body onload=a()>"));
    }

    @DataProvider
    public static Object[][] vatCalculationProvider()
    {
        return new Object[][]
            {
                {
                    "For 100£ VAT portion is 16.67", BigDecimal.valueOf(100), BigDecimal.valueOf(16.67)
                },
                {
                    "For 144.43£ VAT portion is 24.07", BigDecimal.valueOf(144.43), BigDecimal.valueOf(24.07)
                },
                {
                    "For 234.442343£ VAT portion is 39.07", BigDecimal.valueOf(234.442343), BigDecimal.valueOf(39.07)
                },
                {
                    "For 284.000£ VAT portion is 47.33", BigDecimal.valueOf(284.000), BigDecimal.valueOf(47.33)
                },
            };
    }

    @Test
    @UseDataProvider("vatCalculationProvider")
    public void testVatCalculation(String message, BigDecimal amount, BigDecimal vat)
    {
        Assert.assertEquals(message, vat, calculateVat(amount, STANDARD_UK_VAT));
    }
}
