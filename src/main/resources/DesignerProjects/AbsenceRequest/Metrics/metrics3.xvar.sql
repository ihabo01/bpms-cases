## External variable database definition for Derby 
## Generated 04/02/15 14:28
## Descriptor: Metrics/metrics.xvar (currently edited)
# External variable Absence_Request_History
CREATE TABLE Absence_Request_History (ID VARCHAR(250), STATUS_ACTIVITY_TYPE VARCHAR(250), STATUS_ACTIVITY VARCHAR(250), TIME TIMESTAMP, STATUS VARCHAR(250), NOTES VARCHAR(250));
CREATE INDEX Absence_Request_History_KEYS ON Absence_Request_History (ID);
# External variable Absence_Request
CREATE TABLE Absence_Request (ID VARCHAR(250), REQUEST_IDENTIFIER VARCHAR(250), STATUS VARCHAR(250), VALIDATED_ON TIMESTAMP, SUBMITED_ON TIMESTAMP, VALIDATOR VARCHAR(250), REQUESTOR VARCHAR(250));
CREATE INDEX Absence_Request_KEYS ON Absence_Request (ID);

