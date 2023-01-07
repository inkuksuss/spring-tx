package hello.springtx.propagation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    @Transactional
    public void joinV1(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("memberRepository start");
        memberRepository.save(member);
        log.info("memberRepository end");

        log.info("logRepository start");
        logRepository.save(logMessage);
        log.info("logRepository end");
    }

    @Transactional
    public void joinV2(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("memberRepository start");
        memberRepository.save(member);
        log.info("memberRepository end");

        log.info("logRepository start");
        try {
            logRepository.save(logMessage);
        } catch (RuntimeException e) {
            log.info("log save fail, logMsg = {}",logMessage.getMessage());
            log.info("정상 흐름 반환");
        }
        log.info("logRepository end");
    }
}
