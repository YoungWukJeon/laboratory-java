package kafka.chatting.client.ui.chatroom_list;

import kafka.chatting.client.ClientInstance;
import kafka.chatting.client.flow.MessageSubscriber;
import kafka.chatting.model.Message;
import kafka.chatting.utility.MessageFactory;
import kafka.chatting.model.ChatRoomInfo;
import kafka.chatting.client.ui.chatting.ChattingDialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ChatRoomListPanel extends JPanel {
    private final String[] headers = new String[] {"no", "recent", "present"};
    private final Map<Integer, ChattingDialog> chattingDialogMap = new HashMap<> ();
    private JTable chatRoomListTable;
    private DefaultTableModel defaultTableModel;
    private final Subscriber<Message> messageSubscriber = new MessageSubscriber(this::processReceivedMessage);

    public ChatRoomListPanel(LayoutManager layoutManager) {
        super(layoutManager);
        ClientInstance.getInstance().subscribe(messageSubscriber);
        init();
        addComponent();
    }

    private void init() {
        this.setBackground(new Color(180, 180, 180));
        this.setAutoscrolls(true);
    }

    private Object[][] changeContent() {
        final List<ChatRoomInfo> chatRoomInfos = ClientInstance.getInstance().getChatRoomInfos();
        final Object[][] contents = new Object[chatRoomInfos.size()][3];

        for (int i = 0; i < chatRoomInfos.size(); i++) {
            contents[i][0] = chatRoomInfos.get(i).getNo();
            contents[i][1] = chatRoomInfos.get(i).isPresent()? chatRoomInfos.get(i).getRecent(): "-";
            contents[i][2] = chatRoomInfos.get(i).isPresent()? "O": "X";
        }

        return contents;
    }

    private void addComponent() {
        defaultTableModel = new DefaultTableModel(changeContent(), headers) {
            @Override
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        chatRoomListTable = new JTable(defaultTableModel);
        JScrollPane scrollPane = new JScrollPane(chatRoomListTable);

        chatRoomListTable.getTableHeader().setReorderingAllowed(false);
        chatRoomListTable.getTableHeader().setResizingAllowed(false);
        chatRoomListTable.getTableHeader().setFont(new Font("", Font.PLAIN, 14));
        setColumnHeaderWidth();
        chatRoomListTable.setColumnSelectionAllowed(false);
        chatRoomListTable.setFocusable(false);
        chatRoomListTable.setAutoCreateRowSorter(true);
        chatRoomListTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        chatRoomListTable.setFont(new Font("", Font.PLAIN, 14));
        chatRoomListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatRoomListTable.setRowHeight(21);

        chatRoomListTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    final int row = chatRoomListTable.rowAtPoint(e.getPoint());
                    System.out.println((row + 1) + " 번째가 더블 클릭됨");
                    final Integer chatRoomNo = (Integer) chatRoomListTable.getValueAt(row, 0);
                    openChattingDialog(chatRoomNo);
                }
            }
        });
        this.add(scrollPane);
    }

    private void openChattingDialog(int chatRoomNo) {
        if (!ClientInstance.getInstance().isJoinedChatRoomNo(chatRoomNo)) {
            ClientInstance.getInstance().addChatRoomNo(chatRoomNo);
            ClientInstance.getInstance()
                    .send(MessageFactory.userJoinClientMessage(ClientInstance.getInstance().getUser(), chatRoomNo));
        }

        if (chattingDialogMap.containsKey(chatRoomNo)) {
            chattingDialogMap.get(chatRoomNo).requestFocus();
            return;
        }

        ChattingDialog chattingDialog =
                new ChattingDialog(chatRoomNo, ClientInstance.getInstance().getMessagesInChatRoomNo(chatRoomNo));
        chattingDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.out.println("ChattingDialog Closed");
                chattingDialogMap.remove(chatRoomNo);
            }
        });
        chattingDialogMap.put(chatRoomNo, chattingDialog);
    }

    private void setColumnHeaderWidth() {
        final DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        Enumeration<TableColumn> headerColumns = chatRoomListTable.getTableHeader().getColumnModel().getColumns();
        int columnIndex = 0;

        while (headerColumns.hasMoreElements()) {
            TableColumn tableColumn = headerColumns.nextElement();
            if (columnIndex == 0) {
                tableColumn.setPreferredWidth(50);
                tableColumn.setCellRenderer(centerRender);
            } else if (columnIndex == 1) {
                tableColumn.setPreferredWidth(333);
            } else {
                tableColumn.setPreferredWidth(100);
                tableColumn.setCellRenderer(centerRender);
            }
            columnIndex++;
        }
    }

    private void resizeColumnWidth() {
        final DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        for (int columnIndex = 0; columnIndex < chatRoomListTable.getColumnCount(); columnIndex++) {
            TableColumn tableColumn = chatRoomListTable.getColumnModel().getColumn(columnIndex);
            if (columnIndex == 0) {
                tableColumn.setPreferredWidth(50);
                tableColumn.setCellRenderer(centerRender);
            } else if (columnIndex == 1) {
                tableColumn.setPreferredWidth(333);
            } else {
                tableColumn.setPreferredWidth(100);
                tableColumn.setCellRenderer(centerRender);
            }
        }
    }

    public void processReceivedMessage(Message message) {
        System.out.println("onNext(ChatRoomListPanel) -> " + message);
        switch (message.getCommandType()) {
            case GET_CHAT_ROOM_LIST:
                processGetChatRoomListResponse(message);
                return;
            case CREATE_CHAT_ROOM:
                processCreateChatRoomResponse(message);
                return;
            case JOIN:
                processJoinAndLeaveResponse(message, true);
                return;
            case LEAVE:
                processJoinAndLeaveResponse(message, false);
                return;
            case NORMAL:
                processNormalResponse(message);
                return;
            default:
                System.out.println("Command Not Found");
        }
    }

    private void processGetChatRoomListResponse(Message message) {
        ClientInstance.getInstance().setChatRoomInfos(
                Stream.of(message.getMessage().split(" "))
                        .map(Integer::parseInt)
                        .map(i -> ChatRoomInfo.from(i, "-", false))
                        .collect(Collectors.toSet()));
        defaultTableModel.setDataVector(changeContent(), headers);
        resizeColumnWidth();
    }

    private void processCreateChatRoomResponse(Message message) {
        changeChatRoomInfo(ChatRoomInfo.from(message.getChatRoomNo(), "-", true));
        openChattingDialog(message.getChatRoomNo());
    }

    private void processJoinAndLeaveResponse(Message message, boolean isJoinResponse) {
        if (ClientInstance.getInstance().getUser().equals(message.getUser())) {
            changeChatRoomInfo(ChatRoomInfo.from(message.getChatRoomNo(), "-", isJoinResponse));
        }
    }

    private void processNormalResponse(Message message) {
        if (ClientInstance.getInstance().isJoinedChatRoomNo(message.getChatRoomNo())) {
            changeChatRoomInfo(ChatRoomInfo.from(message.getChatRoomNo(), message.getMessage(), true));
        }
    }

    private void changeChatRoomInfo(ChatRoomInfo chatRoomInfo) {
        ClientInstance.getInstance().changeChatRoomInfo(chatRoomInfo);
        defaultTableModel.setDataVector(changeContent(), headers);
        resizeColumnWidth();
    }
}
