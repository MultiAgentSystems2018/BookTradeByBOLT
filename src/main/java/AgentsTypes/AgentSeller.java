package AgentsTypes;

import ETC.BehaviourKiller;
import ETC.Book;
import ETC.BookTitle;
import SellerBehaviours.WaitingForRequest;
import jade.core.Agent;
import jade.core.behaviours.DataStore;

import java.util.ArrayList;
import java.util.List;

import static ETC.Colours.RED;
import static ETC.Colours.ZERO;

public class AgentSeller extends Agent {
    private List<Book> bookList;

    @Override
    protected void setup(){
        super.setup();
        bookList = new ArrayList<Book>();
        createSettingForSeller(bookList);
        DataStore ds = new DataStore();
        ds.put("bookList", bookList);
        WaitingForRequest behaviour = new WaitingForRequest(this, ds);
        addBehaviour(behaviour);
        addBehaviour(new BehaviourKiller(this, 5000, behaviour));
    }

    private void createSettingForSeller (List<Book>  bookList) {
        if (this.getLocalName().equals("Seller1")){
            bookList.add(new Book(BookTitle.WardN06, 130));
            bookList.add(new Book(BookTitle.CrimeAndPunishment,290));
        }
        else if (this.getLocalName().equals("Seller2")){
            bookList.add(new Book(BookTitle.CrimeAndPunishment, 285));
            bookList.add(new Book(BookTitle.WarAndPeace, 1300));
            bookList.add(new Book(BookTitle.TheTaleofGoldenChicken, 250));
        }
        else if (this.getLocalName().equals("Seller3")){
            bookList.add(new Book(BookTitle.WardN06, 150));
            bookList.add(new Book(BookTitle.WarAndPeace, 1260));
        }
        else{
            System.err.println(RED + "Danger! Wrong Agent name: " + this.getLocalName() + ZERO);
        }

    }
}
