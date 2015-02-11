/* place JavaScript code here */

var form=NotifyAbsenceRequest;

function resizeLeaveList(){
    var leaveList=form.getJSXByName("LeaveList");
    var newHeight=(leaveList.getRecordIds().length+1)*leaveList.getRowHeight()+leaveList.getHeaderHeight();
    leaveList.getParent().setHeight(newHeight).repaint();
    leaveList.setHeight(newHeight).repaint();
    leaveList.getParent().repaint();
}