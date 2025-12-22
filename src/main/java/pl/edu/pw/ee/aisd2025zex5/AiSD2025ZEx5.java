package pl.edu.pw.ee.aisd2025zex5;

import pl.edu.pw.ee.aisd2025zex5.services.CompressorService;
import pl.edu.pw.ee.aisd2025zex5.services.DecompressorService;

import java.util.HashMap;
import java.util.Map;

public class AiSD2025ZEx5 {

    public static void main(String[] args) {
        try {
            // Parsowanie argumentów do mapy dla wygody
            Map<String, String> parsedArgs = parseArgs(args);

            String mode = parsedArgs.get("-m");
            String sourcePath = parsedArgs.get("-s");
            String destPath = parsedArgs.get("-d");
            
            // Obsługa parametru długości: sprawdzamy -l oraz -1
            String lengthStr = parsedArgs.getOrDefault("-l", parsedArgs.getOrDefault("-1", "1"));
            int blockSize;

            // Walidacja podstawowa
            if (mode == null || sourcePath == null || destPath == null) {
                printUsageAndExit("Missing required arguments (-m, -s, -d).");
            }

            try {
                blockSize = Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Argument -l (or -1) must be an integer.");
            }

            // Uruchomienie odpowiedniego serwisu
            long startTime = System.currentTimeMillis();
            
            if ("comp".equalsIgnoreCase(mode)) {
                CompressorService compressor = new CompressorService();
                compressor.compress(sourcePath, destPath, blockSize);
            } else if ("decomp".equalsIgnoreCase(mode)) {
                // Przy dekompresji blockSize jest czytany z pliku, argument CLI jest ignorowany
                DecompressorService decompressor = new DecompressorService();
                decompressor.decompress(sourcePath, destPath);
            } else {
                printUsageAndExit("Invalid mode. Use '-m comp' or '-m decomp'.");
            }
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Operation finished in " + duration + " ms.");

        } catch (Exception e) {
            // Zgodnie z wymaganiem: jawna obsługa wyjątków
            System.err.println("Error: " + e.getMessage());
            // e.printStackTrace(); // Opcjonalnie dla debugowania
            System.exit(1);
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    map.put(args[i], args[i + 1]);
                    i++; // Przeskocz wartość
                } else {
                    // Flaga bez wartości lub błędna (np. -d -s)
                    map.put(args[i], "");
                }
            }
        }
        return map;
    }

    private static void printUsageAndExit(String message) {
        System.err.println(message);
        System.out.println("Usage:");
        System.out.println("  java -jar AiSD2025ZEx5.jar -m <comp|decomp> -s <sourceFile> -d <destFile> [-l <blockSize>]");
        System.exit(1);
    }
}