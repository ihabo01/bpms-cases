/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: CASEManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.acm.server;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMNamespace;

public final class CaseXMLConstants {

    public static final String CASE_NAMESPACE = "http://www.intalio.com/BPMS/ACMServices-2015/";

    public static final String CASE_NAMESPACE_PREFIX = "tms";
    
    public static final String CASE_LOCAL_NAME = "CASE";
    
    public static final String CASE_TYPE_LOCAL_NAME = "CaseType";
    
    public final static OMNamespace CASE_OM_NAMESPACE = OMAbstractFactory.getOMFactory().createOMNamespace(
            CASE_NAMESPACE, 
            CASE_NAMESPACE_PREFIX);
    
    public static final QName CASE_QNAME = new QName(CASE_NAMESPACE, CASE_LOCAL_NAME);
    


}
