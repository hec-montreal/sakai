--
-- add all primary and foreign key constraints, based on new columns
--
alter table qrtz_job_details drop primary key;
alter table qrtz_job_details add primary key (sched_name, job_name, job_group);
alter table qrtz_fired_triggers drop primary key;
alter table qrtz_fired_triggers add primary key (sched_name, entry_id);
alter table qrtz_calendars drop primary key;
alter table qrtz_calendars add primary key (sched_name, calendar_name);
alter table qrtz_locks drop primary key;
alter table qrtz_locks add primary key (sched_name, lock_name);
alter table qrtz_paused_trigger_grps drop primary key;
alter table qrtz_paused_trigger_grps add primary key (sched_name, trigger_group);
alter table qrtz_scheduler_state drop primary key;
alter table qrtz_scheduler_state add primary key (sched_name, instance_name);
alter table qrtz_triggers drop primary key;
alter table qrtz_triggers add primary key (sched_name, trigger_name, trigger_group);
alter table qrtz_triggers add foreign key (sched_name, job_name, job_group) references qrtz_job_details(sched_name, job_name, job_group);
alter table qrtz_blob_triggers drop primary key;
alter table qrtz_blob_triggers add primary key (sched_name, trigger_name, trigger_group);
alter table qrtz_blob_triggers add foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers(sched_name, trigger_name, trigger_group);
alter table qrtz_cron_triggers drop primary key;
alter table qrtz_cron_triggers add primary key (sched_name, trigger_name, trigger_group);
alter table qrtz_cron_triggers add foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers(sched_name, trigger_name, trigger_group);
alter table qrtz_simple_triggers drop primary key;
alter table qrtz_simple_triggers add primary key (sched_name, trigger_name, trigger_group);
alter table qrtz_simple_triggers add foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers(sched_name, trigger_name, trigger_group);
--
-- add new simprop_triggers table
--
CREATE TABLE qrtz_simprop_triggers
 (          
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 NUMBER(10,0) NULL,
    INT_PROP_2 NUMBER(10,0) NULL,
    LONG_PROP_1 NUMBER(19,0) NULL,
    LONG_PROP_2 NUMBER(19,0) NULL,
    DEC_PROP_1 NUMBER(13,4) NULL,
    DEC_PROP_2 NUMBER(13,4) NULL,
    BOOL_PROP_1 VARCHAR2(1) NULL,
    BOOL_PROP_2 VARCHAR2(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
--
-- create indexes for faster queries
--
drop index idx_qrtz_j_req_recovery;
create index idx_qrtz_j_req_recovery on qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_j_grp on qrtz_job_details(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_t_j on qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_t_jg on qrtz_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_t_c on qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
create index idx_qrtz_t_g on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
drop index idx_qrtz_t_state;
create index idx_qrtz_t_state on qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
create index idx_qrtz_t_n_state on qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_n_g_state on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
drop index idx_qrtz_t_next_fire_time;
create index idx_qrtz_t_next_fire_time on qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
drop index idx_qrtz_t_nft_st;
create index idx_qrtz_t_nft_st on qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_nft_st_misfire_grp on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);
drop index idx_qrtz_ft_trig_inst_name; 
create index idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index idx_qrtz_ft_inst_job_req_rcvry on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_ft_j_g on qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_ft_jg on qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_ft_t_g on qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_tg on qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);

--
-- SAM-948 - MIN_SCORE option in Samigo
--

alter table SAM_ITEM_T add MIN_SCORE double precision NULL;
alter table SAM_PUBLISHEDITEM_T add MIN_SCORE double precision NULL;

-- KNL-1405 Don't have defaults for TIMESTAMP in SAKAI_SESSION
-- HEC we already have this
--ALTER TABLE SAKAI_SESSION MODIFY SESSION_START NULL;
--ALTER TABLE SAKAI_SESSION MODIFY SESSION_END NULL;

-- 1389 GradebookNG sortable assignments within categories, add CATEGORIZED_SORT_ORDER to GB_GRADABLE_OBJECT_T
ALTER TABLE GB_GRADABLE_OBJECT_T ADD CATEGORIZED_SORT_ORDER number;
-- 1840 Allow quick queries of grading events by date graded
CREATE INDEX GB_GRADING_EVENT_T_DATE_OBJ_ID ON GB_GRADING_EVENT_T (DATE_GRADED, GRADABLE_OBJECT_ID);
-- 
-- SAM-1117 - Option to not display scores
--

alter table SAM_ASSESSACCESSCONTROL_T add DISPLAYSCORE number(10);
alter table SAM_PUBLISHEDACCESSCONTROL_T add DISPLAYSCORE number(10);
alter table SAM_ITEM_T add SCORE_DISPLAY_FLAG number(1,0) default '0' not null;
alter table SAM_PUBLISHEDITEM_T add SCORE_DISPLAY_FLAG number(1,0) default '0' not null;

INSERT INTO SAM_ASSESSMETADATA_T ("ASSESSMENTMETADATAID", "ASSESSMENTID","LABEL",
     "ENTRY")
     VALUES(sam_assessMetaData_id_s.nextVal, 1, 'displayScores_isInstructorEditable', 'true');

INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL,
    ENTRY)
    VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Formative Assessment'
     AND TYPEID='142' AND ISTEMPLATE=1),
      'displayScores_isInstructorEditable', 'true');

INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL,
    ENTRY)
    VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Quiz'
     AND TYPEID='142' AND ISTEMPLATE=1),
      'displayScores_isInstructorEditable', 'true');

INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL,
    ENTRY)
    VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Problem Set'
     AND TYPEID='142' AND ISTEMPLATE=1),
      'displayScores_isInstructorEditable', 'true');

INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL,
    ENTRY)
    VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Survey'
     AND TYPEID='142' AND ISTEMPLATE=1),
      'displayScores_isInstructorEditable', 'true');

INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL,
    ENTRY)
    VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Test'
     AND TYPEID='142' AND ISTEMPLATE=1),
      'displayScores_isInstructorEditable', 'true');

INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL,
    ENTRY)
    VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Timed Test'
     AND TYPEID='142' AND ISTEMPLATE=1),
      'displayScores_isInstructorEditable', 'true');

-- LTI CHANGES !!!
alter table LTI_CONTENT add FA_ICON varchar2(1024);
alter table LTI_CONTENT add CONTENTITEM CLOB;
alter table lti_tools add pl_launch number(3) default 0;
alter table lti_tools add pl_linkselection number(3) default 0;
alter table lti_tools add pl_fileitem number(3) default 0;
alter table lti_tools add pl_contenteditor number(3) default 0;
alter table lti_tools add pl_assessmentselection number(3) default 0;
alter table lti_tools add pl_importitem number(3) default 0;
alter table lti_tools add fa_icon varchar2(1024);
alter table lti_tools add tool_proxy_binding clob;

ALTER TABLE lti_content MODIFY (     title VARCHAR2(1024) );
ALTER TABLE lti_content MODIFY (     pagetitle VARCHAR2(1024) );
ALTER TABLE lti_content MODIFY (     consumerkey VARCHAR2(1024) );
ALTER TABLE lti_content MODIFY (     secret VARCHAR2(1024) );
alter table lti_content add temp CLOB;
update lti_content set temp=custom, custom=null;
alter table lti_content drop column custom;
alter table lti_content rename column temp to custom;

ALTER TABLE lti_tools MODIFY (     title VARCHAR2(1024) );
ALTER TABLE lti_tools MODIFY (     pagetitle VARCHAR2(1024) );
ALTER TABLE lti_tools MODIFY (     consumerkey VARCHAR2(1024) );
ALTER TABLE lti_tools MODIFY (     secret VARCHAR2(1024) );
alter table lti_tools add temp CLOB;
update lti_tools set temp=custom, custom=null;
alter table lti_tools drop column custom;
alter table lti_tools rename column temp to custom;

ALTER TABLE lti_deploy MODIFY (     title VARCHAR2(1024) );
ALTER TABLE lti_deploy MODIFY (     pagetitle VARCHAR2(1024) );
ALTER TABLE lti_deploy ADD (     allowcontentitem NUMBER(1) DEFAULT '0' );
ALTER TABLE lti_deploy MODIFY (     reg_key VARCHAR2(1024) );
ALTER TABLE lti_deploy MODIFY (     reg_password VARCHAR2(1024) );
ALTER TABLE lti_deploy ADD (	reg_ack CLOB );
ALTER TABLE lti_deploy MODIFY (     consumerkey VARCHAR2(1024) );
ALTER TABLE lti_deploy MODIFY (     secret VARCHAR2(1024) );
ALTER TABLE lti_deploy ADD (     new_secret VARCHAR2(1024) );

