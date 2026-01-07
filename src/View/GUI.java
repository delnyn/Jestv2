package View;

import Controllers.Controller;
import Model.Card;
import Model.Game;
import Model.Players.Bot;
import Model.Players.Human;
import Model.Players.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static View.ViewRequest.*;

public class GUI extends JFrame implements Observer, Runnable {
    private Controller controller;

    private JButton nouvellePartieButton;
    private JButton chargerUnePartieButton;
    private JPanel menuPanel;
    private JLabel titre;
    private JButton basiquesButton;
    private JButton inverséesButton;
    private JButton chaosButton;
    private JPanel rulesPanel;
    private JTabbedPane mainPanel;

    public GUI(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {

    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof List<?>) {
            this.makeHand((Player) o, (List<Card>) arg);
        } else {
            switch (arg) {
                case MENU -> this.menu();
                case CHOOSERULES -> this.chooseRules();
                case SHOWCARDORDER -> this.showCardOrder();
                case CHOOSEEXTENSION -> this.chooseExtension();
                case POPULATE -> this.populate();
                case SHOWTROPHIES -> this.showTrophies();
                case MAKEHAND -> this.say("[ " + ((Bot) o).getName() + " a fait son offre ]");
                case PICKCARD -> {
                    if (o instanceof Human) {
                        this.pickCard((Human) o);
                    } else {
                        this.say("[ " + ((Bot) o).getName() + " a pris une carte ]");
                        this.controller.takeCard((Bot) o, ((Bot) o).getStrategy().takeCard(Game.getInstance().getPickables((Bot) o)));
                    }
                }
                case TROPHIESGIVEN -> this.trophiesGiven();
                case WINNER -> this.winner();
                default -> throw new RuntimeException("/!\\ ViewRequest de Game irrecevable : " + arg);
            }
        }
    }

    private void say(String s) {
        System.out.println(s);
    }

    private void menu() {
        setContentPane(mainPanel);
        mainPanel.setSelectedIndex(0);

        setTitle("Jest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setVisible(true);

        nouvellePartieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.controller.newGame();
            }
        });
        chargerUnePartieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.loadGame();
            }
        });
    }

    private void loadGame() {
    }

    private void chooseRules() {
        mainPanel.setSelectedIndex(1);
    }

    private void showCardOrder() {
    }

    private void chooseExtension() {
    }

    private void populate() {
    }

    private void showTrophies() {
    }

    private void makeHand(Player o, List<Card> arg) {
    }

    private void pickCard(Human o) {
    }

    private void trophiesGiven() {
    }

    private void winner() {
    }

}
