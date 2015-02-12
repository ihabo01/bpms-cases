/* place JavaScript code here */
/* place JavaScript code here */



var personOnLeave;
var autofillbtn;
var form=SubmitAbsenceRequest;

function autofill() {
    var relationRadio = form.getJSXByName('Myself');
    relationRadio.setSelected(jsx3.gui.RadioButton.SELECTED);
    com.intalio.ria.validate(relationRadio,jsx3.gui.Event.SELECT);
    selfForm();

    var phone = form.getJSXByName('Phone');
    var code = phone.getChild(0);
    var pnumber = phone.getChild(1);
    code.setValue('+1');
    pnumber.setValue('5961800');
    com.intalio.ria.validate(code,jsx3.gui.Event.SELECT);
    com.intalio.ria.validate(pnumber,jsx3.gui.Event.SELECT);
    var email = form.getJSXByName('email');
    email.setValue('admin@example.com');
    com.intalio.ria.validate(email,jsx3.gui.Event.SELECT);
    form.getJSXByName('comments').setValue('Sample Data');

    var substitutename = form.getJSXByName('substitutename');
    substitutename.setValue('Emily Williams');
    com.intalio.ria.validate(substitutename,jsx3.gui.Event.CHANGE);

    var phoneWhileAway = form.getJSXByName('PhoneWhileAway-field');
    var codeWhileAway = phoneWhileAway.getChild(0);
    var pnumberWhileAway = phoneWhileAway.getChild(1);
    codeWhileAway.setValue('+1');
    pnumberWhileAway.setValue('5961900');
    com.intalio.ria.validate(codeWhileAway,jsx3.gui.Event.SELECT);
    com.intalio.ria.validate(pnumberWhileAway,jsx3.gui.Event.SELECT);
    var emailWhileAway = form.getJSXByName('emailWhileAway');
    emailWhileAway.setValue('ewilliams@examples.intalio.com');
    com.intalio.ria.validate(emailWhileAway,jsx3.gui.Event.SELECT);

    var LeaveList = form.getJSXByName('LeaveList');
    var objRecord = new Object();
    objRecord.jsxid = jsx3.CDF.getKey();

    var fromDate = new Date();
    fromDate.setDate(fromDate.getDate() + 1);

    var toDate = new Date()
    toDate.setDate(toDate.getDate() + 3);

    objRecord.from = fromDate.valueOf();
    objRecord.to = toDate.valueOf();
    objRecord.leaveType = "PTO";
    objRecord.shortComment = 'Sample data';
    LeaveList.insertRecord(objRecord, null, true);
    LeaveList.getParent().getParent().validate(LeaveList, jsx3.gui.Interactive.AFTER_COMMIT);
    LeaveList.repaintData();

    autofillbtn.setEnabled(jsx3.gui.Form.STATEDISABLED,true);
}


function init(){
personOnLeave=form.getJSXByName("PersonOnLeave");
autofillbtn = form.getJSXByName('autofillbtn');
autofillbtn.setEnabled(jsx3.gui.Form.STATEENABLED,true);
form.getJSXByName("reporterid-map").setText(Intalio.Internal.Utilities.getUser());
form.getJSXByName("from").allowDate=futureDate(new Date());
form.getJSXByName("to").allowDate=futureDate(new Date());
eg.service.callgetUserProperties();
eg.service.callgetUsers();
getFormlocation();
}

function futureDate(now){
return function futureDate(y,m,d){

var yearnow=now.getFullYear();
var monthnow=now.getMonth();
var daynow=now.getDate();

if (y>yearnow || (y==yearnow & (m > monthnow || (m==monthnow & d>=daynow)) )) return true;
else return false;

}
}

