package com.innsync.auth.util;

public class DBUtility {
	public static String generateIdentifier(String businessName,Integer id) {
		String cleanedName=businessName.toLowerCase().replaceAll("[^a-z0-9]", "_");
		return "innsync_" + cleanedName + "_" + id;
	}
}
