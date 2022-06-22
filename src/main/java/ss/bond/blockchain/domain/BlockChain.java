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
import java.util.UUID;
import java.util.Objects;

import static ss.bond.blockchain.domain.BlockFields.INDEX;
import static ss.bond.blockchain.domain.BlockFields.TIMESTAMP;
import static ss.bond.blockchain.domain.BlockFields.PREVIOUS_HASH;
import static ss.bond.blockchain.domain.BlockFields.PENDING_TRANSACTIONS;
import static ss.bond.blockchain.domain.BlockFields.HASH;
import static ss.bond.blockchain.domain.BlockFields.NONCE;


public class BlockChain {

    private ArrayList<Map<String, String>> chain = new ArrayList<>();
    private ArrayList<Object> pendingTransactions = new ArrayList<>();

    public BlockChain() {
        createGenesisBlock();
    }

    /**
     * Генерирует новый блок.
     *
     * @return новый блок
     */
    public Map<String, String> newBlock() {
        String index = String.valueOf(chain.size());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        Map<String, String> lastBlock = getLastBlock();
        String previousHash = Objects.nonNull(lastBlock) ? lastBlock.get(HASH.name()) : null;

        Map<String, String> block = new HashMap<>();
        block.put(INDEX.name(), index);
        block.put(TIMESTAMP.name(), timestamp);
        block.put(PREVIOUS_HASH.name(), previousHash);
        block.put(PENDING_TRANSACTIONS.name(), pendingTransactions.toString());//TODO сереализовать / десериализовать правильно
        block.put(NONCE.name(), UUID.randomUUID().toString()); // TODO тут должно быть уникальное 64 бит число

        block.put(HASH.name(), BlockChain.hash(block));

        pendingTransactions = new ArrayList<>(); // TODO Сброс списка незавершенных транзаций ??? Зачем оно ???

        return block;
    }

    /**
     * PoW algorithm.
     * Основная идея доказательства работы: сложно сделать, но легко проверить.
     */
    public void proofOfWork() {
        Map<String, String> newBlock = null;

        do {
            newBlock = newBlock();
        } while (!isValidBlock(newBlock));

        chain.add(newBlock);
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
        chain.add(newBlock());//TODO нормально в блоке генезиса не проверяется валиднсть? Даже видно, что первый блок не валиден
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

    /**
     * Найдите число р, при хешировании которого с помощью решения предыдущего блока получается хэш с четырьмя ведущими нулями.
     *
     * TODO как понять не обманул ли человек и не отправил блок вычесленный где-то еще?
     */
    private static boolean isValidBlock(Map<String, String> block) {
        String hashHexStr = block.get(BlockFields.HASH.name());
        if (Objects.isNull(hashHexStr)) {
            System.out.println("HASH field not found in block");
            return false;
        }

        return hashHexStr.startsWith("0000");
    }
}
