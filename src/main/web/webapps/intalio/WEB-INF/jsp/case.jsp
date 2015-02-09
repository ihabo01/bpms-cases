<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<html>
	<head>
		<link href="style/custom/workflow/workflow.css?version=2194" rel="stylesheet" />
		<link href="style/custom/workflow/bootstrap-datetimepicker.css"
			rel="stylesheet" />
		<script type="text/javascript" src="scripts/plugin/workflow/jqSoapClient.min.js"></script>
		<script type="text/javascript" src="scripts/plugin/workflow/moment.min.js"></script>
		<script type="text/javascript"
			src="scripts/plugin/workflow/bootstrap-datetimepicker.min.js"></script>
		<script type="text/javascript" src="scripts/custom/workflow/cases.js"></script>
		<script type="text/javascript" src="scripts/custom/workflow/workflow.js"></script>
	</head>
	<body>
		<script type="text/javascript">
			var caseType=parse(0);
			var caseId=parse(1);
			var caseTypeDetails=getCaseTypeDetails();
			$(document).ready(function(){

			updateCaseHomeTab(caseType,caseId);
			updateTasksTab(caseType,caseId);


			removeLoading();

			});
			$('#taskform').load(function(){
			loadIFrame('taskform','taskTableDiv');
			});
		</script>
		<div id="breadcrumbs" class="breadcrumbs">
			<ul class="breadcrumb">
				<li>
					<i class="fa fa-user"></i>&nbsp;&nbsp;Workflow&nbsp;
				</li>
				<li class="">Cases</li>
				<li class="">
					<a href="#" class="noDecoration" onclick="javascript:getTasksData();">Absence Request</a>
				</li>
				<li class="">
					<a href="#" class="noDecoration" onclick="javascript:getTasksData();">Case 1</a>
				</li>
				<div style="display:inline-block" id="breadcrumbTaskFilter"
					class="hide">
					<i class="fa fa-angle-right"></i>&nbsp;&nbsp;
					<span id="bcTaskFilterName"></span>&nbsp;&nbsp;
					<a href="#" class="noDecoration" onclick="removeTaskAppliedFilter();">
						<i class="fa fa-times"></i>
					</a>
				</div>
			</ul>
			<div style="display:inline-block" id="breadcrumbName" class="hide">
				<i class="fa fa-angle-right"></i>&nbsp;&nbsp;
				<span id="caseName"></span>&nbsp;&nbsp;
				<a href="#" class="noDecoration" onclick="removeBreadCrumbName();">
					<i class="fa fa-times"></i>
				</a>
			</div>
		</div>

		<div class="page-content">
			<div class="col-sm-11">
				<div class="tabbable">
					<ul class="nav nav-tabs" id="myTab">
						<li class="active">
							<a data-toggle="tab" href="#caseDetails" aria-expanded="false">
								<i class="green ace-icon fa fa-home bigger-120"></i>
								Case Details
							</a>
						</li>

						<li class="">
							<a data-toggle="tab" href="#tasks" aria-expanded="true"
								onclick="updateTasksTab(caseType,caseId);">
								<i class="green ace-icon fa fa-user bigger-120"></i>
								Tasks
								<span class="badge badge-danger"></span>
							</a>
						</li>

						<li class="">
							<a data-toggle="tab" href="#history" aria-expanded="false"
								onclick="updateCaseHistoryTable(caseType,caseId);">
								<i class="green ace-icon fa fa-history bigger-120"></i>
								History
							</a>

						</li>

						<li class="">
							<a data-toggle="tab" href="#status" aria-expanded="false"
								onclick="updateStatusTab(caseType,caseId,'#status');">
								<i class="green ace-icon fa fa-info bigger-120"></i>
								Status
							</a>
						</li>

						<li class="">
							<a data-toggle="tab" href="#discussions" aria-expanded="false" onclick="updateDiscussionsTab(caseType,caseId,'existingComments');">
								<i class="green ace-icon fa fa-comment bigger-120"></i>
								Discussions
							</a>
						</li>

						<li class="">
							<a data-toggle="tab" href="#attachments" aria-expanded="false">
								<i class="green ace-icon fa fa-paperclip bigger-120"></i>
								Attachments
							</a>
						</li>
					</ul>
					<div class="tab-content">
						<div id="caseDetails" class="tab-pane fade in active">
							caseDetails
						</div>
						<div id="tasks" class="tab-pane fade">
							<div id="taskTableDiv" class="col-xs-12">
								<div class="table-responsive">
									<table id="workflow_tasks"
										class="table table-striped table-bordered table-hover">
										<thead>
											<tr id="rowTaskHeader">
												<th>
													<label class="position-relative">
														<input type="checkbox" class="ace nowrap"
															onclick="updateTaskHeaderButtons(this)" />
														<span class="lbl"></span>
													</label>
												</th>
												<th
													onclick="javascript:sortData('workflow_tasks',this,'_description')"
													sort='desc' class="nowrap">
													<span class='pull-right hide'>
														<i class="fa fa-sort-down blue"></i>
													</span>
													<fmt:message
														key="com_intalio_bpms_workflow_taskHolder_description" />
												</th>
												<th onclick="javascript:sortData('workflow_tasks',this,'_state')"
													sort='desc' class="priorityHead nowrap">
													<span class='pull-right hide'>
														<i class="fa fa-sort-down blue"></i>
													</span>
													<fmt:message
														key="com_intalio_bpms_workflow_taskHolder_task_status" />
													<i class="fa fa-info-circle iconCursor" onmouseover="displayStatusLegend(this);"
														onmouseout="hideWorkflowLegend('statusIconsList');"></i>
												</th>
												<th
													onclick="javascript:sortData('workflow_tasks',this,'_creationDate')"
													sort='desc' class="nowrap">
													<span class='pull-right hide'>
														<i class="fa fa-sort-down blue"></i>
													</span>
													<i class="fa fa-clock-o" style="margin-left:0px"></i>&nbsp;&nbsp;
													<fmt:message
														key="com_intalio_bpms_workflow_taskHolder_creationDateTime" />
												</th>
												<th onclick="javascript:sortData('workflow_tasks',this,'_deadline')"
													sort='desc' class="nowrap">
													<span class='pull-right hide'>
														<i class="fa fa-sort-down blue"></i>
													</span>
													<i class="fa fa-clock-o" style="margin-left:0px"></i>&nbsp;&nbsp;
													<fmt:message key="com_intalio_bpms_workflow_taskHolder_dueDate" />
												</th>
												<th onclick="javascript:sortData('workflow_tasks',this,'_priority')"
													sort='desc' class="priorityHead nowrap">
													<span class='pull-right hide'>
														<i class="fa fa-sort-down blue"></i>
													</span>
													<fmt:message
														key="com_intalio_bpms_workflow_taskHolder_priority" />
													<i class="fa fa-info-circle iconCursor" onmouseover="displayPriorityLegend(this);"
														onmouseout="hideWorkflowLegend('priorityIconsList');"></i>
												</th>
												<th>Priority Value</th>
												<th class="nowrap">
													<fmt:message
														key="com_intalio_bpms_workflow_taskHolder_assigned_to" />
												</th>
												<th class="nowrap">
													<fmt:message key="com_intalio_bpms_workflow_taskHolder_Actions" />
												</th>
												<th class="nowrap">isOthersTask</th>
											</tr>
										</thead>
										<tbody id="workflow_Tasks_rows">
										</tbody>
									</table>
								</div>
							</div>
							<iframe src="" name="taskform" frameborder="0" id="taskform"
								scrolling="auto" width="100%" height="100%"></iframe>
						</div>
						<div id="history" class="tab-pane fade">


								<div id="caseTableDiv" class="col-xs-12">
									<div class="table-responsive">
										<table id="case_history"
											class="table table-striped table-bordered table-hover">
											<thead>
												<tr id="rowHistoryHeader">

												</tr>
											</thead>
											<tbody id="case_history_rows">
											</tbody>
										</table>
									</div>
								</div>

						</div>
						<div id="status" class="tab-pane fade">
							test
						</div>
						<div id="discussions" class="tab-pane fade" style="overflow: auto;">
							<div id="existingComments">
								<div id="itemtempletid" class="itemdiv commentdiv hide">
									<div class="body">
										<div class="name">
											<span href="#" id="name"></span>
										</div>
										<div class="time">
											<i class="fa fa-clock-o">
											</i>
											<span id="time"></span>
										</div>
										<div class="text">
											<i class="fa fa-quote-left">
											</i>
											<span id="comment"></span>
										</div>
									</div>
								</div>
							</div>
							<div id="addComment">

								<textarea placeholder="Add comment here." id="newcommentcase"
									class="form-control limited" maxlength="4000"
									onfocus="if ($(this).val().indexOf('Add comment here.') > -1){$(this).val('')}"></textarea>
								<input type="hidden" id="newthreadid" value="Absence_Request" />
								<input type="hidden" id="newmoduleid" value="cases" />
								<input type="hidden" id="newuser" value="leo1" />
								<span class="blue hide">Comment added successfully.</span>
								<span class="red hide" id="errorid">Please enter comment.</span>
								<div id="savebutton" style="margin-top:10px"
									class="navbar-right btn btn-primary btn-xs"
									onclick="addCommentToCase('existingComments', 500, caseId, $('#userid').text(), $('#newcommentcase').val(),insertComment);return false;"
									parentid="comments14">
									comment
								</div>
							</div>
						</div>

						<div id="attachments" class="tab-pane fade">
							test
						</div>
					</div>
				</div>
			</div>
		</div>





		<div>
			<iframe src="" name="caseform" frameborder="0" id="caseform"
				scrolling="auto" width="100%" height="100%"></iframe>
		</div>




	</body>
</html>
