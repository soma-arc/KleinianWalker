package transformer;

import group.SL2C;

import java.util.ArrayList;

import pointSeries.PointSeries;
import mobius.Mobius;
import number.Complex;

public class PointSeriesTransformer {
	public static PointSeries transform(SL2C t, PointSeries pointSeries){
		ArrayList<Complex> transformedPoints = new ArrayList<>();
		for(Complex point : pointSeries.getPointsList()){
			transformedPoints.add(Mobius.onPoint(t, point));
		}
		return new PointSeries(transformedPoints);
	}
}
