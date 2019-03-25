package main.views;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import main.implementations.ExistDAOImplement;
import main.implementations.MongoDAOImplement;
import main.interfaces.ICard;
import main.interfaces.IDeck;
import main.models.Card;
import main.models.Deck;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JScrollPane;

public class Vista {
	
	//Variables de la clase Vista
	private JFrame frame;
	private JTextField txtMazo;
	private JList<Card> listCargarCartas;
	private JList<Card> listBaraja;
	private JButton btnDerecha;
	private JButton btnIzquierda;
	private JButton btnCargarCartas;
	private JButton btnGuardarMazo;
	private JButton btnMazoRandom;
	private JButton btnCargarMazo;
	private JLabel lblColeccion;
	private JLabel lblMazo;
	private DefaultListModel<Card> dlmCargarCartas;
	private DefaultListModel<Card> dlmBaraja;
	private ArrayList<Card> arrayCartas = null;
	private ICard ic;
	private IDeck iDeck;
	private JLabel lblValorTotal;
	private JScrollPane scrollPane;
	private int totalValue = 0;
	private int minValue = 10;
	private int maxValue = 0;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Vista window = new Vista();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public Vista() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1200, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Esta parte del codigo es para poner de fondo la imagen magic_fondo.jpg
		ImageIcon icon = new ImageIcon("src/main/resources" + File.separator + "magic_fondo.jpg");
		final Image image = icon.getImage();
		JPanel contentPane = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
			}
		};

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);

		//Creamos las implementaciones
		ic = new ExistDAOImplement();
		iDeck = new MongoDAOImplement();

		dlmCargarCartas = new DefaultListModel<Card>();

		dlmBaraja = new DefaultListModel<Card>();
		listBaraja = new JList<Card>(dlmBaraja);
		listBaraja.setForeground(Color.WHITE);
		listBaraja.setFont(new Font("Tahoma", Font.BOLD, 11));
		listBaraja.setBackground(Color.DARK_GRAY);

		btnDerecha = new JButton("--->");
		btnDerecha.setFont(new Font("Tahoma", Font.BOLD, 18));

		// En este listener tenemos la logica de pasar cartas de la lista de cartas a la
		// lista de la baraja
		btnDerecha.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Si el valor total mas el siguiente valor es mas pequeño que 20, añadimos el elemento seleccionado
				//Tambien sumamos el valor al valor total y borramos el indice de la primera lista para evitar repeticiones
				if (totalValue + listCargarCartas.getSelectedValue().getValue() <= 20) {
					dlmBaraja.addElement(listCargarCartas.getSelectedValue());
					totalValue = totalValue + listCargarCartas.getSelectedValue().getValue();
					lblValorTotal.setText(String.valueOf(totalValue));
					dlmCargarCartas.remove(listCargarCartas.getSelectedIndex());

				}

			}
		});

		btnIzquierda = new JButton("<---");
		btnIzquierda.setFont(new Font("Tahoma", Font.BOLD, 18));

		// En este listener tenemos la logica de devolver cartas de la lista de la
		// baraja a la lista de las cartas
		btnIzquierda.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlmCargarCartas.addElement(listBaraja.getSelectedValue());
				totalValue = totalValue - listBaraja.getSelectedValue().getValue();
				lblValorTotal.setText(String.valueOf(totalValue));
				dlmBaraja.remove(listBaraja.getSelectedIndex());

			}
		});

		btnCargarCartas = new JButton("Load Cards");
		btnCargarCartas.setFont(new Font("Tahoma", Font.BOLD, 14));

		// En este listener tenemos la logica de cargar las cartas de el arraylist en la
		// lista de las cartas
		btnCargarCartas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlmCargarCartas.removeAllElements();
				dlmBaraja.removeAllElements();
				totalValue = 0;
				lblValorTotal.setText(String.valueOf(totalValue));
				arrayCartas = ic.cargarColeccionCartas();
				btnMazoRandom.setEnabled(true);
				for (int i = 0; i < arrayCartas.size(); i++) {
					dlmCargarCartas.addElement(arrayCartas.get(i));

					if (arrayCartas.get(i).getValue() > maxValue) {
						maxValue = arrayCartas.get(i).getValue();
					}
					if (arrayCartas.get(i).getValue() < minValue) {
						minValue = arrayCartas.get(i).getValue();
					}
				}
				
				JOptionPane.showMessageDialog(null, "Valor Máximo: " + maxValue + " Valor Minimo: " + minValue);

			}
		});

		btnGuardarMazo = new JButton("Save Deck");
		btnGuardarMazo.setFont(new Font("Tahoma", Font.BOLD, 14));

		// En este listener tenemos la logica de crear los parametros de un mazo y
		// guardar ese mazo con una funcion que guarda los
		// datos en una base de datos MongoDB, que tiene una coleccion dentro para
		// almacenar el archivo
		btnGuardarMazo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Card> cartasBaraja = new ArrayList<Card>();
				String nombreMazo = txtMazo.getText();
				int valorTotal = 0;
				for (int i = 0; i < dlmBaraja.getSize(); i++) {
					valorTotal = valorTotal + dlmBaraja.get(i).getValue();
					cartasBaraja.add(dlmBaraja.get(i));
				}

				Deck deck = new Deck(nombreMazo, valorTotal, cartasBaraja);
				
				if(iDeck.obtenerMazo(deck.getDeckName())==null) {
					iDeck.guardarMazo(deck);
					JOptionPane.showMessageDialog(null, "Mazo guardado correctamente");
				}else {
					JOptionPane.showMessageDialog(frame,"Nombre del mazo introducido esta duplicado","Error Guardando Mazo",JOptionPane.ERROR_MESSAGE);
				}
				

			}
		});

		btnMazoRandom = new JButton("Random Deck");
		btnMazoRandom.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnMazoRandom.setEnabled(false);

		//En este listener tenemos la logica de crear un mazo random y mostrarlo en la lista de la baraja
		btnMazoRandom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Borramos todos los elementos del DefaultListModel de la primera lista
				dlmCargarCartas.removeAllElements();
				//Volvemos a cargar las cartas
				arrayCartas = ic.cargarColeccionCartas();
				
				//Las añadimos al DefaultListModel
				for (int i = 0; i < arrayCartas.size(); i++) {
					dlmCargarCartas.addElement(arrayCartas.get(i));
				}
				
				//Borramos todos los elementos del DefaultListModel de la segunda lista 
				//Ponemos el valor total de la baraja a 0 y lo mostramos
				dlmBaraja.removeAllElements();
				totalValue = 0;
				lblValorTotal.setText(String.valueOf(totalValue));
				
				//Recorremos el ArrayList que contiene las cartas, las seleccionamos aleatoriamente
				//y las añadimos a la lista, borrando su hueco en el ArrayList para no repetir cartas
				for (int i = 0; i < arrayCartas.size(); i++) {
					int numeroRandom = (int) (Math.random() * 9) + 1;
					if (numeroRandom > 5) {
						if ((totalValue + arrayCartas.get(i).getValue() <= 20)) {
							totalValue = totalValue + arrayCartas.get(i).getValue();
							lblValorTotal.setText(String.valueOf(totalValue));
							dlmBaraja.addElement(arrayCartas.get(i));
							dlmCargarCartas.removeElement(arrayCartas.get(i));
							arrayCartas.remove(i);
						}
					}
				}

				//Si el valor total es mas pequeño que 19 damos una vuelta mas para seguir añadiendo cartas al mazo
				//El valor minimo de una carta es 2, asi que los resultados del mazo solo pueden ser 19 o 20
				if (totalValue < 19) {
					for (int i = 0; i < arrayCartas.size(); i++) {
						if ((totalValue + arrayCartas.get(i).getValue() <= 20)) {
							totalValue = totalValue + arrayCartas.get(i).getValue();
							lblValorTotal.setText(String.valueOf(totalValue));
							dlmBaraja.addElement(arrayCartas.get(i));
							dlmCargarCartas.removeElement(arrayCartas.get(i));
							arrayCartas.remove(i);
						}
					}
				}

			}
		});

		btnCargarMazo = new JButton("Load Deck");
		btnCargarMazo.setFont(new Font("Tahoma", Font.BOLD, 14));

		
		//En este listener tenemos la logica de cargar un mazo
		btnCargarMazo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				//Creamos un deck a partir del metodo Obtener Mazo
				Deck deck = iDeck.obtenerMazo(txtMazo.getText());
				ArrayList<Card> cartas;

				//Si el deck no esta vacio, limpiamos la segunda lista y el valor total
				if (deck != null) {
					cartas = deck.getCardsOnDeck();
					dlmBaraja.removeAllElements();
					totalValue = 0;
					lblValorTotal.setText(String.valueOf(totalValue));

					//Recorremos el array de cartas creado previamente y añadimos las cartas a la lista
					//Sumamos el valor de las cartas al valor total y lo mostramos
					for (int i = 0; i < cartas.size(); i++) {
						dlmBaraja.addElement(cartas.get(i));
						totalValue = totalValue + cartas.get(i).getValue();
					}

					lblValorTotal.setText(String.valueOf(totalValue));
					
					

				} else {
					JOptionPane.showMessageDialog(frame,"No existe un mazo con el nombre introducido","Error Cargando Mazo",JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		lblColeccion = new JLabel("Cartas de la Coleccion");
		lblColeccion.setFont(new Font("Tahoma", Font.BOLD, 20));

		lblMazo = new JLabel("Cartas para un Mazo");
		lblMazo.setFont(new Font("Tahoma", Font.BOLD, 20));

		txtMazo = new JTextField();
		txtMazo.setColumns(10);

		lblValorTotal = new JLabel("0");
		lblValorTotal.setFont(new Font("Tahoma", Font.PLAIN, 30));

		scrollPane = new JScrollPane();

		// -------------------------------------------------- GROUP LAYOUT ----------------------------------------------------------------------------------------
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
				.createSequentialGroup().addGap(60)
				.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(lblColeccion, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnCargarCartas, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE))
				.addGap(126)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addGap(62).addComponent(lblMazo,
								GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup().addGap(35)
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(btnGuardarMazo, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 384,
												Short.MAX_VALUE)
										.addComponent(listBaraja, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 384,
												Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(btnCargarMazo, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
										.addComponent(txtMazo, 174, 174, 174)
										.addComponent(btnMazoRandom, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))))
				.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup().addContainerGap(462, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnIzquierda, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnDerecha, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 120,
										Short.MAX_VALUE))
						.addGap(592))
				.addGroup(Alignment.LEADING,
						groupLayout.createSequentialGroup().addGap(495)
								.addComponent(lblValorTotal, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(626, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
				.createSequentialGroup().addContainerGap().addComponent(lblValorTotal).addGap(8)
				.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(lblColeccion)
						.addComponent(lblMazo))
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addGap(47)
								.addComponent(btnDerecha, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addGap(37)
								.addComponent(btnIzquierda, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup().addGap(20)
								.addComponent(txtMazo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addGap(18).addComponent(btnCargarMazo).addGap(121).addComponent(btnMazoRandom))
						.addGroup(groupLayout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 234,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(listBaraja, GroupLayout.PREFERRED_SIZE, 246,
												GroupLayout.PREFERRED_SIZE))))
				.addGap(16)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnCargarCartas, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnGuardarMazo, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(70, Short.MAX_VALUE)));
		listCargarCartas = new JList<Card>(dlmCargarCartas);
		scrollPane.setViewportView(listCargarCartas);
		listCargarCartas.setForeground(Color.WHITE);
		listCargarCartas.setFont(new Font("Tahoma", Font.BOLD, 11));
		listCargarCartas.setBackground(Color.DARK_GRAY);
		frame.getContentPane().setLayout(groupLayout);
		// -------------------------------------------------- GROUP LAYOUT ----------------------------------------------------------------------------------------
	}
}
