package com.philemonworks.itable;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.philemonworks.writer.HTMLWriter;

/**
 * TableRow is a container for an array of cells representing a row in a SortableTable. A TableRow also holds optional
 * an array of sortkeys and can do the sorting.
 * 
 * @author E.M.Micklei
 */
public class SortableTableRow {
	/**
	 * Contains (raw) HTML content
	 */
	private String[] cells = new String[0];
	/**
	 * If an entry is null then use the one stored in cells.
	 */
	private String[] sortkeys = new String[0];

	/**
	 * Create an empty row. Cells can be added.
	 */
	public SortableTableRow() {
		super();
	}
	/**
	 * Create an (initially) fixed row. Cells can be inserted or added.
	 * 
	 * @param length
	 *        int
	 */
	public SortableTableRow(int length) {
		super();
		this.cells = new String[length];
		this.sortkeys = new String[length];
		for (int i = 0; i < cells.length; cells[i++] = "")
			;
	}
	/**
	 * @param i,
	 *        index into sortkeys | cells
	 * @return string key for sorting
	 */
	public String getSortkey(int i) {
		String sortkey = sortkeys[i];
		return sortkey == null ? cells[i] : sortkey;
	}
	/**
	 * Add a new cell to the row.
	 * 
	 * @param cell ,
	 *        raw HTML content
	 * @return the row (for cascading style coding)
	 */
	public SortableTableRow add(String cell) {
		return this.add(cell, null); // null means use the cell as sortkey
	}
	/**
	 * Add a new cell to the row with its sortkey (may be the same).
	 * 
	 * @param cell ,
	 *        raw HTML content
	 * @param sortkey,
	 *        String (trimmed)
	 * @return the row (for cascading style coding)
	 */
	public SortableTableRow add(String cell, String sortkey) {
		if (cells.length == 0) {
			cells = new String[] { cell };
			sortkeys = new String[] { sortkey };
			return this;
		}
		String[] newcells = new String[cells.length + 1];
		String[] newsortkeys = new String[cells.length + 1];
		System.arraycopy(cells, 0, newcells, 0, cells.length);
		System.arraycopy(sortkeys, 0, newsortkeys, 0, cells.length);
		newcells[cells.length] = cell;
		newsortkeys[cells.length] = sortkey;
		cells = newcells;
		sortkeys = newsortkeys;
		return this;
	}
	/**
	 * Insert a cell into the row.
	 * 
	 * @param cell ,
	 *        raw HTML content
	 * @return the row (for cascading style coding)
	 */
	public SortableTableRow put(int index, String cell) {
		return this.put(index, cell, null);
	}
	/**
	 * Insert a cell into the row and a key for sorting.
	 * 
	 * @param cell ,
	 *        raw HTML content
	 * @param sortkey,
	 *        String (trimmed)
	 * @return the row (for cascading style coding)
	 */
	public SortableTableRow put(int index, String cell, String sortkey) {
		cells[index] = cell;
		sortkeys[index] = sortkey;
		return this;
	}
	/**
	 * Write the header part of the table
	 * 
	 * @param html
	 * @param columnClickedFunction : String
	 * @param sortcolumn
	 * @param sortmethod
	 */
	public void writeHeader(HTMLWriter html, String columnClickedFunction, int sortcolumn, String sortmethod) {
		html.tr();
		for (int i = 0; i < cells.length; i++) {
			String sortinvocation = this.buildColumnClicked(
					columnClickedFunction, 
					i, 
					this.nextSortMethod(sortcolumn == i ? sortmethod : null));
			// label
			html.tag("th",html.newMap("onclick",sortinvocation,"class","sortlink"),false);
//			html.tag("a", html.newMap(
//					"href", 
//					sortinvocation, 
//					"class", "sortlink"),
//					false);
			html.raw(cells[i]);
//			html.end("a");
			html.end("th");
			// icon
			if (sortmethod == null || sortcolumn != i) {
				html.tagged("th", null, "&nbsp;", false);
			} else {
				html.tag("th", html.newMap("class", "sort" + sortmethod), false);
				html.tag("a", html.newMap(
						"href", 
						sortinvocation,
						"class", "sortlink"),
						false);
				html.nbsp();
				html.nbsp();
				html.end("a");
				html.end("th");
			}
		}
		html.end();
	}
	
