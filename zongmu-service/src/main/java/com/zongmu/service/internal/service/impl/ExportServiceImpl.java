package com.zongmu.service.internal.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zongmu.service.configuration.ApplicationProperties;
import com.zongmu.service.dto.export.ChannelInfo;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.AssetViewTag;
import com.zongmu.service.entity.AssetViewTagItem;
import com.zongmu.service.entity.ColorTag;
import com.zongmu.service.entity.Tag;
import com.zongmu.service.entity.TagItem;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.entity.ViewTag;
import com.zongmu.service.entity.ViewTagItem;
import com.zongmu.service.entity.mark.ShapeFrameIndexInfo;
import com.zongmu.service.entity.mark.TaskMarkGroup;
import com.zongmu.service.entity.mark.TaskMarkPoint;
import com.zongmu.service.entity.mark.TaskMarkRecord;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.AlgorithmService;
import com.zongmu.service.internal.service.AssetService;
import com.zongmu.service.internal.service.AssetViewTagService;
import com.zongmu.service.internal.service.ExportService;
import com.zongmu.service.internal.service.TaskRecordService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.internal.service.mark.TaskMarkRecordService;
import com.zongmu.service.util.FileService;

@Service
public class ExportServiceImpl implements ExportService {

	private static Logger logger = Logger.getLogger(ExportServiceImpl.class);
	
	
	@Autowired
	private ApplicationProperties applicationProperties;

	@Autowired
	private AssetViewTagService assetViewTagService;

	@Autowired
	private AlgorithmService algorithmService;

	@Autowired
	private AssetService assetService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskRecordService taskRecordService;

	@Autowired
	private TaskMarkRecordService taskMarkRecordService;
	
	@Autowired
	private FileService fileService;

	@Override
	public void algorithms(String assetNo) {
		List<Algorithm> algorithms = this.algorithmService.getAlgorithmsFullData();
		for (Algorithm algorithm : algorithms) {
			this.exportAlgorithm(algorithm,assetNo);
		}
	}

