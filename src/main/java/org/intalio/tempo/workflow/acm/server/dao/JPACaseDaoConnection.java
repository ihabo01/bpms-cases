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

import javax.persistence.EntityManager;
import org.intalio.tempo.acm.server.CaseType;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.dao.AbstractJPAConnection;
import org.intalio.tempo.workflow.util.jpa.AttachmentFetcher;
import org.intalio.tempo.workflow.util.jpa.TaskFetcher;

/**
 * Persistence for task using JPA.
 */
public class JPACaseDaoConnection extends AbstractJPAConnection implements ICaseDAOConnection {

    private TaskFetcher _fetcher;
    private AttachmentFetcher _attachmentFetcher;

    public JPACaseDaoConnection(EntityManager createEntityManager) {
        super(createEntityManager);
        _fetcher = new TaskFetcher(createEntityManager);
        _attachmentFetcher = new AttachmentFetcher( createEntityManager );
    }

	@Override
	public CaseType[] fetchAllCaseTypes(UserRoles user) {
		// TODO Auto-generated method stub
		return null;
	}

    
}
