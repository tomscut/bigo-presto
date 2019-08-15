package io.hivesql.sql.parser;

import io.prestosql.sql.parser.ParsingException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ParseErrorMessages extends SQLTester {

    @Test
    public void testAntlr4ParseErrorMessage()
    {
        String sql = "SELECT a from b from c";

        try {
            runHiveSQL(sql);
            Assert.fail("sql: " + sql + " should throw exception");
        }catch (ParsingException e) {
            Assert.assertTrue(e.getMessage().startsWith("line 1:17: mismatched input 'from'. "));
        }
    }

    @Test
    public void testInternalParseErrorMessage()
    {
        String sql = "SELECT a from b sort by c";

        try {
            runHiveSQL(sql);
            Assert.fail("sql: " + sql + " should throw exception");
        }catch (ParsingException e) {
            Assert.assertTrue(e.getMessage().contains("Don't support sort by"));
        }
    }

    @Test
    public void testMissingJoinCriteria()
    {
        String sql = "" +
                "select count(distinct t2.hdid) as dau,sum(video_view_time_01)/count(distinct t2.hdid)\n" +
                "from\n" +
                "right join\n" +
                "(select distinct day,hdid\n" +
                "from\n" +
                "like_dw_com.dwd_like_com_dim_snapshot_user_device\n" +
                "where day='2019-08-13'\n" +
                " ) t2\n" +
                " left join\n" +
                "(select day,hdid,sum(video_view_time_01) as video_view_time_01\n" +
                "from\n" +
                "like_dw_vvd.dwd_like_vvd_video_view_with_list\n" +
                "where day='2019-08-13'\n" +
                "group by day,hdid ) t1\n" +
                " on t1.day=t2.day and t1.hdid=t2.hdid" +
                "";

        try {
            runHiveSQL(sql);
            Assert.fail("sql: " + sql + " should throw exception");
        }catch (ParsingException e) {
            Assert.assertTrue(e.getMessage().contains("Missing join criteria"));
        }
    }

    @Test
    public void testMissingTableAlias()
    {
        String sql = "" +
                "select\n" +
                "t3.uid,t3.hdid,t3.os,t3.os_version,t3.model,t3.resolution,t3.client_version,t3.sdk_version,t3.isp,t3.net,t3.appsflyer_id,t3.day\n" +
                ",t4.gender,t4.birthday\n" +
                "from\n" +
                "(\n" +
                "  select uid,hdid,os,os_version,model,resolution,client_version,sdk_version,isp,net,appsflyer_id,day\n" +
                "  from\n" +
                "  (\n" +
                "    select uid,hdid,os,os_version,model,resolution,client_version,sdk_version,isp,net,appsflyer_id,day\n" +
                "    --,row_number()over (partition by uid order by day desc) as rank_over\n" +
                "    from like_dw_com.dwd_like_com_dim_snapshot_user_device\n" +
                "    where country = 'IN'\n" +
                " -- 这个就是最近的一天的值\n" +
                " and day = '2019-08-12'\n" +
                "  )\n" +
                "  --where rank_over=1\n" +
                ") t3\n" +
                "join\n" +
                "(\n" +
                "  SELECT t1.uid,t1.gender,t1.birthday\n" +
                "  from like_dw_com.dwd_like_com_dim_uid_basic_info t1\n" +
                "  join tmp.guoyanyan_0813_only_push t2\n" +
                "  on t1.uid = t2.uid\n" +
                ") t4\n" +
                "on t3.uid = t4.uid" +
                "";

        try {
            runHiveSQL(sql);
            Assert.fail("sql: " + sql + " should throw exception");
        }catch (ParsingException e) {
            Assert.assertTrue(e.getMessage().contains("Missing table alias"));
        }
    }

    @Test
    public void testCrossJoinUnnestInHiveMode()
    {
        String sql = "" +
                "select  day,hour,count(distinct a.UID) as uv\n" +
                "from\n" +
                "(\n" +
                "SELECT day,hour(rtime) as hour,uid1 as uid\n" +
                "from vlog.like_online_user_uversion_stat_platform\n" +
                "cross join unnest(online) as ont\n" +
                "cross join unnest(ont.uids) as ut  (uid1)\n" +
                "where day>='2019-08-13'\n" +
                "and status=0\n" +
                ") a\n" +
                "join\n" +
                "(\n" +
                "  select UID\n" +
                "  from vlog.user_countrycode\n" +
                "  where countrycode='BD'\n" +
                ")b\n" +
                "on a.uid=b.uid\n" +
                "group by day,hour" +
                "";

        try {
            runHiveSQL(sql);
            Assert.fail("sql: " + sql + " should throw exception");
        }catch (ParsingException e) {
            Assert.assertTrue(e.getMessage().contains("Don't support unnest"));
        }
    }
}