package ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import pointSeries.PointSeries;
import transformer.ImageTransformer;
import explorer.LimitSetExplorer;
import explorer.TransformationExplorer;
import generator.Recipe;
import group.SL2C;
import number.Complex;

public class Display extends JPanel{

	private ArrayList<Complex> points = new ArrayList<>();
	private double magnification = 300;
	private int maxLevel = 35;
	private double threshold = 0.004;
	private SL2C[] gens;
	private PointSeries rootButterfly = null;
	private ArrayList<PointSeries> butterflies = new ArrayList<>();
	private Complex translation;
	private Complex t_a, t_b;

	public Display(){
		t_a = new Complex(1.91, 0.05);
		t_b = new Complex(1.91, 0.05);
		gens = Recipe.parabolicCommutatorGroup(t_a, t_b, true);
		
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
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		translation = new Complex(getWidth() / 2, getHeight() / 2);
		g.translate((int)translation.re(), (int) translation.im());
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
