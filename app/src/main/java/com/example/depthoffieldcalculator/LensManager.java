package com.example.depthoffieldcalculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LensManager implements Iterable<Lens> {

    private List<Lens> lensList = new ArrayList<>();

    // Singleton support
    private static LensManager instance;

    private LensManager() {
        //Private to prevent anyone else from instantiating

        lensList.add(new Lens("Canon", 1.8, 50));
        lensList.add(new Lens("Tamron", 2.8, 90));
        lensList.add(new Lens("Sigma", 2.8, 200));
        lensList.add(new Lens("Nikon", 4, 200));
    }

    public static LensManager getInstance(){
        if (instance == null){
            instance = new LensManager();
        }
        return instance;
    }


    public void add(Lens theLens){
        lensList.add(theLens);
    }

    public Lens getValue (int index){
        return lensList.get(index);
    }

    public int listSize (){
        return lensList.size();
    }

    public void remove(Lens lens){
        lensList.remove(lens);
    }

    @Override
    public Iterator<Lens> iterator() {
        return lensList.iterator();
    }
}
