package com.zongmu.service.specification;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.util.StringUtils;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.search.Compare;
import com.zongmu.service.dto.search.DateRange;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Task2AssetViewTag;

public class AssetSpec {

	public static Predicate assetNamePredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String assetName) {
		if (StringUtils.isEmpty(assetName)) {
			return null;
		}

		Subquery<String> assetQuery = query.subquery(String.class);
		Root<Asset> assetRoot = assetQuery.from(Asset.class);
		assetQuery.select(assetRoot.get("assetNo").as(String.class));

		Predicate like = cb.like(assetRoot.get("name").as(String.class), "%" + assetName + "%");
		return root.get("assetNo").as(String.class).in(assetQuery.where(like));
	}

	public static Predicate assetNoPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, String assetNo) {
		if (StringUtils.isEmpty(assetNo)) {
			return null;
		}

		return cb.like(root.get("assetNo").as(String.class), "%" + assetNo + "%");
	}

	public static Predicate assetTypesPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<AssetType> assetTypes) {
		if (assetTypes.size() == 0) {
			return null;
		}

		return root.get("assetType").as(AssetType.class).in(assetTypes);
	}

	public static Predicate assetUpdateDatePredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Subquery<String> assetQuery = query.subquery(String.class);
		Root<Asset> assetRoot = assetQuery.from(Asset.class);
		assetQuery.select(assetRoot.get("assetNo").as(String.class));
		Predicate predicate = range.getPredicate("createTime", assetRoot, cb);
		if (predicate != null) {
			return root.get("assetNo").as(String.class).in(assetQuery.where(predicate));
		}

		return null;
	}

	public static Predicate assetRecordDatePredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Subquery<String> assetQuery = query.subquery(String.class);
		Root<Asset> assetRoot = assetQuery.from(Asset.class);
		assetQuery.select(assetRoot.get("assetNo").as(String.class));

		Predicate predicate = range.getPredicate("recordTime", assetRoot, cb);

		if (predicate != null) {
			return root.get("assetNo").as(String.class).in(assetQuery.where(predicate));
		}

		return null;
	}

	public static Predicate assetViewTagIdsPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> assetViewTagItemIds) {
		if (assetViewTagItemIds.size() == 0) {
			return null;
		}

		Subquery<Long> task2AssetViewTagQuery = query.subquery(Long.class);
		Root<Task2AssetViewTag> task2AssetViewTagRoot = task2AssetViewTagQuery.from(Task2AssetViewTag.class);
		task2AssetViewTagQuery.select(task2AssetViewTagRoot.get("taskId").as(Long.class));

		Predicate Predicate = task2AssetViewTagRoot.get("assetViewTagItemId").as(Long.class).in(assetViewTagItemIds);

		return root.get("taskId").as(Long.class).in(task2AssetViewTagQuery.where(Predicate));
	}

	public static Predicate videoLengthPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			Compare videoLength) {
		if (videoLength == null) {
			return null;
		}

		Subquery<String> assetQuery = query.subquery(String.class);
		Root<Asset> assetRoot = assetQuery.from(Asset.class);
		assetQuery.select(assetRoot.get("assetNo").as(String.class));

		Predicate predicate = videoLength.getPredicate("recordLength", assetRoot, cb);
		if (predicate == null) {
			return null;
		}

		return root.get("assetNo").as(String.class).in(assetQuery.where(predicate));
	}
}
