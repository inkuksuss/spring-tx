package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager transactionManager;

    @TestConfiguration
    static class config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("start");
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("commit start");
         transactionManager.commit(status);
        log.info("commit end");
    }

    @Test
    void rollback() {
        log.info("start");
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("rollback start");
        transactionManager.rollback(status);
        log.info("rollback end");
    }

    @Test
    void double_commit() {
        log.info("tx1 start");
        TransactionStatus status1 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("commit start1");
        transactionManager.commit(status1);

        log.info("tx2 start");
        TransactionStatus status2 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("commit start2");
        transactionManager.commit(status2);
    }

    @Test
    void double_commit_rollback() {
        log.info("tx1 start");
        TransactionStatus status1 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("commit start1");
        transactionManager.commit(status1);

        log.info("tx2 start");
        TransactionStatus status2 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("rollback start2");
        transactionManager.rollback(status2);
    }

    @Test
    void inner_commit() {
        log.info("outer start");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer is new tx={}", outer.isNewTransaction());

        log.info("inner start");
        TransactionStatus inner = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner is new tx={}", inner.isNewTransaction());
        log.info("inner commit");
        transactionManager.commit(inner);

        log.info("outer commit");
        transactionManager.commit(outer);
    }

    @Test
    void outer_rollback() {
        log.info("outer start");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("inner start");
        TransactionStatus inner = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner commit");
        transactionManager.commit(inner);

        log.info("outer rollback");
        transactionManager.rollback(outer);
    }

    @Test
    void inner_rollback() {
        log.info("outer start");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("inner start");
        TransactionStatus inner = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner rollback");
        transactionManager.rollback(inner);

        log.info("outer commit");

        Assertions.assertThatThrownBy(() -> transactionManager.commit(outer)).isInstanceOf(UnexpectedRollbackException.class);
    }

    @Test
    void inner_rollback_requires_new() {
        log.info("outer start");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer is new={}", outer.isNewTransaction());

        log.info("inner start");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = transactionManager.getTransaction(definition);
        log.info("inner is new={}", inner.isNewTransaction());

        log.info("inner rollback");
        transactionManager.rollback(inner);

        log.info("outer commit");
        transactionManager.commit(outer);
    }
}
