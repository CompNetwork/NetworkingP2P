package main.config.pod;

import java.util.InvalidPropertiesFormatException;

public class CommonConfigData {
    public int getNumberPreferrredNeighbors() {
        return numberPreferrredNeighbors;
    }

    public int getUnchokeInterval() {
        return unchokeInterval;
    }

    public int getOptimisticUnchokeInterval() {
        return optimisticUnchokeInterval;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getPieceSize() {
        return pieceSize;
    }

    private final int numberPreferrredNeighbors;
    private final int unchokeInterval;
    private final int optimisticUnchokeInterval;

    private final String fileName;
    private final int fileSize;
    private final int pieceSize;

    private CommonConfigData(int numberPreferrredNeighbors, int unchokeInterval, int optimisticUnchokeInterval, String fileName, int fileSize, int pieceSize) {
        this.numberPreferrredNeighbors = numberPreferrredNeighbors;
        this.unchokeInterval = unchokeInterval;
        this.optimisticUnchokeInterval = optimisticUnchokeInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
    }

    public static class CommonConfigDataBuilder {
        int numberPreferrredNeighbors;
        int unchokeInterval;
        int optimisticUnchokeInterval;
        String fileName;
        int fileSize;
        int pieceSize;

        public void withFile(int fileSize, String name, int pieceSize) {
            this.fileName = name;
            this.pieceSize = pieceSize;
            this.fileSize = fileSize;
        }

        public void withFileSize(int fileSize) {
            this.fileSize = fileSize;
        }
        public void withFileName(String fileName) {
            this.fileName = fileName;
        }
        public void withPieceSize(int pieceSize) {
            this.pieceSize = pieceSize;
        }

        public void withUnchokeInterval(int unchokeInterval){
            this.unchokeInterval = unchokeInterval;
        }

        public void withOptimisticUnchokeInterval(int optimisticUnchokeInterval) {
            this.optimisticUnchokeInterval = optimisticUnchokeInterval;
        }

        public void withNumberPreferredNeighbors(int neighbors) {
            this.numberPreferrredNeighbors = neighbors;
        }

        public CommonConfigData build()  {
            return new CommonConfigData(numberPreferrredNeighbors, unchokeInterval, optimisticUnchokeInterval, fileName, fileSize, pieceSize);
        }

    }

    public static CommonConfigDataBuilder getBuilder() {
        return new CommonConfigDataBuilder();
    }
}
