package Board;

import Users.ParticipantsManager;

import static Utils.Logger.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Vector;

public class ParticipantListPanel extends JPanel {

    private JScrollPane scrollPane = new JScrollPane();
    private JList<String> participants = new JList<>();
    
    private final JFrame PARENT;

    private String selectedUID;

    private ParticipantsManager participantsManager;


    public ParticipantListPanel(JFrame PARENT, ParticipantsManager participantsManager) {
        this.participantsManager = participantsManager;
        this.PARENT = PARENT;

        setLayout(new BorderLayout());
        initList();
        participantsManager.setParticipantList(this);
        updateList();
    }

    public void updateList() {
        log("updating list");

        Vector<String> list = new Vector<>();

        if(participantsManager.isHost()) {
            list.add("[Host/You] " + participantsManager.getHostId());
        } else {
            list.add("[Host] " + participantsManager.getHostId());
            System.out.println("My name is " + participantsManager.getCurrentUid());
            // always list you on the top-most
            list.add("[Participant/You] " + participantsManager.getCurrentUid());
        }

        for (String uid: participantsManager.getAllParticipantsID()) {
            if (uid.equals(participantsManager.getCurrentUid())) {
                continue;
            }
            list.add("[Participant] " + uid);
        }

        if(participantsManager.isHost()) {
            for (String uid: participantsManager.getAllWaitingID()) {
                list.add("[Waiting] " + uid);
            }
        }

        participants.setListData(list);
    }

    public void initList() {
        log("init Participant List");
        scrollPane.setPreferredSize(new Dimension(200,200));

        participants.setForeground(Color.black);
        participants.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        participants.addListSelectionListener((e) -> {
            // only server can perform these actions
            if (!participants.getValueIsAdjusting() && participantsManager.isHost()) {
                String select = participants.getSelectedValue();

                if (select != null) {
                    // find the real username
                    String[] temp = participants.getSelectedValue().split("]");

                    if(temp[1] != null) {
                        selectedUID = temp[1].strip();

                        // handle selection event
                        if (participantsManager.getAllWaitingID().contains(selectedUID)) {
                            int result = JOptionPane.showConfirmDialog(PARENT,
                                    "Allow |" + selectedUID + "| in?",
                                    "Visitor Management",
                                    JOptionPane.YES_NO_CANCEL_OPTION);

                            switch (result) {
                                case (0) -> // yes
                                        participantsManager.allowJoin(selectedUID);
                                case (1) -> //No
                                        participantsManager.rejectJoin(selectedUID);
                                case (2) -> //cancel
                                        log("Cancel");
                            }
                        } else if (participantsManager.getAllParticipantsID().contains(selectedUID) &&
                                !participantsManager.isHost(selectedUID)) {
                            int result = JOptionPane.showConfirmDialog(PARENT,
                                    "Kick |" + selectedUID + "| ?",
                                    "Guest Management",
                                    JOptionPane.YES_NO_OPTION);

                            switch (result) {
                                case (0) -> // yes
                                        participantsManager.kick(selectedUID);
                                case (1) -> //No
                                        log("Not kicking");
                            }
                        } else {
                            log("Host Selected");
                        }

                        participants.clearSelection();
                    }
                }
            }
        });

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BorderLayout(0, 0));

        scrollPane.add(participants);
        scrollPane.setViewportView(participants);
        subPanel.add(scrollPane);
        add(subPanel, BorderLayout.CENTER);
        subPanel.setBorder(new TitledBorder(null, "Participant List",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
    }

}
