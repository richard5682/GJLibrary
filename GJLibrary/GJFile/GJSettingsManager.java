package GJFile;

import java.io.File;
import java.io.IOException;

public class GJSettingsManager {
	public static String DIRECTORY;
	public static void AddSettings(String name,String value){
		String app_document = DIRECTORY+"//NodalNet";
		File document_file = new File(app_document);
		if(document_file.exists()){
			File settings_text = new File(app_document+"//settings.txt");
			if(settings_text.exists()){
				//READ THEN CHANGE THEN WRITE
				String[] values = GJFileLoader.LoadText(settings_text.getAbsolutePath());
				int indexfound = -1;
				for(int i=0;i<values.length;i++){
					String val = values[i];
					String[] packet = val.split(":,:");
					String param_name = packet[0];
					if(param_name.equals(name)){
						indexfound = i;
						break;
					}
				}
				if(indexfound != -1){
					values[indexfound] = name+":,:"+value;
					try {
						GJFileLoader.SaveText(values, settings_text.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					String[] newvalues = new String[values.length+1];
					for(int i=0;i<values.length;i++){
						newvalues[i] = values[i];
					}
					newvalues[newvalues.length-1] = name+":,:"+value;
					try {
						GJFileLoader.SaveText(newvalues, settings_text.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else{
				//WRITE
				try {
					GJFileLoader.SaveText(name+":,:"+value, settings_text.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			try {
				GJFileLoader.CreateDIR(app_document);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static String LoadSettings(String name){
		String app_document = DIRECTORY+"//NodalNet";
		File document_file = new File(app_document);
		if(document_file.exists()){
			File settings_text = new File(app_document+"//settings.txt");
			if(settings_text.exists()){
				String[] values = GJFileLoader.LoadText(settings_text.getAbsolutePath());
				for(int i=0;i<values.length;i++){
					String val = values[i];
					String[] packet = val.split(":,:");
					String param_name = packet[0];
					if(param_name.equals(name)){
						String param_value = packet[1];
						return param_value;
					}
				}
				System.err.println("NO INDEX FOUND");
				return null;
			}else{
				System.err.println("NO SETTINGS FOUND");
			}
		}
		System.err.println("NO SETTINGS FOUND");
		return null;
	}
}
