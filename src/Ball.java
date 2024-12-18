/*
 * This code is protected under the Gnu General Public License (Copyleft), 2005 by
 * IBM and the Computer Science Teachers of America organization. It may be freely
 * modified and redistributed under educational fair use.
 */

import csta.ibm.pong.GameObject;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The main pong ball for pong
 * Moves in the vertical and horizontal direction and
 * can reflect its velocity upon collision
 */
public class Ball extends GameObject {
	public enum Owner {LEFT, RIGHT, NONE}
	private final double MAX_VELOCITY = 30;
	private final Queue<Trail> trailParticles = new LinkedList<>();
	private double vx = 6, vy = 6;
	private boolean alive = true;
	private int lastX, lastY;
	private Owner currentOwner = Owner.NONE;


	/**
	 * Constructs a new Ball object with random size, position, and velocity.
	 *
	 * @param screenWidth The width of the game screen.
	 * @param screenHeight The height of the game screen.
	 */
	public Ball(int screenWidth, int screenHeight) {
		// Generate a random size for the ball
		int randomSize = (int) (10 + 10 * Math.random());
		// Set the size of the ball
		setSize(randomSize, randomSize);

		// Reset the position of the ball
		resetPosition(screenWidth, screenHeight);

		// Set the starting velocity of the ball to a random direction and magnitude between 6 - 11
		setVX(Math.signum(Math.random() - 0.5) * (Math.random() * 5 +  6));
		setVY(Math.signum(Math.random() - 0.5) * (Math.random() * 5));

		// Center the ball horizontally and vertically within the screen
		setX((screenWidth - getWidth()) / 2);
		setY((screenHeight - getHeight()) / 2);
	}


	/**
	 * Updates the ball's x and y location based on its velocity.
	 */
	public void act() {
		// Store the current x and y coordinates as the last coordinates
		lastX = getX();
		lastY = getY();

		// Update the x and y coordinates based on the velocity
		setX((int)(getX() + vx));
		setY((int)(getY() + vy));

		// Set the color of the ball based on its current owner
//		setColor(switch (currentOwner) { Commented out for the 1.8 jar release
//			case LEFT -> new Color(255, 0, 0);
//			case RIGHT -> new Color(0, 0, 255);
//			case NONE -> new Color(255, 255, 255);
//		});
		switch (currentOwner) {
			case LEFT: setColor(new Color(255, 0, 0)); break;
			case RIGHT: setColor(new Color(0, 0, 255)); break;
			case NONE: setColor(new Color(255, 255, 255)); break;
		};
	}


	/**
	 * Updates the trail of the ball.
	 *
	 * @param mainPong The main Pong game instance.
	 */
	public void updateTrail(Pong mainPong) {
		if (alive) {
			// Create a new trail particle based on the current owner of the ball
//			Trail newTrail = switch (currentOwner) { Commented out for the 1.8 jar release
//				case LEFT -> new Trail(getX(), getY(), getSize().width, new Color(200, 0, 0));
//				case RIGHT -> new Trail(getX(), getY(), getSize().width, new Color(0, 0, 200));
//				case NONE -> new Trail(getX(), getY(), getSize().width, new Color(150, 150, 150));
//			};
			Trail newTrail;
			switch (currentOwner) {
				case LEFT:  newTrail = new Trail(getX(), getY(), getSize().width, new Color(200, 0, 0)); break;
				case RIGHT: newTrail = new Trail(getX(), getY(), getSize().width, new Color(0, 0, 200)); break;
				default: newTrail = new Trail(getX(), getY(), getSize().width, new Color(150, 150, 150)); break;
			};
			// Add the new trail particle to the list of trail particles
			trailParticles.add(newTrail);
			// Add the new trail particle to the main Pong game instance
			mainPong.add(newTrail);
		}
		// Remove the oldest trail particle if the trail size becomes zero
		if (trailParticles.size() > 0 && trailParticles.peek().trailSize() <= 0) {
			mainPong.remove(trailParticles.poll());
		}
	}


