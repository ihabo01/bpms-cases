package org.intalio.tempo.acm.server;

import javax.xml.namespace.QName;

import org.intalio.tempo.workflow.task.xml.TaskXMLConstants;

public interface ACMConstants {
    public static final QName INVALID_INPUT_FORMAT = new QName(CaseXMLConstants.CASE_NAMESPACE, "invalidInputMessageFault", CaseXMLConstants.CASE_NAMESPACE_PREFIX);
    public static final QName UNAVAILABLE_TASK = new QName(CaseXMLConstants.CASE_NAMESPACE, "unavailableTaskFault", CaseXMLConstants.CASE_NAMESPACE_PREFIX);
    public static final QName UNAVAILABLE_ATTACHMENT = new QName(CaseXMLConstants.CASE_NAMESPACE, "unavailableAttachmentFault", CaseXMLConstants.CASE_NAMESPACE_PREFIX);
    public static final QName INVALID_TOKEN = new QName(CaseXMLConstants.CASE_NAMESPACE, "invalidParticipantTokenFault", CaseXMLConstants.CASE_NAMESPACE_PREFIX);
    public static final QName ACCESS_DENIED = new QName(CaseXMLConstants.CASE_NAMESPACE, "accessDeniedFault", CaseXMLConstants.CASE_NAMESPACE_PREFIX);
}
