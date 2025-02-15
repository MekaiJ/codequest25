package client;

import java.io.Serializable;

public class RocketData implements Serializable {
    public int x, y;
    public RocketLevel level;
    RocketData(int x, int y, RocketLevel level) {
        this.x = x;
        this.y = y;
        this.level = level;
    }
}
