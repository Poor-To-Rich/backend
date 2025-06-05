package com.poortorich.user.service;

import com.poortorich.accountbook.repository.AccountBookRepository;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.repository.CategoryRepository;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.response.enums.GlobalResponse;
import com.poortorich.iteration.repository.IterationRepository;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserResetService {

    private final UserRepository userRepository;
    private final IterationRepository iterationRepository;
    private final AccountBookRepository accountBookRepository;
    private final CategoryRepository categoryRepository;

    public void deleteUserAllData(User user) {
        log.info("Starting Date Reset");
        log.info("데이터 삭제는 DB 제약 사항 순서에 맞추어 진행됩니다.");
        try {
            log.info("[user ID: {}] 반복 데이터 삭제", user.getId());
            iterationRepository.deleteAllIterationByUser(user);

            log.info("[user Id: {}] 지출/수입 데이터 삭제", user.getId());
            accountBookRepository.deleteAllAccountBooksByUser(user);

            log.info("[user ID: {}] 커스텀 카테고리 삭제", user.getId());
            categoryRepository.deleteByUserAndType(user, CategoryType.CUSTOM_EXPENSE);
            categoryRepository.deleteByUserAndType(user, CategoryType.CUSTOM_INCOME);

            log.info("[user ID: {}] 모든 데이터 초기화 성공", user.getId());
        } catch (Exception e) {
            log.error("모든 데이터 초기화 중 예외가 발생: {}", user.getId(), e);
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }
    }
}
