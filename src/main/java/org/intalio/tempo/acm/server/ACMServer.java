/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.acm.server;

import java.util.List;

import org.intalio.tempo.workflow.acm.server.dao.Case;
import org.intalio.tempo.workflow.acm.server.dao.CaseHistory;
import org.intalio.tempo.workflow.acm.server.dao.CaseList;
import org.intalio.tempo.workflow.acm.server.dao.ICaseDAOConnection;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.IAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;

import com.intalio.tempo.orgmapping.service.OrganizationUserService;


public class ACMServer implements IACMServer {

    private static final Logger _logger = LoggerFactory.getLogger(ACMServer.class);
    private IAuthProvider _authProvider;
 //   private TaskPermissions _permissions;
    private int _httpTimeout = 30000;

    private String httpChunking = "true";
    private String internalPassword = "verylongpassword";

    private OrganizationUserService organizationUserService;
    
    public String getInternalPassword() {
		return internalPassword;
	}

	public void setInternalPassword(String internalPassword) {
		this.internalPassword = internalPassword;
	}

	public String getHttpChunking() {
		return httpChunking;
	}

	public void setHttpChunking(String httpChunking) {
		this.httpChunking = httpChunking;
	}

	public boolean isHTTPChunking(){
		return Boolean.parseBoolean(httpChunking);
	}



      
    //Added the property for deleting file upload widget attachments




    // Added the property for the process endpoint that are stored without ODE server URL in the database
    private String _odeServerURL;
	public String getOdeServerURL() {
		return _odeServerURL;
	}

	public void setOdeServerURL(String odeServerURL) {
		_odeServerURL = odeServerURL;
	}

	public ACMServer() {
        BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator
                .getInstance();
        BeanFactoryReference bf = beanFactoryLocator
                .useBeanFactory("shareSpringContext");
        organizationUserService = (OrganizationUserService) bf.getFactory()
                .getBean("orgMapping.userService");
    }

    public ACMServer(IAuthProvider authProvider) {
        this();
        _logger.info("New ACM Instance");
        assert authProvider != null : "IAuthProvider implementation is absent!";
        setAuthProvider(authProvider);


    }



    public int getHttpTimeout() {
        return _httpTimeout;
    }

    public void setHttpTimeout(int httpTimeout) {
        _httpTimeout = httpTimeout;
    }

    public void setAuthProvider(IAuthProvider authProvider) {
        this._authProvider = authProvider;
        _logger.info("IAuthProvider implementation : " + _authProvider.getClass());
    }
    


    public CaseType[] getCaseTypeList(ICaseDAOConnection dao,String participantToken) throws ACMException, AuthException {
        UserRoles credentials = this.getUserRoles(participantToken);
        CaseType[] result = dao.fetchAllCaseTypes(credentials);

        return result;
    }
    @Override
    public CaseType getCaseTypeById(ICaseDAOConnection dao,String participantToken,String caseTypeId) throws ACMException, AuthException {
        UserRoles credentials = this.getUserRoles(participantToken);
        CaseType result = dao.fetchCaseTypeByID(credentials,caseTypeId);

        return result;
    }
    
    @Override
	public CaseList getCasesByType(ICaseDAOConnection dao,
			String participantToken,String caseType) throws AuthException, UnavailableTaskException {
    	UserRoles credentials = this.getUserRoles(participantToken);
        CaseList result = dao.fetchAllCasesByType(credentials, caseType);

        return result;
	}



    public UserRoles getUserRoles(String participantToken) throws AuthException {
        UserRoles user = _authProvider.authenticate(participantToken);
 
        return user;
    }

	@Override
	public CaseHistory getCaseHistory(ICaseDAOConnection dao,
			String participantToken, String caseType, String caseId) throws AuthException, UnavailableTaskException {
		UserRoles credentials = this.getUserRoles(participantToken);
        CaseHistory result = dao.fetchAllCaseHistory(credentials, caseType,caseId);

        return result;
	}

	@Override
	public Case getCaseData(ICaseDAOConnection dao,
			String participantToken, String caseType, String caseId) throws AuthException, UnavailableTaskException {
		UserRoles credentials = this.getUserRoles(participantToken);
        Case result = dao.fetchAllCaseData(credentials, caseType,caseId);

        return result;
	}
}
