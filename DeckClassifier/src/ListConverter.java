import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
	
	public ListConverter() {
		//yeet
	}
	public boolean ReadDeck() {
		try {
			File deck = new File("MtGJson/House Party.dck");
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
						System.out.println(card.name);
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
			File json = new File("MtGJson/AllPrintings.json");
			
			Scanner reader = new Scanner(json);
			//Properties data = gson.fromJson(reader.,Properties.class);
		}catch(FileNotFoundException e) {
			System.out.println("Whoops that's not real!");
		}
	}
	
	public void getCard() {
		try {
			URL obj = new URL("https://api.magicthegathering.io/v1/cards/name=Shock");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			//con.setRequestProperty("name","Shock");
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);
			int responseCode = con.getResponseCode();
			System.out.println(con.getResponseMessage());
			System.out.println(responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				System.out.println(response.toString());
			}
		} catch (IOException e) {
			System.out.println("Nopety Nope that URL is broke.");
			e.printStackTrace();
		}
	}
}
