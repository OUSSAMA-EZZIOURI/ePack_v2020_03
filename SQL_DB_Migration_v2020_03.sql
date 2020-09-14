
-- ----------- {{{{{{{{{{{{{{{{ [config_transporter] }}}}}}}}}}}}}}}} --------------

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
    ADD COLUMN IF NOT EXISTS active boolean DEFAULT True;

INSERT INTO public.config_transporter(
	id, name, active)
	VALUES 
        (1, 'SJL', true),
        (2, 'Vectorys', true),
        (3, 'Germanetti', true),
        (4, 'GCL', true),
        (5, 'Schenker', true),
        (6, 'DHL', true),
        (7, 'Airseas', true);

-- ----------------------

-- ----------- {{{{{{{{{{{{{{{{ [config_family] }}}}}}}}}}}}}}}} --------------
--- Rename of config_project to config_family

ALTER TABLE public.config_project RENAME TO config_family;
ALTER TABLE public.config_family  RENAME CONSTRAINT config_project_pkey TO config_family_pkey;

--- Rename the sequence of config_project to config_family
ALTER SEQUENCE public.config_project_id_seq  RENAME TO config_family_id_seq;
ALTER TABLE public.config_family RENAME customer TO project;
ALTER TABLE public.config_family ALTER COLUMN project TYPE character varying (256) COLLATE pg_catalog."default";

COMMENT ON COLUMN public.config_family.packaging_mode
    IS 'Packaging mode 1, 2 or 3';

ALTER TABLE public.config_family ADD
CONSTRAINT unique_family_name UNIQUE (harness_type, project);

-- INSERT INTO public.config_family(
--	id, harness_type, packaging_mode, project)
--	VALUES (2,'SMALL','2','VOLVO'),
--(3,'MDEP','2','VOLVO'),
--(1,'HDEP','2','VOLVO'),
--(4,'DUCATI','2','DUCATI'),
--(5,'PERKINS','3','PERKINS'),
--(6,'CNHI-Minors','3','CNHI'),
--(7,'CNHI-Engine','3','CNHI'),
-- (8,'PROTO','2','CV');

-- ----------- {{{{{{{{{{{{{{{{ [config_project] }}}}}}}}}}}}}}}} --------------

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
	(3, 'PERKINS', 'PERKINS'),
        (4, 'CNHI', 'CNHI');

CREATE SEQUENCE config_project_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 5
  CACHE 1;
ALTER TABLE config_project_id_seq
  OWNER TO openpg;

ALTER TABLE public.config_project ADD
CONSTRAINT unique_project UNIQUE (project);

-- ----------- {{{{{{{{{{{{{{{{ [config_segment] }}}}}}}}}}}}}}}} --------------

ALTER TABLE public.config_segment ADD
CONSTRAINT unique_segment_project_combination UNIQUE (segment, project);

-- ----------- {{{{{{{{{{{{{{{{ [config_workplace] }}}}}}}}}}}}}}}} --------------

ALTER TABLE public.config_workplace ADD
CONSTRAINT unique_project_segment_workplace_combination UNIQUE (project, segment, workplace);


-- ----------- {{{{{{{{{{{{{{{{ [config_warehouse] }}}}}}}}}}}}}}}} --------------
UPDATE public.config_warehouse
	SET wh_type='FINISHED_GOODS'
	WHERE wh_type='FINISH_GOODS';

ALTER TABLE public.config_warehouse ADD
CONSTRAINT unique_project_warehouse_combination UNIQUE (warehouse, project);

ALTER TABLE public.config_warehouse ADD
CONSTRAINT unique_project_warehouse_whtype_combination UNIQUE (warehouse, project, wh_type);

UPDATE config_warehouse SET wh_type = 'FINISHED_GOODS' WHERE id in (2,5,6);
UPDATE config_warehouse SET wh_type = 'PACKAGING' WHERE id in (1,7);
UPDATE config_warehouse SET wh_type = 'SCRAP' WHERE id in (4);
UPDATE config_warehouse SET warehouse = 'W64-PM-VOLVO' WHERE id in (1);
UPDATE config_warehouse SET warehouse = 'W64-PM-PERKINS' WHERE id in (7);
UPDATE config_warehouse SET wh_type = 'TRANSIT' WHERE id in (3,8,9);

INSERT INTO public.config_warehouse(
	id, description, warehouse, project, wh_type)
	VALUES (10, 'PACKAGING WAREHOUSE', 'W64-PM-DUCATI', 'DUCATI', 'PACKAGING');

ALTER TABLE public.config_warehouse
    ADD COLUMN IF NOT EXISTS wh_type character varying(255);

COMMENT ON COLUMN public.config_warehouse.wh_type
    IS 'The type of the warehouse
PACKAGING
FINISHED_GOODS
TRANSIT
SCRAP
BLOQUED';

-- ----------- {{{{{{{{{{{{{{{{ [base_container] }}}}}}}}}}}}}}}} --------------

ALTER TABLE base_container
    ADD COLUMN IF NOT EXISTS priority integer NOT NULL DEFAULT 99,
    ADD COLUMN IF NOT EXISTS openning_sheet_copies integer NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS closing_sheet_copies integer NOT NULL DEFAULT 1,	
	ADD COLUMN IF NOT EXISTS cra_std_time double precision DEFAULT 0.0;

COMMENT ON COLUMN public.base_container.priority
    IS 'More the number is lower, more the priority is high, default is 99';

COMMENT ON COLUMN public.base_container.openning_sheet_copies
    IS 'How many copies of the open sheet will be printed';

COMMENT ON COLUMN public.base_container.closing_sheet_copies
    IS 'How many copies of the closing sheet will be printed';

ALTER TABLE public.base_container
    ADD COLUMN IF NOT EXISTS closing_sheet_format integer DEFAULT 1;

COMMENT ON COLUMN public.base_container.closing_sheet_format
    IS '1 : A simple format (user for Ducati project)
2 : An advanced format (Used for Perkins and Volvo)';

ALTER TABLE public.base_container
    ADD COLUMN IF NOT EXISTS print_destination boolean NOT NULL DEFAULT True;


COMMENT ON COLUMN public.base_container.print_destination
    IS 'Boolean
True : Print the destination in closing sheet label
False : Print à dash "-" unstead of destination';

ALTER TABLE public.base_container
    ADD COLUMN IF NOT EXISTS packaging_warehouse character varying(255) COLLATE pg_catalog."default";

COMMENT ON COLUMN public.base_container.packaging_warehouse
    IS 'Packaging warehouse for this part number.';

ALTER TABLE public.base_container
    ADD COLUMN IF NOT EXISTS truck_no character varying(255) DEFAULT '-';

COMMENT ON COLUMN public.base_container.truck_no
    IS 'Truck number where this pallet is loaded';

ALTER TABLE public.base_container
    ADD COLUMN delete_time timestamp without time zone;

UPDATE base_container SET packaging_warehouse = 'W64-PM-VOLVO' WHERE project = 'VOLVO' OR supplier_part_number LIKE '26C%';
UPDATE base_container SET packaging_warehouse = 'W64-PM-DUCATI' WHERE project = 'DUCATI' OR supplier_part_number LIKE '26F%';
UPDATE base_container SET packaging_warehouse = 'W64-PM-PERKINS' WHERE project = 'PERKINS';
UPDATE base_container SET packaging_warehouse = 'W64-PM-CNHI' WHERE project = 'CNHI' OR supplier_part_number LIKE '26G%';

UPDATE base_container SET openning_sheet_copies = 1;

UPDATE base_container SET closing_sheet_copies = 1 WHERE project IN ('VOLVO', 'CNHI', 'DUCATI') OR supplier_part_number LIKE '26C%'  OR supplier_part_number LIKE '26G%'  OR supplier_part_number LIKE '26F%';
UPDATE base_container SET closing_sheet_copies = 2 WHERE project IN ('PERKINS');
UPDATE base_container SET closing_sheet_format = 2;

UPDATE base_container SET label_per_piece = false WHERE project IN ('VOLVO', 'CNHI', 'DUCATI') OR supplier_part_number LIKE '26C%'  OR supplier_part_number LIKE '26G%'  OR supplier_part_number LIKE '26F%'
UPDATE base_container SET label_per_piece = true WHERE project NOT IN ('VOLVO', 'CNHI', 'DUCATI');

UPDATE base_container SET print_destination = true;
UPDATE base_container SET priority = 0;

UPDATE base_container bc
SET dispatch_label_no = l.dispatch_label_no,
 truck_no = l.truck_no, 
 dispatch_time = l.delivery_time
FROM load_plan_line l
WHERE l.pallet_number = bc.pallet_number;

UPDATE base_container bc
SET 
 consign_no = lp.id,	
 truck_no = lp.truck_no, 
 dispatch_time = lp.delivery_time
FROM load_plan_line l, load_plan lp
WHERE lp.id = l.load_plan_id
AND l.pallet_number = bc.pallet_number;

-- -- block PERKINS closed pallets
UPDATE public.base_container
	SET container_state='BLOCKED', container_state_code='9999'
	WHERE pallet_number in ('200085500',
'200084233',
'200085162',
'200083437',
'200084104',
'200085379',
'200086960',
'200082148',
'200082648',
'200085715',
'200083579',
'200083743',
'200083752',
'200083946',
'200110733',
'200082426',
'200085159',
'200084425',
'200084422',
'200084519',
'200085368',
'200105785',
'200085231',
'200082506',
'200082109',
'200085169',
'200104283',
'200083918',
'200083915',
'200084528',
'200084718',
'200084730',
'200085326',
'200084128',
'200088018',
'200084711',
'200084686',
'200084708',
'200085164',
'200084047',
'200084052',
'200084064',
'200084115',
'200084099',
'200084262',
'200084275',
'200084274',
'200084361',
'200084403',
'200084397',
'200083028',
'200083023',
'200083582',
'200082821',
'200082814',
'200085675',
'200085156',
'200085662',
'200088599',
'200083735',
'200083585',
'200083586',
'200083587',
'200083630',
'200084362',
'200084366',
'200084895',
'200083340',
'200083604',
'200085375',
'200083150',
'200110682',
'200083453',
'200083455',
'200083737',
'200083593',
'200083600',
'200083603',
'200084091',
'200083605',
'200083606',
'200083620',
'200083740',
'200083750',
'200083766',
'200083775',
'200083791',
'200083788',
'200083795',
'200083793',
'200083892',
'200083899',
'200083920',
'200083912',
'200084046',
'200084043',
'200084110',
'200084070',
'200084124',
'200084196',
'200084215',
'200084368',
'200084522',
'200084551',
'200084569',
'200084548',
'200084552',
'200084542',
'200081693',
'200084836',
'200084908',
'200084823',
'200083935',
'200085001',
'200085002',
'200085011',
'200085018',
'200083329',
'200083316',
'200085027',
'200086965',
'200085047',
'200082108',
'200082152',
'200082154',
'200082158',
'200082167',
'200085161',
'200083757',
'200083786',
'200082466',
'200083433',
'200085719',
'200082628',
'200082602',
'200085807',
'200085812',
'200085386',
'200085483',
'200085561',
'200086209',
'200085222',
'200084251',
'200082992',
'200081763',
'200082980',
'200081442',
'200085465',
'200084539',
'200085017',
'200085172',
'200085022',
'200084103',
'200082801',
'200083933',
'200106557',
'200084698',
'200084383',
'200082505',
'200082999',
'200083000',
'200083004',
'200083168',
'200083157',
'200083160',
'200083297',
'200083300',
'200083326',
'200083312',
'200083349',
'200083309',
'200083432',
'200083431',
'200084076',
'200084107',
'200085040',
'200082421',
'200085883',
'200083576',
'200080840',
'200082113',
'200083308',
'200088180',
'200083923',
'200084668',
'200085553',
'200085342',
'200083601',
'200085688',
'200085313',
'200082304',
'200082312',
'200082409',
'200082411',
'200082408',
'200085814',
'200088276',
'200082991',
'200104619',
'200083937',
'200085645',
'200085647',
'200085192',
'200082653',
'200084675',
'200085215',
'200085665',
'200083753',
'200083164',
'200083607',
'200086964',
'200083782',
'200083930',
'200082652',
'200085475',
'200085728',
'200107543',
'200084679',
'200084559',
'200085476',
'200085190',
'200085816',
'200103241',
'200084256',
'200085066',
'200083800',
'200084260',
'200084719',
'200081443',
'200081447',
'200084517',
'200084520',
'200083148',
'200084830',
'200084681',
'200090073',
'200084416',
'200085020',
'200083457',
'200085320',
'200104232',
'200083153',
'200098577',
'200085685',
'200081730',
'200084671',
'200084716',
'200107547',
'200084254',
'200084259',
'200084832',
'200085194',
'200085830',
'200085538',
'200085028',
'200085187',
'200084900',
'200084912',
'200103243',
'200080844',
'200084224',
'200084230',
'200085041',
'200085188',
'200085825',
'200085468',
'200083934',
'200085747',
'200093931',
'200085603',
'200084534',
'200085464',
'200082631',
'200083889',
'200083483',
'200085230',
'200084837',
'200084902',
'200083155',
'200083761',
'200084827',
'200085518',
'200085516',
'200083580',
'200083887',
'200085019',
'200085347',
'200083429',
'200084123',
'200084119',
'200084054',
'200083774',
'200084533',
'200083460',
'200084044',
'200082498',
'200082643',
'200084253',
'200083452',
'200083623',
'200083621',
'200095381',
'200083895',
'200083943',
'200084048',
'200095395',
'200095397',
'200084516',
'200084694',
'200084743',
'200084732',
'200084749',
'200085782',
'200085804',
'200085168',
'200085311',
'200083440',
'200083438',
'200082503',
'200085191',
'200085224',
'200082809',
'200082405',
'200085365',
'200085667',
'200106311',
'200084198',
'200085211',
'200082118',
'200083641',
'200085049',
'200085058',
'200084554',
'200083890',
'200080829',
'200085818',
'200085820',
'200082311',
'200098588',
'200081781',
'200085201',
'200085216',
'200084360',
'200083928',
'200082112',
'200085158',
'200085202',
'200083010',
'200083759',
'200082147',
'200082130',
'200082483',
'200085681',
'200113645',
'200083441',
'200083468',
'200084204',
'200082599',
'200082614',
'200084042',
'200085568',
'200081759',
'200081668',
'200081681',
'200084392',
'200084885',
'200084889',
'200084893',
'200084884',
'200085218',
'200084858',
'200084277',
'200085006',
'200084286',
'200082600',
'200085488',
'200083764',
'200082823',
'200084205',
'200084365',
'200084390',
'200085035',
'200083577',
'200084370',
'200081717',
'200084829',
'200084825',
'200082666',
'200106025',
'200108441',
'200083475',
'200083304',
'200084574',
'200085522',
'200083888',
'200082805',
'200082598',
'200085362',
'200083439',
'200083465',
'200083763',
'200085463',
'200084201',
'200081609',
'200082984',
'200082996',
'200083632',
'200084701',
'200084715',
'200083897',
'200081712',
'200084850',
'200085036',
'200083936',
'200085003',
'200085057',
'200085029',
'200107544',
'200085651',
'200084584',
'200084101',
'200085870',
'200085695',
'200082827',
'200083649',
'200088551',
'200085790',
'200106647',
'200084919',
'200085070',
'200083961',
'200083484',
'200084750',
'200083447')
;


-- ----------- {{{{{{{{{{{{{{{{ [base_harness] }}}}}}}}}}}}}}}} --------------
ALTER TABLE public.base_harness
    ADD COLUMN deleted boolean NOT NULL DEFAULT false,
	ADD COLUMN IF NOT EXISTS cra_std_time double precision DEFAULT 0.0;
	

-- ----------- {{{{{{{{{{{{{{{{ [drop_base_container] }}}}}}}}}}}}}}}} --------------
ALTER TABLE drop_base_container
    ADD COLUMN IF NOT EXISTS priority integer NOT NULL DEFAULT 99,
    ADD COLUMN IF NOT EXISTS openning_sheet_copies integer NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS closing_sheet_copies integer NOT NULL DEFAULT 1;

COMMENT ON COLUMN public.drop_base_container.priority
    IS 'More the number is lower, more the priority is high, default is 99';

COMMENT ON COLUMN public.drop_base_container.openning_sheet_copies
    IS 'How many copies of the open sheet will be printed';

COMMENT ON COLUMN public.drop_base_container.closing_sheet_copies
    IS 'How many copies of the closing sheet will be printed';

ALTER TABLE public.drop_base_container
    ADD COLUMN IF NOT EXISTS closing_sheet_format integer DEFAULT 1;

COMMENT ON COLUMN public.drop_base_container.closing_sheet_format
    IS '1 : A simple format (user for Ducati project)
2 : An advanced format (Used for Perkins and Volvo)';

ALTER TABLE public.drop_base_container
    ADD COLUMN IF NOT EXISTS print_destination boolean NOT NULL DEFAULT True;

COMMENT ON COLUMN public.drop_base_container.print_destination
    IS 'Boolean
True : Print the destination in closing sheet label
False : Print à dash "-" unstead of destination';

-- Add the type of destination
ALTER TABLE public.load_plan_destination
    ADD COLUMN IF NOT EXISTS dest_type character varying(255) NOT NULL DEFAULT '-';

COMMENT ON COLUMN public.load_plan_destination.dest_type
    IS 'VIRTUAL : Destination virtuelle qui n''apparait pas pendant la création d''un plan de chargement.';

-- FDP : Destination réel qui apparait pendant la création d''un plan de chargement.';

-- ----------- {{{{{{{{{{{{{{{{ [load_plan_destination] }}}}}}}}}}}}}}}} --------------

