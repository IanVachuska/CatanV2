import java.util.ArrayList;

public class PortCollection {
    //FIELDS
    private final ArrayList<Port> allPorts;
    private final Port[] ports;

    //CONSTRUCTORS
    public PortCollection(Board board) {
        allPorts = new ArrayList<Port>();
        ports = new Port[board.getPortCount()];
    }


    /**
     * <p>Add the {@code port} to the {@code allPort} array.</p>
     * @param port the element to be appended to the list
     */
    public void add(Port port) {
        allPorts.add(port);
    }


    /**
     * <p>Add the {@code port} to the {@code Port} array at the specified index</p>
     * @param port the element to be added to the array
     * @param index the location in the array
     */
    public void add(Port port, int index) {
        ports[index] = port;
    }


    /**
     * <p>Get the {@code port} at the specified {@code index}
     * from the collection specified by {@code valid}</p>
     * <p>When the {@code valid} parameter is {@code true},
     * this function references the collection of valid {@code ports}
     * when {@code false} this function references the collection of all {@code ports}</p>
     * @param index the index of the {@code port} to be returned
     * @param valid determines which collection to reference.
     * @return the {@code port} object
     */
    public Port get(int index, boolean valid) {
        if(valid){
            return ports[index];
        } else{
            return allPorts.get(index);
        }
    }


    //ITERATOR ACCESSORS

    /**
     * @return an {@code IIterator} object for the {@code allPort} collection
     */
    public IIterator<Port> getAllPortIterator(){
        return new AllPortIterator();
    }


    /**
     * @return an {@code IIterator} object for the {@code port} collection
     */
    public IIterator<Port> getValidPortIterator(){
        return new ValidPortIterator();
    }


//------------------------------ITERATOR CLASSES------------------------------//

    /**
     * <p>{@code IIterator} class for the {@code allPort} collection.</p>
     */
    private class AllPortIterator implements IIterator<Port>{
        //FIELDS
        private int index;

        //CONSTRUCTORS
        public AllPortIterator(){
            this.index = 0;
        }

        //METHODS

        /**
         * @return {@code true} if there is another {@code port} in the collection, else {@code false}
         */
        @Override
        public boolean hasNext() {
            return index < allPorts.size();
        }


        /**
         * @return the next {@code port} in the collection if it exists, else null
         */
        @Override
        public Port getNext() {
            if(hasNext()) {
                return allPorts.get(index++);
            }
            else{
                System.err.println("End of allPort collection");
                return null;
            }
        }


        /**
         * <p>Resets the iterator to its initial position.</p>
         */
        @Override
        public void reset() {
            index = 0;
        }


        //EMPTY BODY METHOD
        @Override
        public void setHead() {}
    }


    /**
     * <p>{@code IIterator} class for the {@code port} collection.</p>
     */
    private class ValidPortIterator implements IIterator<Port>{
        //FIELDS
        private int index;

        //CONSTRUCTORS
        public ValidPortIterator(){
            this.index = 0;
        }

        //METHODS
        /**
         * @return {@code true} if there is another port in the collection, else {@code false}
         */
        @Override
        public boolean hasNext() {
            return index < ports.length;
        }


        /**
         * @return the next valid {@code port} in the collection if it exists, else null
         */
        @Override
        public Port getNext() {
            if(hasNext()) {
                return ports[index++];
            }
            else{
                System.err.println("End of port collection");
                return null;
            }
        }


        /**
         * <p>Resets the iterator to its initial position.</p>
         */
        @Override
        public void reset() {
            index = 0;
        }


        //EMPTY BODY METHOD
        @Override
        public void setHead() {}
    }
}
