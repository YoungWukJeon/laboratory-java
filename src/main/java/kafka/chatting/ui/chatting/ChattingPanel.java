package kafka.chatting.ui.chatting;

import kafka.chatting.model.Message;
import kafka.chatting.network.Client;
import kafka.chatting.ui.EventTarget;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Flow.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class ChattingPanel extends JPanel implements EventTarget<Message> {
    private final JTextPane textPane = new JTextPane();
    private final Style style = textPane.addStyle("Color Style", null);
    private Subscription subscription;

    public ChattingPanel(LayoutManager layout) {
        super(layout);
        Client.getInstance().setEventTarget(this);
        addComponent();
    }

    private void addComponent() {
        textPane.setEditable(false);
        textPane.setAutoscrolls(true);
        textPane.setBackground(new Color(178, 199, 217));
        this.add(textPane);
    }

    private void addText(String text) {
        try {
            int beforeLength = textPane.getStyledDocument().getLength();
            textPane.getStyledDocument().insertString(textPane.getStyledDocument().getLength(), text, style);
            textPane.getStyledDocument().setParagraphAttributes(beforeLength, beforeLength + text.length(), style, false);
        } catch (BadLocationException exception) {
            exception.printStackTrace();
        }
    }

    private void addServerCommand(Message message) {
        switch (message.getCommandType()) {
            case JOIN:
                ChattingTextStyle.adjustServerTextStyle(style);
                addText(message.getUser() + " 님이 들어왔습니다.\n");
                break;
            case LEAVE:
                ChattingTextStyle.adjustServerTextStyle(style);
                addText(message.getUser() + " 님이 나갔습니다.\n");
                break;
            default:
                System.out.println("Command Not Found(Server)");
        }
    }

    private void addClientCommand(Message message) {
        if (message.getCommandType() == Message.CommandType.NORMAL) {
            if (Client.getInstance().getUser().equals(message.getUser())) {
                addClientTextForMe(message);
            } else {
                addClientText(message);
            }
        } else {
            System.out.println("Command Not Found(Client)");
        }
    }

    private void addClientText(Message message) {
        ChattingTextStyle.adjustClientUserStyle(style);
        addText(message.getUser().toString() + " ");
        ChattingTextStyle.adjustClientTimeStyle(style);
        addText("[" + message.getTime().format(DateTimeFormatter.ofPattern(("[yyyy-MM-dd hh:mm:ss]"))) + "]\n");
        ChattingTextStyle.adjustClientTextStyle(style);
        addText(message.getMessage() + "\n");
    }

    private void addClientTextForMe(Message message) {
        ChattingTextStyle.adjustClientTimeStyleForMe(style);
        addText("[" + message.getTime().format(DateTimeFormatter.ofPattern(("[yyyy-MM-dd hh:mm:ss]"))) + "]\n");
        ChattingTextStyle.adjustClientTextStyleForMe(style);
        addText(message.getMessage() + "\n");
    }

    @Override
    public void update(Message message) {
        switch (message.getMessageType()) {
            case SERVER:
                addServerCommand(message);
                break;
            case CLIENT:
                addClientCommand(message);
                break;
            default:
                System.out.println("MessageType Not Found");
        }
    }

    private static class ChattingTextStyle {
        private ChattingTextStyle() {}
        public static void adjustServerTextStyle(Style style) {
            StyleConstants.setBold(style, true);
            StyleConstants.setItalic(style, true);
            StyleConstants.setForeground(style, Color.RED);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
            StyleConstants.setBackground(style, new Color(169, 189, 206));
            StyleConstants.setFontSize(style, 10);
        }
        private static void adjustClientStyle(Style style) {
            StyleConstants.setBold(style, false);
            StyleConstants.setItalic(style, false);
            StyleConstants.setForeground(style, Color.BLACK);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        }
        private static void adjustClientStyleForMe(Style style) {
            adjustClientStyle(style);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
        }
        public static void adjustClientUserStyle(Style style) {
            adjustClientStyle(style);
            StyleConstants.setBackground(style, new Color(255, 255, 255, 1));
            StyleConstants.setFontSize(style, 10);
        }
        public static void adjustClientTextStyle(Style style) {
            adjustClientStyle(style);
            StyleConstants.setBackground(style, Color.WHITE);
            StyleConstants.setFontSize(style, 12);
        }
        public static void adjustClientTextStyleForMe(Style style) {
            adjustClientStyleForMe(style);
            StyleConstants.setBackground(style, new Color(255, 255, 51));
            StyleConstants.setFontSize(style, 12);
        }
        public static void adjustClientTimeStyle(Style style) {
            adjustClientStyle(style);
            StyleConstants.setBackground(style, new Color(255, 255, 255, 1));
            StyleConstants.setFontSize(style, 9);
        }
        public static void adjustClientTimeStyleForMe(Style style) {
            adjustClientStyleForMe(style);
            StyleConstants.setBackground(style, new Color(255, 255, 255, 1));
            StyleConstants.setFontSize(style, 9);
        }
    }
}
