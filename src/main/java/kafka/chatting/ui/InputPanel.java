package kafka.chatting.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class InputPanel extends JPanel implements Subject<String> {
    private JTextField textField = new JTextField("");
    private Observer<String> targetComponent;

    public InputPanel(BorderLayout layout, Observer targetComponent) {
        super(layout);
        this.targetComponent = targetComponent;
        addComponent();
    }

    private void addComponent() {
        textField.setFont(new Font("", Font.PLAIN, 16));

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println(textField.getText());
                    notifyObserver(textField.getText());
                    textField.setText("");
                }
            }
        });

        this.add(textField);
    }

    @Override
    public void notifyObserver(String data) {
        targetComponent.update(data);
    }
}
