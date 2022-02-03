package ch.samt.blockchain.common.utils.byteutils;

public class Offset {

    private int value;

    public Offset() {
        this.value = 0;
    }

    public Offset(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }

    public int addAndGet(int value) {
        return this.value += value;
    }

    public int getAndAdd(int value) {
        int v = this.value;
        this.value += value;
        return v;
    }

    public int getAndIncrement() {
        return value++;
    }

    public int incrementAndGet() {
        return ++value;
    }

}
