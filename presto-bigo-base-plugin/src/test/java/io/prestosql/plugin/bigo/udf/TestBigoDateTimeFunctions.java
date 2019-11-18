package io.prestosql.plugin.bigo.udf;

import io.prestosql.spi.PrestoException;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import static io.airlift.slice.Slices.utf8Slice;
import static io.prestosql.plugin.bigo.udf.BigoDateTimeFunctions.unixTimestamp;
import static org.testng.Assert.*;

public class TestBigoDateTimeFunctions {

    @Test
    public void testDateAdd() {
        String output = BigoDateTimeFunctions.dateAdd(utf8Slice("2019-01-01"), 10).toStringUtf8();

        assertEquals(output, "2019-01-11");
    }

    @Test(expectedExceptions = PrestoException.class)
    public void dateAddWithInvalidDateStringShouldThrowException() {
        BigoDateTimeFunctions.dateAdd(utf8Slice("2019-aaaa-01"), 10).toStringUtf8();
    }

    @Test
    public void testDateSub() {
        String output = BigoDateTimeFunctions.dateSub(utf8Slice("2019-01-11"), 10).toStringUtf8();

        assertEquals(output, "2019-01-01");
    }

    @Test(expectedExceptions = PrestoException.class)
    public void dateSubWithInvalidDateStringShouldThrowException() {
        BigoDateTimeFunctions.dateSub(utf8Slice("2019-aaaa-01"), 10).toStringUtf8();
    }


    @Test
    public void testUnixTimestamp() {
        double uts = unixTimestamp(utf8Slice("2019-08-31 12:00:00"));
        if (String.valueOf(uts).contains("E")) {
            BigDecimal bd1 = new BigDecimal(uts);
            assertEquals(bd1.toPlainString(), "1567224000");
        } else {
            assertEquals(String.valueOf(uts), "1567224000");
        }
    }

    @Test
    public void testUnixTimestampWithFormat() {
        double uts = unixTimestamp(utf8Slice("2019-07-22"), utf8Slice("yyyy-MM-dd"));
        if (String.valueOf(uts).contains("E")) {
            BigDecimal bd1 = new BigDecimal(uts);
            assertEquals(bd1.toPlainString(), "1563724800");
        } else {
            assertEquals(String.valueOf(uts), "1563724800");
        }
    }

    @Test
    public void testDateDiff() {
        long diff = BigoDateTimeFunctions.dateDiff(utf8Slice("2019-01-03"), utf8Slice("2018-12-31"));
        assertEquals(diff, 3);
    }

    @Test
    public void testHourFromString() {
        long hour1 = BigoDateTimeFunctions.hourFromString(utf8Slice("2019-08-01 12:13:14"));
        long hour2 = BigoDateTimeFunctions.hourFromString(utf8Slice("2019-08-01 24:13:14"));
        long hour3 = BigoDateTimeFunctions.hourFromString(utf8Slice("12:13:14"));
        long hour4 = BigoDateTimeFunctions.hourFromString(utf8Slice("24:13:14"));
        assertEquals(hour1, 12);
        assertEquals(hour2, 0);
        assertEquals(hour3, 12);
        assertEquals(hour4, 0);
    }

    @Test
    public void testMinuteFromString() {
        long minute1 = BigoDateTimeFunctions.minuteFromString(utf8Slice("2019-08-01 12:13:14"));
        long minute2 = BigoDateTimeFunctions.minuteFromString(utf8Slice("12:13:14"));
        assertEquals(minute1, 13);
        assertEquals(minute2, 13);
    }

    @Test
    public void testSecondFromString() {
        long second1 = BigoDateTimeFunctions.secondFromString(utf8Slice("2019-08-01 12:13:14"));
        long second2 = BigoDateTimeFunctions.secondFromString(utf8Slice("12:13:14"));
        assertEquals(second1, 14);
        assertEquals(second2, 14);
    }

    @Test
    public void testDayFromString() {
        long day1 = BigoDateTimeFunctions.dayFromString(utf8Slice("2019-08-01 12:13:14"));
        long day2 = BigoDateTimeFunctions.dayFromString(utf8Slice("2019-08-01"));
        assertEquals(day1, 1);
        assertEquals(day2, 1);
    }

    @Test
    public void testMonthFromString() {
        long month1 = BigoDateTimeFunctions.monthFromString(utf8Slice("2019-08-01 12:13:14"));
        long month2 = BigoDateTimeFunctions.monthFromString(utf8Slice("2019-08-01"));
        assertEquals(month1, 8);
        assertEquals(month2, 8);
    }

    @Test
    public void testYearFromString() {
        long year1 = BigoDateTimeFunctions.yearFromString(utf8Slice("2019-08-01 12:13:14"));
        long year2 = BigoDateTimeFunctions.yearFromString(utf8Slice("2019-08-01"));
        assertEquals(year1, 2019);
        assertEquals(year2, 2019);
    }

    @Test
    public void testQuarterFromString() {
        long quarter1 = BigoDateTimeFunctions.quarterFromString(utf8Slice("2019-08-01 12:13:14"));
        long quarter2 = BigoDateTimeFunctions.quarterFromString(utf8Slice("2019-06-01"));
        assertEquals(quarter1, 3);
        assertEquals(quarter2, 2);
    }

    @Test
    public void testCalPt() {
        assertEquals(BigoDateTimeFunctions.cal_pt(utf8Slice("PT10.2S")), 10.2);
        assertEquals(BigoDateTimeFunctions.cal_pt(utf8Slice("PT1.0M10.2S")), 70.2);
        assertEquals(BigoDateTimeFunctions.cal_pt(utf8Slice("PT1.0H1M10.2S")), 3670.2);
    }

    @Test
    public void testAddMonths() {
        assertEquals(BigoDateTimeFunctions.addMonths(utf8Slice("2019-08-01"), 2).toStringUtf8(), "2019-10-01");
        assertEquals(BigoDateTimeFunctions.addMonths(utf8Slice("2019-08-31"), 1).toStringUtf8(), "2019-09-30");
        assertEquals(BigoDateTimeFunctions.addMonths(utf8Slice("2018-08-31"), 13).toStringUtf8(), "2019-09-30");
        assertEquals(BigoDateTimeFunctions.addMonths(utf8Slice("2008-08-31"), 133).toStringUtf8(), "2019-09-30");

        assertEquals(BigoDateTimeFunctions.addMonths(utf8Slice("2019-08-01"), 2, utf8Slice("yyyy-MM-dd")).toStringUtf8(), "2019-10-01");
        assertEquals(BigoDateTimeFunctions.addMonths(utf8Slice("2019-08-31 14:15:16"), 1, utf8Slice("yyyy-MM-dd HH:mm:ss")).toStringUtf8(), "2019-09-30 14:15:16");
        assertEquals(BigoDateTimeFunctions.addMonthsDate(18139, 1).toStringUtf8(), "2019-09-30");
    }
}