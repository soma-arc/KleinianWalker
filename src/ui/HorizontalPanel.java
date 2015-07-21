package ui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class HorizontalPanel extends JPanel{
	public HorizontalPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setOpaque(false);
		this.setAlignmentX(LEFT_ALIGNMENT);
	}
}
