/* place JavaScript code here */
var caseData="hi";
jsx3.lang.Package.definePackage(
  "Intalio.Internal.Utilities",
  function(util) {
  
 util.initApp = function() {
 Intalio.Internal.RBACRoleService.callRBACService();
 acm.services.callgetCaseData();
 Intalio.Internal.Utilities.SERVER.publish({subject:Intalio.Internal.Utilities.GET_TASK_SUCCESS}); 

}

util.prefillCaseData = function(node){

caseData= node.getChildNodes().get(1).getChildNodes();
for(var i=0;i<caseData.getLength();i++ ) 

{
var childNode=caseData.get(i);
var name= childNode.getBaseName().toLowerCase();

var component=Intalio.Internal.Utilities.SERVER.getJSXByName(name);
console.log("mapping column" + name+" to component  with same name "+ component);
if(component){

if (component.setDate && Date.parse(childNode.getValue().replace(" ","T"))>0){

component.setDate(new Date(childNode.getValue().replace(" ","T")));

}
else if(component.setTitleText){
console.log("title text");
component.setTitleText(childNode.getValue());
}
else if(component.setValue){
component.setValue(childNode.getValue());
}
else if(component.setText ){
component.setText(childNode.getValue());
}


component.repaint();}
}
}


}


)


jsx3.lang.Package.definePackage(
  "acm.services",                //the full name of the package to create
  function(service) {          //name the argument of this function

    //call this method to begin the service call (eg.service.callgetCaseData();)
    service.callgetCaseData = function() {
      var objService = AbsenceRequest.loadResource("ACM-Service");
      objService.setOperation("getCaseData");

      //subscribe
      objService.subscribe(jsx3.net.Service.ON_SUCCESS, service.ongetCaseDataSuccess);
      objService.subscribe(jsx3.net.Service.ON_ERROR, service.ongetCaseDataError);
      objService.subscribe(jsx3.net.Service.ON_INVALID, service.ongetCaseDataInvalid);

      //PERFORMANCE ENHANCEMENT: uncomment the following line of code to use XSLT to convert the server response to CDF (refer to the API docs for jsx3.net.Service.compile for implementation details)
      //objService.compile();

      //call the service
      objService.doCall();
    };

    service.ongetCaseDataSuccess = function(objEvent) {
      //var responseXML = objEvent.target.getInboundDocument();
   //   objEvent.target.getServer().alert("Success","The service call was successful.");
    };

    service.ongetCaseDataError = function(objEvent) {
      var myStatus = objEvent.target.getRequest().getStatus();
      objEvent.target.getServer().alert("Error","The service call failed. The HTTP Status code is: " + myStatus);
    };

    service.ongetCaseDataInvalid = function(objEvent) {
      objEvent.target.getServer().alert("Invalid","The following message node just failed validation:\n\n" + objEvent.message);
    };

  }
);

