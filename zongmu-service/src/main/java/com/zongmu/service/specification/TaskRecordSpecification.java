package com.zongmu.service.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.reviewrecord.ReviewRecordStatus;
import com.zongmu.service.dto.search.Compare;
import com.zongmu.service.dto.search.DateRange;
import com.zongmu.service.dto.search.Op;
import com.zongmu.service.dto.search.TaskRecordSearchParam;
import com.zongmu.service.dto.taskrecord.TaskRecordStatus;
import com.zongmu.service.dto.user.BusinessRole;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.Task2AssetViewTag;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskItemXViewTag;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.entity.User;

public class TaskRecordSpecification {

	public Specification<TaskRecord> search(final TaskRecordSearchParam params, final User user) {
		return new Specification<TaskRecord>() {
			@Override
			public Predicate toPredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if (user.getBusinessRole() != BusinessRole.ADMIN) {
					predicates.add(userPredicate(root, query, cb, user.getId()));
				}
				predicates.add(taskNamePredicate(root, query, cb, params.getTaskName()));
				predicates.add(taskItemNoPredicate(root, query, cb, params.getTaskItemNo()));
				predicates.add(assetTypePredicate(root, query, cb, params.getAssetTypes()));
				predicates.add(taskRecordStatusPredicate(root, query, cb, params.getTaskRecordStatus()));
				predicates.add(algorithmIdsPredicate(root, query, cb, params.getAlgorithmIds()));
				predicates.add(assetViewTagItemIdsPredicate(root, query, cb, params.getAssetViewTagItemIds()));
				predicates.add(taskTagItemIdsPredicate(root, query, cb, params.getViewTagItemIds()));
				predicates.add(pointPredicate(root, query, cb, params.getPoint()));
				predicates.add(taskCreateDatePredicate(root, query, cb, params.getCreateDate()));
				predicates.add(taskFinishDatePredicate(root, query, cb, params.getTaskFinishDate()));
				predicates.add(assetUploadDatePredicate(root, query, cb, params.getUploadDate()));

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

	private Predicate taskNamePredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String taskName) {
		if (StringUtils.isEmpty(taskName)) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<TaskItem> subRoot = subQuery.from(TaskItem.class);
		subQuery.select(subRoot.get("taskItemNo").as(String.class));

		Predicate like = cb.like(subRoot.get("taskName").as(String.class), "%" + taskName + "%");
		return root.get("taskItemNo").as(String.class).in(subQuery.where(like));
	}

	private Predicate taskItemNoPredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String taskItemNo) {
		if (StringUtils.isEmpty(taskItemNo)) {
			return null;
		}

		return cb.like(root.get("taskItemNo").as(String.class), "%" + taskItemNo + "%");
	}

