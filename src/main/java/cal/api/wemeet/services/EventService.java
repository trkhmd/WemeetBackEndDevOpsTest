package cal.api.wemeet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cal.api.wemeet.models.Event;
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
        event.setAddress(entry.getAddress());
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
}
