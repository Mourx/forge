import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
	static ArrayList<File> baseDecks = new ArrayList<File>();
	static int fileIndex = 0;
	static int ROWS = 5;
	static int COLUMNS = 12;
	static int CARD_WIDTH = 90;
	static int CARD_HEIGHT = 135;
	static int INSPECT_WIDTH = 200;
	static int INSPECT_HEIGHT = 300;
	static Map<String, ArrayList<CardDataJson>> decks = new HashMap<String, ArrayList<CardDataJson>>();
	static Map<String, Boolean> loaded = new HashMap<String,Boolean>();
	static ArrayList<String> keys = new ArrayList<String>();
	static String currentDeck = "";
	static JTable table;
	static int BUFFER_SIZE = 1; // 3 decks either side loaded
	static int buffLoaded = 0;
	static int prevLoaded = 0;
	static Map<Integer,ImageData> imgData = new HashMap<Integer,ImageData>();
	static Map<Integer,DeckScore> scores = new HashMap<Integer,DeckScore>();
	static float deckScore = 0;
	static int currentLoad = 0;
	static int prevLoad = 0;
	static JLabel highlight;
	static JPanel infoPanel;
	static JSlider speedSlider;
	static boolean bBuffering = false;
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		scanFiles();
		loadNextSet();
		makeGui();
		loadDeck();

		
	}
	
	public static void loadNextSet() {
		for(int i = 0;i<BUFFER_SIZE;i++) {
			loadData(i);
			System.out.println("Processed " + i + ", Name: " + keys.get(i));
		}
	}
	
	
	public static void scanFiles() {
		File dir = new File("MtGJson/");
		files = dir.listFiles();
		for(File file : files) {
			if(file.getName().contains(".dck")) {
				baseDecks.add(file);
			}
		}
	}
	
	public static void loadData(int loadIndex) {		
		try {
			decks.put(baseDecks.get(loadIndex).getCanonicalPath(), doDataThings(baseDecks.get(loadIndex)));
			keys.add(baseDecks.get(loadIndex).getCanonicalPath());
			ArrayList<CardDataJson> json = decks.get(keys.get(loadIndex));
			ImageData iD = new ImageData();
			for(int i=0;i<json.size();i++) {
				URL url;
				try {
					url = new URL(json.get(i).image_uris.normal);
					if(!iD.buffImgs.containsKey(url)) {
						iD.buffImgs.put(url,ImageIO.read(url));
						iD.imgs.put(url,new ImageIcon(iD.buffImgs.get(url).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
					}
					
				}catch (IOException er) {
					// TODO Auto-generated catch block
					er.printStackTrace();
				}
			}
			imgData.put(loadIndex,iD);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void loadDeck() {
		currentDeck = keys.get(fileIndex);
		ArrayList<CardDataJson> json = decks.get(keys.get(fileIndex));
		for(int i=0;i<json.size();i++) {
			try {
				URL url = new URL(json.get(i).image_uris.normal);
				JLabel label = new JLabel(imgData.get(fileIndex).imgs.get(url)); 
				label.setSize(CARD_WIDTH,CARD_HEIGHT);
				table.setValueAt(label.getIcon(),i/COLUMNS,i%COLUMNS);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static void makeGui() {
		
		DefaultTableModel model = new DefaultTableModel(ROWS,COLUMNS) {
			@Override
			public Class<?> getColumnClass(int column){
				return ImageIcon.class;
			}
			
		};
		JFrame frame = new JFrame("My Second GUI");
		
		JButton nextButton = new JButton("Next!");
		nextButton.addActionListener(new ActionListener() {
		  @Override
		  public void actionPerformed(ActionEvent e) {
			  nextEntry();
		  } 
		});
	 
		/*
		 * JButton backButton = new JButton("BACK!"); backButton.addActionListener(new
		 * ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * 
		 * prevEntry(); } });
		 */
		
		JButton saveButton = new JButton("Finish :)");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//saveScores();
			}
		});
		
		table = new JTable(model);
		table.setRowHeight(CARD_HEIGHT);
		table.setShowGrid(false);
		
		
		highlight = new JLabel();
		table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				int row = table.rowAtPoint(p);
				int col = table.columnAtPoint(p);
				if ((row > -1 && row < table.getRowCount()) && (col > -1 && col < table.getColumnCount())) {
					int index = row*COLUMNS + col;	
					try {
						URL url = new URL(decks.get(keys.get(fileIndex)).get(index).image_uris.normal);
						highlight.setIcon(new ImageIcon(imgData.get(fileIndex).buffImgs.get(url).getScaledInstance(INSPECT_WIDTH, INSPECT_HEIGHT, Image.SCALE_SMOOTH)));
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		});
		
		
		table.setPreferredSize(new Dimension(CARD_WIDTH*COLUMNS, CARD_HEIGHT*ROWS));
		JPanel buttonPanel = new JPanel();
		
		infoPanel = new JPanel();
		JLabel spdDescrip = new JLabel("Speed Rating",JLabel.CENTER);
		speedSlider = new JSlider(0,10,5);
		infoPanel.add(speedSlider);
		buttonPanel.add(nextButton);
		buttonPanel.add(saveButton);
		frame.getContentPane().add(infoPanel,BorderLayout.EAST);
		frame.getContentPane().add(table,BorderLayout.WEST);
		frame.getContentPane().add(highlight,BorderLayout.CENTER);
		frame.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1500,750);
		frame.setVisible(true);
	}

	public static ArrayList<CardDataJson> doDataThings(File file) {
		ListConverter classifier = new ListConverter();
		String name = file.getName().replace(".dck", ".json");
		File json = new File("MtgJson/"+name);
		if(!json.exists()) {
			if(classifier.ReadDeck(file.getName())) {
				//classifier.matchJson();
			}
			classifier.getCards();
			classifier.saveJsonList(file.getName());
		} else {
			classifier.getCards();
			classifier.ReadFromJson(json.getName());
		}
		return classifier.getDeckList();
	}
	
	
	public static class LoadingThread extends SwingWorker<String, Object> {

		public LoadingThread() {

		}
		
		@Override
		protected String doInBackground() throws Exception {
			while(true) {

				if(buffLoaded - fileIndex < BUFFER_SIZE/2) {
					
					if(buffLoaded != fileIndex && imgData.get(buffLoaded) == null) {
						loadData(buffLoaded);
							
					}
					buffLoaded++;
				}
				
			}
		}
	}
	
	
	public static void nextEntry() {
		if(imgData.containsKey(fileIndex+1)) {
			scores.remove(fileIndex);
			scores.put(fileIndex, new DeckScore(baseDecks.get(fileIndex).getName(),deckScore));
			fileIndex++;
			loadDeck();
			
		
			
		}
		System.out.println("Index: " + fileIndex + ", Buffered: "+buffLoaded);
	}
	
	public static void saveScores() {
		//save deckScores map to a file
	}
	
	/*
	 * //Deprecated
 	 * public static class BackupThread extends SwingWorker<String,
	 * Object> {
	 * 
	 * public BackupThread() {
	 * 
	 * }
	 * 
	 * @Override protected String doInBackground() throws Exception { while(true) {
	 * if(fileIndex - prevLoaded < BUFFER_SIZE/2) { if(fileIndex - prevLoaded >= 0
	 * && prevLoaded >= 0) {
	 * 
	 * if(prevLoaded != fileIndex && imgData.get(prevLoaded) == null) {
	 * loadData(prevLoaded);
	 * 
	 * } prevLoaded++; } else { prevLoaded++; if(prevLoaded>=fileIndex) { prevLoaded
	 * = fileIndex-1; } } } } } }
	 * 
	 * 
	 * //Deprecated public static void prevEntry() {
	 * if(imgData.containsKey(fileIndex-1)) { scores.remove(fileIndex);
	 * scores.put(fileIndex, new
	 * DeckScore(baseDecks.get(fileIndex).getName(),deckScore));
	 * if(imgData.containsKey(fileIndex+BUFFER_SIZE/2)) { imgData.remove(fileIndex +
	 * BUFFER_SIZE/2); } if(buffLoaded - fileIndex == BUFFER_SIZE/2) { buffLoaded--;
	 * } prevLoaded--; fileIndex--; loadDeck();
	 * 
	 * 
	 * 
	 * 
	 * } System.out.println("Index: " + fileIndex + ", Previous: "+prevLoaded); }
	 */	
	
}



