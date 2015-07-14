package ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	PointSeries rootButtefly;
	ArrayList<PointSeries> butterflies = new ArrayList<>();

	public Display(){
		gens = Recipe.parabolicCommutatorGroup(new Complex(1.91, 0.05), new Complex(1.91, 0.05), true);
		
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
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.translate(getWidth() / 2, getHeight()/2);
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
}
