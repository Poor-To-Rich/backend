package com.poortorich.user.service;

import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.accountbook.repository.AccountBookRepository;
import com.poortorich.category.entity.enums.CategoryType;
import com.poortorich.category.repository.CategoryRepository;
import com.poortorich.chat.service.ChatroomLeaveService;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.response.enums.GlobalResponse;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.repository.IterationInfoRepository;
import com.poortorich.iteration.util.strategy.IterationStrategyFactory;
import com.poortorich.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserResetService {

    private final ChatroomLeaveService chatroomLeaveService;

    private final IterationStrategyFactory iterationStrategyFactory;
    private final AccountBookRepository accountBookRepository;
    private final CategoryRepository categoryRepository;
    private final IterationInfoRepository iterationInfoRepository;

    public void deleteUserAllData(User user) {
        log.info("Starting Date Reset");
        log.info("데이터 삭제는 DB 제약 사항 순서에 맞추어 진행됩니다.");
        try {
            Set<Long> iterationInfoIds = getDeletedIterationInfo(user);

            log.info("[user ID: {}] 반복 데이터 삭제", user.getId());
            iterationStrategyFactory.getStrategy(AccountBookType.EXPENSE).deleteByUser(user);
            iterationStrategyFactory.getStrategy(AccountBookType.INCOME).deleteByUser(user);

            log.info("[user ID: {}] 지출/수입 데이터 삭제", user.getId());
            accountBookRepository.deleteAllAccountBooksByUser(user);

            log.info("[user ID: {}] 반복 규칙 정보 삭제", user.getId());
            iterationInfoRepository.deleteAllById(iterationInfoIds);

            log.info("[user ID: {}] 커스텀 카테고리 삭제", user.getId());
            categoryRepository.deleteByUserAndType(user, CategoryType.CUSTOM_EXPENSE);
            categoryRepository.deleteByUserAndType(user, CategoryType.CUSTOM_INCOME);

            log.info("[user ID: {}] 모든 데이터 초기화 성공", user.getId());
        } catch (Exception e) {
            log.error("모든 데이터 초기화 중 예외가 발생: {}", user.getId(), e);
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }
    }

    private Set<Long> getDeletedIterationInfo(User user) {
        List<Iteration> iterationExpenses
                = iterationStrategyFactory.getStrategy(AccountBookType.EXPENSE).findAllByUser(user);
        List<Iteration> iterationIncomes
                = iterationStrategyFactory.getStrategy(AccountBookType.INCOME).findAllByUser(user);

        return Stream.concat(iterationExpenses.stream(), iterationIncomes.stream())
                .map(Iteration::getId)
                .collect(Collectors.toSet());
    }

    public void deleteDefaultCategories(User user) {
        try {
            log.info("[user ID: {}] 기본 카테고리 삭제", user.getId());
            categoryRepository.deleteByUserAndType(user, CategoryType.DEFAULT_EXPENSE);
            categoryRepository.deleteByUserAndType(user, CategoryType.DEFAULT_INCOME);
        } catch (Exception e) {
            log.error("모든 데이터 초기화 중 예외가 발생: {}", user.getId(), e);
            throw new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION);
        }
    }

    public void closeChatroom(User user, Boolean isWithdraw) {
        chatroomLeaveService.leaveAllChatroom(user, isWithdraw);
    }
}
