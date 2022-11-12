package cal.api.wemeet.models.dto.response;

import java.util.Date;
import java.util.List;

import cal.api.wemeet.models.EventState;

public class EventDto {

    private String id;
    private String title;
    private Date date;
    private Date time;
    private EventState state = EventState.COMING;
    private String Address;
    private String city;
    private String postalCode;
    private String country;
    private double price;
    private String description;
    private UserDto organizer;
    private List<UserDto> co_organizers;
    private List<UserDto> participants;
    private boolean isPublic;
    private int maxParticipants;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public EventState getState() {
        return state;
    }

    public void setState(EventState state) {
        this.state = state;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDto getOrganizer() {
        return organizer;
    }

    public void setOrganizer(UserDto organizer) {
        this.organizer = organizer;
    }

    public List<UserDto> getCo_organizers() {
        return co_organizers;
    }

    public void setCo_organizers(List<UserDto> co_organizers) {
        this.co_organizers = co_organizers;
    }

    public List<UserDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserDto> participants) {
        this.participants = participants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

}
