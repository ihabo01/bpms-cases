package org.intalio.tempo.acm.server;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;



@Entity
@Table(name = "tempo_casetype")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries( { @NamedQuery(name = CaseType.SelectAll, query = "select m from CaseType m")
  })
public class CaseType {
    public static final String SelectAll = "select_all";
    
    public final String id;
    
    public CaseType(String identifier){
    	id=identifier;
    }

	public String getID() {
		// TODO Auto-generated method stub
		return id;
	}
	
}
