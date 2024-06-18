import java.awt.*;

class Block {
    private Character shape;
    private int[] shapes;
    private Color color;
    private int currentShapeIndex;
    private int currentShape;
    private int nextShape;

    public Block(Character shape, int[] shapes, Color color) {
        this.shape = shape;
        this.shapes = shapes;
        this.color = color;
        currentShapeIndex = 0;
        currentShape = shapes[currentShapeIndex];
        nextShape = shapes[currentShapeIndex+1];
    }
    public int getCurrentShape() {
        return currentShape;
    }
    public int getNextShape() {
        return nextShape;
    }
    public Character getShape() {
    	return this.shape;
    }
    public int[] getShapes() {
    	return this.shapes;
    }
    public Color getColor() {
    	return color;
    }
    public void rotate() {
        if (currentShapeIndex == (shapes.length-1))
            currentShapeIndex = 0;
        else
            currentShapeIndex += 1;
        currentShape = shapes[currentShapeIndex];

        if (currentShapeIndex+1 == shapes.length)
            nextShape = shapes[0];
        else
            nextShape = shapes[currentShapeIndex+1];
    }
}