CREATE TABLE lti_memberships_jobs (
    SITE_ID VARCHAR2(99),
    memberships_id VARCHAR2(256),
    memberships_url CLOB,
    consumerkey VARCHAR2(1024),
    lti_version VARCHAR2(32)
);
-- END LTI CHANGES !!

-- LSNBLDR-500
alter table lesson_builder_pages add folder varchar2(250);
-- LSNBLDR-622
alter table lesson_builder_items modify (name varchar2(255 char));
alter table lesson_builder_pages modify (title varchar2(255 char));
alter table lesson_builder_p_eval_results modify (gradee null);                                                      
alter table lesson_builder_p_eval_results modify (row_text null);                                                    
alter table lesson_builder_p_eval_results add gradee_group varchar2(99) null;
alter table lesson_builder_p_eval_results add row_id number(20,0) default 0;
-- sites new with 10 will already have this but it was missing in 10 conversion scripts
ALTER TABLE lesson_builder_groups ADD (tmpgroups CLOB);
UPDATE lesson_builder_groups SET tmpgroups=groups;
ALTER TABLE lesson_builder_groups DROP COLUMN groups;
ALTER TABLE lesson_builder_groups RENAME COLUMN tmpgroups TO groups;

create table lesson_builder_ch_status (
        checklistId number(19,0) not null,
        checklistItemId number(19,0) not null,
        owner varchar2(99 char) not null,
        done number(1,0),
        primary key (checklistId,checklistItemId,owner)
 );
create index lb_p_eval_res_row on lesson_builder_p_eval_results(page_id);
create index lb_page_folder on lesson_builder_pages(siteId, folder);

-----------------------------------------------------------------------------
-- SAKAI_CONFIG_ITEM - KNL-1063 - ORACLE
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_CONFIG_ITEM (
	ID				NUMBER(20) NOT NULL,
	NODE			VARCHAR2(255),
	NAME			VARCHAR2(255) NOT NULL,
	VALUE			CLOB,  
	RAW_VALUE		CLOB,
	TYPE			VARCHAR2(255) NOT NULL,
	DEFAULT_VALUE	CLOB,
	DESCRIPTION		CLOB,
	SOURCE			VARCHAR2(255) DEFAULT NULL,
	DEFAULTED		CHAR(1) NOT NULL,
	REGISTERED		CHAR(1) NOT NULL,
	SECURED			CHAR(1) NOT NULL,
	DYNAMIC			CHAR(1) NOT NULL,
	CREATED			TIMESTAMP NOT NULL,
	MODIFIED		TIMESTAMP NOT NULL,
	POLL_ON			TIMESTAMP DEFAULT NULL
);

ALTER TABLE SAKAI_CONFIG_ITEM
	ADD  ( PRIMARY KEY (ID) );

CREATE INDEX SCI_NODE_IDX ON SAKAI_CONFIG_ITEM (NODE ASC);
CREATE INDEX SCI_NAME_IDX ON SAKAI_CONFIG_ITEM (NAME ASC);

CREATE SEQUENCE SAKAI_CFG_ITEM_S;

-- This is not needed if sequence match name in file HibernateConfigItem.hbm.xml
--CREATE OR REPLACE TRIGGER SCI_ID_AI
--BEFORE INSERT ON SAKAI_CONFIG_ITEM
--FOR EACH ROW
--BEGIN
--  SELECT SAKAI_CFG_ITEM_SEQ.NEXTVAL INTO :new.ID FROM dual;
--END;
--/

-- SAK-30032 Create table to handle Peer Review attachments --
CREATE TABLE ASN_PEER_ASSESSMENT_ATTACH_T (
ID NUMBER(19,0) NOT NULL,
SUBMISSION_ID varchar2(255) NOT NULL, 
ASSESSOR_USER_ID varchar2(255) NOT NULL, 
RESOURCE_ID varchar2(255) NOT NULL, 
PRIMARY KEY(ID)
);
CREATE SEQUENCE ASN_PEER_ATTACH_S START WITH 1 INCREMENT BY 1 nomaxvalue;
create index PEER_ASSESSOR_I on ASN_PEER_ASSESSMENT_ATTACH_T (SUBMISSION_ID, ASSESSOR_USER_ID);
-- END SAK-30032

