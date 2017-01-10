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
import com.zongmu.service.dto.search.AssetSearchParam;
import com.zongmu.service.dto.search.Compare;
import com.zongmu.service.dto.search.DateRange;
import com.zongmu.service.dto.search.Op;
import com.zongmu.service.dto.task.TaskItemStatus;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskItemXViewTag;

public class AssetSpecification {

	private Predicate assetNamePredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String assetName) {
		if (StringUtils.isEmpty(assetName)) {
			return null;
		}

		return cb.like(root.get("name").as(String.class), "%" + assetName + "%");
	}

	private Predicate algorithmIdsPredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> algorithmIds) {
		if (algorithmIds.size() == 0) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<Task> subRoot = subQuery.from(Task.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));
		Predicate in = subRoot.get("algorithmId").as(Long.class).in(algorithmIds);
		return root.get("assetNo").as(String.class).in(subQuery.where(in));
	}

	private Predicate taskNamePredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb, String taskName) {
		if (StringUtils.isEmpty(taskName)) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<Task> subRoot = subQuery.from(Task.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));
		Predicate like = cb.like(subRoot.get("taskName").as(String.class), "%" + taskName + "%");
		return root.get("assetNo").as(String.class).in(subQuery.where(like));
	}

	private Predicate taskItemNoPredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String taskItemNo) {
		if (StringUtils.isEmpty(taskItemNo)) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<TaskItem> subRoot = subQuery.from(TaskItem.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));
		Predicate like = cb.like(subRoot.get("taskItemNo").as(String.class), "%" + taskItemNo + "%");
		return root.get("assetNo").as(String.class).in(subQuery.where(like));
	}

	private Predicate assetNoPredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb, String assetNo) {
		if (StringUtils.isEmpty(assetNo)) {
			return null;
		}

		return cb.like(root.get("assetNo").as(String.class), "%" + assetNo + "%");
	}

	private Predicate assetTypePredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<AssetType> assetTypes) {
		if (assetTypes.size() == 0) {
			return null;
		}

		return root.get("assetType").as(AssetType.class).in(assetTypes);
	}

	private Predicate taskItemStatusPredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<TaskItemStatus> taskItemStatusList) {
		if (taskItemStatusList.size() == 0) {
			return null;
		}

		Subquery<String> subQuery = query.subquery(String.class);
		Root<TaskItem> subRoot = subQuery.from(TaskItem.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));
		Predicate in = subRoot.get("status").as(TaskItemStatus.class).in(taskItemStatusList);
		return root.get("assetNo").as(String.class).in(subQuery.where(in));
	}

	private Predicate assetUploadDatePredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
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

	private Predicate videoRecordDatePredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		if (range.getFrom() != null) {
			if (range.getTo() != null) {
				return cb.between(root.get("recordTime").as(DateTime.class), range.getFrom(), range.getTo());
			} else {
				return cb.greaterThan(root.get("recordTime").as(DateTime.class), range.getFrom());
			}
		} else {
			if (range.getTo() != null) {
				return cb.lessThan(root.get("recordTime").as(DateTime.class), range.getTo());
			}
		}

		return null;
	}

	private Predicate videoLengthPredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			Compare videoLength) {
		if (videoLength == null) {
			return null;
		}

		if (videoLength.getOp() == Op.LessThan) {
			return cb.lessThan(root.get("recordLength").as(Float.class), videoLength.getValue().floatValue());
		} else if (videoLength.getOp() == Op.Equal) {
			return cb.equal(root.get("recordLength").as(Float.class), videoLength.getValue().floatValue());
		} else if (videoLength.getOp() == Op.GreatThan) {
			return cb.greaterThan(root.get("recordLength").as(Float.class), videoLength.getValue().floatValue());
		}

		return null;
	}

	private Predicate taskFinishDatePredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
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

			Subquery<String> taskItemQuery = query.subquery(String.class);
			Root<TaskItem> taskItemRoot = taskItemQuery.from(TaskItem.class);
			taskItemQuery.select(taskItemRoot.get("assetNo").as(String.class));

			Predicate taskItemPredicate = taskItemRoot.get("taskItemNo").as(String.class)
					.in(subQuery.where(toPredicateArray(list)));

			return root.get("assetNo").as(String.class).in(taskItemQuery.where(taskItemPredicate));
		}
		return null;
	}

	private Predicate assetViewTagItemIdsPredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> assetViewTagItemIds) {
		if (assetViewTagItemIds.size() == 0) {
			return null;
		}

		Subquery<Long> subQuery = query.subquery(Long.class);
		Root<Asset2AssetViewTag> subRoot = subQuery.from(Asset2AssetViewTag.class);
		subQuery.select(subRoot.get("assetId").as(Long.class));

		Predicate Predicate = subRoot.get("assetViewTagItemId").as(Long.class).in(assetViewTagItemIds);
		subQuery = subQuery.where(Predicate);
		return root.get("id").as(Long.class).in(subQuery);
	}

	private Predicate taskTagItemIdsPredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> viewTagItemIds) {
		if (viewTagItemIds.size() == 0) {
			return null;
		}

		Subquery<Long> subQuery = query.subquery(Long.class);
		Root<TaskItemXViewTag> subRoot = subQuery.from(TaskItemXViewTag.class);
		subQuery.select(subRoot.get("taskItemId").as(Long.class));

		Predicate Predicate = subRoot.get("viewTagItemId").as(Long.class).in(viewTagItemIds);
		subQuery = subQuery.where(Predicate);

		Subquery<String> taskItemQuery = query.subquery(String.class);
		Root<TaskItem> taskItemRoot = taskItemQuery.from(TaskItem.class);
		taskItemQuery.select(taskItemRoot.get("assetNo").as(String.class));
		Predicate assetNoPredicate = taskItemRoot.get("id").as(Long.class).in(subQuery);

		return root.get("assetNo").as(String.class).in(taskItemQuery.where(assetNoPredicate));
	}

	public Specification<Asset> search(final AssetSearchParam params) {
		return new Specification<Asset>() {
			@Override
			public Predicate toPredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				predicates.add(assetNamePredicate(root, query, cb, params.getAssetName()));
				predicates.add(assetNoPredicate(root, query, cb, params.getAssetNo()));
				predicates.add(assetTypePredicate(root, query, cb, params.getAssetTypes()));
				predicates.add(taskNamePredicate(root, query, cb, params.getTaskName()));
				predicates.add(taskItemNoPredicate(root, query, cb, params.getTaskItemNo()));
				predicates.add(algorithmIdsPredicate(root, query, cb, params.getAlgorithmIds()));
				predicates.add(taskItemStatusPredicate(root, query, cb, params.getTaskItemStatus()));
				predicates.add(assetUploadDatePredicate(root, query, cb, params.getUploadDate()));
				predicates.add(videoLengthPredicate(root, query, cb, params.getVideoLength()));
				predicates.add(videoRecordDatePredicate(root, query, cb, params.getVideoRecordDate()));
				predicates.add(taskFinishDatePredicate(root, query, cb, params.getTaskFinishDate()));
				predicates.add(assetViewTagItemIdsPredicate(root, query, cb, params.getAssetViewTagItemIds()));
				predicates.add(taskTagItemIdsPredicate(root, query, cb, params.getViewTagItemIds()));

				List<Predicate> notNullPredicates = new ArrayList<>();
				for (Predicate predicate : predicates) {
					if (predicate != null) {
						notNullPredicates.add(predicate);
					}
				}

				List<Order> orderList = new ArrayList<>();
				orderList.add(orderByCreateTime(root, query, cb));
				query = query.where(toPredicateArray(notNullPredicates)).orderBy(orderList);

				return query.getRestriction();
			}
		};
	}

	private Order orderByCreateTime(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return cb.asc(root.get("createTime").as(DateTime.class));
	}

	private static Predicate[] toPredicateArray(List<Predicate> list) {
		Predicate[] array = new Predicate[list.size()];
		list.toArray(array);
		return array;
	}
}
