package org.intalio.tempo.workflow.acm.server.dao;

import java.util.List;

public class CaseList {

	private List<DBColumn> columns;
	private List<List> cases;
	public List<DBColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DBColumn> columns) {
		this.columns = columns;
	}

	public List<List> getCases() {
		return cases;
	}

	public void setCases(List<List> cases) {
		this.cases = cases;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String type;
	
	public CaseList( String type,List<DBColumn> columns, List<List> cases) {
		this.columns=columns;
		this.cases=cases;
		this.type=type;
	}
}
