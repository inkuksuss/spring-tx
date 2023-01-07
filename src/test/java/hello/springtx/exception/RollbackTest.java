package hello.springtx.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService rollbackService;

    @Test
    void runtimeEx() {
        Assertions.assertThatThrownBy(() ->rollbackService.runtimeEx()).isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkedEx() {
        Assertions.assertThatThrownBy(() ->rollbackService.checkedEx()).isInstanceOf(MyException.class);
    }

    @Test
    void rollbackFor() {
        Assertions.assertThatThrownBy(() -> rollbackService.rollbackFor()).isInstanceOf(MyException.class);
    }

    @TestConfiguration
    static class RollbackTestConfig {

        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    static class RollbackService {

        @Transactional
        public void runtimeEx() {
            log.info("call runtimeEx");
            throw new RuntimeException();
        }

        @Transactional
        public void checkedEx() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            throw new MyException();
        }
    }

    static class MyException extends Exception {}
}
