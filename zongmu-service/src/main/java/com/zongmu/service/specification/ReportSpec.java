package com.zongmu.service.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.zongmu.service.dto.reviewrecord.ReviewRecordStatus;
import com.zongmu.service.dto.search.DateRange;
import com.zongmu.service.dto.search.ReportSearchParam;
import com.zongmu.service.dto.search.SearchTaskStatus;
import com.zongmu.service.dto.task.TaskItemStatus;
import com.zongmu.service.dto.task.TaskType;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskItemXViewTag;

public class ReportSpec extends AbstractSpec {

	public Specification<TaskItem> search(final ReportSearchParam params, final Long algorithmId,
			final Long viewTagItemId, final TaskType taskType) {
		return new Specification<TaskItem>() {
			@Override
			public Predicate toPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				predicates.add(taskTypePredicate(root, query, cb, taskType));
				predicates.add(algorithmPredicate(root, query, cb, algorithmId));
				predicates.add(currentTaskViewItemIdPredicate(root, query, cb, viewTagItemId));
				predicates.add(AssetSpec.assetNamePredicate(root, query, cb, params.getAssetName()));
				predicates.add(AssetSpec.assetNoPredicate(root, query, cb, params.getAssetNo()));
				predicates.add(taskNamePredicate(root, query, cb, params.getTaskName()));
				predicates.add(taskItemNoPredicate(root, query, cb, params.getTaskItemNo()));
				predicates.add(AssetSpec.assetUpdateDatePredicate(root, query, cb, params.getUploadDate()));
				predicates.add(AssetSpec.assetRecordDatePredicate(root, query, cb, params.getAssetRecordDate()));
				predicates.add(taskCreateDatePredicate(root, query, cb, params.getTaskDate()));
				List<Predicate> list = taskFinishDatePredicate(root, query, cb, params.getTaskFinishDate());
				if (list != null) {
					predicates.addAll(list);
				}
				predicates.add(AssetSpec.videoLengthPredicate(root, query, cb, params.getRecordLength()));
				predicates.add(AssetSpec.assetViewTagIdsPredicate(root, query, cb, params.getAssetViewItemIds()));
				//predicates.add(TaskItemSpec.taskMainViewTagPredicate(root, query, cb,viewTagItemId));
				predicates.add(TaskItemSpec.taskViewTagsPredicate(root, query, cb, params.getViewTagItemMap(),viewTagItemId));
				List<Predicate> list2 = taskItemStatusPredicate(root, query, cb, params.getTaskItemStatus());
				if (list2 != null) {
					predicates.addAll(list2);
				}

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

	public static List<Predicate> taskItemStatusPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<SearchTaskStatus> taskItemStatus) {
		if (taskItemStatus.size() == 0) {
			return null;
		}

		List<TaskItemStatus> taskItemStatues = new ArrayList<>();
		List<ReviewRecordStatus> reviewStatues = new ArrayList<>();
		for (SearchTaskStatus status : taskItemStatus) {
			if (status == SearchTaskStatus.New) {
				taskItemStatues.add(TaskItemStatus.NEW);
			} else if (status == SearchTaskStatus.Accept) {
				taskItemStatues.add(TaskItemStatus.INPROGRESS);
				taskItemStatues.add(TaskItemStatus.FINISHED);
			} else if (status == SearchTaskStatus.Pass) {
				reviewStatues.add(ReviewRecordStatus.PASS);
			} else if (status == SearchTaskStatus.Reject) {
				reviewStatues.add(ReviewRecordStatus.FAILED);
			}
		}

		List<Predicate> list = new ArrayList<>();
		if (taskItemStatues.size() != 0) {
			Predicate predicate1 = root.get("status").as(TaskItemStatus.class).in(taskItemStatues);
			list.add(predicate1);
		}

		if (reviewStatues.size() != 0) {
			Subquery<String> reviewRecordQuery = query.subquery(String.class);
			Root<ReviewRecord> reviewRecordRoot = reviewRecordQuery.from(ReviewRecord.class);
			reviewRecordQuery.select(reviewRecordRoot.get("taskItemNo").as(String.class));
			Predicate predicate = reviewRecordRoot.get("status").as(ReviewRecordStatus.class).in(reviewStatues);
			Predicate predicate2 = root.get("taskItemNo").as(String.class).in(reviewRecordQuery.where(predicate));
			list.add(predicate2);
		}

		if (list.size() == 0) {
			return null;
		}

		return list;

	}

	public static Predicate taskCreateDatePredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		return range.getPredicate("createTime", root, cb);
	}

	public static List<Predicate> taskFinishDatePredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Predicate predicate = range.getPredicate("updateTime", root, cb);
		if (predicate == null) {
			return null;
		}

		List<Predicate> list = new ArrayList<>();
		list.add(predicate);
		list.add(taskFinishPredicate(root, query, cb));
		return list;
	}

	private static Predicate taskFinishPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Subquery<String> reviewRecordQuery = query.subquery(String.class);
		Root<ReviewRecord> reviewRecordRoot = reviewRecordQuery.from(ReviewRecord.class);
		reviewRecordQuery.select(reviewRecordRoot.get("taskItemNo").as(String.class));
		Predicate predicate = cb.equal(reviewRecordRoot.get("status").as(ReviewRecordStatus.class),
				ReviewRecordStatus.PASS);
		return root.get("taskItemNo").as(String.class).in(reviewRecordQuery.where(predicate));
	}

	public static Predicate taskNamePredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String taskName) {
		if (StringUtils.isEmpty(taskName)) {
			return null;
		}

		return cb.like(root.get("taskName").as(String.class), "%" + taskName + "%");
	}

	public static Predicate taskItemNoPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String taskItemNo) {
		if (StringUtils.isEmpty(taskItemNo)) {
			return null;
		}

		return cb.like(root.get("taskItemNo").as(String.class), "%" + taskItemNo + "%");
	}

	private static Predicate taskTypePredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			TaskType taskType) {
		return cb.equal(root.get("taskType").as(TaskType.class), taskType);
	}

	private static Predicate algorithmPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			Long algorithmId) {
		return cb.equal(root.get("algorithmId").as(Long.class), algorithmId);
	}

	private static Predicate currentTaskViewItemIdPredicate(Root<TaskItem> root, CriteriaQuery<?> query,
			CriteriaBuilder cb, Long taskViewTagItemId) {
		Subquery<Long> taskItemXViewTagQuery = query.subquery(Long.class);
		Root<TaskItemXViewTag> taskItemXViewTagRoot = taskItemXViewTagQuery.from(TaskItemXViewTag.class);
		taskItemXViewTagQuery.select(taskItemXViewTagRoot.get("taskItemId").as(Long.class));
		Predicate predicate = cb.equal(taskItemXViewTagRoot.get("viewTagItemId").as(Long.class), taskViewTagItemId);
		return root.get("id").as(Long.class).in(taskItemXViewTagQuery.where(predicate));
	}
}
