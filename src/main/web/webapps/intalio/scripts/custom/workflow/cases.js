var caseType;
var casesOptions = {
    "bPaginate": false,
    "bStateSave": true,
    "bInfo": false,
    "bFilter": true,
    "oLanguage": {
        "sSearch": ""
    },
    "bRetrieve": true,
    "bAutoWidth": false,
    "bSort": false,
	"aoColumns":[]
}
var tableWidth=100%;
var relatedTaskOptions = {
    "bPaginate": false,
    "bStateSave": true,
    "bInfo": false,
    "bFilter": true,
    "bRetrieve": true,
    "oLanguage": {
        "sSearch": ""
    },
    "bAutoWidth": false,
    "bSort": false,
    "aoColumns": [{
        "sWidth": tableWidth * 0.025,
        "sClass": "center"
    }, {
        "sType": "html",
        "sClass": "alignLeft description",
        "sWidth": tableWidth * 0.15
    }, {
        "sWidth": tableWidth * 0.050,
        "sClass": "center status"
    }, {
        "sClass": "alignLeft created",
        "sWidth": tableWidth * 0.10
    }, {
        "sClass": "alignLeft dueDate",
        "sWidth": tableWidth * 0.10
    }, {
        "sType": "string",
        "sClass": "center priority",
        "sWidth": 195	,
        "iDataSort": 6
    }, {
        "bVisible": false,
        "bSearchable": false,
        "sWidth": tableWidth * 0.03
    }, {
        "sClass": "alignLeft assigned",
        "sWidth": tableWidth * 0.10
    }, {
        "sClass": "center action",
        "sWidth": tableWidth * 0.07
    },{
        "bVisible": false,
        "bSearchable": false
    }],
    "fnDrawCallback": function(oSettings) {
        markAsOthersTask(oSettings, 9);
    }
}

var taskData;





function parse(index) {

    var result = "Not found",
        tmp = [];
		var hash=location.href.substring(location.href.indexOf("#")+1);
		var arguments=hash.substring(location.href.indexOf("//")+3);
 
    return arguments.split("/")[index];
}
function updateCasesTable(caseType){
var getCases=getCasesByType(caseType);
var caseTypeColumns=getCases.columns.column;
var cases=getCases.cases.case;
var headers="";


 for (var i=0;i<caseTypeColumns.length;i++){

		var caseUnit=caseTypeColumns[i];
		if(caseUnit.name.toLowerCase()=="id")continue;
		headers+='<th onclick="javascript:sortData(\'workflow_cases\',this,\'_state\')" sort=\'desc\' class="nowrap">';
				headers+='<span class=\'pull-right hide\'><i class="fa fa-sort-down blue"></i></span>';
				headers+=cleanHeaderName(caseUnit.name);
				headers+='	</th>	';
  casesOptions.aoColumns[casesOptions.aoColumns.length] = {
					
					"sWidth": width * 0.20
				}
}



$("#rowCasesHeader").append(headers);

var taskTable = $('#workflow_cases').dataTable(casesOptions);

if(cases){
if(!Array.isArray(cases))cases=[cases];


	for (var i=0;i<cases.length;i++){
	
		var caseUnit=cases[i];
		var item=[];
		var id;
		for (var j=0;j<caseTypeColumns.length;j++){
		
		if(caseTypeColumns[j].name.toLowerCase()=="id"){id=caseUnit[caseTypeColumns[j].name];continue;}
		item[item.length]=caseUnit[caseTypeColumns[j].name];
		}		
		var addId=taskTable.fnAddData(item, false);
		var theNode = taskTable.fnSettings().aoData[addId[0]].nTr;
		theNode.setAttribute('id',id);
	}
taskTable.fnDraw(true);
}
customTable('workflow_cases');
removeLoading();

}


    
function updateCaseHistoryTable(caseType,caseId){
var caseHistory=getCaseHistory(caseType,caseId);
var caseHistoryColumns=caseHistory.columns.column;
var caseHistoryEntries=caseHistory.caseHistory.historyEntry;
var headers="";

var specialColumns=["id","status_activity","status_activity_type","caseid"];
 for (var i=0;i<caseHistoryColumns.length;i++){
		
		var caseHistoryColumn=caseHistoryColumns[i];
		if(specialColumns.indexOf(caseHistoryColumn.name.toLowerCase())!=-1)continue;
		headers+='<th onclick="javascript:sortData(\'workflow_cases\',this,\'_state\')" sort=\'desc\' class="nowrap">';
				headers+='<span class=\'pull-right hide\'><i class="fa fa-sort-down blue"></i></span>';
				headers+=cleanHeaderName(caseHistoryColumn.name);
				headers+='	</th>	';
  casesOptions.aoColumns[casesOptions.aoColumns.length] = {
					
					"sWidth": width * 0.20
				}
}



$("#rowHistoryHeader").html(headers);

var taskTable = $('#case_history').dataTable(casesOptions);
	taskTable.fnClearTable();
	if(caseHistoryEntries ){
	for (var i=0;i<caseHistoryEntries.length;i++){
	
		var caseUnit=caseHistoryEntries[i];
		var item=[];
		var id;
		for (var j=0;j<caseHistoryColumns.length;j++){
		
			if(specialColumns.indexOf(caseHistoryColumns[j].name.toLowerCase())!=-1)continue;
		item[item.length]=caseUnit[caseHistoryColumns[j].name];
		}		
		taskTable.fnAddData(item, false);

	}
}
taskTable.fnDraw(true);
customTable('case_history');


}

