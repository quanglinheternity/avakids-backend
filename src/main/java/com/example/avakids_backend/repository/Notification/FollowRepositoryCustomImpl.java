package com.example.avakids_backend.repository.Notification;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.Follow;
import com.example.avakids_backend.entity.QFollow;
import com.example.avakids_backend.enums.FollowTargetType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryCustomImpl implements FollowRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QFollow follow = QFollow.follow;

    @Override
    public List<Follow> findFollowers(FollowTargetType targetType, Long targetId) {

        return queryFactory
                .selectFrom(follow)
                .where(follow.targetType.eq(targetType), follow.targetId.eq(targetId), follow.notify.isTrue())
                .fetch();
    }

    @Override
    public Optional<Follow> findByUserAndTarget(Long userId, FollowTargetType targetType, Long targetId) {

        return Optional.ofNullable(queryFactory
                .selectFrom(follow)
                .where(follow.userId.eq(userId), follow.targetType.eq(targetType), follow.targetId.eq(targetId))
                .fetchOne());
    }
}
