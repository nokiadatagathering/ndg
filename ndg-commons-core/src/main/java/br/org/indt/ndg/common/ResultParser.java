/*
*  Copyright (C) 2010  INdT - Instituto Nokia de Tecnologia
*
*  NDG is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either 
*  version 2.1 of the License, or (at your option) any later version.
*
*  NDG is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public 
*  License along with NDG.  If not, see <http://www.gnu.org/licenses/ 
*/

package br.org.indt.ndg.common;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ResultParser {

	private static Log logger = LogFactory.getLog(ResultParser.class);
	Document document;

	public ResultParser() {
	}

	public ResultXml parseResult(StringBuffer dataBuffer, String encoding) throws SAXException, ParserConfigurationException, IOException {
		InputSource inputSource = new InputSource(new StringReader(dataBuffer.toString()));
		inputSource.setEncoding(encoding);
		logger.info("parsing result buffer");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse( inputSource );
		return processResult();
	}

	public ResultXml parseResult(String resultXml) throws SAXException, ParserConfigurationException, IOException {
		logger.info("parsing result file: "+ resultXml);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse( new File(resultXml) );
		return processResult();
	}

	private ResultXml processResult() {
		TreeMap<Integer, Category> categories;
		ResultXml result = new ResultXml();
		result.setXmldoc(document);

		logger.info("loading result attributes");
		NodeList nodeSurvey = document.getElementsByTagName("result");
		NamedNodeMap resultAttr = nodeSurvey.item(0).getAttributes();
		result.setResultId(resultAttr.getNamedItem("r_id").getNodeValue());
		result.setSurveyId(resultAttr.getNamedItem("s_id").getNodeValue());
		result.setImei(resultAttr.getNamedItem("u_id").getNodeValue());
		result.setTime(resultAttr.getNamedItem("time").getNodeValue());

		NodeList resultChild = nodeSurvey.item(0).getChildNodes();
		for(int i=0; i < resultChild.getLength(); i++) {
			if (resultChild.item(i).getNodeName().equals("title")) {
				result.setTitle(resultChild.item(i).getTextContent());
			} else if (resultChild.item(i).getNodeName().equals("latitude")) {
				result.setLatitude(resultChild.item(i).getTextContent());
			} else if (resultChild.item(i).getNodeName().equals("longitude")) {
				result.setLongitude(resultChild.item(i).getTextContent());
			}
		}

		logger.info("loading categories attributes");
		categories = new TreeMap<Integer, Category>();
		NodeList nodesCategory = document.getElementsByTagName("category");
		int countCategory = nodesCategory.getLength();
		for (int c=0; c < countCategory; ++c) {
			Category category = new Category();
			NamedNodeMap categoryAttr = nodesCategory.item(c).getAttributes();
			category.setId(Integer.parseInt(categoryAttr.getNamedItem("id").getNodeValue()));
			category.setName(categoryAttr.getNamedItem("name").getNodeValue());
			categories.put(category.getId(), category);
		}

		logger.info("loading answers attributes");
		NodeList nodesAnswer = document.getElementsByTagName("answer");
		int countAnswer = nodesAnswer.getLength();
		logger.info("countAnswer = "+ nodesAnswer);
		for (int a=0; a < countAnswer; ++a) {
			NamedNodeMap answerAttr = nodesAnswer.item(a).getAttributes();
			Field answer = new Field();
			String answerId = answerAttr.getNamedItem("id").getNodeValue();
			answer.setId(Integer.parseInt(answerId));
			String answerType = answerAttr.getNamedItem("type").getNodeValue();
			answer.setXmlType(answerType);
			
			if (answer.getXmlType().equals("_time"))
			{
			   String answerConvention = answerAttr.getNamedItem("convention").getNodeValue();
			   answer.setConvention(answerConvention);
			}
			
			if ( answer.getXmlType().equals("_choice") ){
				NodeList answerChild = nodesAnswer.item(a).getChildNodes();
				Choice choice = new Choice();
				for(int i=0; i < answerChild.getLength(); i++) {
					if (answerChild.item(i).getNodeName().equals("item")) {
						try {
							int index = Integer.parseInt(answerChild.item(i).getTextContent());
							Item item = new Item();
							item.setIndex(index);
							item.setOtr("0");
							choice.addItem(item);
						} catch (Exception e) {
							logger.error("Choice with <item></item>, must have a value " + e);
						}
					} else if (answerChild.item(i).getNodeName().equals("other")) {
						try { 
							NamedNodeMap itemAttr = answerChild.item(i).getAttributes();
							int index = Integer.parseInt(itemAttr.getNamedItem("index").getNodeValue());
							String value = answerChild.item(i).getTextContent();
							Item item = new Item();
							item.setIndex(index);
							item.setValue(value);
							item.setOtr("1");
							choice.addItem(item);
						} catch (Exception e) {
							logger.error("Other with <index></index>, must have a value " + e);
						}						
					}
				}
				answer.setChoice(choice);
			} else {
				String value = null;
				
				try {
						Node nodeAnswerItem = nodesAnswer.item(a);
						if(nodeAnswerItem!=null){
							NodeList nodeList = nodeAnswerItem.getChildNodes();
							Node nodeAnswerItemItem = nodeList.item(1);
							value = nodeAnswerItemItem.getTextContent();
//							value = nodesAnswer.item(a).getChildNodes().item(1).getTextContent();
						} else {
							value = "";
						}
				} catch (Exception e) {
					value = "";
				}
				answer.setValue(value);
				logger.info("Value = "+ value);
			}
			String categoryName = nodesAnswer.item(a).getParentNode().getAttributes().getNamedItem("name").getNodeValue();
			int categoryId = Integer.parseInt(nodesAnswer.item(a).getParentNode().getAttributes().getNamedItem("id").getNodeValue());
			logger.debug(categoryName+" | "+answerId+ " | " + answerType);
			Category category = categories.get(categoryId); 
			logger.debug("category name: " + category.getName());
			answer.setCategoryId(categoryId);
			category.addField(answer);
			categories.put(categoryId, category);
		}
		result.setCategories(categories);
		return result;
	}
}
