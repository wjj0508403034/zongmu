package com.zongmu.service.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import com.zongmu.service.dto.reviewrecord.ReviewRecordStatus;
import com.zongmu.service.dto.search.ReviewRecordSearchParam;
import com.zongmu.service.dto.user.BusinessRole;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.User;

public class ReviewRecordSpec {

	public Specification<ReviewRecord> search(final ReviewRecordSearchParam params, final User user) {
		return new Specification<ReviewRecord>() {
			@Override
			public Predicate toPredicate(Root<ReviewRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if (user.getBusinessRole() != BusinessRole.ADMIN) {
					predicates.add(userPredicate(root, query, cb, user.getId()));
				}

				predicates.add(TaskItemSpec.taskNamePredicate(root, query, cb, params.getTaskName()));
				predicates.add(TaskItemSpec.taskItemNoPredicate(root, query, cb, params.getTaskItemNo()));
				predicates.add(AssetSpec.assetNamePredicate(root, query, cb, params.getAssetName()));
				predicates.add(AssetSpec.assetNoPredicate(root, query, cb, params.getAssetNo()));
				predicates.add(AssetSpec.assetTypesPredicate(root, query, cb, params.getAssetTypes()));
				predicates.add(AlgorithmSpec.algorithmPredicate(root, query, cb, params.getAlgorithmIds()));
				predicates.add(TaskRecordSpec.pointPredicate(root, query, cb, params.getPoint()));
				predicates.add(AssetSpec.videoLengthPredicate(root, query, cb, params.getVideoLength()));
				predicates.add(reviewRecordStatusPredicate(root, query, cb, params.getStatus()));
				predicates.add(TaskItemSpec.taskCreateDatePredicate(root, query, cb, params.getCreateDate()));
				predicates.add(AssetSpec.assetUpdateDatePredicate(root, query, cb, params.getUploadDate()));
				List<Predicate> taskFinishDataPredicates = TaskItemSpec.taskFinishDatePredicate(root, query, cb,
						params.getTaskFinishDate());
				if (taskFinishDataPredicates != null && taskFinishDataPredicates.size() > 0) {
					predicates.addAll(taskFinishDataPredicates);
				}
				predicates.add(AssetSpec.assetRecordDatePredicate(root, query, cb, params.getVideoRecordDate()));
				predicates.add(reasonPredicate(root, query, cb, params.getReasonIds()));
				predicates.add(UserSpec.userPredicate(root, query, cb, params.getUserName()));
				predicates.add(TaskItemSpec.taskViewTagIdsPredicate(root, query, cb, params.getViewTagItemIds()));
				predicates.add(AssetSpec.assetViewTagIdsPredicate(root, query, cb, params.getAssetViewTagItemIds()));
				
				
				List<Predicate> notNullPredicates = new ArrayList<>();
				for (Predicate predicate : predicates) {
					if (predicate != null) {
						notNullPredicates.add(predicate);
					}
				}

				List<Order> orderList = new ArrayList<>();
				query = query.where(toPredicateArray(notNullPredicates)).orderBy(orderList);

				return query.getRestriction();
			}
		};
	}

	private Predicate userPredicate(Root<ReviewRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long userId) {
		return cb.equal(root.get("userId").as(Long.class), userId);
	}

	private Predicate reviewRecordStatusPredicate(Root<ReviewRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<ReviewRecordStatus> statusList) {
		if (statusList.size() == 0) {
			return null;
		}

		return root.get("status").as(ReviewRecordStatus.class).in(statusList);
	}

	private static Predicate reasonPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> reasonIds) {
		if (reasonIds.size() == 0) {
			return null;
		}

		return root.get("reasonId").as(Long.class).in(reasonIds);
	}

	private static Predicate[] toPredicateArray(List<Predicate> list) {
		Predicate[] array = new Predicate[list.size()];
		list.toArray(array);
		return array;
	}
}
