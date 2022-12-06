package cal.api.wemeet.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.validation.Valid;

import cal.api.wemeet.models.Event;
import org.json.JSONArray;
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
import org.springframework.web.client.RestTemplate;


import cal.api.wemeet.models.EventState;
import cal.api.wemeet.models.User;
import cal.api.wemeet.models.dto.request.EventCreationEntry;
import cal.api.wemeet.models.dto.response.SimpleResponse;
import cal.api.wemeet.services.EmailSenderService;
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

    @Autowired
    EmailSenderService emailSenderService;
    @GetMapping
    public ResponseEntity<?> allPublicEvents() {
        return ResponseEntity.ok().body(eventService.getAllPublicEvents());
    }

    @GetMapping("/mine")
    public ResponseEntity<?> allMyEvents() {
        return ResponseEntity.ok().body(eventService.getAllUserEvents());
    }

    @GetMapping("/participations")
    public ResponseEntity<?> allMyEventParticipations() {
        return ResponseEntity.ok().body(eventService.getAllUserEventParticipations());
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

    @PostMapping("/participate/{id}")
    public ResponseEntity<?> participate(@PathVariable String id) {
        Event event = eventService.getEventById(id);
        if (event == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The event is not found !");
        }
        if (event.getState() != EventState.COMING){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The event is cancelled or already finished !");
        }
        if (eventService.isAlreadyParticipating(event, userService.getAuthenticatedUser())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("You are already participating to this event !");
        }
        User user = userService.getAuthenticatedUser();
        event.getParticipants().add(user);
        eventService.saveEvent(event);
        user.getEvents().add(event);
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SimpleResponse("You are now participating to this event !")) ;
    }

    @PostMapping("/create")
    public ResponseEntity<?> registerEvent(@Valid @RequestBody EventCreationEntry entry) {        
        Event event = eventService.getEventFromEventEntry(entry);
        if (event.getDate().before(new Date())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponse("Event date cannot be in the past"));
        }
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://nominatim.openstreetmap.org/search?q="+ event.getAddress() + "&format=json";
        String result = restTemplate.getForObject(url, String.class);
        JSONArray json = new JSONArray(result);
        if(json.length() != 0) {
            event.setLatitude(json.getJSONObject(0).getDouble("lat"));
            event.setLongitude(json.getJSONObject(0).getDouble("lon"));            
        } else {
            event.setLatitude(0.0);
            event.setLongitude(0.0);
        }
        eventService.setEventOrganizer(event);
        eventService.saveEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse("Event created successfully!"));
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
            existEvent.setDate(eventService.buildDate(entry));
            if (existEvent.getDate().before(new Date())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponse("Event date cannot be in the past"));
            }
            existEvent.setAddress(entry.getAddress());
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://nominatim.openstreetmap.org/search?q=" + existEvent.getAddress() + "&format=json";
            String result = restTemplate.getForObject(url, String.class);
            JSONArray json = new JSONArray(result);
            if (json.length() != 0) {
                existEvent.setLatitude(json.getJSONObject(0).getDouble("lat"));
                existEvent.setLongitude(json.getJSONObject(0).getDouble("lon"));
            } else {
                existEvent.setLatitude(0.0);
                existEvent.setLongitude(0.0);
            }
            existEvent.setTitle(entry.getTitle());
            existEvent.setCity(entry.getCity());
            existEvent.setPostalCode(entry.getPostalCode());
            existEvent.setCountry(entry.getCountry());
            existEvent.setPrice(entry.getPrice());
            existEvent.setDescription(entry.getDescription());
            existEvent.setMaxParticipants(entry.getMaxParticipants());
            existEvent.setIsPublic(entry.getIsPublic());

            eventService.saveEvent(existEvent);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(existEvent);

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new SimpleResponse("You are not allowed to edit this event!"));
    }

    @PostMapping("/invite/{id}")
    public ResponseEntity<?> inviteUser(@PathVariable("id") String id, @RequestBody String email) {
        System.out.println("ENTRY : " + email);
        Event event = eventService.getEventById(id);
        if (event == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SimpleResponse("The event to cancel is not found!"));
        }

        email = URLDecoder.decode(email, StandardCharsets.UTF_8);

        System.out.println("RESULT : " + email);
        User userInvited = userService.getUserByEmail(email);

        if (userInvited == null){
            emailSenderService.sendEmail(email, "Invitation to an event", "You have been invited to an event by " + userService.getAuthenticatedUser().getFirstName() + " " + userService.getAuthenticatedUser().getLastName() + ". Please register to our website to participate to this event with the following link : http://localhost:3000/events/" + event.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SimpleResponse("The user to invite is not found! we sent him an email to join us!"));
        }

        User userOrganizer = userService.getAuthenticatedUser();
        if (eventService.isOrganizer(event, userOrganizer) || eventService.isCoOrganizer(event, userOrganizer)){
            if (eventService.isAlreadyParticipating(event, userInvited)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new SimpleResponse("The user is already participating to this event!"));
            }
            event.getParticipants().add(userInvited);
            eventService.saveEvent(event);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new SimpleResponse("User invited successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SimpleResponse("You are not allowed to invite users to this event!"));
        }
    }
}
