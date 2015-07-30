package launcher;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import ui.ControlPanel;
import ui.Display;

public class Launcher {

	private static final int FRAME_WIDTH = 1500;
	private static final int FRAME_HEIGHT = 1500;

	public static void main(String[] args) {
		new Launcher().start();
	}

	public void start(){
		JFrame frame = new JFrame();
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setTitle("KleinianWalker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.getContentPane().add(Display.getInstance());
		frame.getContentPane().add(ControlPanel.getInstance());
		frame.setVisible(true);
		Display.getInstance().requestFocus();
	}
}
