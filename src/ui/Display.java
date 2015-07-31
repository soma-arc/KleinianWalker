package ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import pointSeries.PointSeries;
import explorer.LimitSetExplorer;
import explorer.TransformationExplorer;
import generator.Recipe;
import group.SL2C;
import number.Complex;

public class Display extends JPanel{
	private static Display instance = new Display();
	private ArrayList<Complex> points = new ArrayList<>();
	private double limitSetMagnification = 300;
	private int limitSetMaxLevel = 35;
	private int pointSeriesMaxLevel = 5;
	private double threshold = 0.004;
	private SL2C[] gens;
	private PointSeries rootButterfly = null;
	private PointSeries stepButterfly, initialButterfly;
	private ArrayList<PointSeries> butterflies = new ArrayList<>();
	private Complex translation;
	private Complex t_a, t_b;
	private boolean isT_abPlus = true;
	private Thread calcLimitSetThread = new Thread();
	private PointSeriesDisplayMode pointSeriesDisplayMode = PointSeriesDisplayMode.SEARCH;
	private boolean drawRootButterflyPosition = false;

	private Display(){
		t_a = new Complex(1.91, 0.05);
		t_b = new Complex(1.91, 0.05);
		gens = Recipe.parabolicCommutatorGroup(t_a, t_b, isT_abPlus);

		LimitSetExplorer lsExp = new LimitSetExplorer(gens);
		points = lsExp.runDFS(limitSetMaxLevel, threshold);
		
		try {
			initialButterfly = PointSeries.readData(PointSeries.DATA_DIR_NAME+"butterfly.points").scale(0.125).translate(new Complex(0.5));
		} catch (IOException e) {
			e.printStackTrace();
		}

		stepButterfly = initialButterfly.copy(); 
		rootButterfly = initialButterfly.copy();

		recalcPointSeries();

		addMouseListener(new MousePressedAdapter());
		addMouseMotionListener(new MouseDraggedAdapter());
		requestFocus();
	}

	public static Display getInstance(){
		return instance;
	}

	public void paintComponent(Graphics g){
		requestFocus();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());

