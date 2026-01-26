package com.example.avakids_backend.repository.User;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private static final QUser user = QUser.user;

    @Override
    public Long countNewUsers(LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .select(user.count())
                .from(user)
                .where(user.createdAt.between(startDate, endDate))
                .fetchOne();
    }
}
