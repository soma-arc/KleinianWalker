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
	private double magnification = 300;
	private int maxLevel = 35;
	private double threshold = 0.004;
	private SL2C[] gens;
	private PointSeries rootButterfly = null;
	private ArrayList<PointSeries> butterflies = new ArrayList<>();
	private Complex translation;
	private Complex t_a, t_b;
	private boolean isT_abPlus = true;
	private Thread calcLimitSetThread = new Thread();
	
	private Display(){
		t_a = new Complex(1.91, 0.05);
		t_b = new Complex(1.91, 0.05);
		gens = Recipe.parabolicCommutatorGroup(t_a, t_b, isT_abPlus);

		LimitSetExplorer lsExp = new LimitSetExplorer(gens);
		points = lsExp.runDFS(maxLevel, threshold);
		
		try {
			rootButterfly = PointSeries.readData(PointSeries.DATA_DIR_NAME+"butterfly.points").scale(0.125).translate(new Complex(0.5));
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		if(rootButterfly == null) return;
		TransformationExplorer tExp = new TransformationExplorer(gens);
		butterflies = tExp.runBFS(5, rootButterfly);

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
		
		GradientPaint gp = new GradientPaint(10,10,Color.GREEN,50,10,Color.BLUE,true);
	    g2.setPaint(gp);
		for(PointSeries butterfly : butterflies){
			butterfly.draw(g2, magnification);
		}
		
	}

	private void drawLimitSet(Graphics2D g2){
		g2.setColor(Color.ORANGE);
		for(int i = 0 ; i < points.size(); i+= 3){
			Complex point = points.get(i);
			Complex point2 = points.get(i+1);
			Complex point3 = points.get(i+2);
			g2.drawLine((int) (point.re() * magnification), (int) (point.im() * magnification), (int) (point2.re() * magnification), (int) (point2.im() * magnification));
			g2.drawLine((int) (point2.re() * magnification), (int) (point2.im() * magnification), (int) (point3.re() * magnification), (int) (point3.im() * magnification));
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
	
	public void recalc(){
		gens = Recipe.parabolicCommutatorGroup(t_a, t_b, isT_abPlus);

		if(calcLimitSetThread.isAlive())
			stopCalculation();
		calcLimitSetThread = new Thread(new CalcLimitSetTask());
		calcLimitSetThread.start();

		if(rootButterfly == null) return;
		TransformationExplorer tExp = new TransformationExplorer(gens);
		butterflies = tExp.runBFS(5, rootButterfly);
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
					points = lsExp.runDFS(maxLevel, threshold, calcLimitSetThread);
				} catch (InterruptedException e) {
					return;
				}
			}
			ControlPanel.getInstance().setStateLabelText("");
			repaint();
		}
	}

	private Complex previousPos = null;
	private class MousePressedAdapter extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e){
			if(rootButterfly.isClicked(e.getX(), e.getY(), magnification, translation)){
				previousPos = new Complex((e.getX() - translation.re()) / magnification, (e.getY()- translation.im()) / magnification);
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
			if(previousPos != null){
				Complex currentPos = new Complex((e.getX() - translation.re()) / magnification, (e.getY()- translation.im()) / magnification);
				rootButterfly.translate(currentPos.sub(previousPos));
				previousPos = currentPos;
				
				TransformationExplorer tExp = new TransformationExplorer(gens);
				butterflies = tExp.runBFS(5, rootButterfly);
				repaint();
			}
		}
	}
}
