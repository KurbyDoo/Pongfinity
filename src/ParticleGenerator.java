import csta.ibm.pong.GameObject;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The ParticleGenerator class represents a particle generator within the game environment.
 * It extends the GameObject class and provides methods to control the generation,
 * appearance, and behavior of particles over time.
 */
public class ParticleGenerator extends GameObject {
    private int x, y, duration, frequency, velocity, lifespan;
    private Color colour;
    private Pong game;
    private Queue<Particle> allParticles;
    /**
     * Initializes a particle generator with the specified parameters.
     *
     * @param x          The x-coordinate of the particle generator.
     * @param y          The y-coordinate of the particle generator.
     * @param duration   The duration of the particle generation.
     * @param velocity   The velocity of the particles generated.
     * @param frequency  The frequency of particle generation.
     * @param color      The color of the particles.
     * @param mainGame   The main game instance.
     */
    public ParticleGenerator(int x, int y, int duration, int velocity, int frequency, int lifeSpan, Color color, Pong mainGame) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.frequency = frequency;
        this.velocity = velocity;
        this.lifespan = lifeSpan;
        allParticles = new LinkedList<>();
        colour = color;
        game = mainGame;
    }


    /**
     * Checks if the particle generator has finished generating particles.
     *
     * @return True if the duration is less than 0 and there are no particles left; false otherwise.
     */
    public boolean isFinished() {
        return duration < 0 && allParticles.size() == 0;
    }

    /**
     * Creates particles based on the generator's parameters and adds them to the game.
     */
    public void createParticles() {
        for (int i = 0; i < frequency; i++) {
            Particle newParticle = new Particle(x, y, (int) (Math.sqrt(velocity / 4.0)), (int) (Math.random() * 10 + 30), lifespan, colour);
            allParticles.add(newParticle);
            game.add(newParticle);
        }
    }


    /**
     * Updates the particles by removing those that have reached their minimum size.
     */
    public void updateParticles() {
        while(allParticles.size() > 0 && allParticles.peek().getWidth() <= 0) {
            game.remove(allParticles.poll());
        }
    }

    /**
     * Performs the actions of the particle generator for each game iteration.
     * If the duration is greater than 0, it creates particles, then updates and decrements the duration.
     */
    @Override
    public void act() {
        if (duration > 0) {
            createParticles();
        }
        updateParticles();
        duration -= 1;
    }

}
