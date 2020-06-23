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

// TODO: 2020-06-23 최초에 목록 테이블 초기화될 때, 표 크기가 너무 많은거 수정(ui적으로) 
public class ChatRoomListPanel extends JPanel {
    private final String[] headers = new String[] {"no", "recent", "present"};
    private final Map<Integer, ChattingDialog> chattingDialogMap = new HashMap<> ();
    private JTable chatRoomListTable;
    private DefaultTableModel defaultTableModel;
    private final Subscriber<Message> messageSubscriber = new MessageSubscriber((this::processReceivedMessage));

    public ChatRoomListPanel(LayoutManager layoutManager) {
        super(layoutManager);
        ClientInstance.getInstance().subscribe(messageSubscriber);
//        chatRoomInfoList.add(ChatRoomInfo.from(1, "test1", false));
//        chatRoomInfoList.add(ChatRoomInfo.from(2, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(3, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(4, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(5, "test1", false));
//        chatRoomInfoList.add(ChatRoomInfo.from(6, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(7, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(8, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(9, "test1", false));
//        chatRoomInfoList.add(ChatRoomInfo.from(10, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(11, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(12, "test1", false));
//        chatRoomInfoList.add(ChatRoomInfo.from(13, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(14, "test1", false));
//        chatRoomInfoList.add(ChatRoomInfo.from(15, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(16, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(17, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(18, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(19, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(14, "test1", false));
//        chatRoomInfoList.add(ChatRoomInfo.from(15, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(16, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(17, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(18, "test1", true));
//        chatRoomInfoList.add(ChatRoomInfo.from(19, "test1", true));

        init();
        addComponent();
    }

    private void init() {
        this.setBackground(new Color(180, 180, 180));
        this.setAutoscrolls(true);
    }

    private Object[][] changeContent() {
        final List<ChatRoomInfo> chatRoomInfos = new ArrayList<> (ClientInstance.getInstance().getChatRoomInfos());
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
//        chatRoomListTable.setShowGrid(false);
        chatRoomListTable.setColumnSelectionAllowed(false);
        chatRoomListTable.setFocusable(false);
        chatRoomListTable.setAutoCreateRowSorter(true);
//        chatRoomListTable.isCellEditable(0, 0);
        chatRoomListTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resizeColumnWidth();
        chatRoomListTable.setFont(new Font("", Font.PLAIN, 14));
        chatRoomListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatRoomListTable.setRowHeight(21);

        chatRoomListTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    final int row = chatRoomListTable.rowAtPoint(e.getPoint());
                    final Integer chatRoomNo = (Integer) chatRoomListTable.getValueAt(row, 0);

                    if (!ClientInstance.getInstance().isJoinedChatRoomNo(chatRoomNo)) {
                        ClientInstance.getInstance().addChatRoomNo(chatRoomNo);
                        ClientInstance.getInstance()
                                .send(MessageFactory.userJoinClientMessage(ClientInstance.getInstance().getUser(), chatRoomNo));
                    }

                    System.out.println((row + 1) + " 번째가 더블 클릭됨");
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
            }
        });

        this.add(scrollPane);
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
        switch (message.getCommandType()) {
            case GET_CHAT_ROOM_LIST:
                ClientInstance.getInstance().setChatRoomInfos(
                        Stream.of(message.getMessage().split(" "))
                                .map(Integer::parseInt)
                                .map(i -> ChatRoomInfo.from(i, "-", false))
                                .collect(Collectors.toSet()
                ));
                defaultTableModel.setDataVector(changeContent(), headers);
                break;
            case JOIN:
                if (ClientInstance.getInstance().getUser().equals(message.getUser())) {
                    ClientInstance.getInstance().changeChatRoomInfo(ChatRoomInfo.from(message.getChatRoomNo(), "-", true));
                    defaultTableModel.setDataVector(changeContent(), headers);
                }
                break;
            case LEAVE:
                if (ClientInstance.getInstance().getUser().equals(message.getUser())) {
                    ClientInstance.getInstance().changeChatRoomInfo(ChatRoomInfo.from(message.getChatRoomNo(), "-", false));
                    defaultTableModel.setDataVector(changeContent(), headers);
                }
                break;
            case NORMAL:
                if (ClientInstance.getInstance().isJoinedChatRoomNo(message.getChatRoomNo())) {
                    ClientInstance.getInstance().changeChatRoomInfo(ChatRoomInfo.from(message.getChatRoomNo(), message.getMessage(), true));
                    defaultTableModel.setDataVector(changeContent(), headers);
                }
                break;
        }
        System.out.println("onNext(ChatRoomListPanel) -> " + message);
    }
}