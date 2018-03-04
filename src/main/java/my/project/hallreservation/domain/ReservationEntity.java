package my.project.hallreservation.domain;

public class ReservationEntity {

    private Customer customer;
    private Hall hall;
    private BadDate startDate;
    private BadDate endDate;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public BadDate getStartDate() {
        return startDate;
    }

    public void setStartDate(BadDate startDate) {
        this.startDate = startDate;
    }

    public BadDate getEndDate() {
        return endDate;
    }

    public void setEndDate(BadDate endDate) {
        this.endDate = endDate;
    }
}
