package ss.bond.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ss.bond.blockchain.domain.BlockChain;

@SpringBootApplication
public class BlockChainApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockChainApplication.class, args);
		BlockChain blockGen = new BlockChain();
		//blockGen.newBlock(blockGen.)
	}

}
