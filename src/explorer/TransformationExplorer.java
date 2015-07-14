package explorer;

import java.util.ArrayList;

import pointSeries.PointSeries;
import group.SL2C;

public class TransformationExplorer {
	private SL2C[] gens;
	
	public TransformationExplorer(SL2C[] gens){
		this.gens = gens;
	}
	
	public ArrayList<PointSeries> runBFS(int maxLevel, PointSeries root){
		ArrayList<PointSeries> transformedFigures = new ArrayList<>();
		ArrayList<ArrayList<TaggedPointSeries>> figures = new ArrayList<>();
		figures.add(new ArrayList<TaggedPointSeries>());
		figures.get(0).add(new TaggedPointSeries(-1, root));
		for(int level = 0 ; level < maxLevel ; level++){
			figures.add(new ArrayList<TaggedPointSeries>());
			for(TaggedPointSeries figure : figures.get(level)){
				for(int tag = 0 ; tag < gens.length ; tag++){
					if((tag + 2) % 2 == figure.tag) continue;
					figures.get(level + 1).add(new TaggedPointSeries(tag, figure.pointSeries.transform(gens[tag])));
				}
			}
		}
		
		for(ArrayList<TaggedPointSeries> list : figures){
			for(TaggedPointSeries figure : list){
				transformedFigures.add(figure.pointSeries);
			}
		}
		
		return transformedFigures;
	}
	
	private class TaggedPointSeries{
		protected int tag = -1;
		protected PointSeries pointSeries;

		public TaggedPointSeries(int tag, PointSeries pointSeries) {
			this.tag = tag;
			this.pointSeries = pointSeries;
		}
	}
}
