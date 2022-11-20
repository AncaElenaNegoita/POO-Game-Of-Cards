package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;

import java.util.ArrayList;

public class Game {
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    Player player1;
    Player player2;
    int switchPlayer;
    int countRounds;
    int player1Win = 0;
    int player2Win = 0;
    CommandHelpfulFunctions functions = new CommandHelpfulFunctions();

    public void Actions(Input input, ArrayNode output) {
        for (GameInput game : input.getGames()) {
            ArrayList<ArrayList<Minion>> gameTable = new ArrayList<>();
            countRounds = 2;
            for (int i = 0; i < 4; i++)
                gameTable.add(new ArrayList<>());

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
                    node.put("command", action.getCommand());
                    node.put("playerIdx", action.getPlayerIdx());
                    if (action.getPlayerIdx() == 1) {
                        node.putPOJO("output", new Hero(player1.hero));
                    } else {
                        node.putPOJO("output", new Hero(player2.hero));
                    }
                } else if (action.getCommand().equals("getPlayerTurn")) {
                    node.put("command", action.getCommand());
                    node.putPOJO("output", switchPlayer);
                } else if (action.getCommand().equals("placeCard")) {
                    node = functions.placeCard(action, player1, player2, switchPlayer, node, gameTable);
                } else if (action.getCommand().equals("endPlayerTurn")) {
                    countRounds++;
                    if (countRounds % 2 == 0) {
                        player1.mana += countRounds / 2;
                        player2.mana += countRounds / 2;
                        functions.drawCard(player1);
                        functions.drawCard(player2);
                    }

                    if (switchPlayer == 1) {
                        switchPlayer++;
                        functions.resetMinionsStatus(3, gameTable, player1);
                    } else {
                        switchPlayer--;
                        functions.resetMinionsStatus( 0, gameTable, player2);
                    }
                } else if (action.getCommand().equals("getPlayerMana")) {
                    node.put("command", action.getCommand());
                    node.put("playerIdx", action.getPlayerIdx());
                    if (action.getPlayerIdx() == 1)
                        node.put("output", player1.mana);
                    else
                        node.put("output", player2.mana);
                } else if (action.getCommand().equals("getCardsOnTable")) {
                    node.put("command", action.getCommand());
                    ArrayList<ArrayList<Minion>> tableCopy = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        tableCopy.add(new ArrayList<>());
                        for (int j = 0; j < gameTable.get(i).size(); j++) {
                            tableCopy.get(i).add(gameTable.get(i).get(j));
                        }
                        arrayNode.addPOJO(new ArrayList<>(tableCopy.get(i)));
                    }
                    node.putPOJO("output", arrayNode);
                }

                if (!node.isEmpty())
                    output.add(node);
            }
        }
    }
}
