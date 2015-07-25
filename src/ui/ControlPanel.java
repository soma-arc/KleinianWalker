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

import explorer.LimitSetExplorer;

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

	private JSpinner t_aRealSpinner, t_aImageSpinner; 
	private JSpinner t_bRealSpinner, t_bImageSpinner;
	private JSpinner limitSetMaxLevelSpinner;
	private JSpinner pointSeriesMaxLevelSpinner;
	private JSpinner thresholdSpinner;
	private JSpinner limitSetMagnificationSpinner;
	private JCheckBox autoRecalcCheck;
	private JButton recalcButton, cancelButton;
	private JRadioButton t_abPlus, t_abMinus;
	private JLabel stateLabel;
	private JRadioButton searchPointSeriesButton, stepPointSeriesButton, nonePointSeriesButton;
	private HorizontalPanel step_aA_ButtonsPanel, step_bB_ButtonsPanel;
	private HorizontalPanel pointSeriesMaxLevelSpinnerPanel;
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
		
		HorizontalPanel limitSetMaxLevelSpinnerPanel = new HorizontalPanel();
		limitSetMaxLevelSpinner = createParameterSpinner(35, 1, null, 1);
		limitSetMaxLevelSpinner.getModel().addChangeListener(new ParamChangeListener());
		limitSetMaxLevelSpinnerPanel.add(new JLabel("limit set max level "));
		limitSetMaxLevelSpinnerPanel.add(limitSetMaxLevelSpinner);
		
		HorizontalPanel thresholdSpinnerPanel = new HorizontalPanel();
		thresholdSpinner = createParameterSpinner(0.004, 0.0, null, 0.0001);
		thresholdSpinner.getModel().addChangeListener(new ParamChangeListener());
		thresholdSpinnerPanel.add(new JLabel("limit set threshold "));
		thresholdSpinnerPanel.add(thresholdSpinner);
		
		HorizontalPanel limitSetMagnificationPanel = new HorizontalPanel();
		limitSetMagnificationSpinner = createParameterSpinner(300, 1, null, 10);
		limitSetMagnificationSpinner.getModel().addChangeListener(new MagnificationChangeListener());
		limitSetMagnificationPanel.add(new JLabel("limit set magnification"));
		limitSetMagnificationPanel.add(limitSetMagnificationSpinner);


		pointSeriesMaxLevelSpinnerPanel = new HorizontalPanel();
		pointSeriesMaxLevelSpinner = createParameterSpinner(5, 0, null, 1);
		pointSeriesMaxLevelSpinner.getModel().addChangeListener(new PointSeriesParamChangeListener());
		pointSeriesMaxLevelSpinnerPanel.add(new JLabel("point series max level "));
		pointSeriesMaxLevelSpinnerPanel.add(pointSeriesMaxLevelSpinner);

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

		step_aA_ButtonsPanel = new HorizontalPanel();
		JButton gen0Button = new JButton("a");
		gen0Button.addActionListener(new StepButtonListener(0));
		JButton gen2Button = new JButton("A");
		gen2Button.addActionListener(new StepButtonListener(2));
		step_aA_ButtonsPanel.add(gen0Button);
		step_aA_ButtonsPanel.add(gen2Button);
		
		step_bB_ButtonsPanel = new HorizontalPanel();
		JButton gen1Button = new JButton("b");
		gen1Button.addActionListener(new StepButtonListener(1));
		JButton gen3Button = new JButton("B");
		gen3Button.addActionListener(new StepButtonListener(3));
		step_bB_ButtonsPanel.add(gen1Button);
		step_bB_ButtonsPanel.add(gen3Button);

		JButton initPointSeriesButton = new JButton("初期化");
		initPointSeriesButton.addActionListener(new InitButtonListener());

		HorizontalPanel pointSeriesModePanel = new HorizontalPanel();
		searchPointSeriesButton = new JRadioButton("探索");
		searchPointSeriesButton.addChangeListener(new ModeChangeListener());
		searchPointSeriesButton.setSelected(true);
		stepPointSeriesButton = new JRadioButton("ステップ");
		stepPointSeriesButton.addChangeListener(new ModeChangeListener());
		nonePointSeriesButton = new JRadioButton("なし");
		nonePointSeriesButton.addChangeListener(new ModeChangeListener());
		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(searchPointSeriesButton);
		modeGroup.add(stepPointSeriesButton);
		modeGroup.add(nonePointSeriesButton);
		pointSeriesModePanel.add(searchPointSeriesButton);
		pointSeriesModePanel.add(stepPointSeriesButton);
		pointSeriesModePanel.add(nonePointSeriesButton);
		
		stateLabel = new JLabel();
		add(new JLabel("パラメータ"));
		add(t_aPanel);
		add(t_bPanel);
		add(t_abPanel);
		add(limitSetMaxLevelSpinnerPanel);
		add(thresholdSpinnerPanel);
		add(limitSetMagnificationPanel);
		add(autoRecalcCheck);
		add(recalcButton);
		add(cancelButton);
		
		add(pointSeriesModePanel);
		add(pointSeriesMaxLevelSpinnerPanel);
		add(step_aA_ButtonsPanel);
		add(step_bB_ButtonsPanel);
		add(initPointSeriesButton);
		
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
		Display.getInstance().setLimitSetMaxLevel((int) limitSetMaxLevelSpinner.getValue());
		Display.getInstance().setThreshold((double) thresholdSpinner.getValue());
		Display.getInstance().setPointSeriesMaxLevel((int) pointSeriesMaxLevelSpinner.getValue());
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
	
	private class PointSeriesParamChangeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e){
			Display.getInstance().setPointSeriesMaxLevel((int) pointSeriesMaxLevelSpinner.getValue());
			Display.getInstance().recalcPointSeries();
			Display.getInstance().repaint();
		}
	}
	
	private class RecalcButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			recalc();
		}
	}
	
	private class MagnificationChangeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e){
			Display.getInstance().setLimitSetMagnification((int) limitSetMagnificationSpinner.getValue());
			Display.getInstance().repaint();
		}
	}
	
	private class ModeChangeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e){
			if(searchPointSeriesButton.isSelected()){
				Display.getInstance().setPointSeriesDisplayMode(PointSeriesDisplayMode.SEARCH);
				step_aA_ButtonsPanel.setVisible(false);
				step_bB_ButtonsPanel.setVisible(false);
				pointSeriesMaxLevelSpinnerPanel.setVisible(true);
			}else if(stepPointSeriesButton.isSelected()){
				Display.getInstance().setPointSeriesDisplayMode(PointSeriesDisplayMode.STEP);
				step_aA_ButtonsPanel.setVisible(true);
				step_bB_ButtonsPanel.setVisible(true);
				pointSeriesMaxLevelSpinnerPanel.setVisible(false);
			}else{
				Display.getInstance().setPointSeriesDisplayMode(PointSeriesDisplayMode.NONE);
				step_aA_ButtonsPanel.setVisible(false);
				step_bB_ButtonsPanel.setVisible(false);
				pointSeriesMaxLevelSpinnerPanel.setVisible(false);
			}
			Display.getInstance().repaint();
		}
	}
	
	private class StepButtonListener implements ActionListener{
		int generatorIndex;
		public StepButtonListener(int generatorIndex){
			this.generatorIndex = generatorIndex;
		}
		
		@Override
		public void actionPerformed(ActionEvent e){
			Display.getInstance().stepPointSeries(generatorIndex);
			Display.getInstance().repaint();
		}
	}
	
	private class InitButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			Display.getInstance().initPointSeries();
			Display.getInstance().repaint();
			
		}
	}
}
