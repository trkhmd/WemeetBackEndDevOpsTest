package cal.api.wemeet.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cal.api.wemeet.models.Event;
import cal.api.wemeet.models.dto.request.EventCreationEntry;
import cal.api.wemeet.models.dto.response.SimpleResponse;
import cal.api.wemeet.services.EventService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    EventService eventService;

    @GetMapping
    public String all() {
        return "all";
    }

    
    @PostMapping("/create")
    public ResponseEntity<?> registerUser(@Valid @RequestBody EventCreationEntry entry) {

        Event event = eventService.getEventFromEventEntry(entry);
        eventService.setEventOrganizer(event);
        eventService.saveEvent(event);

        return ResponseEntity.status(HttpStatus.CREATED)
        .body(new SimpleResponse(HttpStatus.CREATED,"Event created successfully!"));

    }
}
