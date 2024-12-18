/*
 * This code is protected under the Gnu General Public License (Copyleft), 2005 by
 * IBM and the Computer Science Teachers of America organization. It may be freely
 * modified and redistributed under educational fair use.
 */

import csta.ibm.pong.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The main class for the pong mini-game
 * Contains a single ball and two paddles
 * controlled by the 'z', 'x', 'n', and 'm' keys
 */
public class Pong extends Game {

	private enum GameState {MENU, PLAYING, GAME_OVER};
	private GameState currentState;
	private final int WIN_SCORE = 50, PADDLE_HEIGHT = 200, BALL_RESPAWN_TIME = 200, POWERUP_RESPAWN_TIME = 50;
	private int leftScore, rightScore, nextBallSpawn, nextPowerupSpawn;
	private boolean spacePressed;
	private Paddle leftPaddle, rightPaddle;
	private JLabel scoreCounter;
	private ArrayList<Ball> allBalls = new ArrayList<>();
	private ArrayList<PowerUp> allPowerups = new ArrayList<>();
	private ArrayList<ParticleGenerator> allParticleGenerators = new ArrayList<>();
	private ArrayList<Integer> toExplode;
	private static int globalTime = 0;
	private int mouseX = 0, mouseY = 0;
	private Menu mainMenu;
	private Font pixelFont;


	/**
	 * Constructs a new instance of the Pong game.
	 */
	public Pong() {
		// Call the constructor of the superclass (JFrame)
		super();
		// Set the size of the game window
		this.setSize(1280, 720);
		this.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				char pressed = Character.toUpperCase(e.getKeyChar());
				switch(pressed) {
					case ' ':
						spacePressed = true;
				}
			}

