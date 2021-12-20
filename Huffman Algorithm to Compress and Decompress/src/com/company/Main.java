package com.company;

import java.io.File;
import java.io.IOException;


public class Main {

	public static void main(String[] args) throws IOException {
		
		String compressionOrDecompression = args[0];
		String sourceFileAbsolutePath = args[1];
		File sourceFile = new File(sourceFileAbsolutePath);
		
		if (compressionOrDecompression.equals("c")) {
		
			int nBytesValue = Integer.valueOf(args[2]);
            String destFileAbsolutePath = sourceFile.getParent() + "\\18010147." + nBytesValue + "." + sourceFile.getName() + ".hc";
            
            long startTimeMs = System.currentTimeMillis();
            Compress.compressFile(sourceFileAbsolutePath, destFileAbsolutePath, nBytesValue);
            long endTimeMs = System.currentTimeMillis();
            System.out.println("Compression is Done Successfully!");
            System.out.println("Compression Time (ms) = " + (endTimeMs - startTimeMs) + " ms");
            System.out.println("Compression Time (s)  = " + String.format("%.2f",(double)(endTimeMs - startTimeMs)/1000) + " s");  
            System.out.println("Compression Time (m)  = " + String.format("%.2f",(double)(endTimeMs - startTimeMs)/60000) + " m");  
    		
            long sourceFileLength = sourceFile.length();
            File destFile = new File(destFileAbsolutePath);            
            long destFileLength = destFile.length();
            System.out.println("Compression Ratio = " + String.format("%.2f",((double)destFileLength/sourceFileLength)));
		
		}
		else if (compressionOrDecompression.equals("d")) {
			
			String extractedFileName = sourceFile.getName().substring(0, sourceFile.getName().length()-3);
            String destFileAbsolutePath = sourceFile.getParent() + "\\extracted." + extractedFileName;            
            long startTimeMs = System.currentTimeMillis();
            Decompress.decompressFile(sourceFileAbsolutePath, destFileAbsolutePath);
            long endTimeMs = System.currentTimeMillis();
            System.out.println("Decompression is Done Successfully!");
            System.out.println("Decompression Time (ms) = " + (endTimeMs - startTimeMs) + " ms");
            System.out.println("Decompression Time (s)  = " + String.format("%.2f", (double)(endTimeMs - startTimeMs)/1000) + " s");  
            System.out.println("Decompression Time (m)  = " + String.format("%.2f", (double)(endTimeMs - startTimeMs)/60000) + " m");  
		
		}
		else {
			System.out.println("Wrong Input ! - Write c Or d For Compression & Decompression Respectively");
		}
	}
}
