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
}
