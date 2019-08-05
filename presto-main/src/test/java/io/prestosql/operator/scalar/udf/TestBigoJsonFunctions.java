package io.prestosql.operator.scalar.udf;

import io.airlift.slice.Slice;
import io.prestosql.operator.scalar.JsonPath;
import org.testng.annotations.Test;

import static io.airlift.slice.Slices.utf8Slice;
import static io.prestosql.operator.scalar.udf.BigoJsonFunctions.varcharGetJsonObject;
import static org.testng.Assert.*;

public class TestBigoJsonFunctions {

    @Test
    public void testGetJsonObject()
    {
        Slice slice = varcharGetJsonObject(utf8Slice("[1, 2, 3]"), new JsonPath("$[2]"));
        assertEquals(slice.toStringUtf8(), "3");
    }
}
