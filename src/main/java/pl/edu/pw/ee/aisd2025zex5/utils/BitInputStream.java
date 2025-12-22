package pl.edu.pw.ee.aisd2025zex5.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream implements AutoCloseable {
    private final InputStream inputStream;
    private int currentByte; // Aktualnie przetwarzany bajt
    private int numBitsRemaining; // Ile bitów zostało w tym bajcie

    public BitInputStream(String filePath) throws IOException {
        this.inputStream = new FileInputStream(filePath);
        this.currentByte = 0;
        this.numBitsRemaining = 0;
    }

    public int readBit() throws IOException {
        // Jeśli skończyły się bity w buforze, pobierz kolejny bajt z pliku
        if (numBitsRemaining == 0) {
            currentByte = inputStream.read();
            if (currentByte == -1) {
                return -1; // Koniec strumienia (EOF)
            }
            numBitsRemaining = 8;
        }

        // Pobierz najstarszy bit (MSB) z aktualnego bajtu
        numBitsRemaining--;
        return (currentByte >> numBitsRemaining) & 1;
    }
    
    // Odczytuje 8 bitów i składa je w bajt (int)
    public int readByte() throws IOException {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            int bit = readBit();
            if (bit == -1) return -1; // Nieoczekiwany koniec
            result = (result << 1) | bit;
        }
        return result;
    }
    
    // Odczytuje 64 bity (long)
    public long readLong() throws IOException {
        long result = 0;
        for (int i = 0; i < 64; i++) {
            int bit = readBit();
            if (bit == -1) throw new IOException("Unexpected EOF while reading long");
            result = (result << 1) | bit;
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}