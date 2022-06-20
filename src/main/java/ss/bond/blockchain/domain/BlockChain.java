package ss.bond.blockchain.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.SortedMap;

public class BlockChain {

    private ArrayList<Map<String, String>> chain = new ArrayList<>();
    private ArrayList<Object> pendingTransactions = new ArrayList<>();

    public BlockChain() {
        createGenesisBlock();
    }

    /**
     * Генерирует новый блок и добавляет его в цепь.
     *
     * @param previousHash предыдущий Хэш, Nullable
     * @return новый блок
     */
    public Map<String, String> newBlock(String previousHash) {
        TreeMap<String, String> block = new java.util.TreeMap<>(Map.of(
                "index", String.valueOf(chain.size()),
                "timestamp", LocalDateTime.now().toString(), //TODO посмотреть чтобы была timeZone и формат даты ISO
                "previousHash", previousHash
        ));

        block.put("hash", BlockChain.hash(block));

        chain.add(block);

        return block;
    }

    /**
     * Хэширует блок.
     *
     * @param block Блок от которого нужно получить Хэш
     * @return Хэш в формате SHA256 закодированный в BASE64
     */
    public static String hash(SortedMap<String, String> block) {
        String unifiedBlock = getUnifiedBlock(block);
        byte[] rawBlock = unifiedBlock.getBytes(StandardCharsets.UTF_8);
        byte[] hash = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            hash = messageDigest.digest(rawBlock);//TODO проверить нужен ли цикличный вызов update (вдруг есь ограничение по длинне массива байт)
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String hashBase64 = Base64.getEncoder().encodeToString(hash);//TODO проверить нужно ли тут явно указвывать StandardCharsets.UTF_8 . PS хотя и так понятно, что BASE64 использует какаую-то универсальную кодировку
        return hashBase64;
    }

    /**
     * Получает последний блок в цепочке.
     * @return
     */
    public Object getLastBlock() {
        if (chain.size() < 1) {
            return null;
        }
        return chain.get(chain.size() - 1);
    }

    /**
     * Добавляет новую транзакцию в список ожидаемых.
     */
    public void newTransaction(String sender, String recipient, String amount) {
        pendingTransactions.add(Map.of(
                "sender", sender,
                "recipient", recipient,
                "amount", amount
        ));
    }

    /**
     * Создание блока генезиса.
     * Блок генезиса - жестко закодированный блок, с индексом 0 и не имеющий предшественников.
     */
    private void createGenesisBlock() {
        newBlock(null);
    }

    /**
     * Сортирует ключи/значения, чтобы при Хэшировани был согласованный Хэш.
     * (если перемешаются ключи то будет другой Хэш, что будет ошибкой,
     * так как при повторных вычисениях блока нужно получить один и тот же Хэш)
     *
     * @param block словарь (Map) отсортированный по ключам
     * @return json с отсортированными ключами
     */
    private static String getUnifiedBlock(SortedMap<String, String> block) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{");
        for (Map.Entry<String, String> entry : block.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(":");
            stringBuilder.append(entry.getValue());
            stringBuilder.append(",");
        }
        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}
