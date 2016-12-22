package com.frame;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class CheckListCellRenderer extends JCheckBox implements ListCellRenderer,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static Border noFocusBorder;
	
    /**
     * Constructs a default renderer object for an item
     * in a list.
     */
    public CheckListCellRenderer() {
        super();
        if (noFocusBorder == null) {
            noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        }
        setOpaque(true);
        setBorder(noFocusBorder);
    }
    
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        if (value instanceof MyCheckBox) {
        	MyCheckBox ckValue = (MyCheckBox) value;
            this.setText(ckValue.value == null ? "" : ckValue.value);
            this.setSelected(ckValue.bolValue);
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setBorder((cellHasFocus) ?
                  UIManager.getBorder("List.focusCellHighlightBorder") :
                  noFocusBorder);
        return this;
    }

}
