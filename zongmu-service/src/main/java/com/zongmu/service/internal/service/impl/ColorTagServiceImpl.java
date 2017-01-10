package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.ColorTag;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.ColorTagService;
import com.zongmu.service.internal.service.mark.TaskMarkRecordService;
import com.zongmu.service.repository.ColorTagRepository;
import com.zongmu.service.util.MapperService;

@Service
public class ColorTagServiceImpl implements ColorTagService {

	@Autowired
	private ColorTagRepository colorTagRepo;

	@Autowired
	private TaskMarkRecordService taskMarkRecordService;

	@Autowired
	private MapperService mapperService;

	@Override
	public List<ColorTag> getColorTags() {
		return this.mapperService.toList(this.colorTagRepo.findAll());
	}

	@Override
	public void create(ColorTag colorTag) throws BusinessException {
		boolean exist = this.colorTagRepo.existsCheckBeforeCreate(colorTag.getColorGroupId(),
				colorTag.getName().trim());
		if (exist) {
			throw new BusinessException(ErrorCode.COLOR_TAG_EXIST);
		}

		this.colorTagRepo.save(colorTag);
	}

	@Transactional
	@Override
	public void delete(Long id) throws BusinessException {
		Long count = this.taskMarkRecordService.countByColorTag(id);
		if (count != 0) {
			throw new BusinessException(ErrorCode.COLORTAG_IN_USING);
		}
		this.colorTagRepo.delete(id);
	}

	@Transactional
	@Override
	public void update(Long id, ColorTag colorTag) throws BusinessException {
		ColorTag oldTag = this.colorTagRepo.findOne(id);
		if (oldTag == null) {
			throw new BusinessException(ErrorCode.COLOR_TAG_EXIST);
		}

		boolean exist = this.colorTagRepo.existsCheckBeforeUpdate(oldTag.getColorGroupId(), colorTag.getName(), id);
		if (exist) {
			throw new BusinessException(ErrorCode.COLOR_TAG_EXIST);
		}

		oldTag.setColor(colorTag.getColor());
		oldTag.setName(colorTag.getName());

		this.colorTagRepo.save(oldTag);
	}

	@Override
	public ColorTag getTag(Long id) {
		if (id == null) {
			return null;
		}
		return this.colorTagRepo.findOne(id);
	}

	@Override
	public List<ColorTag> getTagsByGroup(Long groupId) {
		return this.colorTagRepo.getTagsByGroup(groupId);
	}

	@Override
	public void deleteByGroupId(Long groupId) {
		this.colorTagRepo.deleteByGroupId(groupId);
	}
}
