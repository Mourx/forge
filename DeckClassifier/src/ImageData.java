import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import java.net.URL;
public class ImageData {

	public ArrayList<ImageIcon> imgs;
	public Map<URL,BufferedImage> buffImgs;
	
	public ImageData() {
		imgs = new ArrayList<ImageIcon>();
		buffImgs = new HashMap<URL,BufferedImage>();
	}
}
