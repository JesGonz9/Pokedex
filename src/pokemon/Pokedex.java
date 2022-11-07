package pokemon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase de creacion y manejo de una pokedex. Cuando se llama al constructor, éste crea una nueva pokedex en forma de fichero binario
 * con tantos ceros como extensión maxima hayamos indicado.
 * @author jesus
 *
 */
 

public class Pokedex implements Serializable {
	
	//Atributos
	private static final long serialVersionUID = 1L;
	private static final String PATH_POKEDEX_BIN = "./Pokedex/PokedexsBin/";
	private ArrayList<String> especies = new ArrayList<>();
	private int maxPokemons = 0;
	private String nombrePokedex = "";
	private File pokedexBin = null; //fichero.bin asociado a la clase
	
	//Estructura del fichero de la pokedex en bytes por campo de cada registro
	private final byte ID_ESPECIE = 4;
	private final byte BOOL_NOMBRE = 1;
	private final byte UTF_HEAD = 2;
	private final byte CHARS_NOMBRE = 10;
	private final byte BOOL_SHINY = 1;
	
	/**
	 * Crea un objeto pokedex y un fichero.bin con el nombre que le hayamos pasado
	 * @param nombrePokedex
	 * @param maxPokemons
	 * @param ficheroEspecies
	 * @throws Exception
	 */
	public Pokedex(String nombrePokedex, int maxPokemons, File ficheroEspecies) throws Exception {
		this.maxPokemons = maxPokemons;
		this.nombrePokedex = nombrePokedex;
		this.pokedexBin = this.createPokedex(this.nombrePokedex);
		this.uploadEspecies(ficheroEspecies);
	}
	
//--METODOS PRIVADOS
	
	/**
	 * Devuelve los bytes que ocupa un registro
	 * @return 
	 */
	private int bytesPorRegistro() {
		return ID_ESPECIE + BOOL_NOMBRE + UTF_HEAD + CHARS_NOMBRE + BOOL_SHINY;
	}
	
	//Devuelve true si el espacio ya esta ocupado, false si no
	private boolean isOcupado(int posicion) throws Exception {
		
		RandomAccessFile raf = new RandomAccessFile(this.pokedexBin, "r");
		
		//Colocar el raf en la posicion indicada
		raf.seek(this.getBytesPosition(posicion));
				
		//Si ya está ocupada, false
		if(raf.readInt() > 0) {
			raf.close();
			return true;
		}
		
		raf.close();
		return false;
	}
	
	//Devuelve la capacidad maxima en bytes que puede tener el fichero
	private int capMaxFichero() {
		return bytesPorRegistro() * this.maxPokemons;
	}
	
	//Carga las especies del fichero especies en memoria
	private void uploadEspecies(File ficheroEspecies) throws IOException {

		BufferedReader bfr = new BufferedReader(new FileReader(ficheroEspecies));
		
		String especie = bfr.readLine();
		while(especie != null) {
			especies.add(especie);
			especie = bfr.readLine();
		}
		
		bfr.close();
	}
	
	//Crea una nueva pokedex rellenandola con ceros hasta su capacidad maxima. Devuelve el fichero creado
	private File createPokedex(String nombrePokedex) throws Exception {
		
		//Crear el fichero binario si no existe todavia.
		File ficheroPokedex = new File(PATH_POKEDEX_BIN + nombrePokedex + ".bin");
		if(ficheroPokedex.exists()) throw new Exception("Ya existe una pokedex con ese nombre");
		
		RandomAccessFile raf = new RandomAccessFile(ficheroPokedex, "rw");
			
			for(int i = 0; i < capMaxFichero(); i++) {
				raf.writeByte(0);
			}
			
			raf.close();
			return ficheroPokedex;
	}
	
	//Dar formato al nombre para adecuarse a la medida requerida
	private String formatearNombre(String nombre) {
		
		if(nombre.length() > this.CHARS_NOMBRE) return nombre = nombre.substring(0, this.CHARS_NOMBRE);
		
		if(nombre.length() < this.CHARS_NOMBRE) {
			for(int i = nombre.length(); i < this.CHARS_NOMBRE; i++) {
				nombre += " ";
			}
			return nombre;
		}
		
		return nombre;
	}
	
	//Devuelve la posicion del registro en bytes
	private long getBytesPosition(int posicion) {
		return (posicion-1) * bytesPorRegistro();
	}
	
	
//--METODOS PUBLICOS
	
