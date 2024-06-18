import java.awt.Color;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class CustomButton extends JButton {
	private Color color;
	public CustomButton() {
		this("");
	}
	public CustomButton(String text) {
		this(text, null);
	}
	public CustomButton(Color color) {
		this(null, color);
	}
	public CustomButton(String text, Color color) {
		super(text);
		this.color = color;
		super.setSize(20, 20);
		super.setFocusPainted(false);
		super.setFocusable(false);
        super.setEnabled(false);
		super.setBackground(color);
		super.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(204, 204, 204)));
		// 그림자 값 = 3
	}
	public void setLocation(Point point) {
		this.setLocation(point.x, point.y);
	}
	public void setLocation(int x, int y) {
		super.setLocation(x + 3, y + 3);
	}
}
