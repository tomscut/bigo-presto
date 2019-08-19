package io.prestosql.plugin.bigo.udf;

import io.airlift.slice.Slice;
import io.prestosql.spi.function.Description;
import io.prestosql.spi.function.ScalarFunction;
import io.prestosql.spi.function.SqlNullable;
import io.prestosql.spi.function.SqlType;
import io.prestosql.spi.type.StandardTypes;

import java.io.IOException;
import java.text.ParseException;

import static io.airlift.slice.Slices.utf8Slice;

public class BigoTypeConversionFunctions {

    // convert to int
    @Description("Returns the int value.")
    @ScalarFunction("int")
    @SqlType(StandardTypes.BIGINT)
    @SqlNullable
    public static Long intFunctionSlice(@SqlType(StandardTypes.VARCHAR) Slice slice)
    {

        try {
            return (long)(int)Long.parseLong(slice.toStringUtf8());
        } catch (NumberFormatException e) {
            return (long)(int)Double.parseDouble(slice.toStringUtf8());
        } catch (Exception e) {
            return null;
        }
    }

    @Description("Returns the int value.")
    @ScalarFunction("int")
    @SqlType(StandardTypes.BIGINT)
    @SqlNullable
    public static Long intFunctionTimeStamp(@SqlType(StandardTypes.TIMESTAMP) long timestamp)
    {
        return (long)(int)timestamp;
    }

    @Description("Returns the int value.")
    @ScalarFunction("int")
    @SqlType(StandardTypes.BIGINT)
    @SqlNullable
    public static Long intFunctionTimeStampWithZone(@SqlType(StandardTypes.TIMESTAMP_WITH_TIME_ZONE) long timestamp)
    {
        return (long)(int)timestamp;
    }

    @Description("Returns the int value.")
    @ScalarFunction("int")
    @SqlType(StandardTypes.BIGINT)
    @SqlNullable
    public static Long intFunction(@SqlType(StandardTypes.BIGINT) long n)
    {
        return (long)(int)n;
    }

    @Description("Returns the int value.")
    @ScalarFunction("int")
    @SqlType(StandardTypes.BIGINT)
    @SqlNullable
    public static Long intFunctionInt(@SqlType(StandardTypes.INTEGER) long n)
    {
        return (long)(int)n;
    }

    @Description("Returns the int value.")
    @ScalarFunction("int")
    @SqlType(StandardTypes.BIGINT)
    @SqlNullable
    public static Long intFunctionTiny(@SqlType(StandardTypes.TINYINT) long n)
    {
        return (long)(int)n;
    }

    @Description("Returns the int value.")
    @ScalarFunction("int")
    @SqlType(StandardTypes.BIGINT)
    @SqlNullable
    public static Long intFunctionSmall(@SqlType(StandardTypes.SMALLINT) long n)
    {
        return (long)(int)n;
    }

    @Description("Returns the int value.")
    @ScalarFunction("int")
    @SqlType(StandardTypes.BIGINT)
    @SqlNullable
    public static Long intFunctionDouble(@SqlType(StandardTypes.DOUBLE) double n)
    {
        return (long)(int)n;
    }

    // convert to double
    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunctionSlice(@SqlType(StandardTypes.VARCHAR) Slice slice)
    {
        try {
            return Double.parseDouble(slice.toStringUtf8());
        } catch (Exception e) {
            return null;
        }
    }

    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunctionTime(@SqlType(StandardTypes.TIME) long n)
    {
        return (double)n;
    }

    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunctionTimeWithZone(@SqlType(StandardTypes.TIME_WITH_TIME_ZONE) long n)
    {
        return (double)n;
    }

    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunctionTimestamp(@SqlType(StandardTypes.TIMESTAMP) long n)
    {
        return (double)n;
    }

    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunctionTimestampWithZone(@SqlType(StandardTypes.TIMESTAMP_WITH_TIME_ZONE) long n)
    {
        return (double)n;
    }

    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunctionLong(@SqlType(StandardTypes.BIGINT) long n)
    {
        return (double)n;
    }

    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunctionInt(@SqlType(StandardTypes.INTEGER) long n)
    {
        return (double)n;
    }

    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunctionTiny(@SqlType(StandardTypes.TINYINT) long n)
    {
        return (double)n;
    }

    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunctionSmall(@SqlType(StandardTypes.SMALLINT) long n)
    {
        return (double)n;
    }

    @Description("Returns the double value.")
    @ScalarFunction("double")
    @SqlType(StandardTypes.DOUBLE)
    @SqlNullable
    public static Double doubleFunction(@SqlType(StandardTypes.DOUBLE) double n)
    {
        return n;
    }

    // convert to string.
    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunction(@SqlType(StandardTypes.VARCHAR) Slice slice)
    {
        return slice;
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionDate(@SqlType(StandardTypes.DATE) long n)
    {
        return utf8Slice(Long.toString(n));
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionTime(@SqlType(StandardTypes.TIME) long n)
    {
        return utf8Slice(Long.toString(n));
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionTimeWithZone(@SqlType(StandardTypes.TIME_WITH_TIME_ZONE) long n)
    {
        return utf8Slice(Long.toString(n));
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionTimeStamp(@SqlType(StandardTypes.TIMESTAMP) long n)
    {
        return utf8Slice(Long.toString(n));
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionTimeStampWithZone(@SqlType(StandardTypes.TIMESTAMP_WITH_TIME_ZONE) long n)
    {
        return utf8Slice(Long.toString(n));
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionLong(@SqlType(StandardTypes.BIGINT) long n)
    {
        return utf8Slice(Long.toString(n));
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionInt(@SqlType(StandardTypes.INTEGER) long n)
    {
        return utf8Slice(Long.toString(n));
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionTiny(@SqlType(StandardTypes.TINYINT) long n)
    {
        return utf8Slice(Long.toString(n));
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionSmall(@SqlType(StandardTypes.SMALLINT) long n)
    {
        return utf8Slice(Long.toString(n));
    }

    @Description("Returns the string value.")
    @ScalarFunction("string")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice stringFunctionDouble(@SqlType(StandardTypes.DOUBLE) double n)
    {
        return utf8Slice(Double.toString(n));
    }
}
