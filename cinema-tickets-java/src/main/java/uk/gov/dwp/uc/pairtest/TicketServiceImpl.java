package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        try {
            if(accountId <= 0 || !validateTicketTypes(ticketTypeRequests))
                throw new InvalidPurchaseException();

            // check ticket count
            var totalTickets = getTotalTickets(ticketTypeRequests);
            if(getTotalTickets() > 20)
                throw new InvalidPurchaseException(); // invalid ticket

            var totalTicketPrices = getTotalTicketPrices(ticketTypeRequests);
            var totalSeats = getTotalSeats(ticketTypeRequests);

            var ticketPaymentService = new TicketPaymentServiceImpl();
            var seatReservationService = new SeatReservationServiceImpl();

            // make payment
            ticketPaymentService.makePayment(accountId, totalTicketPrices);
            seatReservationService.reserveSeat(accountId, totalSeats);

            System.out.println("===============================");
            System.out.println("Total Ticket Prices: £" + totalTicketPrices);
            System.out.println("Total Reserved Seats: " + totalSeats + " seat(s)");
            System.out.println("Total Tickets: " + totalTickets + " ticket(s)");

        }catch (InvalidPurchaseException ex){
            System.out.println("Invalid Purchase!");
        }
    }

    private boolean validateTicketTypes(TicketTypeRequest ...ticketTypeRequests){
        /*
            RULES
          1. Max 20 tickets at a time
          2. Can't buy only Infant and Child Ticket, must come with Adult ticket
          3. Can't allocate seat to Infants

            TRACK
          1. Total ticket price
          2. Total seats reserved

            INVALID
          1. accountId < 1 is invalid & insufficient funds to purchase a ticket
         */

        // check for infant and/or child tickets

        // invalidate where ticketNo is 0

        for(var ticket: ticketTypeRequests){
            if(ticket.getNoOfTickets() <= 0)
                throw new InvalidPurchaseException();
        }

        var infant_child_ticket_exists = Arrays.stream(ticketTypeRequests).anyMatch(ticket ->
                ticket.getTicketType() == TicketTypeRequest.Type.INFANT
                        || ticket.getTicketType() == TicketTypeRequest.Type.CHILD );

        var adult_ticket_exists = Arrays.stream(ticketTypeRequests).anyMatch(ticket ->
                ticket.getTicketType() == TicketTypeRequest.Type.ADULT);

        // verify that an Adult ticket exists
        return !infant_child_ticket_exists || adult_ticket_exists; // invalid ticket
    }

    private int getTicketPrice(TicketTypeRequest ticketTypeRequest){
        switch (ticketTypeRequest.getTicketType()){
            case CHILD:
                return 10 * ticketTypeRequest.getNoOfTickets();
            case ADULT:
                return 20 * ticketTypeRequest.getNoOfTickets();
            case INFANT:
            default:
                return 0;
        }
    }

    private int getTotalTicketPrices(TicketTypeRequest ...ticketTypeRequests){
        // calculate the prices
        var totalTicketPrices = 0;
        for (var ticket : ticketTypeRequests) {
            var ticketPrice = getTicketPrice(ticket);
            totalTicketPrices += ticketPrice;
        }

        return totalTicketPrices;
    }

    private int getTotalSeats(TicketTypeRequest ...ticketTypeRequests){
        var totalSeats = 0;
        for (var ticket : ticketTypeRequests) {
            // infants sit on adults lap
            if(ticket.getTicketType() == TicketTypeRequest.Type.INFANT)
                continue;

            totalSeats += ticket.getNoOfTickets();
        }

        return totalSeats;
    }

    private int getTotalTickets(TicketTypeRequest ...ticketTypeRequests){
        var totalTickets = 0;
        for (var ticket : ticketTypeRequests) {

            totalTickets += ticket.getNoOfTickets();
        }

        return totalTickets;
    }

}
