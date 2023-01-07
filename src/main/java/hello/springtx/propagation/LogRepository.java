package hello.springtx.propagation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {

        private final EntityManager em;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void save(Log logMessage) {
                log.info("log save");
                em.persist(logMessage);

                if (logMessage.getMessage().contains("로그 예외")) {
                        log.info("log exception");
                        throw new RuntimeException("예외 발생");
                }
        }

        public Optional<Log> find(String logMessage) {
                return em.createQuery("select l from Log l where l.message = :logMessage", Log.class)
                        .setParameter("logMessage", logMessage)
                        .getResultList().stream().findAny();
        }
}
