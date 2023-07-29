import org.junit.Test;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class CinemaTicketsTest {
    @Test
    public void purchaseTicketsTest(){
        var ticketService = new TicketServiceImpl();

        var infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        var childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        var adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        ticketService.purchaseTickets(1L, infantTicket, childTicket);
    }
}
