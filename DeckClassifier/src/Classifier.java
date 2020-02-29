import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class Classifier {

	static boolean bNext;
	static File[] files;
	static int fileIndex = 0;
	static int ROWS = 5;
	static int COLUMNS = 12;
	static int CARD_WIDTH = 90;
	static int CARD_HEIGHT = 135;
	static int INSPECT_WIDTH = 200;
	static int INSPECT_HEIGHT = 300;
	static Map<String, ArrayList<CardDataJson>> decks = new HashMap<String, ArrayList<CardDataJson>>();
	static Map<String, Boolean> loaded = new HashMap<String,Boolean>();
	static ArrayList<String> keys;
	static String currentDeck = "";
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File dir = new File("MtGJson/");
		bNext = true;
		files = dir.listFiles();
		if(files[fileIndex].getName().contains(".dck") && bNext) {
			decks.put(files[fileIndex].getCanonicalPath(), doDataThings(files[fileIndex]));
			makeGui();
			bNext = false;
			while(!bNext) {}
		}
	}
	
	
	
	public static void makeGui() {
		currentDeck = (String) decks.keySet().toArray()[0];
		ArrayList<CardDataJson> json = decks.get(currentDeck);
		DefaultTableModel model = new DefaultTableModel(ROWS,COLUMNS) {
			@Override
			public Class<?> getColumnClass(int column){
				return ImageIcon.class;
			}
			
		};
		JFrame frame = new JFrame("My Second GUI");
		JButton button = new JButton("Next!");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bNext = true;
			}
		});
		
		JTable table = new JTable(model);
		table.setRowHeight(CARD_HEIGHT);
		table.setShowGrid(false);
		ArrayList<ImageIcon> imgs = new ArrayList<ImageIcon>();
		ArrayList<BufferedImage> buffImgs = new ArrayList<BufferedImage>();
		for(int i=0;i<json.size();i++) {
			URL url;
			try {
				url = new URL(json.get(i).image_uris.normal);
				buffImgs.add(ImageIO.read(url));
				imgs.add(new ImageIcon(buffImgs.get(i).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
				JLabel label = new JLabel(imgs.get(i)); 
				label.setSize(CARD_WIDTH,CARD_HEIGHT);
				table.setValueAt(label.getIcon(),i/COLUMNS,i%COLUMNS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
		
		JLabel highlight = new JLabel(new ImageIcon(imgs.get(0).getImage().getScaledInstance(INSPECT_WIDTH, INSPECT_HEIGHT, Image.SCALE_SMOOTH)));
		table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				int row = table.rowAtPoint(p);
				int col = table.columnAtPoint(p);
				if ((row > -1 && row < table.getRowCount()) && (col > -1 && col < table.getColumnCount())) {
					int index = row*COLUMNS + col;
					
						
						
					highlight.setIcon(new ImageIcon(buffImgs.get(index).getScaledInstance(INSPECT_WIDTH, INSPECT_HEIGHT, Image.SCALE_SMOOTH)));
					
				}
				
				
			}
		});
		table.setPreferredSize(new Dimension(CARD_WIDTH*COLUMNS, CARD_HEIGHT*ROWS));
		frame.getContentPane().add(table,BorderLayout.WEST);
		frame.getContentPane().add(highlight,BorderLayout.CENTER);
		frame.getContentPane().add(button,BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1300,750);
		frame.setVisible(true);
	}

	public static ArrayList<CardDataJson> doDataThings(File file) {
		ListConverter classifier = new ListConverter();
		if(classifier.ReadDeck(file.getName())) {
			//classifier.matchJson();
		}
		classifier.getCards();
		classifier.saveJsonList(file.getName());
		return classifier.getDeckList();
	}
}
