package so.gui.grid;

import java.awt.*;

/**
 * Created by diegomartin on 12/2/15.
 */
public class ColorPoint extends Point {
    private Color color = Color.blue;

    public ColorPoint (int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    public ColorPoint (int x, int y) {
        super(x, y);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
