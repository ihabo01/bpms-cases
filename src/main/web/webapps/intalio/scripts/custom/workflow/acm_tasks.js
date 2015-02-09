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
if(taskOptions.aoColumns.length<=11){
taskOptions.aoColumns.splice(taskOptions.aoColumns.length-2,0,relatedTo);
taskOptions.aoColumns.splice(taskOptions.aoColumns.length-2,0,state);
}
});

function addACMContent(obj,items){

			var caseType=obj.task.customMetadata["caseType"];
			var caseId=obj.task.customMetadata["caseId"];
			items[items.length]="<a href='/intalio/index.htm#/cases//"+caseType+"/"+caseId+"' onclick=\"javascript:selectMenuAndChangepage(this,\'cases\',\'cases.htm\');\">Case</a>";			
			items[items.length]="<i class='green ace-icon fa fa-info bigger-120' onmouseover='updateStatusTab(\""+caseType+"\",\""+caseId+"\",\".information\");showInformation();'></i>";
			
}