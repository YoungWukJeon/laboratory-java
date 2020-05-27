package kafka.chatting.ui.chatroom_list;

import kafka.chatting.MessageFactory;
import kafka.chatting.model.ChatRoomInfo;
import kafka.chatting.network.Client;
import kafka.chatting.ui.chatting.ChattingDialog;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ChatRoomListPanel extends JPanel {
    private final String[] headers = new String[] {"no", "recent", "present"};
    private final List<ChatRoomInfo> chatRoomInfos = new ArrayList<> ();
    private JTable chatRoomListTable;

    public ChatRoomListPanel(LayoutManager layoutManager) {
        super(layoutManager);

        chatRoomInfos.add(ChatRoomInfo.from(1, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(2, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(3, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(4, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(5, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(6, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(7, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(8, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(9, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(10, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(11, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(12, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(13, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(14, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(15, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(16, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(17, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(18, "test1", true));
        chatRoomInfos.add(ChatRoomInfo.from(19, "test1", true));

        init();
        addComponent();
    }

    private void init() {
        this.setBackground(new Color(180, 180, 180));
        this.setAutoscrolls(true);
    }

    private void addComponent() {
        final Object[][] contents = new Object[chatRoomInfos.size()][3];

        for (int i = 0; i < chatRoomInfos.size(); i++) {
            contents[i][0] = chatRoomInfos.get(i).getNo();
            contents[i][1] = chatRoomInfos.get(i).getRecent();
            contents[i][2] = chatRoomInfos.get(i).isPresent()? "O": "X";
        }

        DefaultTableModel defaultTableModel = new DefaultTableModel(contents, headers) {
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
        chatRoomListTable.isCellEditable(0, 0);
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

                    if (!Client.getInstance().isJoinedChatRoomNo(chatRoomNo)) {
                        Client.getInstance().addChatRoomNo(chatRoomNo);
                        Client.getInstance().send(MessageFactory.userJoinClientMessage(Client.getInstance().getUser(), chatRoomNo));
                    }

                    System.out.println((row + 1) + " 번째가 더블 클릭됨");
                    ChattingDialog chattingDialog = new ChattingDialog(chatRoomListTable.getValueAt(row, 0));
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
}
