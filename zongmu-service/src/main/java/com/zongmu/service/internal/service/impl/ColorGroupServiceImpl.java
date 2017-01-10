package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zongmu.service.entity.ColorGroup;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.ColorGroupService;
import com.zongmu.service.internal.service.ColorTagService;
import com.zongmu.service.repository.ColorGroupRepository;

@Service
public class ColorGroupServiceImpl implements ColorGroupService {

	@Autowired
	private ColorGroupRepository groupRepo;

	@Autowired
	private ColorTagService tagService;

	@Override
	public ColorGroup getColorGroupDetail(Long algorithmId) {
		ColorGroup group = this.groupRepo.getGroupByAlgorithm(algorithmId);
		if (group != null) {
			group.setTags(this.tagService.getTagsByGroup(group.getId()));
		}
		return group;
	}

	@Override
	public void createGroup(ColorGroup colorGroup) throws BusinessException {
		if (this.groupRepo.exists(colorGroup.getAlgorithmId())) {
			throw new BusinessException(ErrorCode.COLOR_GROUP_EXIST);
		}
		this.groupRepo.save(colorGroup);
	}

	@Override
	public void updateGroup(Long id, ColorGroup colorGroup) throws BusinessException {
		ColorGroup oldGroup = this.groupRepo.findOne(id);
		if (oldGroup == null) {
			throw new BusinessException(ErrorCode.COLOR_GROUP_NOT_EXIST);
		}

		oldGroup.setName(colorGroup.getName());
		this.groupRepo.save(oldGroup);
	}

	@Override
	public void deleteGroupByAlgorithm(Long algorithmId) {
		List<ColorGroup> groups = this.groupRepo.getGroups(algorithmId);
		for (ColorGroup group : groups) {
			this.tagService.deleteByGroupId(group.getId());
		}
		this.groupRepo.deleteGroupByAlgorithm(algorithmId);
	}
}
