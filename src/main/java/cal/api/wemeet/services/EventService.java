package cal.api.wemeet.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cal.api.wemeet.models.Event;
import cal.api.wemeet.models.User;
import cal.api.wemeet.models.dto.request.EventCreationEntry;
import cal.api.wemeet.models.dto.response.EventDto;
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

    public List<Event> getAllEvents(){
        return eventRepo.findAll();
    }

    public List<EventDto> getAllPublicEvents() {
        return eventRepo.findByIsPublic(true)
                        .stream()
                        .map(event -> converEventToEventDto(event))
                        .collect(Collectors.toList());
    }

    public List<EventDto> getAllUserEvents() {
        return eventRepo.findByOrganizerId(userService.getAuthenticatedUser().getId())
                        .stream()
                        .map(event -> converEventToEventDto(event))
                        .collect(Collectors.toList());
    }

    public EventDto converEventToEventDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setDescription(event.getDescription());
        eventDto.setDate(event.getDate());
        eventDto.setIsPublic(event.getIsPublic());
        eventDto.setOrganizer(userService.convertUserToUserDto(event.getOrganizer()));
        eventDto.setParticipants(event.getParticipants()
                                    .stream()
                                    .map(user -> userService.convertUserToUserDto(user))
                                    .collect(Collectors.toList()));
        eventDto.setCo_organizers(event.getCo_organizers()
                                    .stream()
                                    .map(user -> userService.convertUserToUserDto(user))
                                    .collect(Collectors.toList()));
        eventDto.setAddress(event.getAddress());
        eventDto.setCountry(event.getCountry());
        eventDto.setCity(event.getCity());
        eventDto.setState(event.getState());
        eventDto.setPostalCode(event.getPostalCode());
        return eventDto;
    }

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