UPDATE public.load_plan_destination
	SET dest_type='FDP'
	WHERE project in ('VOLVO', 'DUCATI', 'PERKINS', 'CNHI');

INSERT INTO public.load_plan_destination(
	id, destination, project, dest_type)
	VALUES (14,'Commun', 'VOLVO', 'VIRTUAL'),
	 (15,'Commun', 'DUCATI', 'VIRTUAL'),
	 (16,'Commun', 'PERKINS', 'VIRTUAL'),
	 (17,'Commun', 'CNHI', 'VIRTUAL');

UPDATE public.load_plan_destination
	SET dest_type='VIRTUAL'	
	WHERE destination in ('INVENT. VOLVO','INVENT. DUCATI','INVENT. UK','DHL. VOLVO','DHL. DUCATI','DHL. PERKINS','Commun');

SELECT setval('public.load_plan_destination_id_seq', 20, true);

UPDATE public.load_plan_destination
	SET destination='PUP VOLVO FRANCE'
	WHERE destination='VMI';
	
UPDATE public.load_plan_destination
SET destination='PUP VOLVO SWEDEN'
WHERE destination='SKOVDE';

UPDATE public.load_plan_destination
	SET destination='ORBASSANO ITALY'
	WHERE destination='DUCATI';
	
UPDATE public.load_plan_destination
	SET destination='ORBASSANO ITALY'
	WHERE destination='CNHI';
-- Load plan
ALTER TABLE public.load_plan ALTER COLUMN delivery_time TYPE timestamp with time zone;


-- ----------- {{{{{{{{{{{{{{{{ [config_ucs] }}}}}}}}}}}}}}}} --------------

ALTER TABLE public.config_ucs
    ADD COLUMN IF NOT EXISTS print_destination boolean NOT NULL DEFAULT True,
	ADD COLUMN cra_std_time double precision DEFAULT 0.0;
	

COMMENT ON COLUMN public.config_ucs.print_destination
    IS 'Boolean
True : Print the destination in closing sheet label
False : Print à dash "-" unstead of destination';

ALTER TABLE public.load_plan
    ADD COLUMN IF NOT EXISTS transport_company character varying(255) default '-';

COMMENT ON COLUMN public.load_plan.transport_company
    IS 'Transport partner';

ALTER TABLE config_ucs
    ADD COLUMN IF NOT EXISTS priority integer  NOT NULL DEFAULT 99,
    ADD COLUMN IF NOT EXISTS openning_sheet_copies integer  NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS closing_sheet_copies integer  NOT NULL DEFAULT 1;

COMMENT ON COLUMN public.config_ucs.priority
    IS 'More the number is lower, more the priority is high, default is 99';

COMMENT ON COLUMN public.config_ucs.openning_sheet_copies
    IS 'How many copies of the open sheet will be printed';

COMMENT ON COLUMN public.config_ucs.closing_sheet_copies
    IS 'How many copies of the closing sheet will be printed';

UPDATE config_ucs SET priority = 99, openning_sheet_copies=1, 
closing_sheet_copies=1;

ALTER TABLE public.config_ucs
    ADD COLUMN IF NOT EXISTS closing_sheet_format integer DEFAULT 1;

COMMENT ON COLUMN public.config_ucs.closing_sheet_format
    IS '1 : A simple format (user for Ducati project)
2 : An advanced format (Used for Perkins and Volvo)';

ALTER TABLE public.config_ucs
    ADD COLUMN IF NOT EXISTS label_per_piece boolean;

COMMENT ON COLUMN public.config_ucs.label_per_piece
    IS 'Print an A5 label for each scanned piece.
This label is different from open and closing sheet labels, If set to true, it will be printed once the user scan the QR code of a harness.';


-- Requete pour mettre à jour les config avec LPN avec les nouvelles 
-- destinations (VOF/VOS)

UPDATE public.config_ucs	
        SET packaging_warehouse='W64-PM-VOLVO'
WHERE supplier_part_number like '26C%';

UPDATE public.config_ucs	
        SET packaging_warehouse='W64-PM-CNHI'
WHERE supplier_part_number like '26G%';

UPDATE public.config_ucs	
        SET packaging_warehouse='W64-PM-DUCATI'
WHERE supplier_part_number like '26F%';

UPDATE public.config_ucs	
        SET label_per_piece=False
WHERE supplier_part_number like '26F%' OR supplier_part_number like '26G%' OR supplier_part_number like '26C%';

UPDATE public.config_ucs	
        SET closing_sheet_format=2
WHERE supplier_part_number like '26F%' OR supplier_part_number like '26G%' OR supplier_part_number like '26C%';



UPDATE public.config_ucs	
        SET destination='SKOVDE'
WHERE supplier_part_number like '%S';

UPDATE public.config_ucs	
        SET destination='VMI'
WHERE supplier_part_number like '%F';

UPDATE public.config_ucs
	SET closing_sheet_format=1
	WHERE project in ('VOLVO', 'DUCATI', 'CNHI');

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

ALTER TABLE public.config_ucs
    ADD COLUMN IF NOT EXISTS packaging_warehouse character varying(255) COLLATE pg_catalog."default";

COMMENT ON COLUMN public.config_ucs.packaging_warehouse
    IS 'Packaging warehouse for this part number.';

UPDATE config_ucs SET packaging_warehouse = 'W64-PM-VOLVO' WHERE project = 'VOLVO';
UPDATE config_ucs SET packaging_warehouse = 'W64-PM-DUCATI' WHERE project = 'DUCATI';
UPDATE config_ucs SET packaging_warehouse = 'W64-PM-PERKINS' WHERE project = 'PERKINS';
UPDATE config_ucs SET packaging_warehouse = 'W64-PM-CNHI' WHERE project = 'CNHI';


-- ----------- {{{{{{{{{{{{{{{{ [config_barcode] }}}}}}}}}}}}}}}} --------------


ALTER TABLE public.config_barcode
    ADD COLUMN IF NOT EXISTS config_index character varying(255) COLLATE pg_catalog."default";

COMMENT ON COLUMN public.config_barcode.config_index
    IS 'Index change of the harness';

ALTER TABLE public.config_barcode
    ADD COLUMN IF NOT EXISTS project character varying(255);

-- ######### Données à ajouter via l'interface utilisateur :
-- 1 utilisateur / harness type
--  Dummy / Dummy : login 0000, password 0000, access_level = 0000
-- 3 configurations à ajouter via GUI dans la table config_barecode pour le 
-- keyword DISPATCH_QTY :	
-- ^-?\d*\,\d+$
-- ^-?\d*\.\d+$
-- \d+

-- ----------- {{{{{{{{{{{{{{{{ [load_plan_dispatch_label] }}}}}}}}}}}}}}}} --------------

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
    OWNER TO openpg;

-- ----------- {{{{{{{{{{{{{{{{ [config_company] }}}}}}}}}}}}}}}} --------------

DROP TABLE public.config_company;

CREATE TABLE public.config_company
(
    id integer NOT NULL,
    address_1 character varying(255) COLLATE pg_catalog."default",
    address_2 character varying(255) COLLATE pg_catalog."default",
    city character varying(255) COLLATE pg_catalog."default",
    country character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    company_name character varying(255) COLLATE pg_catalog."default",
    website character varying(255) COLLATE pg_catalog."default",
    zip character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT config_company_pkey PRIMARY KEY (id),
    CONSTRAINT config_company_company_name_key UNIQUE (company_name)

)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.config_company
    OWNER to openpg;

INSERT INTO public.config_company(
	id, address_1, address_2, city, country, description, company_name, website, zip)
	VALUES (
		1, 
		'Sidi El Mekki, Route Settat, Berrechid, Morocco', 
		'-', 
		'Berrechid', 
		'Morocco', 
		'LEONI Wiring Systems Berrechid', 
		'LEONI Wiring Systems', 
		'www.leoni.com', 
		'26114');


-- Column: public.config_ucs.packaging_warehouse

ALTER TABLE public.config_company ADD
CONSTRAINT unique_company_name UNIQUE (company_name);



-- ----------- {{{{{{{{{{{{{{{{ [load_plan] }}}}}}}}}}}}}}}} --------------

ALTER TABLE public.load_plan
    ADD COLUMN IF NOT EXISTS fg_warehouse character varying(255) COLLATE pg_catalog."default";

COMMENT ON COLUMN public.load_plan.fg_warehouse
    IS 'Finish good warehouse to get the quantities from';

ALTER TABLE public.load_plan
    ADD COLUMN IF NOT EXISTS packaging_warehouse character varying(255) COLLATE pg_catalog."default";

COMMENT ON COLUMN public.load_plan.packaging_warehouse
    IS 'Packaging warehouse';


UPDATE load_plan SET packaging_warehouse = 'W64-PM-VOLVO' WHERE project = 'VOLVO';
UPDATE load_plan SET packaging_warehouse = 'W64-PM-DUCATI' WHERE project = 'DUCATI';
UPDATE load_plan SET packaging_warehouse = 'W64-PM-PERKINS' WHERE project = 'PERKINS';
UPDATE load_plan SET packaging_warehouse = 'W64-PM-CNHI' WHERE project = 'CNHI';




-- ----------- {{{{{{{{{{{{{{{{ [wire_booking] }}}}}}}}}}}}}}}} --------------

-- Table: public.wire_booking

-- DROP TABLE public.wire_booking;

