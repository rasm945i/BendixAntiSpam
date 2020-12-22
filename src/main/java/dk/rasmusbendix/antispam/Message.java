package dk.rasmusbendix.antispam;

import dk.rasmusbendix.antispam.modules.ChatModule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;

@AllArgsConstructor
public class Message {

    @Getter private final String content;
    @Getter private final long timestamp;
    private final HashSet<ChatModule> flaggedFor;

    public Message(String content) {
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.flaggedFor = new HashSet<>();
    }

    public void flag(ChatModule chatModule) {
        flaggedFor.add(chatModule);
    }

    // Must pass same instance
    public boolean hasFlag(ChatModule module) {
        for(ChatModule m : flaggedFor) {
            if(m == module)
                return true;
        }
        return false;
    }

    public int getTotalFlags() {
        return flaggedFor.size();
    }

}
