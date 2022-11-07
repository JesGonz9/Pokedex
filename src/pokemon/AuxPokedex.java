package pokemon;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
/**
 * Esta clase cuenta con varios metodos de apoyo para la clase Pokedex. Es la encargada de crear
 * el fichero binario con los pokemons de cada pokedex. Y guardar cada pokedex cread (objeto) en un 
 * fichero de objetos.
 * Tambien lee el fichero especies y las guarda en memoria para su uso durante la ejecucion del programa
 * y soluciona en problema de las cabeceras al escribir varios objetos en sesiones diferentes.
 * 
 * @author jesus
 *
 */

public class AuxPokedex {

	private static final String PATH_ESPECIES = "./especies.txt"; //Provisional para no introducirlo a  mano
	private static final String PATH_POKEDEX_OBJ = "./Pokedex/ObjFiles/Pokedexs.obj";
	
	//Crea una pokedex llamando al constructor de la clase y la guarda en el fichero de objetos
	public static void crearPokedex(String nombre, int capMax) throws IOException, Exception {
	/*
		System.out.println("Ruta del fichero especies: ");
		String pathEspecies = teclado.nextLine().trim();
	*/
		//Instanciamos el fichero que puede existir ya o no.
		File ficheroPokedexObj = new File(PATH_POKEDEX_OBJ);
		
		//Si ya existe, se usa el oos modificado para que no escriba cabeceras y poder usar varios ois sin problema al leer
		if(ficheroPokedexObj.exists()) {

			FileOutputStream fos = new FileOutputStream(ficheroPokedexObj, true);
			AppendingObjectOutputStream aoos = new AppendingObjectOutputStream(fos);
			aoos.writeObject(new Pokedex(nombre, capMax, new File(PATH_ESPECIES)));
			
			fos.close();
			aoos.close();
			return;
		}
		
		//Si no existe, entonces si se escribe con el original y sus cabeceras
		FileOutputStream fos = new FileOutputStream(ficheroPokedexObj, true);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(new Pokedex(nombre, capMax, new File(PATH_ESPECIES)));
		
		fos.close();
		oos.close();
	}
	
	//Metodo que lee las pokedes almacenadas en el fichero de objetos y devuekve un arraylist con todas ellas
	public static ArrayList<Pokedex> getPokedexs() throws FileNotFoundException, IOException, ClassNotFoundException {
		
		//ArrayList para trabajar con las pokedex leidas y ois para leer del fichero obj
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATH_POKEDEX_OBJ));
		ArrayList<Pokedex> pokedexs = new ArrayList<>();

		try {
			
			//Leer el fichero y cargarlas en el arrayList
			while(true) {
				pokedexs.add((Pokedex) ois.readObject());
			}
			
		} catch (EOFException e) {
			ois.close();
		} 
		
		return pokedexs;
	}
	
	//Coge todas las actuales, elimina la indicada y sobreescribe el archivo obj. Tambien elimina el binario asociado al objeto
	public static Pokedex eliminarPokedex(int pos) throws FileNotFoundException, ClassNotFoundException, IOException {
		ArrayList<Pokedex> pks = AuxPokedex.getPokedexs();
		Pokedex pkEliminada = pks.remove(pos);
		
		//Sobreescribir en el fichero el array sin la pk eliminada
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATH_POKEDEX_OBJ));
		for(int i = 0; i < pks.size(); i++) {
			oos.writeObject(pks.get(i));
		}
		
		oos.close();
		
		//Eliminar el fichero binario con sus datos
		File pkBin = pkEliminada.getPokedexBin();
		pkBin.delete();
		return pkEliminada;
	}
	
	
	//Getters
	public static String getPathPokedexObj() {
		return PATH_POKEDEX_OBJ;
	}
	
}



//Clase que sobreescribe el metodo ObjectOutputStream para que no escriba cabeceras antes del objeto
class AppendingObjectOutputStream extends ObjectOutputStream {

	public AppendingObjectOutputStream(OutputStream out) throws IOException {
		 super(out);
	}

	@Override
	protected void writeStreamHeader() throws IOException {
		// do not write a header, but reset:
		reset();
	}

}
