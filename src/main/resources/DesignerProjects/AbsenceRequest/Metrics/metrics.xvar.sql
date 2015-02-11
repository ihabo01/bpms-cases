## External variable database definition for Derby 
## Generated 04/02/15 14:27
## Descriptor: Metrics/metrics.xvar (currently edited)
# External variable Absence_Request_History
DROP TABLE bpms.Absence_Request_History;
CREATE TABLE bpms.Absence_Request_History (caseId VARCHAR(250),ID VARCHAR(250), STATUS_ACTIVITY_Type VARCHAR(250), STATUS_ACTIVITY VARCHAR(250), TIME TIMESTAMP, STATUS VARCHAR(250), NOTES VARCHAR(250));
CREATE INDEX bpms.Absence_Request_History_KEYS ON Absence_Request_History (ID);
# External variable Absence_Request
CREATE TABLE Absence_Request (ID VARCHAR(250), REQUEST_IDENTIFIER VARCHAR(250), STATUS VARCHAR(250), VALIDATED_ON TIMESTAMP, SUBMITED_ON TIMESTAMP, VALIDATOR VARCHAR(250), REQUESTOR VARCHAR(250));
CREATE INDEX Absence_Request_KEYS ON Absence_Request (ID);

DROP TABLE IF EXISTS bpms.tempo_casetype;
CREATE TABLE bpms.tempo_casetype (
		id VARCHAR(250),
		description VARCHAR(250),
		status_diagram VARCHAR(250),
		case_form VARCHAR(250)
	);

insert into module_action values(73,'Cases',NULL,0,NULL,1);