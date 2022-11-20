package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.Coordinates;

import java.util.ArrayList;

import static java.lang.Math.abs;

public final class CommandHelpfulFunctions {
    static final int FULLROW = 5;
    static final int PLAYER1ROW = 3;
    static final int MAXMANA = 10;
    static final int MAXROUNDS = 22;

    /**
     *
     * @param card -is the card on table that needs to be verified if it is from the back line
     * @return -if the card is form the back, return true
     */
    public boolean isBackMinion(final CardInput card) {
        return card.getName().equals("Sentinel")
                || card.getName().equals("Berserker")
                || card.getName().equals("The Cursed One")
                || card.getName().equals("Disciple");
    }

    /**
     *
     * @param card -the card from the deck in the player's hand that the
     *             player wants to put on table(it must be a Minion)
     * @param mana -the total mana of the current player
     * @param lineCard -the row of cards from the game table where the card
     *                 should be placed
     * @return
     */
    public String addNode(final CardInput card, final int mana,
                          final ArrayList<Minion> lineCard) {
        if (card.getHealth() == 0) {
            return "Cannot place environment card on table.";
        } else if (lineCard.size() == FULLROW) {
            return "Cannot place card on table since row is full.";
        } else if (mana < card.getMana()) {
            return "Not enough mana to place card on table.";
        }
        return "";
    }

    /**
     *
     * @param action -the actions that happen during a command(from input)
     * @param node -where is stored what the output should look like
     * @param player1 -first player
     * @param player2 -second player
     * @return
     */
    public ObjectNode getPlayerHero(final ActionsInput action, final ObjectNode node,
                                    final Player player1, final Player player2) {
        node.put("command", action.getCommand());
        node.put("playerIdx", action.getPlayerIdx());
        if (action.getPlayerIdx() == 1) {
            node.putPOJO("output", new Hero(player1.hero));
        } else {
            node.putPOJO("output", new Hero(player2.hero));
        }
        return node;
    }

    /**
     *
     *  @param action -the actions that happen during a command(from input)
     * @param player -current player
     * @param index -the back row of the current player (for player1: 3,
     *              for player2: 0)
     * @param node -where is stored what the output should look like
     * @param gameTable -the table that stores the placed cards to be played
     */
    public void verifyConditionsCard(final ActionsInput action, final Player player,
                                     final int index, final ObjectNode node,
                                     final ArrayList<ArrayList<Minion>> gameTable) {
        CardInput card = player.handCards.get(action.getHandIdx());

        node.put("command", action.getCommand());
        node.put("handIdx", action.getHandIdx());
        int minionRow = (isBackMinion(card)) ? index : abs(index - 1);

        String command = addNode(card, player.mana, gameTable.get(minionRow));
        if (command.equals("")) {
            player.mana -= card.getMana();
            gameTable.get(minionRow).add((Minion) card);
            player.handCards.remove(action.getHandIdx());
            node.removeAll();
        } else {
            node.put("error", command);
        }
    }

    /**
     *
     * @param action -the actions that happen during a command(from input)
     * @param player1 -first player
     * @param player2 -second player
     * @param switchPlayer -variable that shows the current player
     * @param node -where is stored what the output should look like
     * @param gameTable - game table that stores the placed cards to be used
     * @return
     */
    public ObjectNode placeCard(final ActionsInput action, final Player player1,
                                final Player player2, final int switchPlayer,
                                final ObjectNode node,
                                final ArrayList<ArrayList<Minion>> gameTable) {
        if (switchPlayer == 1) {
            this.verifyConditionsCard(action, player1, PLAYER1ROW, node, gameTable);
        } else {
            this.verifyConditionsCard(action, player2, 0, node, gameTable);
        }
        return node;
    }

