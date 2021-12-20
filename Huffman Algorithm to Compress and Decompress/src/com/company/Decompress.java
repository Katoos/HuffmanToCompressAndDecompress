package com.company;

import java.io.*;


public class Decompress {

    private static Node root = new Node();
    static int x;
    static int y;
    static int o;
    static int w;

    // -------------- Some Secondary Helper Methods -----------------------
    
    // 1) To Convert byteArray to Int 

    public static int byteArrayToInt(byte[] b) {
    	return   b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }

    // -------------- Main Universal Method To Decompress -----------------------

    public static void decompressFile(String sourceFileAbsolutePath, String destFileAbsolutePath) throws IOException {    
    	
        	FileInputStream FileInputStream = new FileInputStream(new File(sourceFileAbsolutePath));
        	BufferedInputStream bin = new BufferedInputStream(FileInputStream);

        	byte a [] = bin.readNBytes(4);
        	byte b [] = bin.readNBytes(4);
        	byte c [] = bin.readNBytes(4);
        	byte d [] = bin.readNBytes(4);

        	o = byteArrayToInt(a);
        	x = byteArrayToInt(b);
        	y = byteArrayToInt(c);
        	w = byteArrayToInt(d);
        	
        	root = readDictionaryAndBuildHuffmanTree(bin);
            decompress(destFileAbsolutePath, bin);
    }
    
    // -------------- Some Main Helper Methods -----------------------
    
    // 1) To Read Dictionary And Build Huffman Tree
    
    private static Node readDictionaryAndBuildHuffmanTree(BufferedInputStream bin) throws IOException {
    	if (bin.read() == 1) {
    		Node n = new Node();
    		int c;
    		String m = "";
    		for(int i=0; i<o; i++) {
    			c = bin.read();
    			m += (char)c;
    		}
    		n.string = m;
    		n.left = null;
    		n.right = null;
    		return n;
    	}
    	else {
    		Node leftChild = readDictionaryAndBuildHuffmanTree(bin);
    		Node rightChild = readDictionaryAndBuildHuffmanTree(bin);
    		Node n = new Node();
    		String s = "";
    		n.string = s + (char)0;
    		n.left = leftChild;
    		n.right = rightChild;
    		return n;
    	}
    }
    
    // 2) To Write the decompressed file
    

    private static void decompress(String destFilename, BufferedInputStream bin ) throws IOException {

    	Node tempNode = root;
        FileOutputStream fileWriter = new FileOutputStream(new File(destFilename));
        BufferedOutputStream bout =new BufferedOutputStream(fileWriter);
            
        	int c;
            StringBuilder compressedFileBinary = new StringBuilder();
        	int counterBytes=0;
        	
        	int n = 8;
            int z = 8 - y ;
            while ((c = bin.read()) != -1) {
            	compressedFileBinary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
                for (int i = 0; i < n; i++) {
                	if ((counterBytes+1) == x) {
                		n = 8 - z;
                	}
                	if (compressedFileBinary.charAt(i) == '0') {
                		tempNode = tempNode.left;
               		} 
               		else {
               			tempNode = tempNode.right;
               		}

               		if (tempNode.right == null && tempNode.left == null) {
               			if(counterBytes != x) {
                   			for(int k=0; k<o; k++) {
                   				bout.write((int)tempNode.string.charAt(k));
                   			}
                   			tempNode = root;               				
               			}
               			else {
                   			for(int k=0; k<o-w; k++) {
                   				bout.write((int)tempNode.string.charAt(k));
                   			}
                   			tempNode = root;      
               			}
               		}
               }
       			compressedFileBinary.setLength(0);
       			counterBytes++;
          }
            bout.close();
    }
}
