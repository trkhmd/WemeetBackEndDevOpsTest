package cal.api.wemeet.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import cal.api.wemeet.models.User;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
    User findByEmail(String email);

}
