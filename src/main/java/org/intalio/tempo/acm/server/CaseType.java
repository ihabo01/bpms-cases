package org.intalio.tempo.acm.server;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;



@Entity
@Table(name = "tempo_casetype")

@NamedQueries( { @NamedQuery(name = CaseType.SelectAll, query = "select m from CaseType m")
  , @NamedQuery(name = CaseType.SelectCaseById, query = "select m from CaseType m where id = ?")
  })
public class CaseType {
    public static final String SelectAll = "select_all_case_types";
    public static final String SelectCaseById = "select_case_type_by_id";




	
    
    @Column(name = "id")
    @Basic
    @Id
    public  String id;
    
    
    @Column(name = "description")
    @Basic
    public  String description;
    
    

    
    @Column(name = "status_diagram")
    @Basic
    public  String status_diagram;
    
    
    public String getStatus_diagram() {
		return status_diagram;
	}
	public void setStatus_diagram(String status_diagram) {
		this.status_diagram = status_diagram;
	}
	public CaseType(){
    	
    }
    public CaseType(String identifier){
    	id=identifier;
    }

	public String getID() {
		// TODO Auto-generated method stub
		return id;
	}
	public String getDescription() {
		// TODO Auto-generated method stub
		return description;
	}
}
