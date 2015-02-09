package org.intalio.tempo.workflow.acm.server.dao;

import java.util.List;

public class Case {

	private List<DBColumn> columns;
	private List data;
	private String caseId;


	public void setHistoryList(List caseData) {
		this.data = caseData;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}



	private String type;
	public List<DBColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DBColumn> columns) {
		this.columns = columns;
	}

	public List getCaseData() {
		return data;
	}

	public void setCaseData(List data) {
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	
	public Case( String type,List<DBColumn> columns, List data,String caseId) {
		this.columns=columns;
		this.data=data;
		this.type=type;
		this.caseId=caseId;
	}
}