-- KNL-1424 Add Message Bundle Manager to admin workspace
INSERT INTO SAKAI_SITE_PAGE VALUES('!admin-1575', '!admin', 'Message Bundle Manager', '0', 21, '0' );
INSERT INTO SAKAI_SITE_TOOL VALUES('!admin-1575', '!admin-1575', '!admin', 'sakai.message.bundle.manager', 1, 'Message Bundle Manager', NULL );
INSERT INTO SAKAI_SITE_PAGE_PROPERTY VALUES('!admin', '!admin-1575', 'sitePage.customTitle', 'true');
-- END KNL-1424

--SAM-2709 Submission Email Notifications Hidden Inappropriately--
ALTER TABLE SAM_ASSESSACCESSCONTROL_T ADD INSTRUCTORNOTIFICATION integer;
ALTER TABLE SAM_PUBLISHEDACCESSCONTROL_T ADD INSTRUCTORNOTIFICATION integer;

INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL, ENTRY)
    VALUES(sam_assessMetaData_id_s.nextVal, 1, 'instructorNotification_isInstructorEditable', 'true') ;
 
 INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL, ENTRY)
     VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Formative Assessment'
      AND TYPEID='142' AND ISTEMPLATE=1),
       'instructorNotification_isInstructorEditable', 'true');
 
 INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL, ENTRY)
     VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Quiz'
      AND TYPEID='142' AND ISTEMPLATE=1),
       'instructorNotification_isInstructorEditable', 'true');
 
 INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL, ENTRY)
     VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Problem Set'
      AND TYPEID='142' AND ISTEMPLATE=1),
       'instructorNotification_isInstructorEditable', 'true');
 
 INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL, ENTRY)
     VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Survey'
      AND TYPEID='142' AND ISTEMPLATE=1),
       'instructorNotification_isInstructorEditable', 'true');
 
 INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL, ENTRY)
     VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Test'
      AND TYPEID='142' AND ISTEMPLATE=1),
       'instructorNotification_isInstructorEditable', 'true');
 
 INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL, ENTRY)
     VALUES(sam_assessMetaData_id_s.nextVal, (SELECT ID FROM SAM_ASSESSMENTBASE_T WHERE TITLE='Timed Test'
      AND TYPEID='142' AND ISTEMPLATE=1),
       'instructorNotification_isInstructorEditable', 'true');
--END SAM-2709

-- SAK-29442 Sequence LB_PEER_EVAL_RESULT_S Missing
CREATE SEQUENCE LB_PEER_EVAL_RESULT_S;
-- END SAK-29442

-- SAM-2751
ALTER TABLE SAM_ASSESSACCESSCONTROL_T ADD HONORPLEDGE NUMBER(1,0);
ALTER TABLE SAM_PUBLISHEDACCESSCONTROL_T ADD HONORPLEDGE NUMBER(1,0);
INSERT INTO SAM_ASSESSMETADATA_T (ASSESSMENTMETADATAID, ASSESSMENTID, LABEL, ENTRY)
    SELECT SAM_ASSESSMETADATA_ID_S.nextval, ASSESSMENTID, LABEL, ENTRY
     FROM (SELECT DISTINCT ASSESSMENTID, 'honorpledge_isInstructorEditable' as LABEL, 'true' as ENTRY
            FROM SAM_ASSESSMETADATA_T WHERE ASSESSMENTID NOT IN
            (SELECT DISTINCT ASSESSMENTID FROM SAM_ASSESSMETADATA_T WHERE LABEL = 'honorpledge_isInstructorEditable'));
-- END SAM-2751

-- SAM-1200 Oracle conversion, size increases of Samigo columns
alter table SAM_PUBLISHEDASSESSMENT_T add tempcol clob;
update SAM_PUBLISHEDASSESSMENT_T set tempcol=description;
alter table SAM_PUBLISHEDASSESSMENT_T drop column description;
alter table SAM_PUBLISHEDASSESSMENT_T rename column tempcol to description;

alter table SAM_PUBLISHEDSECTION_T add tempcol clob;
update SAM_PUBLISHEDSECTION_T set tempcol=description;
alter table SAM_PUBLISHEDSECTION_T drop column description;
alter table SAM_PUBLISHEDSECTION_T rename column tempcol to description;

alter table SAM_ASSESSMENTBASE_T add tempcol clob;
update SAM_ASSESSMENTBASE_T set tempcol=description;
alter table SAM_ASSESSMENTBASE_T drop column description;
alter table SAM_ASSESSMENTBASE_T rename column tempcol to description;

