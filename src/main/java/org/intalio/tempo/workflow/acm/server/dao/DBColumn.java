package org.intalio.tempo.workflow.acm.server.dao;

public class DBColumn {

	int type; //SQL types
	String name;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	DBColumn(int columnType, String columnName){
		name=columnName;
		type=columnType;
	}
}
