insert into tmp.test
with
t1 as (select uid,name,day from tmp.test)
select uid,name,day from t1