package pl.edu.pw.ee.aisd2025zex5.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import pl.edu.pw.ee.aisd2025zex5.core.HuffmanNode;
import pl.edu.pw.ee.aisd2025zex5.utils.BitInputStream;

public class DecompressorService {

    public void decompress(String sourcePath, String destPath) {
        System.out.println("Starting decompression...");

        try (BitInputStream bitIn = new BitInputStream(sourcePath);
             OutputStream out = new FileOutputStream(destPath)) {

            // KROK 1: Odczyt Nagłówka
            // a) Rozmiar bloku (-l)
            int blockSize = bitIn.readByte();
            if (blockSize < 1) {
                throw new RuntimeException("Corrupted file: Invalid block size in header.");
            }

            // b) Rozmiar oryginalnego pliku (aby wiedzieć kiedy przestać)
            long originalFileSize = bitIn.readLong();

            System.out.println("Header info: BlockSize=" + blockSize + ", OriginalSize=" + originalFileSize);

            // KROK 2: Odtworzenie Drzewa Huffmana
            HuffmanNode root = readTreeStructure(bitIn, blockSize);
            if (root == null) {
                throw new RuntimeException("Corrupted file: Failed to decode Huffman tree.");
            }

            // KROK 3: Dekodowanie Danych
            decodeData(bitIn, out, root, originalFileSize, blockSize);

            System.out.println("Decompression successful: " + destPath);

        } catch (IOException e) {
            throw new RuntimeException("Error during decompression: " + e.getMessage(), e);
        }
    }

    // Rekurencyjne odtwarzanie drzewa (DFS)
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
            // Częstość nie ma znaczenia przy dekompresji, ustawiamy 0
            return new HuffmanNode(symbol, 0);
        } else {
            // To węzeł wewnętrzny - czytamy lewe i prawe dziecko
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

            // Przechodzimy po drzewie
            if (bit == 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }

            // Czy doszliśmy do liścia?
            if (current.isLeaf()) {
                // Zapisujemy odkodowany blok
                byte[] symbol = current.getSymbol();
                
                // Obliczamy ile bajtów zapisać (obsługa paddingu ostatniego bloku)
                // Np. blok ma 3 bajty, a do końca pliku został 1 bajt -> zapisujemy tylko 1
                int bytesToWrite = (int) Math.min(blockSize, originalSize - bytesWritten);
                
                out.write(symbol, 0, bytesToWrite);
                bytesWritten += bytesToWrite;

                // Resetujemy wskaźnik na korzeń dla następnego znaku
                current = root;
            }
        }
    }
}