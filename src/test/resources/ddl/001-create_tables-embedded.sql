DROP ALL OBJECTS;

CREATE TABLE app_name
(
     app_name  VARCHAR(100) NOT NULL
    ,app_description VARCHAR(255)

);

ALTER TABLE app_name 
  ADD CONSTRAINT PK_APP_NAME PRIMARY KEY (APP_NAME);

CREATE TABLE app_parameter
 (
   app_parameter_id     INTEGER NOT NULL
 , opco                 VARCHAR(10) DEFAULT 'DEFAULT'
 , envtype              VARCHAR(10) DEFAULT 'LIVE'
 , name                 VARCHAR(100)
 , value                VARCHAR(4000)
 , description          VARCHAR(4000)
 , startup				INTEGER DEFAULT 0
 , appname				VARCHAR(100) DEFAULT 'DEFAULT'
 , version				VARCHAR(100)
 , range				VARCHAR(100)
 );

ALTER TABLE app_parameter
  ADD CONSTRAINT nn_app_parameter_01 CHECK (app_parameter_id is not null);

ALTER TABLE app_parameter
  ADD CONSTRAINT nn_app_parameter_02 CHECK (opco is not null);

ALTER TABLE app_parameter
   ADD CONSTRAINT nn_app_parameter_03 CHECK (name is not null);

ALTER TABLE app_parameter
   ADD CONSTRAINT ck_app_parameter_05 CHECK (envtype in ('LIVE','STAGING'));

CREATE UNIQUE INDEX ix_app_parameter_01 ON app_parameter (app_parameter_id);

CREATE UNIQUE INDEX ix_app_parameter_02 ON app_parameter (opco, envtype, appname, name);
  
CREATE INDEX ix_app_parameter_03 ON app_parameter (name);

ALTER TABLE app_parameter
   ADD CONSTRAINT pk_app_parameter PRIMARY KEY (app_parameter_id);
   
ALTER TABLE app_parameter
   ADD CONSTRAINT nn_app_parameter_06 CHECK (startup is not null);
   
ALTER TABLE app_parameter
   ADD CONSTRAINT ck_app_parameter_06 CHECK (startup in (0,1));      

ALTER TABLE app_parameter ADD CONSTRAINT CK_APP_PARAMETER_07 FOREIGN KEY (APPNAME)
      REFERENCES APP_NAME (APP_NAME);

ALTER TABLE app_parameter
   ADD CONSTRAINT NN_APP_PARAMETER_04 CHECK (envtype is not null);

ALTER TABLE app_parameter
   ADD CONSTRAINT NN_APP_PARAMETER_08 CHECK (appname is not null);
 	
CREATE TABLE audit_parameter
  (
     audit_parameter_id INTEGER NOT NULL
    ,username           VARCHAR(30)
    ,app_parameter_id   INTEGER
    ,change_date        TIMESTAMP
    ,opco               VARCHAR(10)
    ,envtype            VARCHAR(10)
    ,name               VARCHAR(100)
    ,old_value          VARCHAR(4000)
    ,new_value          VARCHAR(4000)
    ,old_description    VARCHAR(4000)
    ,new_description    VARCHAR(4000)
    ,sql_action         VARCHAR(0008)
    ,change_description VARCHAR(4000)
    ,old_startup    	INTEGER
    ,new_startup		INTEGER    
 , appname				VARCHAR(100)
 , version				VARCHAR(100)
 , range				VARCHAR(100)  
  );

ALTER TABLE audit_parameter
  ADD CONSTRAINT nn_audit_parameter_01 CHECK (audit_parameter_id is not null);

ALTER TABLE audit_parameter
  ADD CONSTRAINT nn_audit_parameter_02 CHECK (app_parameter_id is not null);

ALTER TABLE audit_parameter
  ADD CONSTRAINT nn_audit_parameter_03 CHECK (change_date is not null);

ALTER TABLE audit_parameter
   ADD CONSTRAINT nn_audit_parameter_04 CHECK (change_description is not null);

