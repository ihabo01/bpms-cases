package org.intalio.tempo.workflow.acm.server.dao;

import java.util.List;

public class CaseHistory {

	private List<DBColumn> columns;
	private List<List> historyList;
	private String caseId;
	public List<List> getHistoryList() {
		return historyList;
	}

	public void setHistoryList(List<List> historyList) {
		this.historyList = historyList;
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

	public List<List> getHistoryEntries() {
		return historyList;
	}

	public void setCases(List<List> cases) {
		this.historyList = cases;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	
	public CaseHistory( String type,List<DBColumn> columns, List<List> cases,String caseId) {
		this.columns=columns;
		this.historyList=cases;
		this.type=type;
		this.caseId=caseId;
	}
}
