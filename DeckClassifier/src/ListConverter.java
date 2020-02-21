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
						String fullCleaned = response.toString().replace("—", "-"); //formatting made em dash an error symbol
						CardDataJson json = gson.fromJson(fullCleaned, CardDataJson.class);
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
