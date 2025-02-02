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
        private int index;

        public AllPortIterator(){
            this.index = 0;
        }
        @Override
        public boolean hasNext() {
            return index < allPorts.size();
        }
        @Override
        public Port getNext() {
            return allPorts.get(index++);
        }
        @Override
        public void reset() {
            index = 0;
        }
        @Override
        public void setHead() {}
    }
    private class ValidPortIterator implements IIterator<Port>{
        private int index;

        public ValidPortIterator(){
            this.index = 0;
        }
        @Override
        public boolean hasNext() {
            return index < ports.length;
        }
        @Override
        public Port getNext() {
            return ports[index++];
        }
        @Override
        public void reset() {
            index = 0;
        }
        @Override
        public void setHead() {}
    }
}
