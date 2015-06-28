package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import explorer.LimitSetExplorer;
import generator.Recipe;
import number.Complex;

public class Display extends JPanel{
	
	private ArrayList<Complex> points = new ArrayList<>();
	private double magnification = 300;
	private int maxLevel = 20;
	private double threshold = 0.003;
	
	public Display(){
		LimitSetExplorer exp = new LimitSetExplorer(Recipe.parabolicCommutatorGroup(new Complex(-2), new Complex(-2), true));
		points = exp.runDFS(maxLevel, threshold);
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.translate(getWidth() / 2, getHeight()/2);
		drawLimitSet(g2);
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
