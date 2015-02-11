/* place JavaScript code here */
function init(){
var form=ValidateAbsenceRequest;

var leaveDetailsMatrixBlock=form.getJSXByName("LeaveType-block");

var leaveDetailsMatrix=form.getJSXByName("LeaveList");

var newHeight=(leaveDetailsMatrix.getRecordIds().length)*leaveDetailsMatrix.getRowHeight()+leaveDetailsMatrix.getHeaderHeight();


leaveDetailsMatrix.getParent().setHeight(newHeight).repaint();
leaveDetailsMatrix.setHeight(newHeight).repaint();

leaveDetailsMatrix.repaint();
leaveDetailsMatrix.getParent().repaint();
leaveDetailsMatrix.repaint();

}