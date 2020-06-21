package kafka.chatting.client.ui.chatting;

import kafka.chatting.model.Message;
import kafka.chatting.client.network.Client;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import javax.swing.*;

public class ChattingDialog extends JDialog implements Subscriber<Message> {
    private final ChattingPanel chattingPanel;
    private final JPanel inputPanel;
    private final Integer chatRoomNo;
    private Subscription subscription;

    public ChattingDialog(Integer chatRoomNo, List<Message> messages) {
        this.chatRoomNo = chatRoomNo;
        chattingPanel = new ChattingPanel(new BorderLayout(), messages);
        inputPanel = new InputPanel(new BorderLayout(), this.chatRoomNo);
        init();
        addComponent();
        chattingPanel.setBackground(new Color(178, 199, 217));
        inputPanel.setBackground(Color.BLUE);
        Client.getInstance().subscribe(this);
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

    public void dismiss() {
        onComplete();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1L);
    }

    @Override
    public void onNext(Message message) {
        System.out.println("onNext(ChattingDialog[" + chatRoomNo + "]) -> " + message);
        if (this.chatRoomNo.equals(message.getChatRoomNo())) {
            if (message.getMessageType() == Message.MessageType.SERVER
                    && message.getCommandType() == Message.CommandType.LEAVE
                    && Client.getInstance().getUser().equals(message.getUser())) {
                this.dispose();
                return;
            }
            chattingPanel.addMessageToTextPane(message);
        } else {
            System.out.println("This message is not in chatRoomNo=" + chatRoomNo + "(message.chatRoomNo=" + message.getChatRoomNo()+ ")");
        }
        subscription.request(1L);
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println(throwable.getMessage());
        subscription.cancel();
    }

    @Override
    public void onComplete() {
        System.out.println("Done!(ChattingDialog[" + chatRoomNo + "])");
        subscription.cancel();
    }
}
