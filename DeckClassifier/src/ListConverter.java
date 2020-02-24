import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.google.gson.*;

public class ListConverter {
	ArrayList<CardData> deckList = new ArrayList<CardData>();
	ArrayList<String> jsonCards = new ArrayList<String>();
	public ListConverter() {
		//yeet
	}
	public boolean ReadDeck(String deckname) {
		try {
			deckList.clear();
			jsonCards.clear();
			File deck = new File("MtGJson/"+deckname);
			Scanner reader = new Scanner(deck);
			boolean bList = false; //tag if we are at decklist yet
			while(reader.hasNextLine()) {
				String data = reader.nextLine();
				
				if(data.contains("[main]")) {
					bList = true;
				}
				if(bList == true) {
					if(!data.contains("[")) {
						data = data.split("\\|")[0];
						String[] cData = data.split(" ", 2);
						CardData card = new CardData(Integer.parseInt(cData[0]),cData[1]);
						deckList.add(card);
						//System.out.println(card.name);
					}
				}
			}
			return true;
		} catch(FileNotFoundException e){
			System.out.println("Whoops that's not real!");
			return false;
		}
	}
	
	public void matchJson() {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		try {
			File json = new File("MtGJson/AllCards.json");
			
			Scanner reader = new Scanner(json);
			//Properties data = gson.fromJson(reader.,Properties.class);
		}catch(FileNotFoundException e) {
			System.out.println("Whoops that's not real!");
		}
	}
	
	public void getCards() {
		for(int i = 0; i<deckList.size();i++) {
			for(int j = 0;j<deckList.get(i).count;j++) {
				try {
					String str = deckList.get(i).name;
					str = str.replaceAll("\\s", "-");
					URL obj = new URL("https://api.scryfall.com/cards/named?exact=" + str);
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setConnectTimeout(4000);
					con.setReadTimeout(4000);
					int responseCode = con.getResponseCode();
					//System.out.println(con.getResponseMessage());
					//System.out.println(responseCode);
					if (responseCode == HttpURLConnection.HTTP_OK) {
						BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();
		
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
						GsonBuilder builder = new GsonBuilder();
						Gson gson = builder.create();
						String fullCleaned = response.toString().replaceAll("—", "-"); //formatting made em dash an error symbol
						fullCleaned = fullCleaned.toString().replaceAll("\u0027", "");
						CardDataJson json = gson.fromJson(fullCleaned, CardDataJson.class);
						Map<String,String[]> keywords = GetKeywords(json.name);
						json.keywords = keywords;
						String data = gson.toJson(json);
						// print result
						//System.out.println(data);
						jsonCards.add(data);
					}
				} catch (IOException e) {
					System.out.println("Nopety Nope that URL is broke.");
					e.printStackTrace();
				}
			}
		}
	}

	public Map<String,String[]> GetKeywords(String name) {
		Map<String,String[]> keys = new HashMap<String,String[]>();
		name = name.replaceAll("\\s", "_");
		File dir = new File("../../Forge/forge/forge-gui/res/cardsfolder/"+name.toLowerCase().charAt(0)+"/");
		for(File file:dir.listFiles()) {
			if(file.getName().contains(name.toLowerCase()+".txt")) {
				Scanner scan = null;
				try {
					scan = new Scanner(new File(file.getCanonicalPath()));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(scan.hasNextLine()) {
					String data = scan.nextLine();
					if(data.startsWith("A:") || data.startsWith("T:")) {
						data = data.split(":")[1];
						String[] abilities = data.split("\\|");
						for(int i = 0;i<abilities.length;i++) {
							if(!abilities[i].contains("Cost")) {

								String[] div = abilities[i].trim().split("\\s",2);
								if(div[1].contains("\u0026")) System.out.println(div[1]);
								div[1] = div[1].replaceAll(" \u0026 ", ",");
								div[1] = div[1].replaceAll("\u0027", "");
								if(div[1].contains("\\s")) {
									String[] fin = new String[1];
									fin[0] =  div[1].replaceAll("\u0026", "");
									fin[0] = fin[0].replaceAll("\u0027", "");
									keys.put(div[0],fin);	
								}else {
								String[] words = div[1].split(",");

								
								keys.put(div[0], words);
								}
							}
						}
					}
				}
			}
		}
		
		return keys;
	}
	
	public void saveJsonList(String deckname) {
		try {
			String str = "MtGJson/"+deckname;
			str = str.split("\\.")[0] + ".json";
			System.out.println(str);
			File file = new File(str);
			if(file.createNewFile()) {
				FileWriter writer = new FileWriter(str);
				for(int i = 0;i<jsonCards.size();i++) {
					writer.write(jsonCards.get(i)+"\n");
					
				}
				writer.close();
			}else {
				System.out.println("already exists m8");
			}
		}catch (IOException e) {
			
		}
	}
}
