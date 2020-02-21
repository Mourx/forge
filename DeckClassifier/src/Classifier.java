import java.io.File;

public class Classifier {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ListConverter classifier = new ListConverter();
		File dir = new File("MtGJson/");
		for(File file: dir.listFiles()){
			if(classifier.ReadDeck(file.getName())) {
				//classifier.matchJson();
			}
			classifier.getCards();
			classifier.saveJsonList(file.getName());
		}
	}

}
