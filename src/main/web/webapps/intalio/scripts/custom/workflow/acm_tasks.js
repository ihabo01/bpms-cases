$(document).ready(function () {
$('<th class="nowrap">Related to</th><th class="nowrap">State</th>').insertBefore( $(( "th:contains('isOthersTask')" )));
var relatedTo={
        "sClass": "center qsdqsd",
        "sWidth": width * 0.07
    };
	
	var state={
        "sClass": "center qsdqsd",
        "sWidth": width * 0.07
    };
taskOptions.aoColumns.splice(taskOptions.aoColumns.length-2,0,relatedTo);
taskOptions.aoColumns.splice(taskOptions.aoColumns.length-2,0,state);
});

function addACMContent(obj,items){
items[items.length]="<a href='/intalio/index.htm#cases?type=Absence_Request&id=a7d1f1e7-a290-465e-890d-afcfd2083455-2'>Case</a>";
			var caseType=obj.task.customMetadata["caseType"];
			var caseId=obj.task.customMetadata["caseId"];			
			items[items.length]="<i class='green ace-icon fa fa-info bigger-120' onmouseover='updateStatusTab(\""+caseType+"\",\""+caseId+"\",\".information\");showInformation();'></i>";
			
			}