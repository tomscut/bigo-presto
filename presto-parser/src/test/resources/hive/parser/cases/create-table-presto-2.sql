 CREATE TABLE tmp.rec_like_event_orc (
<<<<<<< HEAD
    uid BIGINT,
    day VARCHAR ,
    hour VARCHAR
=======
    uid bigint,                              
    video_id bigint,                         
    dispatch_id varchar,                     
    refer varchar,                           
    dispatched integer,                      
    displayed integer,                       
    clicked integer,                         
    slide_play integer,                      
    followed integer,                        
    liked integer,                           
    shared integer,                          
    comments integer,                        
    slide integer,                           
    stay integer,                            
    play_second integer,                     
    complete_count integer,                  
    update_timestamp varchar,                
    slide_time integer,                      
    event_time varchar,                      
    req_pos integer,                         
    ranker varchar,                          
    rough_ranker varchar,                    
    score varchar,                           
    rough_ranker_score varchar,              
    selector varchar,                        
    strategy varchar,                        
    check_status varchar,                    
    cover_ab varchar,                        
    plugin varchar,                          
    domain_type varchar,                     
    domain_value varchar,                    
    abflags_v3 varchar,                      
    user_type varchar,                       
    filter varchar,                          
    os varchar,                              
    country varchar,                         
    country_region varchar,                  
    lng bigint,                              
    lat bigint,                              
    net varchar,                             
    list_pos integer,                        
    ob1 varchar,                             
    ob2 varchar,                             
    ob3 varchar,                             
    ob4 varchar,                             
    ob5 varchar,                             
    day varchar,                             
    hour varchar                             
>>>>>>> support create table as select & support insert into
 )                                           
 WITH (                                      
    partitioned_by = ARRAY['day','hour'],
    format = 'ORC',
    location = 'hdfs://bigocluster/recommend/hive/tmp.db/rec_like_event_orc'
 )