package cal.api.wemeet.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cal.api.wemeet.models.Event;
import cal.api.wemeet.models.User;
import cal.api.wemeet.models.dto.request.EventCreationEntry;
import cal.api.wemeet.repositories.EventRepository;
import cal.api.wemeet.repositories.UserRepository;

@Service
public class EventService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    EventRepository eventRepo;

    @Autowired
    UserService userService;

    public Event getEventFromEventEntry(EventCreationEntry entry) {
        Event event = new Event();
        event.setDate(entry.getDate());
        Calendar cal = Calendar.getInstance();
        cal.setTime(entry.getDate());
        LocalDateTime localDateTime = entry.getTime().atDate(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
        localDateTime = localDateTime.plusHours(2);
        Instant instant = localDateTime.atZone(ZoneId.of("Europe/Paris")).toInstant();
        Date date = Date.from(instant);
        event.setTime(date);
        event.setAddress(entry.getAddress());
        event.setTitle(entry.getTitle());
        event.setCity(entry.getCity());
        event.setPostalCode(entry.getPostalCode());
        event.setCountry(entry.getCountry());
        event.setPrice(entry.getPrice());
        event.setDescription(entry.getDescription());
        event.setMaxParticipants(entry.getMaxParticipants());
        return event;
    }

    public void setEventOrganizer(Event event) {
        event.setOrganizer(userService.getAuthenticatedUser());
    }

    public void saveEvent(Event event) {
        eventRepo.save(event);
    }

    public Event getEventById(String id) {
        if (! eventRepo.findById(id).isPresent()) {
            return null;
        } else {
            return eventRepo.findById(id).get();
        }
    }

    public boolean isOrganizer(Event event, User user){
        System.out.println("event.getOrganizer().getId() : " + event.getOrganizer().getId());
        System.out.println("user.getId() : " + user.getId());
        return user.getId().contentEquals(event.getOrganizer().getId());
    }

    public boolean isCoOrganizer(Event event, User user){
        return event.getCo_organizers().contains(user);
    }
}
