package pl.edu.pw.ee.aisd2025zex5;

import pl.edu.pw.ee.aisd2025zex5.services.CompressorService;
import pl.edu.pw.ee.aisd2025zex5.services.DecompressorService;

public class AiSD2025ZEx5 {

    public static void main(String[] args) {
        try {
            String mode = null;
            String sourcePath = null;
            String destPath = null;
            String lengthStr = "1";

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                String nextArg = (i + 1 < args.length) ? args[i + 1] : null;

                if ("-m".equals(arg) && nextArg != null) {
                    mode = nextArg;
                    i++;
                } else if ("-s".equals(arg) && nextArg != null) {
                    sourcePath = nextArg;
                    i++;
                } else if ("-d".equals(arg) && nextArg != null) {
                    destPath = nextArg;
                    i++;
                } else if (("-l".equals(arg) || "-1".equals(arg)) && nextArg != null) {
                    lengthStr = nextArg;
                    i++;
                }
            }

            if (mode == null || sourcePath == null || destPath == null) {
                printUsageAndExit("Missing required arguments (-m, -s, -d).");
            }

            int blockSize;
            try {
                blockSize = Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Argument -l must be an integer.");
            }

            if (blockSize < 1 || blockSize > 5) {
                printUsageAndExit("Error: Block size (-l) must be less or equal to 5 and greater than 0");
            }
            
            long startTime = System.currentTimeMillis();
            
            if ("comp".equalsIgnoreCase(mode)) {
                CompressorService compressor = new CompressorService();
                compressor.compress(sourcePath, destPath, blockSize);
            } else if ("decomp".equalsIgnoreCase(mode)) {
                DecompressorService decompressor = new DecompressorService();
                decompressor.decompress(sourcePath, destPath);
            } else {
                printUsageAndExit("Invalid mode. Use '-m comp' or '-m decomp'.");
            }
            
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Operation finished in " + duration + " ms.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            // e.printStackTrace(); 
            System.exit(1);
        }
    }

    private static void printUsageAndExit(String message) {
        System.err.println(message);
        System.out.println("Usage:");
        System.out.println("  java -jar AiSD2025ZEx5.jar -m <comp|decomp> -s <sourceFile> -d <destFile> [-l <blockSize>]");
        System.exit(1);
    }
}