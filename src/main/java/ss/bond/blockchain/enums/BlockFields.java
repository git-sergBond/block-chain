package ss.bond.blockchain.enums;

public enum BlockFields {
    INDEX,
    TIMESTAMP,
    PREVIOUS_HASH,
    PENDING_TRANSACTIONS,
    HASH,
    NONCE // TODO безсмысленная строка (одноразовое случайное число) - важный источник случайности блоков (уникальное 64 бит число) Я так понял, чтобы не пбыла возможность заранее просчитать все блоки в PoW и был смысл в проверке блоков
}
