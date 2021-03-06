package RMI;

import Users.Participant;
import Tools.Drawable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import static Utils.Logger.*;

public class RemoteBoard extends UnicastRemoteObject implements IRemoteBoard {

    private Participant participant;

    public RemoteBoard(Participant participant) throws RemoteException {
        this.participant = participant;
    }

    @Override
    public void allowJoin(String hostId) {
        participant.invokeBoard(hostId);
    }

    @Override
    public void rejectJoin() {
        System.out.println("Join request reject by server");
        Thread temp = new Thread(participant::exit);
        temp.start();
    }

    @Override
    public void kickOut() {
        log("You are kicked by the host");
        Thread temp = new Thread(() -> {
            participant.eventNotification("You are kicked by the host!");
            participant.exit();
        });
        temp.start();
    }

    @Override
    public void HostQuit() {
        log("Host closed its board!");
        participant.hostQ = true;
        Thread temp = new Thread(() -> {
            participant.eventNotification("Server Quit!");
            participant.exit();
        });
        temp.start();
    }

    @Override
    public void notification(String text) {
        Thread temp = new Thread(() -> participant.eventNotification(text));
        temp.start();
    }

    @Override
    public void refreshParticipantList() {
        participant.getParticipantsManager().updateList();
    }

    @Override
    public void repaint() {
        participant.getBoardView().getDrawArea().repaint();
    }
}