CREATE TABLE public.wire_booking
(
    id integer NOT NULL,
    booking_type character varying(255) COLLATE pg_catalog."default",
    create_id integer,
    create_time timestamp without time zone,
    create_user character varying(255) COLLATE pg_catalog."default",
    dest_wh character varying(255) COLLATE pg_catalog."default",
    dest_wh_loc character varying(255) COLLATE pg_catalog."default",
    project character varying(255) COLLATE pg_catalog."default",
    qty double precision,
    source_wh character varying(255) COLLATE pg_catalog."default",
    source_wh_loc character varying(255) COLLATE pg_catalog."default",
    wire_no character varying(255) COLLATE pg_catalog."default",
    write_id integer,
    write_time timestamp without time zone,
    write_user character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT wire_booking_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.wire_booking
    OWNER to openpg;

-- ----------- {{{{{{{{{{{{{{{{ [wire_config] }}}}}}}}}}}}}}}} --------------

-- DROP TABLE public.wire_config;

CREATE TABLE public.wire_config
(
    id integer NOT NULL,
    bundle_qty integer,
    card_number integer,
    color character varying(255) COLLATE pg_catalog."default",
    create_id integer,
    create_time timestamp without time zone,
    create_user character varying(255) COLLATE pg_catalog."default",
    description character varying(255) COLLATE pg_catalog."default",
    destination_wh character varying(255) COLLATE pg_catalog."default",
    harness_pn character varying(255) COLLATE pg_catalog."default",
    int_seal1 character varying(255) COLLATE pg_catalog."default",
    int_seal2 character varying(255) COLLATE pg_catalog."default",
    int_term1 character varying(255) COLLATE pg_catalog."default",
    int_term2 character varying(255) COLLATE pg_catalog."default",
    internal_part character varying(255) COLLATE pg_catalog."default",
    kanban_qty integer,
    length_ double precision,
    multicore_name character varying(255) COLLATE pg_catalog."default",
    operation_no character varying(255) COLLATE pg_catalog."default",
    product_wire_no character varying(255) COLLATE pg_catalog."default",
    project character varying(255) COLLATE pg_catalog."default",
    spool_pn character varying(255) COLLATE pg_catalog."default",
    stock integer NOT NULL DEFAULT 0,
    strip_length1 double precision,
    strip_length2 double precision,
    type_ character varying(255) COLLATE pg_catalog."default",
    warehouse character varying(255) COLLATE pg_catalog."default",
    wh_location character varying(255) COLLATE pg_catalog."default",
    wire_no character varying(255) COLLATE pg_catalog."default",
    wire_type character varying(255) COLLATE pg_catalog."default",
    write_id integer,
    write_time timestamp without time zone,
    write_user character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT wire_config_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.wire_config
    OWNER to openpg;


-- ----------- {{{{{{{{{{{{{{{{ [wire_stock_loc] }}}}}}}}}}}}}}}} --------------

-- DROP TABLE public.wire_stock_loc;

CREATE TABLE public.wire_stock_loc
(
    id integer NOT NULL,
    create_id integer,
    create_time timestamp without time zone,
    create_user character varying(255) COLLATE pg_catalog."default",
    project character varying(255) COLLATE pg_catalog."default",
    location character varying(255) COLLATE pg_catalog."default",
    warehouse character varying(255) COLLATE pg_catalog."default",
    write_id integer,
    write_time timestamp without time zone,
    write_user character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT wire_stock_loc_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.wire_stock_loc
    OWNER to openpg;

-- SEQUENCE: public.wire_booking_id_seq

-- DROP SEQUENCE public.wire_booking_id_seq;

CREATE SEQUENCE public.wire_booking_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.wire_booking_id_seq
    OWNER TO openpg;

-- SEQUENCE: public.wire_config_id_seq

-- DROP SEQUENCE public.wire_config_id_seq;

CREATE SEQUENCE public.wire_config_id_seq
    INCREMENT 1
    START 450
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.wire_config_id_seq
    OWNER TO openpg;

-- SEQUENCE: public.wire_stock_loc_id_seq

-- DROP SEQUENCE public.wire_stock_loc_id_seq;

CREATE SEQUENCE public.wire_stock_loc_id_seq
    INCREMENT 1
    START 220
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.wire_stock_loc_id_seq
    OWNER TO openpg;

INSERT INTO public.wire_stock_loc(
id, create_id, create_time, create_user, location, write_id, write_time, write_user) VALUES
  (1,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (2,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (3,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (4,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (5,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (6,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (7,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (8,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (9,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (10,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'XXXXXXXXXXXX' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (11,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (12,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (13,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (14,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E1 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (15,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (16,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (17,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (18,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (19,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (20,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (21,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (22,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (23,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (24,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (25,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (26,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (27,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (28,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E2 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (29,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (30,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (31,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (32,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (33,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (34,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (35,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (36,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (37,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (38,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (39,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (40,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (41,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (42,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E3 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (43,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (44,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (45,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (46,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (47,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (48,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (49,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (50,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (51,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (52,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (53,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (54,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (55,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (56,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E4 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (57,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (58,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (59,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (60,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (61,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (62,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (63,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (64,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (65,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (66,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (67,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (68,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (69,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (70,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AA E5 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (71,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (72,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (73,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (74,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (75,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (76,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (77,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (78,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (79,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (80,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (81,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (82,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (83,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (84,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E1 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (85,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (86,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (87,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (88,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (89,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (90,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (91,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (92,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (93,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (94,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (95,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (96,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (97,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (98,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E2 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (99,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (100,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (101,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (102,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (103,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (104,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (105,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (106,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (107,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (108,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (109,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (110,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (111,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (112,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E3 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (113,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (114,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (115,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (116,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (117,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (118,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (119,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (120,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (121,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (122,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (123,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (124,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (125,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (126,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E4 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (127,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (128,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (129,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (130,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (131,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (132,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (133,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (134,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (135,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (136,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (137,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (138,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (139,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (140,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AB E5 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (141,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (142,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (143,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (144,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (145,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (146,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (147,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (148,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (149,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (150,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (151,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (152,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (153,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (154,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E1 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (155,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (156,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (157,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (158,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (159,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (160,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (161,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (162,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (163,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (164,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (165,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (166,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (167,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (168,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E2 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (169,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (170,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (171,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (172,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (173,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (174,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (175,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (176,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (177,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (178,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (179,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (180,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (181,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (182,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E3 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (183,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (184,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (185,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (186,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (187,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (188,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (189,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (190,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (191,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (192,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (193,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (194,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (195,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (196,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E4 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (197,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (198,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (199,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (200,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (201,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P5' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (202,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P6' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (203,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P7' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (204,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P8' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (205,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P9' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (206,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P10' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (207,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P11' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (208,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P12' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (209,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P13' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (210,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AC E5 P14' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (211,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AD E1 P1' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (212,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AD E1 P2' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (213,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AD E1 P3' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama'),
 (214,1, '2020-03-02 22:57:00', 'EZZIOURI Oussama', 'AD E1 P4' , 1, '2020-03-02 22:57:00','EZZIOURI Oussama');
SELECT setval('public.wire_stock_loc_id_seq', 214, true);


INSERT INTO public.wire_config
(
	id, 
	bundle_qty, 
	card_number, 
	color, 
	create_id, 
	create_time, 
	create_user, 
	description, 
	destination_wh, 
	harness_pn, 
	int_seal1, 
	int_seal2, 
	int_term1, 
	int_term2, 
	internal_part, 
	kanban_qty, 
	length_, 
	multicore_name, 
	operation_no, 
	product_wire_no, 
	project, 
	spool_pn, 
	stock, 
	strip_length1, 
	strip_length2, 
	type_, 
	warehouse, 
	wh_location, 
	wire_no, 
	wire_type, 
	write_id, 
	write_time, 
	write_user
)
 VALUES 
(1, 25,  305281,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506500 1.50 BU  30X0.26', 'WPA', '5802535878', '492136421', 'P00102617', '414400100', '414345300', '26G003401', 225, 310, 'TW1', 10, '26G003401-01A', 'CNHI', 'P00490445', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P1', '01A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(2, 25,  305299,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01505500 1.50 BN  30X0.26', 'WPA', '5802535878', '492136421', 'P00102617', '414400100', '414345300', '26G003401', 225, 310, 'TW1', 60, '26G003401-02C', 'CNHI', 'P00172233', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P2', '02C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(3, 25,  305281,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506500 1.50 BU  30X0.26', 'WPA', '5802535883', '492136421', 'P00102617', '414400100', '414345300', '26G003301', 225, 310, 'TW1', 10, '26G003301-01A', 'CNHI', 'P00490445', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P1', '01A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(4, 25,  305299,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01505500 1.50 BN  30X0.26', 'WPA', '5802535883', '492136421', 'P00102617', '414400100', '414345300', '26G003301', 225, 310, 'TW1', 60, '26G003301-02C', 'CNHI', 'P00172233', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P2', '02C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(5, 25,  305305,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01502500 1.50 GY  30X0.26', 'WPA', '5802535878', '492136421', 'P00102617', '414400100', '414345300', '26G003401', 225, 340, 'TW2', 90, '26G003401-03C', 'CNHI', 'P00490449', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P3', '03C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(6, 25,  305307,  'YE/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01503545 1.50 YE-OG  30X0.26', 'WPA', '5802535878', '492136421', 'P00102617', '414400100', '414345300', '26G003401', 225, 340, 'TW2', 100, '26G003401-04A', 'CNHI', 'P00490446', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P4', '04A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(7, 25,  305305,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01502500 1.50 GY  30X0.26', 'WPA', '5802535883', '492136421', 'P00102617', '414400100', '414345300', '26G003301', 225, 340, 'TW2', 90, '26G003301-03C', 'CNHI', 'P00490449', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P3', '03C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(8, 25,  305307,  'YE/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01503545 1.50 YE-OG  30X0.26', 'WPA', '5802535883', '492136421', 'P00102617', '414400100', '414345300', '26G003301', 225, 340, 'TW2', 100, '26G003301-04A', 'CNHI', 'P00490446', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P4', '04A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(9, 25,  305305,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01502500 1.50 GY  30X0.26', 'WPA', '5802535888', '492136421', 'P00102617', '414400100', '414345300', '26G003701', 225, 340, 'TW2', 90, '26G003701-03C', 'CNHI', 'P00490449', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P3', '03C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(10, 25,  305313,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01500500 1.50 WH  30X0.26', 'WPA', '5802535878', '492136421', 'P00102617', '414400100', '414345300', '26G003401', 225, 440, 'TW3', 130, '26G003401-05A', 'CNHI', 'P00158201', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P5', '05A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(11, 25,  305319,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506000 1.50 VT  30X0.26', 'WPA', '5802535878', '492136421', 'P00102617', '414400100', '414345300', '26G003401', 225, 440, 'TW3', 160, '26G003401-06A', 'CNHI', 'P00490447', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P6', '06A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(12, 25,  305313,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01500500 1.50 WH  30X0.26', 'WPA', '5802535883', '492136421', 'P00102617', '414400100', '414345300', '26G003301', 225, 440, 'TW3', 130, '26G003301-05A', 'CNHI', 'P00158201', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P5', '05A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(13, 25,  305319,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506000 1.50 VT  30X0.26', 'WPA', '5802535883', '492136421', 'P00102617', '414400100', '414345300', '26G003301', 225, 440, 'TW3', 160, '26G003301-06A', 'CNHI', 'P00490447', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P6', '06A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(14, 25,  305313,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01500500 1.50 WH  30X0.26', 'WPA', '5802535888', '492136421', 'P00102617', '414400100', '414345300', '26G003701', 225, 440, 'TW3', 130, '26G003701-05A', 'CNHI', 'P00158201', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P5', '05A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(15, 25,  305281,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506500 1.50 BU  30X0.26', 'WPA', '5802535888', '492136421', 'P00102617', '414400100', '414345300', '26G003701', 225, 310, 'TW1', 10, '26G003701-01A', 'CNHI', 'P00490445', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P1', '01A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(16, 25,  305299,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01505500 1.50 BN  30X0.26', 'WPA', '5802535888', '492136421', 'P00102617', '414400100', '414345300', '26G003701', 225, 310, 'TW1', 60, '26G003701-02C', 'CNHI', 'P00172233', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P2', '02C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(17, 25,  305281,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506500 1.50 BU  30X0.26', 'WPA', '5802535894', '492136421', 'P00102617', '414400100', '414345300', '26G003901', 225, 310, 'TW1', 10, '26G003901-01A', 'CNHI', 'P00490445', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P1', '01A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(18, 25,  305299,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01505500 1.50 BN  30X0.26', 'WPA', '5802535894', '492136421', 'P00102617', '414400100', '414345300', '26G003901', 225, 310, 'TW1', 60, '26G003901-02C', 'CNHI', 'P00172233', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P2', '02C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(19, 25,  305301,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506500 1.50 BU  30X0.26', 'WPA', '5802535901', '492136421', 'P00102617', '414400100', '414345300', '26G003001', 225, 400, 'TW1', 70, '26G003001-03A', 'CNHI', 'P00490445', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P7', '03A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(20, 25,  305325,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01505500 1.50 BN  30X0.26', 'WPA', '5802535901', '492136421', 'P00102617', '414400100', '414345300', '26G003001', 225, 400, 'TW1', 190, '26G003001-07A', 'CNHI', 'P00172233', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P8', '07A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(21, 25,  305301,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506500 1.50 BU  30X0.26', 'WPA', '5802535903', '492136421', 'P00102617', '414400100', '414345300', '26G003801', 225, 400, 'TW1', 70, '26G003801-03A', 'CNHI', 'P00490445', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P7', '03A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(22, 25,  305325,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01505500 1.50 BN  30X0.26', 'WPA', '5802535903', '492136421', 'P00102617', '414400100', '414345300', '26G003801', 225, 400, 'TW1', 190, '26G003801-07A', 'CNHI', 'P00172233', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P8', '07A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(23, 25,  305303,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506500 1.50 BU  30X0.26', 'WPA', '5802535910', '492136421', 'P00102617', '414400100', '414345300', '26G002901', 100, 440, 'TW1', 80, '26G002901-03B', 'CNHI', 'P00490445', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P9', '03B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(24, 25,  305307,  'YE/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01503545 1.50 YE-OG  30X0.26', 'WPA', '5802535888', '492136421', 'P00102617', '414400100', '414345300', '26G003701', 225, 340, 'TW2', 100, '26G003701-04A', 'CNHI', 'P00490446', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P4', '04A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(25, 25,  305305,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01502500 1.50 GY  30X0.26', 'WPA', '5802535894', '492136421', 'P00102617', '414400100', '414345300', '26G003901', 225, 340, 'TW2', 90, '26G003901-03C', 'CNHI', 'P00490449', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P3', '03C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(26, 25,  305307,  'YE/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01503545 1.50 YE-OG  30X0.26', 'WPA', '5802535894', '492136421', 'P00102617', '414400100', '414345300', '26G003901', 225, 340, 'TW2', 100, '26G003901-04A', 'CNHI', 'P00490446', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P4', '04A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(27, 25,  305291,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01502500 1.50 GY  30X0.26', 'WPA', '5802535901', '492136421', 'P00102617', '414400100', '414345300', '26G003001', 1, 320, 'TW2', 20, '26G003001-01B', 'CNHI', 'P00490449', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'XXXXXXXXXXXX', '01B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(28, 25,  305315,  'YE/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01503545 1.50 YE-OG  30X0.26', 'WPA', '5802535901', '492136421', 'P00102617', '414400100', '414345300', '26G003001', 75, 320, 'TW2', 140, '26G003001-05B', 'CNHI', 'P00490446', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P11', '05B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(29, 25,  305293,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01502500 1.50 GY  30X0.26', 'WPA', '5802535903', '492136421', 'P00102617', '414400100', '414345300', '26G003801', 150, 350, 'TW2', 30, '26G003801-01C', 'CNHI', 'P00490449', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P12', '01C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(30, 25,  305317,  'YE/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01503545 1.50 YE-OG  30X0.26', 'WPA', '5802535903', '492136421', 'P00102617', '414400100', '414345300', '26G003801', 150, 350, 'TW2', 150, '26G003801-05C', 'CNHI', 'P00490446', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P13', '05C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(31, 25,  305293,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01502500 1.50 GY  30X0.26', 'WPA', '5802535910', '492136421', 'P00102617', '414400100', '414345300', '26G002901', 150, 350, 'TW2', 30, '26G002901-01C', 'CNHI', 'P00490449', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P12', '01C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(32, 25,  305319,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506000 1.50 VT  30X0.26', 'WPA', '5802535888', '492136421', 'P00102617', '414400100', '414345300', '26G003701', 225, 440, 'TW3', 160, '26G003701-06A', 'CNHI', 'P00490447', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P6', '06A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(33, 25,  305313,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01500500 1.50 WH  30X0.26', 'WPA', '5802535894', '492136421', 'P00102617', '414400100', '414345300', '26G003901', 225, 440, 'TW3', 130, '26G003901-05A', 'CNHI', 'P00158201', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P5', '05A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(34, 25,  305319,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506000 1.50 VT  30X0.26', 'WPA', '5802535894', '492136421', 'P00102617', '414400100', '414345300', '26G003901', 225, 440, 'TW3', 160, '26G003901-06A', 'CNHI', 'P00490447', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P6', '06A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(35, 25,  305295,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01500500 1.50 WH  30X0.26', 'WPA', '5802535901', '492136421', 'P00102617', '414400100', '414345300', '26G003001', 225, 465, 'TW3', 40, '26G003001-02A', 'CNHI', 'P00158201', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P14', '02A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(36, 25,  305309,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506000 1.50 VT  30X0.26', 'WPA', '5802535901', '492136421', 'P00102617', '414400100', '414345300', '26G003001', 225, 465, 'TW3', 110, '26G003001-04B', 'CNHI', 'P00490447', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P1', '04B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(37, 25,  305295,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01500500 1.50 WH  30X0.26', 'WPA', '5802535903', '492136421', 'P00102617', '414400100', '414345300', '26G003801', 225, 465, 'TW3', 40, '26G003801-02A', 'CNHI', 'P00158201', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P14', '02A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(38, 25,  305309,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506000 1.50 VT  30X0.26', 'WPA', '5802535903', '492136421', 'P00102617', '414400100', '414345300', '26G003801', 225, 465, 'TW3', 110, '26G003801-04B', 'CNHI', 'P00490447', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P1', '04B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(39, 25,  305297,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01500500 1.50 WH  30X0.26', 'WPA', '5802535910', '492136421', 'P00102617', '414400100', '414345300', '26G002901', 100, 535, 'TW3', 50, '26G002901-02B', 'CNHI', 'P00158201', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P2', '02B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(40, 25,  305311,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506000 1.50 VT  30X0.26', 'WPA', '5802535910', '492136421', 'P00102617', '414400100', '414345300', '26G002901', 100, 535, 'TW3', 120, '26G002901-04C', 'CNHI', 'P00490447', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P3', '04C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(41, 25,  305317,  'YE/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01503545 1.50 YE-OG  30X0.26', 'WPA', '5802535910', '492136421', 'P00102617', '414400100', '414345300', '26G002901', 150, 350, 'TW2', 150, '26G002901-05C', 'CNHI', 'P00490446', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P13', '05C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(42, 25,  305293,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01502500 1.50 GY  30X0.26', 'WPA', '5802535921', '492136421', 'P00102617', '414400100', '414345300', '26G002301', 150, 350, 'TW2', 30, '26G002301-01C', 'CNHI', 'P00490449', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P12', '01C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(43, 25,  305317,  'YE/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01503545 1.50 YE-OG  30X0.26', 'WPA', '5802535921', '492136421', 'P00102617', '414400100', '414345300', '26G002301', 150, 350, 'TW2', 150, '26G002301-05C', 'CNHI', 'P00490446', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P13', '05C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(44, 25,  305293,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01502500 1.50 GY  30X0.26', 'WPA', '5802535966', '492136421', 'P00102617', '414400100', '414345300', '26G004101', 150, 350, 'TW2', 30, '26G004101-01C', 'CNHI', 'P00490449', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P12', '01C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(45, 25,  305317,  'YE/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01503545 1.50 YE-OG  30X0.26', 'WPA', '5802535966', '492136421', 'P00102617', '414400100', '414345300', '26G004101', 150, 350, 'TW2', 150, '26G004101-05C', 'CNHI', 'P00490446', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P13', '05C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(46, 25,  305297,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01500500 1.50 WH  30X0.26', 'WPA', '5802535921', '492136421', 'P00102617', '414400100', '414345300', '26G002301', 100, 535, 'TW3', 50, '26G002301-02B', 'CNHI', 'P00158201', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P2', '02B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(47, 25,  305311,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506000 1.50 VT  30X0.26', 'WPA', '5802535921', '492136421', 'P00102617', '414400100', '414345300', '26G002301', 100, 535, 'TW3', 120, '26G002301-04C', 'CNHI', 'P00490447', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P3', '04C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(48, 25,  305297,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01500500 1.50 WH  30X0.26', 'WPA', '5802535966', '492136421', 'P00102617', '414400100', '414345300', '26G004101', 100, 535, 'TW3', 50, '26G004101-02B', 'CNHI', 'P00158201', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P2', '02B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(49, 25,  305311,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506000 1.50 VT  30X0.26', 'WPA', '5802535966', '492136421', 'P00102617', '414400100', '414345300', '26G004101', 100, 535, 'TW3', 120, '26G004101-04C', 'CNHI', 'P00490447', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P3', '04C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(50, 25,  305329,  'OG/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504565 1.50 OG-BU  30X0.26', 'WPA', '5802535878', '492136421', 'P00102617', '414400100', '414345300', '26G003401', 150, 510, 'TW4', 210, '26G003401-07C', 'CNHI', 'P00490448', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P4', '07C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(51, 25,  305329,  'OG/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504565 1.50 OG-BU  30X0.26', 'WPA', '5802535888', '492136421', 'P00102617', '414400100', '414345300', '26G003701', 150, 510, 'TW4', 210, '26G003701-07C', 'CNHI', 'P00490448', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P4', '07C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(52, 25,  305329,  'OG/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504565 1.50 OG-BU  30X0.26', 'WPA', '5802535894', '492136421', 'P00102617', '414400100', '414345300', '26G003901', 150, 510, 'TW4', 210, '26G003901-07C', 'CNHI', 'P00490448', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P4', '07C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(53, 25,  305335,  'GN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504060 1.50 GN-VT  30X0.26', 'WPA', '5802535878', '492136421', 'P00102617', '414400100', '414345300', '26G003401', 150, 510, 'TW4', 240, '26G003401-08B', 'CNHI', 'P00490450', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P5', '08B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(54, 25,  305335,  'GN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504060 1.50 GN-VT  30X0.26', 'WPA', '5802535888', '492136421', 'P00102617', '414400100', '414345300', '26G003701', 150, 510, 'TW4', 240, '26G003701-08B', 'CNHI', 'P00490450', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P5', '08B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(55, 25,  305327,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01505500 1.50 BN  30X0.26', 'WPA', '5802535910', '492136421', 'P00102617', '414400100', '414345300', '26G002901', 100, 440, 'TW1', 200, '26G002901-07B', 'CNHI', 'P00172233', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P6', '07B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(56, 25,  305303,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506500 1.50 BU  30X0.26', 'WPA', '5802535921', '492136421', 'P00102617', '414400100', '414345300', '26G002301', 100, 440, 'TW1', 80, '26G002301-03B', 'CNHI', 'P00490445', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P9', '03B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(57, 25,  305327,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01505500 1.50 BN  30X0.26', 'WPA', '5802535921', '492136421', 'P00102617', '414400100', '414345300', '26G002301', 100, 440, 'TW1', 200, '26G002301-07B', 'CNHI', 'P00172233', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P6', '07B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(58, 25,  305303,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01506500 1.50 BU  30X0.26', 'WPA', '5802535966', '492136421', 'P00102617', '414400100', '414345300', '26G004101', 100, 440, 'TW1', 80, '26G004101-03B', 'CNHI', 'P00490445', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E1 P9', '03B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(59, 25,  305327,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01505500 1.50 BN  30X0.26', 'WPA', '5802535966', '492136421', 'P00102617', '414400100', '414345300', '26G004101', 100, 440, 'TW1', 200, '26G004101-07B', 'CNHI', 'P00172233', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P6', '07B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(60, 25,  305335,  'GN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504060 1.50 GN-VT  30X0.26', 'WPA', '5802535894', '492136421', 'P00102617', '414400100', '414345300', '26G003901', 150, 510, 'TW4', 240, '26G003901-08B', 'CNHI', 'P00490450', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P5', '08B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(61, 25,  305331,  'OG/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504565 1.50 OG-BU  30X0.26', 'WPA', '5802535883', '492136421', 'P00102617', '414400100', '414345300', '26G003301', 75, 590, 'TW4', 220, '26G003301-07D', 'CNHI', 'P00490448', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P7', '07D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(62, 25,  305337,  'GN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504060 1.50 GN-VT  30X0.26', 'WPA', '5802535883', '492136421', 'P00102617', '414400100', '414345300', '26G003301', 75, 590, 'TW4', 250, '26G003301-08C', 'CNHI', 'P00490450', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P8', '08C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(63, 25,  305321,  'OG/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504565 1.50 OG-BU  30X0.26', 'WPA', '5802535901', '492136421', 'P00102617', '414400100', '414345300', '26G003001', 75, 440, 'TW4', 170, '26G003001-06B', 'CNHI', 'P00490448', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P9', '06B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(64, 25,  305333,  'GN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504060 1.50 GN-VT  30X0.26', 'WPA', '5802535901', '492136421', 'P00102617', '414400100', '414345300', '26G003001', 75, 440, 'TW4', 230, '26G003001-08A', 'CNHI', 'P00490450', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P10', '08A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(65, 25,  305323,  'OG/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504565 1.50 OG-BU  30X0.26', 'WPA', '5802535903', '492136421', 'P00102617', '414400100', '414345300', '26G003801', 150, 490, 'TW4', 180, '26G003801-06C', 'CNHI', 'P00490448', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P11', '06C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(66, 25,  305323,  'OG/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504565 1.50 OG-BU  30X0.26', 'WPA', '5802535910', '492136421', 'P00102617', '414400100', '414345300', '26G002901', 150, 490, 'TW4', 180, '26G002901-06C', 'CNHI', 'P00490448', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P11', '06C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(67, 25,  305323,  'OG/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504565 1.50 OG-BU  30X0.26', 'WPA', '5802535921', '492136421', 'P00102617', '414400100', '414345300', '26G002301', 150, 490, 'TW4', 180, '26G002301-06C', 'CNHI', 'P00490448', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P11', '06C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(68, 25,  305323,  'OG/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504565 1.50 OG-BU  30X0.26', 'WPA', '5802535966', '492136421', 'P00102617', '414400100', '414345300', '26G004101', 150, 490, 'TW4', 180, '26G004101-06C', 'CNHI', 'P00490448', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P11', '06C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(69, 25,  305339,  'GN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504060 1.50 GN-VT  30X0.26', 'WPA', '5802535903', '492136421', 'P00102617', '414400100', '414345300', '26G003801', 150, 490, 'TW4', 260, '26G003801-08D', 'CNHI', 'P00490450', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P12', '08D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(70, 25,  305339,  'GN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504060 1.50 GN-VT  30X0.26', 'WPA', '5802535910', '492136421', 'P00102617', '414400100', '414345300', '26G002901', 150, 490, 'TW4', 260, '26G002901-08D', 'CNHI', 'P00490450', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P12', '08D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(71, 25,  305339,  'GN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504060 1.50 GN-VT  30X0.26', 'WPA', '5802535921', '492136421', 'P00102617', '414400100', '414345300', '26G002301', 150, 490, 'TW4', 260, '26G002301-08D', 'CNHI', 'P00490450', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P12', '08D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(72, 25,  305339,  'GN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B01504060 1.50 GN-VT  30X0.26', 'WPA', '5802535966', '492136421', 'P00102617', '414400100', '414345300', '26G004101', 150, 490, 'TW4', 260, '26G004101-08D', 'CNHI', 'P00490450', 0, 4.5, 5, 'FLR2X-B 1.50', '3CV', 'AA E2 P12', '08D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(73, 25,  305341,  'GY/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502565 0.50 GY-BU  19X0.19', 'Assembly', '5802535878', 'P00004495', '499161806', '414298726', 'P00108579', '26G003401', 150, 770, '0', 270, '26G003401-09A', 'CNHI', 'P00134443', 0, 4, 4.1, 'FLR2X-A 0.50', '3CV', 'AA E2 P13', '09A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(74, 25,  305341,  'GY/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502565 0.50 GY-BU  19X0.19', 'Assembly', '5802535888', 'P00004495', '499161806', '414298726', 'P00108579', '26G003701', 150, 770, '0', 270, '26G003701-09A', 'CNHI', 'P00134443', 0, 4, 4.1, 'FLR2X-A 0.50', '3CV', 'AA E2 P13', '09A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(75, 25,  305341,  'GY/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502565 0.50 GY-BU  19X0.19', 'Assembly', '5802535894', 'P00004495', '499161806', '414298726', 'P00108579', '26G003901', 150, 770, '0', 270, '26G003901-09A', 'CNHI', 'P00134443', 0, 4, 4.1, 'FLR2X-A 0.50', '3CV', 'AA E2 P13', '09A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(76, 25,  305343,  'GY/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502565 0.50 GY-BU  19X0.19', 'Assembly', '5802535883', 'P00004495', '499161806', '414298726', 'P00108579', '26G003301', 75, 745, '0', 280, '26G003301-09B', 'CNHI', 'P00134443', 0, 4, 4.1, 'FLR2X-A 0.50', '3CV', 'AA E2 P14', '09B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(77, 25,  305503,  'VT/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756005 0.75 VT-WH  19X0.23', 'WPA', '5802535878', 'P00004495', 'P00102616', '414298726', '414345400', '26G003401', 75, 610, 'TW5', 1080, '26G003401-22D', 'CNHI', 'P00425828', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P1', '22D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(78, 25,  305513,  'OG/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754505 0.75 OG-WH  19X0.23', 'WPA', '5802535878', 'P00004495', 'P00102616', '414298726', '414345400', '26G003401', 75, 610, 'TW5', 1130, '26G003401-23D', 'CNHI', 'P00318149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P2', '23D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(79, 25,  305503,  'VT/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756005 0.75 VT-WH  19X0.23', 'WPA', '5802535888', 'P00004495', 'P00102616', '414298726', '414345400', '26G003701', 75, 610, 'TW5', 1080, '26G003701-22D', 'CNHI', 'P00425828', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P1', '22D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(80, 25,  305513,  'OG/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754505 0.75 OG-WH  19X0.23', 'WPA', '5802535888', 'P00004495', 'P00102616', '414298726', '414345400', '26G003701', 75, 610, 'TW5', 1130, '26G003701-23D', 'CNHI', 'P00318149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P2', '23D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(81, 25,  305501,  'VT/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756005 0.75 VT-WH  19X0.23', 'WPA', '5802535883', 'P00004495', 'P00102616', '414298726', '414345400', '26G003301', 75, 570, 'TW5', 1070, '26G003301-22C', 'CNHI', 'P00425828', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P3', '22C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(82, 25,  305511,  'OG/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754505 0.75 OG-WH  19X0.23', 'WPA', '5802535883', 'P00004495', 'P00102616', '414298726', '414345400', '26G003301', 75, 570, 'TW5', 1120, '26G003301-23C', 'CNHI', 'P00318149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P4', '23C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(83, 25,  305505,  'VT/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756005 0.75 VT-WH  19X0.23', 'WPA', '5802535894', 'P00004495', 'P00102616', '414298726', '414345400', '26G003901', 75, 645, 'TW5', 1090, '26G003901-22E', 'CNHI', 'P00425828', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P5', '22E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(84, 25,  305515,  'OG/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754505 0.75 OG-WH  19X0.23', 'WPA', '5802535894', 'P00004495', 'P00102616', '414298726', '414345400', '26G003901', 75, 645, 'TW5', 1140, '26G003901-23E', 'CNHI', 'P00318149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P6', '23E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(85, 25,  305347,  'OG/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754540 0.75 OG-GN  19X0.23', 'WPA', '5802535901', 'P00004495', 'P00004495', '414298726', '412048666', '26G003001', 75, 800, 'TW5', 300, '26G003001-09D', 'CNHI', 'P00144508', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E3 P7', '09D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(86, 25,  305355,  'BU/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756545 0.75 BU-OG  19X0.23', 'WPA', '5802535901', 'P00004495', 'P00004495', '414298726', '412048666', '26G003001', 75, 800, 'TW5', 340, '26G003001-10B', 'CNHI', 'P00425830', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E3 P8', '10B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(87, 25,  305361,  'GN/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504045 0.50 GN-OG  19X0.19', 'WPA', '5802535878', 'P00004495', 'P00102616', '414298726', '414345400', '26G003401', 150, 365, 'TW10', 370, '26G003401-10E', 'CNHI', 'P00425845', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P9', '10E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(88, 25,  305361,  'GN/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504045 0.50 GN-OG  19X0.19', 'WPA', '5802535883', 'P00004495', 'P00102616', '414298726', '414345400', '26G003301', 150, 365, 'TW10', 370, '26G003301-10E', 'CNHI', 'P00425845', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P9', '10E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(89, 25,  305363,  'GN/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504045 0.50 GN-OG  19X0.19', 'WPA', '5802535888', 'P00004495', 'P00102616', '414298726', '414345400', '26G003701', 75, 405, 'TW9', 380, '26G003701-10F', 'CNHI', 'P00425845', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P10', '10F', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(90, 25,  305363,  'GN/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504045 0.50 GN-OG  19X0.19', 'WPA', '5802535894', 'P00004495', 'P00102616', '414298726', '414345400', '26G003901', 75, 405, 'TW9', 380, '26G003901-10F', 'CNHI', 'P00425845', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P10', '10F', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(91, 25,  305365,  'YE/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503565 0.50 YE-BU  19X0.19', 'WPA', '5802535878', 'P00004495', 'P00102616', '414298726', '414345400', '26G003401', 150, 365, 'TW10', 390, '26G003401-11A', 'CNHI', 'P00173225', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P11', '11A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(92, 25,  305365,  'YE/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503565 0.50 YE-BU  19X0.19', 'WPA', '5802535883', 'P00004495', 'P00102616', '414298726', '414345400', '26G003301', 150, 365, 'TW10', 390, '26G003301-11A', 'CNHI', 'P00173225', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P11', '11A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(93, 25,  305367,  'YE/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503565 0.50 YE-BU  19X0.19', 'WPA', '5802535888', 'P00004495', 'P00102616', '414298726', '414345400', '26G003701', 75, 405, 'TW9', 400, '26G003701-11B', 'CNHI', 'P00173225', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P12', '11B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(94, 25,  305367,  'YE/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503565 0.50 YE-BU  19X0.19', 'WPA', '5802535894', 'P00004495', 'P00102616', '414298726', '414345400', '26G003901', 75, 405, 'TW9', 400, '26G003901-11B', 'CNHI', 'P00173225', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P12', '11B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(95, 25,  305523,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750545 0.75 WH-OG  19X0.23', 'WPA', '5802535878', 'P00004495', 'P00004495', '414298726', '412048666', '26G003401', 150, 390, 'TW6', 1180, '26G003401-24D', 'CNHI', 'P00425827', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E3 P13', '24D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(96, 25,  305535,  'GY/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00752545 0.75 GY-OG  19X0.23', 'WPA', '5802535878', 'P00004495', 'P00004495', '414298726', '412048666', '26G003401', 150, 390, 'TW6', 1240, '26G003401-25D', 'CNHI', 'P00145777', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E3 P14', '25D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(97, 25,  305523,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750545 0.75 WH-OG  19X0.23', 'WPA', '5802535883', 'P00004495', 'P00004495', '414298726', '412048666', '26G003301', 150, 390, 'TW6', 1180, '26G003301-24D', 'CNHI', 'P00425827', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E3 P13', '24D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(98, 25,  305535,  'GY/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00752545 0.75 GY-OG  19X0.23', 'WPA', '5802535883', 'P00004495', 'P00004495', '414298726', '412048666', '26G003301', 150, 390, 'TW6', 1240, '26G003301-25D', 'CNHI', 'P00145777', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E3 P14', '25D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(99, 25,  305525,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750545 0.75 WH-OG  19X0.23', 'WPA', '5802535888', 'P00004495', 'P00004495', '414298726', '412048666', '26G003701', 100, 700, 'TW6', 1190, '26G003701-24E', 'CNHI', 'P00425827', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P1', '24E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(100, 25,  305537,  'GY/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00752545 0.75 GY-OG  19X0.23', 'WPA', '5802535888', 'P00004495', 'P00004495', '414298726', '412048666', '26G003701', 100, 700, 'TW6', 1250, '26G003701-25E', 'CNHI', 'P00145777', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E3 P2', '25E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(101, 25,  305527,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750545 0.75 WH-OG  19X0.23', 'WPA', '5802535894', 'P00004495', 'P00004495', '414298726', '412048666', '26G003901', 75, 775, 'TW6', 1200, '26G003901-24F', 'CNHI', 'P00425827', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E3 P3', '24F', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(102, 25,  305539,  'GY/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00752545 0.75 GY-OG  19X0.23', 'WPA', '5802535894', 'P00004495', 'P00004495', '414298726', '412048666', '26G003901', 75, 775, 'TW6', 1260, '26G003901-25F', 'CNHI', 'P00145777', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E3 P4', '25F', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(103, 25,  305369,  'WH/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750555 0.75 WH-BN  19X0.23', 'WPA', '5802535901', 'P00004495', '492145600', '414298726', 'P00024412', '26G003001', 75, 965, 'TW6', 410, '26G003001-11C', 'CNHI', 'P00174149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P5', '11C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(104, 25,  305375,  'RD',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00755000 0.75 RD  19X0.23', 'WPA', '5802535901', 'P00004495', '492145600', '414298726', 'P00024412', '26G003001', 75, 965, 'TW6', 440, '26G003001-12A', 'CNHI', 'P00145779', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P6', '12A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(105, 25,  305381,  'GY/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502540 0.50 GY-GN  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414345400', '26G003401', 150, 355, '0', 470, '26G003401-12D', 'CNHI', 'P00174329', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P7', '12D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(106, 25,  305381,  'GY/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502540 0.50 GY-GN  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414345400', '26G003301', 150, 355, '0', 470, '26G003301-12D', 'CNHI', 'P00174329', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P7', '12D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(107, 25,  305383,  'GY/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502540 0.50 GY-GN  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414345400', '26G003701', 75, 395, '0', 480, '26G003701-12E', 'CNHI', 'P00174329', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P9', '12E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(108, 25,  305383,  'GY/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502540 0.50 GY-GN  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414345400', '26G003901', 75, 395, '0', 480, '26G003901-12E', 'CNHI', 'P00174329', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P9', '12E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(109, 25,  305385,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'WPA', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 75, 815, 'TW9', 490, '26G003301-13A', 'CNHI', 'P00134451', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P11', '13A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(110, 25,  305387,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'WPA', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 75, 875, 'TW9', 500, '26G003401-13B', 'CNHI', 'P00134451', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P12', '13B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(111, 25,  305661,  'WH/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750555 0.75 WH-BN  19X0.23', 'WPA', '5802535878', 'P00004495', '492145600', '414298726', 'P00024412', '26G003401', 150, 1020, 'TW8', 1870, '26G003401-38C', 'CNHI', 'P00174149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P13', '38C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(112, 25,  305669,  'RD',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00755000 0.75 RD  19X0.23', 'WPA', '5802535878', 'P00004495', '492145600', '414298726', 'P00024412', '26G003401', 150, 1020, 'TW8', 1910, '26G003401-39B', 'CNHI', 'P00145779', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P14', '39B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(113, 25,  305575,  'OG/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754540 0.75 OG-GN  19X0.23', 'WPA', '5802535878', 'P00004495', 'P00004495', '414298726', '412048666', '26G003401', 225, 880, 'TW7', 1440, '26G003401-29B', 'CNHI', 'P00144508', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P1', '29B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(114, 25,  305587,  'BU/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756545 0.75 BU-OG  19X0.23', 'WPA', '5802535878', 'P00004495', 'P00004495', '414298726', '412048666', '26G003401', 225, 880, 'TW7', 1500, '26G003401-30C', 'CNHI', 'P00425830', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P2', '30C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(115, 25,  305575,  'OG/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754540 0.75 OG-GN  19X0.23', 'WPA', '5802535883', 'P00004495', 'P00004495', '414298726', '412048666', '26G003301', 225, 880, 'TW7', 1440, '26G003301-29B', 'CNHI', 'P00144508', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P1', '29B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(116, 25,  305587,  'BU/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756545 0.75 BU-OG  19X0.23', 'WPA', '5802535883', 'P00004495', 'P00004495', '414298726', '412048666', '26G003301', 225, 880, 'TW7', 1500, '26G003301-30C', 'CNHI', 'P00425830', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P2', '30C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(117, 25,  305575,  'OG/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754540 0.75 OG-GN  19X0.23', 'WPA', '5802535894', 'P00004495', 'P00004495', '414298726', '412048666', '26G003901', 225, 880, 'TW7', 1440, '26G003901-29B', 'CNHI', 'P00144508', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P1', '29B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(118, 25,  305587,  'BU/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756545 0.75 BU-OG  19X0.23', 'WPA', '5802535894', 'P00004495', 'P00004495', '414298726', '412048666', '26G003901', 225, 880, 'TW7', 1500, '26G003901-30C', 'CNHI', 'P00425830', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P2', '30C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(119, 25,  305573,  'OG/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754540 0.75 OG-GN  19X0.23', 'WPA', '5802535888', 'P00004495', 'P00004495', '414298726', '412048666', '26G003701', 100, 780, 'TW7', 1430, '26G003701-29A', 'CNHI', 'P00144508', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P3', '29A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(120, 25,  305585,  'BU/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756545 0.75 BU-OG  19X0.23', 'WPA', '5802535888', 'P00004495', 'P00004495', '414298726', '412048666', '26G003701', 100, 780, 'TW7', 1490, '26G003701-30B', 'CNHI', 'P00425830', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P4', '30B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(121, 25,  305393,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750545 0.75 WH-OG  19X0.23', 'WPA', '5802535901', 'P00004495', 'P00004495', '414298726', '412048666', '26G003001', 75, 775, 'TW7', 530, '26G003001-13E', 'CNHI', 'P00425827', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P5', '13E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(122, 25,  305397,  'GY/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00752545 0.75 GY-OG  19X0.23', 'WPA', '5802535901', 'P00004495', 'P00004495', '414298726', '412048666', '26G003001', 75, 775, 'TW7', 550, '26G003001-14B', 'CNHI', 'P00145777', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P6', '14B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(123, 25,  305399,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'WPA', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 75, 815, 'TW9', 560, '26G003301-14C', 'CNHI', 'P00169734', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E4 P7', '14C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(124, 25,  305401,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'WPA', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 75, 875, 'TW9', 570, '26G003401-14D', 'CNHI', 'P00169734', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E4 P8', '14D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(125, 25,  305661,  'WH/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750555 0.75 WH-BN  19X0.23', 'WPA', '5802535883', 'P00004495', '492145600', '414298726', 'P00024412', '26G003301', 150, 1020, 'TW8', 1870, '26G003301-38C', 'CNHI', 'P00174149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P13', '38C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(126, 25,  305669,  'RD',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00755000 0.75 RD  19X0.23', 'WPA', '5802535883', 'P00004495', '492145600', '414298726', 'P00024412', '26G003301', 150, 1020, 'TW8', 1910, '26G003301-39B', 'CNHI', 'P00145779', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P14', '39B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(127, 25,  305405,  'GY/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502565 0.50 GY-BU  19X0.19', 'Assembly', '5802535903', 'P00004495', '499161806', '414298726', 'P00108579', '26G003801', 150, 620, '0', 590, '26G003801-15A', 'CNHI', 'P00134443', 0, 4, 4.1, 'FLR2X-A 0.50', '3CV', 'AA E4 P9', '15A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(128, 25,  305407,  'GY/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502565 0.50 GY-BU  19X0.19', 'Assembly', '5802535901', 'P00004495', '499161806', '414298726', 'P00108579', '26G003001', 75, 735, '0', 600, '26G003001-15B', 'CNHI', 'P00134443', 0, 4, 4.1, 'FLR2X-A 0.50', '3CV', 'AA E4 P10', '15B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(129, 25,  305409,  'GY/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502565 0.50 GY-BU  19X0.19', 'Assembly', '5802535921', 'P00004495', '499161806', '414298726', 'P00108579', '26G002301', 100, 760, '0', 610, '26G002301-15C', 'CNHI', 'P00134443', 0, 4, 4.1, 'FLR2X-A 0.50', '3CV', 'AA E4 P11', '15C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(130, 25,  305409,  'GY/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502565 0.50 GY-BU  19X0.19', 'Assembly', '5802535966', 'P00004495', '499161806', '414298726', 'P00108579', '26G004101', 100, 760, '0', 610, '26G004101-15C', 'CNHI', 'P00134443', 0, 4, 4.1, 'FLR2X-A 0.50', '3CV', 'AA E4 P11', '15C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(131, 25,  305409,  'GY/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502565 0.50 GY-BU  19X0.19', 'Assembly', '5802535910', 'P00004495', '499161806', '414298726', 'P00108579', '26G002901', 100, 760, '0', 610, '26G002901-15C', 'CNHI', 'P00134443', 0, 4, 4.1, 'FLR2X-A 0.50', '3CV', 'AA E4 P11', '15C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(132, 25,  305411,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502500 0.50 GY  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 100, 970, '0', 620, '26G003701-15D', 'CNHI', 'P00145134', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E4 P12', '15D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(133, 25,  305413,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502500 0.50 GY  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 75, 1035, '0', 630, '26G003901-15E', 'CNHI', 'P00145134', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E4 P13', '15E', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(134, 25,  305415,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502500 0.50 GY  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 150, 1490, '0', 640, '26G003301-15F', 'CNHI', 'P00145134', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E4 P14', '15F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(135, 25,  305415,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502500 0.50 GY  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 150, 1490, '0', 640, '26G003401-15F', 'CNHI', 'P00145134', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E4 P14', '15F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(136, 25,  305417,  'VT/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506040 0.50 VT-GN  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 100, 970, '0', 650, '26G003701-16A', 'CNHI', 'P00294250', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E5 P1', '16A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(137, 25,  305419,  'VT/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506040 0.50 VT-GN  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 75, 1030, '0', 660, '26G003901-16B', 'CNHI', 'P00294250', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E5 P2', '16B', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(138, 25,  305421,  'VT/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506040 0.50 VT-GN  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 150, 1495, '0', 670, '26G003301-16C', 'CNHI', 'P00294250', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E5 P3', '16C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(139, 25,  305421,  'VT/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506040 0.50 VT-GN  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 150, 1495, '0', 670, '26G003401-16C', 'CNHI', 'P00294250', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E5 P3', '16C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(140, 25,  305423,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500545 0.50 WH-OG  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003801', 150, 710, '0', 680, '26G003801-16D', 'CNHI', 'P00425841', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P4', '16D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(141, 25,  305425,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500545 0.50 WH-OG  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003001', 75, 735, '0', 690, '26G003001-16E', 'CNHI', 'P00425841', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P5', '16E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(142, 25,  305427,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500545 0.50 WH-OG  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002901', 100, 795, '0', 700, '26G002901-16F', 'CNHI', 'P00425841', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P6', '16F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(143, 25,  305429,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500545 0.50 WH-OG  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002301', 100, 830, '0', 710, '26G002301-16G', 'CNHI', 'P00425841', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P7', '16G', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(144, 25,  305429,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500545 0.50 WH-OG  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G004101', 100, 830, '0', 710, '26G004101-16G', 'CNHI', 'P00425841', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P7', '16G', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(145, 25,  305431,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 100, 970, '0', 720, '26G003701-17A', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E5 P8', '17A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(146, 25,  305433,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 75, 1025, '0', 730, '26G003901-17B', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E5 P9', '17B', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(147, 25,  305435,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 150, 1500, '0', 740, '26G003301-17C', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E5 P10', '17C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(148, 25,  305435,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 150, 1500, '0', 740, '26G003401-17C', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E5 P10', '17C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(149, 25,  305437,  'BU/GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506525 0.50 BU-GY  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003801', 150, 710, '0', 750, '26G003801-17D', 'CNHI', 'P00173209', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P11', '17D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(150, 25,  305439,  'BU/GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506525 0.50 BU-GY  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003001', 75, 735, '0', 760, '26G003001-17E', 'CNHI', 'P00173209', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P12', '17E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(151, 25,  305441,  'BU/GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506525 0.50 BU-GY  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002901', 100, 795, '0', 770, '26G002901-17F', 'CNHI', 'P00173209', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P13', '17F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(152, 25,  305443,  'BU/GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506525 0.50 BU-GY  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002301', 100, 830, '0', 780, '26G002301-17G', 'CNHI', 'P00173209', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P14', '17G', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(153, 25,  305443,  'BU/GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506525 0.50 BU-GY  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G004101', 100, 830, '0', 780, '26G004101-17G', 'CNHI', 'P00173209', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AA E5 P14', '17G', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(154, 25,  305445,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 100, 880, '0', 790, '26G003701-18A', 'CNHI', 'P00141984', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P1', '18A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(155, 25,  305447,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 75, 930, '0', 800, '26G003301-18B', 'CNHI', 'P00141984', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P2', '18B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(156, 25,  305449,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 150, 970, '0', 810, '26G003401-18C', 'CNHI', 'P00141984', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P3', '18C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(157, 25,  305449,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 150, 970, '0', 810, '26G003901-18C', 'CNHI', 'P00141984', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P3', '18C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(158, 25,  305451,  'BN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505505 0.50 BN-WH  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 475, '0', 820, '26G002901-18D', 'CNHI', 'P00173207', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P4', '18D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(159, 25,  305453,  'BN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505505 0.50 BN-WH  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 525, '0', 830, '26G002301-18E', 'CNHI', 'P00173207', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P5', '18E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(160, 25,  305453,  'BN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505505 0.50 BN-WH  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 525, '0', 830, '26G004101-18E', 'CNHI', 'P00173207', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P5', '18E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(161, 25,  305455,  'VT/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756005 0.75 VT-WH  19X0.23', 'WPA', '5802535901', 'P00004495', 'P00102616', '414298726', '414345400', '26G003001', 75, 510, 'TW9', 840, '26G003001-18F', 'CNHI', 'P00425828', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E1 P6', '18F', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(162, 25,  305457,  'VT/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756005 0.75 VT-WH  19X0.23', 'WPA', '5802535903', 'P00004495', 'P00102616', '414298726', '414345400', '26G003801', 150, 580, 'TW9', 850, '26G003801-18G', 'CNHI', 'P00425828', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E1 P7', '18G', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(163, 25,  305459,  'PK',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00501000 0.50 PK  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 475, '0', 860, '26G002901-19A', 'CNHI', 'P00144159', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P8', '19A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(164, 25,  305461,  'PK',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00501000 0.50 PK  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 525, '0', 870, '26G002301-19B', 'CNHI', 'P00144159', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P9', '19B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(165, 25,  305461,  'PK',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00501000 0.50 PK  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 525, '0', 870, '26G004101-19B', 'CNHI', 'P00144159', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P9', '19B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(166, 25,  305463,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503500 0.50 YE  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 100, 880, '0', 880, '26G003701-19C', 'CNHI', 'P00161179', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P10', '19C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(167, 25,  305465,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503500 0.50 YE  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 75, 930, '0', 890, '26G003301-19D', 'CNHI', 'P00161179', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P11', '19D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(168, 25,  305467,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503500 0.50 YE  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 150, 970, '0', 900, '26G003401-19E', 'CNHI', 'P00161179', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P12', '19E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(169, 25,  305467,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503500 0.50 YE  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 150, 970, '0', 900, '26G003901-19E', 'CNHI', 'P00161179', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E1 P12', '19E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(170, 25,  305469,  'OG/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754505 0.75 OG-WH  19X0.23', 'WPA', '5802535901', 'P00004495', 'P00102616', '414298726', '414345400', '26G003001', 75, 510, 'TW9', 910, '26G003001-19F', 'CNHI', 'P00318149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E1 P13', '19F', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(171, 25,  305471,  'OG/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754505 0.75 OG-WH  19X0.23', 'WPA', '5802535903', 'P00004495', 'P00102616', '414298726', '414345400', '26G003801', 150, 580, 'TW9', 920, '26G003801-19G', 'CNHI', 'P00318149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E1 P14', '19G', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(172, 25,  305473,  'PK',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00501000 0.50 PK  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 75, 440, '0', 930, '26G003301-20A', 'CNHI', 'P00144159', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E2 P1', '20A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(173, 25,  305475,  'PK',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00501000 0.50 PK  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 75, 520, '0', 940, '26G003401-20B', 'CNHI', 'P00144159', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E2 P2', '20B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(174, 25,  305477,  'PK',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00501000 0.50 PK  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 75, 685, '0', 950, '26G003701-20C', 'CNHI', 'P00144159', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E2 P3', '20C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(175, 25,  305477,  'PK',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00501000 0.50 PK  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 75, 685, '0', 950, '26G003901-20C', 'CNHI', 'P00144159', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E2 P3', '20C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(176, 25,  305479,  'BN/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505565 0.50 BN-BU  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003801', 150, 710, '0', 960, '26G003801-20D', 'CNHI', 'P00153336', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P4', '20D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(177, 25,  305481,  'BN/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505565 0.50 BN-BU  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003001', 75, 735, '0', 970, '26G003001-20E', 'CNHI', 'P00153336', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P5', '20E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(178, 25,  305483,  'BN/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505565 0.50 BN-BU  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002901', 100, 795, '0', 980, '26G002901-20F', 'CNHI', 'P00153336', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P6', '20F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(179, 25,  305485,  'BN/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505565 0.50 BN-BU  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002301', 100, 830, '0', 990, '26G002301-20G', 'CNHI', 'P00153336', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P7', '20G', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(180, 25,  305485,  'BN/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505565 0.50 BN-BU  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G004101', 100, 830, '0', 990, '26G004101-20G', 'CNHI', 'P00153336', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P7', '20G', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(181, 25,  305487,  'BU/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506505 0.50 BU-WH  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003801', 150, 710, '0', 1000, '26G003801-21A', 'CNHI', 'P00134388', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P8', '21A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(182, 25,  305489,  'BU/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506505 0.50 BU-WH  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003001', 75, 750, '0', 1010, '26G003001-21B', 'CNHI', 'P00134388', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P9', '21B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(183, 25,  305489,  'BU/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506505 0.50 BU-WH  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002901', 75, 750, '0', 1010, '26G002901-21B', 'CNHI', 'P00134388', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P9', '21B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(184, 25,  305489,  'BU/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506505 0.50 BU-WH  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002301', 75, 750, '0', 1010, '26G002301-21B', 'CNHI', 'P00134388', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P9', '21B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(185, 25,  305489,  'BU/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506505 0.50 BU-WH  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G004101', 75, 750, '0', 1010, '26G004101-21B', 'CNHI', 'P00134388', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P9', '21B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(186, 25,  305491,  'BN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505505 0.50 BN-WH  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 75, 440, '0', 1020, '26G003301-21C', 'CNHI', 'P00173207', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E2 P10', '21C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(187, 25,  305493,  'BN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505505 0.50 BN-WH  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 75, 520, '0', 1030, '26G003401-21D', 'CNHI', 'P00173207', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E2 P11', '21D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(188, 25,  305495,  'BN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505505 0.50 BN-WH  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 75, 685, '0', 1040, '26G003701-21E', 'CNHI', 'P00173207', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E2 P12', '21E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(189, 25,  305495,  'BN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505505 0.50 BN-WH  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 75, 685, '0', 1040, '26G003901-21E', 'CNHI', 'P00173207', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E2 P12', '21E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(190, 25,  305497,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003801', 150, 710, '0', 1050, '26G003801-22A', 'CNHI', 'P00169734', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P13', '22A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(191, 25,  305499,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003001', 75, 750, '0', 1060, '26G003001-22B', 'CNHI', 'P00169734', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P14', '22B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(192, 25,  305499,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002901', 75, 750, '0', 1060, '26G002901-22B', 'CNHI', 'P00169734', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P14', '22B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(193, 25,  305499,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002301', 75, 750, '0', 1060, '26G002301-22B', 'CNHI', 'P00169734', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P14', '22B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(194, 25,  305499,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G004101', 75, 750, '0', 1060, '26G004101-22B', 'CNHI', 'P00169734', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E2 P14', '22B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(195, 25,  305345,  'OG/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754540 0.75 OG-GN  19X0.23', 'WPA', '5802535903', 'P00004495', 'P00004495', '414298726', '412048666', '26G003801', 150, 720, 'TW5', 290, '26G003801-09C', 'CNHI', 'P00144508', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E3 P1', '09C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(196, 25,  305353,  'BU/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756545 0.75 BU-OG  19X0.23', 'WPA', '5802535903', 'P00004495', 'P00004495', '414298726', '412048666', '26G003801', 150, 720, 'TW5', 330, '26G003801-10A', 'CNHI', 'P00425830', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E3 P2', '10A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(197, 25,  305349,  'OG/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754540 0.75 OG-GN  19X0.23', 'WPA', '5802535910', 'P00004495', 'P00004495', '414298726', '412048666', '26G002901', 100, 825, 'TW5', 310, '26G002901-09E', 'CNHI', 'P00144508', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E3 P3', '09E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(198, 25,  305357,  'BU/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756545 0.75 BU-OG  19X0.23', 'WPA', '5802535910', 'P00004495', 'P00004495', '414298726', '412048666', '26G002901', 100, 825, 'TW5', 350, '26G002901-10C', 'CNHI', 'P00425830', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E3 P4', '10C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(199, 25,  305507,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003801', 150, 710, '0', 1100, '26G003801-23A', 'CNHI', 'P00134451', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P5', '23A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(200, 25,  305509,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003001', 75, 750, '0', 1110, '26G003001-23B', 'CNHI', 'P00134451', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P6', '23B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(201, 25,  305509,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002901', 75, 750, '0', 1110, '26G002901-23B', 'CNHI', 'P00134451', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P6', '23B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(202, 25,  305509,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G002301', 75, 750, '0', 1110, '26G002301-23B', 'CNHI', 'P00134451', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P6', '23B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(203, 25,  305509,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G004101', 75, 750, '0', 1110, '26G004101-23B', 'CNHI', 'P00134451', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P6', '23B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(204, 25,  305351,  'OG/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754540 0.75 OG-GN  19X0.23', 'WPA', '5802535921', 'P00004495', 'P00004495', '414298726', '412048666', '26G002301', 100, 865, 'TW5', 320, '26G002301-09F', 'CNHI', 'P00144508', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E3 P7', '09F', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(205, 25,  305359,  'BU/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756545 0.75 BU-OG  19X0.23', 'WPA', '5802535921', 'P00004495', 'P00004495', '414298726', '412048666', '26G002301', 100, 865, 'TW5', 360, '26G002301-10D', 'CNHI', 'P00425830', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E3 P8', '10D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(206, 25,  305351,  'OG/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754540 0.75 OG-GN  19X0.23', 'WPA', '5802535966', 'P00004495', 'P00004495', '414298726', '412048666', '26G004101', 100, 865, 'TW5', 320, '26G004101-09F', 'CNHI', 'P00144508', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E3 P7', '09F', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(207, 25,  305359,  'BU/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756545 0.75 BU-OG  19X0.23', 'WPA', '5802535966', 'P00004495', 'P00004495', '414298726', '412048666', '26G004101', 100, 865, 'TW5', 360, '26G004101-10D', 'CNHI', 'P00425830', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E3 P8', '10D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(208, 25,  305517,  'YE/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503555 0.50 YE-BN  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', '414298726', '26G003001', 75, 150, '0', 1150, '26G003001-24A', 'CNHI', 'P00174322', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P9', '24A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(209, 25,  305519,  'YE/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503555 0.50 YE-BN  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', '414298726', '26G003801', 150, 210, '0', 1160, '26G003801-24B', 'CNHI', 'P00174322', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P10', '24B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(210, 25,  305519,  'YE/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503555 0.50 YE-BN  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', '414298726', '26G002901', 150, 210, '0', 1160, '26G002901-24B', 'CNHI', 'P00174322', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P10', '24B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(211, 25,  305521,  'WH/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500540 0.50 WH-GN  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 570, '0', 1170, '26G002301-24C', 'CNHI', 'P00186192', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P11', '24C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(212, 25,  305371,  'WH/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750555 0.75 WH-BN  19X0.23', 'WPA', '5802535903', 'P00004495', '492145600', '414298726', 'P00024412', '26G003801', 150, 1015, 'TW6', 420, '26G003801-11D', 'CNHI', 'P00174149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E3 P12', '11D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(213, 25,  305377,  'RD',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00755000 0.75 RD  19X0.23', 'WPA', '5802535903', 'P00004495', '492145600', '414298726', 'P00024412', '26G003801', 150, 1015, 'TW6', 450, '26G003801-12B', 'CNHI', 'P00145779', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E3 P13', '12B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(214, 25,  305371,  'WH/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750555 0.75 WH-BN  19X0.23', 'WPA', '5802535910', 'P00004495', '492145600', '414298726', 'P00024412', '26G002901', 150, 1015, 'TW6', 420, '26G002901-11D', 'CNHI', 'P00174149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E3 P12', '11D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(215, 25,  305377,  'RD',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00755000 0.75 RD  19X0.23', 'WPA', '5802535910', 'P00004495', '492145600', '414298726', 'P00024412', '26G002901', 150, 1015, 'TW6', 450, '26G002901-12B', 'CNHI', 'P00145779', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E3 P13', '12B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(216, 25,  305529,  'BU/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506555 0.50 BU-BN  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 480, '0', 1210, '26G002301-25A', 'CNHI', 'P00134389', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P14', '25A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(217, 25,  305529,  'BU/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506555 0.50 BU-BN  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 480, '0', 1210, '26G004101-25A', 'CNHI', 'P00134389', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P14', '25A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(218, 25,  305531,  'BU/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506555 0.50 BU-BN  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 225, 510, '0', 1220, '26G003001-25B', 'CNHI', 'P00134389', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P1', '25B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(219, 25,  305531,  'BU/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506555 0.50 BU-BN  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 225, 510, '0', 1220, '26G003801-25B', 'CNHI', 'P00134389', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P1', '25B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(220, 25,  305533,  'BU/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506555 0.50 BU-BN  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 590, '0', 1230, '26G002901-25C', 'CNHI', 'P00134389', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P2', '25C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(221, 25,  305373,  'WH/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750555 0.75 WH-BN  19X0.23', 'WPA', '5802535921', 'P00004495', '492145600', '414298726', 'P00024412', '26G002301', 100, 1055, 'TW6', 430, '26G002301-11E', 'CNHI', 'P00174149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E3 P3', '11E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(222, 25,  305379,  'RD',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00755000 0.75 RD  19X0.23', 'WPA', '5802535921', 'P00004495', '492145600', '414298726', 'P00024412', '26G002301', 100, 1055, 'TW6', 460, '26G002301-12C', 'CNHI', 'P00145779', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E3 P4', '12C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(223, 25,  305373,  'WH/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750555 0.75 WH-BN  19X0.23', 'WPA', '5802535966', 'P00004495', '492145600', '414298726', 'P00024412', '26G004101', 100, 1055, 'TW6', 430, '26G004101-11E', 'CNHI', 'P00174149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E3 P3', '11E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(224, 25,  305379,  'RD',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00755000 0.75 RD  19X0.23', 'WPA', '5802535966', 'P00004495', '492145600', '414298726', 'P00024412', '26G004101', 100, 1055, 'TW6', 460, '26G004101-12C', 'CNHI', 'P00145779', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AB E3 P4', '12C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(225, 25,  305541,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003301', 150, 375, '0', 1270, '26G003301-26A', 'CNHI', 'P00134451', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P5', '26A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(226, 25,  305541,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003401', 150, 375, '0', 1270, '26G003401-26A', 'CNHI', 'P00134451', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P5', '26A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(227, 25,  305543,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003701', 100, 700, '0', 1280, '26G003701-26B', 'CNHI', 'P00134451', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P6', '26B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(228, 25,  305545,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003901', 75, 750, '0', 1290, '26G003901-26C', 'CNHI', 'P00134451', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P7', '26C', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(229, 25,  305547,  'BN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505560 0.50 BN-VT  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 480, '0', 1300, '26G002301-26D', 'CNHI', 'P00425846', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P9', '26D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(230, 25,  305547,  'BN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505560 0.50 BN-VT  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 480, '0', 1300, '26G004101-26D', 'CNHI', 'P00425846', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P9', '26D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(231, 25,  305549,  'BN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505560 0.50 BN-VT  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 225, 510, '0', 1310, '26G003001-26E', 'CNHI', 'P00425846', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P11', '26E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(232, 25,  305549,  'BN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505560 0.50 BN-VT  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 225, 510, '0', 1310, '26G003801-26E', 'CNHI', 'P00425846', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P11', '26E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(233, 25,  305551,  'BN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505560 0.50 BN-VT  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 590, '0', 1320, '26G002901-26F', 'CNHI', 'P00425846', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E3 P12', '26F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(234, 25,  305553,  'BU/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506505 0.50 BU-WH  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003301', 150, 375, '0', 1330, '26G003301-27A', 'CNHI', 'P00134388', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P13', '27A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(235, 25,  305553,  'BU/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506505 0.50 BU-WH  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003401', 150, 375, '0', 1330, '26G003401-27A', 'CNHI', 'P00134388', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P13', '27A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(236, 25,  305555,  'BU/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506505 0.50 BU-WH  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003701', 100, 700, '0', 1340, '26G003701-27B', 'CNHI', 'P00134388', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E3 P14', '27B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(237, 25,  305557,  'BU/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506505 0.50 BU-WH  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003901', 75, 750, '0', 1350, '26G003901-27C', 'CNHI', 'P00134388', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E4 P1', '27C', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(238, 25,  305559,  'VT/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506045 0.50 VT-OG  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 480, '0', 1360, '26G002301-27D', 'CNHI', 'P00425842', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P2', '27D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(239, 25,  305559,  'VT/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506045 0.50 VT-OG  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 480, '0', 1360, '26G004101-27D', 'CNHI', 'P00425842', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P2', '27D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(240, 25,  305561,  'VT/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506045 0.50 VT-OG  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 225, 510, '0', 1370, '26G003001-27E', 'CNHI', 'P00425842', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P3', '27E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(241, 25,  305561,  'VT/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506045 0.50 VT-OG  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 225, 510, '0', 1370, '26G003801-27E', 'CNHI', 'P00425842', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P3', '27E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(242, 25,  305563,  'VT/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506045 0.50 VT-OG  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 590, '0', 1380, '26G002901-27F', 'CNHI', 'P00425842', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P4', '27F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(243, 25,  305565,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003301', 150, 375, '0', 1390, '26G003301-28A', 'CNHI', 'P00169734', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E4 P5', '28A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(244, 25,  305565,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003401', 150, 375, '0', 1390, '26G003401-28A', 'CNHI', 'P00169734', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E4 P5', '28A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(245, 25,  305567,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003701', 100, 700, '0', 1400, '26G003701-28B', 'CNHI', 'P00169734', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E4 P6', '28B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(246, 25,  305569,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003901', 75, 750, '0', 1410, '26G003901-28C', 'CNHI', 'P00169734', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E4 P7', '28C', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(247, 25,  305571,  'YE/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503565 0.50 YE-BU  19X0.19', 'WPA', '5802535921', 'P00004495', 'P00102616', '414298726', '414345400', '26G002301', 100, 350, 'TW10', 1420, '26G002301-28D', 'CNHI', 'P00173225', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P8', '28D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(248, 25,  305571,  'YE/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503565 0.50 YE-BU  19X0.19', 'WPA', '5802535966', 'P00004495', 'P00102616', '414298726', '414345400', '26G004101', 100, 350, 'TW10', 1420, '26G004101-28D', 'CNHI', 'P00173225', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P8', '28D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(249, 25,  305393,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750545 0.75 WH-OG  19X0.23', 'WPA', '5802535910', 'P00004495', 'P00004495', '414298726', '412048666', '26G002901', 75, 775, 'TW7', 530, '26G002901-13E', 'CNHI', 'P00425827', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P5', '13E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(250, 25,  305397,  'GY/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00752545 0.75 GY-OG  19X0.23', 'WPA', '5802535910', 'P00004495', 'P00004495', '414298726', '412048666', '26G002901', 75, 775, 'TW7', 550, '26G002901-14B', 'CNHI', 'P00145777', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P6', '14B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(251, 25,  305393,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750545 0.75 WH-OG  19X0.23', 'WPA', '5802535921', 'P00004495', 'P00004495', '414298726', '412048666', '26G002301', 75, 775, 'TW7', 530, '26G002301-13E', 'CNHI', 'P00425827', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P5', '13E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(252, 25,  305397,  'GY/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00752545 0.75 GY-OG  19X0.23', 'WPA', '5802535921', 'P00004495', 'P00004495', '414298726', '412048666', '26G002301', 75, 775, 'TW7', 550, '26G002301-14B', 'CNHI', 'P00145777', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P6', '14B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(253, 25,  305387,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'WPA', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 75, 875, 'TW9', 500, '26G003701-13B', 'CNHI', 'P00134451', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E3 P12', '13B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(254, 25,  305401,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'WPA', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 75, 875, 'TW9', 570, '26G003701-14D', 'CNHI', 'P00169734', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AA E4 P8', '14D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(255, 25,  305389,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'WPA', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 75, 955, 'TW8', 510, '26G003901-13C', 'CNHI', 'P00134451', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P9', '13C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(256, 25,  305581,  'GN/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504045 0.50 GN-OG  19X0.19', 'WPA', '5802535921', 'P00004495', 'P00102616', '414298726', '414345400', '26G002301', 100, 350, 'TW10', 1470, '26G002301-29E', 'CNHI', 'P00425845', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P10', '29E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(257, 25,  305581,  'GN/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504045 0.50 GN-OG  19X0.19', 'WPA', '5802535966', 'P00004495', 'P00102616', '414298726', '414345400', '26G004101', 100, 350, 'TW10', 1470, '26G004101-29E', 'CNHI', 'P00425845', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P10', '29E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(258, 25,  305583,  'GY/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502540 0.50 GY-GN  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414345400', '26G002301', 100, 340, '0', 1480, '26G002301-30A', 'CNHI', 'P00174329', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P11', '30A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(259, 25,  305583,  'GY/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502540 0.50 GY-GN  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414345400', '26G004101', 100, 340, '0', 1480, '26G004101-30A', 'CNHI', 'P00174329', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P11', '30A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(260, 25,  305393,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750545 0.75 WH-OG  19X0.23', 'WPA', '5802535966', 'P00004495', 'P00004495', '414298726', '412048666', '26G004101', 75, 775, 'TW7', 530, '26G004101-13E', 'CNHI', 'P00425827', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P5', '13E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(261, 25,  305397,  'GY/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00752545 0.75 GY-OG  19X0.23', 'WPA', '5802535966', 'P00004495', 'P00004495', '414298726', '412048666', '26G004101', 75, 775, 'TW7', 550, '26G004101-14B', 'CNHI', 'P00145777', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AA E4 P6', '14B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(262, 25,  305391,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750545 0.75 WH-OG  19X0.23', 'WPA', '5802535903', 'P00004495', 'P00004495', '414298726', '412048666', '26G003801', 150, 710, 'TW7', 520, '26G003801-13D', 'CNHI', 'P00425827', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E4 P12', '13D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(263, 25,  305395,  'GY/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00752545 0.75 GY-OG  19X0.23', 'WPA', '5802535903', 'P00004495', 'P00004495', '414298726', '412048666', '26G003801', 150, 710, 'TW7', 540, '26G003801-14A', 'CNHI', 'P00145777', 0, 4, 3.5, 'FLR2X-A 0.75', '3CV', 'AB E4 P13', '14A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(264, 25,  305403,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'WPA', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 75, 955, 'TW8', 580, '26G003901-14E', 'CNHI', 'P00169734', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E4 P14', '14E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(265, 25,  305579,  'YE/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503565 0.50 YE-BU  19X0.19', 'WPA', '5802535910', 'P00004495', 'P00102616', '414298726', '414345400', '26G002901', 100, 410, 'TW8', 1460, '26G002901-29D', 'CNHI', 'P00173225', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P1', '29D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(266, 25,  305591,  'GN/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504045 0.50 GN-OG  19X0.19', 'WPA', '5802535910', 'P00004495', 'P00102616', '414298726', '414345400', '26G002901', 100, 410, 'TW8', 1520, '26G002901-30E', 'CNHI', 'P00425845', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P2', '30E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(267, 25,  305593,  'BN/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505565 0.50 BN-BU  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003701', 100, 780, '0', 1530, '26G003701-31A', 'CNHI', 'P00153336', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E5 P3', '31A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(268, 25,  305595,  'BN/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505565 0.50 BN-BU  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003301', 75, 805, '0', 1540, '26G003301-31B', 'CNHI', 'P00153336', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E5 P4', '31B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(269, 25,  305597,  'BN/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505565 0.50 BN-BU  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003401', 150, 845, '0', 1550, '26G003401-31C', 'CNHI', 'P00153336', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E5 P5', '31C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(270, 25,  305597,  'BN/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505565 0.50 BN-BU  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003901', 150, 845, '0', 1550, '26G003901-31C', 'CNHI', 'P00153336', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E5 P5', '31C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(271, 25,  305599,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503500 0.50 YE  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 965, '0', 1560, '26G002301-31D', 'CNHI', 'P00161179', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P6', '31D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(272, 25,  305599,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503500 0.50 YE  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 965, '0', 1560, '26G004101-31D', 'CNHI', 'P00161179', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P6', '31D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(273, 25,  305601,  'GY/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502540 0.50 GY-GN  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414345400', '26G003801', 150, 300, '0', 1570, '26G003801-31E', 'CNHI', 'P00174329', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P7', '31E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(274, 25,  305603,  'GY/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502540 0.50 GY-GN  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414345400', '26G003001', 75, 340, '0', 1580, '26G003001-31F', 'CNHI', 'P00174329', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P8', '31F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(275, 25,  305605,  'GY/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502540 0.50 GY-GN  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414345400', '26G002901', 100, 400, '0', 1590, '26G002901-31G', 'CNHI', 'P00174329', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P9', '31G', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(276, 25,  305607,  'WH/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500565 0.50 WH-BU  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00004495', '414298726', '411502200', '26G002301', 100, 825, '0', 1600, '26G002301-32A', 'CNHI', 'P00134452', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AB E5 P10', '32A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(277, 25,  305607,  'WH/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500565 0.50 WH-BU  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', '411502200', '26G004101', 100, 825, '0', 1600, '26G004101-32A', 'CNHI', 'P00134452', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AB E5 P10', '32A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(278, 25,  305609,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503500 0.50 YE  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 225, 820, '0', 1610, '26G003801-32B', 'CNHI', 'P00161179', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P11', '32B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(279, 25,  305609,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503500 0.50 YE  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 225, 820, '0', 1610, '26G003001-32B', 'CNHI', 'P00161179', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P11', '32B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(280, 25,  305611,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503500 0.50 YE  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 930, '0', 1620, '26G002901-32C', 'CNHI', 'P00161179', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AB E5 P12', '32C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(281, 25,  305613,  'BU/GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506525 0.50 BU-GY  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003701', 100, 780, '0', 1630, '26G003701-32D', 'CNHI', 'P00173209', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E5 P13', '32D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(282, 25,  305615,  'BU/GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506525 0.50 BU-GY  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003301', 75, 805, '0', 1640, '26G003301-32E', 'CNHI', 'P00173209', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AB E5 P14', '32E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(283, 25,  305617,  'BU/GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506525 0.50 BU-GY  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003401', 150, 845, '0', 1650, '26G003401-32F', 'CNHI', 'P00173209', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AC E1 P1', '32F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(284, 25,  305617,  'BU/GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506525 0.50 BU-GY  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003901', 150, 845, '0', 1650, '26G003901-32F', 'CNHI', 'P00173209', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AC E1 P1', '32F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(285, 25,  305619,  'GY/YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502535 0.50 GY-YE  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00004495', '414298726', '411502200', '26G002301', 100, 825, '0', 1660, '26G002301-33A', 'CNHI', 'P00134387', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P2', '33A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(286, 25,  305619,  'GY/YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502535 0.50 GY-YE  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', '411502200', '26G004101', 100, 825, '0', 1660, '26G004101-33A', 'CNHI', 'P00134387', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P2', '33A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(287, 25,  305621,  'WH/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500565 0.50 WH-BU  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', '411502200', '26G003001', 75, 710, '0', 1670, '26G003001-33B', 'CNHI', 'P00134452', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P3', '33B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(288, 25,  305623,  'WH/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500565 0.50 WH-BU  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', '411502200', '26G003801', 150, 810, '0', 1680, '26G003801-33C', 'CNHI', 'P00134452', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P4', '33C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(289, 25,  305623,  'WH/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500565 0.50 WH-BU  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', '411502200', '26G002901', 150, 810, '0', 1680, '26G002901-33C', 'CNHI', 'P00134452', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P4', '33C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(290, 25,  305625,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500545 0.50 WH-OG  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003701', 100, 780, '0', 1690, '26G003701-33D', 'CNHI', 'P00425841', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AC E1 P5', '33D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(291, 25,  305627,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500545 0.50 WH-OG  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003301', 75, 805, '0', 1700, '26G003301-33E', 'CNHI', 'P00425841', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AC E1 P6', '33E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(292, 25,  305629,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500545 0.50 WH-OG  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003401', 150, 845, '0', 1710, '26G003401-33F', 'CNHI', 'P00425841', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AC E1 P7', '33F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(293, 25,  305629,  'WH/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500545 0.50 WH-OG  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', 'P00024413', '26G003901', 150, 845, '0', 1710, '26G003901-33F', 'CNHI', 'P00425841', 0, 4, 3.2, 'FLR2X-A 0.50', '3CV', 'AC E1 P7', '33F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(294, 25,  305631,  'GY/YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502535 0.50 GY-YE  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', '411502200', '26G003001', 75, 710, '0', 1720, '26G003001-34A', 'CNHI', 'P00134387', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P8', '34A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(295, 25,  305633,  'GY/YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502535 0.50 GY-YE  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', '411502200', '26G003801', 150, 810, '0', 1730, '26G003801-34B', 'CNHI', 'P00134387', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P9', '34B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(296, 25,  305633,  'GY/YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502535 0.50 GY-YE  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', '411502200', '26G002901', 150, 810, '0', 1730, '26G002901-34B', 'CNHI', 'P00134387', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P9', '34B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(297, 25,  305635,  'VT/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506045 0.50 VT-OG  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 225, 400, '0', 1740, '26G003301-34C', 'CNHI', 'P00425842', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P10', '34C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(298, 25,  305635,  'VT/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506045 0.50 VT-OG  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 225, 400, '0', 1740, '26G003701-34C', 'CNHI', 'P00425842', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P10', '34C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(299, 25,  305635,  'VT/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506045 0.50 VT-OG  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 225, 400, '0', 1740, '26G003901-34C', 'CNHI', 'P00425842', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P10', '34C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(300, 25,  305635,  'VT/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506045 0.50 VT-OG  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 225, 400, '0', 1740, '26G003401-34C', 'CNHI', 'P00425842', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P10', '34C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(301, 25,  305637,  'BU/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506555 0.50 BU-BN  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 225, 400, '0', 1750, '26G003301-35A', 'CNHI', 'P00134389', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P11', '35A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(302, 25,  305637,  'BU/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506555 0.50 BU-BN  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 225, 400, '0', 1750, '26G003701-35A', 'CNHI', 'P00134389', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P11', '35A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(303, 25,  305637,  'BU/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506555 0.50 BU-BN  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 225, 400, '0', 1750, '26G003901-35A', 'CNHI', 'P00134389', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P11', '35A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(304, 25,  305637,  'BU/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506555 0.50 BU-BN  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 225, 400, '0', 1750, '26G003401-35A', 'CNHI', 'P00134389', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P11', '35A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(305, 25,  305639,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'WPA', '5802535901', 'P00004495', 'P00100259', '414298726', 'USW', '26G003001', 75, 125, 'MC1', 1760, '26G003001-35B', 'CNHI', 'P00134456', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P12', '35B', 'Splice - MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(306, 25,  305639,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'WPA', '5802535910', 'P00004495', 'P00100259', '414298726', 'USW', '26G002901', 75, 125, 'MC1', 1760, '26G002901-35B', 'CNHI', 'P00134456', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P12', '35B', 'Splice - MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(307, 25,  305641,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 965, '0', 1770, '26G002301-35C', 'CNHI', 'P00141984', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P13', '35C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(308, 25,  305641,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 965, '0', 1770, '26G004101-35C', 'CNHI', 'P00141984', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E1 P13', '35C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(309, 25,  305643,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'WPA', '5802535901', 'P00102616', 'P00139648', '414148873 replaced by P00005206', 'USW', '26G003001', 225, 400, '0', 1780, '26G003001-36A', 'CNHI', 'P00141984', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P14', '36A', 'Splice', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(310, 25,  305643,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'WPA', '5802535903', 'P00102616', 'P00139648', '414148873 replaced by P00005206', 'USW', '26G003801', 225, 400, '0', 1780, '26G003801-36A', 'CNHI', 'P00141984', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E1 P14', '36A', 'Splice', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(311, 25,  305645,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 930, '0', 1790, '26G002901-36B', 'CNHI', 'P00141984', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P1', '36B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(312, 25,  305647,  'YE/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503555 0.50 YE-BN  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', '414298726', '26G004101', 100, 210, '0', 1800, '26G004101-36C', 'CNHI', 'P00174322', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P2', '36C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(313, 25,  305649,  'YE/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503555 0.50 YE-BN  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 570, '0', 1810, '26G002301-36D', 'CNHI', 'P00174322', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P3', '36D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(314, 25,  305651,  'BN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505560 0.50 BN-VT  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 225, 400, '0', 1820, '26G003301-36E', 'CNHI', 'P00425846', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P4', '36E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(315, 25,  305651,  'BN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505560 0.50 BN-VT  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 225, 400, '0', 1820, '26G003701-36E', 'CNHI', 'P00425846', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P4', '36E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(316, 25,  305651,  'BN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505560 0.50 BN-VT  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 225, 400, '0', 1820, '26G003901-36E', 'CNHI', 'P00425846', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P4', '36E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(317, 25,  305651,  'BN/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505560 0.50 BN-VT  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 225, 400, '0', 1820, '26G003401-36E', 'CNHI', 'P00425846', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P4', '36E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(318, 25,  305653,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003301', 225, 400, '0', 1830, '26G003301-37A', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P5', '37A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(319, 25,  305653,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003701', 225, 400, '0', 1830, '26G003701-37A', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P5', '37A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(320, 25,  305653,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003901', 225, 400, '0', 1830, '26G003901-37A', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P5', '37A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(321, 25,  305653,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003401', 225, 400, '0', 1830, '26G003401-37A', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P5', '37A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(322, 25,  305655,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505500 0.50 BN  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00004495', '414298726', '411502200', '26G002301', 100, 825, '0', 1840, '26G002301-37B', 'CNHI', 'P00144162', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E2 P6', '37B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(323, 25,  305655,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505500 0.50 BN  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00004495', '414298726', '411502200', '26G004101', 100, 825, '0', 1840, '26G004101-37B', 'CNHI', 'P00144162', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E2 P6', '37B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(324, 25,  305657,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505500 0.50 BN  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004495', '414298726', '411502200', '26G003001', 75, 710, '0', 1850, '26G003001-38A', 'CNHI', 'P00144162', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E2 P7', '38A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(325, 25,  305659,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505500 0.50 BN  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004495', '414298726', '411502200', '26G003801', 150, 810, '0', 1860, '26G003801-38B', 'CNHI', 'P00144162', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E2 P8', '38B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(326, 25,  305659,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505500 0.50 BN  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004495', '414298726', '411502200', '26G002901', 150, 810, '0', 1860, '26G002901-38B', 'CNHI', 'P00144162', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E2 P8', '38B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(327, 25,  305577,  'YE/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503565 0.50 YE-BU  19X0.19', 'WPA', '5802535901', 'P00004495', 'P00102616', '414298726', '414345400', '26G003001', 225, 310, 'TW8', 1450, '26G003001-29C', 'CNHI', 'P00173225', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P9', '29C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(328, 25,  305589,  'GN/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504045 0.50 GN-OG  19X0.19', 'WPA', '5802535901', 'P00004495', 'P00102616', '414298726', '414345400', '26G003001', 225, 310, 'TW8', 1510, '26G003001-30D', 'CNHI', 'P00425845', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P10', '30D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(329, 25,  305661,  'WH/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750555 0.75 WH-BN  19X0.23', 'WPA', '5802535888', 'P00004495', '492145600', '414298726', 'P00024412', '26G003701', 150, 1020, 'TW8', 1870, '26G003701-38C', 'CNHI', 'P00174149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P13', '38C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(330, 25,  305663,  'WH/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00750555 0.75 WH-BN  19X0.23', 'WPA', '5802535894', 'P00004495', '492145600', '414298726', 'P00024412', '26G003901', 75, 1045, 'TW10', 1880, '26G003901-38D', 'CNHI', 'P00174149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AC E2 P11', '38D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(331, 25,  305665,  'VT/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506040 0.50 VT-GN  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 1480, '0', 1890, '26G002301-38E', 'CNHI', 'P00294250', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P12', '38E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(332, 25,  305665,  'VT/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506040 0.50 VT-GN  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 1480, '0', 1890, '26G004101-38E', 'CNHI', 'P00294250', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P12', '38E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(333, 25,  305667,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 1485, '0', 1900, '26G004101-39A', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P13', '39A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(334, 25,  305667,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 1485, '0', 1900, '26G002301-39A', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P13', '39A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(335, 25,  305577,  'YE/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503565 0.50 YE-BU  19X0.19', 'WPA', '5802535903', 'P00004495', 'P00102616', '414298726', '414345400', '26G003801', 225, 310, 'TW8', 1450, '26G003801-29C', 'CNHI', 'P00173225', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P9', '29C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(336, 25,  305589,  'GN/OG',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504045 0.50 GN-OG  19X0.19', 'WPA', '5802535903', 'P00004495', 'P00102616', '414298726', '414345400', '26G003801', 225, 310, 'TW8', 1510, '26G003801-30D', 'CNHI', 'P00425845', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E2 P10', '30D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(337, 25,  305669,  'RD',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00755000 0.75 RD  19X0.23', 'WPA', '5802535888', 'P00004495', '492145600', '414298726', 'P00024412', '26G003701', 150, 1020, 'TW8', 1910, '26G003701-39B', 'CNHI', 'P00145779', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AA E3 P14', '39B', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(338, 25,  305671,  'RD',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00755000 0.75 RD  19X0.23', 'WPA', '5802535894', 'P00004495', '492145600', '414298726', 'P00024412', '26G003901', 75, 1045, 'TW10', 1920, '26G003901-39C', 'CNHI', 'P00145779', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AC E2 P14', '39C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(339, 25,  305673,  'VT/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506040 0.50 VT-GN  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 75, 1455, '0', 1930, '26G003001-39D', 'CNHI', 'P00294250', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P1', '39D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(340, 25,  305675,  'VT/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506040 0.50 VT-GN  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 150, 1395, '0', 1940, '26G003801-39E', 'CNHI', 'P00294250', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P2', '39E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(341, 25,  305675,  'VT/GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506040 0.50 VT-GN  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 150, 1395, '0', 1940, '26G002901-39E', 'CNHI', 'P00294250', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P2', '39E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(342, 25,  305677,  'GY/YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502535 0.50 GY-YE  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', '411502200', '26G003301', 75, 730, '0', 1950, '26G003301-40A', 'CNHI', 'P00134387', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P3', '40A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(343, 25,  305679,  'GY/YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502535 0.50 GY-YE  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', '411502200', '26G003401', 150, 830, '0', 1960, '26G003401-40B', 'CNHI', 'P00134387', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P4', '40B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(344, 25,  305679,  'GY/YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502535 0.50 GY-YE  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', '411502200', '26G003701', 150, 830, '0', 1960, '26G003701-40B', 'CNHI', 'P00134387', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P4', '40B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(345, 25,  305679,  'GY/YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502535 0.50 GY-YE  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', '411502200', '26G003901', 150, 830, '0', 1960, '26G003901-40B', 'CNHI', 'P00134387', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P4', '40B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(346, 25,  305681,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 75, 1455, '0', 1970, '26G003001-40C', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P5', '40C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(347, 25,  305683,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 150, 1400, '0', 1980, '26G003801-40D', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P6', '40D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(348, 25,  305683,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 150, 1400, '0', 1980, '26G002901-40D', 'CNHI', 'P00134456', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P6', '40D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(349, 25,  305685,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'WPA', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 150, 815, 'TW10', 1990, '26G003801-41A', 'CNHI', 'P00134451', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P7', '41A', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(350, 25,  305687,  'WH/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500565 0.50 WH-BU  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', '411502200', '26G003301', 75, 750, '0', 2000, '26G003301-41B', 'CNHI', 'P00134452', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P8', '41B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(351, 25,  305687,  'WH/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500565 0.50 WH-BU  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', '411502200', '26G003701', 75, 750, '0', 2000, '26G003701-41B', 'CNHI', 'P00134452', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P8', '41B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(352, 25,  305689,  'WH/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500565 0.50 WH-BU  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', '411502200', '26G003401', 150, 830, '0', 2010, '26G003401-41C', 'CNHI', 'P00134452', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P9', '41C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(353, 25,  305689,  'WH/BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500565 0.50 WH-BU  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', '411502200', '26G003901', 150, 830, '0', 2010, '26G003901-41C', 'CNHI', 'P00134452', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P9', '41C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(354, 25,  305691,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505500 0.50 BN  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', '411502200', '26G003301', 75, 790, '0', 2020, '26G003301-42A', 'CNHI', 'P00144162', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P10', '42A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(355, 25,  305693,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505500 0.50 BN  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', '411502200', '26G003401', 150, 830, '0', 2030, '26G003401-42B', 'CNHI', 'P00144162', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P11', '42B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(356, 25,  305693,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505500 0.50 BN  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', '411502200', '26G003701', 150, 830, '0', 2030, '26G003701-42B', 'CNHI', 'P00144162', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P11', '42B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(357, 25,  305693,  'BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505500 0.50 BN  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', '411502200', '26G003901', 150, 830, '0', 2030, '26G003901-42B', 'CNHI', 'P00144162', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P11', '42B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(358, 25,  305695,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502500 0.50 GY  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 1490, '0', 2040, '26G002301-42C', 'CNHI', 'P00145134', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P12', '42C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(359, 25,  305695,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502500 0.50 GY  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 1490, '0', 2040, '26G004101-42C', 'CNHI', 'P00145134', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P12', '42C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(360, 25,  305697,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506000 0.50 VT  19X0.19', 'WPA', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 150, 815, 'TW10', 2050, '26G003801-42D', 'CNHI', 'P00169734', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P13', '42D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(361, 25,  305699,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'WPA', '5802535901', 'P00102616', 'P00100259', '414148873 replaced by P00005206', 'USW', '26G003001', 75, 200, 'MC1', 2060, '26G003001-43A', 'CNHI', 'P00134456', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P14', '43A', 'Splice - MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(362, 25,  305699,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'WPA', '5802535910', 'P00102616', 'P00100259', '414148873 replaced by P00005206', 'USW', '26G002901', 75, 200, 'MC1', 2060, '26G002901-43A', 'CNHI', 'P00134456', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E3 P14', '43A', 'Splice - MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(363, 25,  305701,  'YE/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503555 0.50 YE-BN  19X0.19', 'Assembly', '5802535894', 'P00004495', 'P00004495', '414298726', '414298726', '26G003901', 225, 210, '0', 2070, '26G003901-43B', 'CNHI', 'P00174322', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E4 P1', '43B', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(364, 25,  305701,  'YE/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503555 0.50 YE-BN  19X0.19', 'Assembly', '5802535878', 'P00004495', 'P00004495', '414298726', '414298726', '26G003401', 225, 210, '0', 2070, '26G003401-43B', 'CNHI', 'P00174322', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E4 P1', '43B', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(365, 25,  305701,  'YE/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503555 0.50 YE-BN  19X0.19', 'Assembly', '5802535883', 'P00004495', 'P00004495', '414298726', '414298726', '26G003301', 225, 210, '0', 2070, '26G003301-43B', 'CNHI', 'P00174322', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E4 P1', '43B', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(366, 25,  305701,  'YE/BN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503555 0.50 YE-BN  19X0.19', 'Assembly', '5802535888', 'P00004495', 'P00004495', '414298726', '414298726', '26G003701', 225, 210, '0', 2070, '26G003701-43B', 'CNHI', 'P00174322', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E4 P1', '43B', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(367, 25,  305703,  'OG/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754505 0.75 OG-WH  19X0.23', 'WPA', '5802535966', 'P00004495', 'P00102616', '414298726', '414345400', '26G004101', 100, 595, 'TW9', 2080, '26G004101-43C', 'CNHI', 'P00318149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AC E3 P2', '43C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(368, 25,  305703,  'OG/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754505 0.75 OG-WH  19X0.23', 'WPA', '5802535921', 'P00004495', 'P00102616', '414298726', '414345400', '26G002301', 100, 595, 'TW9', 2080, '26G002301-43C', 'CNHI', 'P00318149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AC E3 P2', '43C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(369, 25,  305705,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502500 0.50 GY  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 150, 1390, '0', 2090, '26G003801-44A', 'CNHI', 'P00145134', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P3', '44A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(370, 25,  305705,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502500 0.50 GY  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 150, 1390, '0', 2090, '26G002901-44A', 'CNHI', 'P00145134', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P3', '44A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(371, 25,  305707,  'GY',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00502500 0.50 GY  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 75, 1440, '0', 2100, '26G003001-44B', 'CNHI', 'P00145134', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P4', '44B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(372, 25,  305709,  'VT/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756005 0.75 VT-WH  19X0.23', 'WPA', '5802535966', 'P00004495', 'P00102616', '414298726', '414345400', '26G004101', 100, 595, 'TW9', 2110, '26G004101-44C', 'CNHI', 'P00425828', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AC E3 P5', '44C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(373, 25,  305709,  'VT/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756005 0.75 VT-WH  19X0.23', 'WPA', '5802535921', 'P00004495', 'P00102616', '414298726', '414345400', '26G002301', 100, 595, 'TW9', 2110, '26G002301-44C', 'CNHI', 'P00425828', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AC E3 P5', '44C', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(374, 25,  305711,  'GN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02504005 2.50 GN-WH  50X0.26', 'Assembly', '5802535878', 'P00102617', 'P00102617', '414246631', '414148875', '26G003401', 150, 465, '0', 2120, '26G003401-44D', 'CNHI', 'P00490260', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E3 P6', '44D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(375, 25,  305711,  'GN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02504005 2.50 GN-WH  50X0.26', 'Assembly', '5802535883', 'P00102617', 'P00102617', '414246631', '414148875', '26G003301', 150, 465, '0', 2120, '26G003301-44D', 'CNHI', 'P00490260', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E3 P6', '44D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(376, 25,  305711,  'GN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02504005 2.50 GN-WH  50X0.26', 'Assembly', '5802535888', 'P00102617', 'P00102617', '414246631', '414148875', '26G003701', 150, 465, '0', 2120, '26G003701-44D', 'CNHI', 'P00490260', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E3 P6', '44D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(377, 25,  305713,  'GN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02504005 2.50 GN-WH  50X0.26', 'Assembly', '5802535894', 'P00102617', 'P00102617', '414246631', '414148875', '26G003901', 75, 575, '0', 2130, '26G003901-44E', 'CNHI', 'P00490260', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E3 P7', '44E', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(378, 25,  305715,  'PK',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00501000 0.50 PK  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 75, 475, '0', 2140, '26G003001-45A', 'CNHI', 'P00144159', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P9', '45A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(379, 25,  305717,  'PK',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00501000 0.50 PK  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 150, 375, '0', 2150, '26G003801-45B', 'CNHI', 'P00144159', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E3 P11', '45B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(380, 25,  305719,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02503500 2.50 YE  50X0.26', 'Assembly', '5802535878', 'P00102617', 'P00102617', '414246631', '414148875', '26G003401', 150, 465, '0', 2160, '26G003401-45C', 'CNHI', 'P00169858', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E3 P12', '45C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(381, 25,  305719,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02503500 2.50 YE  50X0.26', 'Assembly', '5802535883', 'P00102617', 'P00102617', '414246631', '414148875', '26G003301', 150, 465, '0', 2160, '26G003301-45C', 'CNHI', 'P00169858', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E3 P12', '45C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(382, 25,  305719,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02503500 2.50 YE  50X0.26', 'Assembly', '5802535888', 'P00102617', 'P00102617', '414246631', '414148875', '26G003701', 150, 465, '0', 2160, '26G003701-45C', 'CNHI', 'P00169858', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E3 P12', '45C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(383, 25,  305721,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02503500 2.50 YE  50X0.26', 'Assembly', '5802535894', 'P00102617', 'P00102617', '414246631', '414148875', '26G003901', 75, 575, '0', 2170, '26G003901-45D', 'CNHI', 'P00169858', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E3 P13', '45D', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(384, 25,  305723,  'OG/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00754505 0.75 OG-WH  19X0.23', 'WPA', '5802535910', 'P00004495', 'P00102616', '414298726', '414345400', '26G002901', 100, 615, 'TW9', 2180, '26G002901-45E', 'CNHI', 'P00318149', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AC E3 P14', '45E', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(385, 25,  305725,  'GN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02504005 2.50 GN-WH  50X0.26', 'Assembly', '5802535921', 'P00102617', 'P00004510', '414246631', '420003467', '26G002301', 100, 650, '0', 2190, '26G002301-45F', 'CNHI', 'P00490260', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E4 P1', '45F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(386, 25,  305725,  'GN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02504005 2.50 GN-WH  50X0.26', 'Assembly', '5802535966', 'P00102617', 'P00004510', '414246631', '420003467', '26G004101', 100, 650, '0', 2190, '26G004101-45F', 'CNHI', 'P00490260', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E4 P1', '45F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(387, 25,  305727,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02503500 2.50 YE  50X0.26', 'Assembly', '5802535921', 'P00102617', 'P00004510', '414246631', '420003467', '26G002301', 100, 590, '0', 2200, '26G002301-46A', 'CNHI', 'P00169858', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E4 P2', '46A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(388, 25,  305727,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02503500 2.50 YE  50X0.26', 'Assembly', '5802535966', 'P00102617', 'P00004510', '414246631', '420003467', '26G004101', 100, 590, '0', 2200, '26G004101-46A', 'CNHI', 'P00169858', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E4 P2', '46A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(389, 25,  305729,  'BN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505505 0.50 BN-WH  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003801', 150, 375, '0', 2210, '26G003801-46B', 'CNHI', 'P00173207', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E4 P3', '46B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(390, 25,  305731,  'BN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00505505 0.50 BN-WH  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 75, 475, '0', 2220, '26G003001-46C', 'CNHI', 'P00173207', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E4 P4', '46C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(391, 25,  305733,  'VT/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00756005 0.75 VT-WH  19X0.23', 'WPA', '5802535910', 'P00004495', 'P00102616', '414298726', '414345400', '26G002901', 100, 615, 'TW9', 2230, '26G002901-46D', 'CNHI', 'P00425828', 0, 4, 4, 'FLR2X-A 0.75', '3CV', 'AC E4 P5', '46D', 'TWIST', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(392, 25,  305735,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506000 2.50 VT  50X0.26', 'Assembly', '5802535883', 'P00102617', 'P00102617', '414246631', '414148875', '26G003301', 75, 440, '0', 2240, '26G003301-46E', 'CNHI', 'P00490259', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E4 P6', '46E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(393, 25,  305737,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506000 2.50 VT  50X0.26', 'Assembly', '5802535878', 'P00102617', 'P00102617', '414246631', '414148875', '26G003401', 75, 470, '0', 2250, '26G003401-46F', 'CNHI', 'P00490259', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E4 P7', '46F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(394, 25,  305737,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506000 2.50 VT  50X0.26', 'Assembly', '5802535888', 'P00102617', 'P00102617', '414246631', '414148875', '26G003701', 75, 470, '0', 2250, '26G003701-46F', 'CNHI', 'P00490259', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E4 P7', '46F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(395, 25,  305739,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506000 2.50 VT  50X0.26', 'Assembly', '5802535894', 'P00102617', 'P00102617', '414246631', '414148875', '26G003901', 75, 570, '0', 2260, '26G003901-46G', 'CNHI', 'P00490259', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E4 P8', '46G', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(396, 25,  305741,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506500 2.50 BU  50X0.26', 'Assembly', '5802535883', 'P00102617', 'P00102617', '414246631', '414148875', '26G003301', 75, 440, '0', 2270, '26G003301-47A', 'CNHI', 'P00490258', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E4 P9', '47A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(397, 25,  305743,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506500 2.50 BU  50X0.26', 'Assembly', '5802535878', 'P00102617', 'P00102617', '414246631', '414148875', '26G003401', 75, 470, '0', 2280, '26G003401-47B', 'CNHI', 'P00490258', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E4 P10', '47B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(398, 25,  305743,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506500 2.50 BU  50X0.26', 'Assembly', '5802535888', 'P00102617', 'P00102617', '414246631', '414148875', '26G003701', 75, 470, '0', 2280, '26G003701-47B', 'CNHI', 'P00490258', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E4 P10', '47B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(399, 25,  305745,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506500 2.50 BU  50X0.26', 'Assembly', '5802535894', 'P00102617', 'P00102617', '414246631', '414148875', '26G003901', 75, 570, '0', 2290, '26G003901-47C', 'CNHI', 'P00490258', 0, 5, 4, 'FLR2X-B 2.50', '3CV', 'AC E4 P11', '47C', 'single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(400, 25,  305747,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506000 2.50 VT  50X0.26', 'Assembly', '5802535921', 'P00102617', 'P00004510', '414246631', '420003467', '26G002301', 100, 440, '0', 2300, '26G002301-47D', 'CNHI', 'P00490259', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E4 P12', '47D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(401, 25,  305747,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506000 2.50 VT  50X0.26', 'Assembly', '5802535966', 'P00102617', 'P00004510', '414246631', '420003467', '26G004101', 100, 440, '0', 2300, '26G004101-47D', 'CNHI', 'P00490259', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E4 P12', '47D', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(402, 25,  305749,  'GN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02504005 2.50 GN-WH  50X0.26', 'Assembly', '5802535901', 'P00102617', 'P00004510', '414246631', '420003467', '26G003001', 75, 570, '0', 2310, '26G003001-47E', 'CNHI', 'P00490260', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E4 P13', '47E', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(403, 25,  305751,  'GN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02504005 2.50 GN-WH  50X0.26', 'Assembly', '5802535910', 'P00102617', 'P00004510', '414246631', '420003467', '26G002901', 100, 650, '0', 2320, '26G002901-47F', 'CNHI', 'P00490260', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E4 P14', '47F', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(404, 25,  305753,  'GN/WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02504005 2.50 GN-WH  50X0.26', 'Assembly', '5802535903', 'P00102617', 'P00004510', '414246631', '420003467', '26G003801', 150, 680, '0', 2330, '26G003801-47G', 'CNHI', 'P00490260', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P1', '47G', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(405, 25,  305755,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02503500 2.50 YE  50X0.26', 'Assembly', '5802535901', 'P00102617', 'P00004510', '414246631', '420003467', '26G003001', 225, 510, '0', 2340, '26G003001-48A', 'CNHI', 'P00169858', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P2', '48A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(406, 25,  305755,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02503500 2.50 YE  50X0.26', 'Assembly', '5802535903', 'P00102617', 'P00004510', '414246631', '420003467', '26G003801', 225, 510, '0', 2340, '26G003801-48A', 'CNHI', 'P00169858', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P2', '48A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(407, 25,  305757,  'YE',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02503500 2.50 YE  50X0.26', 'Assembly', '5802535910', 'P00102617', 'P00004510', '414246631', '420003467', '26G002901', 100, 590, '0', 2350, '26G002901-48B', 'CNHI', 'P00169858', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P3', '48B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(408, 25,  305759,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506500 2.50 BU  50X0.26', 'Assembly', '5802535921', 'P00102617', 'P00004510', '414246631', '420003467', '26G002301', 100, 465, '0', 2360, '26G002301-48C', 'CNHI', 'P00490258', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P4', '48C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(409, 25,  305759,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506500 2.50 BU  50X0.26', 'Assembly', '5802535966', 'P00102617', 'P00004510', '414246631', '420003467', '26G004101', 100, 465, '0', 2360, '26G004101-48C', 'CNHI', 'P00490258', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P4', '48C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(410, 25,  305761,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'WPA', '5802535921', 'P00004495', 'P00100259', '414298726', 'USW', '26G002301', 100, 125, 'MC1', 2370, '26G002301-49A', 'CNHI', 'P00134456', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E5 P5', '49A', 'Splice - MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(411, 25,  305761,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'WPA', '5802535966', 'P00004495', 'P00100259', '414298726', 'USW', '26G004101', 100, 125, 'MC1', 2370, '26G004101-49A', 'CNHI', 'P00134456', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E5 P5', '49A', 'Splice - MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(412, 25,  305763,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506000 2.50 VT  50X0.26', 'Assembly', '5802535901', 'P00102617', 'P00004510', '414246631', '420003467', '26G003001', 75, 400, '0', 2380, '26G003001-49B', 'CNHI', 'P00490259', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P6', '49B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(413, 25,  305765,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506000 2.50 VT  50X0.26', 'Assembly', '5802535903', 'P00102617', 'P00004510', '414246631', '420003467', '26G003801', 150, 445, '0', 2390, '26G003801-49C', 'CNHI', 'P00490259', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P7', '49C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(414, 25,  305765,  'VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506000 2.50 VT  50X0.26', 'Assembly', '5802535910', 'P00102617', 'P00004510', '414246631', '420003467', '26G002901', 150, 445, '0', 2390, '26G002901-49C', 'CNHI', 'P00490259', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P7', '49C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(415, 25,  305767,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'WPA', '5802535921', 'P00102616', 'P00100259', '414148873 replaced by P00005206', 'USW', '26G002301', 100, 200, 'MC1', 2400, '26G002301-50A', 'CNHI', 'P00134456', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E5 P8', '50A', 'Splice - MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(416, 25,  305767,  'WH',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00500500 0.50 WH  19X0.19', 'WPA', '5802535966', 'P00102616', 'P00100259', '414148873 replaced by P00005206', 'USW', '26G004101', 100, 200, 'MC1', 2400, '26G004101-50A', 'CNHI', 'P00134456', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E5 P8', '50A', 'Splice - MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(417, 25,  305769,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506500 2.50 BU  50X0.26', 'Assembly', '5802535901', 'P00102617', 'P00004510', '414246631', '420003467', '26G003001', 225, 440, '0', 2410, '26G003001-50B', 'CNHI', 'P00490258', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P9', '50B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(418, 25,  305769,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506500 2.50 BU  50X0.26', 'Assembly', '5802535903', 'P00102617', 'P00004510', '414246631', '420003467', '26G003801', 225, 440, '0', 2410, '26G003801-50B', 'CNHI', 'P00490258', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P9', '50B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(419, 25,  305771,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4B02506500 2.50 BU  50X0.26', 'Assembly', '5802535910', 'P00102617', 'P00004510', '414246631', '420003467', '26G002901', 100, 470, '0', 2420, '26G002901-50C', 'CNHI', 'P00490258', 0, 5, 4.7, 'FLR2X-B 2.50', '3CV', 'AC E5 P10', '50C', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(420, 25,  305773,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535901', 'P00004495', 'P00004344', '414298726', 'P00115285', '26G003001', 225, 440, '0', 2430, '26G003001-51A', 'CNHI', 'P00141984', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E5 P11', '51A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(421, 25,  305773,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535903', 'P00004495', 'P00004344', '414298726', 'P00115285', '26G003801', 225, 440, '0', 2430, '26G003801-51A', 'CNHI', 'P00141984', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E5 P11', '51A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(422, 25,  305773,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004344', '414298726', 'P00115285', '26G002901', 225, 440, '0', 2430, '26G002901-51A', 'CNHI', 'P00141984', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E5 P11', '51A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(423, 25,  305775,  'YE/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503560 0.50 YE-VT  19X0.19', 'Assembly', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 100, 480, '0', 2440, '26G002301-51B', 'CNHI', 'P00173212', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E5 P12', '51B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(424, 25,  305775,  'YE/VT',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00503560 0.50 YE-VT  19X0.19', 'Assembly', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 100, 480, '0', 2440, '26G004101-51B', 'CNHI', 'P00173212', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AC E5 P12', '51B', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(425, 25,  305777,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'Assembly', '5802535910', 'P00004495', 'P00004344', '414298726', 'P00115285', '26G002901', 100, 440, '0', 2450, '26G002901-52A', 'CNHI', 'P00134451', 0, 4, 3.5, 'FLR2X-A 0.50', '3CV', 'AC E5 P13', '52A', 'Single wire', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(426, 25,  305779,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'WPA', '5802535901', 'P00004344', 'P00139648', 'P00115285', 'USW', '26G003001', 75, 750, '0', 2460, '26G003001-52B', 'CNHI', 'P00134451', 0, 3.5, 14.5, 'FLR2X-A 0.50', '3CV', 'AC E5 P14', '52B', 'Splice', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(427, 25,  305781,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00506500 0.50 BU  19X0.19', 'WPA', '5802535903', 'P00004344', 'P00139648', 'P00115285', 'USW', '26G003801', 150, 800, '0', 2470, '26G003801-52C', 'CNHI', 'P00134451', 0, 3.5, 14.5, 'FLR2X-A 0.50', '3CV', 'AD E1 P1', '52C', 'Splice', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(428, 25,  305783,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'WPA', '5802535901', 'P00004495', 'P00139648', '414298726', 'USW', '26G003001', 225, 530, '0', 2480, '26G003001-53A', 'CNHI', 'P00141984', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AD E1 P2', '53A', 'Splice', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(429, 25,  305783,  'GN',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', '2X4A00504000 0.50 GN  19X0.19', 'WPA', '5802535903', 'P00004495', 'P00139648', '414298726', 'USW', '26G003801', 225, 530, '0', 2480, '26G003801-53A', 'CNHI', 'P00141984', 0, 4, 14.5, 'FLR2X-A 0.50', '3CV', 'AD E1 P2', '53A', 'Splice', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(430, 25,  305785,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 710, 'MC1', 2490, '26G002901-MC1A', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P3', 'MC1A', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(431, 25,  305785,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 710, 'MC1', 2490, '26G002901-MC1A', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P3', 'MC1A', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(432, 25,  305785,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 710, 'MC1', 2490, '26G002901-MC1A', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P3', 'MC1A', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(433, 25,  305785,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 710, 'MC1', 2490, '26G002901-MC1A', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P3', 'MC1A', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(434, 25,  305785,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535910', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002901', 100, 710, 'MC1', 2490, '26G002901-MC1A', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P3', 'MC1A', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(435, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 375, 760, 'MC1', 2500, '26G003001-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(436, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 375, 760, 'MC1', 2500, '26G003001-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(437, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 375, 760, 'MC1', 2500, '26G003001-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(438, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 375, 760, 'MC1', 2500, '26G002301-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(439, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 375, 760, 'MC1', 2500, '26G002301-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(440, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 375, 760, 'MC1', 2500, '26G002301-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(441, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 375, 760, 'MC1', 2500, '26G004101-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(442, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 375, 760, 'MC1', 2500, '26G004101-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(443, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 375, 760, 'MC1', 2500, '26G004101-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(444, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 375, 760, 'MC1', 2500, '26G003001-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(445, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535901', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G003001', 375, 760, 'MC1', 2500, '26G003001-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(446, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 375, 760, 'MC1', 2500, '26G002301-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(447, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535921', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G002301', 375, 760, 'MC1', 2500, '26G002301-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(448, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 375, 760, 'MC1', 2500, '26G004101-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama'),
(449, 25,  305787,  'BU',  1, '2020-03-02 23:00:00', 'EZZIOURI Oussama', 'MRSWK/2507 BK 2X0.5 BU+VT -40/+125', 'WPA', '5802535966', 'P00004495', 'P00102616', '414298726', '414148873 replaced by P00005206', '26G004101', 375, 760, 'MC1', 2500, '26G004101-MC1B', 'CNHI', 'P00434241', 0, 4, 4, 'FLR2X-A 0.50', '3CV', 'AD E1 P4', 'MC1B', 'MC1', 1, '2020-03-02 23:00:00', 'EZZIOURI Oussama');
SELECT setval('public.wire_config_id_seq', 450, true);