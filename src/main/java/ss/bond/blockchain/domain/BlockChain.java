package ss.bond.blockchain.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.buf.HexUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


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

        Map<String, String> block = new HashMap<>();
        block.put(BlockFields.INDEX.name(), index);
        block.put(BlockFields.TIMESTAMP.name(), timestamp);
        block.put(BlockFields.PREVIOUS_HASH.name(), previousHash);
        block.put(BlockFields.PENDING_TRANSACTIONS.name(), pendingTransactions.toString());//TODO сереализовать / десериализовать правильно

        block.put(BlockFields.HASH.name(), BlockChain.hash(block));

        chain.add(block);

        return block;
    }

    /**
     * Хэширует блок.
     *
     * @param block Блок от которого нужно получить Хэш
     * @return Хэш в формате SHA256 закодированный в BASE64
     */
    public static String hash(Map<String, String> block) {
        String unifiedBlock = getUnifiedBlock(block);

        byte[] rawBlock = unifiedBlock.getBytes(StandardCharsets.UTF_8);
        byte[] hash = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            hash = messageDigest.digest(rawBlock);//TODO проверить нужен ли цикличный вызов update (вдруг есь ограничение по длинне массива байт)
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return HexUtils.toHexString(hash);
    }

    /**
     * Получает последний блок в цепочке.
     *
     * @return last block
     */
    public Map<String, String> getLastBlock() {
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
     * @param block сериализуемый блок
     * @return json с отсортированными ключами
     */
    private static String getUnifiedBlock(Map<String, String> block) {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.setConfig(jsonMapper.getSerializationConfig().with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));

        String json = null;
        try {
            json = jsonMapper.writeValueAsString(block);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }
}
