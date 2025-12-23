package pl.edu.pw.ee.aisd2025zex5.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream implements AutoCloseable {
    private final OutputStream outputStream;
    private int currentByte;
    private int numBitsFilled;

    public BitOutputStream(String filePath) throws IOException {
        this.outputStream = new FileOutputStream(filePath);
        this.currentByte = 0;
        this.numBitsFilled = 0;
    }
    
    public BitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.currentByte = 0;
        this.numBitsFilled = 0;
    }

    public void writeBit(int bit) throws IOException {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("Bit must be 0 or 1");
        }

        currentByte = (currentByte << 1) | bit;
        numBitsFilled++;

        if (numBitsFilled == 8) {
            outputStream.write(currentByte);
            currentByte = 0;
            numBitsFilled = 0;
        }
    }

    public void writeByte(int b) throws IOException {
        for (int i = 7; i >= 0; i--) {
            int bit = (b >> i) & 1;
            writeBit(bit);
        }
    }
    
    public void writeLong(long value) throws IOException {
        for (int i = 63; i >= 0; i--) {
            int bit = (int) ((value >> i) & 1);
            writeBit(bit);
        }
    }

    @Override
    public void close() throws IOException {
        if (numBitsFilled > 0) {
            while (numBitsFilled < 8) {
                currentByte = (currentByte << 1);
                numBitsFilled++;
            }
            outputStream.write(currentByte);
        }
        outputStream.close();
    }
}