	/**
	 * Checks for vertical collision of the ball with the top and bottom boundaries of the screen.
	 * If a collision is detected, the ball is bounced vertically.
	 *
	 * @param screenHeight The height of the screen.
	 */
	public void checkVerticalCollision(int screenHeight) {
		// Check if the ball's bottom edge exceeds the screen height
		if (getY() + getHeight() > screenHeight) {
			// If so, bounce the ball vertically from the bottom boundary
			verticalBounce(screenHeight - getHeight());
		}
		// Check if the ball's top edge goes beyond the top boundary of the screen
		else if (getY() < 0) {
			// If so, bounce the ball vertically from the top boundary
			verticalBounce(0);
		}
	}


	/**
	 * Checks for collision with paddles and updates ball movement accordingly.
	 *
	 * @param leftPaddle The left paddle.
	 * @param rightPaddle The right paddle.
	 */
	public void checkPaddleCollision(Paddle leftPaddle, Paddle rightPaddle) {
		// Check for collision with the left paddle
		if (getVX() < 0 && getX() <= leftPaddle.getX() + leftPaddle.getWidth() && collides(leftPaddle)) {
			if (getLastX() >= leftPaddle.getLastX() + leftPaddle.getWidth()) {
				// Bounce horizontally from the right edge of the left paddle
				horizontalBounce(leftPaddle.getX() + leftPaddle.getWidth());
				// Bounce vertically with adjusted velocity based on paddle velocity
				verticalBounce(getY(), (int)(getVY() + leftPaddle.getVelocity() * 1.1));
			} else if (getLastY() >= leftPaddle.getLastY() + leftPaddle.getHeight()) {
				// Bounce vertically from the bottom edge of the left paddle
				verticalBounce(leftPaddle.getY() + leftPaddle.getHeight(), (int)(leftPaddle.getVelocity() * 1.1));
			} else if (getLastY() + getHeight() <= leftPaddle.getLastY()) {
				// Bounce vertically from the top edge of the left paddle
				verticalBounce(leftPaddle.getY() - getHeight(), (int)(leftPaddle.getVelocity() * 1.1));
			}
			// Set the current owner of the ball to the left paddle
			currentOwner = Owner.LEFT;
		}
		// Check for collision with the right paddle
		else if (getX() + getWidth() > rightPaddle.getX() && collides(rightPaddle)) {
			if (getLastX() + getWidth() <= rightPaddle.getLastX()) {
				// Bounce horizontally from the left edge of the right paddle
				horizontalBounce(rightPaddle.getX() - getWidth());
				// Bounce vertically with adjusted velocity based on paddle velocity
				verticalBounce(getY(), (int)(getVY() + rightPaddle.getVelocity() * 1.1));
			} else if (getLastY() >= rightPaddle.getLastY() + rightPaddle.getHeight()) {
				// Bounce vertically from the bottom edge of the right paddle
				verticalBounce(rightPaddle.getY() + rightPaddle.getHeight(), (int)(rightPaddle.getVelocity() * 1.1));
			} else if (getLastY() + getHeight() <= rightPaddle.getLastY()) {
				// Bounce vertically from the top edge of the right paddle
				verticalBounce(rightPaddle.getY() - getHeight(), (int)(rightPaddle.getVelocity() * 1.1));
			}
			// Set the current owner of the ball to the right paddle
			currentOwner = Owner.RIGHT;
		}
	}


	/**
	 * Resets the position of the ball to the center of the screen.
	 *
	 * @param screenWidth The width of the screen.
	 * @param screenHeight The height of the screen.
	 */
	public void resetPosition(int screenWidth, int screenHeight) {
		// Set the starting velocity of the ball to a random direction and magnitude between 6 - 11
		setVX(Math.signum(Math.random() - 0.5) * (Math.random() * 5 +  6));
		setVY(Math.signum(Math.random() - 0.5) * (Math.random() * 5));

		// Center the ball horizontally and vertically within the screen
		setX((screenWidth - getWidth()) / 2);
		setY((screenHeight - getHeight()) / 2);

		// Set the current owner of the ball to NONE
		currentOwner = Owner.NONE;
	}