    /**
     *
     * @param index -index of the current player(for player1 is 3(his backrow),
     *              for player2 0(his backrow))
     * @param gameTable -the map where the player places the cards he wants to
     *                  use
     * @param player -current player
     */
    public void resetMinionsStatus(final int index,
                                   final ArrayList<ArrayList<Minion>> gameTable,
                                   final Player player) {
        for (int i = 0; i < gameTable.get(abs(index - 1)).size(); i++) {
            gameTable.get(abs(index - 1)).get(i).stunnedMinion = false;
            gameTable.get(abs(index - 1)).get(i).attackedAnotherCard = false;
        }

        for (int i = 0; i < gameTable.get(index).size(); i++) {
            gameTable.get(index).get(i).stunnedMinion = false;
            gameTable.get(index).get(i).attackedAnotherCard = false;
        }
        player.hero.attackedAnotherCard = false;
    }

    /**
     *
     * @param player -the one that needs to draw a card
     */
    public void drawCard(final Player player) {
        if (!player.deck.isEmpty()) {
            player.handCards.add(player.deck.get(0));
            player.deck.remove(0);
        }
    }

    /**
     *
     * @param player1 -first player
     * @param player2 -second player
     * @param countRounds -counts the rounds
     * @return
     */
    public int verifyCount(final Player player1, final Player player2,
                           int countRounds) {
        countRounds++;
        if (countRounds % 2 == 0) {
            if (countRounds >= MAXROUNDS) {
                player1.mana += MAXMANA;
                player2.mana += MAXMANA;
            } else {
                player1.mana += countRounds / 2;
                player2.mana += countRounds / 2;
            }
            this.drawCard(player1);
            this.drawCard(player2);
        }
        return countRounds;
    }

    /**
     *
     * @param action -the actions that happen during a command(from input)
     * @param node -where is stored what the output should look like
     * @param player -current player
     * @param gameTable -the map/table that stores the placed cards that a player
     *                  can use
     * @param playerRow -the index of the back row of the current player
     */
    public void verifyConditionsEnvironment(final ActionsInput action, final ObjectNode node,
                                            final Player player,
                                            final ArrayList<ArrayList<Minion>> gameTable,
                                            final int playerRow) {
        CardInput card = player.handCards.get(action.getHandIdx());
        Environment envCard = new Environment();
        int index = action.getAffectedRow();
        node.put("command", action.getCommand());
        node.put("handIdx", action.getHandIdx());
        node.put("affectedRow", index);

        if (card.getHealth() != 0) {
            node.put("error", "Chosen card is not of type environment.");
        } else {
            if (player.mana < card.getMana()) {
                node.put("error", "Not enough mana to use environment card.");
            } else {
                if (action.getAffectedRow() == playerRow
                        || action.getAffectedRow() == abs(playerRow - 1)) {
                    node.put("error", "Chosen row does not belong to the enemy.");
                } else {
                    switch (card.getName()) {
                        case "Firestorm" :
                            envCard.firestorm(gameTable, index);
                            node.removeAll();
                            break;

                        case "Winterfell" :
                            envCard.winterfell(gameTable.get(index));
                            node.removeAll();
                            break;

                        default:
                            if (gameTable.get(PLAYER1ROW - index).size() == FULLROW) {
                                node.put("error",
                                        "Cannot steal enemy card since the player's row is full.");
                            } else {
                                envCard.heartHound(gameTable, index);
                                node.removeAll();
                            }
                            break;
                    }
                    if (node.isEmpty()) {
                        player.mana -= card.getMana();
                        player.handCards.remove(action.getHandIdx());
                    }
                }
            }
        }
    }

    /**
     *
     * @param action -the actions that happen during a command(from input)
     * @param node -where is stored what the output should look like
     * @param player1 -first player
     * @param player2 -second player
     * @param switchPlayer -variable that stores the index of the current player
     * @param gameTable -the game table that stores the placed cards that can be used
     * @return
     */
    public ObjectNode useEnvironmentCard(final ActionsInput action, final ObjectNode node,
                                         final Player player1,
                                         final Player player2, final int switchPlayer,
                                         final ArrayList<ArrayList<Minion>> gameTable) {
        if (switchPlayer == 1) {
            verifyConditionsEnvironment(action, node, player1, gameTable, PLAYER1ROW);
        } else {
            verifyConditionsEnvironment(action, node, player2, gameTable, 0);
        }
        return node;
    }

