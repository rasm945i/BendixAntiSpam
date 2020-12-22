package dk.rasmusbendix.antispam.modules;

import dk.rasmusbendix.antispam.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
public class SymbolsModule extends ChatModule {

    // NOTE: Just an idea, not finished

    @Getter @Setter private int maxRepetitiveSymbols; // Max amount of times a character that is not a-zA-Z0-9 can be present in a row

    @Override
    public boolean allowChatEvent(Message message, ArrayList<Message> history) {
        return isSymbolsRepetitive(message.getContent());
    }

    public boolean isSymbolsRepetitive(String msg) {

        int count = 0;

        char[] chars = msg.toCharArray();
        for(char ch : chars) {

            if(!Character.isLetterOrDigit(ch) && !Character.isSpaceChar(ch))
                // Not a letter, digit or space, probably is a symbol
                count++;
            else
                // Reset once a valid character has been met
                count = 0;

        }

        // If symbols take up half the message, id call that excessive or unnecessary
        return count >= maxRepetitiveSymbols && count >= msg.length()/2;

    }

}