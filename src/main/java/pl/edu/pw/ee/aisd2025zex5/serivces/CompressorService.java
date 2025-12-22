package pl.edu.pw.ee.aisd2025zex5.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pl.edu.pw.ee.aisd2025zex5.core.HuffmanNode;
import pl.edu.pw.ee.aisd2025zex5.structures.ByteBlockFrequencyMap;
import pl.edu.pw.ee.aisd2025zex5.structures.MinPriorityQueue;
import pl.edu.pw.ee.aisd2025zex5.utils.BitOutputStream;

public class CompressorService {

    // Klasa wewnętrzna do przechowywania kodu dla danego bloku (Słownik kodów)
    private static class CodeEntry {
        byte[] block;
        String code; // np. "101"

        CodeEntry(byte[] block, String code) {
            this.block = block;
            this.code = code;
        }
    }

    private final List<CodeEntry> codeTable = new ArrayList<>();

    public void compress(String sourcePath, String destPath, int blockSize) {
        if (blockSize < 1) throw new IllegalArgumentException("Block size must be >= 1");

        try {
            // KROK 1: Analiza częstości występowania bloków
            System.out.println("Analyzing file frequency...");
            ByteBlockFrequencyMap frequencyMap = countFrequencies(sourcePath, blockSize);
            long originalFileSize = getFileSize(sourcePath);

            // KROK 2: Budowa drzewa Huffmana
            System.out.println("Building Huffman tree...");
            HuffmanNode root = buildHuffmanTree(frequencyMap);

            // KROK 3: Generowanie kodów (tabela kodowa)
            codeTable.clear();
            generateCodes(root, "");

            // KROK 4: Zapis do pliku wynikowego
            System.out.println("Writing compressed file...");
            try (BitOutputStream bitOut = new BitOutputStream(destPath)) {
                
                // 4a. Zapis Nagłówka
                bitOut.writeByte(blockSize);       // 1 bajt: rozmiar bloku (-l)
                bitOut.writeLong(originalFileSize); // 8 bajtów: rozmiar oryginału
                
                // 4b. Zapis Drzewa (Wariant B - topologia)
                writeTreeStructure(root, bitOut);

                // 4c. Kodowanie Danych
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
                // Jeśli ostatni blok jest krótszy, musimy go dopełnić (padding zerami w pamięci)
                // Nasza mapa i tak traktuje klucz jako byte[blockSize]
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

        // Przypadek szczególny: plik pusty lub 1 rodzaj znaku
        if (queue.size() == 0) return null;
        if (queue.size() == 1) {
            // Tworzymy sztucznego rodzica, żeby algorytm miał co kodować (0 lub 1)
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
            codeTable.add(new CodeEntry(node.getSymbol(), currentCode.length() > 0 ? currentCode : "1"));
            return;
        }

        generateCodes(node.getLeft(), currentCode + "0");
        generateCodes(node.getRight(), currentCode + "1");
    }

    // Rekurencyjny zapis drzewa: 0 dla węzła, 1 + symbol dla liścia
    private void writeTreeStructure(HuffmanNode node, BitOutputStream out) throws IOException {
        if (node == null) return;

        if (node.isLeaf()) {
            out.writeBit(1); // To jest liść
            // Zapisz symbol (byte[]) bit po bicie
            for (byte b : node.getSymbol()) {
                out.writeByte(b);
            }
        } else {
            out.writeBit(0); // To węzeł wewnętrzny
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
                    // Padding zerami dla ostatniego bloku
                    for (int i = bytesRead; i < blockSize; i++) buffer[i] = 0;
                }
                
                // Znajdź kod dla tego bloku
                String code = findCode(buffer);
                if (code == null) throw new RuntimeException("Critical Error: Code not found for block!");

                // Wypisz kod bit po bicie
                for (char c : code.toCharArray()) {
                    out.writeBit(c == '1' ? 1 : 0);
                }
            }
        }
    }
    
    // Proste wyszukiwanie liniowe w tabeli kodów (można zoptymalizować mapą, 
    // ale przy tej strukturze projektu i zakazie bibliotek to jest bezpieczne i czytelne)
    private String findCode(byte[] block) {
        for (CodeEntry entry : codeTable) {
            if (arraysEqual(entry.block, block)) {
                return entry.code;
            }
        }
        return null;
    }

    private boolean arraysEqual(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }
    
    private long getFileSize(String path) {
        return new java.io.File(path).length();
    }
}