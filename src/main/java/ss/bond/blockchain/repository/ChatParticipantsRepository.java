package ss.bond.blockchain.repository;

import org.springframework.stereotype.Service;

import java.util.Collection;
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

    public String getUserNameBySessionId(String sessionId) {
        return users.get(sessionId);
    }

    public int getCountUsers() {
        return users.size();
    }
}
