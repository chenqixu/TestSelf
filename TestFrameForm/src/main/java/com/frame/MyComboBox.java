package com.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

public class MyComboBox extends JComboBox implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyComboBox(){
		addItem(new MyCheckBox(false, "Select All"));
        this.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                itemSelected();
            }
        });
	}
	
	private void itemSelected(){
		if (getSelectedItem() instanceof MyCheckBox) {
			if (getSelectedIndex() == 0) {
                selectedAllItem();
            } else {
            	MyCheckBox jcb = (MyCheckBox) getSelectedItem();
                jcb.bolValue = (!jcb.bolValue);
                setSelectedIndex(getSelectedIndex());
            }
            SwingUtilities.invokeLater(
                    new Runnable() {
                public void run() {
                    /*选中后依然保持当前弹出状态*/
                    showPopup();
                }
            });
		}
	}
	
	private void selectedAllItem() {
        boolean bl = false;
        for (int i = 0; i < getItemCount(); i++) {
        	MyCheckBox jcb = (MyCheckBox) getItemAt(i);
            if (i == 0) {
                bl = !jcb.bolValue;
            }
            jcb.bolValue = (bl);
        }
        setSelectedIndex(0);
    }
	
    /*获取选取的对象*/
    public Vector getComboVc() {
        Vector<String> vc = new Vector<String>();
        for (int i = 1; i < getItemCount(); i++) {
        	MyCheckBox jcb = (MyCheckBox) getItemAt(i);
            if (jcb.bolValue) {
                vc.add(jcb.value);
            }
        }
        return vc;
    }
}