		translation = new Complex(getWidth() / 2, getHeight() / 2);
		g2.translate((int)translation.re(), (int) translation.im());
		drawLimitSet(g2);
		drawPointSeries(g2);
	}

	private void drawLimitSet(Graphics2D g2){
		g2.setColor(Color.ORANGE);
		for(int i = 0 ; i < points.size(); i+= 3){
			Complex point = points.get(i);
			Complex point2 = points.get(i+1);
			Complex point3 = points.get(i+2);
			g2.drawLine((int) (point.re() * limitSetMagnification), (int) (point.im() * limitSetMagnification), (int) (point2.re() * limitSetMagnification), (int) (point2.im() * limitSetMagnification));
			g2.drawLine((int) (point2.re() * limitSetMagnification), (int) (point2.im() * limitSetMagnification), (int) (point3.re() * limitSetMagnification), (int) (point3.im() * limitSetMagnification));
		}
	}
	
	private void drawPointSeries(Graphics2D g2){
	    if(pointSeriesDisplayMode == PointSeriesDisplayMode.SEARCH){
	    	if(drawRootButterflyPosition){
	    		rootButterfly.drawBounds(g2, limitSetMagnification);
	    	}
	    	GradientPaint gp = new GradientPaint(10,10,Color.GREEN,50,10,Color.BLUE,true);
		    g2.setPaint(gp);
	    	for(PointSeries butterfly : butterflies){
				butterfly.draw(g2, limitSetMagnification);
			}
		}else if(pointSeriesDisplayMode == PointSeriesDisplayMode.STEP){
			GradientPaint gp = new GradientPaint(10,10,Color.GREEN,50,10,Color.BLUE,true);
		    g2.setPaint(gp);
			stepButterfly.draw(g2, limitSetMagnification);
		}
	}

	public void setT_a(Complex t_a){
		this.t_a = t_a;
	}
	
	public void setT_b(Complex t_b){
		this.t_b = t_b;
	}
	
	public void setIsT_abPlus(boolean isT_abPlus){
		this.isT_abPlus = isT_abPlus;
	}
	
	public void setLimitSetMaxLevel(int maxLevel){
		this.limitSetMaxLevel = maxLevel;
	}
	
	public void setPointSeriesMaxLevel(int pointSeriesMaxLevel){
		this.pointSeriesMaxLevel = pointSeriesMaxLevel;
	}
	
	public void setThreshold(double threshold){
		this.threshold = threshold;
	}
	
	public void setLimitSetMagnification(int limitSetMagnification){
		this.limitSetMagnification = limitSetMagnification;
	}
	
	public void setPointSeriesDisplayMode(PointSeriesDisplayMode mode){
		this.pointSeriesDisplayMode = mode;
	}
	
	public void setDrawRootButterflyPosition(boolean drawRootButterflyPosition){
		this.drawRootButterflyPosition = drawRootButterflyPosition;
	}
	
	public void recalc(){
		gens = Recipe.parabolicCommutatorGroup(t_a, t_b, isT_abPlus);

		if(calcLimitSetThread.isAlive())
			stopCalculation();
		calcLimitSetThread = new Thread(new CalcLimitSetTask());
		calcLimitSetThread.start();

		recalcPointSeries();
	}
	
	public void recalcPointSeries(){
		if(rootButterfly == null) return;
		if(pointSeriesDisplayMode == PointSeriesDisplayMode.SEARCH){
			TransformationExplorer tExp = new TransformationExplorer(gens);
			butterflies = tExp.runBFS(pointSeriesMaxLevel, rootButterfly, limitSetMagnification);
		}else if(pointSeriesDisplayMode == PointSeriesDisplayMode.STEP){
			
		}else{
			
		}
		repaint();
	}
	
	public void stepPointSeries(int generatorIndex){
		if(generatorIndex < 0 || gens.length <= generatorIndex) return;
		stepButterfly = stepButterfly.transform(gens[generatorIndex]);
	}
	
	public void initPointSeries(){
		if(pointSeriesDisplayMode == PointSeriesDisplayMode.SEARCH){
			rootButterfly = initialButterfly.copy();
			recalcPointSeries();
		}else if(pointSeriesDisplayMode == PointSeriesDisplayMode.STEP){
			stepButterfly = initialButterfly.copy();
		}
	}
	
	public void stopCalculation(){
		if(calcLimitSetThread.isAlive()){
			calcLimitSetThread.interrupt();
		}
	}
	
	private class CalcLimitSetTask implements Runnable{
		@Override
		public void run(){
			synchronized (points) {
				LimitSetExplorer lsExp = new LimitSetExplorer(gens);
				try {
					points = lsExp.runDFS(limitSetMaxLevel, threshold, calcLimitSetThread);
				} catch (InterruptedException e) {
					return;
				}
			}
			ControlPanel.getInstance().setStateLabelText("state::");
			repaint();
		}
	}

	private Complex previousPos = null;
	private class MousePressedAdapter extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e){
			if(rootButterfly.isClicked(e.getX(), e.getY(), limitSetMagnification, translation)){
				previousPos = new Complex((e.getX() - translation.re()) / limitSetMagnification, (e.getY()- translation.im()) / limitSetMagnification);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e){
			previousPos = null;
		}
	}

	private class MouseDraggedAdapter extends MouseMotionAdapter{
		@Override
		public void mouseDragged(MouseEvent e){
			if(previousPos != null && pointSeriesDisplayMode == PointSeriesDisplayMode.SEARCH){
				Complex currentPos = new Complex((e.getX() - translation.re()) / limitSetMagnification, (e.getY()- translation.im()) / limitSetMagnification);
				rootButterfly.translate(currentPos.sub(previousPos));
				previousPos = currentPos;
				
				TransformationExplorer tExp = new TransformationExplorer(gens);
				butterflies = tExp.runBFS(pointSeriesMaxLevel, rootButterfly, limitSetMagnification);
				repaint();
			}
		}
	}
}
