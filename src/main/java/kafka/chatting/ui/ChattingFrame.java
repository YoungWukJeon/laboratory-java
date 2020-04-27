package kafka.chatting.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChattingFrame extends JFrame {
    private Container chattingPanel = new ChattingPanel(new BorderLayout());
    private Container inputPanel = new InputPanel(new BorderLayout());

    public ChattingFrame() {
        init();
        addComponent();
        chattingPanel.setBackground(new Color(178, 199, 217));
        inputPanel.setBackground(Color.BLUE);
    }

    private void init() {
        this.setTitle("Chatting");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(300, 500);
//        this.setResizable(false);
        this.setLocationRelativeTo(getParent());
        this.setVisible(true);
    }

    private void addComponent() {
        JScrollPane scrollPane = new JScrollPane(chattingPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            BoundedRangeModel brm = scrollPane.getVerticalScrollBar().getModel();
            boolean wasAtBottom = true;
            @Override
            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                if (!brm.getValueIsAdjusting() && wasAtBottom) {
                    brm.setValue(brm.getMaximum());
                } else {
                    wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
                }
            }
        });

        this.add(scrollPane);
        this.add(inputPanel, BorderLayout.SOUTH);
    }
}
