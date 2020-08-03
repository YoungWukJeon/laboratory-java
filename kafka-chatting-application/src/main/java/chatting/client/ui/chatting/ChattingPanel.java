package chatting.client.ui.chatting;

import chatting.client.ClientInstance;
import chatting.model.Message;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class ChattingPanel extends JPanel {
    private final JTextPane textPane = new JTextPane();
    private final Style style = textPane.addStyle("Color Style", null);
    private final List<Message> messages;

    public ChattingPanel(LayoutManager layout, List<Message> messages) {
        super(layout);
        this.messages = messages;
        addComponent();
    }

    private void addComponent() {
        textPane.setEditable(false);
        textPane.setAutoscrolls(true);
        textPane.setBackground(new Color(178, 199, 217));

        messages.forEach(this::addMessageToTextPane); // 기존에 있던 채팅 목록들을 textPane에 붙여넣음
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

    private void addServerMessage(Message message) {
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

    private void addClientMessage(Message message) {
        if (message.getCommandType() == Message.CommandType.NORMAL) {
            if (ClientInstance.getInstance().getUser().equals(message.getUser())) {
                ChattingTextStyle.adjustMyTimeTextStyle(style);
                addText("[" + message.getTime().format(DateTimeFormatter.ofPattern(("[yyyy-MM-dd hh:mm]"))) + "]\n");
                ChattingTextStyle.adjustMyMessageTextStyle(style);
            } else {
                ChattingTextStyle.adjustUserTextStyle(style);
                addText(message.getUser().toString() + " ");
                ChattingTextStyle.adjustTimeTextStyle(style);
                addText("[" + message.getTime().format(DateTimeFormatter.ofPattern(("[yyyy-MM-dd hh:mm]"))) + "]\n");
                ChattingTextStyle.adjustMessageTextStyle(style);
            }
            addText(message.getMessage() + "\n");
        } else {
            System.out.println("Command Not Found(Client)");
        }
    }

    public void addMessageToTextPane(Message message) {
        switch (message.getMessageType()) {
            case SERVER:
                addServerMessage(message);
                break;
            case CLIENT:
                addClientMessage(message);
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
            StyleConstants.setFontSize(style, 11);
        }
        private static void adjustCommonStyle(Style style) {
            StyleConstants.setBold(style, false);
            StyleConstants.setItalic(style, false);
            StyleConstants.setForeground(style, Color.BLACK);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        }
        private static void adjustMyCommonStyle(Style style) {
            adjustCommonStyle(style);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
        }
        public static void adjustUserTextStyle(Style style) {
            adjustCommonStyle(style);
            StyleConstants.setBackground(style, new Color(255, 255, 255, 1));
            StyleConstants.setFontSize(style, 11);
        }
        public static void adjustMessageTextStyle(Style style) {
            adjustCommonStyle(style);
            StyleConstants.setBackground(style, Color.WHITE);
            StyleConstants.setFontSize(style, 13);
        }
        public static void adjustMyMessageTextStyle(Style style) {
            adjustMyCommonStyle(style);
            StyleConstants.setBackground(style, new Color(255, 255, 51));
            StyleConstants.setFontSize(style, 13);
        }
        public static void adjustTimeTextStyle(Style style) {
            adjustCommonStyle(style);
            StyleConstants.setBackground(style, new Color(255, 255, 255, 1));
            StyleConstants.setFontSize(style, 10);
        }
        public static void adjustMyTimeTextStyle(Style style) {
            adjustMyCommonStyle(style);
            StyleConstants.setBackground(style, new Color(255, 255, 255, 1));
            StyleConstants.setFontSize(style, 10);
        }
    }
}