	//Añade un pokemon en la posicion dada unicamente si ésta está libre
	public boolean aniadirPokemon(int posicion, int idEspecie, String nombre, boolean shiny) throws Exception {
		
		if(this.isOcupado(posicion)) throw new Exception("Esta posicion ya está ocupada");
		
		long posicionNuevoPokemon = this.getBytesPosition(posicion);
		RandomAccessFile raf = new RandomAccessFile(this.pokedexBin, "rw");
		
		//Colocar el raf en el sitio 
		raf.seek(posicionNuevoPokemon);
		
		//Comprobar si hay nombre y darle formato. Si no tiene nombre, se escribiran espacios en blanco
		boolean hayNombre = nombre.length() > 0;
		nombre = formatearNombre(nombre);
		
		//Escribir los campos
		raf.writeInt(idEspecie);
		raf.writeBoolean(hayNombre);
		raf.writeUTF(nombre);
		raf.writeBoolean(shiny);
		
		raf.close();
		System.out.println("Aniadido!");
		return true;
	}
	
	//Libera un pokemon poniendo su ID de especie a 0 pudiendo ser sobreescrito
	public boolean liberarPokemon(int posicion) throws IOException {
		
		RandomAccessFile raf = new RandomAccessFile(this.pokedexBin, "rw");
		
		//Colocar el raf en la posicion indicada
		raf.seek(this.getBytesPosition(posicion));
		raf.writeInt(0);
		
		raf.close();
		return true;
	}
	
	//Muestra los nombres de los pokemons o vacio si no hay. Devuelve la cantidad de items mostrados
	public void mostrarPokedex() throws IOException {
		
		RandomAccessFile raf = new RandomAccessFile(this.pokedexBin, "r");
		
		int registro = 0; //Recorrer el fichero binario de la pokedex
		while (raf.getFilePointer() < this.pokedexBin.length()) {
			
			int idEspecie = raf.readInt();
			
			if(idEspecie == 0) {
				System.out.printf("%-4d VACIO \n", registro+1);
				raf.seek(this.bytesPorRegistro() * (registro+1)); //Colocar el raf al siguiente registro y continue
				
				registro++;
				continue;
			}
			
			boolean tieneNombre = raf.readBoolean();
			String nombre = raf.readUTF();
			boolean isShiny = raf.readBoolean();
			
			//Imprimir datos por pantalla
			String especie = this.especies.get(idEspecie-1);
			System.out.printf("%-4d %s %s %s \n",
										registro+1,
										especie, 
										tieneNombre? String.format("(%s)", nombre.trim()):"",
										isShiny? "*":"");
			registro++;
		}
		
		raf.close();
	}
	
	//Extrae el registro indicado, marcandolo para poder ser sobreescrito
	public Byte[] extraerRegistro(int posicion) throws IOException {
		
		RandomAccessFile raf = new RandomAccessFile(this.pokedexBin, "rw");
		
		Byte[] bytesRegistro = new Byte[this.bytesPorRegistro()];
		//Leer todo lo que hay en la posicion de ORIGEN
		raf.seek(this.getBytesPosition(posicion));
				
		for(int i = 0; i < bytesRegistro.length; i++) {
			bytesRegistro[i] = raf.readByte();
		}
		
		//Marcar la posicion como vacia
		raf.seek(this.getBytesPosition(posicion));
		raf.writeInt(0);
		raf.close();
		return bytesRegistro;
	}
	
	//Inserta el registro que le pasamos en forma de array de bytes
	public void insertarRegistro(int posicion, Byte[] registro) throws IOException {
		
		RandomAccessFile raf = new RandomAccessFile(this.pokedexBin, "rw");
		
		//Escribir lo que hay en el array
		raf.seek(this.getBytesPosition(posicion));
				
		for(int i = 0; i < registro.length; i++) {
			raf.writeByte(registro[i]);
		}
		raf.close();
	}
	
	//Calcula la ocupacion en funcion de si el Id de especies es o no > 0
	public int getOcupacion() throws IOException {
		RandomAccessFile raf = new RandomAccessFile(this.pokedexBin, "r");
		
		int ocupacion = 0;
		while(raf.getFilePointer() < this.pokedexBin.length()) {
			if(raf.readInt() > 0) ocupacion++;
			
			raf.seek(raf.getFilePointer() + this.bytesPorRegistro() - this.ID_ESPECIE);
		}
		
		raf.close();
		return ocupacion;
	}

//--Getters y setters
	
	public void setNombrePokedex(String nombrePokedex) {
		this.nombrePokedex = nombrePokedex;
	}

	public void setEspecies(ArrayList<String> especies) {
		this.especies = especies;
	}

	public void setMaxPokemons(int maxPokemons) {
		this.maxPokemons = maxPokemons;
	}

	public String getNombrePokedex() {
		return nombrePokedex;
	}
	
	//--Getters y setters
	
	public int getMaxPokemons() {
		return maxPokemons;
	}
	
	public File getPokedexBin() {
		return this.pokedexBin;
	}

}

/**TODO (bugs)
 * Si mueves un pokemon de otra pokedex a una posicion superior a las disponibes, se crean las posiciones que haya de diferencia
 * y se añade sin problema. Si colocas el raf con seek en una posicion superior a la longitud de fichero, ésta aumenta.
 * Si intentas hacerlo dentro de la misma pk, indica un error y se pierde el pokemon.
 */
