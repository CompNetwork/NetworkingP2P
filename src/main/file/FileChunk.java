package main.file;

public interface FileChunk {
    int size();
    byte get(int i);

    byte[] asByteArray();
}