function cleanHeaderName(name){
	name=name.replace('_',' ');

    return name.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});

}

function getCaseTypeDetails(caseTypeId){
var acmNS={"$":"http:\/\/www.intalio.com\/BPMS\/ACMServices-2015\/"};
var participantToken=getCookie("singleLogin");

//participantToken=participantToken.substring(1, participantToken.length - 1);;

var data= {
"getCaseTypeByIdRequest":{"@xmlns":acmNS,"participantToken":{"$":participantToken},"caseType":{"$":caseType}}};


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
		 
		return response;
		//return response;
//sendAjaxCall('/intalio/ode/processes/AdvancedCaseManagementServices', "POST", false, true, "json", data, handleAjaxError, populateCasesInSideBar);
}

function getCasesByType(caseType){
var acmNS={"$":"http:\/\/www.intalio.com\/BPMS\/ACMServices-2015\/"};
var participantToken=getCookie("singleLogin");

//participantToken=participantToken.substring(1, participantToken.length - 1);;

var data= {
"getCasesByTypeRequest":{"@xmlns":acmNS,"participantToken":{"$":participantToken},"caseType":{"$":caseType}}};


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
		 
		return response;
		//return response;
//sendAjaxCall('/intalio/ode/processes/AdvancedCaseManagementServices', "POST", false, true, "json", data, handleAjaxError, populateCasesInSideBar);
}
function getCaseHistory(caseType,caseId){
var acmNS={"$":"http:\/\/www.intalio.com\/BPMS\/ACMServices-2015\/"};
var participantToken=getCookie("singleLogin");

//participantToken=participantToken.substring(1, participantToken.length - 1);;

var data= {
"getCaseHistoryRequest":{"@xmlns":acmNS,"participantToken":{"$":participantToken},"caseType":{"$":caseType},"caseId":{"$":caseId}}};


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
		 
		return response;
		//return response;
//sendAjaxCall('/intalio/ode/processes/AdvancedCaseManagementServices', "POST", false, true, "json", data, handleAjaxError, populateCasesInSideBar);
}

function updateCaseHomeTab(caseType,caseId){

}
function updateTasksTab(caseType,caseId){


taskTable=$('#workflow_tasks').dataTable(relatedTaskOptions);
customRelatedTasksTable('workflow_tasks');
var participantToken=getCookie("singleLogin");
 
		var tmsNS={"$":"http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"};

		var data= {
"getAvailableTasksRequest":{"@xmlns":tmsNS,"participantToken":{"$":participantToken},"taskType":{"$":"Activity"},
"subQuery":{"$":
//"(INDEX(C)='caseType' AND C='"+caseType+"') AND"+ 
"(INDEX(C)='caseId' AND C='"+caseId+"')"}}};


	 var response=$.ajax({ 
			headers: {          
                 "content-type" :"application/json/badgerfish",
				 "Accept" : "application/json",           
				}  ,
             type: "POST",
			 data:JSON.stringify(data),
			 async: false,
             url: "/intalio/ode/processes/TaskManagementServices",
            
         }).responseText;
		  var taskData=$.parseJSON(response);
		 
   updateRelatedData(taskData,taskTable);

}

