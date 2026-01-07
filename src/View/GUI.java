package View;

import Controllers.Controller;
import Model.Card;
import Model.Game;
import Model.Players.Bot;
import Model.Players.Human;
import Model.Players.Player;
import Model.RuleSets.BaseVisitor;
import Model.RuleSets.ChaosVisitor;
import Model.RuleSets.InvertedVisitor;
import Model.Suit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import static Model.Suit.*;
import static Model.Suit.HEART;
import static View.ViewRequest.*;

public class GUI extends JFrame implements Observer {
    private Controller controller;
    private int nbPlayers = 3;
    private List<Player> players = new ArrayList<>();

    private JButton nouvellePartieButton;
    private JButton chargerUnePartieButton;
    private JPanel menuPanel;
    private JLabel titre;
    private JButton basiquesButton;
    private JButton inverseesButton;
    private JButton chaosButton;
    private JPanel rulesPanel;
    private JTabbedPane mainPanel;
    private JPanel gamePanel;
    private JLabel order;
    private JPanel extensionPanel;
    private JButton ajouterButton;
    private JButton annulerButton;
    private JPanel playersPanel;
    private JTextField nameField;
    private JRadioButton humainRadioButton;
    private JRadioButton botRadioButton;
    private JPanel nbPlayersPanel;
    private JButton a3JoueursButton;
    private JButton a4JoueursButton;
    private JRadioButton facileRadioButton;
    private JRadioButton difficileRadioButton;

    public GUI(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void update(Observable o, Object arg) {
        new Thread(() -> {
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
                    case MAKEHAND -> this.botMadeOffer((Bot) o);
                    case PICKCARD -> {
                        if (o instanceof Human) {
                            this.pickCard((Human) o);
                        } else {
                            this.botPickCard((Bot) o);
                        }
                    }
                    case TROPHIESGIVEN -> this.trophiesGiven();
                    case WINNER -> this.winner();
                    default -> throw new RuntimeException("/!\\ ViewRequest de Game irrecevable : " + arg);
                }
            }
        }).start();
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
                System.out.println(GUI.this.controller);
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
        basiquesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.controller.setRules(new BaseVisitor());
            }
        });
        inverseesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.controller.setRules(new InvertedVisitor());
            }
        });
        chaosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.controller.setRules(new ChaosVisitor());
            }
        });
    }

    private void showCardOrder() {
        String order = "";
        List<Suit> suits = Arrays.asList(SPADE, CLUB, DIAMOND, HEART);
        for (Suit suit : suits) {
            order += suit;
            if (suit.getValue() > 2) {
                order += " [+] ";
            } else  {
                order += " [-] ";
            }
        }
        this.order.setText(order);
    }

    private void chooseExtension() {
        this.mainPanel.setSelectedIndex(2);
        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.controller.addExtension(true);
            }
        });
        annulerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.controller.addExtension(false);
            }
        });
    }

    private void populate() {
        this.mainPanel.setSelectedIndex(3);
        a3JoueursButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.nbPlayers = 3;
                GUI.this.playerMenu();
            }
        });
        a4JoueursButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.nbPlayers = 4;
                GUI.this.playerMenu();
            }
        });
    }

    private void playerMenu() {
        this.mainPanel.setSelectedIndex(4);
        humainRadioButton.setSelected(true);
        botRadioButton.setSelected(false);
        facileRadioButton.setSelected(true);
        facileRadioButton.setEnabled(false);
        difficileRadioButton.setSelected(false);
        difficileRadioButton.setEnabled(false);
        humainRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botRadioButton.setSelected(false);
                facileRadioButton.setEnabled(false);
                difficileRadioButton.setEnabled(false);
            }
        });
        botRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                humainRadioButton.setSelected(false);
                facileRadioButton.setEnabled(true);
                difficileRadioButton.setEnabled(true);
            }
        });
        facileRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficileRadioButton.setSelected(false);
            }
        });
        difficileRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                facileRadioButton.setSelected(false);
            }
        });
    }

    private void showTrophies() {
    }

    private void botMadeOffer(Bot bot) {
    }

    private void makeHand(Player player, List<Card> cards) {
    }

    private void pickCard(Human player) {
    }

    private void botPickCard(Bot player) {
        this.controller.takeCard(player, player.getStrategy().takeCard(Game.getInstance().getPickables(player)));
    }

    private void trophiesGiven() {
    }

    private void winner() {
    }

}
