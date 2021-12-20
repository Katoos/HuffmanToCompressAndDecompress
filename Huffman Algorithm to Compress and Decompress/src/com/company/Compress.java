package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Compress {

    static Node root;
	static String code = "";
    static int fileBytes = 0;
    static int lastByteInfo = 0;
    static int lastNByteInfo = 0;
    
    static StringBuilder compressionCode = new StringBuilder();
    static HashMap<String, Integer> frequency = new HashMap<>();
    static HashMap<String, String> codes = new HashMap<>();
    static PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

    // -------------- Some Secondary Helper Methods -----------------------
    
    // 1) To Convert bits to decimal 
    
    private static int convertBitsToDecimal(String Byte) {
    	int ascii = 0;
    	for(int i=0; i<Byte.length(); i++) {
    		if(Byte.charAt(i)=='1') {
    			ascii += 1 * Math.pow(2, 7-i);    			
    		}
    	}
    	return ascii;
    }

    // 2) To Convert integer to byte array 

    public static byte[] toBytes(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) (i >> 24);
		result[1] = (byte) (i >> 16);
		result[2] = (byte) (i >> 8);
		result[3] = (byte) (i);
		return result;
	}

    // -------------- Main Universal Method To Compress -----------------------

    public static void compressFile(String sourceFileAbsolutePath, String destFileAbsolutePath, int nBytesValue) throws IOException {
    	
    	File sourceFile = new File(sourceFileAbsolutePath);
    	File destFile = new File(destFileAbsolutePath);

    	createFrequencyHashmap(sourceFile, nBytesValue);
    	buildingPriorityQueue();
    	root = createHuffmanTree();
    	createCodesHashmap(root);
    	compress(sourceFile, destFile, nBytesValue);
    }

    // -------------- Some Main Helper Methods -----------------------

    // 1) To Read the Source File & Calculate and Store the Frequency of Each String According to nBytes to HashMap

    public static void createFrequencyHashmap(File sourceFile , int nBytes) throws IOException {
    	FileInputStream fileReader = new FileInputStream(sourceFile);
    	BufferedInputStream bin = new BufferedInputStream(fileReader);
    	int c;
    	String cc = "";
    	while((c = bin.read()) != -1) {
    		cc += (char)c;
    		if(nBytes > 1) {
    			for(int i=1; i<nBytes; i++) {
    				c = bin.read();
              		if (c == -1) {
              			for (int k=0; k<(nBytes-i); k++) {
              				cc += (char) 0;
                   		}
               			break;                        		
               		}
                   	cc += (char) c;
           		}
           	}
    		if(frequency.get(cc) == null)
    			frequency.put(cc, 1);
    		else
    			frequency.put(cc, frequency.get(cc)+1);	
            cc = "";
    	}
    	bin.close();
    }

    // 2) To Create Nodes with its String and Frequency & Add into Priority Queue

    private static void buildingPriorityQueue() {
    	frequency.forEach((key, value) -> {
    		Node node = new Node(key, (int)value);
    		priorityQueue.add(node);
    	});
    }

    // 3) To Create the Huffman Tree 
    
    public static Node createHuffmanTree() {
    	int size = priorityQueue.size();
    	Node x, y;
    	for(int i=1; i<size; i++) {
    		Node node = new Node();
    		x = priorityQueue.poll();
    		y = priorityQueue.poll();
    		node.left = x;
    		node.right = y;
    		node.freq = x.freq + y.freq;
    		priorityQueue.add(node);
    	}
    	return priorityQueue.poll();
    }
    
	// 4) To Create Hashmap with New Representation Codes
    
    public static void createCodesHashmap (Node rootNode){
    	if (rootNode.getLeft() == null && rootNode.getRight() == null) {
    		codes.put(rootNode.string, code);
    	}
    	else {
    		code+="0";
    		createCodesHashmap(rootNode.getLeft());
    		code = code.substring(0,code.length()-1);
    		code+="1";
    		createCodesHashmap(rootNode.getRight());
    		code = code.substring(0,code.length()-1);
    	}        
    }
    
    // 5) Writing the dictionary into the compressed file
	
    private static void writeDictionary (Node node, BufferedOutputStream bout) throws IOException {     
    	if (node.left == null && node.right == null) {
    		bout.write(1);
    		for(int i=0; i<node.string.length(); i++) {
    			bout.write((int)node.string.charAt(i));
    		}
    	}
    	else {
    		bout.write(0);
    		writeDictionary(node.left, bout);
    		writeDictionary(node.right, bout);
    	}
    }

    // 6) To Write the Compressed File according to nBytes 
    public static void compress(File sourceFile, File compressedFile, int nBytes) throws IOException {
        int c;
        FileOutputStream fileOutputStream = new FileOutputStream(compressedFile);
       	BufferedOutputStream bout=new BufferedOutputStream(fileOutputStream);

       	byte[] nBytesArray = toBytes(0);
       	bout.write(nBytesArray);
       	byte[] fileBytesArray = toBytes(0);
       	bout.write(fileBytesArray);
       	byte[] lastByteInfoArray = toBytes(0);
       	bout.write(lastByteInfoArray);
       	byte[] lastNByteInfoArray = toBytes(0);
       	bout.write(lastNByteInfoArray);
        	
        // To Write the dictionary
        writeDictionary(root, bout);

        FileInputStream fileReader = new FileInputStream(sourceFile);
       	BufferedInputStream bin = new BufferedInputStream(fileReader);
                
        // To Read from input file and save the binary string into compressionCode
        String cc = "";
        while ((c = bin.read()) != -1) {
    		cc += (char)c;
    		if(nBytes > 1) {
           		for(int j=1; j<nBytes; j++) {
               		c = bin.read();
               		if (c == -1) {
                   		lastNByteInfo = nBytes-j;                    			
               			for (int k=0; k<lastNByteInfo; k++) {
                       		cc += (char) 0;
               			}
               			break;                        		
                   	}
               		cc += (char) c;
              	}
           	}

      		compressionCode.append(codes.get(cc));
      		int n = compressionCode.length();
           	if (n >= 8) {
       			for(int u=0; u<(n/8); u++) {
       				String bits = "";
            		int character;
       	            for(int v=1; v<=compressionCode.length(); v++) {
       	                bits += compressionCode.charAt(v-1);
       	                if (v % 8 == 0) {
       	                	character = convertBitsToDecimal(bits);
            	            bout.write(character);
                            bits = "";
       	                    fileBytes++;
       	                    compressionCode.delete(0, 8);
       	                    break;
            	         }
                  }
      			}
       		}
            cc = "";
       	}            
       	bin.close();
            
       		int character;
           	String currentByte;
            fileBytes++;
            String leftoverBits = compressionCode.substring(0);
            lastByteInfo = leftoverBits.length();
            currentByte = String.format("%-8s", leftoverBits).replace(' ', '0');
            character = convertBitsToDecimal(currentByte);
            bout.write(character);
            bout.close();

            RandomAccessFile randomAccessFile = new RandomAccessFile(compressedFile, "rw");
            randomAccessFile.seek(0);
            randomAccessFile.write(toBytes(nBytes));
            randomAccessFile.seek(4);
            randomAccessFile.write(toBytes(fileBytes));
            randomAccessFile.seek(8);
            randomAccessFile.write(toBytes(lastByteInfo));
            randomAccessFile.seek(12);
            randomAccessFile.write(toBytes(lastNByteInfo));
            randomAccessFile.close();
    }

}
