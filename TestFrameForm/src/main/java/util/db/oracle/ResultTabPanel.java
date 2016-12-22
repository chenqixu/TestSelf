package util.db.oracle;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * ������tab���
 * */
@SuppressWarnings("serial")
public class ResultTabPanel extends JPanel {
	public ResultTabPanel(List<List<List<String>>> allResultList){
		// ���ò��ֹ�������Ĭ�ϵĲ��ֹ������� BorderLayout,����û��ô����
		// ѡ��GridLayout(1,1)���ɣ���������Ϊһ��
		super(new GridLayout(1, 1));
		// ����JTabbedPane
		JTabbedPane tp = new JTabbedPane();
		// ���������
		if(allResultList!=null&&allResultList.size()>0){
			for(int i=0;i<allResultList.size();i++){
				// ������ǩ�µ�panel
				JPanel panel0 = createPanel("panel"+(i+1));
				// ָ����ǩ������ǩͼ�꣬panel������ʾ��Ϣ
				tp.addTab("panel"+(i+1), null, panel0, "result"+(i+1));
				// ���ƽ��
				buisnessDeal(allResultList.get(i),panel0);
			}
			// ���ú��ʵ���ʾ�ߴ磬����Ǳ���ģ���Ϊ������еı�ǩ��
			// ��ָ���ʺϵ���ʾ�ߴ磬ϵͳ�޷��жϳ�ʼ��ʾ�ߴ��С
			// Ĭ����ʹ����С�������Ҷ�һ����ǩ��Ƽ���
			tp.setPreferredSize(new Dimension(500, 500));
			// ��tabbedPanel��ӵ�Jpanel��
			add(tp);
			// ���ô��ڹ�Сʱ����ǩ����ʾ����
			tp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			// ���ñ�ǩͣ�ŵ�λ�ã���������Ϊ���ͣ��
			tp.setTabPlacement(JTabbedPane.TOP);
		}
	}
	
	private JPanel createPanel(String string) {
		// ����һ��JPanel����Ϊ���캯����ʼfalse
		// ��ʾ������˫����
		JPanel panel = new JPanel(false);
		// ���ò���
		panel.setLayout(new GridLayout(1, 1));
		// ����һ��label�ŵ�panel��
		JLabel filler = new JLabel(string);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.add(filler);
		return panel;
	}
	
	/**
	 * ҵ����
	 * */
	@SuppressWarnings("unchecked")
	private void buisnessDeal(List<List<String>> str, JPanel jp){
		String[][] cellData = new String[str.size()-1][str.get(0).size()];
		Object[] columnObj = str.get(0).toArray();
		String[] columnNames = new String[columnObj.length];
		for(int j=0;j<columnObj.length;j++){
			columnNames[j] = columnObj[j].toString();
		}
		
		Iterator it = str.iterator();
		int i = 0;
		while(it.hasNext()){
			List<String> tmp = (List<String>)it.next();
			if(i==0){
			}else{
				Object[] tmpObj = tmp.toArray();
				for(int x=0;x<tmpObj.length;x++){					
					cellData[i-1][x] = (tmpObj[x]==null?"NULL":tmpObj[x].toString());
				}
			}
			i++;
		}
		JTable table = new JTable(cellData, columnNames);
		table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);//ˮƽ������
		table = FitTableColumns(table);
		JScrollPane js2 = new JScrollPane(table);
		jp.removeAll();
		jp.add(js2);
		jp.revalidate();//ˢ��
	}
	
	/**
	 * �O��table���Ќ��S�����{��
	 * */
	public JTable FitTableColumns(JTable tmpTable) {
		JTable myTable = tmpTable;
	    JTableHeader header = myTable.getTableHeader();
	    int rowCount = myTable.getRowCount();
	    Enumeration columns = myTable.getColumnModel().getColumns();
	    while (columns.hasMoreElements()) {
	        TableColumn column = (TableColumn) columns.nextElement();
	        int col = header.getColumnModel().getColumnIndex(
	                column.getIdentifier());
	        int width = (int) myTable.getTableHeader().getDefaultRenderer()
	                .getTableCellRendererComponent(myTable,
	                        column.getIdentifier(), false, false, -1, col)
	                .getPreferredSize().getWidth();
	        for (int row = 0; row < rowCount; row++) {
	            int preferedWidth = (int) myTable.getCellRenderer(row, col)
	                    .getTableCellRendererComponent(myTable,
	                            myTable.getValueAt(row, col), false, false,
	                            row, col).getPreferredSize().getWidth();
	            width = Math.max(width, preferedWidth);
	        }
	        header.setResizingColumn(column);
	        column.setWidth(width + myTable.getIntercellSpacing().width);
	    }
	    return myTable;
	}
}
