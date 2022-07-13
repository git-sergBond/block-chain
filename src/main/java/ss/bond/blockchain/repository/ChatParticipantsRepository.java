package ss.bond.blockchain.repository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatParticipantsRepository {

    private final Map<String, String> users = new ConcurrentHashMap<>();

    public void connectUser(String sessionId, String userName) {
        users.put(sessionId, userName);
    }

    public void disconnectUser(String sessionId) {
        users.remove(sessionId);
    }

    public Collection<String> getConnectedUserNames() {
        return users.values();
    }

    public int getCountUsers() {
        return users.size();
    }
}
