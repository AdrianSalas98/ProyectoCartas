package main.implementations;

import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import main.interfaces.IDeck;
import main.models.Deck;

public class MongoDAOImplement implements IDeck {

	//Atributos de la clase MongoDAOImplement
	private MongoClientURI connector;
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> coleccion;

	//Metodo para obtener un mazo, le pasamos como parametro el nombre del mazo a buscar
	@Override
	public Deck obtenerMazo(String nombreMazo) {
		//Con el connector accedemos al MongoDB con la ip y el puerto
		//Creamos el MongoClient con el connector
		//Creamos la database con el nombre de la base de datos(si no existe la crea)
		//Creamos la coleccion con el nombre de la coleccion que tenemos(si no existe la crea)
		connector = new MongoClientURI("mongodb://localhost:27017");
		mongoClient = new MongoClient(connector);
		database = mongoClient.getDatabase("magicDecks");
		coleccion = database.getCollection("myDecks");
		Deck deck = null;

		//Con el MongoCursor recorremos la coleccion, filtrando por el nombre del mazo, si hay alguno con nombre coincidente
		MongoCursor<Document> cursor = coleccion.find(Filters.eq("deckName", nombreMazo)).iterator();

		
		try {
			//Creamos el deck con el formato JSON indicandole que es de la clase deck
			Document document = cursor.next();
			deck = new Gson().fromJson(document.toJson(), Deck.class);
		} catch (Exception e) {

		}

		//Cerramos los elementos de conexion a MongoDB
		mongoClient.close();
		connector = null;
		coleccion = null;
		database = null;

		//Devolvemos el deck con el nombre coincidente
		return deck;
	}

	//Metodo para guardar el mazo creado, le pasamos por parametro el mazo a guardar
	@Override
	public void guardarMazo(Deck deck) {
		//Con el connector accedemos al MongoDB con la ip y el puerto
		//Creamos el MongoClient con el connector
		//Creamos la database con el nombre de la base de datos(si no existe la crea)
		//Creamos la coleccion con el nombre de la coleccion que tenemos(si no existe la crea)
		connector = new MongoClientURI("mongodb://localhost:27017");
		mongoClient = new MongoClient(connector);
		database = mongoClient.getDatabase("magicDecks");
		coleccion = database.getCollection("myDecks");
		
		//Creamos un ArrayList de objetos(BasicDBList)
		ArrayList<Object> cartasMazo = new BasicDBList();

		//Creamos un documento y le ponemos el nombre del deck y el valor total del deck
		Document doc = new Document();
		doc.put("deckName", deck.getDeckName());
		doc.put("deckValue", deck.getDeckValue());

		//Ahora para saber las cartas del mazo, recorremos el mazo y ponemos en el documento el atributo de cada carta
		for (int i = 0; i < deck.getCardsOnDeck().size(); i++) {
			DBObject object = new BasicDBObject();
			object.put("id", deck.getCardsOnDeck().get(i).getCardId());
			object.put("name", deck.getCardsOnDeck().get(i).getName());
			object.put("summonCost", deck.getCardsOnDeck().get(i).getSummonCost());
			object.put("attack", deck.getCardsOnDeck().get(i).getAttack());
			object.put("defense", deck.getCardsOnDeck().get(i).getDefense());
			object.put("value", deck.getCardsOnDeck().get(i).getValue());

			//Añadimos el BasicDBObject al ArrayList de objetos que hemos creado anteriormente
			cartasMazo.add(object);

		}

		//Ponemos las cartas del mazo en el documento e insertamos todo en la coleccion
		doc.put("cardsOnDeck", cartasMazo);
		coleccion.insertOne(doc);

		//Cerramos los elementos de conexion a MongoDB
		mongoClient.close();
		connector = null;
		coleccion = null;
		database = null;
	}

}
