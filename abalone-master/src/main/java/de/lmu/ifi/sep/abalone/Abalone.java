package de.lmu.ifi.sep.abalone;

import de.lmu.ifi.sep.abalone.components.GameObserver;
import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.logic.AbaloneGame;
import de.lmu.ifi.sep.abalone.logic.communication.EventBus;
import de.lmu.ifi.sep.abalone.logic.communication.publishers.EventPublisher;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.BoardEventListener;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.ErrorEventListener;
import de.lmu.ifi.sep.abalone.logic.communication.subscribers.EventBusSubscription;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard;
import de.lmu.ifi.sep.abalone.network.ConnectorObserver;
import de.lmu.ifi.sep.abalone.network.Network;
import de.lmu.ifi.sep.abalone.network.NetworkUtilities;
import de.lmu.ifi.sep.abalone.views.AbaloneView;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import static javax.swing.JFrame.MAXIMIZED_BOTH;

/**
 * Constructs a new Abalone game and is responsible for the GUI.
 */
public class Abalone {

    /**
     * Frame that contains GUI.
     */
    private final JFrame myFrame;

    /**
     * Construction of a Start JPanel containing two JButtons: "Start game" and
     * "Join Game". Necessary to initiate prior to start to have correct rendering.
     */
    private JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 305));
    private JPanel hostPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 305));
    private JPanel guestPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 305));
    private JPanel waitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 305));
    private JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 305));
    private JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 305));
    private JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
    private JLabel turnLabel = new JLabel("");
    private JLabel playerLabel = new JLabel("");

    /**
     * JPanel GUI of this game.
     */
    private AbaloneView boardView;

    /**
     * If the player cancels connection from Guest side, stops Game Logic from
     * initiating.
     */
    private boolean cancelled = false;

    /**
     * Size set by Host.
     */
    private int gameSize = 0;

    /**
     * Pointer to the Network that relays all network related information.
     */
    private Network network;

    /**
     * Pointer to controller.
     */
    private AbaloneGame abaloneGame;

    /**
     * String representation of port number, IP address, and the name of Host.
     */
    private String port, ip, hostname;

    /**
     * Logger to relay all game information.
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Observer to game events such as next turn or game won.
     */
    private GameObserver gameObserver;

    /**
     * EventBus allows publish-subscribe communication between components
     * without requiring components to explicitly register with one another.
     */
    private final EventBus eventBus;

    /**
     * Subscription to error events
     */
    private EventBusSubscription<ErrorEventListener> errorEventSubscription;

    /**
     * Subscription to board events
     */
    private EventBusSubscription<BoardEventListener> boardEventSubscription;

    /**
     * Close start dialog and starts the game if network connection succeeded.
     */
    private final ConnectorObserver connectorObserver = () -> initGame(null);

    /**
     * Create start window of Abalone game
     */
    private Abalone() {
        myFrame = new JFrame("Abalone");
        myFrame.setLayout(new BorderLayout());
        myFrame.setExtendedState(MAXIMIZED_BOTH);
        myFrame.setMinimumSize(new Dimension(800, 700));
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.setJMenuBar(createMenuBar());
        myFrame.setVisible(true);
        turnPanel.setOpaque(false);
        myFrame.revalidate();
        eventBus = new EventBus();
        this.errorEventSubscription = eventBus.newErrorSubscription();
        this.boardEventSubscription = eventBus.newBoardSubscription();
        setupSubscriptions();
        createStartPanel();
    }

    /**
     * Main entry point for Abalone Game.
     *
     * @param args to run
     */
    public static void main(String[] args) {
        Runnable guiCreator = () -> {
            try {
                for (LookAndFeelInfo info : UIManager
                        .getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        new Abalone();
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        // Schedules application to run as EDT
        SwingUtilities.invokeLater(guiCreator);
    }

    /**
     * Initiates subscriptions to events both from Game Logic and Network.
     */
    private void setupSubscriptions() {
        this.boardEventSubscription.attachEventHandler(new BoardEventListener() {
            @Override
            public void handleWin() {
                youWon();
            }

            @Override
            public void handleBoardReady() {
                startView();
            }
        });

        this.errorEventSubscription.attachEventHandler(new ErrorEventListener() {
            @Override
            public void handleSyncError() {
                return;
            }

            @Override
            public void handleNetworkError() {
                return;
            }
        });

    }

    /**
     * Constructs a menu with clickable menu entries.
     *
     * @return New menu.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menu = new JMenuBar();
        JMenu game = new JMenu("Game");
        JMenu help = new JMenu("Help");
        JMenu exit = new JMenu("Exit");
        menu.add(game);
        menu.add(help);
        menu.add(exit);
        JMenuItem aboutItem, helpItem, exitItem;
        game.add(aboutItem = new JMenuItem("About"));
        help.add(helpItem = new JMenuItem("Help"));
        exit.add(exitItem = new JMenuItem("Exit"));
        aboutItem.addActionListener(event -> aboutInfo());
        helpItem.addActionListener(event -> helpInfo());
        exitItem.addActionListener(event -> {
            myFrame.dispose(); // close window
            System.exit(0); // stop program
        });
        return menu;
    }

    /**
     * Creates a new start button.
     *
     * @param label The name of the button.
     * @return New button.
     */
    private JButton createStartButton(String label) {
        JButton startButton = new JButton(label);
        startButton.setPreferredSize(new Dimension(150, 60));
        startButton.setFont(new Font("Arial", Font.PLAIN, 17));
        return startButton;
    }

    /**
     * Creates a new starting panel and constructs two starting buttons. Adds them to the panel.
     */
    private void createStartPanel() {
        myFrame.revalidate();
        startPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 350));
        /*create buttons "Start game" and "Join game"*/
        JButton startButton = createStartButton("Start game");
        JButton joinButton = createStartButton("Join game");
        /*Events after pressing "Start game" und "Join game"*/
        startButton.addActionListener(event -> {
            deactivatePanel(startPanel);
            entryHost();
        });
        joinButton.addActionListener(event -> {
            deactivatePanel(startPanel);
            entryGuest();
        });
        startPanel.add(startButton);
        startPanel.add(joinButton);
        myFrame.add(startPanel, BorderLayout.SOUTH);
        myFrame.revalidate();
    }

    /**
     * Constructs a panel in which the host chooses the game size.
     */
    private void entryHost() {
        hostPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 350));
        myFrame.add(hostPanel, BorderLayout.SOUTH);
        myFrame.revalidate();

        JLabel sizeLabel = new JLabel("Set size: ");
        JButton sevenButton = new JButton("    7   ");
        JButton nineButton = new JButton("    9   ");
        JButton elevenButton = new JButton("  11  ");
        JButton thirteenButton = new JButton("  13  ");
        JButton cancelButton = new JButton("Cancel");
        JLabel portLabel = new JLabel("Set port: ");
        JTextField inputPort = new JTextField(NetworkUtilities.getDefaultPort(), 15);

        Component[] components = {portLabel, inputPort, sizeLabel, sevenButton, nineButton,
                elevenButton, thirteenButton, cancelButton};
        for (Component component : components) {
            hostPanel.add(component);
        }
        sevenButton.addActionListener(event -> {
            port = inputPort.getText();
            gameSize = 7;
            deactivatePanel(hostPanel);
            myFrame.setMinimumSize(new Dimension(450, 500));
            validateHostInput();
        });
        nineButton.addActionListener(event -> {
            port = inputPort.getText();
            gameSize = 9;
            deactivatePanel(hostPanel);
            myFrame.setMinimumSize(new Dimension(550, 600));
            validateHostInput();
        });
        elevenButton.addActionListener(event -> {
            port = inputPort.getText();
            gameSize = 11;
            myFrame.setMinimumSize(new Dimension(600, 650));
            deactivatePanel(hostPanel);
            validateHostInput();
        });
        thirteenButton.addActionListener(event -> {
            port = inputPort.getText();
            gameSize = 13;
            myFrame.setMinimumSize(new Dimension(700, 750));
            deactivatePanel(hostPanel);
            validateHostInput();
        });
        cancelButton.addActionListener(event -> {
            deactivatePanel(hostPanel);
            gameSize = 0;
            createStartPanel();
        });
    }

    /**
     * Constructs a panel in which the guest player fills in host address and port.
     */
    private void entryGuest() {
        guestPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 350));
        JTextField inputIp = new JTextField("xxx.xxx.xxx.xxx", 15);
        JTextField inputPort = new JTextField(NetworkUtilities.getDefaultPort(), 15);
        JLabel ipLabel = new JLabel("Host address/IP: ");
        JLabel portLabel = new JLabel("Port: ");
        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");

        Component[] components = {ipLabel, inputIp, portLabel, inputPort, okButton, cancelButton};
        for (Component component : components) {
            guestPanel.add(component);
        }

        okButton.addActionListener(event -> {
            deactivatePanel(guestPanel);
            this.ip = inputIp.getText();
            this.port = inputPort.getText();
            cancelled = false;
            validateGuestInput();
        });

        cancelButton.addActionListener(event -> {
            deactivatePanel(guestPanel);
            this.ip = null;
            this.port = null;
            network.close();
            createStartPanel();
        });

        myFrame.add(guestPanel, BorderLayout.SOUTH);
        myFrame.revalidate();
    }

    /**
     * Checks user input: -ip and -port.
     */
    private void validateGuestInput() {
        if (NetworkUtilities.validateHost(ip)) {
            if (NetworkUtilities.validatePort(port)) {
                startGuestNetwork();
                connecting();
            } else {
                errorPort(false);
            }
        } else {
            errorIpGuest();
        }
    }

    /**
     * Checks host input: -port
     */
    private void validateHostInput() {
        if (NetworkUtilities.validatePort(port)) {
            startHostNetwork();
            waitingForPlayer();
        } else {
            errorPort(true);
        }
    }

    /**
     * Constructs a new waiting panel for host.
     */
    private void waitingForPlayer() {
        waitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 350));
        try {
            hostname = NetworkUtilities.getLocalHostName();
            ip = NetworkUtilities.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            errorInterrupt();
        }
        deactivatePanel(hostPanel);
        JLabel ipLabel = new JLabel("Your IP address: " + ip);
        JLabel hostLabel = new JLabel("Your hostname: " + hostname);
        JLabel portLabel = new JLabel("Port: " + port);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> {
            deactivatePanel(waitPanel);
            gameSize = 0;
            entryHost();
            network.close();
        });
        waitPanel.add(ipLabel);
        waitPanel.add(hostLabel);
        waitPanel.add(portLabel);
        waitPanel.add(cancelButton);
        myFrame.add(waitPanel, BorderLayout.SOUTH);
        myFrame.revalidate();
    }

    /**
     * Creates a waiting panel with a cancel option for guest.
     */
    private void connecting() {
        connectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 350));
        deactivatePanel(hostPanel);
        deactivatePanel(guestPanel);
        JLabel connectLabel = new JLabel("Connecting with player...");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> {
            cancelled = true;
            this.ip = null;
            this.port = null;
            connectPanel.setVisible(false);
            myFrame.remove(connectPanel);
            myFrame.revalidate();
            entryGuest();
            network.close();
        });
        connectPanel.add(connectLabel);
        connectPanel.add(cancelButton);
        myFrame.add(connectPanel, BorderLayout.SOUTH);
    }

    /**
     * Shows an info screen.
     */
    private void aboutInfo() {
        JTextArea textArea = new JTextArea("Abalone is an award-winning " +
                "two player abstract strategy board game. This adaption was " +
                "developed by Caroline Frank, Jana Klinitska, Simon Lund, " +
                "Dennis Simon and Jonas Meissner for Software Development " +
                "Internship at Ludwig Maximilian University of Munich, " +
                "Germany Summer 2018.", 6, 40);
        textArea.setFont(new Font("Serif", Font.ITALIC, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JOptionPane.showOptionDialog(myFrame, textArea, null,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new Object[]{}, null);
        myFrame.revalidate();
    }

    /**
     * Shows a small info screen about the game.
     */
    private void helpInfo() {
        JTextArea textArea = new JTextArea(
                "Players play as opposing black and white marbles on a " +
                        "hexagonal board with the objective of pushing the " +
                        "opponent's marbles off the edge of the board. The " +
                        "players alternate turns starting with the black " +
                        "marbles moving first. \n\nA player can move one, " +
                        "two or three marbles of their color each turn. " +
                        "The marbles must be adjacent, in the same row and " +
                        "can move in any direction if the space is empty or " +
                        "occupied by a moving marble. There are three types " +
                        "of moves. The first, \"side step\", moves the " +
                        "marbles parallel to the line of marbles. The second" +
                        ", \"in-line\", moves the marbles in a direction " +
                        "with respect to the line of marbles. The last, " +
                        "\"sumito\", allows a player to push opponent′s " +
                        "marbles that are in-line to their own. A push is " +
                        "only possible if the pushing line has more marbles " +
                        "than the pushed line (three can push one or two; " +
                        "two can push one) and the opponent is being pushed " +
                        "either off the board or to an empty space. \n\nThe " +
                        "winner is the first player to push off 4, 6, 7 or " +
                        "8 opponent's marbles off the board determined by " +
                        "the board size 7, 9, 11, or 13, respectively."
                , 15, 47);
        textArea.setFont(new Font("Serif", Font.ITALIC, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JOptionPane.showOptionDialog(myFrame, textArea, "How to play Abalone",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new Object[]{}, null);
        myFrame.revalidate();
    }

    /**
     * Shows whose turn it is.
     */
    private void yourTurn(AbaloneBoard.Owner next) {
        turnLabel.setText("Player " + AbaloneBoard.getOwnerName(next) + "'s turn.");
        myFrame.revalidate();
    }

    /**
     * Signals that a player has won.
     */
    private void youWon() {
        deactivatePanel(turnPanel);
        String won = AbaloneBoard.getOwnerName(abaloneGame.getActivePlayer().getOwner());
        int restart = JOptionPane.showConfirmDialog(myFrame, "The " +
                        won
                        + " player has won!" + "\n\n" +
                        "Do you want to restart the game?\n" +
                        "(Sides will be switched)",
                "We have a winner!", JOptionPane.YES_NO_OPTION);
        myFrame.dispose(); // close window

        if (restart == JOptionPane.YES_OPTION) {
            deactivatePanel(boardView);
            initGame(abaloneGame.getSwitchedPlayerColor());
            if (network.getClientType() == Network.ClientType.GUEST) {
                myFrame.add(connectPanel, BorderLayout.SOUTH);
            }
        } else {
            network.close();
            System.exit(0);
        }
    }

    /**
     * Deactivate JPanel.
     *
     * @param panel The panel to deactivate.
     */
    private void deactivatePanel(JPanel panel) {
        panel.setVisible(false);
        myFrame.remove(panel);
        myFrame.repaint();
        myFrame.revalidate();
    }

    /**
     * Shows error if port is not available.
     */
    private void errorPort(boolean isHost) {
        errorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 350));
        deactivatePanel(hostPanel);
        deactivatePanel(guestPanel);
        JLabel errorLabel = new JLabel("Port validation failed! Try again.");
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(event -> {
            deactivatePanel(errorPanel);
            if (isHost) {
                entryHost();
            } else {
                entryGuest();
            }
        });
        errorPanel.add(errorLabel);
        errorPanel.add(okButton);
        myFrame.add(errorPanel, BorderLayout.SOUTH);
    }

    /**
     * Shows error if IP address or Host is not available.
     */
    private void errorIpGuest() {
        errorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 350));
        deactivatePanel(hostPanel);
        deactivatePanel(guestPanel);
        JLabel errorLabel = new JLabel("Address validation failed! Try again.");
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(event -> {
            deactivatePanel(errorPanel);
            entryGuest();
        });
        errorPanel.add(errorLabel);
        errorPanel.add(okButton);
        myFrame.add(errorPanel, BorderLayout.SOUTH);
    }

    /**
     * Shows error if connection fails.
     */
    private void errorInterrupt() {
        JOptionPane.showOptionDialog(myFrame, "Unexpected Error! Please Try again.", " ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new Object[]{}, null);
        myFrame.remove(turnLabel);
        ip = null;
        port = null;
        hostname = null;
        if (boardView != null) {
            deactivatePanel(boardView);
        }
        deactivatePanel(startPanel);
        deactivatePanel(hostPanel);
        deactivatePanel(guestPanel);
        deactivatePanel(waitPanel);
        deactivatePanel(connectPanel);
        deactivatePanel(errorPanel);
        createStartPanel();
    }

    /**
     * Starts host network.
     */
    private void startHostNetwork() {
        network = new Network(port, new EventPublisher<>(eventBus));
        network.connect(connectorObserver);
    }

    /**
     * Starts guest network.
     */
    private void startGuestNetwork() {
        try {
            network = new Network(ip, port, new EventPublisher<>(eventBus));
            network.connect(connectorObserver);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts {@code AbaloneView} class which is a JPanel with board GUI.
     */
    private void startView() {
        Runnable guiBoard = () -> {
            try {
                boardView = new AbaloneView(abaloneGame);
                abaloneGame.addObserver(gameObserver);
                deactivatePanel(connectPanel);
                deactivatePanel(waitPanel);
                boardView.setLocale(null);
                myFrame.add(boardView, BorderLayout.CENTER);
                myFrame.setVisible(true);
                playerLabel.setText("<html><b>PLAYER "+
                        AbaloneBoard.getOwnerName(abaloneGame.getLocalPlayer().
                                getOwner()).toUpperCase()+"</html>");
                myFrame.add(turnPanel, BorderLayout.NORTH);
                turnPanel.add(playerLabel);
                turnPanel.add(turnLabel);
                turnPanel.setVisible(true);
                yourTurn(abaloneGame.getActivePlayer().getOwner());
            } catch (RuntimeException e) {
                logger.info(e.getLocalizedMessage());
                e.printStackTrace();
                errorInterrupt();
            }
        };
        SwingUtilities.invokeLater(guiBoard);
    }

    /**
     * Start Abalone game class.
     *
     * @param newSide Is null if first round of game, otherwise player color.
     */
    private void startLogic(AbaloneBoard.Owner newSide) {
        try {
            if (network.getClientType() == Network.ClientType.GUEST) {
                gameSize = 0;
            }
            if (newSide != null) {
                abaloneGame = new AbaloneGame(gameSize, network, newSide,
                        new EventPublisher<>(eventBus),
                        new EventPublisher<>(eventBus));
            } else {
                abaloneGame = new AbaloneGame(gameSize, network,
                        new EventPublisher<>(eventBus),
                        new EventPublisher<>(eventBus));
            }

            gameObserver = new GameObserver() {
                @Override
                public void setValidMoves(List<Vector> validMoves) {
                }

                @Override
                public void setValidClicks(List<Vector> validClicks) {
                }

                @Override
                public void endTurn(AbaloneBoard.Owner next) {
                    yourTurn(next);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            errorInterrupt();
        }
    }

    /**
     * Initializes Controller, which handles Game Logic and communication over
     * the network between Host and Guest and AbaloneView, which is the GUI
     * of the AbaloneBoard.
     *
     * @param newSide Holds the color of player on rematch, otherwise {@code null}.
     */
    private void initGame(AbaloneBoard.Owner newSide) {
        myFrame.repaint();
        myFrame.revalidate();
        if (network.getClientType().equals(Network.ClientType.GUEST) && !cancelled) {
            startLogic(newSide);
        } else if (gameSize != 0) {
            startLogic(newSide);
            startView();
        }
    }

}