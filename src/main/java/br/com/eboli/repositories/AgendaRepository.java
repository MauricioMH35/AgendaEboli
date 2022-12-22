package br.com.eboli.repositories;

import br.com.eboli.models.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByTitleContains(String title);

    List<Agenda> findByMarkedTo(LocalDateTime markedTo);

    List<Agenda> findByMarkedToBetween(LocalDateTime start, LocalDateTime end);

    List<Agenda> findByConcluded(Boolean concluded);

    @Modifying
    @Query("UPDATE Agenda a SET a.concluded=true WHERE a.id=?1")
    void markConcluded(Long id);

}
