/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.acm.server;



import org.intalio.tempo.workflow.acm.server.dao.ICaseDAOConnection;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.UserRoles;





//Added ITaskDAOConnection in every method signature for JIRA WF-1466
public interface IACMServer {

    CaseType[] getCaseTypeList(ICaseDAOConnection dao,String participantToken) throws ACMException, AuthException;

	UserRoles getUserRoles(String participantToken) throws AuthException;


}