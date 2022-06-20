package ss.bond.blockchain.domain;

import java.util.ArrayList;
import java.util.Map;

public class BlockChain {

    private ArrayList<Object> chain = new ArrayList<>();
    private ArrayList<Object> pendingTransactions = new ArrayList<>();

    public void newBlock() {
        //генерирует новый блок и добавляет его в цепь
    }

    public static void hash() {
        //хэширует блок
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
}