function customRelatedTasksTable(tableId) {
    $('#' + tableId).css('width', tableWidth);
    //$('#' + tableId + '_wrapper .row .col-sm-6:first').removeClass('col-sm-6').addClass("col-sm-9 tableButtons");
    //$('#' + tableId + '_wrapper .row .col-sm-6:first').removeClass('col-sm-6').addClass("col-sm-0 searchBoxTasks");
    $('#' + tableId + '_wrapper .row:first').css({
        "background-color": "#FFFFFF",
        'height': '40px'
    });
    $('#' + tableId + '_wrapper .row:last').css("background-color", "#FFFFFF").css("border-bottom", 0).css('padding-bottom', 0);
    $('#' + tableId + '_wrapper .row:first').css("padding", "0");
    $('#' + tableId + '_wrapper .row:first').css("padding-bottom", "8px");
    $('#' + tableId + '_wrapper .row:last .col-sm-6:first').remove();
    $('#' + tableId + '_wrapper .row:last .col-sm-6').removeClass('col-sm-6').addClass('col-sm-12 table_pagination');
    $('#' + tableId + '_filter').find('input').attr('placeholder', $("#dtSearchPlaceHolder").text());
    if ($('#' + tableId + '_filter').find('.table_refresh_icon').length == 0)
        $('#' + tableId + '_filter').append(getToolbarIconsCodes("viewAllTask"));
    $('.sorting_disabled').css('color', '#707070');
	 $('#' + tableId + '_wrapper .table_container').css("border",0);
}

function updateStatusTab(caseType,caseId, container){
if (caseTypeDetails.CaseType.status_diagram && caseTypeDetails.CaseType.status_diagram!=null && caseTypeDetails.CaseType.status_diagram!=undefined){
$(container).load(caseTypeDetails.CaseType.status_diagram,function(){
var history=getCaseHistory(caseType,caseId);
var activityColor={"state":"blue","start_activity":"green","end_activity":"blue","fail":"red"}
$.each(history.caseHistory.historyEntry, function(key, hitoryEntry) {
$.each(hitoryEntry.STATUS_ACTIVITY.split(","), function(key1, state) {
var activityType=hitoryEntry.STATUS_ACTIVITY_Type.split(",")[key1];
$("circle[opacity][opacity!=0][opacity!=null]").attr('display',"none");
$("rect[opacity][opacity!=0][opacity!=null]").attr('display',"none");
$("polygon[opacity][opacity!=0][opacity!=null]").attr('display',"none");
$("text:contains("+state+")").prev().filter("[stroke!=none]").attr("stroke-width",2).attr("stroke",activityColor[activityType]);
$("text:contains("+state+")").prev().prev().filter("circle").filter("[stroke!=none]").attr("stroke-width",2).attr("stroke",activityColor[activityType]);
//$("rect[bpmn\\:activity-label="+state+"]").attr("stroke-width",2).attr("stroke",activityColor[activityType]);
//$("circle[bpmn\\:activity-label="+state+"]").next().attr("stroke-width",2).attr("stroke",activityColor[activityType]);
//$("circle[bpmn\\:activity-label="+state+"]").next().next().not("text").attr("stroke-width",2).attr("stroke",activityColor[activityType]);
})
})});
}
else {
$(container).html("No status process has been associated with this case");
}

}

