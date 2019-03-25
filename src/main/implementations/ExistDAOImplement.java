package main.implementations;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import com.google.gson.Gson;

import main.interfaces.ICard;
import main.models.Card;

public class ExistDAOImplement implements ICard {

	// Atributos de la clase ExistDAOImplement
	private final String URI = "xmldb:exist://localhost:8888/exist/xmlrpc/db/";
	private final String driver = "org.exist.xmldb.DatabaseImpl";
	private Class c1;
	private Database database;
	private Collection col = null;
	private XMLResource res = null;
	private String colName = "Cards";
	private String resName = "Cards.xml";
	private ArrayList<Card> coleccionCartas;

	// Metodo para obtener toda la coleccion de cartas en un ArrayList
	// Este metodo accede a existDB, buscando en una coleccion que he creado el archivo XML correspondiente a las cartas
	public ArrayList<Card> cargarColeccionCartas() {

		try {
			//Instanciamos la base de datos, obtenemos la coleccion y dentro obtenemos el nombre del archivo
			c1 = Class.forName(driver);
			database = (Database) c1.newInstance();
			database.setProperty("create-database", "true");
			DatabaseManager.registerDatabase(database);
			col = DatabaseManager.getCollection(URI + colName);
			res = (XMLResource) col.getResource(resName);

			//Si el archivo esta vacio saltará un error
			if (res == null) {
				System.err.println("¡Documento XML no encontrado!");
			} else {
				//Si el archivo contiene datos pasaremos las cardas que estan en XML a formato JSON 
				coleccionCartas = new ArrayList<Card>();
				res = (XMLResource) col.getResource(resName);
				JSONObject xtjObject = XML.toJSONObject((String) res.getContent());
				JSONArray arrayCartas = xtjObject.getJSONObject("cards").getJSONArray("card");

				//Finalmente con este bucle llenaremos el ArrayList con todas las cartas obtenidas en JSON
				for (int i = 0; i < arrayCartas.length(); i++) {
					Card card = new Gson().fromJson(arrayCartas.get(i).toString(), Card.class);
					coleccionCartas.add(card);
				}

			}

		} catch (ClassNotFoundException | XMLDBException | InstantiationException | IllegalAccessException
				| JSONException e) {
			e.printStackTrace();
		}

		//Devolvemos el ArrayList con todas las cartas cargadas
		return coleccionCartas;
	}

}
