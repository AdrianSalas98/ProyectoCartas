package main.models;

public class Card {

	//Variables de la clase carta
	private int id;
	private String name;
	private int summonCost;
	private int attack;
	private int defense;
	private int value;

	//Constructor de la clase carta
	public Card(int id, String name, int summonCost, int attack, int defense, int value) {
		this.id = id;
		this.name = name;
		this.summonCost = summonCost;
		this.attack = attack;
		this.defense = defense;
		this.value = value;
	}

	//Getters de la clase carta
	public int getCardId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getSummonCost() {
		return summonCost;
	}

	public int getAttack() {
		return attack;
	}

	public int getDefense() {
		return defense;
	}

	public int getValue() {
		return value;
	}

	//Metodo ToString de la clase carta
	@Override
	public String toString() {
		return name +  " - Ataque: " + attack + " - Defensa: " + defense + " - Valor: " + value;
	}

	

}