    /**
     *
     * @param gameTable
     * @param arrayNode
     * @return
     */
    public ArrayNode getFrozenCards(final ArrayList<ArrayList<Minion>> gameTable,
                                    final ArrayNode arrayNode) {
        for (int i = 0; i < gameTable.size(); i++) {
            for (int j = 0; j < gameTable.get(i).size(); j++) {
                if (gameTable.get(i).get(j).stunnedMinion) {
                    arrayNode.addPOJO(new Minion(gameTable.get(i).get(j)));
                }
            }
        }
        return arrayNode;
    }

    /**
     *
     * @param rowCards -the row of cards from the other player that may have tank Minions
     * @param y -the y coordonate where a tank should be if the front row is searched
     * @return
     */
    public Boolean isTankOrTableWithoutTank(final ArrayList<Minion> rowCards, final int y) {
        int foundOne = 0;
        for (int i = 0; i < rowCards.size(); i++) {
            if (rowCards.get(i).getName().equals("Goliath")
                    || rowCards.get(i).getName().equals("Warden")) {
                foundOne = 1;
                if (y == i) {
                    return true;
                }
            }
        }
        if (foundOne == 1) {
            return false;
        }

        return true;
    }

    /**
     *
     * @param attackerCard -the card of the current player from the game table that
     *                     wants to attack another card
     * @param action -the actions that happen during a command(from input)
     * @param node -where is stored what the output should look like
     * @param index -back row of the current player
     * @return
     */
    public int verifyAttackConditions(final Minion attackerCard, final ActionsInput action,
                                      final ObjectNode node, final int index) {
        node.put("command", action.getCommand());
        node.putPOJO("cardAttacker", new Coordinates(action.getCardAttacker()));
        node.putPOJO("cardAttacked", new Coordinates(action.getCardAttacked()));

        if (index == action.getCardAttacked().getX()
                || abs(index - 1) == action.getCardAttacked().getX()) {
            node.put("error", "Attacked card does not belong to the enemy.");
            return 0;
        } else if (attackerCard.attackedAnotherCard) {
            node.put("error", "Attacker card has already attacked this turn.");
            return 0;
        } else if (attackerCard.stunnedMinion) {
            node.put("error", "Attacker card is frozen.");
            return 0;
        }
        return 1;
    }

    /**
     *
     * @param action -the actions that happen during a command(from input)
     * @param node -where is stored what the output should look like
     * @param gameTable -the game table that stores the placed cards that can be used
     * @param index -back row of the current player
     * @return
     */
    public ObjectNode cardAttack(final ActionsInput action, final ObjectNode node,
                                 final ArrayList<ArrayList<Minion>> gameTable, final int index) {
        int attackedCardX = action.getCardAttacked().getX();
        int attackedCardY = action.getCardAttacked().getY();
        int attackerCardX = action.getCardAttacker().getX();
        int attackerCardY = action.getCardAttacker().getY();

        Minion attackedCard = gameTable.get(attackedCardX).get(attackedCardY);
        Minion attackerCard = gameTable.get(attackerCardX).get(attackerCardY);

        int verify = verifyAttackConditions(attackerCard, action, node, index);
        if (verify == 1) {
            if (action.getCardAttacked().getX() % PLAYER1ROW != 0) {
                if (!isTankOrTableWithoutTank(gameTable.get(abs(index - 2)),
                        attackedCardY)) {
                    node.put("error", "Attacked card is not of type 'Tank'.");
                } else {
                    attackedCard.gotAttacked(attackerCard, gameTable.get(attackedCardX),
                            attackedCardY);
                    node.removeAll();
                }
            } else {
                if (!isTankOrTableWithoutTank(gameTable.get(abs(index - 2)),
                        -1)) {
                    node.put("error", "Attacked card is not of type 'Tank'.");
                } else {
                    attackedCard.gotAttacked(attackerCard,
                            gameTable.get(attackedCardX), attackedCardY);
                    node.removeAll();
                }
            }
        }
        return node;
    }

