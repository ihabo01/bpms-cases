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

package org.intalio.tempo.cases.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.intalio.tempo.acm.server.IACMServer;
import org.intalio.tempo.acm.server.ACMRequestProcessor;
import org.intalio.tempo.workflow.acm.server.dao.JPACaseDaoConnectionFactory;
import org.intalio.tempo.workflow.acm.server.dao.SimpleTaskDAOConnectionFactory;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.tms.server.permissions.TaskPermissions;
import org.junit.Assert;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CasesRequestProcessorTest extends TestCase {

  private static final Logger _logger = LoggerFactory.getLogger(CasesRequestProcessorTest.class);

  private ACMRequestProcessor createRequestProcessor() throws Exception {
    IACMServer server = Utils.createTMSServer();
    ACMRequestProcessor proc = new ACMRequestProcessor(new SimpleTaskDAOConnectionFactory());
    proc.setServer(server);
    return proc;
  }

  private ACMRequestProcessor createRequestProcessorJPA() throws Exception {
    IACMServer server = Utils.createTMSServerJPA();
    ACMRequestProcessor proc = new ACMRequestProcessor(new JPACaseDaoConnectionFactory());
    proc.setServer(server);
    return proc;
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(CasesRequestProcessorTest.class);
  }

  public void setUp() {
      System.setProperty("org.intalio.tempo.configDirectory",
              "src/test/resources/");
  }

  public void testCreateAndGetTaskList() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
    OMElement createTaskResponse = requestProcessor.create(createTaskRequest);
    _logger.debug(Utils.toPrettyXML(createTaskResponse));

    OMElement getTaskListRequest = Utils.loadElementFromResource("/getTaskListRequest1.xml");
    OMElement getTaskListResponse = requestProcessor.getTaskList(getTaskListRequest);
    _logger.debug(Utils.toPrettyXML(getTaskListResponse));
    

  }

  public void testGetAvailableTasks() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessorJPA();

    OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
    OMElement createTaskResponse = requestProcessor.create(createTaskRequest);
    OMElement createTaskRequest2 = Utils.loadElementFromResource("/createTaskRequest2.xml");
    OMElement createTaskResponse2 = requestProcessor.create(createTaskRequest2);
    _logger.debug(Utils.toPrettyXML(createTaskResponse));
    _logger.debug(Utils.toPrettyXML(createTaskResponse2));

    OMElement getAvailableTasksRequest = Utils.loadElementFromResource("/getAvailableTasksRequest1.xml");
    OMElement getTaskListResponse = requestProcessor.getAvailableTasks(getAvailableTasksRequest);
    _logger.debug(Utils.toPrettyXML(getTaskListResponse));
  }

  public void testClearCache() throws Exception{
	  ACMRequestProcessor requestProcessor = this.createRequestProcessorJPA();	  
	  //Creating task
	  OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
	  requestProcessor.create(createTaskRequest);	  
	  
	  OMElement getTaskRequest = Utils.loadElementFromResource("/getTaskRequest1.xml");
	  OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest);
	  _logger.debug(Utils.toPrettyXML(getTaskResponse1));
	  
	  //Update the task through db
	  String dbURL = "jdbc:derby:target/JPADB;create=true;user=APP;password=APP";
	  Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
	  Connection conn = DriverManager.getConnection(dbURL); 
	  Statement stmt = conn.createStatement();
	  stmt.execute("update tempo_task set description='I am a Good Test Task I Updated' where taskid='001_Create_Good_Test_Task'");
	  stmt.close();
	  conn.close();

	  OMElement getTaskResponse2 = requestProcessor.getTask(getTaskRequest);
	  _logger.debug(Utils.toPrettyXML(getTaskResponse2));
	  //It'll not show the updated description due to cache.
	  assertFalse(getTaskResponse2.toString().contains("I am a Good Test Task I Updated"));
	  
	  //Clear cache
	  OMElement clearCacheRequest = Utils.loadElementFromResource("/clearCacheRequest.xml");
	  requestProcessor.clearCache(clearCacheRequest);
	  
	  OMElement getTaskResponse3 = requestProcessor.getTask(getTaskRequest);
	  _logger.debug(Utils.toPrettyXML(getTaskResponse3));
	  //It'll show the updated description as cache is updated.
	  assertTrue(getTaskResponse3.toString().contains("I am a Good Test Task I Updated"));
  }
  
  public void testGetTask() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
    requestProcessor.create(createTaskRequest);

    OMElement getTaskRequest1 = Utils.loadElementFromResource("/getTaskRequest1.xml");
    OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
    _logger.debug(Utils.toPrettyXML(getTaskResponse1));
  }

  public void testDelete() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest1 = Utils.loadElementFromResource("/createTaskRequest1.xml");
    requestProcessor.create(createTaskRequest1);
    OMElement createTaskRequest2 = Utils.loadElementFromResource("/createTaskRequest2.xml");
    requestProcessor.create(createTaskRequest2);

    OMElement deleteRequest = Utils.loadElementFromResource("/deleteRequest1.xml");
    OMElement deleteResponse = requestProcessor.delete(deleteRequest);
    _logger.debug(Utils.toPrettyXML(deleteResponse));
  }

  public void testDeleteMultiple() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest1 = Utils.loadElementFromResource("/createTaskRequest1.xml");
    requestProcessor.create(createTaskRequest1);
    OMElement createTaskRequest2 = Utils.loadElementFromResource("/createTaskRequest2.xml");
    requestProcessor.create(createTaskRequest2);

    try {
      OMElement deleteRequest = Utils.loadElementFromResource("/deleteRequestMultiple.xml");
      OMElement deleteResponse = requestProcessor.delete(deleteRequest);
      _logger.debug(Utils.toPrettyXML(deleteResponse));
      fail("Should have failed");
    } catch (AxisFault e) {
      _logger.debug(e.getMessage());
    }
  }

  public void testPermissions() throws Exception {
      Map<String, Set<String>> bindIconSetToRole = new HashMap<String, Set<String>>();
      Set<String> deletePerms = new HashSet<String>();
      deletePerms.add("intalio\\admin");
      bindIconSetToRole.put("administrator", deletePerms);
      Set<String> createPerms = new HashSet<String>();
      createPerms.add("just\\me");
      bindIconSetToRole.put("manager", createPerms);
      
      Map<String, Map<String, Set<String>>> toolbarIconSets = new HashMap<String, Map<String, Set<String>>>();
      Set<String> iconSet = new HashSet<String>();
      iconSet.add("delete");
      Map<String, Set<String>> toolBarIconMap = new HashMap<String, Set<String>>();
      toolBarIconMap.put("task", iconSet);
      toolbarIconSets.put("administrator", toolBarIconMap);

      TaskPermissions tp = new TaskPermissions(toolbarIconSets, bindIconSetToRole);
      
      UserRoles ur = new UserRoles("Matthieu", new AuthIdentifierSet(new String[]{"intalio\\admin", "intalio\\tester"}));
      UserRoles ur2 = new UserRoles("Niko", new AuthIdentifierSet(new String[]{"intalio\\guru"}));
      UserRoles ur3 = new UserRoles("just\\me", new AuthIdentifierSet(new String[]{"intalio\\guru"}));

      Assert.assertTrue(tp.isAuthorized("delete", new PIPATask(), ur));
      Assert.assertFalse(tp.isAuthorized("delete", new PIPATask(), ur2));
      Assert.assertFalse(tp.isAuthorized("delete", new PIPATask(), ur3));
  }

  public void testSetOutput() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
    requestProcessor.create(createTaskRequest);

    OMElement setOutputRequest = Utils.loadElementFromResource("/setOutputRequest1.xml");
    requestProcessor.setOutput(setOutputRequest);

    OMElement getTaskRequest1 = Utils.loadElementFromResource("/getTaskRequest1.xml");
    OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
    _logger.debug(Utils.toPrettyXML(getTaskResponse1));
  }

  public void testComplete() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
    requestProcessor.create(createTaskRequest);

    OMElement completeRequest = Utils.loadElementFromResource("/completeRequest1.xml");
    requestProcessor.complete(completeRequest);

    OMElement getTaskRequest1 = Utils.loadElementFromResource("/getTaskRequest1.xml");
    OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
    _logger.debug(Utils.toPrettyXML(getTaskResponse1));
  }

  public void testSetOutputAndComplete() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
    requestProcessor.create(createTaskRequest);

    OMElement setOutputAndCompleteRequest = Utils.loadElementFromResource("/setOutputAndCompleteRequest1.xml");
    requestProcessor.setOutputAndComplete(setOutputAndCompleteRequest);

    OMElement getTaskRequest1 = Utils.loadElementFromResource("/getTaskRequest1.xml");
    OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
    _logger.debug(Utils.toPrettyXML(getTaskResponse1));
  }

  public void testFail() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
    requestProcessor.create(createTaskRequest);

    OMElement failRequest = Utils.loadElementFromResource("/failRequest1.xml");
    requestProcessor.fail(failRequest);

    OMElement getTaskRequest1 = Utils.loadElementFromResource("/getTaskRequest1.xml");
    OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
    _logger.debug(Utils.toPrettyXML(getTaskResponse1));
  }

  public void testAddGetAndRemoveAttachment() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
    requestProcessor.create(createTaskRequest);

    OMElement addAttachmentRequest = Utils.loadElementFromResource("/addAttachmentRequest1.xml");
    requestProcessor.addAttachment(addAttachmentRequest);

    OMElement getTaskRequest1 = Utils.loadElementFromResource("/getTaskRequest1.xml");
    OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
    _logger.debug(Utils.toPrettyXML(getTaskResponse1));

    OMElement getAttachmentsRequest1 = Utils.loadElementFromResource("/getAttachmentsRequest1.xml");
    OMElement getAttachmentsResponse1 = requestProcessor.getAttachments(getAttachmentsRequest1);
    _logger.debug(Utils.toPrettyXML(getAttachmentsResponse1));

    OMElement removeAttachmentRequest = Utils.loadElementFromResource("/removeAttachmentRequest1.xml");
    requestProcessor.removeAttachment(removeAttachmentRequest);

    OMElement getTaskResponse2 = requestProcessor.getTask(getTaskRequest1);
    _logger.debug(Utils.toPrettyXML(getTaskResponse2));
  }

  public void testMessedUpAttachmentMetadata() throws Exception {
    ACMRequestProcessor requestProcessor = this.createRequestProcessor();

    OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
    requestProcessor.create(createTaskRequest);

    OMElement addAttachmentRequest = Utils.loadElementFromResource("/addAttachmentRequest2.xml");
    requestProcessor.addAttachment(addAttachmentRequest);

    OMElement getTaskRequest1 = Utils.loadElementFromResource("/getTaskRequest1.xml");
    OMElement getTaskResponse1 = requestProcessor.getTask(getTaskRequest1);
    _logger.debug(Utils.toPrettyXML(getTaskResponse1));

    OMElement getAttachmentsRequest1 = Utils.loadElementFromResource("/getAttachmentsRequest1.xml");
    OMElement getAttachmentsResponse1 = requestProcessor.getAttachments(getAttachmentsRequest1);
    _logger.debug(Utils.toPrettyXML(getAttachmentsResponse1));
  }
  
