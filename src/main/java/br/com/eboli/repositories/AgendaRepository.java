package br.com.eboli.repositories;

import br.com.eboli.models.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByTitleContains(String title);

    List<Agenda> findByMarkedTo(LocalDateTime markedTo);

    List<Agenda> findByMarkedToBetween(LocalDateTime start, LocalDateTime end);

    List<Agenda> findByConcluded(Boolean concluded);

}
