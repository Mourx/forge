
public class Classifier {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ListConverter classifier = new ListConverter();
		if(classifier.ReadDeck()) {
			classifier.matchJson();
		}
		classifier.getCards();
		classifier.saveJsonList();
	}

}
