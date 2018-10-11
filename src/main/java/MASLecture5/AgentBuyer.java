package MASLecture5;

import jade.core.Agent;
import jade.core.behaviours.DataStore;

import java.util.ArrayList;
import java.util.List;

public class AgentBuyer  extends Agent {
    private List<Book> buyingBooks;

    @Override
    protected void setup() {
        super.setup();
        buyingBooks = new ArrayList<Book>();
        buyingBooks.add(new Book(BookTitle.CrimeAndPunishment));
        buyingBooks.add(new Book(BookTitle.WarAndPeace));
        buyingBooks.add(new Book(BookTitle.WardN06));

        DataStore ds = new DataStore();
        ds.put("booklist",buyingBooks);
        addBehaviour(new StartOfBuying(this, ds));
    }

}


