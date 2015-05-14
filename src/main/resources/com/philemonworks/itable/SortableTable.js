<script type='text/javascript'>//<![CDATA[
// These functions exist for com.philemonworks.write.SortableTable
// see http://www.philemonworks.com for more info

function rowSelected(tr,rowclass){	
	// if selected again then deselect
	if(tr.parentNode.selectedrow == tr){
		tr.className=tr.parentNode.selectedrowclass;
		tr.parentNode.selectedrow=null;
		return;
	}
	// if selection was present then restore its style
	if(tr.parentNode.selectedrow != null){
		tr.parentNode.selectedrow.className=tr.parentNode.selectedrowclass;
	}
	// store new selection
	tr.parentNode.selectedrow=tr;
	tr.parentNode.selectedrowclass = rowclass;
	tr.className = 'rowselected';
} 
function emphasizeRow(tr, on, old) {
	if (tr == tr.parentNode.selectedrow) {
		tr.className = 'rowselected';
		return;
	}	
	if (on) { tr.className = 'rowhighlight';} else {tr.className = old;}
}
function columnClicked(tr,columnIndex,sortmethod) {
	window.top.location="?sortindex=" + columnIndex + "&sortmethod=" + sortmethod;
}
//]]>
</script>