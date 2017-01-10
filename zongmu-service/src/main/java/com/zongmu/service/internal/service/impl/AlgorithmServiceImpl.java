package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.Tag;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.AlgorithmService;
import com.zongmu.service.internal.service.ColorGroupService;
import com.zongmu.service.internal.service.TagService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.internal.service.ViewTagService;
import com.zongmu.service.repository.AlgorithmRepository;
import com.zongmu.service.util.MapperService;

@Service
public class AlgorithmServiceImpl implements AlgorithmService {

	private static Logger logger = Logger.getLogger(AlgorithmServiceImpl.class);

	@Autowired
	private AlgorithmRepository algorithmRepos;

	@Autowired
	private MapperService mapperService;

	@Autowired
	private TagService tagService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private ColorGroupService colorGroupService;

	@Autowired
	private ViewTagService viewTagService;

	@Override
	public List<Algorithm> getAlgorithms() {
		return this.mapperService.toList(this.algorithmRepos.findAll());
	}

	@Override
	public List<Algorithm> getAlgorithmsFullData() {
		List<Algorithm> algorithms = this.mapperService.toList(this.algorithmRepos.findAll());
		for (Algorithm algorithm : algorithms) {
			algorithm.setTags(this.tagService.getTagsByAlgorithm(algorithm.getId()));
			algorithm.setColorGroup(this.colorGroupService.getColorGroupDetail(algorithm.getId()));
			algorithm.setViewTags(this.viewTagService.getViewTags(algorithm.getId()));
		}

		return algorithms;
	}

	@Override
	public Algorithm getAlgorithm(Long id) throws BusinessException {
		Algorithm algorithm = this.algorithmRepos.findOne(id);

		if (algorithm == null) {
			throw new BusinessException(ErrorCode.Algorithm_NOT_EXIST);
		}

		return algorithm;
	}

	@Transactional
	@Override
	public Algorithm getAlgorithmDetail(Long id) throws BusinessException {
		Algorithm algorithm = this.algorithmRepos.findOne(id);
		if (algorithm == null) {
			throw new BusinessException(ErrorCode.Algorithm_NOT_EXIST);
		}

		algorithm.setTags(this.tagService.getTagsByAlgorithm(algorithm.getId()));
		algorithm.setColorGroup(this.colorGroupService.getColorGroupDetail(algorithm.getId()));
		algorithm.setViewTags(this.viewTagService.getViewTags(algorithm.getId()));
		return algorithm;
	}

	@Transactional
	@Override
	public void updateAlgorithm(Long id, Algorithm algorithm) throws BusinessException {
		Algorithm oldAlgorithm = this.algorithmRepos.findOne(id);
		if (oldAlgorithm == null) {
			throw new BusinessException(ErrorCode.Algorithm_NOT_EXIST);
		}

		boolean exist = this.algorithmRepos.exists(algorithm.getName().trim(), id);
		if (exist) {
			throw new BusinessException(ErrorCode.Algorithm_EXIST);
		}

		oldAlgorithm.setName(algorithm.getName());
		this.algorithmRepos.save(oldAlgorithm);
	}

	@Transactional
	@Override
	public Algorithm createAlgorithm(Algorithm algorithm) throws BusinessException {
		boolean exist = this.algorithmRepos.exists(algorithm.getName().trim());
		if (exist) {
			throw new BusinessException(ErrorCode.Algorithm_EXIST);
		}

		return this.algorithmRepos.save(algorithm);
	}

	@Transactional
	@Override
	public void deleteAlgorithm(Long id) throws BusinessException {
		Long count = this.taskService.countTasksByAlgorithm(id);
		if (count != 0) {
			throw new BusinessException(ErrorCode.Algorithm_IN_USING);
		}
		
		this.tagService.deleteTagsByAlgorithm(id);
		this.colorGroupService.deleteGroupByAlgorithm(id);
		this.viewTagService.deleteViewTagsByAlgorithm(id);
		this.algorithmRepos.delete(id);
	}

	@Override
	public void setTag(Long id, Algorithm algorithm) throws BusinessException {
		Algorithm oldAlgorithm = this.algorithmRepos.findOne(id);
		if (oldAlgorithm == null) {
			throw new BusinessException(ErrorCode.Algorithm_NOT_EXIST);
		}

		// oldAlgorithm.setHasColor(algorithm.isHasColor());
		// oldAlgorithm.setTagIds(algorithm.getTagIds());
		this.algorithmRepos.save(oldAlgorithm);
	}

	@Override
	public List<Tag> getTags(Long algorithmId) throws BusinessException {
		Algorithm algorithm = this.algorithmRepos.findOne(algorithmId);
		if (algorithm == null) {
			throw new BusinessException(ErrorCode.Algorithm_NOT_EXIST);
		}

		return this.getTags(algorithm);
	}

	private List<Tag> getTags(Algorithm algorithm) throws BusinessException {
		return null;
		// List<Long> tagIds = new ArrayList<>();
		// if (!StringUtils.isEmpty(algorithm.getTagIds())) {
		// for (String it : algorithm.getTagIds().split(";")) {
		// try {
		// tagIds.add(Long.parseLong(it));
		// } catch (Exception ex) {
		// logger.error(String.format("Parse string %s to long failed", it));
		// ExceptionUtils.getStackTrace(ex);
		// }
		//
		// }
		// }
		// return this.tagService.getTags(tagIds);
	}

}