			public void keyReleased(KeyEvent e) {
				char released = Character.toUpperCase(e.getKeyChar());
				switch(released) {
					case ' ':
						spacePressed = false;
				}
			}
		});
	}


	/**
	 * Sets up the paddles, ball, and score label
	 */
	public void setup() {
		initializeMenu();
		initializeGame();

	}

	/**
	 * Initializes the menu for the Pong game.
	 */
	private void initializeMenu() {
		// Create a new Menu object with the dimensions of the game field and a reference to this Pong object.
		mainMenu = new Menu(getFieldWidth(), getFieldHeight(), this);
		// Add the main menu to the game.
		add(mainMenu);
	}

	/**
	 * Initializes the game components and sets up the initial game state.
	 */
	private void initializeGame() {
		// Set the delay for the game loop.
		setDelay(20);

		// Create left and right paddles.
		leftPaddle = new Paddle(40, PADDLE_HEIGHT, true);
		rightPaddle = new Paddle(40, PADDLE_HEIGHT, false);

		// Add left and right paddles to the game.
		add(leftPaddle);
		add(rightPaddle);

		try {
			// Load the custom font for the game.
			pixelFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/ARCADECLASSIC.TTF"));
		} catch (IOException | FontFormatException e) {
			// Handle font loading exceptions.
			System.out.println(e);
		}

		// Create and configure the score counter label.
		scoreCounter = new JLabel("0     0");
		scoreCounter.setFont(pixelFont.deriveFont(Font.BOLD, 48f));
		scoreCounter.setForeground(Color.WHITE);
		scoreCounter.setBounds(0, 20, getFieldWidth(), getFieldHeight() / 10);
		scoreCounter.setHorizontalAlignment(SwingConstants.CENTER);
		add(scoreCounter);
		repaint();

		// Reset the game state.
		resetGame();

		// Set the initial game state to MENU.
		setStateMenu();
		// Uncomment the line below to set the initial game state to PLAYING.
		// currentState = GameState.PLAYING;
	}


	/**
	 * Resets the game to its initial state.
	 */
	public void resetGame() {
		// Remove all balls from the game and clear the list of balls.
		for (Ball b : allBalls) {
			remove(b);
		}
		allBalls.clear();

		// Remove all power-ups from the game and clear the list of power-ups.
		for (PowerUp p : allPowerups) {
			remove(p);
		}
		allPowerups.clear();

		// Reset scores, next power-up spawn time, next ball spawn time, and global time.
		leftScore = 0;
		rightScore = 0;
		nextPowerupSpawn = 0;
		nextBallSpawn = 0;
		globalTime = 0;

		// Reset left and right paddles.
		leftPaddle.reset();
		rightPaddle.reset();

		// Reset the y-coordinate of left and right paddles to the center of the game field.
		leftPaddle.setY((getFieldHeight() - leftPaddle.getHeight()) / 2);
		rightPaddle.setY((getFieldHeight() - rightPaddle.getHeight()) / 2);

		// Update the score counter text to display the reset scores.
		scoreCounter.setText("0     0");
	}


	/**
	 * Checks if either player has won the game.
	 *
	 * @param isIdle a boolean indicating if the game is currently idle
	 */
	public void checkForWin(boolean isIdle) {
		// Check if the left player has won.
		if (leftScore >= rightScore + 2 && leftScore >= WIN_SCORE) {
			// Uncomment the line below to display the winning message.
			// Reset the game if it's idle, otherwise stop the game.
			if (isIdle) resetGame();
			else endGame(true);

			// Check if the right player has won.
		} else if (rightScore >= leftScore + 2 && rightScore >= WIN_SCORE) {
			// Uncomment the line below to display the winning message.
			// Reset the game if it's idle, otherwise stop the game.
			if (isIdle) resetGame();
			else endGame(false);
		}
	}

	/**
	 * Ends the game and prepares for the end game explosion animation.
	 *
	 * @param leftWin True if the left player wins, false otherwise.
	 */
	private void endGame(boolean leftWin) {
		// Initialize the list to hold indices of elements to explode
		toExplode = new ArrayList<>();

		// Add indices of balls and powerups to the list
		for (int i = 0; i < allBalls.size() + allPowerups.size(); i++) {
			toExplode.add(i);
			if (i < allBalls.size()) {
				// Stop all balls by setting their velocity to 0
				allBalls.get(i).setVX(0);
				allBalls.get(i).setVY(0);
			}
		}

		// Shuffle the list to randomize the explosion effect
		Collections.shuffle(toExplode);

		// Set the winner in the main menu
		mainMenu.setWinner(leftWin);

		// Set the game state to "Game Over"
		setStateOver();
	}



	/**
	 * Updates the scoring in the game.
	 */
	public void updateScoring() {
		// Iterate through all balls in the game.
		for (Ball currentBall : allBalls) {
			// Check if the ball is out of bounds on the right side.
			if (currentBall.getX() > getFieldWidth()) {
				// Increment left player's score and reset the ball's position.
				leftScore++;
				currentBall.resetPosition(getFieldWidth(), getFieldHeight());
			}
			// Check if the ball is out of bounds on the left side.
			else if (currentBall.getX() + currentBall.getWidth() < 0) {
				// Increment right player's score and reset the ball's position.
				rightScore++;
				currentBall.resetPosition(getFieldWidth(), getFieldHeight());
			}
		}

		// Update the score counter text to display the updated scores.
		scoreCounter.setText(leftScore + "     " + rightScore);
		// Adjust the position of the score counter.
		scoreCounter.setBounds(0, 20, getFieldWidth(), getFieldHeight() / 10);
	}


	/**
	 * Checks for collisions between power-ups and balls, generates particles, and manages power-up lifespan.
	 *
	 * This method iterates through all active power-ups in the game. For each power-up, it checks for collisions
	 * with each ball in play. If a collision is detected, particles are generated at the collision point, the power-up
	 * is removed from the game, and the loop moves to the next power-up. If no collision is detected, the power-up is
	 * retained for further use. After iterating through all power-ups, the list of active power-ups is updated to contain
	 * only those that are still in play. Additionally, it invokes the checkGeneratorLifespan method to manage the lifespan
	 * of particle generators.
	 *
	 * @see PowerUp
	 * @see Ball
	 * @see #checkGeneratorLifespan()
	 */
	public void checkPowerupCollision() {
		// List to store power-ups that are not collided with any ball.
		ArrayList<PowerUp> carriedOver = new ArrayList<>();
		// Iterate through all active power-ups
		for (PowerUp currentPowerUp: allPowerups) {
			boolean collided = false;
			// Check for collision with each ball
			for (Ball currentBall: allBalls) {
				// If collision detected, generate particles, remove power-up, and break the loop
				if (currentPowerUp.checkCollides(currentBall, leftPaddle, rightPaddle, this)) {
					collided = true;
					addNewParticleGenerator(
							currentPowerUp.getX() + currentPowerUp.getWidth() / 2,
							currentPowerUp.getY() + currentPowerUp.getWidth() / 2,
							1,
							currentBall.getVX() * currentBall.getVX() + currentBall.getVY() * currentBall.getVY(),
							7,
							currentPowerUp.getColor()
					);
					remove(currentPowerUp);
					break;
				}
			}
			// If no collision detected, retain power-up
			if (!collided) {
				carriedOver.add(currentPowerUp);
			}
		}
		// Update the list of all power-ups to contain only those that are still in play
		allPowerups = carriedOver;
		// Manage the lifespan of particle generators
		checkGeneratorLifespan();
	}


	/**
	 * Checks the lifespan of particle generators and removes those that have finished.
	 *
	 * This method iterates through all particle generators currently active in the game. If a generator
	 * has finished its lifespan, it is removed from the list of active generators. Otherwise, it is
	 * retained for further use in the game. After the iteration, the list of active generators is updated
	 * to contain only the ones that are still active.
	 *
	 * @see ParticleGenerator
	 */
	public void checkGeneratorLifespan() {
		ArrayList<ParticleGenerator> carriedOver = new ArrayList<>();
		// Iterate through all particle generators
		for (ParticleGenerator currentGenerator: allParticleGenerators) {
			// Check if the current generator has finished its lifespan
			if (currentGenerator.isFinished()) {
				// If generator has finished, remove it from the list of active generators
				remove(currentGenerator);
			} else {
				// If generator is still active, add it to the list of carried over generators
				carriedOver.add(currentGenerator);
			}
		}
		// Update the list of all particle generators to contain only the ones that are still active
		allParticleGenerators = carriedOver;
	}


	/**
	 * Performs actions based on the current state of the game.
	 *
	 * This method acts as the main controller for game logic. It first executes general game logic through the
	 * actGameLogic method. Then, it updates the visibility of the main menu based on the current game state.
	 * Finally, it switches between different game states and executes specific logic accordingly.
	 *
	 * @see #actGameLogic()
	 * @see Menu#updateVisibility(boolean, boolean)
	 * @see #actMenuLogic()
	 * @see #actPlayerLogic()
	 */
	public void act() {
		// Perform general game logic
		actGameLogic();
		// Switch between game states and execute specific logic
		switch (currentState) {
			case MENU: actMenuLogic(); break;
			case PLAYING: actPlayerLogic(); break;
			case GAME_OVER: actGameOverLogic(); break;
		}
	}


	/**
	 * Executes player-specific logic for controlling paddles.
	 *
	 * This method handles player input for controlling the paddles. If certain keys are pressed, it triggers
	 * the corresponding paddle movement (up or down). Additionally, it checks for win conditions in the game.
	 *
	 * @see Paddle#moveUp()
	 * @see Paddle#moveDown()
	 * @see #ZKeyPressed()
	 * @see #XKeyPressed()
	 * @see #NKeyPressed()
	 * @see #MKeyPressed()
	 * @see #checkForWin(boolean)
	 */
	private void actPlayerLogic() {
		// Update main menu visibility based on current game state
		mainMenu.updateVisibility(false, false);
		// Move left paddle up if Z key is pressed
		if (ZKeyPressed()) {
			leftPaddle.moveUp();
		}
		// Move left paddle down if X key is pressed
		if (XKeyPressed()) {
			leftPaddle.moveDown();
		}
		// Move right paddle up if N key is pressed
		if (NKeyPressed()) {
			rightPaddle.moveUp();
		}
		// Move right paddle down if M key is pressed
		if (MKeyPressed()) {
			rightPaddle.moveDown();
		}

		// Check for win conditions
		checkForWin(false);
	}


	/**
	 * Executes logic specific to the main menu.
	 *
	 * This method handles logic related to the main menu. It increments the global time counter, processes
	 * user input from key presses to navigate the menu options, and ensures that the paddles remain idle.
	 * Additionally, it checks for win conditions in the game if applicable.
	 *
	 * @see Menu#processPressed(boolean, boolean, boolean, boolean, Pong)
	 * @see Paddle#idle(ArrayList)
	 * @see #ZKeyPressed()
	 * @see #XKeyPressed()
	 * @see #NKeyPressed()
	 * @see #MKeyPressed()
	 * @see #checkForWin(boolean)
	 */
	private void actMenuLogic() {
		// Update main menu visibility based on current game state
		mainMenu.updateVisibility(true, false);
		// Increment global time counter
		globalTime += 1;
		// Process user input to navigate menu options
		mainMenu.processPressed(ZKeyPressed(), XKeyPressed(), NKeyPressed(), MKeyPressed(), this);
		// Ensure left paddle remains idle
		leftPaddle.idle(allBalls);
		// Ensure right paddle remains idle
		rightPaddle.idle(allBalls);
		// Check for win conditions if applicable
		checkForWin(true);
	}


	/**
	 * Executes general game logic.
	 *
	 * This method handles various aspects of the game logic. It increments the global time counter, checks for
	 * collisions between balls and paddles, updates ball positions after collisions, updates scoring, checks for
	 * collisions between power-ups and balls, and manages the spawning of new balls and power-ups. Additionally,
	 * it updates the dimensions of the paddles based on the screen size.
	 *
	 * @see Ball#checkPaddleCollision(Paddle, Paddle)
	 * @see Ball#checkVerticalCollision(int)
	 * @see Ball#updateTrail(Pong)
	 * @see #updateScoring()
	 * @see #checkPowerupCollision()
	 * @see Paddle#setScreenHeight(int)
	 * @see Paddle#setScreenWidth(int)
	 * @see #getFieldHeight()
	 * @see #getFieldWidth()
	 * @see #addNewBall()
	 * @see #addNewPowerup()
	 */
	private void actGameLogic() {
		// Increment global time counter
		globalTime += 1;

		// Iterate through all balls and handle collisions, trail update
		for (Ball currentBall: allBalls) {
			currentBall.checkPaddleCollision(leftPaddle, rightPaddle);
			currentBall.checkVerticalCollision(getFieldHeight());
			currentBall.updateTrail(this);
		}

		// Update scoring
		updateScoring();

		// Check for collisions between power-ups and balls
		checkPowerupCollision();

		// Update paddles with the screen dimensions
		leftPaddle.setScreenHeight(getFieldHeight());
		rightPaddle.setScreenHeight(getFieldHeight());
		leftPaddle.setScreenWidth(getFieldWidth());
		rightPaddle.setScreenWidth(getFieldWidth());

		if (currentState != GameState.GAME_OVER) {
			// Spawn new ball if necessary
			if (globalTime > nextBallSpawn) {
				nextBallSpawn += BALL_RESPAWN_TIME;
				addNewBall();
			}

			// Spawn new power-up if necessary
			if (globalTime > nextPowerupSpawn) {
				nextPowerupSpawn += POWERUP_RESPAWN_TIME;
				addNewPowerup();
				repaint();
			}
		}
	}

	/**
	 * Handles the game logic when the game is over.
	 */
	private void actGameOverLogic() {
		// Update main menu visibility based on current game state
		mainMenu.updateVisibility(true, true);

		// Check if space bar is pressed to return to the main menu
		if (spacePressed) {
			setStateMenu();
		}

		// Check if there are elements to explode
		if (toExplode.size() > 0) {
			// Get the index of the next element to explode
			int toRemove = toExplode.get(0);

			// If the index is for a powerup
			if (toRemove >= allBalls.size()) {
				// Get the powerup at the specified index
				PowerUp p = allPowerups.get(toRemove - allBalls.size());

				// Create particle generator for the powerup explosion
				addNewParticleGenerator(p.getX(), p.getY(), 1, (int) (Math.random() * 50 + 200), 7, p.getColor());

				// Remove the powerup from the game
				remove(p);
			} else {
				// If the index is for a ball
				Ball b = allBalls.get(toRemove);

				// Create particle generator for the ball explosion
				addNewParticleGenerator(b.getX(), b.getY(), 1, (int) (Math.random() * 50 + 200), 7, b.getColor());

				// Remove the ball from the game
				b.selfDestruct();
			}
			// Remove the processed index from the list
			toExplode.remove(0);
		} else {
			// If there are no elements to explode, remove all balls and powerups from the game
			for (Ball b : allBalls) {
				remove(b);
			}
			allBalls.clear();
			allPowerups.clear();
		}
	}



	/**
	 * Adds a new ball to the game.
	 * This method creates a new ball object and adds it to the game if the maximum ball limit has not been reached.
	 * The new ball is created with random initial position and velocity within the game field boundaries.
	 *
	 * @see Ball
	 * @see #getFieldWidth()
	 * @see #getFieldHeight()
	 */
	public void addNewBall() {
		// Check if the maximum ball limit has been reached
		if (allBalls.size() > 50) {
			return;
		}
		// Create a new ball with random initial position and velocity
		Ball newBall = new Ball(getFieldWidth(), getFieldHeight());
		// Add the new ball to the list of all balls
		allBalls.add(newBall);
		// Add the new ball to the game canvas
		add(newBall);
	}


	/**
	 * Adds a new power-up to the game.
	 *
	 * This method creates a new power-up object and adds it to the game if the maximum power-up limit has not been reached.
	 * The new power-up is created with random initial position within the game field boundaries.
	 *
	 * @see PowerUp
	 * @see #getFieldWidth()
	 * @see #getFieldHeight()
	 */
	private void addNewPowerup() {
		// Check if the maximum power-up limit has been reached
		if (allPowerups.size() > 50) {
			return;
		}
		// Create a new power-up with random initial position
		PowerUp newPowerup = new PowerUp(getFieldWidth(), getFieldHeight());
		// Add the new power-up to the list of all power-ups
		allPowerups.add(newPowerup);
		// Add the new power-up to the game canvas
		add(newPowerup);
	}


	/**
	 * Adds a new particle generator to the game.
	 *
	 * This method creates a new particle generator object and adds it to the game. The particle generator is created
	 * with the specified position, velocity, size, color, and reference to the game instance.
	 *
	 * @param x The x-coordinate of the particle generator's position.
	 * @param y The y-coordinate of the particle generator's position.
	 * @param velocity The velocity of the particles emitted by the generator.
	 * @param color The color of the particles emitted by the generator.
	 * @see ParticleGenerator
	 */
	private void addNewParticleGenerator(int x, int y, int duration, int velocity, int lifespan, Color color) {
		// Create a new particle generator with the specified parameters
		ParticleGenerator newParticleGenerator = new ParticleGenerator(x, y, duration, velocity, 10, lifespan, color, this);
		// Add the new particle generator to the list of all particle generators
		allParticleGenerators.add(newParticleGenerator);
		// Add the new particle generator to the game canvas
		add(newParticleGenerator);
	}

	/**
	 * Sets the game state to MENU.
	 */
	public void setStateMenu() {
		currentState = GameState.MENU;
	}

	/**
	 * Sets the game state to PLAYING.
	 */
	public void setStatePlaying() {
		currentState = GameState.PLAYING;
	}

	/**
	 * Sets the game state to GAME_OVER.
	 */
	public void setStateOver() {
		currentState = GameState.GAME_OVER;
	}

	/**
	 * Main method to launch the Pong game.
	 *
	 * @param args Command-line arguments (unused).
	 * @throws IOException If an I/O error occurs.
	 */
	public static void main(String[] args) throws IOException {
		// Create a new instance of the Pong game
		Pong p = new Pong();
		// Set the game window to be visible
		p.setVisible(true);
		// Initialize game components
		p.initComponents();
	}



}