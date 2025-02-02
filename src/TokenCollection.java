import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class TokenCollection {
    //FIELDS
    private ArrayList<Integer> tokens;
    private final IIterator iterator;
    //CONSTRUCTOR
    public TokenCollection(int[] tokens) {
        this.tokens = new ArrayList<Integer>();
        for (int token : tokens) {
            this.tokens.add(token);
        }
        iterator = this.getIterator();
    }
    public void addAll(TokenCollection other) {
        tokens.addAll(other.tokens);
    }
    public boolean hasNext() {
        return iterator.hasNext();
    }
    public Integer getNext(){
        return (Integer)iterator.getNext();
    }
    public void setHead(){
        iterator.setHead();
    }
    public void reset(){
        iterator.reset();
    }
    public void shuffle(){
        int tokenCount = 0;
        for (int i = 0; i < tokens.size(); i++){
            tokenCount += tokens.get(i);
        }
        ArrayList<Integer> shuffledlist = new ArrayList<Integer>(tokenCount);
        Random rand = new Random();
        int startOffset = 0, endOffset = 0;
        int randomCycle = 0;//incremented every loop iteration
        while(shuffledlist.size() < tokenCount) {
            //if the last resource in the resourceCounts array is 0, then trim tail
            if (tokens.get((tokens.size() - 1) - endOffset) == 0) {
                //System.out.println("end"+endOffset);
                endOffset++;
            }
            //if the first resource in the resourceCounts array is 0, then trim head
            if (tokens.get(startOffset) == 0) {
                //System.out.println("start"+startOffset);
                startOffset++;
            }
            if (tokens.size() - endOffset - startOffset <= 0) {
                break;
            }
            int randomToken = startOffset + rand.nextInt(tokens.size()
                    - endOffset - startOffset);
            if(tokens.get(randomToken) > 0){
                int val = tokens.get(randomToken);
                tokens.set(randomToken, val-1);
                //randomToken changed now
                randomToken = randomToken + 2;
                if(randomToken > 6){
                    randomToken++;
                }
                shuffledlist.add(randomToken);
            }
            randomCycle++;
        }
        Collections.shuffle(shuffledlist);
        tokens = shuffledlist;
        System.out.println("Random Report\nt:" + randomCycle + "/" + tokenCount);
    }
    public void reshuffle(){
        Collections.shuffle(tokens);
    }
    public IIterator<Integer> getIterator(){
        return new TokenIterator();
    }
    private class TokenIterator implements IIterator<Integer>{
        private int current;
        private int head;
        public TokenIterator(){
            current = 0;
            head = 0;
        }
        @Override
        public boolean hasNext() {
            return current < tokens.size();
        }
        @Override
        public Integer getNext() {
            int token = -1;
            if(hasNext()){
                token = tokens.get(current);
                current++;
            }
            return token;
        }
        public void setHead() {
            head = current;
        }
        @Override
        public void reset() {
            current = head;
        }
    }
}