	/** 
	 * Return the javascript function call that is invoked when clicking a column.
	 * 
	 * @param columnClickedFunction
	 * @param sortcolumn
	 * @param sortmethod
	 * @return String
	 */
	private String buildColumnClicked(String columnClickedFunction, int sortcolumn, String sortmethod) {
		return "javascript:" + columnClickedFunction + "(this," + sortcolumn + ",'" + sortmethod + "');";
	}
	/**
	 * Answer the logical next method for sorting, switching between ASC and DESC.
	 * 
	 * @param sortmethod :
	 *        String
	 * @return SortableTable.ASCENDING | SortableTable.DESCENDING
	 */
	private String nextSortMethod(String sortmethod) {
		if (sortmethod == null)
			return SortableTable.ASCENDING;
		if (SortableTable.ASCENDING.equals(sortmethod))
			return SortableTable.DESCENDING;
		return SortableTable.ASCENDING;
	}
	/**
	 * Write a HTML row (tr,td) with cell data
	 * 
	 * @param html
	 *        HTMLWriter
	 * @param rowMap ,
	 *        Map for attributes
	 * @param cellMap ,
	 *        Map for cells        
	 */
	public void write(HTMLWriter html, Map rowMap, Map cellMap) {
		html.tr(rowMap);
		for (int i = 0; i < cells.length; i++) {
			html.tagged("td", cellMap, cells[i], false); // no encoding
		}
		html.end();
	}
	/**
	 * Return debug info
	 */
	public String toString() {
		StringWriter sw = new StringWriter();
		sw.write("TableRow[");
		for (int i = 0; i < cells.length; i++) {
			if (i > 0)
				sw.write(',');
			sw.write(cells[i]);
		}
		sw.write("]");
		return sw.toString();
	}
	/**
	 * Return number of columns
	 * 
	 * @return length
	 */
	public int length() {
		return cells.length;
	}
	/**
	 * Return a comparator for sorting a SortableTableRow[] using a given method and column index.
	 * 
	 * @param method
	 * @param i
	 * @return the comparator instance
	 */
	public static Comparator sortComparator(String method, int i) {
		if (SortableTable.ASCENDING.equals(method))
			return ascendingSortComparator(i);
		if (SortableTable.DESCENDING.equals(method))
			return descendingSortComparator(i);
		throw new IllegalArgumentException("Unknown sort method:" + method);
	}
	/**
	 * Return a comparator for descending sorting a TableRow[] and column index. Arranging data from high to low
	 * sequence; for example, from Z to A or from 9 to 0.
	 * 
	 * @param i
	 * @return the comparator instance
	 */
	private static Comparator descendingSortComparator(final int i) {
		return new Comparator() {
			public int compare(Object arg0, Object arg1) {
				SortableTableRow row0 = (SortableTableRow) arg0;
				SortableTableRow row1 = (SortableTableRow) arg1;
				return row1.getSortkey(i).compareToIgnoreCase(row0.getSortkey(i));
			};
		};
	}
	/**
	 * Return a comparator for ascending sorting a TableRow[] and column index. Arranging data from the normal low to
	 * high sequence; for example, from A to Z or from 0 to 9
	 * 
	 * @param i
	 * @return the comparator instance
	 */
	private static Comparator ascendingSortComparator(final int i) {
		return new Comparator() {
			public int compare(Object arg0, Object arg1) {
				SortableTableRow row0 = (SortableTableRow) arg0;
				SortableTableRow row1 = (SortableTableRow) arg1;
				return row0.getSortkey(i).compareToIgnoreCase(row1.getSortkey(i));
			};
		};
	}
}
