package my.project.hallreservation.repositories;

import my.project.hallreservation.domain.Hall;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallRepository extends CrudRepository<Hall, Long> {
    List<Hall> findAll();
}
