package ss.bond.blockchain.domain;

import org.apache.tomcat.util.buf.HexUtils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProfOfWork {

    /**
     * Хэш некоторого целого числа х, умноженного на другое целое число у, должен заканчиваться на 0.
     * Основная идея доказательства работы: сложно сделать, но легко проверить.
     */
    public static void PoW() {
        int x = 5;
        int y = 0;

        byte[] hash = null;
        do {
            byte[] input = ByteBuffer.allocate(4).putInt(x * y).array();
            hash = hash(input);
            y += 1;
        } while (hash[hash.length - 1] != 0);

        System.out.println("[ProfOfWork] Найдено решение: x=" + x + " y=" + y + " hash=" + HexUtils.toHexString(hash));
    }

    private static byte[] hash(byte[] input) {
        byte[] hash = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            hash = messageDigest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}
