package br.com.app.config;

import br.com.app.repository.CobrancaRepository;
import br.com.app.service.BatchTask;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Configuration
@Transactional
@RequiredArgsConstructor
public class ConfigBatchProcess {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @PersistenceContext
    private final EntityManager entityManager;

    @Bean
    public BatchTask taskImportarCobranca() {
        BatchTask batchTask = new BatchTask();

        batchTask.importarCobranca(entityManager);
        return batchTask;
    }

    @Bean
    public Job jobProcessarCobranca() {
        return jobBuilderFactory.get("processarDados")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("ImportarDadosCobranca")
                .tasklet(taskImportarCobranca())
                .build();
    }

}
