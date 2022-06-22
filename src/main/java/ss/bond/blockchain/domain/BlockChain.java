package ss.bond.blockchain.domain;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        String index = String.valueOf(chain.size());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        TreeMap<String, String> block = new java.util.TreeMap<>();
        block.put("index", index);
        block.put("timestamp", timestamp);
        block.put("previousHash", previousHash);

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
        return byteArrayToHexString(hash);
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

    /**
     * Converter byte[] to Hex String.
     * https://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l
     * 
     * @param bytes array of bytes
     * @return String in Hex view
     */
    private static String byteArrayToHexString(byte[] bytes) { //TODO разобрать как это работает
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
}
