/* place JavaScript code here */

jsx3.lang.Package.definePackage(
  "Intalio.Internal.Utilities",
  function(util) {
  
 util.initApp = function() {
 Intalio.Internal.RBACRoleService.callRBACService();
 Intalio.Internal.Utilities.SERVER.publish({subject:Intalio.Internal.Utilities.GET_TASK_SUCCESS}); 

}
}
)