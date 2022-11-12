package cal.api.wemeet.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import cal.api.wemeet.models.Event;

public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findByIsPublic(boolean isPublic);
    List<Event> findByOrganizerId(String organizerId);
    
}
