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
import com.zongmu.service.dto.search.HomePageSearchParam;
import com.zongmu.service.dto.search.Op;
import com.zongmu.service.dto.task.TaskItemStatus;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.Task2AssetViewTag;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskItemXViewTag;

public class TaskSpecification {

	public Specification<TaskItem> homePageSearch(final HomePageSearchParam params) {
		return new Specification<TaskItem>() {
			@Override
			public Predicate toPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				predicates.add(hideCuttingTaskItemPredicate(root, query, cb));
				predicates.add(hideFailedTaskItemPredicate(root, query, cb));
				predicates.add(showHomePredicate(root, query, cb));
				predicates.add(taskNamePredicate(root, query, cb, params.getTaskName()));
				predicates.add(taskItemNoPredicate(root, query, cb, params.getTaskItemNo()));
				predicates.add(assetNamePredicate(root, query, cb, params.getAssetName()));
				predicates.add(assetNoPredicate(root, query, cb, params.getAssetNo()));
				predicates.add(assetTypePredicate(root, query, cb, params.getAssetTypes()));
				predicates.add(algorithmIdsPredicate(root, query, cb, params.getAlgorithmIds()));
				predicates.add(pointPredicate(root, query, cb, params.getPoint()));
				predicates.add(taskItemStatusPredicate(root, query, cb, params.getTaskItemStatus()));
				predicates.add(taskCreateDatePredicate(root, query, cb, params.getCreateDate()));
				predicates.add(assetUploadDatePredicate(root, query, cb, params.getUploadDate()));
				predicates.add(videoLengthPredicate(root, query, cb, params.getVideoLength()));
				predicates.add(taskFinishDatePredicate(root, query, cb, params.getTaskFinishDate()));
				predicates.add(videoRecordDatePredicate(root, query, cb, params.getVideoRecordDate()));
				predicates.add(assetViewTagItemIdsPredicate(root, query, cb, params.getAssetViewTagItemIds()));
				predicates.add(taskTagItemIdsPredicate(root, query, cb, params.getViewTagItemIds()));

				List<Predicate> notNullPredicates = new ArrayList<>();
				for (Predicate predicate : predicates) {
					if (predicate != null) {
						notNullPredicates.add(predicate);
					}
				}

				if (params.isNull()) {
					notNullPredicates.add(newTaskItemPredicate(root, query, cb));
				}

				List<Order> orderList = new ArrayList<>();
				orderList.add(orderByTop(root, query, cb));
				orderList.add(orderByPriority(root, query, cb));
				query = query.where(toPredicateArray(notNullPredicates)).orderBy(orderList);

				return query.getRestriction();
			}
		};
	}

	private Predicate showHomePredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return cb.equal(root.get("showHome").as(boolean.class), true);
	}

	private Order orderByTop(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return cb.desc(root.get("top").as(int.class));
	}

	private Order orderByPriority(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return cb.asc(root.get("priority").as(int.class));
	}

	private Predicate taskNamePredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String taskName) {
		if (StringUtils.isEmpty(taskName)) {
			return null;
		}

		return cb.like(root.get("taskName").as(String.class), "%" + taskName + "%");
	}

	private Predicate taskItemNoPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String taskItemNo) {
		if (StringUtils.isEmpty(taskItemNo)) {
			return null;
		}

		return cb.like(root.get("taskItemNo").as(String.class), "%" + taskItemNo + "%");
	}

	private Predicate assetNoPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String assetNo) {
		if (StringUtils.isEmpty(assetNo)) {
			return null;
		}

		return cb.like(root.get("assetNo").as(String.class), "%" + assetNo + "%");
	}

	private Predicate assetNamePredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String assetName) {
		if (StringUtils.isEmpty(assetName)) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<Asset> subRoot = subQuery.from(Asset.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));

		Predicate like = cb.like(subRoot.get("name").as(String.class), "%" + assetName + "%");
		return root.get("assetNo").as(String.class).in(subQuery.where(like));
	}

	private Predicate assetTypePredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<AssetType> assetTypes) {
		if (assetTypes.size() == 0) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<Asset> subRoot = subQuery.from(Asset.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));

		Predicate predicate = subRoot.get("assetType").as(AssetType.class).in(assetTypes);
		return root.get("assetNo").as(String.class).in(subQuery.where(predicate));
	}

	private Predicate algorithmIdsPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> algorithmIds) {
		if (algorithmIds.size() == 0) {
			return null;
		}

		return root.get("algorithmId").as(Long.class).in(algorithmIds);
	}

	private Predicate pointPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb, Compare point) {
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

	private Predicate taskItemStatusPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<TaskItemStatus> taskItemStatusList) {
		if (taskItemStatusList.size() == 0) {
			return null;
		}

		return root.get("status").as(TaskItemStatus.class).in(taskItemStatusList);
	}

	private Predicate newTaskItemPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return cb.equal(root.get("status").as(TaskItemStatus.class), TaskItemStatus.NEW);
	}

	private Predicate hideCuttingTaskItemPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return cb.notEqual(root.get("status").as(TaskItemStatus.class), TaskItemStatus.CUTTING);
	}

	private Predicate hideFailedTaskItemPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return cb.notEqual(root.get("status").as(TaskItemStatus.class), TaskItemStatus.CREATEFAILED);
	}

	private Predicate taskCreateDatePredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}
		if (range.getFrom() != null) {
			if (range.getTo() != null) {
				return cb.between(root.get("createTime").as(DateTime.class), range.getFrom(), range.getTo());
			} else {
				return cb.greaterThan(root.get("createTime").as(DateTime.class), range.getFrom());
			}

		} else {
			if (range.getTo() != null) {
				return cb.lessThan(root.get("createTime").as(DateTime.class), range.getTo());
			}
		}
		return null;
	}

	private Predicate assetViewTagItemIdsPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> assetViewTagItemIds) {
		if (assetViewTagItemIds.size() == 0) {
			return null;
		}

		Subquery<Long> subQuery = query.subquery(Long.class);
		Root<Task2AssetViewTag> subRoot = subQuery.from(Task2AssetViewTag.class);
		subQuery.select(subRoot.get("taskId").as(Long.class));

		Predicate Predicate = subRoot.get("assetViewTagItemId").as(Long.class).in(assetViewTagItemIds);
		subQuery = subQuery.where(Predicate);
		return root.get("taskId").as(Long.class).in(subQuery);
	}

	private Predicate taskTagItemIdsPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> viewTagItemIds) {
		if (viewTagItemIds.size() == 0) {
			return null;
		}

		Subquery<Long> subQuery = query.subquery(Long.class);
		Root<TaskItemXViewTag> subRoot = subQuery.from(TaskItemXViewTag.class);
		subQuery.select(subRoot.get("taskItemId").as(Long.class));

		Predicate Predicate = subRoot.get("viewTagItemId").as(Long.class).in(viewTagItemIds);
		subQuery = subQuery.where(Predicate);
		return root.get("id").as(Long.class).in(subQuery);
	}

	private Predicate assetUploadDatePredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<Asset> subRoot = subQuery.from(Asset.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));

		Predicate predicate = null;
		if (range.getFrom() != null) {
			if (range.getTo() != null) {
				predicate = cb.between(subRoot.get("createTime").as(DateTime.class), range.getFrom(), range.getTo());
			} else {
				predicate = cb.greaterThan(subRoot.get("createTime").as(DateTime.class), range.getFrom());
			}

		} else {
			if (range.getTo() != null) {
				predicate = cb.lessThan(subRoot.get("createTime").as(DateTime.class), range.getTo());
			}
		}

		if (predicate != null) {
			return root.get("assetNo").as(String.class).in(subQuery.where(predicate));
		}
		return null;
	}

	private Predicate videoRecordDatePredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<Asset> subRoot = subQuery.from(Asset.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));

		Predicate predicate = null;
		if (range.getFrom() != null) {
			if (range.getTo() != null) {
				predicate = cb.between(subRoot.get("recordTime").as(DateTime.class), range.getFrom(), range.getTo());
			} else {
				predicate = cb.greaterThan(subRoot.get("recordTime").as(DateTime.class), range.getFrom());
			}

		} else {
			if (range.getTo() != null) {
				predicate = cb.lessThan(subRoot.get("recordTime").as(DateTime.class), range.getTo());
			}
		}

		if (predicate != null) {
			return root.get("assetNo").as(String.class).in(subQuery.where(predicate));
		}
		return null;
	}

	private Predicate videoLengthPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			Compare videoLength) {
		if (videoLength == null) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<Asset> subRoot = subQuery.from(Asset.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));
		Predicate predicate = null;
		if (videoLength.getOp() == Op.LessThan) {
			predicate = cb.lessThan(subRoot.get("recordLength").as(Float.class), videoLength.getValue().floatValue());
		} else if (videoLength.getOp() == Op.Equal) {
			predicate = cb.equal(subRoot.get("recordLength").as(Float.class), videoLength.getValue().floatValue());
		} else if (videoLength.getOp() == Op.GreatThan) {
			predicate = cb.greaterThan(subRoot.get("recordLength").as(Float.class), videoLength.getValue().floatValue());
		}

		if (predicate != null) {
			return root.get("assetNo").as(String.class).in(subQuery.where(predicate));
		}
		return null;
	}

	private Predicate taskFinishDatePredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<ReviewRecord> subRoot = subQuery.from(ReviewRecord.class);
		subQuery.select(subRoot.get("taskItemNo").as(String.class));

		Predicate predicate = null;
		if (range.getFrom() != null) {
			if (range.getTo() != null) {
				predicate = cb.between(subRoot.get("endTime").as(DateTime.class), range.getFrom(), range.getTo());
			} else {
				predicate = cb.greaterThan(subRoot.get("endTime").as(DateTime.class), range.getFrom());
			}

		} else {
			if (range.getTo() != null) {
				predicate = cb.lessThan(subRoot.get("endTime").as(DateTime.class), range.getTo());
			}
		}

		if (predicate != null) {
			Predicate statusPredicate = cb.equal(subRoot.get("status").as(ReviewRecordStatus.class),
					ReviewRecordStatus.PASS);
			List<Predicate> list = new ArrayList<>();
			list.add(predicate);
			list.add(statusPredicate);
			return root.get("taskItemNo").as(String.class).in(subQuery.where(toPredicateArray(list)));
		}
		return null;
	}

	private static Predicate[] toPredicateArray(List<Predicate> list) {
		Predicate[] array = new Predicate[list.size()];
		list.toArray(array);
		return array;
	}
}
