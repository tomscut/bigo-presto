package io.hivesql.sql.parser;

import io.prestosql.sql.parser.ParsingException;
import org.testng.annotations.Test;

public class UnsupportedSQLs extends SQLTester {

    @Test(expectedExceptions = ParsingException.class)
    public void sortByShouldThrowException()
    {
        String sql = "SELECT a from b sort by c";
        runHiveSQL(sql);
    }

    @Test(expectedExceptions = ParsingException.class)
    public void listResourceThrowException()
    {
        String sql = "LIST FILES";
        runHiveSQL(sql);
    }

    @Test(expectedExceptions = ParsingException.class)
    public void distinctOnThrowException()
    {
        String sql = "SELECT distinct on a from tb1";

        runHiveSQL(sql);
    }

    @Test(expectedExceptions = ParsingException.class)
    public void missingSelectStatementShouldThrowException()
    {
        String sql = "from tb1 where a > 10";

        runHiveSQL(sql);
    }

    @Test(expectedExceptions = ParsingException.class)
    public void loadDataShouldThrowException()
    {
        String sql = "load data inpath '/directory-path/file.csv' into tbl";

        runHiveSQL(sql);
    }

    @Test(expectedExceptions = ParsingException.class)
    public void syntaxErrorSQLShouldThrowExceptionNotOOM()
    {
        String sql = "SELECT * FROM b.aa day between '2019-11-01' and '2019-11-24'";

        runHiveSQL(sql);
    }
    // create table as select:when no as
    @Test(expectedExceptions = ParsingException.class)
    public void testCase10() {
        String hiveSql = "create table t select m from t1";
        checkASTNode(hiveSql);
    }
}