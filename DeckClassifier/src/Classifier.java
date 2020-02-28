import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;
public class Classifier {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//doDataThings();
		makeGui();
	}
	
	public static void makeGui() {
		JFrame frame = new JFrame("My Second GUI");
		URL url;
		BufferedImage img = null;
		try {
			url = new URL("https://img.scryfall.com/cards/normal/front/3/0/30f6fca9-003b-4f6b-9d6e-1e88adda4155.jpg?1562847413");
			img = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageIcon icon = new ImageIcon(img.getScaledInstance(50, 75, Image.SCALE_SMOOTH));
		JLabel label = new JLabel(icon); 
		label
		label.setSize(60,100);
		frame.getContentPane().add(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640,480);
		frame.setVisible(true);
	}

	public static void doDataThings() {
		ListConverter classifier = new ListConverter();
		File dir = new File("MtGJson/");
		for(File file: dir.listFiles()){
			if(file.getName().contains(".dck")) {
				if(classifier.ReadDeck(file.getName())) {
					//classifier.matchJson();
				}
				classifier.getCards();
				classifier.saveJsonList(file.getName());
			}
		}
	}
}
