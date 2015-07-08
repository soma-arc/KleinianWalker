package pointSeries;

import java.awt.Graphics;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import number.Complex;

public class PointSeries {
	public static final String DATA_DIR_NAME = "pointsData\\";
	public ArrayList<Complex> points = new ArrayList<>();

	public PointSeries(ArrayList<Complex> points){
		this.points = points;
	}
	
	public ArrayList<Complex> getPointsList(){
		return points;
	}
	
	public void draw(Graphics g, double magnification){
		int[] x = new int[points.size() * 2];
		int[] y = new int[points.size() * 2];
		for(int i = 0 ; i < points.size() ; i++){
			x[i] = (int)( points.get(i).re() * magnification);
			y[i] = (int)( points.get(i).im() * magnification);
		}
		g.fillPolygon(x, y, points.size());
	}
	
	public static PointSeries readData(String fileName) throws IOException{
		ArrayList<Complex> points = new ArrayList<>();
		FileReader in = new FileReader(new File(fileName));
		BufferedReader reader = new BufferedReader(in);
		String line = null;
		while((line = reader.readLine()) != null){
			String[] elems = line.split(",");
			points.add(new Complex(Double.valueOf(elems[0]), Double.valueOf(elems[1])).mult(0.125).add(0.5));
		}
		reader.close();
		in.close();
		return new PointSeries(points);
	}
}