function resizeLeaveList(){
var leaveList=form.getJSXByName("LeaveList");
//jsx3.util.Logger.doLog(leaveList.getRowHeight());
//alert(form.getJSXByName("LeaveList").getRecordIds().leng);
var newHeight=(leaveList.getRecordIds().length+1)*leaveList.getRowHeight()+leaveList.getHeaderHeight();
leaveList.getParent().setHeight(newHeight).repaint();
leaveList.setHeight(newHeight).repaint();
leaveList.getParent().repaint();
}
function selfForm(){

personOnLeave.setEnabled(jsx3.gui.Form.STATEDISABLED);
form.getJSXByName("Reporter-field").setDisplay(jsx3.gui.Block.DISPLAYNONE, true);
var user=form.getJSXByName("reporterid-map").getText();
personOnLeave.setValue(user);

personOnLeave.repaint();
}
function otherForm(){
form.getJSXByName("Reporter-field").setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
personOnLeave.setEnabled(jsx3.gui.Form.STATEENABLED);
personOnLeave.setValue("");
personOnLeave.repaint();
}
jsx3.lang.Package.definePackage(
  "eg.service",                //the full name of the package to create
  function(service) {          //name the argument of this function

    //call this method to begin the service call (eg.service.callgetUserProperties();)
    service.callgetUserProperties = function() {
      var objService = SubmitAbsenceRequest.loadResource("getUserName");
      objService.setOperation("getUserProperties");

      //subscribe
      objService.subscribe(jsx3.net.Service.ON_SUCCESS, service.ongetUserPropertiesSuccess);
      objService.subscribe(jsx3.net.Service.ON_ERROR, service.ongetUserPropertiesError);
      objService.subscribe(jsx3.net.Service.ON_INVALID, service.ongetUserPropertiesInvalid);

      //PERFORMANCE ENHANCEMENT: uncomment the following line of code to use XSLT to convert the server response to CDF (refer to the API docs for jsx3.net.Service.compile for implementation details)
      //objService.compile();
      
      //update endpoint
      var endpoint = objService.getEndpointURL();
      var formlocation = getFormlocation();
      var newendpoint = formLocation + endpoint.substring(endpoint.indexOf('/axis2/'));
      objService.setEndpointURL(newendpoint);
      
      //call the service
      objService.doCall();
    };

    service.ongetUserPropertiesSuccess = function(objEvent) {
      //var responseXML = objEvent.target.getInboundDocument();
    // objEvent.target.getServer().alert("Success","The service call was successful.");
    selfForm();
    };

    service.ongetUserPropertiesError = function(objEvent) {
      var myStatus = objEvent.target.getRequest().getStatus();
      objEvent.target.getServer().alert("Error","The service call failed. The HTTP Status code is: " + myStatus);
    };

    service.ongetUserPropertiesInvalid = function(objEvent) {
      objEvent.target.getServer().alert("Invalid","The following message node just failed validation:\n\n" + objEvent.message);
    };

  }
);

jsx3.lang.Package.definePackage(
  "eg.service",                //the full name of the package to create
  function(service) {          //name the argument of this function

    //call this method to begin the service call (eg.service.callgetUsers();)
    service.callgetUsers = function() {
      var objService = SubmitAbsenceRequest.loadResource("usersList");
      objService.setOperation("getUsers");

      //subscribe
      objService.subscribe(jsx3.net.Service.ON_SUCCESS, service.ongetUsersSuccess);
      objService.subscribe(jsx3.net.Service.ON_ERROR, service.ongetUsersError);
      objService.subscribe(jsx3.net.Service.ON_INVALID, service.ongetUsersInvalid);

      //PERFORMANCE ENHANCEMENT: uncomment the following line of code to use XSLT to convert the server response to CDF (refer to the API docs for jsx3.net.Service.compile for implementation details)
      //objService.compile();
      
      //update endpoint
      var endpoint = objService.getEndpointURL();
      var formlocation = getFormlocation();
      var newendpoint = formLocation + endpoint.substring(endpoint.indexOf('/axis2/'));
      objService.setEndpointURL(newendpoint);
      
      //call the service
      objService.doCall();
    };

    service.ongetUsersSuccess = function(objEvent) {
      //var responseXML = objEvent.target.getInboundDocument();
      //objEvent.target.getServer().alert("Success","The service call was successful.");
      personOnLeave.setValue(Intalio.Internal.Utilities.getUser());
    };

    service.ongetUsersError = function(objEvent) {
      var myStatus = objEvent.target.getRequest().getStatus();
      objEvent.target.getServer().alert("Error","The service call failed. The HTTP Status code is: " + myStatus);
    };

    service.ongetUsersInvalid = function(objEvent) {
      objEvent.target.getServer().alert("Invalid","The following message node just failed validation:\n\n" + objEvent.message);
    };

  }
);

function getFormlocation(){
    var str = document.URL
    formLocation = str.substring(0,str.indexOf("/gi/apppath"));
    return formLocation;
};




