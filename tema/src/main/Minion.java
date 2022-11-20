package main;

import fileio.CardInput;

import java.util.ArrayList;

public class Minion extends CardInput {
    Boolean stunnedMinion = false;
    Boolean attackedAnotherCard = false;

    public Minion() {}
    public Minion(CardInput card) {
        this.name = card.getName();
        this.attackDamage = card.getAttackDamage();
        this.colors = card.getColors();
        this.mana = card.getMana();
        this.description = card.getDescription();
        this.health = card.getHealth();
    }

    public boolean gotAttacked (Minion attacker, ArrayList<Minion> rowCards, int index) {
        health -= attacker.getAttackDamage();
        attacker.setAttackedAnotherCard(true);
        if (health <= 0) {
            rowCards.remove(index);
            return true;
        }
        return false;
    }

    public void setAttackedAnotherCard(Boolean attackedAnotherCard) {
        this.attackedAnotherCard = attackedAnotherCard;
    }

    public void theRipper (Minion attacker) {
        attackDamage -= 2;
        if (attackDamage < 0)
            attackDamage = 0;
        attacker.setAttackedAnotherCard(true);
    }

    public void miraj (Minion attacker) {
        int copyHealth = health;
        health = attacker.getHealth();
        attacker.setHealth(copyHealth);
        attacker.setAttackedAnotherCard(true);
    }

    public Boolean theCursedOne (Minion attacker) {
        int copyElem = health;
        health = attackDamage;
        attackDamage = copyElem;
        attacker.setAttackedAnotherCard(true);
        if (health == 0) {
            return true;
        }
        return false;
    }

    public void disciple () {
        health += 2;
        this.setAttackedAnotherCard(true);
    }
}
