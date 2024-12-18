/*
 * This code is protected under the Gnu General Public License (Copyleft), 2005 by
 * IBM and the Computer Science Teachers of America organization. It may be freely
 * modified and redistributed under educational fair use.
 */

import csta.ibm.pong.GameObject;

import java.util.ArrayList;

/**
 * The Paddle class represents a paddle within the Pong game environment.
 * It extends the GameObject class and provides methods to control the paddle's movement,
 * behavior, and interaction with other game components such as balls.
 */
public class Paddle extends GameObject {
	private double velocity;
	private final int INITIAL_HEIGHT, INITIAL_WIDTH;
	private int screenHeight, screenWidth, lastX, lastY;
	private double bounceAmount = 0.5, acceleration = 2, friction = 0.95, paddleHeight, paddleWidth;
	private final boolean IS_LEFT;

	/**
	 * Constructs a paddle with the specified dimensions and position.
	 *
	 * @param width   The width of the paddle.
	 * @param height  The height of the paddle.
	 * @param isLeft  Indicates whether the paddle is on the left side of the screen.
	 */
	public Paddle(int width, int height, boolean isLeft) {
		INITIAL_HEIGHT = height;
		INITIAL_WIDTH = width;
		this.IS_LEFT = isLeft;
		reset();
	}


	/**
	 * Moves the paddle based on its velocity and handles boundary collisions.
	 */
	public void act() {
		setSize((int) paddleWidth, (int) paddleHeight);
		lastX = getX();
		lastY = getY();

		// Set the x-coordinate of the paddle
		if (IS_LEFT) {
			setX(screenWidth / 20);
		} else {
			setX(screenWidth - getWidth() - screenWidth / 20);
		}

		// Update the y-coordinate of the paddle based on its velocity
		if (velocity > 0 && getY() + getHeight() < screenHeight) {
			setY(Math.min(getY() + (int) velocity, screenHeight - getHeight()));
		} else if (velocity < 0 && getY() > 0) {
			setY(Math.max(getY() + (int) velocity, 0));
		}

		// Apply friction to velocity
		velocity *= friction;
	}


	/**
	 * Makes the paddle automatically adjust its position based on the position of the closest ball.
	 *
	 * @param allBalls The list of all balls in the game.
	 */
	public void idle(ArrayList<Ball> allBalls) {
		Ball closestBall = null;
		int closestDistance = Integer.MAX_VALUE;
		int dx, dy;

		// Find the closest ball to the paddle
		for (Ball b : allBalls) {
			dx = Math.abs((getX() + getWidth() / 2) - (b.getX() + b.getWidth() / 2));
			dy = Math.abs((getY() + getHeight() / 2) - (b.getY() + b.getHeight() / 2));
			if (((IS_LEFT && b.getVX() < 0) || (!IS_LEFT && b.getVX() > 0)) && (dx * dx + dy * dy < closestDistance)) {
				closestBall = b;
				closestDistance = dx * dx + dy * dy;
			}
		}

		// If no ball is found, return
		if (closestBall == null) return;

		// Move the paddle up or down depending on the position of the closest ball
		if (closestBall.getY() + closestBall.getHeight() / 2 > getY() + getHeight() / 2) {
			moveDown();
		} else if (closestBall.getY() + closestBall.getHeight() / 2 < getY() + getHeight() / 2) {
			moveUp();
		}
	}


	/**
	 * Resets the paddle to its initial state.
	 */
	public void reset() {
		paddleHeight = INITIAL_HEIGHT;
		paddleWidth = INITIAL_WIDTH;
		setSize((int) paddleWidth, (int) paddleHeight);
		bounceAmount = 0.5;
		acceleration = 2;
		friction = 0.95;
		velocity = 0;
	}

	/**
	 * Sets the screen height for the paddle.
	 *
	 * @param screenHeight The height of the screen.
	 */
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	/**
	 * Sets the screen width for the paddle.
	 *
	 * @param screenWidth The width of the screen.
	 */
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	/**
	 * Moves the paddle upward.
	 * Decreases the velocity of the paddle.
	 */
	public void moveUp() {
		velocity -= acceleration;
		if (getY() + velocity <= 0) {
			velocity = 0;
		}
	}

	/**
	 * Moves the paddle downward.
	 * Increases the velocity of the paddle.
	 */
	public void moveDown() {
		velocity += acceleration;
		if (getY() + getHeight() + velocity >= screenHeight) {
			velocity = 0;
		}
	}

	/**
	 * Gets the x-coordinate of the paddle in the previous frame.
	 *
	 * @return The x-coordinate of the paddle in the previous frame.
	 */
	public int getLastX() {
		return lastX;
	}

	/**
	 * Gets the y-coordinate of the paddle in the previous frame.
	 *
	 * @return The y-coordinate of the paddle in the previous frame.
	 */
	public int getLastY() {
		return lastY;
	}

	/**
	 * Gets the velocity of the paddle.
	 *
	 * @return The velocity of the paddle.
	 */
	public int getVelocity() {
		return (int) velocity;
	}


	/**
	 * Changes the height of the paddle by a given factor.
	 *
	 * @param factorChange The factor by which to change the paddle height.
	 */
	public void changePaddleHeight(double factorChange) {
		double newPaddleHeight = Math.min(screenHeight, Math.max(0, paddleHeight * factorChange));
		if (factorChange > 1) {
			newPaddleHeight += 5;
		}
		setY(getY() - (int) (newPaddleHeight - paddleHeight) / 2);
		paddleHeight = newPaddleHeight;
	}

	/**
	 * Changes the paddle's acceleration, affecting its speed.
	 *
	 * @param magnitudeChange The change in acceleration magnitude.
	 */
	public void changePaddleSpeed(double magnitudeChange) {
		acceleration = Math.min(5, Math.max(0.3, acceleration + magnitudeChange));
	}
}
