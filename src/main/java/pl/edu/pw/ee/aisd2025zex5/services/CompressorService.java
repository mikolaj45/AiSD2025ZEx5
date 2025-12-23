package pl.edu.pw.ee.aisd2025zex5.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import pl.edu.pw.ee.aisd2025zex5.core.HuffmanNode;
import pl.edu.pw.ee.aisd2025zex5.structures.ByteBlockCodeMap;
import pl.edu.pw.ee.aisd2025zex5.structures.ByteBlockFrequencyMap;
import pl.edu.pw.ee.aisd2025zex5.structures.MinPriorityQueue;
import pl.edu.pw.ee.aisd2025zex5.utils.BitOutputStream;

public class CompressorService {

    private final ByteBlockCodeMap codeMap = new ByteBlockCodeMap();

    public void compress(String sourcePath, String destPath, int blockSize) {
        if (blockSize < 1) throw new IllegalArgumentException("Block size must be >= 1");

        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("Input file does not exist: " + sourcePath);
        }
        if (!sourceFile.isFile()) {
            throw new IllegalArgumentException("Input path is a directory, not a file: " + sourcePath);
        }
        if (!sourceFile.canRead()) {
            throw new IllegalArgumentException("Cannot read input file (permission denied): " + sourcePath);
        }
        if (sourceFile.length() == 0) {
            throw new IllegalArgumentException("Input file is empty!");
        }
        
        validateOutputFile(destPath);
        
        try {
            System.out.println("Analyzing file frequency...");
            ByteBlockFrequencyMap frequencyMap = countFrequencies(sourcePath, blockSize);
            long originalFileSize = getFileSize(sourcePath);

            System.out.println("Building Huffman tree...");
            HuffmanNode root = buildHuffmanTree(frequencyMap);

            generateCodes(root, "");

            System.out.println("Writing compressed file...");
            try (BitOutputStream bitOut = new BitOutputStream(destPath)) {
                
                bitOut.writeByte(blockSize);
                bitOut.writeLong(originalFileSize);
                
                writeTreeStructure(root, bitOut);

                encodeData(sourcePath, blockSize, bitOut);
            }

            System.out.println("Compression successful: " + destPath);

        } catch (IOException e) {
            throw new RuntimeException("Error during compression: " + e.getMessage(), e);
        }
    }

    private ByteBlockFrequencyMap countFrequencies(String path, int blockSize) throws IOException {
        ByteBlockFrequencyMap map = new ByteBlockFrequencyMap();
        try (InputStream is = new FileInputStream(path)) {
            byte[] buffer = new byte[blockSize];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                if (bytesRead < blockSize) {
                    for (int i = bytesRead; i < blockSize; i++) {
                        buffer[i] = 0; 
                    }
                }
                map.increment(buffer);
            }
        }
        return map;
    }

    private HuffmanNode buildHuffmanTree(ByteBlockFrequencyMap map) {
        MinPriorityQueue queue = new MinPriorityQueue();
        queue.addAll(map.toNodeList());

        if (queue.size() == 0) return null;
        if (queue.size() == 1) {
            HuffmanNode node = queue.poll();
            return new HuffmanNode(node, new HuffmanNode(new byte[node.getSymbol().length], 0)); 
        }

        while (queue.size() > 1) {
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();
            HuffmanNode parent = new HuffmanNode(left, right);
            queue.add(parent);
        }

        return queue.poll();
    }

    private void generateCodes(HuffmanNode node, String currentCode) {
        if (node == null) return;

        if (node.isLeaf()) {
            codeMap.put(node.getSymbol(), currentCode.length() > 0 ? currentCode : "1");
            return;
        }

        generateCodes(node.getLeft(), currentCode + "0");
        generateCodes(node.getRight(), currentCode + "1");
    }

    private void writeTreeStructure(HuffmanNode node, BitOutputStream out) throws IOException {
        if (node == null) return;

        if (node.isLeaf()) {
            out.writeBit(1);
            for (byte b : node.getSymbol()) {
                out.writeByte(b);
            }
        } else {
            out.writeBit(0);
            writeTreeStructure(node.getLeft(), out);
            writeTreeStructure(node.getRight(), out);
        }
    }

    private void encodeData(String sourcePath, int blockSize, BitOutputStream out) throws IOException {
        try (InputStream is = new FileInputStream(sourcePath)) {
            byte[] buffer = new byte[blockSize];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                if (bytesRead < blockSize) {
                    for (int i = bytesRead; i < blockSize; i++) buffer[i] = 0;
                }
                
                String code = codeMap.get(buffer);
                
                if (code == null) throw new RuntimeException("Critical Error: Code not found for block!");

                for (char c : code.toCharArray()) {
                    out.writeBit(c == '1' ? 1 : 0);
                }
            }
        }
    }
    
    private long getFileSize(String path) {
        return new java.io.File(path).length();
    }
    
    private void validateOutputFile(String destPath) {
        File destFile = new File(destPath);
        File parentDir = destFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            throw new IllegalArgumentException("Target directory does not exist: " + parentDir.getAbsolutePath());
        }

        if (parentDir != null && !parentDir.canWrite()) {
            throw new IllegalArgumentException("Cannot write to target directory (permission denied): " + parentDir.getAbsolutePath());
        }
    }
}