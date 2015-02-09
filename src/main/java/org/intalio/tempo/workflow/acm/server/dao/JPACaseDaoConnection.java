/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */

package org.intalio.tempo.workflow.acm.server.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.intalio.tempo.acm.server.CaseType;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.jpa.AttachmentFetcher;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Persistence for task using JPA.
 */
public class JPACaseDaoConnection extends AbstractJPAConnection implements ICaseDAOConnection {

    private CaseFetcher _fetcher;
    private AttachmentFetcher _attachmentFetcher;

    public JPACaseDaoConnection(EntityManager createEntityManager) {
        super(createEntityManager);
        _fetcher = new CaseFetcher(createEntityManager);
      
    }
    
	@Override
	public CaseType[] fetchAllCaseTypes(UserRoles user) {

		List<CaseType> queryResult = _fetcher.selectAll();
		CaseType[] cases=new CaseType[queryResult.size()];
		
			return  queryResult.toArray(cases);
		
	}
	
	@Override
	public CaseList fetchAllCasesByType(UserRoles user,String caseType) throws UnavailableTaskException {

		CaseList queryResult = _fetcher.selectCases(caseType);
		
		
			return  queryResult;
		
	}

	@Override
	public CaseHistory fetchAllCaseHistory(UserRoles credentials,
			String caseType, String caseId) throws UnavailableTaskException {
		CaseHistory queryResult = _fetcher.selectCaseHistory(caseType,caseId);
		
		
		return  queryResult;
	}
	
	public Case fetchAllCaseData(UserRoles credentials,
			String caseType, String caseId) throws UnavailableTaskException {
		Case queryResult = _fetcher.selectCaseData(caseType,caseId);
		
		
		return  queryResult;
	}

	@Override
	public CaseType fetchCaseTypeByID(UserRoles credentials,
			String caseTypeId) {
		CaseType queryResult = _fetcher.selectCaseById(caseTypeId);

		
			return  queryResult;
	}
    
}
