/**
 * This is a rendition of the Abalone Board game programmed for Software
 * Development Practical Course at Ludwig Maximilian University of Munich,
 * Germany, Summer 2018.
 * <p>
 * <p> Active participants were Caroline Frank, Jana Klinitska, Simon Lund,
 * Jonas Meißner, and Dennis Simon with guidance by Matthias Dangl.
 * <p>
 * <p> Structure is based on the classic Model-View-Controller architecture,
 * with an implementation of Observer-Pattern. Graphical user interfaces
 * were implemented using Java Swing. For additional information, please
 * see corresponding classes or packages.
 * <p>
 * <p> {@code Abalone} class implements the entry method {@code main} and
 * responsible for delegation of user inputs, starting network connection
 * and controller.
 * <p>
 * <p> {@code views package} Represents the View in MVC architecture.
 * Responsible for game GUI handling user interactions and passing to
 * controller.
 * <p>
 * <p> {@code models package} Represents the Model in MVC architecture.
 * Responsible for holding game data.
 * <p>
 * <p> {@code logic package} Represents the Controller in MVC architecture.
 * {@code AbaloneGame class} is them main controller of the game, receiving
 * user inputs delegating responses including changing the board structure
 * and interacting over the Network. {@code Context} class is a utility class
 * responsible for implementing all game rules.
 * <p>
 * <p> {@code network package} Allows users to connect over a network.
 * <p>
 * <p> {@code components package} Includes classes that implement the
 * Observer-Observable pattern in between classes and a {@code Vector}
 * class to work in a coordinates System.
 *
 * @version 1.0
 * @since 1.0
 */
package de.lmu.ifi.sep.abalone;