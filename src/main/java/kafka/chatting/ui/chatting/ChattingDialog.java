package kafka.chatting.ui.chatting;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChattingDialog extends JDialog {
    private final JPanel chattingPanel;
    private final JPanel inputPanel;
    private final Integer chatRoomNo;

    public ChattingDialog(Object chatRoomNo) {
        this.chatRoomNo = (Integer) chatRoomNo;
        chattingPanel = new ChattingPanel(new BorderLayout());
        inputPanel = new InputPanel(new BorderLayout(), this.chatRoomNo);
        init();
        addComponent();
        chattingPanel.setBackground(new Color(178, 199, 217));
        inputPanel.setBackground(Color.BLUE);
    }

    private void init() {
        this.setTitle("Chatting(" + chatRoomNo + ")");
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setModal(false);
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
            final BoundedRangeModel brm = scrollPane.getVerticalScrollBar().getModel();
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

    public void dismiss() {
        this.dispose();
    }
}
