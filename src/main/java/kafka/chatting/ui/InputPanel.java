package kafka.chatting.ui;

import kafka.chatting.network.Client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class InputPanel extends JPanel {
    private JTextField textField = new JTextField("");

    public InputPanel(BorderLayout layout) {
        super(layout);
        addComponent();
    }

    private void addComponent() {
        textField.setFont(new Font("", Font.PLAIN, 16));
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (textField.getText().trim().length() > 0) {
                        Client.getInstance().send(textField.getText().trim());
                        textField.setText("");
                    }
                }
            }
        });

        this.add(textField);
    }
}