alter table SAM_SECTION_T add tempcol clob;
update SAM_SECTION_T set tempcol=description;
alter table SAM_SECTION_T drop column description;
alter table SAM_SECTION_T rename column tempcol to description;

alter table SAM_ITEMGRADING_T add tempcol clob;
update SAM_ITEMGRADING_T set tempcol=comments;
alter table SAM_ITEMGRADING_T drop column comments;
alter table SAM_ITEMGRADING_T rename column tempcol to comments;

alter table SAM_ASSESSMENTGRADING_T add tempcol clob;
update SAM_ASSESSMENTGRADING_T set tempcol=comments;
alter table SAM_ASSESSMENTGRADING_T drop column comments;
alter table SAM_ASSESSMENTGRADING_T rename column tempcol to comments;
-- END SAM-1200

CREATE TABLE SST_LESSONBUILDER
(ID             NUMBER(19) PRIMARY KEY,
 USER_ID        VARCHAR2(99) NOT NULL,
 SITE_ID        VARCHAR2(99) NOT NULL,
 PAGE_REF       VARCHAR2(255) NOT NULL,
 PAGE_ID        NUMBER(19) NOT NULL,
 PAGE_ACTION    VARCHAR2(12) NOT NULL,
 PAGE_DATE      DATE NOT NULL,
 PAGE_COUNT     NUMBER(19) NOT NULL
);

CREATE SEQUENCE SST_LESSONBUILDER_ID;

CREATE INDEX SST_LESSONBUILDER_PAGE_ACT_IDX ON SST_LESSONBUILDER (PAGE_ACTION);

CREATE INDEX SST_LESSONBUILDER_DATE_IX ON SST_LESSONBUILDER (PAGE_DATE);

CREATE INDEX SST_LESSONBUILDER_SITE_ID_IX ON SST_LESSONBUILDER (SITE_ID);

CREATE INDEX SST_LESSONBUILDER_USER_ID_IX ON SST_LESSONBUILDER (USER_ID);

CREATE TABLE MFR_ANONYMOUS_MAPPING_T (
  SITE_ID varchar(255) NOT NULL,
  USER_ID varchar(255) NOT NULL,
  ANON_ID varchar(255) NOT NULL,
  PRIMARY KEY (SITE_ID,USER_ID)
);

CREATE TABLE MFR_RANK_INDIVIDUAL_T (
  RANK_ID number(19,0) NOT NULL,
  USER_ID varchar(99) NOT NULL,
  PRIMARY KEY (RANK_ID,USER_ID),
  CONSTRAINT mfr_rank_indiv_fk FOREIGN KEY (RANK_ID) REFERENCES MFR_RANK_T (ID)
);

CREATE INDEX mfr_rank_indiv_idx ON MFR_RANK_INDIVIDUAL_T (RANK_ID);

-- SAK-30141 New permissions
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.nextval, 'syllabus.add.item');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.nextval, 'syllabus.bulk.add.item');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.nextval, 'syllabus.bulk.edit.item');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.nextval, 'syllabus.redirect');

-- maintain
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.edit.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.redirect'));

-- maintain
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.edit.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.redirect'));

-- Instructor
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.edit.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.redirect'));

-- Administrator
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.lti'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Administrator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.lti'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Administrator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.lti'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Administrator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.edit.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.lti'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Administrator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.redirect'));

-- Instructor
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.lti'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.lti'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.add.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.lti'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.bulk.edit.item'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.lti'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'syllabus.redirect'));

-- Permission backfill

-- for each realm that has a role matching something in this table, we will add to that role the function from this table
CREATE TABLE PERMISSIONS_SRC_TEMP (ROLE_NAME VARCHAR(99), FUNCTION_NAME VARCHAR(99));

-- maintain
INSERT INTO PERMISSIONS_SRC_TEMP values ('maintain','syllabus.add.item');
INSERT INTO PERMISSIONS_SRC_TEMP values ('maintain','syllabus.bulk.add.item');
INSERT INTO PERMISSIONS_SRC_TEMP values ('maintain','syllabus.bulk.edit.item');
INSERT INTO PERMISSIONS_SRC_TEMP values ('maintain','syllabus.redirect');