	private Predicate assetTypePredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<AssetType> assetTypes) {
		if (assetTypes.size() == 0) {
			return null;
		}

		return root.get("assetType").as(AssetType.class).in(assetTypes);
	}

	private Predicate userPredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long userId) {
		return cb.equal(root.get("userId").as(Long.class), userId);
	}

	private Predicate taskRecordStatusPredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<TaskRecordStatus> status) {
		if (status.size() == 0) {
			return null;
		}

		return root.get("status").as(TaskRecordStatus.class).in(status);
	}

	private Predicate algorithmIdsPredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> algorithmIds) {
		if (algorithmIds.size() == 0) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<TaskItem> subRoot = subQuery.from(TaskItem.class);
		subQuery.select(subRoot.get("taskItemNo").as(String.class));

		Predicate in = subRoot.get("algorithmId").as(Long.class).in(algorithmIds);
		return root.get("taskItemNo").as(String.class).in(subQuery.where(in));
	}

	private Predicate assetViewTagItemIdsPredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> assetViewTagItemIds) {
		if (assetViewTagItemIds.size() == 0) {
			return null;
		}

		Subquery<Long> subQuery = query.subquery(Long.class);
		Root<Task2AssetViewTag> subRoot = subQuery.from(Task2AssetViewTag.class);
		subQuery.select(subRoot.get("taskId").as(Long.class));

		Predicate Predicate = subRoot.get("assetViewTagItemId").as(Long.class).in(assetViewTagItemIds);
		subQuery = subQuery.where(Predicate);

		Subquery<String> subQuery2 = subQuery.subquery(String.class);
		Root<TaskItem> subRoot2 = subQuery2.from(TaskItem.class);
		subQuery2.select(subRoot2.get("taskItemNo").as(String.class));
		Predicate Predicate2 = subRoot2.get("taskId").as(Long.class).in(subQuery);
		subQuery2 = subQuery2.where(Predicate2);
		return root.get("taskItemNo").as(String.class).in(subQuery2);
	}

	private Predicate taskTagItemIdsPredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> viewTagItemIds) {
		if (viewTagItemIds.size() == 0) {
			return null;
		}

		Subquery<Long> subQuery = query.subquery(Long.class);
		Root<TaskItemXViewTag> subRoot = subQuery.from(TaskItemXViewTag.class);
		subQuery.select(subRoot.get("taskItemId").as(Long.class));

		Predicate Predicate = subRoot.get("viewTagItemId").as(Long.class).in(viewTagItemIds);
		subQuery = subQuery.where(Predicate);
		// return root.get("id").as(Long.class).in(subQuery);

		Subquery<String> subQuery2 = subQuery.subquery(String.class);
		Root<TaskItem> subRoot2 = subQuery2.from(TaskItem.class);
		subQuery2.select(subRoot2.get("taskItemNo").as(String.class));
		Predicate Predicate2 = subRoot2.get("id").as(Long.class).in(subQuery);
		subQuery2 = subQuery2.where(Predicate2);
		return root.get("taskItemNo").as(String.class).in(subQuery2);
	}

	private Predicate pointPredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb, Compare point) {
		if (point == null) {
			return null;
		}

		if (point.getOp() == Op.LessThan) {
			return cb.lessThan(root.get("point").as(int.class), point.getValue().intValue());
		} else if (point.getOp() == Op.Equal) {
			return cb.equal(root.get("point").as(int.class), point.getValue().intValue());
		} else if (point.getOp() == Op.GreatThan) {
			return cb.greaterThan(root.get("point").as(int.class), point.getValue().intValue());
		}

		return null;
	}

	private Predicate taskCreateDatePredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Predicate predicate = null;
		if (range.getFrom() != null) {
			if (range.getTo() != null) {
				predicate = cb.between(root.get("createTime").as(DateTime.class), range.getFrom(), range.getTo());
			} else {
				predicate = cb.greaterThan(root.get("createTime").as(DateTime.class), range.getFrom());
			}

		} else {
			if (range.getTo() != null) {
				predicate = cb.lessThan(root.get("createTime").as(DateTime.class), range.getTo());
			}
		}

		if (predicate != null) {
			Subquery<String> subQuery = query.subquery(String.class);
			Root<TaskItem> subRoot = subQuery.from(TaskItem.class);
			subQuery.select(subRoot.get("taskItemNo").as(String.class));
			return root.get("taskItemNo").as(String.class).in(subQuery.where(predicate));
		}

		return null;
	}

	private Predicate assetUploadDatePredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Subquery<String> assetQuery = query.subquery(String.class);
		Root<Asset> assetRoot = assetQuery.from(Asset.class);
		assetQuery.select(assetRoot.get("assetNo").as(String.class));
		Predicate predicate = null;
		if (range.getFrom() != null) {
			if (range.getTo() != null) {
				predicate = cb.between(assetRoot.get("createTime").as(DateTime.class), range.getFrom(), range.getTo());
			} else {
				predicate = cb.greaterThan(assetRoot.get("createTime").as(DateTime.class), range.getFrom());
			}

		} else {
			if (range.getTo() != null) {
				predicate = cb.lessThan(assetRoot.get("createTime").as(DateTime.class), range.getTo());
			}
		}

		if (predicate == null) {
			return null;
		}
		assetQuery = assetQuery.where(predicate);

		Subquery<String> taskItemQuery = query.subquery(String.class);
		Root<TaskItem> taskItemRoot = taskItemQuery.from(TaskItem.class);
		taskItemQuery.select(taskItemRoot.get("taskItemNo").as(String.class));
		taskItemQuery = taskItemQuery.where(taskItemRoot.get("assetNo").as(String.class).in(assetQuery));

		return root.get("taskItemNo").as(String.class).in(taskItemQuery);
	}

	private Predicate taskFinishDatePredicate(Root<TaskRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Subquery<String> reviewRecordQuery = query.subquery(String.class);
		Root<ReviewRecord> reviewRecordRoot = reviewRecordQuery.from(ReviewRecord.class);
		reviewRecordQuery.select(reviewRecordRoot.get("taskItemNo").as(String.class));
		Predicate predicate = null;
		if (range.getFrom() != null) {
			if (range.getTo() != null) {
				predicate = cb.between(reviewRecordRoot.get("endTime").as(DateTime.class), range.getFrom(),
						range.getTo());
			} else {
				predicate = cb.greaterThan(reviewRecordRoot.get("endTime").as(DateTime.class), range.getFrom());
			}

		} else {
			if (range.getTo() != null) {
				predicate = cb.lessThan(reviewRecordRoot.get("endTime").as(DateTime.class), range.getTo());
			}
		}

		if (predicate == null) {
			return null;
		}
		List<Predicate> predicateList = new ArrayList<>();
		predicateList.add(predicate);
		predicateList
				.add(cb.equal(reviewRecordRoot.get("status").as(ReviewRecordStatus.class), ReviewRecordStatus.PASS));
		reviewRecordQuery = reviewRecordQuery.where(toPredicateArray(predicateList));

		return root.get("taskItemNo").as(String.class).in(reviewRecordQuery);
	}

	private static Predicate[] toPredicateArray(List<Predicate> list) {
		Predicate[] array = new Predicate[list.size()];
		list.toArray(array);
		return array;
	}
}
