package main.interfaces;

import main.models.Deck;

public interface IDeck {
	
	//Interface Mazo
	
	public Deck obtenerMazo(String nombreMazo);
	
	public void guardarMazo(Deck deck);

}

