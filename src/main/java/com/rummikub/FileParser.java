package com.rummikub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileParser {
	public static List<String> playerCommands;
	public static Stock stock;
	public static Boolean inputError = false;

	static void parse(String filePath) {
		File file = new File(filePath);

		if (file.exists()) {
			parse(file);
		} else {
			inputError = true;
			Print.println("File doesn't exist");
		}
	}

	static void parse(File file) {
		List<String> fileContents;
		String filePath = file.getPath();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String fileText = "";
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			// read the file into a string, and split that string by spaces when added it to
			// a List.
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			fileText = sb.toString();
			fileContents = Arrays.asList(fileText.split("\\s"));

			List<Tile> tileList = new ArrayList<Tile>();
			playerCommands = new ArrayList<String>();

			for (String element : fileContents) {
				if (element.length() == 0) {
					// do nothing
				} else if (isInteger(element)) {
					playerCommands.add(element);
				} else if (Tile.verifyTile(element)) {
					tileList.add(new Tile(element));
				} else {
					Print.println("Invalid file contents: " + element);
					inputError = true;
				}
			}

			stock = new Stock(tileList, true);

			// verify that the stock doesn't have any triplets of cards
			// stock.validate

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			Print.println("Unknown catch-all error");
			inputError = true;
		}

		if (!inputError && playerCommands.size() > 0) {
			Prompt.init(playerCommands);
		}
	}

	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static void reset() {
		playerCommands = new ArrayList<String>();
		stock = null;
		inputError = false;
	}
}
