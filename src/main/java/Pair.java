public class Pair {
    final int len;
    int num;
    private static final String DELIMITER = ":";

    Pair(int len, int num) {
        this.len = len;
        this.num = num;
    }


    public int getLen() {
        return len;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return len + DELIMITER + num;
    }
}