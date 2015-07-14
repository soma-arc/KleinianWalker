package pointSeries;

import group.SL2C;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


import mobius.Mobius;
import number.Complex;

public class PointSeries {
	public static final String DATA_DIR_NAME = "pointsData\\";
	public ArrayList<Complex> points = new ArrayList<>();
	public double scale = 1.0;
	public Complex translation = Complex.ZERO;

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
			Complex point = points.get(i);
			x[i] = (int)( point.re() * magnification);
			y[i] = (int)( point.im() * magnification);
		}
		g.fillPolygon(x, y, points.size());
	}
	
	public PointSeries transform(SL2C t){
		ArrayList<Complex> transformedPoints = new ArrayList<>();
		for(Complex point : points){
			transformedPoints.add(Mobius.onPoint(t, point));
		}
		return new PointSeries(transformedPoints);
	}

	public PointSeries scale(double scale){
		this.scale *= scale;
		ArrayList<Complex> newPoints = new ArrayList<>();
		for(Complex point : points){
			newPoints.add(point.mult(scale));
		}
		points = newPoints;
		return this;
	}
	
	public PointSeries translate(Complex translation){
		this.translation = this.translation.add(translation);
		ArrayList<Complex> newPoints = new ArrayList<>();
		for(Complex point : points){
			newPoints.add(point.add(translation));
		}
		points = newPoints;
		return this;
	}
	
	public static PointSeries readData(String fileName) throws IOException{
		ArrayList<Complex> points = new ArrayList<>();
		FileReader in = new FileReader(new File(fileName));
		BufferedReader reader = new BufferedReader(in);
		String line = null;
		while((line = reader.readLine()) != null){
			String[] elems = line.split(",");
			points.add(new Complex(Double.valueOf(elems[0]), Double.valueOf(elems[1])));
		}
		reader.close();
		in.close();
		return new PointSeries(points);
	}
}
