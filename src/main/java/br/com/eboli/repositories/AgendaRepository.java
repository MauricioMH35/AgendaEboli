package br.com.eboli.repositories;

import br.com.eboli.models.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Integer> {

    List<Agenda> findByTitleContains(String title);

    List<Agenda> findByMarkedTo(LocalDateTime markedTo);

    List<Agenda> findByMarkedToBetween(LocalDateTime start, LocalDateTime end);

    List<Agenda> findByConcluded(Boolean concluded);

    @Modifying
    @Query("SELECT a FROM Agenda a WHERE a.customer.id=?1")
    List<Agenda> findByCustomerId(Integer customerId);

    @Modifying
    @Query("UPDATE Agenda a SET a.concluded=true WHERE a.id=?1")
    void markConcluded(Integer id);

}
