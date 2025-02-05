import java.util.ArrayList;

public class PortCollection {

    private ArrayList<Port> allPorts;
    private Port[] ports;

    //CONSTRUCTORS
    public PortCollection(Board board) {
        allPorts = new ArrayList<Port>();
        ports = new Port[board.getPortCount()];
    }

    public void add(Port port) {
        allPorts.add(port);
    }
    public void add(Port port, int index) {
        ports[index] = port;
    }
    public Port get(int index, boolean valid) {
        if(valid){
            return ports[index];
        } else{
            return allPorts.get(index);
        }
    }
    public void clear(){
        for(int i = 0; i < allPorts.size(); i++){
            allPorts.get(i).setBiome(Tile.OCEAN);
        }
    }
    public IIterator<Port> getAllPortIterator(){
        return new AllPortIterator();
    }
    public IIterator<Port> getValidPortIterator(){
        return new ValidPortIterator();
    }
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
