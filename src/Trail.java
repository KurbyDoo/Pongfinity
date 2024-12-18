import csta.ibm.pong.GameObject;

import java.awt.*;

public class Trail extends GameObject {
    private final int initialSize;
    private int time;
    private final Color startingColor;

    /**
     * Constructor for Trail objects.
     *
     * @param x          The x-coordinate of the trail.
     * @param y          The y-coordinate of the trail.
     * @param size       The initial size of the trail.
     * @param ballColor  The color of the ball associated with the trail.
     */
    public Trail(int x, int y, int size, Color ballColor) {
        setX(x);
        setY(y);
        initialSize = size;
        time = 0;
        setSize(size, size);
        startingColor = ballColor;
    }


    /**
     * Updates the Trail's appearance and position.
     */
    @Override
    public void act() {
        time += 1;
        setSize(initialSize - 2 * time, initialSize - 2 * time);
        setX(getX() + 1);
        setY(getY() + 1);
        setColor(new Color(
                (int) (startingColor.getRed() * Math.max((initialSize - time * 2) / (double) initialSize, 0.0)),
                (int) (startingColor.getGreen() * Math.max((initialSize - time * 2) / (double) initialSize, 0.0)),
                (int) (startingColor.getBlue() * Math.max((initialSize - time * 2) / (double) initialSize, 0.0))
        ));
    }

    /**
     * Gets the current size of the trail.
     *
     * @return The width of the trail.
     */
    public int trailSize() {
        return getSize().width;
    }

}