	/**
	 * Destroys the particle by setting its velocity to zero, size to zero, and marking it as not alive.
	 */
	public void selfDestruct() {
		setVX(0);
		setVY(0);
		setSize(0, 0);
		alive = false;
	}



	/**
	 * Inverts the ball's x-velocity
	 * @param newX The new position of the ball upon bouncing
	 */
	public void horizontalBounce(int newX) {
		lastX = getX();
		setX(newX);
		vx *= -1;
		setVX(vx * (1 + Math.random() * 0.5));
		setVY(vy * (Math.random() + 0.5));
	}

	/**
	 * Inverts the ball's y-velocity
	 * @param newY The new position of the ball upon bouncing
	 */
	public void verticalBounce(int newY) {
		lastY = getY();
		setY(newY);
		vy *= -1;
	}

	/**
	 * Inverts and sets the ball's y-velocity to a new value
	 * @param newY The new position of the ball upon bouncing
	 * @param newVY The new y-velocity of the ball upon bouncing
	 */
	public void verticalBounce(int newY, int newVY) {
		verticalBounce(newY);
		setVY(Math.signum(newVY) * Math.max(Math.abs(newVY), Math.abs(vy)));
	}

	/**
	 * Checks if the ball was last touched by the left paddle.
	 *
	 * @return True if the ball was last touched by the left paddle, otherwise false.
	 */
	public boolean lastTouchedLeft() {
		return currentOwner == Owner.LEFT;
	}

	/**
	 * Checks if the ball was last touched by the right paddle.
	 *
	 * @return True if the ball was last touched by the right paddle, otherwise false.
	 */
	public boolean lastTouchedRight() {
		return currentOwner == Owner.RIGHT;
	}

	/**
	 * Gets the x-coordinate of the ball's last position.
	 *
	 * @return The x-coordinate of the ball's last position.
	 */
	public int getLastX() {
		return lastX;
	}

	/**
	 * Gets the y-coordinate of the ball's last position.
	 *
	 * @return The y-coordinate of the ball's last position.
	 */
	public int getLastY() {
		return lastY;
	}

	/**
	 * Sets the horizontal velocity of the ball, limiting it to the maximum velocity.
	 *
	 * @param newVX The new horizontal velocity of the ball.
	 */
	public void setVX(double newVX) {
		vx = Math.signum(newVX) * Math.min(Math.abs(newVX), MAX_VELOCITY);
	}

	/**
	 * Sets the vertical velocity of the ball, limiting it to the maximum velocity.
	 *
	 * @param newVY The new vertical velocity of the ball.
	 */
	public void setVY(double newVY) {
		vy =  Math.signum(newVY) * Math.min(Math.abs(newVY), MAX_VELOCITY);
	}

	/**
	 * Gets the integer value of the vertical velocity of the ball.
	 *
	 * @return The integer value of the vertical velocity of the ball.
	 */
	public int getVY() {
		return (int) vy;
	}

	/**
	 * Gets the integer value of the horizontal velocity of the ball.
	 *
	 * @return The integer value of the horizontal velocity of the ball.
	 */
	public int getVX() {
		return (int) vx;
	}

	/**
	 * Gets the color of the particle based on its current owner.
	 *
	 * @return The color of the particle.
	 */
	public Color getColor() {
//		return 	switch (currentOwner) { // Commented out for 1.8 jar release
//			case LEFT -> new Color(255, 0, 0);
//			case RIGHT -> new Color(0, 0, 255);
//			case NONE -> new Color(255, 255, 255);
//		};
		switch (currentOwner) {
			case LEFT: return new Color(255, 0, 0);
			case RIGHT: return new Color(0, 0, 255);
			default: return new Color(255, 255, 255);
		}
	}

}
