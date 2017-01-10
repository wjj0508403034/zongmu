package com.zongmu.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.dto.user.BusinessRole;
import com.zongmu.service.entity.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	@Query("select count(t) from User t where t.email = ?1")
	Long countByEmail(String email);

	@Query("select count(t) from User t where t.email = ?1 and t.password = ?2")
	Long countByEmailAndPassword(String email, String password);

	@Query("select t from User t where t.email = ?1")
	User findByEmail(String email);

	@Query("select count(t) > 0 from User t where t.email = ?1")
	boolean exists(String email);

	@Query("select t from User t where t.businessRole = ?1 and t.black <> 1 and t.active = 1")
	Page<User> getList(BusinessRole role, Pageable pageable);

	@Query("select t from User t where t.black <> 1 and t.active = 1")
	Page<User> getList(Pageable pageable);

	@Query("select t from User t where t.black = 1")
	Page<User> getBlackList(Pageable pageable);
}
