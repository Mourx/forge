import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import java.net.URL;
public class ImageData {

	public Map<URL,ImageIcon> imgs;
	public Map<URL,BufferedImage> buffImgs;
	
	public ImageData() {
		imgs = new HashMap<URL,ImageIcon>();
		buffImgs = new HashMap<URL,BufferedImage>();
	}
}
