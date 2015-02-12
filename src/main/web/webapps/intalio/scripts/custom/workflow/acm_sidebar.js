$(document).ready(function () {

$('	<li  class="hide" id="moduleID73"><a href="#" class="dropdown-toggle" > <i class="menu-icon fa fa-briefcase"></i> <span class="menu-text">'
			    +' Cases</span><b class="arrow fa fa-angle-down hide"></b></a><b class="arrow"></b>'
				+'<ul class="submenu notModules">	</ul></li>').insertBefore( $(( "#moduleID6" )));
moduleIds['/cases']='moduleID73';
getCaseTypes();

$('#sidebar ul:not(.notModules)>li>a,#moduleUserProfile a').click(function(event) {

        var id = $(this).closest('li').attr('id');
        event.preventDefault();
        var hash = window.location.hash.replace('#', '')
        hash = hash.split('//')
        var mid = hash[0]
					 
        if (!(hash.length > 1 && getHashId(id) == mid)) {
              window.location.hash = '#' + getHashId(id);

         }
    })
});

function getCaseTypes(){
var acmNS={"$":"http:\/\/www.intalio.com\/BPMS\/ACMServices-2015\/"};
var participantToken=getCookie("singleLogin");
//participantToken=participantToken.substring(1, participantToken.length - 1);;

var data= {
"getCaseTypeListRequest":{"@xmlns":acmNS,"participantToken":{"$":participantToken}}};


	 var response=$.ajax({ 
			headers: {          
                 "content-type" :"application/json/badgerfish",
				 "Accept" : "application/json",           
				}  ,
             type: "POST",
			 data:JSON.stringify(data),
			 async: false,
             url: "/intalio/ode/processes/AdvancedCaseManagementServices",
            
         }).responseText;
		 response=$.parseJSON(response);
		 var submenu="";
		 if (response.CaseType){
		 if(!Array.isArray(response.CaseType))response.CaseType=[response.CaseType];
		 for (var i=0;i<response.CaseType.length;i++){
		var caseType=response.CaseType[i];
		
		submenu+='<li  id="moduleID'+caseType.ID+'"><a href="#/cases//'+caseType.ID+'" onclick="javascript:changePage(0,\'cases.htm\');"> <i class="menu-icon fa fa-caret-right"></i>'+caseType.description+'</a></li>';
		}
		$("#moduleID73 .arrow").removeClass("hide");
		}
		else{
		$("#moduleID73 .arrow").addClass("hide");
		}
		return $( "#moduleID73 .submenu").html(submenu);
		//return response;
//sendAjaxCall('/intalio/ode/processes/AdvancedCaseManagementServices', "POST", false, true, "json", data, handleAjaxError, populateCasesInSideBar);
}


function getCookie(c_name)
{
    var i,x,y,ARRcookies=document.cookie.split(";");
	
    for (i=0;i<ARRcookies.length;i++)
    {
        x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
        y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
        x=x.replace(/^\s+|\s+$/g,"");
        if (x==c_name)
        {
		
            return y.replace(/"/g,'');
        }
     }
}
