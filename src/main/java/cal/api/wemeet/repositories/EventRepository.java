package cal.api.wemeet.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import cal.api.wemeet.models.Event;

public interface EventRepository extends MongoRepository<Event, String> {
    
}
