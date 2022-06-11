package GJFile;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class GJFileLoader {
	public final static boolean SAVING=false;
	public static final boolean OPENING=true;
	public static BufferedImage[] LoadImagesFromDIR(String link){
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		File file = new File(link);
		File[] files = file.listFiles();
		for(File f:files){
			if(CheckFileisImage(f)){
				try {
					images.add(ImageIO.read(f));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		BufferedImage[] returnimages = new BufferedImage[images.size()];
		images.toArray(returnimages);
		return returnimages;
	}
	public static BufferedImage[] LoadImagesFromDIR(String link,int maxload){
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		File file = new File(link);
		File[] files = file.listFiles();
		int no_image = 0;
		for(File f:files){
			if(no_image > maxload){
				break;
			}
			if(CheckFileisImage(f)){
				try {
					images.add(ImageIO.read(f));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			no_image++;
		}
		BufferedImage[] returnimages = new BufferedImage[images.size()];
		images.toArray(returnimages);
		return returnimages;
	}
	public static boolean CheckSupported(String link) {
		String extension = "";
		int i = link.lastIndexOf('.');
		if (i > 0) {
		    extension = link.substring(i+1);
		}
		extension = extension.toLowerCase();
		if(extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg")) {
			return true;
		}else {
			System.out.println("ERROR NOT SUPPORTED IMAGE TYPE");
			return false;
		}
	}
	public static BufferedImage LoadImage(String link){
		File file = new File(link);
		String extension = "";
		int i = link.lastIndexOf('.');
		if (i > 0) {
		    extension = link.substring(i+1);
		}
		extension = extension.toLowerCase();
		if(extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg")) {
			try {
				return ImageIO.read(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			System.out.println("ERROR NOT SUPPORTED IMAGE TYPE");
		}
		return null;
	}
	public static void SaveImage(BufferedImage bi,String directory){
		File outputfile = new File(directory);
		try {
			ImageIO.write(bi, "jpg", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void CreateDIR(String directory) throws IOException{
		new File(directory).mkdirs();
	}
	public static void SaveText(String text,String directory) throws IOException{
		File file = new File(directory);
		try {
			FileWriter writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			String[] textline = text.split("\n");
			for(String textdata:textline){
				bw.write(textdata);
				bw.newLine();
			}
			bw.close();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void SaveText(String[] text,String directory) throws IOException{
		File file = new File(directory);
		try {
			FileWriter writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			for(String textdata:text){
				bw.write(textdata);
				bw.newLine();
			}
			bw.close();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String OpenSaveChooser(String initial_directory,JFrame window){
		JFileChooser filechooser = new JFileChooser(initial_directory);
		if(filechooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION){
			return filechooser.getSelectedFile().getAbsolutePath();
		}else{
			return null;
		}
	}
	public static String OpenLoadChooser(String initial_directory,JFrame window){
		JFileChooser filechooser = new JFileChooser(initial_directory);
		if(filechooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION){
			return filechooser.getSelectedFile().getAbsolutePath();
		}else{
			return null;
		}
	}
	public static String OpenDirChooser(String initial_directory,JFrame window,boolean opening){
		JFileChooser filechooser = new JFileChooser(initial_directory);
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(!opening && filechooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION){
			return filechooser.getSelectedFile().getAbsolutePath();
		}else if(opening && filechooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION){
			return filechooser.getSelectedFile().getAbsolutePath();
		}else{
			return null;
		}
	}
	public static String[] LoadText(String directory){
		File file = new File(directory);
		ArrayList<String> buffer = new ArrayList<String>();
		String line = null;
		if(file.exists()){
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				try {
					while((line = br.readLine()) != null){
						buffer.add(line);
					}
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String[] returnbuffer = new String[buffer.size()];
		buffer.toArray(returnbuffer);
		return  returnbuffer;
	}
	public static boolean CheckFileisImage(File f){
        try {
			if(ImageIO.read(f) != null){
			   return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			  return false;
		}
        return false;
	}
}
