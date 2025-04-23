package cyberpro.game.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ModuleLayer.Controller;
import java.util.ArrayList;
import java.util.List;

import cyberpro.game.controller.GameController;
import cyberpro.game.model.Game;
import cyberpro.game.model.Player;

public class DataHandler {
	private static final String BASE = "C:\\Java\\JavaCourseFinalProject";
	private static String LAST_PLAYERSET_FILENAME = "lastPlayerSet";
	private static final String bombermenFolder = "bombermen";
	private static String bombermenFile = "game.ser";
	private static String counterFileName = "counter.txt";

	private static boolean checkPath() {
		String filePath = BASE + "\\" + bombermenFolder;
		// creating base folder
		File baseDir = new File(filePath);
		// creating folders according to the path
		baseDir.mkdirs();
		return true;
	}

	// saves last playersSetId into the special file
	public static void saveLastPlayersSet(ArrayList<Player> playersSet) {
		if (playersSet == null) {
			return;
		}
		// creating folders for path BASE
		checkPath();
		// specifying a file's name
		String lastPlayersSetPath = LAST_PLAYERSET_FILENAME + ".ser";
		File lastPlayersSetFile = new File(BASE + "\\" + bombermenFolder + "\\" + lastPlayersSetPath);
		try {
			// creating file for the lastPlayersSet
			lastPlayersSetFile.createNewFile();
			// creating streams for serializing lastPlayersSet
			FileOutputStream fileOutStream = new FileOutputStream(lastPlayersSetFile);
			ObjectOutputStream outStream = new ObjectOutputStream(fileOutStream);
			// writing lastPlayersSet into the file
			outStream.writeObject(playersSet);
			// closing the streams
			fileOutStream.close();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// returns the last PlayerSet
	public static ArrayList<Player> getLastPlayersSet() {
		// getting last PlayersSet's Id by import from the special file
		String lastPlayersSetPath = BASE + "\\" + bombermenFolder + "\\" + LAST_PLAYERSET_FILENAME + ".ser";
		// creating folders for path BASE
		checkPath();

		File lastPlayersSetFile = new File(lastPlayersSetPath);
		try {
			// if a source file doesn't exist
			if (!lastPlayersSetFile.isFile()) {
				return null;
			}
			// if a source file is empty
			if (lastPlayersSetFile.length() == 0) {
				return null;
			}
			// open reading streams
			FileInputStream fileInStream = new FileInputStream(lastPlayersSetFile);
			ObjectInputStream inStream = new ObjectInputStream(fileInStream);
			// deserializing playersSet object
			ArrayList<Player> playersSet = (ArrayList<Player>) inStream.readObject();
			// closing streams
			fileInStream.close();
			inStream.close();
			return playersSet;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	// saves counters' values for classes Client and Order: 2 values with a space
	// delimiter

	public static boolean saveCounterIntoFile() {
		// creating folders for path BASE
		checkPath();
		// specifying a counter file's name
		String counterFilePath = BASE + "\\" + bombermenFolder + "\\" + counterFileName;
		File counterFile = new File(counterFilePath);
		try {
			// creating file for Player counter
			counterFile.createNewFile();
			// if didn't manage to create a file
			if (!counterFile.exists()) {
				System.out.println("Unable to create a file for saving the Player counter...");
				return false;
			}
			// creating a stream for writing counters file
			FileWriter writeFile = new FileWriter(counterFile);
			writeFile.append(Player.getCounter() + "");
			writeFile.flush();
			writeFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	// updates counters' values for classes Client and Order from file

	public static boolean loadCounterFromFile() {
		String counterFilePath = BASE + "\\" + bombermenFolder + "\\" + counterFileName;
		// creating folders for path BASE
		checkPath();
		try {
			File counterFile = new File(counterFilePath);

			// check if file with counters' values exists
			if (!counterFile.isFile()) {
				return false;
			}
			// create a read stream
			FileReader readFile = new FileReader(counterFilePath);
			// read into temp and append to str
			int temp;
			StringBuilder str = new StringBuilder("");
			while ((temp = readFile.read()) != -1) {
				str.append((char) temp);
			}
			// closing the reading stream
			readFile.close();
			// check if file content is empty
			if (str.toString().isBlank()) {
				return false;
			}
			// updating Player counter's value
			int counter = Integer.parseInt(str + "");
			System.out.println(
					"Updating Player's counter: old value = " + Player.getCounter() + ", new value = " + counter);
			if (counter >= 0) {
				Player.setCounter(counter);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	// serialize playersSet
	public static boolean serializePlayersSet(ArrayList<Player> players) {
		// creating folders for path BASE
		checkPath();
		// specifying a file's name
		bombermenFile = GameController.getPlayersSetId(players) + ".ser";
		File playersFile = new File(BASE + "\\" + bombermenFolder + "\\" + bombermenFile);
		try {
			// creating file for players
			playersFile.createNewFile();
			// creating streams for serializing players
			FileOutputStream fileOutStream = new FileOutputStream(playersFile);
			ObjectOutputStream outStream = new ObjectOutputStream(fileOutStream);
			// writing broker object into the file
			outStream.writeObject(players);
			// closing the streams
			fileOutStream.close();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static ArrayList<Player> deserializePlayersSet(File bomberFile) {
		String filePath = bomberFile.getAbsolutePath();//BASE + "\\" + bombermenFolder + "\\" + bomberFile;
		// creating folders for path BASE
		//checkPath();
		File bombermenFile = new File(filePath);
		try { // if a source file doesn't exist
			if (!bombermenFile.isFile()) {
				return null;
			}
			// if a source file is empty
			if (bombermenFile.length() == 0) {
				return null;
			}
			// open reading streams
			FileInputStream fileInStream = new FileInputStream(bombermenFile);
			ObjectInputStream inStream = new ObjectInputStream(fileInStream);
			// deserializing players object
			ArrayList<Player> players = (ArrayList<Player>) inStream.readObject();
			// closing streams
			fileInStream.close();
			inStream.close();
			return players;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// returns an ArrayList of all the playersSets from all the files from the
	// bombermen folder
	public static ArrayList<ArrayList<Player>> deserializePlayersSets() {
		String filePath = BASE + "\\" + bombermenFolder;
		// creating folders for path BASE
		checkPath();
		// getting list of files in the bombermen folder
		File dir = new File(filePath);
		List<File> filesList = new ArrayList<>();
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().charAt(0) == 'P')
				filesList.add(file);
		}
		System.out.println(filesList);
		// declaring a variable for collecting deserialized playersSets
		ArrayList<ArrayList<Player>> playersSets = new ArrayList<ArrayList<Player>>();
		// deserializing every playersSet from each file
		ArrayList<Player> playersSet = new ArrayList<Player>();
		for (File file : filesList) {
			playersSet = deserializePlayersSet(file);
			System.out.println("For file " + file + " playersSet = " + playersSet);
			if (playersSet != null) {
				playersSets.add(playersSet);
			}
		}
		return playersSets;
	}

}