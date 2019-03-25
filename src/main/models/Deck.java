package main.models;

import java.util.ArrayList;

public class Deck {

	//Atributos de la clase Deck
	private String deckName;
	private int deckValue;
	private ArrayList<Card> cardsOnDeck;

	//Constructor de la clase Deck
	public Deck(String deckName, int deckValue, ArrayList<Card> cardsOnDeck) {
		super();
		this.deckName = deckName;
		this.deckValue = deckValue;
		this.cardsOnDeck = cardsOnDeck;
	}
	
	//Getters de la clase Deck
	public String getDeckName() {
		return deckName;
	}

	public int getDeckValue() {
		return deckValue;
	}

	public ArrayList<Card> getCardsOnDeck() {
		return cardsOnDeck;
	}

	//Metodo ToString de la clase Deck
	@Override
	public String toString() {
		return "Deck [deckName=" + deckName + ", deckValue=" + deckValue + ", cardsOnDeck=" + cardsOnDeck + "]";
	}

}
