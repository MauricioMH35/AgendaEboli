package br.com.eboli.services;

import br.com.eboli.models.requests.AgendaRequest;

import java.util.List;

public interface AgendaService {

    AgendaRequest save(AgendaRequest request);

    AgendaRequest findById(Integer id);

    List<AgendaRequest> findAll();

    List<AgendaRequest> findByTitleContains(String title);

    List<AgendaRequest> findByMarkedTo(String markedTo);

    List<AgendaRequest> findByMarkedToBetween(String markedStart, String markedEnd);

    List<AgendaRequest> findByConcluded(String concluded);

    AgendaRequest markedConcluded(Integer id);

    AgendaRequest updateById(Integer id, AgendaRequest request);

    void deleteById(Integer id);

}
