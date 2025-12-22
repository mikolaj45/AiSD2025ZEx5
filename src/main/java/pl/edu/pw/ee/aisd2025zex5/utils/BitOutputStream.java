package pl.edu.pw.ee.aisd2025zex5.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream implements AutoCloseable {
    private final OutputStream outputStream;
    private int currentByte; // Bufor na 8 bitów
    private int numBitsFilled; // Ile bitów mamy już w buforze

    public BitOutputStream(String filePath) throws IOException {
        this.outputStream = new FileOutputStream(filePath);
        this.currentByte = 0;
        this.numBitsFilled = 0;
    }
    
    // Konstruktor przyjmujący istniejący strumień (np. dla testów)
    public BitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.currentByte = 0;
        this.numBitsFilled = 0;
    }

    public void writeBit(int bit) throws IOException {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("Bit must be 0 or 1");
        }

        // Dodajemy bit do bufora (przesuwamy istniejące w lewo, wstawiamy nowy na pozycję LSB)
        currentByte = (currentByte << 1) | bit;
        numBitsFilled++;

        // Jeśli mamy pełny bajt (8 bitów), zapisujemy go do pliku
        if (numBitsFilled == 8) {
            outputStream.write(currentByte);
            currentByte = 0;
            numBitsFilled = 0;
        }
    }

    // Zapisuje cały bajt (8 bitów) - przydatne do nagłówka
    public void writeByte(int b) throws IOException {
        // Musimy zapisać 8 bitów z inta 'b'
        for (int i = 7; i >= 0; i--) {
            int bit = (b >> i) & 1;
            writeBit(bit);
        }
    }
    
    // Zapisuje wartość long (64 bity) - przydatne do długości pliku
    public void writeLong(long value) throws IOException {
        for (int i = 63; i >= 0; i--) {
            int bit = (int) ((value >> i) & 1);
            writeBit(bit);
        }
    }

    @Override
    public void close() throws IOException {
        // Jeśli w buforze zostały jakieś bity (niepełny bajt), dopełniamy zerami (padding)
        if (numBitsFilled > 0) {
            while (numBitsFilled < 8) {
                currentByte = (currentByte << 1); // Dopełnienie zerem
                numBitsFilled++;
            }
            outputStream.write(currentByte);
        }
        outputStream.close();
    }
}