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
 * 多个结果tab面板
 * */
@SuppressWarnings("serial")
public class ResultTabPanel extends JPanel {
	public ResultTabPanel(List<List<List<String>>> allResultList){
		// 设置布局管理器，默认的布局管理器是 BorderLayout,这里没那么复杂
		// 选择GridLayout(1,1)即可，就是整个为一块
		super(new GridLayout(1, 1));
		// 创建JTabbedPane
		JTabbedPane tp = new JTabbedPane();
		// 如果有数据
		if(allResultList!=null&&allResultList.size()>0){
			for(int i=0;i<allResultList.size();i++){
				// 创建标签下的panel
				JPanel panel0 = createPanel("panel"+(i+1));
				// 指定标签名，标签图标，panel，和提示信息
				tp.addTab("panel"+(i+1), null, panel0, "result"+(i+1));
				// 绘制结果
				buisnessDeal(allResultList.get(i),panel0);
			}
			// 设置合适的显示尺寸，这个是必须的，因为如果所有的标签都
			// 不指定适合的显示尺寸，系统无法判断初始显示尺寸大小
			// 默认是使用最小化，并且对一个标签设计即可
			tp.setPreferredSize(new Dimension(500, 500));
			// 将tabbedPanel添加到Jpanel中
			add(tp);
			// 设置窗口过小时，标签的显示策略
			tp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			// 设置标签停放的位置，这里设置为左侧停放
			tp.setTabPlacement(JTabbedPane.TOP);
		}
	}
	
	private JPanel createPanel(String string) {
		// 创建一个JPanel，并为构造函数初始false
		// 表示不适用双缓冲
		JPanel panel = new JPanel(false);
		// 设置布局
		panel.setLayout(new GridLayout(1, 1));
		// 创建一个label放到panel中
		JLabel filler = new JLabel(string);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.add(filler);
		return panel;
	}
	
	/**
	 * 业务处理
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
		table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);//水平滚动条
		table = FitTableColumns(table);
		JScrollPane js2 = new JScrollPane(table);
		jp.removeAll();
		jp.add(js2);
		jp.revalidate();//刷新
	}
	
	/**
	 * 設置table的列寬隨內容調整
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
