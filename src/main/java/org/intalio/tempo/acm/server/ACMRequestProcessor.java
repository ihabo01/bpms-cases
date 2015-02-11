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

import java.sql.Types;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.workflow.acm.server.dao.Case;
import org.intalio.tempo.workflow.acm.server.dao.CaseHistory;
import org.intalio.tempo.workflow.acm.server.dao.DBColumn;
import org.intalio.tempo.workflow.acm.server.dao.CaseList;
import org.intalio.tempo.workflow.acm.server.dao.ICaseDAOConnection;
import org.intalio.tempo.workflow.acm.server.dao.ICaseDAOConnectionFactory;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.tms.AccessDeniedException;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.intalio.tempo.workflow.util.xml.InvalidInputFormatException;
import org.intalio.tempo.workflow.util.xml.OMElementQueue;
import org.intalio.tempo.workflow.util.xml.OMMarshaller;
import org.intalio.tempo.workflow.util.xml.OMUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ACMRequestProcessor extends OMUnmarshaller {
	final static Logger _logger = LoggerFactory
			.getLogger(ACMRequestProcessor.class);

	private IACMServer _server;

	private static final OMFactory OM_FACTORY = OMAbstractFactory
			.getOMFactory();
	private ICaseDAOConnectionFactory _caseDAOFactory;

	public ACMRequestProcessor(ICaseDAOConnectionFactory caseDAOFactory) {
		super(CaseXMLConstants.CASE_NAMESPACE,
				CaseXMLConstants.CASE_NAMESPACE_PREFIX);
		assert caseDAOFactory != null : "ICaseDAOConnectionFactory implementation is absent!";
		_logger.debug("Created CaseRequestProcessor");
		setCaseDAOFactory(caseDAOFactory);
	}

	public void setServer(IACMServer server) {
		// if (_registerPipa != null) {
		// _registerPipa.destroy();
		// }

		_logger.debug("ACMRequestProcessor.setServer:"
				+ server.getClass().getSimpleName());
		_server = server;

	}

	/*
	 * JIRA WF-1466 Changes have been made open the dao connection in
	 * TMSRequestProcessor instead of creating and opening connections in the
	 * TMSServer
	 */
	public void setCaseDAOFactory(ICaseDAOConnectionFactory caseDAOFactory) {
		this._caseDAOFactory = caseDAOFactory;
		_logger.info("ITaskDAOConnectionFactory implementation : "
				+ _caseDAOFactory.getClass());
	}

	// unify desctroy pipa behaviour make it easy to be covered by test.

	public void clearCache(final OMElement requestElement) {
		if (_caseDAOFactory != null) {
			_caseDAOFactory.clearCache();
		}
	}

	public OMElement getCaseTypeList(final OMElement requestElement)
			throws AxisFault {
		ICaseDAOConnection dao = null;
		try {
			dao = _caseDAOFactory.openConnection();
			OMElementQueue rootQueue = new OMElementQueue(requestElement);
			String participantToken = requireElementValue(rootQueue,
					"participantToken");
			final UserRoles user = _server.getUserRoles(participantToken);
			CaseType[] cases = _server.getCaseTypeList(dao, participantToken);
			return marshalCaseTypeList(user, cases, "getCaseTypeListResponse");
		} catch (Exception e) {
			throw makeFault(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	public OMElement getCaseTypeById(final OMElement requestElement)
			throws AxisFault {
		ICaseDAOConnection dao = null;
		try {
			dao = _caseDAOFactory.openConnection();
			OMElementQueue rootQueue = new OMElementQueue(requestElement);
			String participantToken = requireElementValue(rootQueue,
					"participantToken");
			String caseTypeId= requireElementValue(rootQueue,
					"caseType");
			final UserRoles user = _server.getUserRoles(participantToken);
			CaseType caseType = _server.getCaseTypeById(dao, participantToken,caseTypeId);
			return marshalCaseTypeById(user, caseType, "getCaseTypeByIdResponse");
			
		} catch (Exception e) {
			throw makeFault(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	public OMElement getCasesByType(final OMElement requestElement)
			throws AxisFault {
		ICaseDAOConnection dao = null;
		try {
			dao = _caseDAOFactory.openConnection();
			OMElementQueue rootQueue = new OMElementQueue(requestElement);
			String participantToken = requireElementValue(rootQueue,
					"participantToken");
			String caseType = requireElementValue(rootQueue, "caseType");
			final UserRoles user = _server.getUserRoles(participantToken);
			CaseList cases = _server.getCasesByType(dao, participantToken,
					caseType);
			return marshalCaseList(user, cases, "getCasesByTypeResponse");
		} catch (Exception e) {
			throw makeFault(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	public OMElement getCaseData(final OMElement requestElement)
			throws AxisFault {
		ICaseDAOConnection dao = null;
		try {
			dao = _caseDAOFactory.openConnection();
			OMElementQueue rootQueue = new OMElementQueue(requestElement);
			String participantToken = requireElementValue(rootQueue,
					"participantToken");
			String caseType = requireElementValue(rootQueue, "caseType");
			String caseId = requireElementValue(rootQueue, "caseId");
			final UserRoles user = _server.getUserRoles(participantToken);
			
			Case caseData = _server.getCaseData(dao, participantToken,
					caseType, caseId);
			return marshalCaseData(user, caseData, "getCaseDataResponse");
		} catch (Exception e) {
			throw makeFault(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	public OMElement getCaseHistory(final OMElement requestElement)
			throws AxisFault {
		ICaseDAOConnection dao = null;
		try {
			dao = _caseDAOFactory.openConnection();
			OMElementQueue rootQueue = new OMElementQueue(requestElement);
			String participantToken = requireElementValue(rootQueue,
					"participantToken");
			String caseType = requireElementValue(rootQueue, "caseType");
			String caseId = requireElementValue(rootQueue, "caseId");
			final UserRoles user = _server.getUserRoles(participantToken);
			CaseHistory history = _server.getCaseHistory(dao, participantToken,
					caseType, caseId);
			return marshalCaseHistory(user, history, "getCaseHistoryResponse");
		} catch (Exception e) {
			throw makeFault(e);
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	private OMElement marshalCaseHistory(UserRoles user,
			final CaseHistory history, final String responseTag) {
		OMElement response = new CaseResponseMarshaller(OM_FACTORY) {
			public OMElement marshalResponse(CaseHistory caseList) {
				if (_logger.isDebugEnabled())
					_logger.debug("Marshalling case history");
				List<DBColumn> columns = history.getColumns();
				OMElement response = createElement(responseTag);
				OMElement columnsElement = createElement("columns");
				for (DBColumn column : history.getColumns()) {
					OMElement columnElement = createElement("column");
					OMElement columnNameElement = createElement("name");
					columnNameElement.setText(column.getName());
					OMElement columnTypeElement = createElement("type");
					columnTypeElement.setText(getSqlTypeName(column.getType()));
					columnElement.addChild(columnNameElement);
					columnElement.addChild(columnTypeElement);
					columnsElement.addChild(columnElement);
					;

				}

				response.addChild(columnsElement);
				OMElement caseHistory = createElement("caseHistory");
				for (List<Object> caseUnit : history.getHistoryEntries()) {
					OMElement historyEntryElement = createElement("historyEntry");
					int attributeNum = 0;
					for (Object caseAttribute : caseUnit) {
						OMElement historyAttribute = createElement(columns.get(
								attributeNum).getName());

						if (caseAttribute != null)
							historyAttribute.setText(caseAttribute.toString());
						historyEntryElement.addChild(historyAttribute);
						attributeNum++;
					}
					caseHistory.addChild(historyEntryElement);
					;

				}
				response.addChild(caseHistory);
				if (_logger.isDebugEnabled())
					_logger.debug("Returning marshalled response");
				return response;
			}
		}.marshalResponse(history);
		if (_logger.isDebugEnabled())
			_logger.debug(response.toString());
		return response;
	}

	private OMElement marshalCaseList(UserRoles user, final CaseList casesList,
			final String responseTag) {
		OMElement response = new CaseResponseMarshaller(OM_FACTORY) {
			public OMElement marshalResponse(CaseList caseList) {
				if (_logger.isDebugEnabled())
					_logger.debug("Marshalling cases");
				List<DBColumn> columns = casesList.getColumns();
				OMElement response = createElement(responseTag);
				OMElement columnsElement = createElement("columns");
				for (DBColumn column : casesList.getColumns()) {
					OMElement columnElement = createElement("column");
					OMElement columnNameElement = createElement("name");
					columnNameElement.setText(column.getName());
					OMElement columnTypeElement = createElement("type");
					columnTypeElement.setText(getSqlTypeName(column.getType()));
					columnElement.addChild(columnNameElement);
					columnElement.addChild(columnTypeElement);
					columnsElement.addChild(columnElement);
					;

				}

				response.addChild(columnsElement);
				OMElement casesElement = createElement("cases");
				for (List<Object> caseUnit : casesList.getCases()) {
					OMElement caseElement = createElement("case");
					int attributeNum = 0;
					for (Object caseAttribute : caseUnit) {
						OMElement caseAttributeElement = createElement(columns
								.get(attributeNum).getName());

						if (caseAttribute != null)
							caseAttributeElement.setText(caseAttribute
									.toString());
						caseElement.addChild(caseAttributeElement);
						attributeNum++;
					}
					casesElement.addChild(caseElement);
					;

				}
				response.addChild(casesElement);
				if (_logger.isDebugEnabled())
					_logger.debug("Returning marshalled response");
				return response;
			}
		}.marshalResponse(casesList);
		if (_logger.isDebugEnabled())
			_logger.debug(response.toString());
		return response;
	}

	private OMElement marshalCaseData(UserRoles user, final Case caseData,
			final String responseTag) {
		OMElement response = new CaseResponseMarshaller(OM_FACTORY) {
			public OMElement marshalResponse(Case caseData) {
				if (_logger.isDebugEnabled())
					_logger.debug("Marshalling case");
				List<DBColumn> columns = caseData.getColumns();
				OMElement response = createElement(responseTag);
				OMElement columnsElement = createElement("columns");
				for (DBColumn column : caseData.getColumns()) {
					OMElement columnElement = createElement("column");
					OMElement columnNameElement = createElement("name");
					columnNameElement.setText(column.getName());
					OMElement columnTypeElement = createElement("type");
					columnTypeElement.setText(getSqlTypeName(column.getType()));
					columnElement.addChild(columnNameElement);
					columnElement.addChild(columnTypeElement);
					columnsElement.addChild(columnElement);
					;

				}

				response.addChild(columnsElement);

				List caseUnit = caseData.getCaseData();
				OMElement caseElement = createElement("case");
				int attributeNum = 0;
				for (Object caseAttribute : caseUnit) {
					OMElement caseAttributeElement = createElement(columns.get(
							attributeNum).getName());

					if (caseAttribute != null)
						caseAttributeElement.setText(caseAttribute.toString());
					caseElement.addChild(caseAttributeElement);
					attributeNum++;
				}

				response.addChild(caseElement);
				if (_logger.isDebugEnabled())
					_logger.debug("Returning marshalled response");
				return response;
			}
		}.marshalResponse(caseData);
		if (_logger.isDebugEnabled())
			_logger.debug(response.toString());
		return response;
	}

	public static String getSqlTypeName(int type) {
		switch (type) {
		case Types.BIT:
			return "BIT";
		case Types.TINYINT:
			return "TINYINT";
		case Types.SMALLINT:
			return "SMALLINT";
		case Types.INTEGER:
			return "INTEGER";
		case Types.BIGINT:
			return "BIGINT";
		case Types.FLOAT:
			return "FLOAT";
		case Types.REAL:
			return "REAL";
		case Types.DOUBLE:
			return "DOUBLE";
		case Types.NUMERIC:
			return "NUMERIC";
		case Types.DECIMAL:
			return "DECIMAL";
		case Types.CHAR:
			return "CHAR";
		case Types.VARCHAR:
			return "VARCHAR";
		case Types.LONGVARCHAR:
			return "LONGVARCHAR";
		case Types.DATE:
			return "DATE";
		case Types.TIME:
			return "TIME";
		case Types.TIMESTAMP:
			return "TIMESTAMP";
		case Types.BINARY:
			return "BINARY";
		case Types.VARBINARY:
			return "VARBINARY";
		case Types.LONGVARBINARY:
			return "LONGVARBINARY";
		case Types.NULL:
			return "NULL";
		case Types.OTHER:
			return "OTHER";
		case Types.JAVA_OBJECT:
			return "JAVA_OBJECT";
		case Types.DISTINCT:
			return "DISTINCT";
		case Types.STRUCT:
			return "STRUCT";
		case Types.ARRAY:
			return "ARRAY";
		case Types.BLOB:
			return "BLOB";
		case Types.CLOB:
			return "CLOB";
		case Types.REF:
			return "REF";
		case Types.DATALINK:
			return "DATALINK";
		case Types.BOOLEAN:
			return "BOOLEAN";
		case Types.ROWID:
			return "ROWID";
		case Types.NCHAR:
			return "NCHAR";
		case Types.NVARCHAR:
			return "NVARCHAR";
		case Types.LONGNVARCHAR:
			return "LONGNVARCHAR";
		case Types.NCLOB:
			return "NCLOB";
		case Types.SQLXML:
			return "SQLXML";
		}

		return "?";
	}

	/**
	 * This is used in both <code>getAvailableTasks</code> and
	 * <code>getTaskList</code>
	 */
	private OMElement marshalCaseTypeList(final UserRoles user,
			final CaseType[] caseTypes, final String responseTag) {
		OMElement response = new CaseResponseMarshaller(OM_FACTORY) {
			public OMElement marshalResponse(CaseType[] caseTypes) {
				if (_logger.isDebugEnabled())
					_logger.debug("Marshalling tasks");
				OMElement response = createElement(responseTag);
				for (CaseType caseType : caseTypes) {
					try {
						OMElement caseTypeElement = createElement("CaseType");

						OMElement caseTypeIDElement = createElement("ID");
						caseTypeIDElement.setText(caseType.getID());
						caseTypeElement.addChild(caseTypeIDElement);

						OMElement caseTypeDescriptionElement = createElement("description");
						caseTypeDescriptionElement.setText(caseType
								.getDescription());
						caseTypeElement.addChild(caseTypeDescriptionElement);
						OMElement statusDiagramElement = createElement("status_diagram");
						statusDiagramElement.setText(caseType
								.getStatus_diagram());
						;
						caseTypeElement.addChild(statusDiagramElement);
						
						OMElement caseFormElement = createElement("case_form");
						caseFormElement.setText(caseType
								.getCase_form());
						;
						caseTypeElement.addChild(caseFormElement);
						response.addChild(caseTypeElement);
						;
					} catch (Exception e) {
						// marshalling of that task failed.
						// let's not fail fast, but provide info in the logs.
						_logger.error(caseType.getID()
								+ "could not be serialized to xml", e);
					}
				}
				if (!response.getChildren().hasNext())
					response.setText("none");
				if (_logger.isDebugEnabled())
					_logger.debug("Returning marshalled response");
				return response;
			}
		}.marshalResponse(caseTypes);
		if (_logger.isDebugEnabled())
			_logger.debug(response.toString());
		return response;
	}
	
	
	private OMElement marshalCaseTypeById(final UserRoles user,
			final CaseType caseType, final String responseTag) {
		OMElement response = new CaseResponseMarshaller(OM_FACTORY) {
			public OMElement marshalResponse(CaseType caseType) {
				if (_logger.isDebugEnabled())
					_logger.debug("Marshalling tasks");
				
				OMElement response = createElement(responseTag);
				
					try {
						OMElement caseTypeElement = createElement("CaseType");

						OMElement caseTypeIDElement = createElement("ID");
						caseTypeIDElement.setText(caseType.getID());
						caseTypeElement.addChild(caseTypeIDElement);

						OMElement caseTypeDescriptionElement = createElement("description");
						caseTypeDescriptionElement.setText(caseType
								.getDescription());
						caseTypeElement.addChild(caseTypeDescriptionElement);
						OMElement statusDiagramElement = createElement("status_diagram");
						statusDiagramElement.setText(caseType
								.getStatus_diagram());
						;
						caseTypeElement.addChild(statusDiagramElement);
						
						OMElement caseFormElement = createElement("case_form");
						caseFormElement.setText(caseType
								.getCase_form());
						;
						caseTypeElement.addChild(caseFormElement);
						response.addChild(caseTypeElement);
						;
					} catch (Exception e) {
						// marshalling of that task failed.
						// let's not fail fast, but provide info in the logs.
						_logger.error(caseType.getID()
								+ "could not be serialized to xml", e);
					}
				
				
				if (_logger.isDebugEnabled())
					_logger.debug("Returning marshalled response");
				return response;
			}
		}.marshalResponse(caseType);
		if (_logger.isDebugEnabled())
			_logger.debug(response.toString());
		return response;
	}
	
	private AxisFault makeFault(Exception e) {
		if (e instanceof ACMException
				|| e instanceof InvalidInputFormatException) {
			if (_logger.isDebugEnabled())
				_logger.debug(e.getMessage(), e);
			OMElement response = null;
			if (e instanceof InvalidInputFormatException)
				response = OM_FACTORY
						.createOMElement(ACMConstants.INVALID_INPUT_FORMAT);
			else if (e instanceof AccessDeniedException)
				response = OM_FACTORY
						.createOMElement(ACMConstants.ACCESS_DENIED);
			else if (e instanceof UnavailableTaskException)
				response = OM_FACTORY
						.createOMElement(ACMConstants.UNAVAILABLE_TASK);
			else if (e instanceof UnavailableAttachmentException)
				response = OM_FACTORY
						.createOMElement(ACMConstants.UNAVAILABLE_ATTACHMENT);
			else if (e instanceof AuthException)
				response = OM_FACTORY
						.createOMElement(ACMConstants.INVALID_TOKEN);

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

	private abstract class CaseResponseMarshaller extends OMMarshaller {
		public CaseResponseMarshaller(OMFactory omFactory) {
			super(omFactory, omFactory.createOMNamespace(
					CaseXMLConstants.CASE_NAMESPACE,
					CaseXMLConstants.CASE_NAMESPACE_PREFIX));
		}
	}

	/**
	 * function to truncate time from date.
	 * 
	 * @param date
	 *            with time
	 * @return date Date without time
	 */

	public String checkForEmptyString(String val) {
		return "".equals(val) ? null : val;
	}

}
