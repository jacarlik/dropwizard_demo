package com.engage.expenses.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Util tests
 *
 * @author N/A
 * @since 2018-02-10
 */
public class CommonUtilsTest
{
    @Test
    public void testStripXSS()
    {
        Assert.assertEquals("xss example&lt;XSS ROCKSclick m\n", CommonUtils.stripXSS("<html><head><title>xss example</title><script>function a(){alert(1)}</script></head><body><x onclick=alert(1)><b onkeyup=alert(1)><x onclick=a()><b onkeyup=a()><body onload=a()><<h1>XSS ROCKS</h1><p>click m</p><form><input value='try typing in here'></form></body>\n"));
        Assert.assertEquals("function a(){alert(123)}", CommonUtils.stripXSS("<body onload=alert(1)><x onclick=alert(1)><b onkeyup=alert(1)>function a(){alert(123)}<x onclick=a()><b onkeyup=a()><body onload=a()>"));
    }
}
