package ss.bond.blockchain.domain;

import org.apache.tomcat.util.buf.HexUtils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

/**
 * PoW algorithm.
 *
 * Основная идея доказательства работы: сложно сделать, но легко проверить.
 */
public class ProfOfWork {

    /**
     * Майниг Хэша.
     *
     * Найдите число р, при хешировании которого с помощью решения предыдущего блока получается хэш с четырьмя ведущими нулями.
     */
    public static void profOfWork() {
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

    /**
     * Проверка Хэша.
     *
     * TODO как понять не обманул ли человек и не отправил блок вычесленный где-то еще?
     */
    public boolean validHash(Map<String, String> block) {
        String hashHexStr = block.get(BlockFields.HASH.name());
        if (Objects.isNull(hashHexStr)) {
            System.out.println("HASH field not found in block");
            return false;
        }

        return hashHexStr.startsWith("0000");
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
