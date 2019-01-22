public class Main {

    public static void main(String[] args) {
        Chain chain = Chain.getInstance();

        Transaction transaction1 = new Transaction("Data of transaction 1");
        Transaction transaction2 = new Transaction("Data of transaction 2");
        Transaction transaction3 = new Transaction("Data of transaction 3");
        Transaction transaction4 = new Transaction("Data of transaction 4");
        Transaction transaction5 = new Transaction("Data of transaction 5");
        Transaction transaction6 = new Transaction("Data of transaction 6");
        Transaction transaction7 = new Transaction("Data of transaction 7");
        Transaction transaction8 = new Transaction("Data of transaction 8");

        Block block1 = new Block("###");
        block1.addTransaction(transaction1);
        block1.addTransaction(transaction2);
        block1.addTransaction(transaction3);
        block1.addTransaction(transaction4);
        block1.addTransaction(transaction5);
        block1.addTransaction(transaction6);
        block1.addTransaction(transaction7);
        block1.addTransaction(transaction8);
        block1.getMerkleRoot();
        block1.generateHash();
        chain.addBlock(block1);

        Transaction transaction9 = new Transaction("Data of transaction 9");
        Transaction transaction10 = new Transaction("Data of transaction 10");
        Transaction transaction11 = new Transaction("Data of transaction 11");
        Transaction transaction12 = new Transaction("Data of transaction 12");

        Block block2 = new Block(chain.getBlocks().get(chain.getBlocks().size()-1).getHash());
        block2.addTransaction(transaction9);
        block2.addTransaction(transaction10);
        block2.addTransaction(transaction11);
        block2.addTransaction(transaction12);
        block2.getMerkleRoot();
        block2.generateHash();
        chain.addBlock(block2);
    }
}