    /**
     *
     * @param action -the actions that happen during a command(from input)
     * @param node -where is stored what the output should look like
     * @param gameTable -the game table that stores the placed cards that can be used
     * @return -what the output needs to show when the command happens
     */
    public ObjectNode getCardAtPosition(final ActionsInput action, final ObjectNode node,
                                        final ArrayList<ArrayList<Minion>> gameTable) {
        node.put("command", action.getCommand());
        node.put("x", action.getX());
        node.put("y", action.getY());
        if (gameTable.size() < action.getX()) {
            node.put("output", "No card available at that position.");
        } else {
            if (gameTable.get(action.getX()).size() < action.getY()) {
                node.put("output", "No card available at that position.");
            } else {
                node.putPOJO("output",
                        new Minion(gameTable.get(action.getX()).get(action.getY())));
            }
        }
        return node;
    }

    /**
     *
     * @param attackedCard -the card of the other player from the game table that
     *                     is attacked
     * @param attackerCard -the card of the current player from the game table that
     *                     wants to attack another card
     * @param action -the actions that happen during a command(from input)
     * @param gameTable -the game table that stores the placed cards that can be used
     */
    public void switchCaseAbility(final Minion attackedCard, final Minion attackerCard,
                                  final ActionsInput action,
                                  final ArrayList<ArrayList<Minion>> gameTable) {
        switch (attackerCard.getName()) {
            case "The Ripper":
                attackedCard.theRipper(attackerCard);
                break;

            case "Miraj":
                attackedCard.miraj(attackerCard);
                break;

            default:
                Boolean isTrue = attackedCard.theCursedOne(attackerCard);
                if (isTrue) {
                    int attackedCardX = action.getCardAttacked().getX();
                    int attackedCardY = action.getCardAttacked().getY();
                    gameTable.get(attackedCardX).remove(attackedCardY);
                }
                break;
        }
    }

    /**
     *
     * @param action -the actions that happen during a command(from input)
     * @param node -where is stored what the output should look like
     * @param gameTable -the game table that stores the placed cards that can be used
     * @param index -back row of the current player
     * @return
     */
    public ObjectNode cardUsesAbility(final ActionsInput action, final ObjectNode node,
                                      final ArrayList<ArrayList<Minion>> gameTable,
                                      final int index) {
        node.put("command", action.getCommand());
        node.putPOJO("cardAttacker", new Coordinates(action.getCardAttacker()));
        node.putPOJO("cardAttacked", new Coordinates(action.getCardAttacked()));

        int attackedCardX = action.getCardAttacked().getX();
        int attackedCardY = action.getCardAttacked().getY();
        int attackerCardX = action.getCardAttacker().getX();
        int attackerCardY = action.getCardAttacker().getY();

        Minion attackedCard = gameTable.get(attackedCardX).get(attackedCardY);
        Minion attackerCard = gameTable.get(attackerCardX).get(attackerCardY);

        if (attackerCard.getName().equals("Disciple")) {
            if (index != attackedCardX && abs(index - 1) != attackedCardX) {
                node.put("error", "Attacked card does not belong to the current player.");
            } else {
                attackedCard.disciple();
                node.removeAll();
            }
        } else {
            int verify = verifyAttackConditions(attackerCard, action, node, index);
            if (verify == 1) {
                if (action.getCardAttacked().getX() % PLAYER1ROW != 0) {
                    if (!isTankOrTableWithoutTank(gameTable.get(abs(index - 2)),
                            attackedCardY)) {
                        node.put("error", "Attacked card is not of type 'Tank'.");
                    } else {
                        switchCaseAbility(attackedCard, attackerCard, action, gameTable);
                        node.removeAll();
                    }
                } else {
                    if (!isTankOrTableWithoutTank(gameTable.get(abs(index - 2)),
                            -1)) {
                        node.put("error", "Attacked card is not of type 'Tank'.");
                    } else {
                        switchCaseAbility(attackedCard, attackerCard, action, gameTable);
                        node.removeAll();
                    }
                }
            }
        }
        return node;
    }

