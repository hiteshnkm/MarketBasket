package models.tables; /**
 * William Trent Holliday
 * 4/23/15
 */

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ReportTable extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private static String[] COLUMN_NAMES;
    private static List<String[]> reportList = new ArrayList<String[]>();

    public ReportTable(String[] columnNames, ArrayList<String[]> report){
        COLUMN_NAMES = columnNames;
        reportList = report;
    }

    public void clearItems(){
        reportList.clear();
    }

    @Override public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override public int getRowCount() {
        return reportList.size();
    }

    @Override public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override public Object getValueAt(final int rowIndex, final int columnIndex) {
            /*Adding components*/
        final String[] reportRow = reportList.get(rowIndex);
        return reportRow[columnIndex];
    }
}

