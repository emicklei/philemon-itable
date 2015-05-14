package com.philemonworks.itable;

import junit.framework.TestCase;
import com.philemonworks.writer.HTMLWriter;

public class TableRowTest extends TestCase {
	public void testCreate(){
		SortableTableRow row = new SortableTableRow();
		row.add("Hello");
		row.add("World");
		System.out.println(row);
		
	}
	public void testSort2(){
		SortableTableRow row = new SortableTableRow();
		row.add("HelloA");
		row.add("WorldA");
		
		SortableTableRow row2 = new SortableTableRow();
		row2.add("HelloB");
		row2.add("WorldB");

		SortableTableRow row3 = new SortableTableRow();
		row3.add("<a href='here'>HelloZ</a>","HelloZ");
		row3.add("<a href='there'>WorldZ</a>","WorldZ");		
		
		SortableTableRow[] rows = new SortableTableRow[]{row,row2,row3};
		
		SortableTable t = new SortableTable("id",2);
		t.sort(rows,SortableTable.ASCENDING, 0);
		
		for (int i = 0; i < rows.length; i++) {
			System.out.println(rows[i]);
		}
		HTMLWriter html = new HTMLWriter(System.out);
		for (int i = 0; i < rows.length; i++) {
			rows[i].write(html,null,null);
		}		
		
	}	
}
