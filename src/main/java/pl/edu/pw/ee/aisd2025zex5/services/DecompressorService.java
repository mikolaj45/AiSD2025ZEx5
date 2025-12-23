package pl.edu.pw.ee.aisd2025zex5.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;

import pl.edu.pw.ee.aisd2025zex5.core.HuffmanNode;
import pl.edu.pw.ee.aisd2025zex5.utils.BitInputStream;

public class DecompressorService {

    public void decompress(String sourcePath, String destPath) {
        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("Input file does not exist: " + sourcePath);
        }
        if (!sourceFile.isFile()) {
            throw new IllegalArgumentException("Input path is not a file: " + sourcePath);
        }
        if (!sourceFile.canRead()) {
            throw new IllegalArgumentException("Cannot read input file: " + sourcePath);
        }
        if (sourceFile.length() == 0) {
            throw new IllegalArgumentException("Input file is empty (corrupted or invalid archive).");
        }
        
        validateOutputFile(destPath);
        
        System.out.println("Starting decompression...");

        try (BitInputStream bitIn = new BitInputStream(sourcePath);
             OutputStream out = new FileOutputStream(destPath)) {

            int blockSize = bitIn.readByte();
            if (blockSize < 1) {
                throw new RuntimeException("Corrupted file: Invalid block size in header.");
            }

            long originalFileSize = bitIn.readLong();

            System.out.println("Header info: BlockSize=" + blockSize + ", OriginalSize=" + originalFileSize);

            HuffmanNode root = readTreeStructure(bitIn, blockSize);
            if (root == null) {
                throw new RuntimeException("Corrupted file: Failed to decode Huffman tree.");
            }

            decodeData(bitIn, out, root, originalFileSize, blockSize);

            System.out.println("Decompression successful: " + destPath);

        } catch (IOException e) {
            throw new RuntimeException("Error during decompression: " + e.getMessage(), e);
        }
    }

    private HuffmanNode readTreeStructure(BitInputStream in, int blockSize) throws IOException {
        int bit = in.readBit();
        if (bit == -1) throw new IOException("Unexpected EOF while reading tree");

        if (bit == 1) {
            // To jest liść - czytamy symbol (cały blok bajtów)
            byte[] symbol = new byte[blockSize];
            for (int i = 0; i < blockSize; i++) {
                int val = in.readByte();
                if (val == -1) throw new IOException("Unexpected EOF while reading leaf symbol");
                symbol[i] = (byte) val;
            }
            return new HuffmanNode(symbol, 0);
        } else {
            HuffmanNode left = readTreeStructure(in, blockSize);
            HuffmanNode right = readTreeStructure(in, blockSize);
            return new HuffmanNode(left, right);
        }
    }

    private void decodeData(BitInputStream in, OutputStream out, HuffmanNode root, long originalSize, int blockSize) throws IOException {
        HuffmanNode current = root;
        long bytesWritten = 0;

        while (bytesWritten < originalSize) {
            int bit = in.readBit();
            if (bit == -1) {
                throw new RuntimeException("Unexpected EOF. Compressed data might be truncated.");
            }

            if (bit == 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }

            if (current.isLeaf()) {
                byte[] symbol = current.getSymbol();
                
                int bytesToWrite = (int) Math.min(blockSize, originalSize - bytesWritten);
                
                out.write(symbol, 0, bytesToWrite);
                bytesWritten += bytesToWrite;

                current = root;
            }
        }
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