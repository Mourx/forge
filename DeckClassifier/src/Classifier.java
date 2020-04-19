import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
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
	static int offset = 49+69+54+72;
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
	static int BUFFER_SIZE = 10; // 3 decks either side loaded
	static int buffLoaded = 0;
	static int prevLoaded = 0;
	static Map<Integer,ImageData> imgData = new HashMap<Integer,ImageData>();
	static Map<Integer,DeckScore> scores = new HashMap<Integer,DeckScore>();
	static ArrayList<JRadioButton> radios = new ArrayList<JRadioButton>();
	static int deckScore = 5;
	static int currentLoad = 0;
	static int prevLoad = 0;
	static JFrame frame;
	static JLabel highlight;
	static JPanel infoPanel;
	static JButton saveButton;
	static int REQUIRED_TO_SAVE = 10;
	static boolean bBuffering = false;
	static LoadingThread th = new LoadingThread();
	
	public static void main(String[] args) throws IOException {
		scanFiles();
		//loadall(0);
		loadNextSet(fileIndex);
		makeGui();
		loadDeck();
		th.execute();
		
		
	}
	
	public static void loadall(int index) {
		for(int i = index;i<400;i++) {
			if(i%20 == 0) {
				for(int j=i-20;j<i;j++) {
					imgData.remove(j);
				}
			}
			if(i<baseDecks.size()) {
				loadData(i);
				System.out.println("Processed " + i + ", Name: " + keys.get(i));
				buffLoaded++;
			}else {
				System.out.println("No More Data!");
			}
		}
	}
	
	public static void loadNextSet(int index) {
		for(int i = index;i<BUFFER_SIZE;i++) {
			if(i<baseDecks.size()) {
				loadData(i+offset);
				System.out.println("Processed " + i + ", Name: " + keys.get(i));
				buffLoaded++;
			}else {
				System.out.println("No More Data!");
			}
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
			ArrayList<CardDataJson> json = decks.get(keys.get(loadIndex-offset));
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
					er.printStackTrace();
				}
			}
			imgData.put(loadIndex,iD);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void loadDeck() {
		currentDeck = keys.get(fileIndex);
		ArrayList<CardDataJson> json = decks.get(keys.get(fileIndex));
		for(int i=0;i<json.size();i++) {
			try {
				URL url = new URL(json.get(i).image_uris.normal);
				JLabel label = new JLabel(imgData.get(fileIndex+offset).imgs.get(url)); 
				label.setSize(CARD_WIDTH,CARD_HEIGHT);
				table.setValueAt(label.getIcon(),i/COLUMNS,i%COLUMNS);
			} catch (MalformedURLException e) {
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
		frame = new JFrame("My Second GUI");
		
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
		
		saveButton = new JButton("Finish :)");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				saveScores();
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
						highlight.setIcon(new ImageIcon(imgData.get(fileIndex+offset).buffImgs.get(url).getScaledInstance(INSPECT_WIDTH, INSPECT_HEIGHT, Image.SCALE_SMOOTH)));
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		});
		
		
		table.setPreferredSize(new Dimension(CARD_WIDTH*COLUMNS, CARD_HEIGHT*ROWS));
		JPanel buttonPanel = new JPanel();
		
		infoPanel = new JPanel();
		JLabel spdDescrip = new JLabel("Speed Rating",JLabel.CENTER);
		

		JPanel speedPanel = new JPanel();
		ButtonGroup groupRadios = new ButtonGroup();
		for(int i = 0;i<10;i++) {
			JRadioButton b = new JRadioButton();
			b.setText(""+(i+1));
			radios.add(b);
			speedPanel.add(radios.get(i));
			groupRadios.add(b);
		}
		radios.get(5).setSelected(true);
		
		
		
		infoPanel.setLayout(new BorderLayout());
		infoPanel.add(highlight,BorderLayout.NORTH);
		speedPanel.add(spdDescrip);
		infoPanel.add(speedPanel,BorderLayout.CENTER);
		buttonPanel.add(nextButton);
		buttonPanel.add(saveButton);
		saveButton.setVisible(false);
		frame.getContentPane().add(infoPanel,BorderLayout.CENTER);
		frame.getContentPane().add(table,BorderLayout.WEST);
		frame.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		        saveScores();
		    }
		});

		frame.setSize(1300,750);
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

		
		@Override
		protected String doInBackground() throws Exception {
			while(buffLoaded+offset < fileIndex+offset+BUFFER_SIZE) {
				loadData(buffLoaded+offset);
				System.out.println("Loaded Deck: "+ buffLoaded+offset);
				buffLoaded++;
				imgData.remove(fileIndex+offset-1);
			}
			return "nice";
							

		}	
	}
	
	
	public static void nextEntry() {			
		
		if(imgData.containsKey(fileIndex+offset+1)) {
			
			if(th.isDone()) {
				th = new LoadingThread();
				th.execute();
			}
				
			
			scores.remove(fileIndex+offset);
			for(int i = 0;i<radios.size();i++) {
				if(radios.get(i).isSelected()) {
					deckScore = i+1;
				}
			}
			scores.put(fileIndex+offset, new DeckScore(baseDecks.get(fileIndex+offset).getName(),deckScore));
			fileIndex++;
			loadDeck();	
			if(fileIndex >= REQUIRED_TO_SAVE) {
				saveButton.setVisible(true);
			}
			
		}
		
		System.out.println("Index: " + fileIndex + ", Buffered: "+buffLoaded);
	}
	
	public static void saveScores() {
		//save deckScores map to a file
		scores.remove(fileIndex+offset);
		for(int i = 0;i<radios.size();i++) {
			if(radios.get(i).isSelected()) {
				deckScore = i+1;
			}
		}
		scores.put(fileIndex+offset, new DeckScore(baseDecks.get(fileIndex+offset).getName(),deckScore));
		File dir = new File("ScoresLog/");
		int index = dir.listFiles().length;
		String str = "ScoresLog/archetypes"+index+".txt";
		File scoreTxt = new File(str);
		try {
			if(scoreTxt.createNewFile()) {
				FileWriter writer = new FileWriter(str);
				for(int i = 0;i<scores.size();i++) {
					writer.write("\""+baseDecks.get(i+offset).getName()+ "\" : " + scores.get(i+offset).score+"\n");
				}
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.dispose();
		
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



