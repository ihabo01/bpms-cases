<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<head>
	<link href="style/custom/workflow/workflow.css?version=2194" rel="stylesheet"/>
	<link href="style/custom/workflow/bootstrap-datetimepicker.css" rel="stylesheet"/>
	<script type="text/javascript" src="scripts/plugin/workflow/jqSoapClient.min.js"></script>
	<script type="text/javascript" src="scripts/plugin/workflow/moment.min.js"></script>
	<script type="text/javascript" src="scripts/plugin/workflow/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="scripts/custom/workflow/cases.js"></script>
</head>
<body>
<script type="text/javascript">


$(document).ready(function(){
caseType=parse(0);
if(parse(1))selectMenuAndChangepage(this,'case','case.htm');
else{
updateCasesTable(caseType);
 $('#workflow_cases tbody').on( 'click', 'tr', function () {
     
		var newLocation="#/cases//"+caseType+"/"+this.id;

		changePage(this,'case.htm');
		location.href=newLocation;
    } );
}
});
</script>
<div id="breadcrumbs" class="breadcrumbs">
	<ul class="breadcrumb">
		<li><i class="fa fa-user"></i>&nbsp;&nbsp;Workflow&nbsp;</li>
		<li class="active"><a href="#" class="noDecoration" onclick="javascript:getTasksData();">Cases</a></li>
		<li class=""><a href="#" class="noDecoration" onclick="javascript:getTasksData();">Absence Request</a></li>
		<div style="display:inline-block" id="breadcrumbTaskFilter" class="hide"> <i class="fa fa-angle-right"></i>&nbsp;&nbsp; <span id="bcTaskFilterName"></span>&nbsp;&nbsp;<a href="#" class="noDecoration" onclick="removeTaskAppliedFilter();"><i class="fa fa-times"></i></a></div>




	</ul>
	<div style="display:inline-block" id="breadcrumbName" class="hide"> <i class="fa fa-angle-right"></i>&nbsp;&nbsp; <span id="caseName"></span>&nbsp;&nbsp;<a href="#" class="noDecoration" onclick="removeBreadCrumbName();"><i class="fa fa-times"></i></a></div>
</div>
<div class="page-content">
	<div id="caseTableDiv" class="col-xs-12">
		<div class="table-responsive">
			<table id="workflow_cases" class="table table-striped table-bordered table-hover">
			<thead>
				<tr id="rowCasesHeader">
					
				</tr>
			</thead>
		<tbody id="workflow_cases_rows">
		</tbody>
		</table>
		</div> 
</div>
	
</div>



	
	
</body>
</html>