//  public void testInitProcess() throws Exception{
//	    TMSRequestProcessor requestProcessor = this.createRequestProcessorJPA();
//	    _logger.info("================================");
//	    OMElement createTaskRequest = Utils.loadElementFromResource("/createPipaRequest.xml");
//	    OMElement createTaskResponse = requestProcessor.storePipa(createTaskRequest);
//	    _logger.debug(Utils.toPrettyXML(createTaskResponse));
//	    _logger.info("================================");

//	    //requestProcessor = this.createRequestProcessor();
//	  //OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
//	   createTaskRequest = Utils.loadElementFromResource("/initProcess.xml");
//	  _logger.debug(createTaskRequest.getText());
//	  //createTaskRequest = (OMElement)createTaskRequest.getChildrenWithLocalName("taskId").next();
//	  //createTaskRequest = createTaskRequest.getFirstElement().getFirstElement();
//	  _logger.debug("ele="+createTaskRequest.getLocalName());
//	  OMElement ret = requestProcessor.initProcess(createTaskRequest);
//	  _logger.debug(ret.getText());
//  }
  
  public void testGetAndDeletePipa() throws Exception{
      ACMRequestProcessor requestProcessor = this.createRequestProcessorJPA();
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
      OMElement deleteAllRequest = Utils.loadElementFromResource("/deleteAll.xml");
      OMElement deleteAllResponse = requestProcessor.deleteAll(deleteAllRequest);
      _logger.debug(Utils.toPrettyXML(deleteAllResponse));
      
//      OMElement getPipaequest = Utils.loadElementFromResource("/getPipa.xml");
//      OMElement getPipaResponse = requestProcessor.getPipa(getPipaequest);
//      System.out.println(Utils.toPrettyXML(getPipaResponse));
      
      OMElement getAvailableTasksRequest = Utils.loadElementFromResource("/getAvailableTasksRequest1.xml");
      OMElement getTaskListResponse = requestProcessor.getAvailableTasks(getAvailableTasksRequest);
      _logger.debug(Utils.toPrettyXML(getTaskListResponse));
      
      OMElement createTaskRequest = Utils.loadElementFromResource("/createPipaRequest.xml");
      OMElement createTaskResponse = requestProcessor.storePipa(createTaskRequest);
      
      OMElement getPipaequest = Utils.loadElementFromResource("/getPipa.xml");
      OMElement getPipaResponse = requestProcessor.getPipa(getPipaequest);
      _logger.debug(Utils.toPrettyXML(getPipaResponse));
      
      OMElement deletePipaRequest = Utils.loadElementFromResource("/deletePipa.xml");
      OMElement deletePipaResponse = requestProcessor.deletePipa(deletePipaRequest);
      _logger.debug(Utils.toPrettyXML(deletePipaResponse));
      
      
      try{
          getPipaResponse = null;
          getPipaResponse = requestProcessor.getPipa(getPipaequest);
          _logger.debug(Utils.toPrettyXML(getPipaResponse));
          throw new Exception("expect an IndexOutOfBoundsException, because pipa task is deleted ");
      }catch (AxisFault e){
          // here should assert receiving IndexOutOfBoundsException, but cannot do it with AxisFault
          //TestCase.assertTrue(e.getCause() instanceof java.lang.IndexOutOfBoundsException);
      }

      //_logger.debug(Utils.toPrettyXML(getPipaResponse));
  }
  
  public void testException()throws Exception{
      ACMRequestProcessor requestProcessor = this.createRequestProcessor();
      try{
          requestProcessor.getTask(null);
      }catch(Exception e){
          _logger.info("get exception from requestProcessor.getTask():"+ e.getMessage());
      }
      
      requestProcessor = this.createRequestProcessor();
      OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
      try{
          OMElement createTaskResponse = requestProcessor.create(createTaskRequest);
      }catch(Exception e){
          _logger.info("get exception from requestProcessor.create():"+ e.getMessage());
      }
      return;
  }
  
  public void testReassign()throws Exception{
      ACMRequestProcessor r = this.createRequestProcessor();
      
      OMElement createTaskRequest = Utils.loadElementFromResource("/createTaskRequest1.xml");
      OMElement createTaskResponse = r.create(createTaskRequest);
      _logger.debug(Utils.toPrettyXML(createTaskResponse));
      
      OMElement  requestElement = Utils.loadElementFromResource("/reassignRequest.xml");
      OMElement  responseElement = r.reassign(requestElement);
      _logger.debug(Utils.toPrettyXML(responseElement));
      
  }
  
 
}
