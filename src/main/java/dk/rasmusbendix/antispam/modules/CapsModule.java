package dk.rasmusbendix.antispam.modules;

import dk.rasmusbendix.antispam.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
public class CapsModule extends ChatModule {

    // Minimum amount of characters in a message before caps-check is applied
    @Getter @Setter private int minimumCapsCheck;
    // How large a percentage that must be caps for it to be flagged.
    // For this to have effect, minimum caps check must also be "violated"
    @Getter @Setter private double capsThreshold;

    @Override
    public boolean allowChatEvent(Message message, ArrayList<Message> history) {

        // Short message, allow it
        if(message.getContent().length() < minimumCapsCheck)
            return true;

        double capsPercent = uppercasePercentage(message.getContent());

        // 0.95 is not 1.0, allow message. 1.0 value can be adjusted
        if(capsPercent < capsThreshold) {
            return true;
        }

        // Message is caps, flag it
        message.flag(this);

        // Read comments in this method if you question it
        return hasAcceptableHistory(message, history);

    }

    private double uppercasePercentage(String content) {

        double upper = 0;
        char[] chars = content.toCharArray();

        for(char ch : chars) {
            if(Character.isUpperCase(ch)) {
                upper++;
            }
        }

        return upper / chars.length;

    }

}
