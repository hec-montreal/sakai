--
-- drop all foreign key constraints, so that we can define new ones
--
begin
    for r in ( select table_name,constraint_name
                from user_constraints
                where table_name in (upper('qrtz_triggers'),upper('qrtz_blob_triggers'),upper('qrtz_cron_triggers'),upper('qrtz_simple_triggers'))
                and constraint_type='R')
    loop
        execute immediate 'alter table '||r.table_name||' drop constraint '||r.constraint_name;
    end loop;
end;