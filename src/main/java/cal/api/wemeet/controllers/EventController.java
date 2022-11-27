package cal.api.wemeet.controllers;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cal.api.wemeet.models.Event;
import cal.api.wemeet.models.EventState;
import cal.api.wemeet.models.User;
import cal.api.wemeet.models.dto.request.EventCreationEntry;
import cal.api.wemeet.models.dto.response.SimpleResponse;
import cal.api.wemeet.services.EventService;
import cal.api.wemeet.services.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> allPublicEvents() {
        return ResponseEntity.ok().body(eventService.getAllPublicEvents());
    }

    @GetMapping("/mine")
    public ResponseEntity<?> allMyEvents() {
        return ResponseEntity.ok().body(eventService.getAllUserEvents());
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) {

        Event event = eventService.getEventById(id);
        if (event == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Event not exist !");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(event) ;
    }


    
    @PostMapping("/create")
    public ResponseEntity<?> registerEvent(@Valid @RequestBody EventCreationEntry entry) {
        
        Event event = eventService.getEventFromEventEntry(entry);
        if (event.getDate().before(new Date())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponse("Event date cannot be in the past"));
        }
        eventService.setEventOrganizer(event);
        eventService.saveEvent(event);

        return ResponseEntity.status(HttpStatus.CREATED)
        .body(new SimpleResponse("Event created successfully!"));
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelEvent(@PathVariable("id") String id) {
        Event event = eventService.getEventById(id);
        if (event == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new SimpleResponse("The event to cancel is not found!"));
        }

        if (event.getState() == EventState.DONE){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new SimpleResponse("The event is already done!"));
        }

        User user = userService.getAuthenticatedUser();
        if (eventService.isOrganizer(event, user) || eventService.isCoOrganizer(event, user)){
            event.setState(EventState.CANCELLED);
            eventService.saveEvent(event);
            return ResponseEntity.status(HttpStatus.OK)
            .body(new SimpleResponse("Event cancelled successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new SimpleResponse("You are not allowed to cancel this event!"));
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editEvent(@RequestBody EventCreationEntry entry, @PathVariable("id") String id) {

        Event existEvent = eventService.getEventById(id);
        if (existEvent == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SimpleResponse("The event to cancel is not found!"));
        }

        User user = userService.getAuthenticatedUser();

        if (eventService.isOrganizer(existEvent, user) || eventService.isCoOrganizer(existEvent, user)){
            Event event = eventService.getEventFromEventEntry(entry);
            if (event.getDate().before(new Date())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponse("Event date cannot be in the past"));
            }

            event.setDate(eventService.buildDate(entry));
            event.setAddress(entry.getAddress());
            event.setTitle(entry.getTitle());
            event.setCity(entry.getCity());
            event.setPostalCode(entry.getPostalCode());
            event.setCountry(entry.getCountry());
            event.setPrice(entry.getPrice());
            event.setDescription(entry.getDescription());
            event.setMaxParticipants(entry.getMaxParticipants());
            event.setIsPublic(entry.getIsPublic());

            eventService.saveEvent(event);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(event);

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new SimpleResponse("You are not allowed to edit this event!"));
    }


}
