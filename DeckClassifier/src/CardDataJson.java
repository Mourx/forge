import java.util.ArrayList;
import java.util.Map;

public class CardDataJson {

	public String name;
	public String mana_cost = "0";
	public float cmc;
	public String type_line;
	public String[] colors;
	public float power;
	public float toughness;
	public Map<String,String[]> keywords;
	public StringData image_uris;
	public CardDataJson () {
		
	}
}


