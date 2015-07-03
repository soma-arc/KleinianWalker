package transformer;

import group.SL2C;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import mobius.Mobius;
import number.Complex;

public class ImageTransformer {
	private Complex origin;
	private double magnification;
	private BufferedImage image;
	private double shrinkRatio = 0.5;

	public ImageTransformer(Complex origin, double magnification, BufferedImage image){
		this.origin = origin;
		this.magnification = magnification;
		this.image = image;
	}

	public void draw(Graphics g, ImageObserver observer){
		g.drawImage(image, 
				(int) (origin.re() * magnification) ,
				(int) (origin.im() * magnification) ,
				(int) (image.getWidth()),
				(int) (image.getHeight()),
				observer);
	}

	public void drawTransformedImage(Graphics g, SL2C t){
		for(int x = 0 ; x < image.getWidth() ; x++){
			for(int y = 0 ; y < image.getHeight() ; y++){
				int c = image.getRGB(x, y);
				if(c == 0) continue;
				Complex pos = origin.add(new Complex(x/magnification, y/magnification));
				pos = Mobius.onPoint(t, pos);
				g.setColor(new Color(c));
				g.drawRect((int) (pos.re() * magnification), 
						   (int) (pos.im() * magnification), 1, 1);
			}
		}
	}
}
