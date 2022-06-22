package ss.bond.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ss.bond.blockchain.domain.BlockChain;
import ss.bond.blockchain.domain.BlockFields;
import ss.bond.blockchain.domain.ProfOfWork;

@SpringBootApplication
public class BlockChainApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockChainApplication.class, args);
		BlockChain blockGen = new BlockChain();
		blockGen.newBlock(blockGen.getLastBlock().get(BlockFields.HASH.name()));

		ProfOfWork.PoW();
	}

}
