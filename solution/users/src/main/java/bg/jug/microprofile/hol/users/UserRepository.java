package bg.jug.microprofile.hol.users;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metric;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserRepository {

    private Map<String, User> users = new ConcurrentHashMap<>();

    @PostConstruct
    public void initUsers() {
        users.put("bilbo@example.org", new User("Bilbo", "Baggins",
                "bilbo@example.org", "bilbo123", "admin"));
        users.put("frodo@example.org", new User("Frodo", "Baggins",
                "frodo@example.org", "frodo123", "author", "subscriber"));
        users.put("gandalf@example.org", new User("Gandalf", "the Grey",
                "gandalf@example.org", "gandalf123", "author"));
        users.put("aragorn@example.org", new User("Aragorn", "son of Aratorn",
                "aragorn@example.org", "aragorn123", "subscriber"));
        users.put("legolas@example.org", new User("Legolas", "son of Thranduil",
                "aragorn@example.org", "aragorn123", "subscriber"));
        users.put("gimli@example.org", new User("Gimli", "son of Gloin",
                "gimli@example.org", "gimli123"));
    }

    public Optional<User> findByLoginDetails(String email, String password) {
        User user = users.get(email);
        if (user == null || !user.getPassword().equals(password)) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }
    }

    public void createOrUpdate(User user) {
        users.put(user.getEmail(), user);
    }

    public boolean addRole(String email, String newRole) {
        User user = users.get(email);
        if (user == null) {
            return false;
        }
        user.getRoles().add(newRole);
        return true;
    }

    public Collection<User> getAll() {
        return users.values();
    }

    /* Metrics */

    //FIXME: uncomment in pom.xml
    @Produces
    @Metric(name="authorsPercentage")
    @ApplicationScoped
    private Gauge<Double> authorsPercentage = new Gauge<Double>() {

        @Override
        public Double getValue() {
            long authors = users.values().stream().filter(u -> u.getRoles().contains("author")).count();
            return Double.valueOf(authors / users.size());
        }
    };
}
