package main;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fileio.CardInput;

import java.util.ArrayList;

@JsonIgnoreProperties({"attackDamage"})

public class Hero extends CardInput{
    Boolean attackedAnotherCard = false;

    public Hero(CardInput card) {
        this.name = card.getName();
        this.colors = card.getColors();
        this.mana = card.getMana();
        this.description = card.getDescription();
        this.health = card.getHealth();
    }

    public void attackHero(Minion attacker) {
        health -= attacker.getAttackDamage();
        attacker.setAttackedAnotherCard(true);
    }

    public void lordRoyce (ArrayList<Minion> rowCards) {
        int attackDamageY = 0;
        for(int i = 0; i < rowCards.size(); i++) {
            if (attackDamageY < rowCards.get(i).getAttackDamage()) {
                attackDamageY = i;
            }
        }
        rowCards.get(attackDamageY).stunnedMinion = true;
        attackedAnotherCard = true;
    }

    public void empressThorina (ArrayList<Minion> rowCards) {
        int healthY = 0;
        for(int i = 0; i < rowCards.size(); i++) {
            if (healthY < rowCards.get(i).getHealth()) {
                healthY = i;
            }
        }
        rowCards.remove(healthY);
        attackedAnotherCard = true;
    }

    public void kingMudface (ArrayList<Minion> rowCards) {
        for(int i = 0; i < rowCards.size(); i++) {
            int health = rowCards.get(i).getHealth();
            rowCards.get(i).setHealth(health + 1);
        }
        attackedAnotherCard = true;
    }

    public void generalKocioraw (ArrayList<Minion> rowCards) {
        for(int i = 0; i < rowCards.size(); i++) {
            int attackDamage = rowCards.get(i).getAttackDamage();
            rowCards.get(i).setAttackDamage(attackDamage + 1);
        }
        attackedAnotherCard = true;
    }
}
