package com.stackoverflow.repository;

import com.stackoverflow.model.UserGroup;
import com.stackoverflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findByCreator(User creator);
    List<UserGroup> findByMembersContaining(User member);
    boolean existsByNameAndCreator(String name, User creator);
}