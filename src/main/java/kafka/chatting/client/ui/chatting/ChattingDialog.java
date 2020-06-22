package kafka.chatting.client.ui.chatting;

import kafka.chatting.client.ClientInstance;
import kafka.chatting.client.flow.MessageSubscriber;
import kafka.chatting.model.Message;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import javax.swing.*;

public class ChattingDialog extends JDialog {
    private final ChattingPanel chattingPanel;
    private final JPanel inputPanel;
    private final Integer chatRoomNo;
    private final Subscriber<Message> messageSubscriber = new MessageSubscriber((this::processReceivedMessage));

    public ChattingDialog(Integer chatRoomNo, List<Message> messages) {
        this.chatRoomNo = chatRoomNo;
        chattingPanel = new ChattingPanel(new BorderLayout(), messages);
        inputPanel = new InputPanel(new BorderLayout(), this.chatRoomNo);
        chattingPanel.setBackground(new Color(178, 199, 217));
        inputPanel.setBackground(Color.BLUE);
        init();
        addComponent();
        ClientInstance.getInstance().subscribe(messageSubscriber);
    }

    private void init() {
        this.setTitle("Chatting(" + chatRoomNo + ")");
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setModal(false);
        this.setSize(300, 500);
        this.setResizable(false);
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

    private void processReceivedMessage(Message message) {
        System.out.println("onNext(ChattingDialog[" + chatRoomNo + "]) -> " + message);
        if (this.chatRoomNo.equals(message.getChatRoomNo())) {
            if (message.getMessageType() == Message.MessageType.SERVER
                    && message.getCommandType() == Message.CommandType.LEAVE
                    && ClientInstance.getInstance().getUser().equals(message.getUser())) {
                this.dispose();
                return;
            }
            chattingPanel.addMessageToTextPane(message);
        } else {
            System.out.println("This message is not in chatRoomNo=" + chatRoomNo + "(message.chatRoomNo=" + message.getChatRoomNo()+ ")");
        }
    }

    public void dismiss() {
        messageSubscriber.onComplete();
    }
}
