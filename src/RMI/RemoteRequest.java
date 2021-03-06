package RMI;

import Utils.Feedback;
import Tools.Drawable;
import Users.ParticipantsManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Vector;

public class RemoteRequest extends UnicastRemoteObject implements IRemoteRequest {

    private ParticipantsManager participantsManager;

    public RemoteRequest(ParticipantsManager participantsManager) throws RemoteException {
        this.participantsManager = participantsManager;
    }

    @Override
    public Feedback joinRequest(String userId, String host, int port) {
        System.out.println("==== get a user ====");
        System.out.println("userId = " + userId + ", host = " + host + ", port = " + port);
        // participantsManager add clients
        return participantsManager.addUserToWaitingList(userId, host, port);
    }

    @Override
    public HashSet<String> getParticipantList() {
        return new HashSet<>(participantsManager.getAllParticipantsID());
    }

    @Override
    public void removeUserRequest(String uid) {
        participantsManager.removeUser(uid);
    }

    @Override
    public Vector<Drawable> getHistoryRequest() {
        return participantsManager.getHistory();
    }

    @Override
    public void addDrawableRequest(Drawable drawable) {
        participantsManager.getBoardView().addDrawable(drawable);
        //todo : finish the following command for repainting when host update its board
        //participantsManager.repaintAll();
    }
}
