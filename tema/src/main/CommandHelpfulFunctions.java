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
    int fullRow = 5;

    /**
     *
     * @param card is the card on table that needs to be verified if it is from the back line
     * @return
     */
    public boolean isBackMinion(final CardInput card) {
        return card.getName().equals("Sentinel")
                || card.getName().equals("Berserker")
                || card.getName().equals("The Cursed One")
                || card.getName().equals("Disciple");
    }

    /**
     *
     * @param card
     * @param mana
     * @param lineCard
     * @return
     */
    public String addNode(final CardInput card, final int mana,
                          final ArrayList<Minion> lineCard) {
        if (card.getHealth() == 0) {
            return "Cannot place environment card on table.";
        } else if (lineCard.size() == fullRow) {
            return "Cannot place card on table since row is full.";
        } else if (mana < card.getMana()) {
            return "Not enough mana to place card on table.";
        }
        return "";
    }

    /**
     *
     * @param action
     * @param player
     * @param index
     * @param node
     * @param gameTable
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
     * @param action
     * @param player1
     * @param player2
     * @param switchPlayer
     * @param node
     * @param gameTable
     * @return
     */
    public ObjectNode placeCard(final ActionsInput action, final Player player1,
                                final Player player2, final int switchPlayer,
                                final ObjectNode node,
                                final ArrayList<ArrayList<Minion>> gameTable) {
        if (switchPlayer == 1) {
            this.verifyConditionsCard(action, player1, 3, node, gameTable);
        } else {
            this.verifyConditionsCard(action, player2, 0, node, gameTable);
        }
        return node;
    }

    /**
     *
     * @param index
     * @param gameTable
     * @param player
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
     * @param player
     */
    public void drawCard(final Player player) {
        if (!player.deck.isEmpty()) {
            player.handCards.add(player.deck.get(0));
            player.deck.remove(0);
        }
    }

    /**
     *
     * @param action
     * @param node
     * @param player
     * @param gameTable
     * @param playerRow
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
                            if (gameTable.get(3 - index).size() == 5) {
                                node.put("error", "Cannot steal enemy card since the player's row is full.");
                            } else {
                                envCard.heartHound(action.getHandIdx(), gameTable, index);
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
     * @param action
     * @param node
     * @param player1
     * @param player2
     * @param switchPlayer
     * @param gameTable
     * @return
     */
    public ObjectNode useEnvironmentCard(final ActionsInput action, final ObjectNode node,
                                         final Player player1,
                                         final Player player2, final int switchPlayer,
                                         final ArrayList<ArrayList<Minion>> gameTable) {
        if (switchPlayer == 1)
            verifyConditionsEnvironment(action, node, player1, gameTable, 3);
        else
            verifyConditionsEnvironment(action, node, player2, gameTable, 0);
        return node;
    }

    public ArrayNode getFrozenCards(final ArrayList<ArrayList<Minion>> gameTable, final ArrayNode arrayNode) {
        for (int i = 0; i < gameTable.size(); i++) {
            for (int j = 0; j < gameTable.get(i).size(); j++) {
                if (gameTable.get(i).get(j).stunnedMinion) {
                    arrayNode.addPOJO(new Minion(gameTable.get(i).get(j)));
                }
            }
        }
        return arrayNode;
    }

    public Boolean isTankOrTableWithoutTank (final ArrayList<Minion> rowCards, final int y) {
        int foundOne = 0;
        for (int i = 0; i < rowCards.size(); i++) {
            if (rowCards.get(i).getName().equals("Goliath") || rowCards.get(i).getName().equals("Warden")) {
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

    public int verifyAttackConditions (final Minion attackerCard, final ActionsInput action,
                                       final ObjectNode node, final int index) {
        node.put("command", action.getCommand());
        node.putPOJO("cardAttacker", new Coordinates(action.getCardAttacker()));
        node.putPOJO("cardAttacked", new Coordinates(action.getCardAttacked()));

        if (index == action.getCardAttacked().getX() || abs(index - 1) == action.getCardAttacked().getX()) {
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

    public ObjectNode cardAttack (final ActionsInput action, final ObjectNode node,
                                  final ArrayList<ArrayList<Minion>> gameTable, final int index) {
        Minion attackedCard = gameTable.get(action.getCardAttacked().getX()).get(action.getCardAttacked().getY());
        Minion attackerCard = gameTable.get(action.getCardAttacker().getX()).get(action.getCardAttacker().getY());

        int verify = verifyAttackConditions(attackerCard, action, node, index);
        if (verify == 1) {
            if (action.getCardAttacked().getX() % 3 != 0) {
                if (!isTankOrTableWithoutTank(gameTable.get(abs(index - 2)),
                        action.getCardAttacked().getY())) {
                    node.put("error", "Attacked card is not of type 'Tank'.");
                } else {
                    attackedCard.gotAttacked(attackerCard,
                            gameTable.get(action.getCardAttacked().getX()), action.getCardAttacked().getY());
                    node.removeAll();
                }
            } else {
                if (!isTankOrTableWithoutTank(gameTable.get(abs(index - 2)),
                        -1)) {
                    node.put("error", "Attacked card is not of type 'Tank'.");
                } else {
                    attackedCard.gotAttacked(attackerCard,
                            gameTable.get(action.getCardAttacked().getX()), action.getCardAttacked().getY());
                    node.removeAll();
                }
            }
        }
        return node;
    }

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
                node.putPOJO("output", new Minion(gameTable.get(action.getX()).get(action.getY())));
            }
        }
        return node;
    }

    public void switchCaseAbility (final Minion attackedCard, final Minion attackerCard,
                                   final ActionsInput action, final ArrayList<ArrayList<Minion>> gameTable) {
        switch (attackerCard.getName()) {
            case "The Ripper":
                attackedCard.theRipper(attackerCard);
                break;

            case "Miraj":
                attackedCard.miraj(attackerCard);
                break;

            case "The Cursed One":
                Boolean isTrue = attackedCard.theCursedOne(attackerCard);
                if (isTrue) {
                    gameTable.get(action.getCardAttacked().getX()).remove(action.getCardAttacked().getY());
                }
                break;
        }
    }

    public ObjectNode cardUsesAbility(final ActionsInput action, final ObjectNode node,
                                      final ArrayList<ArrayList<Minion>> gameTable, final int index) {
        node.put("command", action.getCommand());
        node.putPOJO("cardAttacker", new Coordinates(action.getCardAttacker()));
        node.putPOJO("cardAttacked", new Coordinates(action.getCardAttacked()));

        Minion attackedCard = gameTable.get(action.getCardAttacked().getX()).get(action.getCardAttacked().getY());
        Minion attackerCard = gameTable.get(action.getCardAttacker().getX()).get(action.getCardAttacker().getY());

        if (attackerCard.getName().equals("Disciple")) {
            if (index != action.getCardAttacked().getX()
                    && abs(index - 1) != action.getCardAttacked().getX()) {
                node.put("error", "Attacked card does not belong to the current player.");
            } else {
                attackedCard.disciple();
                node.removeAll();
            }
        } else {
            int verify = verifyAttackConditions(attackerCard, action, node, index);
            if (verify == 1) {
                if (action.getCardAttacked().getX() % 3 != 0) {
                    if (!isTankOrTableWithoutTank(gameTable.get(abs(index - 2)),
                            action.getCardAttacked().getY())) {
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

    public ObjectNode useAttackHero (final ActionsInput action, final ObjectNode node, final Player player,
                                     final int index, final ArrayList<ArrayList<Minion>> gameTable) {
        node.put("command", action.getCommand());
        node.putPOJO("cardAttacker", new Coordinates(action.getCardAttacker()));

        Minion attackerCard = gameTable.get(action.getCardAttacker().getX()).get(action.getCardAttacker().getY());

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

    public ObjectNode useHeroAbility (final ActionsInput action, final ObjectNode node, Player player,
                                      final ArrayList<ArrayList<Minion>> gameTable, final int index) {
        node.put("command", action.getCommand());
        int affectedRow = action.getAffectedRow();
        node.put("affectedRow", affectedRow);

        if (player.hero.getMana() > player.mana) {
            node.put("error", "Not enough mana to use hero's ability.");
        } else if (player.hero.attackedAnotherCard) {
            node.put("error", "Hero has already attacked this turn.");
        } else {

            switch (player.hero.getName()) {
                case "Lord Royce": //stun card cel mai mare damage
                    if (index == affectedRow || abs(index - 1) == affectedRow) {
                        node.put("error", "Selected row does not belong to the enemy.");
                    } else {
                        player.hero.lordRoyce(gameTable.get(affectedRow));
                        player.mana -= player.hero.getMana();
                        node.removeAll();
                    }
                    break;

                case "Empress Thorina": //distruge carte cu cea mai mare viata
                    if (index == affectedRow || abs(index - 1) == affectedRow) {
                        node.put("error", "Selected row does not belong to the enemy.");
                    } else {
                        player.hero.empressThorina(gameTable.get(affectedRow));
                        player.mana -= player.hero.getMana();
                        node.removeAll();
                    }
                    break;

                case "King Mudface": //+1 viata pe tot randul
                    if (abs(index - 2) == affectedRow || abs(index - 3) == affectedRow) {
                        node.put("error", "Selected row does not belong to the current player.");
                    } else {
                        player.hero.kingMudface(gameTable.get(affectedRow));
                        player.mana -= player.hero.getMana();
                        node.removeAll();
                    }
                    break;

                case "General Kocioraw": //+1 attack toti pe rand
                    if (abs(index - 2) == affectedRow || abs(index - 3) == affectedRow) {
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
