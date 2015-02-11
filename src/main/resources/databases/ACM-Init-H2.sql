DROP TABLE IF EXISTS bpms.tempo_casetype;
CREATE TABLE bpms.tempo_casetype (
		id VARCHAR(250),
		description VARCHAR(250),
		status_diagram VARCHAR(250),
		case_form VARCHAR(250)
	);

insert into module_action values(73,'Cases',NULL,0,NULL,1);