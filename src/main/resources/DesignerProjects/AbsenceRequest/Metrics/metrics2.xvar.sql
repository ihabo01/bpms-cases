## External variable database definition for H2 
## Generated 03/02/15 10:56
## Descriptor: Metrics/metrics.xvar (currently edited)
# External variable Absence_Request_History
CREATE TABLE Absence_Request_History (ID VARCHAR(250), ATTRIBUTE4 VARCHAR(250), NOTES VARCHAR(250), STATUS VARCHAR(250));
CREATE INDEX Absence_Request_History_KEYS ON Absence_Request_History (ID);
# External variable Absence_Request
DROP TABLE  bpms.Absence_Request
CREATE TABLE bpms.Absence_Request (REQUEST_IDENTIFIER VARCHAR(250), STATUS VARCHAR(250), VALIDATED_ON TIMESTAMP, SUBMITED_ON TIMESTAMP, VALIDATOR VARCHAR(250), REQUESTOR VARCHAR(250));
CREATE INDEX bpms.Absence_Request_KEYS ON Absence_Request (REQUEST_IDENTIFIER);

