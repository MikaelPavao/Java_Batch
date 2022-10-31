package br.com.app.service;

import br.com.app.entity.Cobranca;
import br.com.app.entity.Pagamento;
import br.com.app.repository.CobrancaRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class BatchTask implements Tasklet {


    List<Cobranca> cobrancaList = new ArrayList<>();

    List<Pagamento> pagamentoList = new ArrayList<>();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        return RepeatStatus.FINISHED;
    }

    public void tranformarCobranca(EntityManager entityManager) {
        cobrancaList = new ArrayList<>();
        cobrancaList.forEach(cobranca ->
        {
            Pagamento pagamento = new Pagamento();
            pagamento.setTotalPago(cobranca.getValor());
            pagamento.setNome(cobranca.getNome());
            pagamentoList.add(pagamento);
        });

        System.out.println(pagamentoList);

        for (Pagamento pagamento : pagamentoList) {
            entityManager.persist(pagamento);
        }
    }

    public void importarCobranca(EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Cobranca> query = criteriaBuilder.createQuery(Cobranca.class);

        Root<Cobranca> rootCobranca = query.from(Cobranca.class);

        query.select(rootCobranca);

        cobrancaList.addAll(entityManager.createQuery(query).getResultList());

        tranformarCobranca(entityManager);
    }
}
