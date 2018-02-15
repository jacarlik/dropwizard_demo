package com.engagetech.expenses.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Custom date deserializer
 *
 * @author N/A
 * @since 2018-02-10
 */
public class LocalDateDeserializer extends StdDeserializer<LocalDate>
{
    private DateTimeFormatter m_dateTimeFormatter;

    public LocalDateDeserializer(DateTimeFormatter dateTimeFormatter)
    {
        super(LocalDate.class);
        m_dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        return LocalDate.parse(parser.readValueAs(String.class), m_dateTimeFormatter);
    }
}