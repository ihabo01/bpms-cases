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
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.acm.server;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.workflow.acm.server.dao.ICaseDAOConnection;
import org.intalio.tempo.workflow.acm.server.dao.ICaseDAOConnectionFactory;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.TMSException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMElementQueue;
import org.intalio.tempo.workflow.util.xml.OMMarshaller;
import org.intalio.tempo.workflow.util.xml.OMUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ACMRequestProcessor extends OMUnmarshaller {
    final static Logger _logger = LoggerFactory.getLogger(ACMRequestProcessor.class);

    private IACMServer _server;


    private static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();
    private ICaseDAOConnectionFactory _caseDAOFactory;

    public ACMRequestProcessor(ICaseDAOConnectionFactory caseDAOFactory) {
        super(CaseXMLConstants.CASE_NAMESPACE, CaseXMLConstants.CASE_NAMESPACE_PREFIX);
        assert caseDAOFactory != null : "ICaseDAOConnectionFactory implementation is absent!";
         _logger.debug("Created CaseRequestProcessor");
         setCaseDAOFactory(caseDAOFactory);
    }

      /*JIRA WF-1466 Changes have been made open the dao connection in TMSRequestProcessor
       instead of creating and opening connections in the TMSServer */
      public void setCaseDAOFactory(ICaseDAOConnectionFactory caseDAOFactory) {
         this._caseDAOFactory = caseDAOFactory;
           _logger.info("ITaskDAOConnectionFactory implementation : " + _caseDAOFactory.getClass());
       }
      

    // unify desctroy pipa behaviour make it easy to be covered by test.



    
    public void clearCache(final OMElement requestElement){
    	if(_caseDAOFactory != null){
    		_caseDAOFactory.clearCache();
    	}
    }

    public OMElement getCaseTypeList(final OMElement requestElement) throws AxisFault {
    	ICaseDAOConnection dao=null;
    	try {
    		dao=_caseDAOFactory.openConnection();
            OMElementQueue rootQueue = new OMElementQueue(requestElement);
            String participantToken = requireElementValue(rootQueue, "participantToken");
            final UserRoles user = _server.getUserRoles(participantToken);
            CaseType[] cases = _server.getCaseTypeList(dao,participantToken);
            return marshalCaseTypeList(user, cases, "getCaseTypeListResponse");
        } catch (Exception e) {
            throw makeFault(e);
        }
        finally{
        	if(dao!=null)
        	dao.close();
        }
    }
    


    
    /**
     * This is used in both <code>getAvailableTasks</code> and
     * <code>getTaskList</code>
     */
    private OMElement marshalCaseTypeList(final UserRoles user,final CaseType[] caseTypes, final String responseTag) {
        OMElement response = new TMSResponseMarshaller(OM_FACTORY) {
            public OMElement marshalResponse(CaseType[] caseTypes) {
                if(_logger.isDebugEnabled())
                    _logger.debug("Marshalling tasks");
                OMElement response = createElement(responseTag);
                for (CaseType caseType : caseTypes) {
                    try {
                        OMElement caseTypeElement = createElement("CaseType");
                        response.addChild(caseTypeElement);;
                        OMElement caseTypeIDElement = createElement("ID");
                        caseTypeIDElement.setText(caseType.getID());
                        caseTypeElement.addChild(caseTypeIDElement);
                        
                    } catch (Exception e) {
                        // marshalling of that task failed.
                        // let's not fail fast, but provide info in the logs.
                        _logger.error(caseType.getID() + "could not be serialized to xml", e);
                    }
                }
                if(_logger.isDebugEnabled())
                    _logger.debug("Returning marshalled response");
                return response;
            }
        }.marshalResponse(caseTypes);
        if (_logger.isDebugEnabled())
            _logger.debug(response.toString());
        return response;
    }

    
    private AxisFault makeFault(Exception e) {
        if (e instanceof TMSException || e instanceof InvalidInputFormatException) {
            if (_logger.isDebugEnabled())
                _logger.debug(e.getMessage(), e);
            OMElement response = null;
            if (e instanceof InvalidInputFormatException)
                response = OM_FACTORY.createOMElement(TMSConstants.INVALID_INPUT_FORMAT);
            else if (e instanceof AccessDeniedException)
                response = OM_FACTORY.createOMElement(TMSConstants.ACCESS_DENIED);
            else if (e instanceof UnavailableTaskException)
                response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_TASK);
            else if (e instanceof UnavailableAttachmentException)
                response = OM_FACTORY.createOMElement(TMSConstants.UNAVAILABLE_ATTACHMENT);
            else if (e instanceof AuthException)
                response = OM_FACTORY.createOMElement(TMSConstants.INVALID_TOKEN);

            else
                return AxisFault.makeFault(e);

            response.setText(e.getMessage());
            AxisFault axisFault = new AxisFault(e.getMessage(), e);
            axisFault.setDetail(response);
            return axisFault;
        } else if (e instanceof AxisFault) {
            _logger.error(e.getMessage(), e);
            return (AxisFault) e;
        } else {
            _logger.error(e.getMessage(), e);
            return AxisFault.makeFault(e);
        }
    }

    

    protected void finalize() throws Throwable {

        super.finalize();
    }

    private abstract class TMSResponseMarshaller extends OMMarshaller {
        public TMSResponseMarshaller(OMFactory omFactory) {
            super(omFactory, omFactory.createOMNamespace(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX));
        }
    }

    /**
     * function to truncate time from date.
     * @param date with time
     * @return date Date without time
     */



    public String checkForEmptyString(String val) {
        return "".equals(val) ? null : val;
    }

	
}
