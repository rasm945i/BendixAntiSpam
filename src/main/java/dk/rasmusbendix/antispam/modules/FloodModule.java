package dk.rasmusbendix.antispam.modules;

import dk.rasmusbendix.antispam.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
public class FloodModule extends ChatModule {

    @Getter @Setter private long chatDelayMs; // Minimum delay between messages

    @Override
    public boolean allowChatEvent(Message message, ArrayList<Message> history) {

        if(history.isEmpty())
            return true;

        return history.get(history.size()-1).getTimestamp() + chatDelayMs <= System.currentTimeMillis();

    }

}