    /**
     *
     * @param action -the actions that happen during a command(from input)
     * @param node -where is stored what the output should look like
     * @param player -current player
     * @param index -back row of the current player
     * @param gameTable -the game table that stores the placed cards that can be used
     * @return -what the output will show after the command is given and verified
     */
    public ObjectNode useAttackHero(final ActionsInput action, final ObjectNode node,
                                    final Player player, final int index,
                                    final ArrayList<ArrayList<Minion>> gameTable) {
        node.put("command", action.getCommand());
        node.putPOJO("cardAttacker", new Coordinates(action.getCardAttacker()));

        int attackerCardX = action.getCardAttacker().getX();
        int attackerCardY = action.getCardAttacker().getY();

        Minion attackerCard = gameTable.get(attackerCardX).get(attackerCardY);

        if (attackerCard.attackedAnotherCard) {
            node.put("error", "Attacker card has already attacked this turn.");
        } else if (attackerCard.stunnedMinion) {
            node.put("error", "Attacker card is frozen.");
        } else {
            if (!isTankOrTableWithoutTank(gameTable.get(abs(index - 2)), -1)) {
                node.put("error", "Attacked card is not of type 'Tank'.");
            } else {
                player.hero.attackHero(attackerCard);
                node.removeAll();
            }
        }
        return node;
    }

    /**
     *
     * @param action -the actions that happen during a command(from input)
     * @param node -where is stored what the output should look like
     * @param player -current player
     * @param gameTable -the game table that stores the placed cards that can be used
     * @param index -back row of the current player
     * @return
     */
    public ObjectNode useHeroAbility(final ActionsInput action, final ObjectNode node,
                                     final Player player,
                                     final ArrayList<ArrayList<Minion>> gameTable,
                                     final int index) {
        node.put("command", action.getCommand());
        int affectedRow = action.getAffectedRow();
        node.put("affectedRow", affectedRow);

        if (player.hero.getMana() > player.mana) {
            node.put("error", "Not enough mana to use hero's ability.");
        } else if (player.hero.attackedAnotherCard) {
            node.put("error", "Hero has already attacked this turn.");
        } else {

            switch (player.hero.getName()) {
                case "Lord Royce":
                    if (index == affectedRow || abs(index - 1) == affectedRow) {
                        node.put("error", "Selected row does not belong to the enemy.");
                    } else {
                        player.hero.lordRoyce(gameTable.get(affectedRow));
                        player.mana -= player.hero.getMana();
                        node.removeAll();
                    }
                    break;

                case "Empress Thorina":
                    if (index == affectedRow || abs(index - 1) == affectedRow) {
                        node.put("error", "Selected row does not belong to the enemy.");
                    } else {
                        player.hero.empressThorina(gameTable.get(affectedRow));
                        player.mana -= player.hero.getMana();
                        node.removeAll();
                    }
                    break;

                case "King Mudface":
                    if (abs(index - 2) == affectedRow || abs(index - PLAYER1ROW) == affectedRow) {
                        node.put("error", "Selected row does not belong to the current player.");
                    } else {
                        player.hero.kingMudface(gameTable.get(affectedRow));
                        player.mana -= player.hero.getMana();
                        node.removeAll();
                    }
                    break;

                default:
                    if (abs(index - 2) == affectedRow || abs(index - PLAYER1ROW) == affectedRow) {
                        node.put("error", "Selected row does not belong to the current player.");
                    } else {
                        player.hero.generalKocioraw(gameTable.get(affectedRow));
                        player.mana -= player.hero.getMana();
                        node.removeAll();
                    }
                    break;
            }
        }
        return node;
    }
}
