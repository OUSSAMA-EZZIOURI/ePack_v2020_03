--############################# SQL Patch Version 2019.06 #################################
--SQL Patch to execute for new version 2019.06:
--------------------------------

-- SEQUENCE: public.config_transporter_id_seq

-- DROP SEQUENCE public.config_transporter_id_seq;

CREATE SEQUENCE public.config_transporter_id_seq
    INCREMENT 1
    START 8
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.config_transporter_id_seq
    OWNER TO openpg;

-- DROP TABLE public.config_transporter;

CREATE TABLE public.config_transporter
(
    id integer NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT config_transporter_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.config_transporter
    OWNER to openpg;

COMMENT ON COLUMN public.config_transporter.name
    IS 'Name';

ALTER TABLE public.config_transporter
    ADD CONSTRAINT transporter_unique_name UNIQUE (name);

ALTER TABLE public.config_transporter
    ADD COLUMN active boolean DEFAULT True;

INSERT INTO public.config_transporter(
	id, name, active)
	VALUES 
        (1, 'SJL', true),
        (2, 'Vectorys', false),
        (3, 'Germanetti', true),
        (4, 'GCL', true),
        (5, 'Schenker', true),
        (6, 'DHL', true),
        (7, 'Airseas', true);


-- ----------------------

ALTER TABLE public.config_family ADD
CONSTRAINT unique_family_name UNIQUE (harness_type, project);

-- ----------------------

ALTER TABLE public.config_segment ADD
CONSTRAINT unique_segment_project_combination UNIQUE (segment, project);

-- ----------------------

ALTER TABLE public.config_workplace ADD
CONSTRAINT unique_project_segment_workplace_combination UNIQUE (project, segment, workplace);

-- ----------------------

ALTER TABLE public.config_warehouse ADD
CONSTRAINT unique_project_warehouse_whtype_combination UNIQUE (warehouse, project, wh_type);

-- ----------------------

ALTER TABLE public.config_warehouse ADD
CONSTRAINT unique_project_warehouse_combination UNIQUE (warehouse, project);

-- ----------------------

ALTER TABLE public.config_company ADD
CONSTRAINT unique_company_name UNIQUE (company_name);

-- ----------------------

ALTER TABLE public.config_project ADD
CONSTRAINT unique_project UNIQUE (project);

-- ----------------------

-- config warehouse
UPDATE public.config_warehouse
	SET wh_type='FINISHED_GOODS'
	WHERE wh_type='FINISH_GOODS';

-- base_container
ALTER TABLE base_container
    ADD COLUMN priority integer NOT NULL DEFAULT 99,
    ADD COLUMN openning_sheet_copies integer NOT NULL DEFAULT 1,
    ADD COLUMN closing_sheet_copies integer NOT NULL DEFAULT 1;

COMMENT ON COLUMN public.base_container.priority
    IS 'More the number is lower, more the priority is high, default is 99';

COMMENT ON COLUMN public.base_container.openning_sheet_copies
    IS 'How many copies of the open sheet will be printed';

COMMENT ON COLUMN public.base_container.closing_sheet_copies
    IS 'How many copies of the closing sheet will be printed';

ALTER TABLE public.base_container
    ADD COLUMN closing_sheet_format integer DEFAULT 1;

COMMENT ON COLUMN public.base_container.closing_sheet_format
    IS '1 : A simple format (user for Ducati project)
2 : An advanced format (Used for Perkins and Volvo)';

ALTER TABLE public.base_container
    ADD COLUMN print_destination boolean NOT NULL DEFAULT False;

COMMENT ON COLUMN public.base_container.print_destination
    IS 'Boolean
True : Print the destination in closing sheet label
False : Print à dash "-" unstead of destination';

-- drop_base_container
ALTER TABLE drop_base_container
    ADD COLUMN priority integer NOT NULL DEFAULT 99,
    ADD COLUMN openning_sheet_copies integer NOT NULL DEFAULT 1,
    ADD COLUMN closing_sheet_copies integer NOT NULL DEFAULT 1;

COMMENT ON COLUMN public.drop_base_container.priority
    IS 'More the number is lower, more the priority is high, default is 99';

COMMENT ON COLUMN public.drop_base_container.openning_sheet_copies
    IS 'How many copies of the open sheet will be printed';

COMMENT ON COLUMN public.drop_base_container.closing_sheet_copies
    IS 'How many copies of the closing sheet will be printed';

ALTER TABLE public.drop_base_container
    ADD COLUMN closing_sheet_format integer DEFAULT 1;

COMMENT ON COLUMN public.drop_base_container.closing_sheet_format
    IS '1 : A simple format (user for Ducati project)
2 : An advanced format (Used for Perkins and Volvo)';

ALTER TABLE public.drop_base_container
    ADD COLUMN print_destination boolean NOT NULL DEFAULT False;

COMMENT ON COLUMN public.drop_base_container.print_destination
    IS 'Boolean
True : Print the destination in closing sheet label
False : Print à dash "-" unstead of destination';

--- Rename of config_project to config_family

ALTER TABLE public.config_project RENAME TO config_family;
ALTER TABLE public.config_family
    RENAME CONSTRAINT config_project_pkey TO config_family_pkey;

--- Rename the sequence of config_project to config_family
ALTER SEQUENCE public.config_project_id_seq
    RENAME TO config_family_id_seq;

ALTER TABLE public.config_family
    RENAME customer TO project;

ALTER TABLE public.config_family
    ALTER COLUMN project TYPE character varying (255) COLLATE pg_catalog."default";


-- -----------------
-- Création de la table config_project et ajouter les projets éxistants
-- Table: public.config_project

-- DROP TABLE public.config_project;

CREATE TABLE public.config_project
(
    id integer NOT NULL,
    description character varying(255) COLLATE pg_catalog."default",
    project character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT config_project_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

INSERT INTO public.config_project(
	id, description, project)
	VALUES 
	(1, 'VOLVO', 'VOLVO'),
	(2, 'DUCATI', 'DUCATI'),
	(3, 'PERKINS', 'PERKINS');

CREATE SEQUENCE config_project_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE config_project_id_seq
  OWNER TO openpg;
	
SELECT setval('public.config_project_id_seq', 3, true);	

-- Add the type of destination
ALTER TABLE public.load_plan_destination
    ADD COLUMN dest_type character varying(255) NOT NULL DEFAULT '-';

COMMENT ON COLUMN public.load_plan_destination.dest_type
    IS 'VIRTUAL : Destination virtuelle qui n''apparait pas pendant la création d''un plan de chargement.'

-- FDP : Destination réel qui apparait pendant la création d''un plan de chargement.';

UPDATE public.load_plan_destination
	SET dest_type='FDP'
	WHERE project in ('VOLVO', 'DUCATI', 'PERKINS');

INSERT INTO public.load_plan_destination(
	id, destination, project, dest_type)
	VALUES (11,'Commun', 'VOLVO', 'VIRTUAL'),
	 (12,'Commun', 'DUCATI', 'VIRTUAL'),
	 (13,'Commun', 'PERKINS', 'VIRTUAL');

UPDATE public.load_plan_destination
	SET dest_type='VIRTUAL'	
	WHERE destination in ('INVENT. VOLVO','INVENT. DUCATI','INVENT. UK','DHL. VOLVO','DHL. DUCATI','DHL. PERKINS','Commun','Commun','Commun');

SELECT setval('public.load_plan_destination_id_seq', 14, true);

UPDATE public.load_plan_destination
	SET destination='PUP VOLVO FRANCE'
	WHERE destination='VMI';
	
UPDATE public.load_plan_destination
SET destination='PUP VOLVO SWEDEN'
WHERE destination='SKOVDE';

UPDATE public.load_plan_destination
	SET destination='ORBASSANO ITALY'
	WHERE destination='DUCATI';
	

-- Load plan
ALTER TABLE public.load_plan ALTER COLUMN delivery_time TYPE timestamp with time zone;
-- config_ucs
ALTER TABLE public.config_ucs
    ADD COLUMN print_destination boolean NOT NULL DEFAULT False;

COMMENT ON COLUMN public.config_ucs.print_destination
    IS 'Boolean
True : Print the destination in closing sheet label
False : Print à dash "-" unstead of destination';

ALTER TABLE public.load_plan
    ADD COLUMN transport_company character varying(255) NOT NULL;

COMMENT ON COLUMN public.load_plan.transport_company
    IS 'Transport partner';

-- Config UCS

ALTER TABLE config_ucs
    ADD COLUMN priority integer  NOT NULL DEFAULT 99,
    ADD COLUMN openning_sheet_copies integer  NOT NULL DEFAULT 1,
    ADD COLUMN closing_sheet_copies integer  NOT NULL DEFAULT 1;

COMMENT ON COLUMN public.config_ucs.priority
    IS 'More the number is lower, more the priority is high, default is 99';

COMMENT ON COLUMN public.config_ucs.openning_sheet_copies
    IS 'How many copies of the open sheet will be printed';

COMMENT ON COLUMN public.config_ucs.closing_sheet_copies
    IS 'How many copies of the closing sheet will be printed';

UPDATE config_ucs SET priority = 99, openning_sheet_copies=1, 
closing_sheet_copies=1;

ALTER TABLE public.config_ucs
    ADD COLUMN closing_sheet_format integer DEFAULT 1;

COMMENT ON COLUMN public.config_ucs.closing_sheet_format
    IS '1 : A simple format (user for Ducati project)
2 : An advanced format (Used for Perkins and Volvo)';

-- Requete pour mettre à jour les config avec LPN avec les nouvelles 
-- destinations (VOF/VOS)

UPDATE public.config_ucs	
        SET destination='SKOVDE'
WHERE supplier_part_number like '%S';

UPDATE public.config_ucs	
        SET destination='VMI'
WHERE supplier_part_number like '%F';

UPDATE public.config_ucs
	SET closing_sheet_format=1
	WHERE project in ('VOLVO', 'DUCATI');

UPDATE public.config_ucs
	SET closing_sheet_format=2
	WHERE project in ('PERKINS');

-- Mise de la FDP selon le supplier part number 100% VMI
UPDATE public.config_ucs	
        SET destination='VMI'
WHERE supplier_part_number in (
'26C59472A',
'26C06971A',
'26C34302A',
'26C34312A',
'26C32623A',
'26C31862A',
'26C44203A',
'26C43401A',
'26C43402A',
'26C39532A',
'26C38692A',
'26C45121A',
'26C97592A',
'26C98061A',
'26C98020A',
'26C24270A',
'26C97800A',
'26C97810A',
'26C97820A',
'26C97790A',
'26C55032A',
'26C98140A',
'26C97900A',
'26C97931A',
'26C97981A',
'26C98260A',
'26C24344A',
'26C97942A',
'26C97950A',
'26C41284A',
'26C29950A',
'26C31251A',
'26C41943A',
'26C41275A',
'26C48980A',
'26C50471A',
'26C54245F',
'26C51781A',
'26C53212A',
'26C79250A',
'26C63051F',
'26C59461A',
'26C63071A',
'26C76451A',
'26C97850A',
'26C79970A',
'26C90901A',
'26C82883A',
'26C90350A',
'26C04602A',
'26C22591A',
'26C22120A',
'26C94415F',
'26C00361A',
'26C04691A',
'26C03051A',
'26C13780A',
'26C15582A',
'26C35620A',
'26C35631F',
'26C40660A',
'26C22090A',
'26C36530A',
'26C30332A',
'26C35662A',
'26C36151A',
'26C36171A',
'26C36291F',
'26C37071A',
'26C42280A',
'26C44210A',
'26C42460A',
'26C45710F',
'26C97680A',
'26C97690A',
'26C97700A',
'26C97710A',
'26C97720A',
'26C97730A',
'26C55450A',
'26C97880F',
'26C97890F',
'26C97910A',
'26C45210A',
'26C33890A',
'26C97640A',
'26C93582A',
'26C41300A',
'26C22101A',
'26C04681A',
'26C13770A',
'26C36540F',
'26C45370A',
'26C98072A',
'26C94425F',
'26C97972F',
'26C41031A',
'26C43470A',
'26C43480A',
'26C43460A',
'26C97380A',
'26C94310A',
'26C85432A',
'26C93021A',
'26C07852A',
'26C18602A',
'26C23201A',
'26C98290A',
'26C90331A',
'26C90340A',
'26C90370A',
'26C90430A',
'26C90440A',
'26C90450A',
'26C90460A',
'26C97321A',
'26C97581A',
'26C53202A',
'26C36302A',
'26C36840A',
'26C79240A',
'26C36993A'
);

-- Mise de la FDP selon le supplier part number 100% SKOVDE
UPDATE public.config_ucs	
        SET destination='SKOVDE'
WHERE supplier_part_number in (
'26C74402A',
'26C31180A',
'26C35631A',
'26C36291A',
'26C44734A',
'26C45710A',
'26C97830A',
'26C98080A',
'26C97762A',
'26C97921A',
'26C98000A',
'26C97880A',
'26C98240A',
'26C97972A',
'26C98051A',
'26C98071A',
'26C98181A',
'26C98210A',
'26C98151A',
'26C98250A',
'26C98201A',
'26C41943S',
'26C48980S',
'26C51781S',
'26C06971S',
'26C59472S',
'26C82883S',
'26C04602S',
'26C22591S',
'26C13780S',
'26C35662S',
'26C43401S',
'26C37100A',
'26C97900S',
'26C41275S',
'26C41284S',
'26C54245A',
'26C94415A',
'26C94425A',
'26C36530S',
'26C42270A',
'26C42280S',
'26C42290S',
'26C97841A',
'26C98120A',
'26C98090A',
'26C90901S'
);

ALTER TABLE public.config_barcode
    ADD COLUMN config_index character varying(255) COLLATE pg_catalog."default";

COMMENT ON COLUMN public.config_barcode.config_index
    IS 'Index change of the harness';

ALTER TABLE public.config_barcode
    ADD COLUMN project character varying(255);

######### Données à ajouter via l'interface utilisateur :
-- 1 utilisateur / harness type
  Dummy / Dummy : login 0000, password 0000, access_level = 0000
-- 3 configurations à ajouter via GUI dans la table config_barecode pour le 
keyword DISPATCH_QTY :	
^-?\d*\,\d+$
^-?\d*\.\d+$
\d+



-- Creation de la table des dispatch label no

CREATE TABLE public.load_plan_dispatch_label
(
    id integer NOT NULL,
    create_id integer,
    create_time timestamp without time zone,
    dispatch_label_no character varying(255) COLLATE pg_catalog."default",
    fdp character varying(255) COLLATE pg_catalog."default",
    load_plan_id integer,
    part_number character varying(255) COLLATE pg_catalog."default",
    qty double precision,
    loadplan_id integer,
    CONSTRAINT load_plan_dispatch_label_pkey PRIMARY KEY (id),
    CONSTRAINT fkcb1b51ec215db945 FOREIGN KEY (loadplan_id)
        REFERENCES public.load_plan (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.load_plan_dispatch_label
    OWNER to openpg;

CREATE SEQUENCE public.load_plan_dispatch_label_id_seq
    INCREMENT 1
    START 1
    MAXVALUE 9223372036854775807  
  CACHE 1;

ALTER SEQUENCE public.load_plan_dispatch_label_id_seq
    OWNER TO postgres;

