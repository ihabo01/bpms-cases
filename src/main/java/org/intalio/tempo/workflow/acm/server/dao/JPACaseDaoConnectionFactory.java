/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.intalio.tempo.workflow.acm.server.dao;

import java.util.Map;

import org.intalio.tempo.workflow.acm.server.dao.ICaseDAOConnection;
import org.intalio.tempo.workflow.acm.server.dao.ICaseDAOConnectionFactory;
import org.intalio.tempo.workflow.acm.server.dao.JPACaseDaoConnection;
import org.intalio.tempo.workflow.dao.AbstractJPAConnectionFactory;

/**
 * Factory for JPA-based task persistence
 */
public class JPACaseDaoConnectionFactory extends AbstractJPAConnectionFactory implements ICaseDAOConnectionFactory {

	public JPACaseDaoConnectionFactory() {
		super("org.intalio.tempo.acm");
	}

	public JPACaseDaoConnectionFactory(Map<String, Object> properties) {
		super("org.intalio.tempo.acm", properties);
	}

	public JPACaseDaoConnectionFactory(String tms, Map<String, Object> properties) {
		super(tms, properties);
	}

	public ICaseDAOConnection openConnection() {
		return new JPACaseDaoConnection(factory.createEntityManager());
	}
}