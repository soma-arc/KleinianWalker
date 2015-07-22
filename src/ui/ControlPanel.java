package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import number.Complex;

import com.sun.corba.se.spi.orbutil.fsm.ActionBase;

public class ControlPanel extends JPanel{
	private static ControlPanel instance = new ControlPanel();
	private ControlPanel(){
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setMinimumSize(new Dimension(300, 3000));
		setMaximumSize(new Dimension(300, 3000));
		setPreferredSize(new Dimension(300, 3000));
		setUI();
	}

	public static ControlPanel getInstance(){
		return instance;
	}

	private JSpinner t_aRealSpinner;
	private JSpinner t_aImageSpinner; 
	private JSpinner t_bRealSpinner;
	private JSpinner t_bImageSpinner;
	private JCheckBox autoRecalcCheck;
	private JButton recalcButton, cancelButton;
	private JRadioButton t_abPlus, t_abMinus;
	private JLabel stateLabel;
	private void setUI(){
		t_aRealSpinner = createParameterSpinner(1.91, null, null, 0.01);
		t_aRealSpinner.getModel().addChangeListener(new ParamChangeListener());
		t_aImageSpinner = createParameterSpinner(0.05, null, null, 0.01);
		t_aImageSpinner.getModel().addChangeListener(new ParamChangeListener());
		HorizontalPanel t_aPanel = new HorizontalPanel();
		t_aPanel.add(new JLabel("t_a"));
		t_aPanel.add(t_aRealSpinner);
		t_aPanel.add(new JLabel("+"));
		t_aPanel.add(t_aImageSpinner);
		t_aPanel.add(new JLabel("i"));

		t_bRealSpinner = createParameterSpinner(1.91, null, null, 0.01);
		t_bRealSpinner.getModel().addChangeListener(new ParamChangeListener());
		t_bImageSpinner = createParameterSpinner(0.05, null, null, 0.01);
		t_bImageSpinner.getModel().addChangeListener(new ParamChangeListener());
		HorizontalPanel t_bPanel = new HorizontalPanel();
		t_bPanel.add(new JLabel("t_b"));
		t_bPanel.add(t_bRealSpinner);
		t_bPanel.add(new JLabel("+"));
		t_bPanel.add(t_bImageSpinner);
		t_bPanel.add(new JLabel("i"));
		
		HorizontalPanel t_abPanel = new HorizontalPanel();
		ButtonGroup t_abGroup = new ButtonGroup();
		t_abPlus = new JRadioButton("plus");
		t_abPlus.setSelected(true);
		t_abMinus = new JRadioButton("minus");
		t_abGroup.add(t_abPlus);
		t_abGroup.add(t_abMinus);
		t_abPanel.add(new JLabel("t_ab "));
		t_abPanel.add(t_abPlus);
		t_abPanel.add(t_abMinus);
		
		autoRecalcCheck = new JCheckBox("自動再計算");
		recalcButton = new JButton("再計算");
		recalcButton.addActionListener(new RecalcButtonActionListener());
		
		cancelButton = new JButton("キャンセル");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Display.getInstance().stopCalculation();
				stateLabel.setText("cancelled");
			}
		});
		stateLabel = new JLabel();
		add(new JLabel("パラメータ"));
		add(t_aPanel);
		add(t_bPanel);
		add(t_abPanel);
		add(autoRecalcCheck);
		add(recalcButton);
		add(cancelButton);
		add(stateLabel);
	}
	
	private JSpinner createParameterSpinner(Number value, Comparable<?> minimum, Comparable<?> maximum, Number stepSize){
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, minimum, maximum, stepSize));
		spinner.setMaximumSize(new Dimension(50, 20));
		return spinner;
	}
	
	private void recalc(){
		Complex t_a = new Complex((double) t_aRealSpinner.getValue(),
				(double) t_aImageSpinner.getValue());
		Display.getInstance().setT_a(t_a);
		Complex t_b = new Complex((double) t_bRealSpinner.getValue(),
				(double) t_bImageSpinner.getValue());
		Display.getInstance().setT_b(t_b);
		Display.getInstance().setIsT_abPlus(t_abPlus.isSelected());
		Display.getInstance().recalc();
		stateLabel.setText("calculating...");
	}
	
	public void setStateLabelText(String stateText){
		stateLabel.setText(stateText);
	}
	
	private class ParamChangeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e){
			if(autoRecalcCheck.isSelected())
				recalc();
		}
	}
	
	private class RecalcButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			recalc();
		}
	}
}
