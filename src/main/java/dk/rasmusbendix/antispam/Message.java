package dk.rasmusbendix.antispam;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Message {

    @Getter private final String content;
    @Getter private final long timestamp;

    public Message(String content) {
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }


}
