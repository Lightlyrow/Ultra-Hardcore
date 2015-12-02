package com.leontg77.uhc.utils;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * File utilities class.
 * <p>
 * Contains file related methods.
 * 
 * @author LeonTG77
 */
public class FileUtils {
	public static List<FileConfiguration> files = new ArrayList<FileConfiguration>();
	
	/**
	 * Update the files list if any new ones came.
	 */
	public static void updateFiles() {
		File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
		
		files.clear();
		
		for (File file : folder.listFiles()) {
			files.add(YamlConfiguration.loadConfiguration(file));
		}
	}
	
	/**
	 * Deletes the given file and it's subfiles.
	 * 
	 * @return True if sucess, false otherwise.
	 * 
	 * @author D4mnX
	 */
	public static boolean deleteFile(File path) {
		if (path.exists()) {
			File files[] = path.listFiles();
			
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteFile(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		
		return (path.delete());
	}
}