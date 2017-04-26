package spuzi.atenea.Common;

import java.util.LinkedList;

/**
 * Created by spuzi on 09/03/2017.
 */

public class Buffer {
    private LinkedList<Object> fifo;//una fifo FIFO (Primero que entra, primero que sale)
    private int MAX_NUMBER_ELEMENTS; //maximo número de objetos que podemos guardar en el buffer

    public Buffer(){
        fifo = new LinkedList<>();
        MAX_NUMBER_ELEMENTS = 1;
    }

    public Buffer(int numeroDeElementos){
        fifo = new LinkedList<>();
        MAX_NUMBER_ELEMENTS = numeroDeElementos;
    }

    public Object pollFirst(){
        synchronized ( fifo ){
            return ( size()>0 ? fifo.pollFirst() : null);
        }
    }

    public Object pollLast(){
        synchronized ( fifo ){
            return ( size()>0 ? fifo.pollLast() : null);
        }
    }

    public Object getFirst(){
        synchronized ( fifo ){
            return ( size()>0 ? fifo.getFirst() : null);
        }
    }

    public Object getLast(){
        synchronized ( fifo ){
            if(hasElements()){
                return fifo.getLast();
            }
            return null;
        }
    }

    public Object getElement(int i ){
        synchronized ( fifo ){
            return fifo.get( i );
        }
    }

    public void reset(){
        synchronized ( fifo ){
            if( fifo != null)
                fifo.clear();
        }
    }

    public void add(Object newObject){
        synchronized ( fifo ){
            //If maximum number of elements has been reached then delete first and add a at end of the fifo (FIFO)
            if(size() == MAX_NUMBER_ELEMENTS )
                fifo.poll();
            fifo.add( newObject );
        }
    }

    public int size(){
        synchronized ( fifo ){
            return ( fifo != null ? fifo.size() : 0);
        }
    }

    public boolean hasElements(){
        return (size() > 0 ? true : false);
    }


}
