package io.prestosql.sql.hive;

import io.prestosql.sql.parser.ParsingOptions;
import io.prestosql.sql.parser.SqlParser;
import io.prestosql.sql.parser.SqlParserOptions;
import io.prestosql.sql.tree.Node;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.prestosql.sql.parser.IdentifierSymbol.COLON;

public class TestHiveSqlParser {

    private SqlParser sqlParser = null;
    private ParsingOptions parsingOptions = null;
    @BeforeTest
    public void init() {
        sqlParser = new SqlParser(new SqlParserOptions().allowIdentifierSymbol(COLON));
        parsingOptions = new ParsingOptions();
        parsingOptions.setIfUseHiveParser(true);
    }

    @Test
    public void testUse()
    {
        String sql = "USE hive.tmp";
        Node node = sqlParser.createStatement(sql, parsingOptions);
        System.out.println(node);
    }

    @Test
    public void testSetSession()
    {
        String sql = "SET SESSION foo=true";
        Node node = sqlParser.createStatement(sql, parsingOptions);
        System.out.println(node);
    }

    @Test
    public void testLogicalBinary()
    {
        String sql = "SELECT x FROM t";
        Node node = sqlParser.createStatement(sql, parsingOptions);
        System.out.println(node);
    }

    @Test
    public void testSelect01()
    {
        String sql = "SELECT \"a\",b,c,d FROM ALGO.t WHERE x=321 LIMIT 100";
        Node node = sqlParser.createStatement(sql, parsingOptions);
        System.out.println(node);
    }

    @Test
    public void testQuotedQuery()
    {
        String sql = "SELECT `a`,b,c,d FROM ALGO.t WHERE x=321 LIMIT 100";
        Node node = sqlParser.createStatement(sql, parsingOptions);
        System.out.println(node);
    }
    @Test
    public void testDoubleEq()
    {
        String sql = "SELECT `a`,b,c,d FROM ALGO.t WHERE x==321 LIMIT 100";
        Node node = sqlParser.createStatement(sql, parsingOptions);
        System.out.println(node);
    }
    @Test
    public void testTableStartWithDigit()
    {
        String sql = "select * from tmp.20171014_tmpdata limit 10";
        Node node = sqlParser.createStatement(sql, parsingOptions);
        System.out.println(node);
    }

}