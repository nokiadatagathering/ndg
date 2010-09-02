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
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

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

public class SurveyParser {
	
	private static Log logger = LogFactory.getLog(SurveyParser.class);
	private Document document;
	private TreeMap<Integer, Category> categories;
	private SurveyXML survey;
	private int keys;
	private int fieldid = 0;
	private File surveyFile = null;
	
	public SurveyParser() {	
	}
	
	public SurveyXML parseSurvey(StringBuffer dataBuffer, String encoding) throws SAXException, ParserConfigurationException, IOException {
		InputSource inputSource = new InputSource(new StringReader(dataBuffer.toString()));
		inputSource.setEncoding(encoding);
		logger.info("parsing survey buffer");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse( inputSource );
		return processSurvey();
	}
	
	
	public SurveyXML parseSurvey(String surveyXml) throws SAXException, ParserConfigurationException, IOException {
		logger.info("parsing survey file: "+ surveyXml);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	
		DocumentBuilder builder = factory.newDocumentBuilder();
		surveyFile = new File(surveyXml);
		document = builder.parse( surveyFile );
		return processSurvey();
	}
	
	private SurveyXML processSurvey() throws ParserConfigurationException, SAXException, IOException  {
			survey = new SurveyXML();
			survey.setXmldoc(document);
			
			logger.info("loading survey attributes");
			NodeList nodeSurvey = document.getElementsByTagName("survey");
			NamedNodeMap surveyAttr = nodeSurvey.item(0).getAttributes();
			survey.setId(surveyAttr.getNamedItem("id").getNodeValue());
			survey.setDisplay(surveyAttr.getNamedItem("display").getNodeValue());
			if (surveyAttr.getNamedItem("title") != null)
				survey.setTitle(surveyAttr.getNamedItem("title").getNodeValue());
			else 
				survey.setTitle(nodeSurvey.item(0).getChildNodes().item(1).getTextContent());
			if (surveyAttr.getNamedItem("deployed") != null)
				survey.setDeployed(surveyAttr.getNamedItem("deployed").getNodeValue());
			if (surveyAttr.getNamedItem("checksum") != null)
				survey.setChecksum(surveyAttr.getNamedItem("checksum").getNodeValue());
			
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
			
			logger.info("loading fields attributes");
			NodeList nodesQuestion = document.getElementsByTagName("question");
			int countQuestions = nodesQuestion.getLength();
			//logger.debug("countQuestions= "+ countQuestions);
			for (int a=0; a < countQuestions; ++a) {
				NamedNodeMap questionAttr = nodesQuestion.item(a).getAttributes();
				Field question = new Field();
				String direction = "";
				if (questionAttr.getNamedItem("direction") != null)
					direction = questionAttr.getNamedItem("direction").getNodeValue();
				question.setDirection(direction);
				String questionId = questionAttr.getNamedItem("id").getNodeValue();
				question.setId(Integer.parseInt(questionId));
				String questionType = questionAttr.getNamedItem("type").getNodeValue();
				question.setXmlType(questionType);
				
				if (question.getFieldType() == FieldType.TIME) 
				{
					String convention = "";
					if (questionAttr.getNamedItem("convention") != null)
						convention = questionAttr.getNamedItem("convention").getNodeValue();
					question.setConvention(convention);
				}
				
				String field = null;
				if (questionAttr.getNamedItem("field") != null)
					field = questionAttr.getNamedItem("field").getNodeValue();
				else 
					field = ""+fieldid++;
				question.setName(field);
				Node nquestion = nodesQuestion.item(a);
				Node nchild = nquestion.getChildNodes().item(1);
				String description = nchild.getTextContent();
				question.setDescription(description);
				if ( question.getXmlType().equals("_choice") ){
					NodeList questionChild = nodesQuestion.item(a).getChildNodes();
					Choice choice = new Choice();
					int index = 0;
					for(int i=0; i < questionChild.getLength(); i++) {
						if (questionChild.item(i).getNodeName().equals("select")) {
							String select = questionChild.item(i).getTextContent();
							choice.setChoiceType((select.equals("exclusive") ? ChoiceType.EXCLUSIVE : ChoiceType.MULTIPLE));
						} else if (questionChild.item(i).getNodeName().equals("item")) {
							NamedNodeMap itemAttr = questionChild.item(i).getAttributes();
							String otr = itemAttr.getNamedItem("otr").getNodeValue();
							String value = questionChild.item(i).getTextContent();
							Item item = new Item();
							item.setOtr(otr);
							item.setValue(value);
							item.setIndex(index++);
							choice.addItem(item);
						} else if (questionChild.item(i).getNodeName().equals("SkipLogic")) {
							//TODO: Skiplogic objects here;
						}
					}
					question.setChoice(choice);
				}

				String categoryName = nodesQuestion.item(a).getParentNode().getAttributes().getNamedItem("name").getNodeValue();
				int categoryId = Integer.parseInt(nodesQuestion.item(a).getParentNode().getAttributes().getNamedItem("id").getNodeValue());
				//logger.debug(categoryName+" | "+questionId+ " | " + questionType);
				Category category = categories.get(categoryId); 
				//logger.debug("category name: " + category.getName());
				question.setCategoryId(categoryId);
				category.addField(question);
				categories.put(categoryId, category);
				++keys;
			}
			survey.setCategories(categories);	
			
			return survey;
	}
	
	/*
	 * Return the number of found key_value with direction=out
	 */
	public int getKeys() {
		return this.keys;
	}
	
	public Vector<Field> getQuestions(String direction) {
		Vector<Field> questions = new Vector<Field>();
		Iterator<Category> iterator = categories.values().iterator();
		while(iterator.hasNext()) {
			Category category = iterator.next();
			int sizeQuestions = category.getFields().size();
			for(int j=0; j < sizeQuestions; ++j) {
				Field question = category.getFields().get(j);
					if (question.getDirection().equals(direction)) {
						questions.add(question);
					}
			}
		}
		return questions;
	}
	
	public SurveyXML getSurvey() {
		return survey;
	}
	
	public File getSurveyFile() {
		return surveyFile;
	}
	
}
