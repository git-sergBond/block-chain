package ss.bond.blockchain.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

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
        Map<String, String> block = new java.util.HashMap<>(Map.of(
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
     * @return Хэш в формате SHA256
     */
    public static String hash(Map<String, String> block) {
        return null;
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
}
