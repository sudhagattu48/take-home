package dataclass;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameInfo {
    private int id;
    private String text;

    public GameInfo(@JsonProperty("id") int id,
                @JsonProperty("name") String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
