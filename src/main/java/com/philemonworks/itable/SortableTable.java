package com.philemonworks.itable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.philemonworks.writer.HTMLWriter;
import com.philemonworks.writer.XMLWriter;
/**
 * SortableTable represents a table in HTML
 * that has event hooks for sorting rows by column clicks and row selection.
 * Sorting is done server-side so a pageURL must be provided when writing this table.
 * 
 * See: write(HTMLWriter html, Map tableMap, String pageURL, int sortcolumn, String sortmethodOrNull)
 * 
 * @author Ernest Micklei
 */
public class SortableTable {
	/**
	 * Z..A, 9..0
	 */
	public static final String DESCENDING = "desc";

	/**
	 * A..Z, 0..9
	 */
	public static final String ASCENDING = "asc";	
	public final static Map DefaultOddRowMap;
	public final static Map DefaultEvenRowMap;
	public final static Map Colspan2Map;	
	/**
	 * Initialized with an empty array of SortableTableRow instances.
	 */
	private SortableTableRow[] rows = new SortableTableRow[0];
	/**
	 * The HTML name of the table.
	 */
	public String id = "";
	/**
	 * Function to invoke when a column is clicked
	 */
	public String columnClickedFunction = "columnClicked";  // default function declaration is found in SortableTable.js
	/**
	 * Map of row attributes for odd numbered rows.
	 */
	public Map oddRowMap;
	/**
	 * Map of row attributes for even numbered rows.
	 */
	public Map evenRowMap;
	/**
	 * Numbers of cells per row.
	 */
	public int columns;
	
	static {
		XMLWriter util = new XMLWriter(null); // misuse?
		DefaultEvenRowMap = util.newMap(
				"class","roweven",
				"onmouseout", "javascript:emphasizeRow(this, false,'roweven' );", 
				"onmouseover","javascript:emphasizeRow(this, true, 'roweven' );");
		DefaultEvenRowMap.put("onclick","javascript:rowSelected(this,'roweven');");		
		DefaultOddRowMap = util.newMap(
				"class","rowodd",
				"onmouseout", "javascript:emphasizeRow(this, false, 'rowodd');", 
				"onmouseover","javascript:emphasizeRow(this, true, 'rowodd');");
		DefaultOddRowMap.put("onclick","javascript:rowSelected(this,'rowodd');");
		Colspan2Map = new HashMap();
		Colspan2Map.put("colspan","2");
	}
	
	/**
	 * Create a new SortableTable without any rows but initialize the row length.
	 * @param newid
	 * @param howManyColumns
	 */
	public SortableTable(String newid, int howManyColumns) {
		this(newid, 0, howManyColumns);
	}
	/**
	 * Create a new SortableTable and initialize it with rows with fixed columns.
	 * @param newid
	 * @param howManyRows
	 * @param howManyColumns
	 */
	public SortableTable(String newid, int howManyRows, int howManyColumns) {
		super();
		this.id = newid;
		this.columns = howManyColumns;
		this.rows = new SortableTableRow[howManyRows];
		for (int i = 0; i < rows.length; i++) {
			this.rows[i] = new SortableTableRow(columns);
		}
		this.initDefaultAttributeMaps();
	}
	private void initDefaultAttributeMaps(){
		// need to make copies to protect the default values
		oddRowMap = XMLWriter.copyMap(DefaultOddRowMap);
		evenRowMap = XMLWriter.copyMap(DefaultEvenRowMap);
	}
	/**
	 * Write the set of Javascript function to allow the tablerows to be selectable.
	 * 
	 * Requires style definitions for:
	 * .sortlink
	 * .sortasc
	 * .sortdesc 
	 * .roweven
	 * .rowodd
	 * .rowhighlight
	 * .rowselected
	 * @param writer
	 */
	public static void writeSelectionScriptsOn(HTMLWriter writer){
		// TODO cache this?
		InputStream is = SortableTable.class.getResourceAsStream("SortableTable.js");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {
			while (reader.ready()) {
				writer.raw(reader.readLine());
				writer.raw('\n');
			}
			is.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}
	/**
	 * Convenience methods for putting data into the table
	 * @param row
	 * @param column
	 * @param rawcontents
	 * @return SortableTable
	 */
	public SortableTable put(int row, int column, String rawcontents){
		rows[row].put(column, rawcontents);
		return this;
	}
	/**
	 * Convenience method for sorting the rows of the table
	 * @param sortcolumn : int , [0..columns>
	 * @param sortmethod : SortableTable.ASCENDING, SortableTable.DESCENDING
	 */
	public void sort(int sortcolumn, String sortmethod){
		this.sort(rows, sortmethod, sortcolumn);
	}
	/**
	 * Sort the collection of TableRows using a method (ascending,descending)
	 * and column index.
	 * 
	 * @param rows
	 * @param method : String, ASCENDING | DESCENDING
	 * @param column : index, index
	 */
	public void sort(SortableTableRow[] rows, String method, int column) {
		if (rows.length == 0)
			return;
		if (column >= rows.length) // if a illegal column is specified then do not sort
			return;
		Arrays.sort(rows,1,rows.length, SortableTableRow.sortComparator(method, column));
	}

	/**
	 * Return a new row that has been appended to the rows of the table.
	 * @return SortableTableRow
	 */
	public SortableTableRow newRow() {
		SortableTableRow[] newrows = new SortableTableRow[rows.length + 1];
		System.arraycopy(rows, 0, newrows, 0, rows.length);
		rows = newrows;
		SortableTableRow newrow = new SortableTableRow(columns);
		rows[rows.length - 1] = newrow;
		return newrow;
	}
	/**
	 * Return the row at the index
	 * @param row : SortableTableRow
	 * @return int
	 */
	public SortableTableRow getRow(int row) {
		return rows[row];
	}
	/**
	 * Return the number of rows
	 * @return int
	 */
	public int size(){
		return rows.length;
	}
	/**
	 * Write the HTML table with all its rows.
	 * The first row will become the table header. 
	 * Each column is clickable and causes a callback using the pageURL.
	 * @param html
	 * @param tableMap
	 * @param currentSortedcolumn
	 * @param currentSortmethodOrNull
	 */
	public void write(HTMLWriter html, Map tableMap, int currentSortedcolumn, String currentSortmethodOrNull) {
		if (tableMap == null) tableMap = new HashMap();
		// Ensure the tablemap has the id attribute
		tableMap.put("id", id);
		html.table(tableMap);
		tableMap.remove("id");
		if (rows.length > 0)
			this.rows[0].writeHeader(html, columnClickedFunction, currentSortedcolumn, currentSortmethodOrNull);
		for (int i = 1; i < rows.length; i++) {
			if (html.pretty) html.raw("\n");			
			if (i % 2 == 0) {
				evenRowMap.put("id", this.rowIDFrom(i));
				this.rows[i].write(html,evenRowMap,Colspan2Map);
			} else {
				oddRowMap.put("id", this.rowIDFrom(i));
				this.rows[i].write(html,oddRowMap,Colspan2Map);
			}
		}
		html.end();
	}
	private String rowIDFrom(int i) {
		return id+"."+String.valueOf(i - 1);
	}
}
