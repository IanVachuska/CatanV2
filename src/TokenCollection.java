import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class TokenCollection {
    //FIELDS
    private ArrayList<Integer> tokens;
    private final IIterator<Integer> iterator;

    //CONSTRUCTORS
    public TokenCollection(int[] tokens) {
        this.tokens = new ArrayList<Integer>();
        for (int token : tokens) {
            this.tokens.add(token);
        }
        iterator = this.getIterator();
    }


    /**
     * <p>Concatenate the two {@code TokenCollection} objects into one.</p>
     * @param other the old {@code TokenCollection} object
     */
    public void addAll(TokenCollection other) {
        tokens.addAll(other.tokens);
    }


    /**
     * <p>Converts the {@code tokens} field from count format to random ordered format</p>
     */
    public void shuffle(){
        int tokenCount = 0;
        for (Integer token : tokens) {
            tokenCount += token;
        }
        ArrayList<Integer> shuffledlist = new ArrayList<Integer>(tokenCount);
        Random rand = new Random();
        int startOffset = 0, endOffset = 0;
        int randomCycle = 0;//incremented every loop iteration
        while(shuffledlist.size() < tokenCount) {
            //if the last resource in the resourceCounts array is 0, then trim tail
            if (tokens.get((tokens.size() - 1) - endOffset) == 0) {
                endOffset++;
            }
            //if the first resource in the resourceCounts array is 0, then trim head
            if (tokens.get(startOffset) == 0) {
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


    /**
     * <p>Randomizes the ordering of the {@code tokens}</p>
     */
    public void reshuffle(){
        Collections.shuffle(tokens);
    }


    /**
     * <p>Sets the {@code head} of the built in iterator to the value of {@code index}.
     * Subsequent calls to {@code reset()} will set the value of {@code index}
     * to the new value of {@code head} instead of zero</p>
     */
    public void setHead(){
        iterator.setHead();
    }


    /**
     * <p>Resets the iterator to the current value of {@code head}.</p>
     */
    public void reset(){
        iterator.reset();
    }


    /**
     * <p>Iterate the built in token iterator</p>
     * @return the next number token in the list
     */
    public Integer getNext(){
        if(iterator.hasNext()) {
            return iterator.getNext();
        }
        else {
            System.err.println("No more tokens in collection: ");
            return -1;
        }
    }


    //ITERATOR ACCESSORS

    /**
     * @return a new {@code IIterator} object for the {@code TokenCollection}
     */
    public IIterator<Integer> getIterator(){
        return new TokenIterator();
    }


//------------------------------ITERATOR CLASSES------------------------------//

    /**
     * <p>{@code IIterator} class for the {@code tokens} collection.</p>
     */
    private class TokenIterator implements IIterator<Integer>{
        //FIELDS
        private int index;
        private int head;

        //CONSTRUCTORS
        public TokenIterator(){
            index = 0;
            head = 0;
        }

        //METHODS
        /**
         * @return {@code true} if there is another {@code token} in the collection, else {@code false}
         */
        @Override
        public boolean hasNext() {
            return index < tokens.size();
        }


        /**
         * @return the next {@code token} in the collection if it exists, else -1
         */
        @Override
        public Integer getNext() {
            int token = -1;
            if(hasNext()){
                token = tokens.get(index);
                index++;
            }
            return token;
        }


        /**
         * <p>Sets the {@code head} of the iterator to the value of {@code index}.
         * Subsequent calls to {@code reset()} will set the value of {@code index}
         * to the new value of {@code head} instead of zero</p>
         */
        public void setHead() {
            head = index;
        }


        /**
         * <p>Resets the iterator to the current value of {@code head}.</p>
         */
        @Override
        public void reset() {
            index = head;
        }
    }
}