-- Instructor
INSERT INTO PERMISSIONS_SRC_TEMP values ('Instructor','syllabus.add.item');
INSERT INTO PERMISSIONS_SRC_TEMP values ('Instructor','syllabus.bulk.add.item');
INSERT INTO PERMISSIONS_SRC_TEMP values ('Instructor','syllabus.bulk.edit.item');
INSERT INTO PERMISSIONS_SRC_TEMP values ('Instructor','syllabus.redirect');

-- lookup the role and function numbers
CREATE TABLE PERMISSIONS_TEMP (ROLE_KEY INTEGER, FUNCTION_KEY INTEGER);
INSERT INTO PERMISSIONS_TEMP (ROLE_KEY, FUNCTION_KEY)
SELECT SRR.ROLE_KEY, SRF.FUNCTION_KEY
from PERMISSIONS_SRC_TEMP TMPSRC
JOIN SAKAI_REALM_ROLE SRR ON (TMPSRC.ROLE_NAME = SRR.ROLE_NAME)
JOIN SAKAI_REALM_FUNCTION SRF ON (TMPSRC.FUNCTION_NAME = SRF.FUNCTION_NAME);

-- insert the new functions into the roles of any existing realm that has the role (don't convert the "!site.helper" OR "!user.template")
INSERT INTO SAKAI_REALM_RL_FN (REALM_KEY, ROLE_KEY, FUNCTION_KEY)
SELECT
    SRRFD.REALM_KEY, SRRFD.ROLE_KEY, TMP.FUNCTION_KEY
FROM
    (SELECT DISTINCT SRRF.REALM_KEY, SRRF.ROLE_KEY FROM SAKAI_REALM_RL_FN SRRF) SRRFD
    JOIN PERMISSIONS_TEMP TMP ON (SRRFD.ROLE_KEY = TMP.ROLE_KEY)
    JOIN SAKAI_REALM SR ON (SRRFD.REALM_KEY = SR.REALM_KEY)
    WHERE SR.REALM_ID != '!site.helper' AND SR.REALM_ID NOT LIKE '!user.template%'
    AND NOT EXISTS (
        SELECT 1
            FROM SAKAI_REALM_RL_FN SRRFI
            WHERE SRRFI.REALM_KEY=SRRFD.REALM_KEY AND SRRFI.ROLE_KEY=SRRFD.ROLE_KEY AND SRRFI.FUNCTION_KEY=TMP.FUNCTION_KEY
    );

-- clean up the temp tables
DROP TABLE PERMISSIONS_TEMP;
DROP TABLE PERMISSIONS_SRC_TEMP;
-- ------------------------------
--  END permission backfill -----
-- ------------------------------

-- END SAK-30141

-- SAK-30144: Add the new 'EID' column to the VALIDATIONACCOUNT_ITEM table
ALTER TABLE VALIDATIONACCOUNT_ITEM ADD EID VARCHAR2(255);

-- SAK-31468 rename existing gradebooks to 'Gradebook Classic'
-- This will not change any tool placements. To do that, uncomment the following line:
-- UPDATE SAKAI_SITE_TOOL SET REGISTRATION='sakai.gradebookng' WHERE REGISTRATION='sakai.gradebook.tool';
UPDATE SAKAI_SITE_TOOL SET TITLE='Gradebook Classic' WHERE TITLE='Gradebook';
UPDATE SAKAI_SITE_PAGE SET TITLE='Gradebook Classic' WHERE TITLE='Gradebook';

--
-- Copyright 2003 Sakai Foundation Licensed under the
-- Educational Community License, Version 2.0 (the "License"); you may
-- not use this file except in compliance with the License. You may
-- obtain a copy of the License at
--
-- http://www.osedu.org/licenses/ECL-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an "AS IS"
-- BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- permissions and limitations under the License.
--

-- Oracle conversion script - 1.4 to 11

alter table EVAL_EVALUATION add (SECTION_AWARE number(1,0) default 0 not null);

create table EVAL_HIERARCHY_RULE (
    ID number(19,0) not null,
    NODE_ID number(19,0) not null,
    RULE varchar(255) not null,
    OPT varchar(10) not null,
    primary key (ID)
);

--ZCII-2404 - remove rutgers link tool everywhere
delete from sakai_site_tool_property where tool_id in (select tool_id from SAKAI_SITE_TOOL where registration like 'sakai.rutgers.linktool');
delete from SAKAI_SITE_TOOL where registration like 'sakai.rutgers.linktool';