ALTER TABLE audit_parameter
   ADD CONSTRAINT nn_audit_parameter_05 CHECK (username is not null);

ALTER TABLE audit_parameter
   ADD CONSTRAINT ck_audit_parameter_06 CHECK (envtype in ('LIVE','STAGING'));

CREATE UNIQUE INDEX ix_audit_parameter_01 ON audit_parameter (audit_parameter_id);

CREATE INDEX ix_audit_parameter_02 ON audit_parameter (opco, name, app_parameter_id);

CREATE INDEX ix_audit_parameter_03 ON audit_parameter (name, app_parameter_id);

create index IX_AUDIT_PARAMETER_06 on audit_parameter (change_date, app_parameter_id);

ALTER TABLE audit_parameter
  ADD CONSTRAINT pk_audit_parameter PRIMARY KEY (audit_parameter_id);
  
ALTER TABLE audit_parameter
   ADD CONSTRAINT ck_audit_parameter_07 CHECK (old_startup in (0,1));  
  
ALTER TABLE audit_parameter
   ADD CONSTRAINT ck_audit_parameter_08 CHECK (new_startup in (0,1));  

CREATE TABLE app_translation
 (
   app_translation_id     INTEGER NOT NULL
 , country              VARCHAR(10)  DEFAULT 'DEFAULT'
 , language              VARCHAR(10) 
 , name                 VARCHAR(100)
 , value                VARCHAR(4000)
 , description          VARCHAR(4000)
 );

ALTER TABLE app_translation
  ADD CONSTRAINT nn_app_translation_01 CHECK (app_translation_id is not null);

ALTER TABLE app_translation
  ADD CONSTRAINT nn_app_translation_02 CHECK (language is not null);

ALTER TABLE app_translation
   ADD CONSTRAINT nn_app_translation_03 CHECK (name is not null);

ALTER TABLE app_translation
   ADD CONSTRAINT nn_app_translation_04 CHECK (country is not null);

ALTER TABLE app_translation
   ADD CONSTRAINT pk_app_translation PRIMARY KEY (app_translation_id);

CREATE UNIQUE INDEX ix_app_translation_01 ON app_translation(country, language, name);  

CREATE TABLE audit_translation
  (
     audit_translation_id INTEGER NOT NULL
    ,username             VARCHAR(30)
    ,app_translation_id   INTEGER
    ,change_timestamp     TIMESTAMP
    ,sql_action           VARCHAR(0008) --insert or delete only
    ,country              VARCHAR(10)
    ,language             VARCHAR(10)
    ,name                 VARCHAR(100)
    ,value                VARCHAR(4000)
    ,description          VARCHAR(4000)
  );

ALTER TABLE audit_translation
  ADD CONSTRAINT nn_audit_translation_01 CHECK (audit_translation_id is not null);

ALTER TABLE audit_translation
  ADD CONSTRAINT nn_audit_translation_02 CHECK (app_translation_id is not null);

ALTER TABLE audit_translation
  ADD CONSTRAINT nn_audit_translation_03 CHECK (change_timestamp is not null);

ALTER TABLE audit_translation
   ADD CONSTRAINT nn_audit_translation_04 CHECK (username is not null);

ALTER TABLE audit_translation
  ADD CONSTRAINT pk_audit_translation PRIMARY KEY (audit_translation_id);

CREATE TABLE audit_app
(
    AUDIT_APP_NAME_ID INTEGER NOT NULL
   ,USERNAME          VARCHAR(30)
   ,SQL_ACTION        VARCHAR(8)
   ,APP_NAME          VARCHAR(100)
   ,APP_DESCRIPTION   VARCHAR(255)
   ,CHANGE_DATE       TIMESTAMP
);

CREATE UNIQUE INDEX PK_AUDIT_APP_PK ON AUDIT_APP (AUDIT_APP_NAME_ID);
     
ALTER TABLE audit_app ADD CONSTRAINT PK_AUDIT_APP PRIMARY KEY (AUDIT_APP_NAME_ID);

COMMIT;
