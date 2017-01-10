package com.zongmu.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.RequestPay;
import com.zongmu.service.point.PayStatus;

@Repository
public interface RequestPayRepository extends PagingAndSortingRepository<RequestPay, Long> {

	@Query("select t from RequestPay t where t.status = ?1")
	Page<RequestPay> getList(PayStatus status, Pageable pageable);
	
	@Query("select count(t) > 0 from RequestPay t where t.transcationNo = ?1")
	boolean isDup(String transcationNo);
}
