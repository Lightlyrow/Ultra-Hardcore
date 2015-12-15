package com.leontg77.ultrahardcore.utils;

import java.util.Collection;

/**
 * Name utilities class.
 * <p>
 * Contains name related methods.
 * 
 * @author LeonTG77
 */
public class ListUtils {
	
	public String convertToString(Collection<?> collection) {
		StringBuilder list = new StringBuilder("");
		int i = 1;
		
		for (Object entry : collection) {
			if (list.length() > 0) {
				if (i == collection.size()) {
					list.append(" §7and §f");
				} else {
					list.append("§7, §f");
				}
			}
			
			list.append(entry.toString());
			i++;
		}
		
		return list.toString().trim();
	}
}