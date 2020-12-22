package dk.rasmusbendix.antispam.modules;

import dk.rasmusbendix.antispam.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public abstract class ChatModule {

    // How deep into the message history the module goes
    // E.g. rather than checking only current message, it checks if X previous messages was flagged for the same thing
    @Getter @Setter protected boolean checkingHistory = false;
    // 3rd entry gets blocked
    @Getter @Setter protected int acceptableHistory = 2;
    // How deep into the history to look. If 10, only checks flags for 10 most recent
    // messages and compares that amount to acceptableHistory
    // E.g. if this is 20, the past 30 messages can contain 2 flagged messages, the 3rd gets blocked.
    @Getter @Setter protected int depthIntoHistory = 20;
    @Getter @Setter protected String violationMessage = "Please refrain from spamming.";

    public abstract boolean allowChatEvent(Message message, ArrayList<Message> history);

    public int countFlags(ArrayList<Message> history) {
        return countFlags(this, history);
    }

    public int countFlags(ChatModule module, ArrayList<Message> history) {
        return countFlags(module, history.size(), history);
    }

    public int countFlags(ChatModule module, int depth , ArrayList<Message> history) {

        int flags = 0;

        for (int i = history.size()-1; i >= history.size() - (depth + 1); i--) {
            // Safety against weird depths
            if(i < 0)
                break;
            Message message = history.get(i);
            // If checking any module and the message is flagged for anything
            // Or, the message is flagged for the module we check against
            if((module == null && message.getTotalFlags() > 0) || message.hasFlag(module)) {
                flags++;
            }

        }

        return flags;

    }

    public boolean hasAcceptableHistory(Message message, ArrayList<Message> history) {

        if(!checkingHistory) {
            // If we don't check the history..
            // Check if this message has the flag for the current module
            // If it does NOT, return TRUE
            // hasFlag returns false, we invert that, we get true
            // If current message is not flagged and we do not check history, history is technically acceptable
            // I hope you enjoyed my essay, cheers.
            return !message.hasFlag(this);
        }

        if(countFlags(this, depthIntoHistory, history) > acceptableHistory)
            return false;

        return true;

    }

}