	@Override
	public void tasks(String assetNo, String taskNo) throws BusinessException {
		Asset asset = this.assetService.getSimpleAsset(assetNo);
		Task task = this.taskService.getSimpleTask(taskNo);
		try {
			Document doc = this.newXDoc();
			Element root = doc.createElement("annotation_property");
			doc.appendChild(root);
			Element objects = doc.createElement("objects");
			root.appendChild(objects);
			root.appendChild(this.createSimpleElement(doc, "name", asset.getName()));
			List<TaskItem> taskItems = this.taskService.getTaskItemsByTaskId(task.getId());
			List<Element> shapes = this.getTaskItemElements(doc, taskItems.get(0));
			for(Element ele: shapes){
				objects.appendChild(ele);
			}
			/*for (TaskItem taskItem : taskItems) {
				List<Element> shapes = this.getTaskItemElements(doc, taskItem);
				for(Element ele: shapes){
					objects.appendChild(ele);
				}
			}*/
			saveXDoc(applicationProperties.getUploadFolder() + "/" + "video_propertyxx.xml", doc);
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	private List<Element> getTaskItemElements(Document doc, TaskItem taskItem) throws BusinessException {
		List<Element> shapeElements = new ArrayList<>();
		TaskRecord taskRecord = this.taskRecordService.getTaskRecord(taskItem.getTaskRecordNo());
		taskRecord.setTaskMarkRecords(this.taskMarkRecordService.getRecords(taskRecord.getId()));
		for (int index = 0; index < taskRecord.getTaskMarkRecords().size(); index++) {
			TaskMarkRecord taskMarkRecord = taskRecord.getTaskMarkRecords().get(index);
			Element shapeEle = this.getShapeObjectElement(doc, taskMarkRecord, index);
			shapeElements.add(shapeEle);
		}

		return shapeElements;
	}

	private Element getShapeObjectElement(Document doc, TaskMarkRecord taskMarkRecord, int index) {
		Element root = doc.createElement("object" + index);
		root.appendChild(this.createSimpleElement(doc, "id", Long.toString(taskMarkRecord.getId())));
		root.appendChild(this.createSimpleElement(doc, "name", taskMarkRecord.getName()));
		root.appendChild(this.createSimpleElement(doc, "color", taskMarkRecord.getColor()));
		if (taskMarkRecord.getColorTagId() != null) {
			root.appendChild(
					this.createSimpleElement(doc, "colorTagId", Long.toString(taskMarkRecord.getColorTagId())));
		} else {
			root.appendChild(this.createSimpleElement(doc, "colorTagId", ""));
		}

		Map<Long, ChannelInfo> channelInfoMap = new HashMap<>();
		for (TaskMarkGroup group : taskMarkRecord.getGroups()) {
			ChannelInfo info = channelInfoMap.get(group.getTaskItemFileId());
			if (info == null) {
				info = new ChannelInfo();
				channelInfoMap.put(group.getTaskItemFileId(), info);
			}
			info.getGroups().add(group);
		}

		Element channels = doc.createElement("channels");
		root.appendChild(channels);
		for (int channelIndex = 0; channelIndex < taskMarkRecord.getShapeFrameIndexInfos().size(); channelIndex++) {
			ShapeFrameIndexInfo infos = taskMarkRecord.getShapeFrameIndexInfos().get(channelIndex);
			ChannelInfo channelInfo = channelInfoMap.get(infos.getFileId());
			channels.appendChild(this.getChannelObjectElement(doc, taskMarkRecord, infos, channelIndex, channelInfo));
		}
		return root;
	}

	private Element getChannelObjectElement(Document doc, TaskMarkRecord taskMarkRecord, ShapeFrameIndexInfo frameInfo,
			int channelIndex, ChannelInfo channelInfo) {
		Element root = doc.createElement("channel" + channelIndex);
		root.appendChild(this.createSimpleElement(doc, "startIndex", Long.toString(frameInfo.getStartIndex())));
		root.appendChild(this.createSimpleElement(doc, "endIndex", Long.toString(frameInfo.getStartIndex())));
		Element geometries = doc.createElement("geometries");
		root.appendChild(geometries);
		for (int index = 0; index < channelInfo.getGroups().size(); index++) {
			geometries.appendChild(getGeometeyObjectElement(doc, channelInfo.getGroups().get(index), index));
		}
		return root;
	}

	private Element getGeometeyObjectElement(Document doc, TaskMarkGroup group, int index) {
		Element root = doc.createElement("geometry" + index);
		Element points = doc.createElement("points");
		root.appendChild(points);
		for (int pointIndex = 0; pointIndex < group.getPoints().size(); pointIndex++) {
			points.appendChild(this.getPointObjectElement(doc, group.getPoints().get(pointIndex), pointIndex));
		}
		return root;
	}

	private Element getPointObjectElement(Document doc, TaskMarkPoint point, int index) {
		Element root = doc.createElement("point" + index);
		root.appendChild(this.createSimpleElement(doc, "x", Float.toString(point.getX())));
		root.appendChild(this.createSimpleElement(doc, "y", Float.toString(point.getX())));
		return root;
	}

	@Override
	public void assets(String assetNo) throws BusinessException {
		Asset asset = this.assetService.getSimpleAsset(assetNo);
		asset.setViewTags(this.assetService.getAssetViewTags(asset.getId()));
		try {
			Document doc = this.newXDoc();
			Element root = doc.createElement("video_property");
			doc.appendChild(root);
			root.appendChild(this.createSimpleElement(doc, "name", asset.getName()));
			root.appendChild(this.createSimpleElement(doc, "data_type", "视频"));
			root.appendChild(this.createSimpleElement(doc, "memo", asset.getMemo()));
			Element props = doc.createElement("properties");
			root.appendChild(props);
			for(int pIndex =0 ; pIndex < asset.getViewTags().size(); pIndex++){
				Asset2AssetViewTag tag = asset.getViewTags().get(pIndex);
				Element prop = doc.createElement("property"+ pIndex);
				props.appendChild(prop);
				prop.appendChild(this.createSimpleElement(doc, "nameId",String.valueOf(tag.getAssetViewTagId())));
				prop.appendChild(this.createSimpleElement(doc, "name",tag.getViewTag().getName()));
				prop.appendChild(this.createSimpleElement(doc, "valueId",String.valueOf(tag.getAssetViewTagItemId())));
				prop.appendChild(this.createSimpleElement(doc, "value",getTagItemName(tag)));
			}
//			for (Asset2AssetViewTag viewTag : asset.getViewTags()) {
//				root.appendChild(
//						this.createSimpleElement(doc, viewTag.getViewTag().getName(), getTagItemName(viewTag)));
//			}
			root.appendChild(this.timeSpan(doc, asset.getRecordLength()));
			root.appendChild(this.createDateElement(doc, asset.getRecordTime()));
			//saveXDoc(applicationProperties.getUploadFolder() + "/" + "video_property.xml", doc);
			fileService.saveXmlFile2(doc, assetNo, "video_property.xml");
		} catch (Exception ex) {
			logger.error(ExceptionUtils.getStackTrace(ex));
		}
	}
	
	private Element timeSpan(Document doc,Float value){
		Element element = doc.createElement("time_span");
		int sec = value.intValue() % 60;
		int min = ((value.intValue() - sec) / 60) % 60;
		int hour = value.intValue() / 3600;
		element.appendChild(this.createSimpleElement(doc, "hour", String.valueOf(hour)));
		element.appendChild(this.createSimpleElement(doc, "minute", String.valueOf(min)));
		element.appendChild(this.createSimpleElement(doc, "second", String.valueOf(sec)));
		return element;
	}

	private String getTagItemName(Asset2AssetViewTag viewTag) {
		if (viewTag.getViewTag() != null) {
			for (AssetViewTagItem item : viewTag.getViewTag().getItems()) {
				if (item.getId() == viewTag.getAssetViewTagItemId()) {
					return item.getName();
				}
			}
		}

		return "";
	}

	private Element createDateElement(Document doc, DateTime datetime) {
		Element dateEle = doc.createElement("time");
		dateEle.appendChild(this.createSimpleElement(doc, "year", Integer.toString(datetime.getYear())));
		dateEle.appendChild(this.createSimpleElement(doc, "month", Integer.toString(datetime.getMonthOfYear())));
		dateEle.appendChild(this.createSimpleElement(doc, "day", Integer.toString(datetime.getDayOfMonth())));
		dateEle.appendChild(this.createSimpleElement(doc, "hour", Integer.toString(datetime.getHourOfDay())));
		dateEle.appendChild(this.createSimpleElement(doc, "minute", Integer.toString(datetime.getMinuteOfHour())));
		dateEle.appendChild(this.createSimpleElement(doc, "second", Integer.toString(datetime.getSecondOfMinute())));
		return dateEle;
	}

	private void exportAlgorithm(Algorithm algorithm,String assetNo) {
		try {
			Document doc = this.newXDoc();
			Element root = doc.createElement(algorithm.getName() + "_property_set");
			doc.appendChild(root);
			Element tagsEle = doc.createElement("annotation_property");
			root.appendChild(tagsEle);
			for (int index = 0; index < algorithm.getTags().size(); index++) {
				tagsEle.appendChild(this.createTagElement(doc, algorithm.getTags().get(index), index));
			}
			Element scenesEle = doc.createElement("scene_property");
			root.appendChild(scenesEle);
			for (int index = 0; index < algorithm.getViewTags().size(); index++) {
				scenesEle.appendChild(this.createViewTagElement(doc, algorithm.getViewTags().get(index), index));
			}
			Element colorEle = doc.createElement("color_property");
			root.appendChild(colorEle);
			if (algorithm.getColorGroup() != null) {
				colorEle.appendChild(this.createSimpleElement(doc, "id", algorithm.getColorGroup().getId().toString()));
				colorEle.appendChild(this.createSimpleElement(doc, "name", algorithm.getColorGroup().getName()));
				Element valuesEle = doc.createElement("values");
				colorEle.appendChild(valuesEle);
				for (int index = 0; index < algorithm.getColorGroup().getTags().size(); index++) {
					valuesEle.appendChild(
							this.createColorElement(doc, algorithm.getColorGroup().getTags().get(index), index));
				}
			}
			
			fileService.saveXmlFile2(doc, assetNo, algorithm.getName() + "_property_set.xml");
			//saveXDoc(applicationProperties.getUploadFolder() + "/" + algorithm.getName() + "property_set.xml", doc);
		} catch (Exception ex) {
			logger.error(ExceptionUtils.getStackTrace(ex));
		}
	}

	private Element createColorElement(Document doc, ColorTag tag, int index) {
		Element propEle = doc.createElement("value" + index);
		propEle.appendChild(this.createSimpleElement(doc, "id", tag.getId().toString()));
		propEle.appendChild(this.createSimpleElement(doc, "name", tag.getName()));
		propEle.appendChild(this.createSimpleElement(doc, "value", tag.getColor()));
		return propEle;
	}

	private Element createViewTagElement(Document doc, ViewTag tag, int index) {
		Element propEle = doc.createElement("property" + index);
		propEle.appendChild(this.createSimpleElement(doc, "id", tag.getId().toString()));
		propEle.appendChild(this.createSimpleElement(doc, "name", tag.getName()));
		propEle.appendChild(this.createSimpleElement(doc, "type", "string"));
		Element valuesEle = doc.createElement("values");
		propEle.appendChild(valuesEle);
		for (int itemIndex = 0; itemIndex < tag.getItems().size(); itemIndex++) {
			valuesEle.appendChild(this.createViewTagItemElement(doc, tag.getItems().get(itemIndex), itemIndex));
		}
		return propEle;
	}

	private Element createViewTagItemElement(Document doc, ViewTagItem tagItem, int index) {
		Element ele = doc.createElement("value" + index);
		ele.appendChild(this.createSimpleElement(doc, "id", tagItem.getId().toString()));
		ele.appendChild(this.createSimpleElement(doc, "value", tagItem.getName()));
		return ele;
	}

	private Element createTagElement(Document doc, Tag tag, int index) {
		Element propEle = doc.createElement("property" + index);
		propEle.appendChild(this.createSimpleElement(doc, "id", tag.getId().toString()));
		propEle.appendChild(this.createSimpleElement(doc, "name", tag.getName()));
		propEle.appendChild(this.createSimpleElement(doc, "type", "string"));
		propEle.appendChild(this.createSimpleElement(doc, "control_type", tag.getType().toString()));
		Element valuesEle = doc.createElement("values");
		propEle.appendChild(valuesEle);
		for (int itemIndex = 0; itemIndex < tag.getItems().size(); itemIndex++) {
			valuesEle.appendChild(this.createTagItemElement(doc, tag.getItems().get(itemIndex), itemIndex));
		}
		return propEle;
	}

	private Element createTagItemElement(Document doc, TagItem tagItem, int index) {
		Element ele = doc.createElement("value" + index);
		ele.appendChild(this.createSimpleElement(doc, "id", tagItem.getId().toString()));
		ele.appendChild(this.createSimpleElement(doc, "value", tagItem.getValue()));
		return ele;
	}

	private Element createSimpleElement(Document doc, String eleName, String value) {
		Element ele = doc.createElement(eleName);
		ele.setTextContent(value);
		return ele;
	}

	private Document newXDoc() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.newDocument();
	}

	private void saveXDoc(String filePath, Document doc) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filePath));

		transformer.transform(source, result);

	}

	@Override
	public void assetViewTags(String assetNo) {
		List<AssetViewTag> tags = this.assetViewTagService.getAssetViewTag();
		try {
			Document doc = this.newXDoc();
			Element root = doc.createElement("video_property_set");
			doc.appendChild(root);
			for (int index = 0; index < tags.size(); index++) {
				root.appendChild(this.createAssetViewTag(doc, tags.get(index), index));
			}
			
			fileService.saveXmlFile2(doc, assetNo, "video_property_set.xml");
			//saveXDoc(applicationProperties.getUploadFolder() + "/video_property_set.xml", doc);
		} catch (Exception ex) {
			logger.error(ExceptionUtils.getStackTrace(ex));
		}

	}

	private Element createAssetViewTag(Document doc, AssetViewTag tag, int index) {
		Element propEle = doc.createElement("property" + index);
		Element idEle = doc.createElement("id");
		idEle.setTextContent(tag.getId().toString());
		propEle.appendChild(idEle);
		Element nameEle = doc.createElement("name");
		nameEle.setTextContent(tag.getName());
		propEle.appendChild(nameEle);
		Element typeEle = doc.createElement("type");
		typeEle.setTextContent("string");
		propEle.appendChild(typeEle);
		Element valuesEle = doc.createElement("values");
		propEle.appendChild(valuesEle);
		for (int itemIndex = 0; itemIndex < tag.getItems().size(); itemIndex++) {
			valuesEle.appendChild(this.createAssetViewTagItem(doc, tag.getItems().get(itemIndex), itemIndex));
		}
		return propEle;
	}

	private Element createAssetViewTagItem(Document doc, AssetViewTagItem tagItem, int index) {
		Element valueEle = doc.createElement("value" + index);
		Element idEle = doc.createElement("id");
		idEle.setTextContent(tagItem.getId().toString());
		valueEle.appendChild(idEle);
		Element nameEle = doc.createElement("name");
		nameEle.setTextContent(tagItem.getName());
		valueEle.appendChild(nameEle);
		return valueEle;
	}

}
