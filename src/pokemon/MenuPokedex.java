package pokemon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Controla todas las acciones de la poekedex a traves de dos menus.
 * 
 * @author jesus
 *
 */

public class MenuPokedex {

	private static final byte OP_SALIRM1 = 4;
	private static final byte OP_SALIRM2 = 7;

	public static void main(String[] args) {

		// Crear sistema de directorios que usaremos. Si se cambia el sistema de
		// carpetas debe reflejarse en los path de pokedex.java
		// y MenuPrincipal.java
		File directorioBin = new File("./Pokedex/PokedexsBin/");
		File directorioObj = new File("./Pokedex/ObjFiles/");

		// Se crean solo si no existen
		if (!directorioBin.exists())
			directorioBin.mkdirs();
		if (!directorioObj.exists())
			directorioObj.mkdirs();

		Scanner teclado = new Scanner(System.in);

		byte opcionM1 = 0;
		Pokedex pokedex = null;
		do {
			try {

				System.out.println();
				System.out.println("1. Seleccionar Pokedex");
				System.out.println("2. Crear Pokedex");
				System.out.println("3. Eliminar Pokedex");
				System.out.println("4. SALIR");

				opcionM1 = Byte.parseByte(teclado.nextLine());
//--------------
				if (opcionM1 == 1) { // SELECCIONAR POKEDEX

					System.out.println("------SELECCIONA UNA POKEDEX------");
					mostrarPokedexs();
					pokedex = AuxPokedex.getPokedexs().get(Integer.parseInt(teclado.nextLine()) - 1);

					byte opcionM2 = 0;
					do {

						System.out.println();
						System.out.println("1. Mostrar pokedex");
						System.out.println("2. Aniadir pokemon");
						System.out.println("3. Liberar pokemon");
						System.out.println("4. Mover pokemon");
						System.out.println("5. Ocupacion");
						System.out.println("6. Mover pokemon a otra pokedex");
						System.out.println("7. SALIR");

						opcionM2 = Byte.parseByte(teclado.nextLine());

						switch (opcionM2) {

						case 1: // MOSTRAR POKEDEX

							pokedex.mostrarPokedex();
							break;

						case 2: // AÑADIR POKEMON

							System.out.println("Posicion? [1-" + pokedex.getMaxPokemons() + "]");
							int posicion = Integer.parseInt(teclado.nextLine());

							System.out.println("Numero de especie? [1-151]");
							int numeroEspecie = Integer.parseInt(teclado.nextLine());

							String respuesta = "";
							System.out.println("Tiene nombre? (s/N)");
							String nombre = "";
							respuesta = teclado.nextLine().trim().toLowerCase();
							if (respuesta.length() > 0 && respuesta.charAt(0) == 's') {
								System.out.println("Nombre:");
								nombre = teclado.nextLine();
							}

							respuesta = "";
							System.out.println("Es shiny? (s/N)");
							respuesta = teclado.nextLine().trim().toLowerCase();
							boolean shiny = respuesta.length() > 0 && respuesta.charAt(0) == 's';

							pokedex.aniadirPokemon(posicion, numeroEspecie, nombre, shiny);
							break;

						case 3: // LIBERAR POKEMON

							System.out.println("Posicion del pokemon");
							int posicionLiberar = Integer.parseInt(teclado.nextLine());
							pokedex.liberarPokemon(posicionLiberar);
							break;

						case 4: // MOVER POKEMON
							
							pokedex.mostrarPokedex();
							System.out.println("Posicion del Pokemon a mover");
							int pos1 = Integer.parseInt(teclado.nextLine());
							System.out.println("Posicion de DESTINO");
							int pos2 = Integer.parseInt(teclado.nextLine());

							// Mover pokemon. Leer ambos registros y escribirlos cruzados
							Byte[] registro1 = pokedex.extraerRegistro(pos1);
							Byte[] registro2 = pokedex.extraerRegistro(pos2);
							pokedex.insertarRegistro(pos1, registro2);
							pokedex.insertarRegistro(pos2, registro1);
							break;

						case 5: // MOSTRAR OCUPACION

							System.out.println(pokedex.getOcupacion() + "/" + pokedex.getMaxPokemons());
							break;

						case 6: // MOVER DE POKEDEX
							
							pokedex.mostrarPokedex();
							System.out.println("Pokemon a mover (posicion): [1-" + pokedex.getMaxPokemons() + "]");
							int posMover = Integer.parseInt(teclado.nextLine());
							Byte[] pokemonPk1 = pokedex.extraerRegistro(posMover);

							System.out.println("Elige una pokedex a la que moverlo");
							mostrarPokedexs();
							Pokedex pokedexDestino = AuxPokedex.getPokedexs()
									.get(Integer.parseInt(teclado.nextLine()) - 1);
							
							System.out.println("----------" + pokedexDestino.getNombrePokedex() + "----------");
							pokedexDestino.mostrarPokedex();
							System.out.println("En que posicion deseas colocarlo? (Si está ocupada se sobreescribira)");
							pokedexDestino.insertarRegistro(Integer.parseInt(teclado.nextLine()), pokemonPk1);
							break;
						}

					} while (opcionM2 != OP_SALIRM2);
//-------------
				} else if (opcionM1 == 2) { // CREAR POKEDEX

					System.out.println("Nombre de la nueva Pokedex");
					String nombre = teclado.nextLine().trim();

					System.out.println("Numero maximo de pokemons (capacidad maxima)");
					int capMax = Integer.parseInt(teclado.nextLine());

					AuxPokedex.crearPokedex(nombre, capMax);
//-------------
				} else if (opcionM1 == 3) {

					System.out.println("Que pokedex quieres eliminar?");
					mostrarPokedexs();
					Pokedex pkEliminada = AuxPokedex.eliminarPokedex(Integer.parseInt(teclado.nextLine()) - 1);
					if(pkEliminada != null) System.out.println("\n" + pkEliminada.getNombrePokedex() + " Eliminada!\n");
				}

			} catch (NumberFormatException e) {
				System.out.println("Debes introducir un valor numerico");

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		} while (opcionM1 != OP_SALIRM1);
		
		teclado.close();
	}

	// Muestra las pks disponibles
	public static void mostrarPokedexs() throws FileNotFoundException, ClassNotFoundException, IOException {

		ArrayList<Pokedex> pokedexs = AuxPokedex.getPokedexs();

		// Mostrar las pk obtenidas
		for (int i = 0; i < pokedexs.size(); i++) {
			System.out.println((i + 1) + ". " + pokedexs.get(i).getNombrePokedex());
		}
	}

}
