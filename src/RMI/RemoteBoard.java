package RMI;

import Users.Participant;
import Tools.Drawable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.Vector;

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
        participant.exit();
    }


    @Override
    public void updateBoard(Vector<Drawable> paints) {
        // todo : finish this when board is set
        for (Drawable d: paints) {
            System.out.println("History said: " + d.msg);
        }
    }

    @Override
    public void kickOut() throws RemoteException {
        participant.exit();
    }
}
