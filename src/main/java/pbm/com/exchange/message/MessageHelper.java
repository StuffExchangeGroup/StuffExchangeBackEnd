package pbm.com.exchange.message;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageHelper implements InitializingBean {

    private static MessageHelper instance = null;

    @Autowired
    MessageSource messageSource;

    public static Message getMessage(Message.Keys key, Object... arguments) {
        Message message = new Message();
        message.setId(key);
        try {
            List<String> strings = new ArrayList<String>();
            for (Object argument : arguments) {
                strings.add(argument == null ? null : argument.toString());
            }
            Locale locale = LocaleContextHolder.getLocale();
            if (Objects.isNull(locale)) {
                locale = Locale.getDefault();
            }
            message.setContent(instance.messageSource.getMessage(key.name(), strings.toArray(), locale));
            return message;
        } catch (Exception ex) {
            return message;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (instance == null) {
            instance = this;
        }
    }

    public static Message getMessage(String message) {
        Message messageContent = new Message();
        messageContent.setContent(message);
        return messageContent;
    }
}
