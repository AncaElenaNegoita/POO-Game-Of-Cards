package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;

import java.util.ArrayList;

public final class Game {
    private ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private Player player1;
    private Player player2;
    private int switchPlayer;
    private int countRounds;
    private int player1Win = 0;
    private int player2Win = 0;
    static final int PLAYER1ROW = 3;
    static final int ALLROW = 4;
    private CommandHelpfulFunctions functions = new CommandHelpfulFunctions();

    /**
     *
     * @param input
     * @param output
     */
    public void actions(final Input input, final ArrayNode output) {
        for (GameInput game : input.getGames()) {
            ArrayList<ArrayList<Minion>> gameTable = new ArrayList<>();
            countRounds = 2;
            for (int i = 0; i < ALLROW; i++) {
                gameTable.add(new ArrayList<>());
            }
            player1 = new Player(1, game.getStartGame().getPlayerOneDeckIdx(), input, game);
            player2 = new Player(2, game.getStartGame().getPlayerTwoDeckIdx(), input, game);
            switchPlayer = game.getStartGame().getStartingPlayer();

            for (ActionsInput action : game.getActions()) {
                ArrayNode arrayNode = mapper.createArrayNode();
                ObjectNode node = mapper.createObjectNode();
                if (action.getCommand().equals("getPlayerDeck")) {
                    node.put("command", action.getCommand());
                    node.put("playerIdx", action.getPlayerIdx());
                    if (action.getPlayerIdx() == 1) {
                        node.putPOJO("output", new ArrayList<>(player1.deck));
                    } else {
                        node.putPOJO("output", new ArrayList<>(player2.deck));
                    }
                } else if (action.getCommand().equals("getPlayerHero")) {
                    node = functions.getPlayerHero(action, node, player1, player2);
                } else if (action.getCommand().equals("getPlayerTurn")) {
                    node.put("command", action.getCommand());
                    node.putPOJO("output", switchPlayer);
                } else if (action.getCommand().equals("placeCard")) {
                    node = functions.placeCard(action, player1, player2,
                            switchPlayer, node, gameTable);
                } else if (action.getCommand().equals("endPlayerTurn")) {
                    countRounds = functions.verifyCount(player1, player2, countRounds);
                    if (switchPlayer == 1) {
                        switchPlayer++;
                        functions.resetMinionsStatus(PLAYER1ROW, gameTable, player1);
                    } else {
                        switchPlayer--;
                        functions.resetMinionsStatus(0, gameTable, player2);
                    }
                } else if (action.getCommand().equals("getPlayerMana")) {
                    node.put("command", action.getCommand());
                    node.put("playerIdx", action.getPlayerIdx());
                    if (action.getPlayerIdx() == 1) {
                        node.put("output", player1.mana);
                    } else {
                        node.put("output", player2.mana);
                    }
                } else if (action.getCommand().equals("getCardsOnTable")) {
                    node.put("command", action.getCommand());
                    ArrayList<ArrayList<Minion>> tableCopy = new ArrayList<>();
                    for (int i = 0; i < ALLROW; i++) {
                        tableCopy.add(new ArrayList<>());
                        for (int j = 0; j < gameTable.get(i).size(); j++) {
                            tableCopy.get(i).add(gameTable.get(i).get(j));
                        }
                        arrayNode.addPOJO(new ArrayList<>(tableCopy.get(i)));
                    }
                    node.putPOJO("output", arrayNode);
                } else if (action.getCommand().equals("getCardsInHand")) {
                    node.put("command", action.getCommand());
                    node.put("playerIdx", action.getPlayerIdx());
                    if (action.getPlayerIdx() == 1) {
                        ArrayList<CardInput> cardsCopy = new ArrayList<>();
                        for (CardInput card: player1.handCards) {
                            if (card.getHealth() == 0) {
                                cardsCopy.add(new Environment(card));
                            } else {
                                cardsCopy.add(new Minion(card));
                            }
                        }
                        node.putPOJO("output", new ArrayList<>(cardsCopy));
                    } else {
                        ArrayList<CardInput> cardsCopy = new ArrayList<>();
                        for (CardInput card: player2.handCards) {
                            if (card.getHealth() == 0) {
                                cardsCopy.add(new Environment(card));
                            } else {
                                cardsCopy.add(new Minion(card));
                            }
                        }
                        node.putPOJO("output", new ArrayList<>(cardsCopy));
                    }
                } else if (action.getCommand().equals("getEnvironmentCardsInHand")) {
                    node.put("command", action.getCommand());
                    node.put("playerIdx", action.getPlayerIdx());
                    if (action.getPlayerIdx() == 1) {
                        node.putPOJO("output", new ArrayList<>(player1.handEnvironmentCards()));
                    } else {
                        node.putPOJO("output", new ArrayList<>(player2.handEnvironmentCards()));
                    }
                } else if (action.getCommand().equals("useEnvironmentCard")) {
                    node = functions.useEnvironmentCard(action, node, player1, player2,
                            switchPlayer, gameTable);
                } else if (action.getCommand().equals("getCardAtPosition")) {
                    node = functions.getCardAtPosition(action, node, gameTable);
                } else if (action.getCommand().equals("getFrozenCardsOnTable")) {
                    node.put("command", action.getCommand());
                    arrayNode = functions.getFrozenCards(gameTable, arrayNode);
                    node.putPOJO("output", arrayNode);
                } else if (action.getCommand().equals("cardUsesAttack")) {
                    if (switchPlayer == 1) {
                        node = functions.cardAttack(action, node, gameTable, PLAYER1ROW);
                    } else {
                        node = functions.cardAttack(action, node, gameTable, 0);
                    }
                } else if (action.getCommand().equals("cardUsesAbility")) {
                    if (switchPlayer == 1) {
                        node = functions.cardUsesAbility(action, node, gameTable, PLAYER1ROW);
                    } else {
                        node = functions.cardUsesAbility(action, node, gameTable, 0);
                    }
                } else if (action.getCommand().equals("useAttackHero")) {
                    if (switchPlayer == 1) {
                        node = functions.useAttackHero(action, node, player2, PLAYER1ROW,
                                gameTable);
                        if (player2.hero.getHealth() <= 0) {
                            player1Win++;
                            node.put("gameEnded", "Player one killed the enemy hero.");
                        }
                    } else {
                        node = functions.useAttackHero(action, node, player1, 0, gameTable);
                        if (player1.hero.getHealth() <= 0) {
                            player2Win++;
                            node.put("gameEnded", "Player two killed the enemy hero.");
                        }
                    }
                } else if (action.getCommand().equals("useHeroAbility")) {
                    if (switchPlayer == 1) {
                        node = functions.useHeroAbility(action, node, player1, gameTable,
                                PLAYER1ROW);
                    } else {
                        node = functions.useHeroAbility(action, node, player2, gameTable, 0);
                    }
                } else if (action.getCommand().equals("getPlayerOneWins")) {
                    node.put("command", action.getCommand());
                    node.put("output", player1Win);
                } else if (action.getCommand().equals("getPlayerTwoWins")) {
                    node.put("command", action.getCommand());
                    node.put("output", player2Win);
                } else if (action.getCommand().equals("getTotalGamesPlayed")) {
                    node.put("command", action.getCommand());
                    node.put("output", player1Win + player2Win);
                }

                if (!node.isEmpty()) {
                    output.add(node);
                }
            }
        }
    }
}
