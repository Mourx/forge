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
	static int BUFFER_SIZE = 6; // 3 decks either side loaded
	static int buffLoaded = 0;
	static int prevLoaded = 0;
	static Map<Integer,ImageData> imgData = new HashMap<Integer,ImageData>();
	static int currentLoad = 0;
	static int prevLoad = 0;
	static JLabel highlight;
	static boolean bBuffering = false;
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		scanFiles();
		loadData(fileIndex);
		makeGui();
		loadDeck();
		LoadingThread thread = new LoadingThread();
		thread.execute();
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
					
					iD.buffImgs.add(ImageIO.read(url));
					iD.imgs.add(new ImageIcon(iD.buffImgs.get(i).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
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
			JLabel label = new JLabel(imgData.get(fileIndex).imgs.get(i)); 
			label.setSize(CARD_WIDTH,CARD_HEIGHT);
			table.setValueAt(label.getIcon(),i/COLUMNS,i%COLUMNS);
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
		JButton button = new JButton("Next!");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				refreshTable();
				if(bNext == true) {
					LoadingThread th = new LoadingThread();
					th.execute();
				}
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
					highlight.setIcon(new ImageIcon(imgData.get(fileIndex).buffImgs.get(index).getScaledInstance(INSPECT_WIDTH, INSPECT_HEIGHT, Image.SCALE_SMOOTH)));
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
	
	
	public static class LoadingThread extends SwingWorker<String, Object> {

		public LoadingThread() {

		}
		
		@Override
		protected String doInBackground() throws Exception {
			while(true) {

				if(buffLoaded - fileIndex < BUFFER_SIZE/2) {
					
					if(decks.get(baseDecks.get(buffLoaded).getCanonicalPath()) == null) {
						loadData(buffLoaded);
							
					}
					buffLoaded++;
				}
				if(fileIndex - prevLoaded < BUFFER_SIZE/2) {
					if(fileIndex - prevLoaded >= 0) {
						
						if(decks.get(baseDecks.get(prevLoaded).getCanonicalPath()) == null) {
							loadData(prevLoaded);
							
						}
						prevLoaded++;
						
						
					}
				}
			}
		}
	}
	
	
	public static void refreshTable() {
		if(imgData.containsKey(fileIndex+1)) {
			fileIndex++;
			loadDeck();
			if(imgData.containsKey(fileIndex-BUFFER_SIZE/2)) {
				imgData.remove(fileIndex - BUFFER_SIZE/2);
			}
		
			
		}
		System.out.println("Index: " + fileIndex + ", Buffered: "+buffLoaded);
	}
	
}



