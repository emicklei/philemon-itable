package com.philemonworks.itable;

import java.io.FileOutputStream;
import java.util.Date;
import junit.framework.TestCase;
import com.philemonworks.writer.HTMLWriter;

public class SortableTableTest extends TestCase {
	public void testTable() throws Exception {
		FileOutputStream fos = new FileOutputStream("target/sorttest.html");
		HTMLWriter html = new HTMLWriter(fos);
		// once per page
		SortableTable.writeSelectionScriptsOn(html);
		
		int rows = 5;
		SortableTable t1 = new SortableTable("table1",rows,2);
		t1.getRow(0).put(0,"id");t1.getRow(0).put(1,"date");
		for (int r=1;r<rows;r++){
			t1.put(r,0, ""+(r-1)+" row");
			t1.put(r,1,new Date().toString());
		}

		SortableTable t2 = new SortableTable("table2",rows,2);
		t2.getRow(0).put(0,"id");t2.getRow(0).put(1,"datum");
		for (int r=1;r<rows;r++){
			t2.put(r,0, ""+(r-1)+" row");
			t2.put(r,1,new Date().toString());
		}
		
		html.html();
			html.tag("style");
				html.raw(
						"table { border:solid black 1px; padding: 0; margin:0}"+
						" .sortheadertable { border: none; } " +
						" .sortlink { text-decoration: none; border: none; }" +
						" .sortdesc { width: 16px; background: url(alphab_revsort_co.gif) no-repeat right;}" +						
						" .sortasc {width: 16px; background: url(alphab_sort_co.gif) no-repeat right;}" +						
						" .roweven { background: #FFAAAA; }" +
						" .rowhighlight { background: red; }" +
						" .rowselected { background: blue; } ");
			html.end("style");
			html.body();
			t1.write(html,null,1,SortableTable.DESCENDING);			
			html.hr();
			t2.write(html,null,1,SortableTable.ASCENDING);				
			html.end();
		html.end();
		fos.close();
	}

	public void testGrow(){
		SortableTable st = new SortableTable("grow",0,1);
		SortableTableRow row = st.newRow();
		assertEquals(row.length(), 1);
		assertEquals(st.size(), 1);
		
	}
}
