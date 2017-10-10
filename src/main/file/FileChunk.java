package main.file;

public interface FileChunk {
    int size();
    int get(int i);

    byte[] asByteArray();
}

