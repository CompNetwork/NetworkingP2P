package main;

public class FileChunkImpl implements FileChunk {
    byte[] backing_store;
    public FileChunkImpl(byte[] read_in) {
        this.backing_store = read_in;
    }

    @Override
    public int size() {
        return backing_store.length;
    }

    @Override
    public int get(int i) {
        return backing_store[i];
    }

    @Override
    public byte[] asByteArray() {
        return backing_store;
    }
}