function updateRelatedData(data, taskTable) {
    var oSettings = taskTable.fnSettings();
    taskTable.fnClearTable();
    taskTable.fnFilter('');
    var check = 1;
	if(!Array.isArray(data.task) && data.task!=undefined)data.task=[data.task];
	
	var tasksCount=data.task.length;
	$("a[href=#tasks] span").text(tasksCount);
    if (data.task != undefined && tasksCount > 0) {
        $.each(data.task, function(key, obj) {
            var items = [];
            var html = "";
            
            var isTaskOwner = obj.isTaskAvailable;

            items[items.length] = "<label class='position-relative'><input type='checkbox' class='ace taskSelected' id='taskSelected' onclick='updateHeaderCheckbox(this);updateTaskButtons(this);'> <span class='lbl'></span></label>";
            if (obj.description != "" && obj.description != null) {
						
                if(obj.taskState == "FAILED")
                    items[items.length] = "<span tid="+obj.taskId+">"+obj.description + "</span><span class='pull-right othersTaskIcon' title='" + $('#othersTaskWorkflow').text() + "'><img src='images/others_task.png' height='17'></img></span>";
                else if (isTaskOwner )
                    items[items.length] = "<a class='task_name' href='" + obj.formUrl+"?id="+obj.taskId+"&token="+getCookie("singleLogin") +"&type=PATask&url="+obj.formUrl+ "&user="+$("#userid").text()+"' " +"istaskowner=" + isTaskOwner + " desciption=" + obj.description + " priority=" + obj.priority + " target='taskform' tid=" + obj.taskId + " state=" + obj.taskState + ">" + obj.description + "</a><span class='pull-right othersTaskIcon' title='" + $('#othersTaskWorkflow').text() + "'><img src='images/others_task.png' height='17'></img></span>";
                else
                    items[items.length] = "<a class='task_name' onclick=javascript:showAlert(); istaskowner=" + isTaskOwner + " description=" + obj.description + " priority=" + obj.priority + " target='taskform' tid=" + obj.taskId + " state=" + obj.taskState + ">" + obj.description + "</a><span class='pull-right othersTaskIcon' title='" + $('#othersTaskWorkflow').text() + "'><img src='images/others_task.png' height='17'></img></span>";
            } else {
                if(obj.taskState == "FAILED")
                    items[items.length] = "<span tid="+obj.taskId+">"+$("#noDescription").text() + "</span><span class='pull-right othersTaskIcon' title='" + $('#othersTaskWorkflow').text() + "'><img src='images/others_task.png' height='17'></img></span>";
                else if (isTaskOwner == 'true')
                    items[items.length] = "<a class='task_name' href=" + obj.formManagerURL + " istaskowner=" + isTaskOwner + " description='' priority=" + obj.priority + " target='taskform' tid=" + obj.taskId + " state=" + obj.taskState + ">" + $("#noDescription").text() + "</a><span class='pull-right othersTaskIcon' title='" + $('#othersTaskWorkflow').text() + "'><img src='images/others_task.png' height='17'></img></span>";
                else
                    items[items.length] = "<a class='task_name' onclick=javascript:showAlert(); istaskowner=" + isTaskOwner + " description=" + obj.description + " priority=" + obj.priority + " target='taskform' tid=" + obj.taskId + " state=" + obj.taskState + ">" + $("#noDescription").text() + "</a><span class='pull-right othersTaskIcon' title='" + $('#othersTaskWorkflow').text() + "'><img src='images/others_task.png' height='17'></img></span>";
            }
            if (obj.taskState == "CLAIMED")
                items[items.length] = '<span class="action-buttons"><a class="text-warning ace-popup cursorDefault title="" data-placement="bottom" data-content="' + $("#claimedTaskDesc").text() + '" data-trigger="hover"><i class="fa-zoom-in fa fa-lock bigger-140 orange"></i></a></span>';
            else if (obj.taskState == "READY")
                items[items.length] = '<span class="action-buttons"><a class="text-success ace-popup cursorDefault" title="" data-placement="bottom" data-content="' + $('#readyTaskDesc').text() + '" data-trigger="hover"><i class="fa-zoom-in fa fa-check-circle bigger-125 green" ></i></a></span>';
            else if(obj.taskState == "FAILED")
                items[items.length] = '<span class="action-buttons"><a class="ace-popup cursorDefault" title="" data-placement="bottom" data-content="' + $('#taskExpiredMsg').text() + '" data-trigger="hover"><i class="fa-zoom-in fa fa-circle bigger-125 red"></i></a></span>';
            else 
                items[items.length] = '<span class="action-buttons"><a class="ace-popup cursorDefault" title="" data-placement="bottom" data-content="' + $('#escalatedTaskDesc').text() + '" data-trigger="hover"><i class="fa-zoom-in fa fa-arrow-circle-up bigger-125 blue"></i></a></span>';
            if (obj.creationDate != "" && obj.creationDate != null)
                items[items.length] = $.format.date(obj.creationDate, userPreferences.dateFormat+userPreferences.hourFormat);
            else
                items[items.length] = " ";
            if (obj.deadline != "" && obj.deadline != null){
                if ($.inArray('updateDueDate', taskIconSet) >= 0 && obj.taskState != "FAILED") {
                    items[items.length] = "<a href='#' class='noDecoration task_due_date' taskId='"+obj.taskId+"' onclick='javascript:openDueDateModal(this)'>"+$.format.date(obj.deadline, userPreferences.dateFormat+userPreferences.hourFormat)+"</a>";
                } else {
                    items[items.length] = $.format.date(obj.deadline, userPreferences.dateFormat+userPreferences.hourFormat)
                }
            }
            else
                items[items.length] = "";

            if (obj.priority != "" && obj.priority != null) {
                if (parseInt(obj.priority) >= parseInt(51))
                    items[items.length] = "<i class='fa fa-circle bigger-125 red redOpacity cursorDefault taskPriority ace-popup' value='" + obj.priority + "' title='' data-placement='bottom' data-content='" + $("#priorityCritical").text() + "' data-trigger='hover'></i>";
                else if (parseInt(obj.priority) >= parseInt(31) && parseInt(obj.priority) <= parseInt(50))
                    items[items.length] = "<i class='fa fa-circle bigger-125 orange orangeOpacity cursorDefault taskPriority ace-popup' value='" + obj.priority + "' title='' data-placement='bottom' data-content='" + $("#priorityImportant").text() + "' data-trigger='hover'></i>";
                else if (parseInt(obj.priority) >= parseInt(11) && parseInt(obj.priority) <= parseInt(30))
                    items[items.length] = "<i class='fa fa-circle bigger-125 green greenOpacity cursorDefault taskPriority ace-popup' value='" + obj.priority + "' title='' data-placement='bottom' data-content='" + $("#priorityNormal").text() + "' data-trigger='hover'></i>";
                else if (parseInt(obj.priority) <= parseInt(10))
                    items[items.length] = "<i class='fa fa-circle bigger-125 blue blueOpacity cursorDefault taskPriority ace-popup' value='" + obj.priority + "' title='' data-placement='bottom' data-content='" + $("#priorityLow").text() + "' data-trigger='hover'></span>";
            } else
                items[items.length] = "<i class='fa fa-circle bigger-125 green taskPriority' value='15' title='' data-placement='bottom' data-content='" + $("#priorityNormal").text() + "'></i>";
            if (obj.priority != "" && obj.priority != null)
                items[items.length] = parseInt(obj.priority);
            else
                items[items.length] = parseInt(15);
            var tempHtml = '';

            if (obj.userOwner != null || obj.roleOwner != null) {
                if (obj.userOwner != null && obj.userOwner.length > 0) {
                    $.each(obj.userOwner.split(","), function(key, value) {
                        var nameObj = [];
                        if(data.users!=undefined && data.users.length>=0)
                            nameObj = $.grep(data.users, function(e){return e.userID.toLowerCase() == value.toLowerCase()});
                        if (value != "string"){
                            tempHtml += '<span class="nowrap">';
                            nameObj.length==1 ? tempHtml+='<i class="fa fa-user" title="'+nameObj[0].userID+'"></i> '+nameObj[0].userName+'</span>' : tempHtml += '<i class="fa fa-user" title="User"></i> ' + value + ' </span>';
                            tempHtml += '<span class="wrap-line"> </span>';
                        }
                    })
                }
                if (obj.roleOwner != null && obj.roleOwner.length > 0) {
                    $.each(obj.roleOwner.split(","), function(key, value) {
                        value = value.replace('*', 'All');
                        tempHtml += '<span class="nowrap"><i class="fa fa-group" title="Role"></i> ' + value + ' </span><span class="wrap-line"> </span>';
                    });
                }
                items[items.length] = tempHtml;
            } else
                items[items.length] = '';
            var html="";
            if(!isObjectEmpty(obj.attachments))
                html = "<span class='action-buttons attachmentsExist'>";
            else
                html = "<span class='action-buttons'>";
            if (!isObjectEmpty(obj.taskState)) {
                if ($.inArray('update', taskIconSet) >= 0 && obj.taskState == "FAILED") {
                    html += "<i class='fa-zoom-in fa fa-edit bigger-120' title='"+$("#editTaskMsg").text()+"' return false;'></i></span>";
                    check = 2;
                }else if($.inArray('update', taskIconSet) >= 0){
                    html += "<a class='text-purple iconCursor'><i class='fa-zoom-in fa fa-edit bigger-120' title='"+$("#editTaskMsg").text()+"' onclick='updateTask(this,false);return false;'></i></a></span>";
                    check = 2;
                }
                if (!isObjectEmpty(obj.attachments)) {
                    html += "<div id='attachments_div' class='btn-group'><a  class='dropdown-toggle iconCursor' data-toggle='dropdown' onclick='setDropdownPosition(this);'><i class='fa-zoom-in fa fa-paperclip fa-only bigger-120 '></i></a><ul id='attachments_list' class='dropdown-menu dropdown-yellow dropdown-caret dropdown-closer dropdown-menu-right positionFixed'>";
                    $.each(obj.attachments, function(key, inf) {
                        html += '<li><a href="' + inf.payloadURLAsString + '" target="_blank">' + inf.metadata.fileName + '</a></li>';
                    });
                    html += '</ul></div>'
                }
                items[items.length] = html;
            } else
                items[items.length] = " ";
			//items[items.length] = isOthersTask(data.userRoles, obj.userOwner, obj.roleOwner, obj.isTaskAvailable);
			items[items.length] = "";
            if (data.newColumnList != undefined) {
                $.each(data.newColumnList, function(columnKey, columnObj) {
                    if (obj.customMetadata[columnObj] != null && obj.customMetadata[columnObj] != undefined)
                        items[items.length] = obj.customMetadata[columnObj];
                    else
                        items[items.length] = "";
                });
            }
			
            taskTable.fnAddData(items, false);
        });
        taskTable.fnDraw(true);
        $('#workflow_tasks thead tr th').removeClass("sorting_asc").removeClass("sorting");
        tempWorkflowTable = taskTable;
        taskTable.fnFilter('');
   
        
        
        applyNiceScroll($("#workflow_tasks_wrapper").find(".table_container"), 190);
    } else {
        $('.dataTables_empty').html($("#dtNoRecordsFound").text().replace('{0}',$("#taskMsg").text()));
        taskStateFlag = false;
        $("#rowTaskHeader th:gt(9)").remove();
        $("#workflow_tasks_wrapper .table_pagination").remove();
    }
    
    $('table thead th input:checkbox').prop('checked', '');
    $('#workflow_tasks tr th input:first').prop('checked', false);
    removeLoading();
    $('.ace-popup').popover();
}
function addCommentToCase(container,type, id, user, comment,insertCommentFunction) {
$('#newcommentcase').val('');
    var data = {
        refCommentTypeId: type,
        threadId: id,
	createdBy: user,
        comment: comment
    }
    var url = formURL(SAVE,data);
    sendAjaxCall(url, "POST", false, true, "html", data, commentErrorCall, insertCommentFunction(data,container));
}

function updateDiscussionsTab(caseType, caseId,container) {
    var data = {
        moduleId: 500,
        threadId: caseId,
		start:0,
		max:1000
    }
  var url = formURL(LIST,data);
  get(url, data, function(data1){
    container=$("#"+container);
container.children().not('#itemtempletid:first-child').remove();
				  for(var comment in data1.comments){

				container.append(formatComment(container,data1.comments[comment]));

				  }
				}
  );
}
function insertComment(commentData,container){
return function(data){
container=$("#"+container);

container.append(formatComment(container,commentData));

}
}

function formatComment(container,data){

var commentHTML="";
if(!data.createdDate)data.createdDate=new Date();
var templet = container.find("#itemtempletid:first-child");

var itemdiv  =$(templet).clone();;
itemdiv.find( "#name" ).text(data.createdBy);
itemdiv.find( "#time" ).text(timeDifference(new Date(), new Date(data.createdDate)));

itemdiv.find( "#comment" ).text(data.comment);
itemdiv.removeClass("hide");
return itemdiv;
}