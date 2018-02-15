package com.engagetech.expenses.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Custom date serializer
 *
 * @author N/A
 * @since 2018-02-10
 */
public class LocalDateSerializer extends StdSerializer<LocalDate>
{
    private DateTimeFormatter m_dateTimeFormatter;

    public LocalDateSerializer(DateTimeFormatter dateTimeFormatter)
    {
        super(LocalDate.class);
        m_dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException
    {
        generator.writeString(value.format(m_dateTimeFormatter));
    }
}
