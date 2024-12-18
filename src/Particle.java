import csta.ibm.pong.GameObject;

import java.awt.*;

/**
 * The Particle class represents a particle within the game environment.
 * It extends the GameObject class and provides methods to control the particle's movement,
 * appearance, and behavior over time.
 */
public class Particle extends GameObject {
    private double vx, vy;
    private int totalLifespan, currentLifespan, initialSize;

    /**
     * Initializes a particle with the specified parameters.
     *
     * @param x         The x-coordinate of the particle's center.
     * @param y         The y-coordinate of the particle's center.
     * @param r         The radius of the particle's motion range.
     * @param size      The size of the particle.
     * @param lifespan  The lifespan of the particle.
     * @param color     The color of the particle.
     */
    public Particle(int x, int y, int r, int size, int lifespan, Color color) {
        setSize(size, size);
        setX(x - size / 2);
        setY(y - size / 2);
        this.vx = Math.random() * r * r * 2 - r * r;
        vy = (r * r - Math.abs(vx)) * Math.signum(Math.random() - 0.5);
        initialSize = size;
        totalLifespan = lifespan;
        currentLifespan = lifespan;
        setColor(color);
    }


    /**
     * Updates the particle's position and size over time.
     * Reduces the current lifespan of the particle.
     */
    @Override
    public void act() {
        currentLifespan--;
        setX(getX() + (int) (Math.signum(vx) * Math.sqrt(Math.abs(vx))));
        setY(getY() + (int) (Math.signum(vy) * Math.sqrt(Math.abs(vy))));
        setSize(initialSize * currentLifespan / totalLifespan, initialSize * currentLifespan / totalLifespan);
    }
}
