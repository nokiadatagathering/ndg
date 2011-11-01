/*
*  Nokia Data Gathering
*
*  Copyright (C) 2011 Nokia Corporation
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/
*/

package br.org.indt.ndg.common;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
        TreeMap<Integer, CategoryAnswer> categories;
        ResultXml result = new ResultXml();
        result.setXmldoc(document);

        logger.info("loading result attributes");
        NodeList nodeSurvey = document.getElementsByTagName("result");
        NamedNodeMap resultAttr = nodeSurvey.item(0).getAttributes();
        result.setResultId(resultAttr.getNamedItem("r_id").getNodeValue());
        result.setSurveyId(resultAttr.getNamedItem("s_id").getNodeValue());
        result.setImei(resultAttr.getNamedItem("u_id").getNodeValue());
        result.setTime(resultAttr.getNamedItem("time").getNodeValue());

        String version = null;
        if ( resultAttr.getNamedItem("version") != null ) {
            version = resultAttr.getNamedItem("version").getNodeValue();
        }

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
        categories = new TreeMap<Integer, CategoryAnswer>();
        NodeList nodesCategory = document.getElementsByTagName("category");
        int countCategory = nodesCategory.getLength();
        for (int c=0; c < countCategory; ++c) {
            CategoryAnswer category = new CategoryAnswer();
            NamedNodeMap categoryAttr = nodesCategory.item(c).getAttributes();
            category.setId(Integer.parseInt(categoryAttr.getNamedItem("id").getNodeValue()));
            category.setName(categoryAttr.getNamedItem("name").getNodeValue());
            categories.put(category.getId(), category);
        }

        logger.info("loading answers attributes");
        NodeList nodesAnswer = document.getElementsByTagName("answer");
        int countAnswer = nodesAnswer.getLength();
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
            Node answerNode = nodesAnswer.item(a);
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
            }else if(answer.getXmlType().equals("_img")){
                ArrayList<TaggedImage> imageArray = new ArrayList<TaggedImage>();
                try{
                    Element answerElement = (Element)answerNode;
                    NodeList imageNodeList = answerElement.getElementsByTagName("img_data");
                    logger.info("Parsing images: count: " + imageNodeList.getLength());
                    Node imageNode = null;
                    for(int idx = 0; idx < imageNodeList.getLength(); idx++) { // iterate trough image nodes
                        imageNode = imageNodeList.item(idx);
                        TaggedImage taggedImage = new TaggedImage();
                        taggedImage.setImageData(imageNode.getTextContent());
                        NamedNodeMap imageAttr = imageNode.getAttributes();
                        Node latitudeAtrNode = imageAttr.getNamedItem("latitude");
                        Node longitudeAtrNode = imageAttr.getNamedItem("longitude");
                        if ( null != latitudeAtrNode &&  null != longitudeAtrNode) {
                            String latitude = latitudeAtrNode.getNodeValue();
                            String longitude = longitudeAtrNode.getNodeValue();
                            taggedImage.setLatitude(latitude);
                            taggedImage.setLongitude(longitude);
                        }
                        imageArray.add(taggedImage);
                    }
                } catch(Exception e) {
                    logger.info("Failed to parse all image data");
                }
                answer.setImages(imageArray);
                logger.info("Parsed image array");
            }else {
                String value = null;

                try {
                        Node nodeAnswerItem = nodesAnswer.item(a);
                        if(nodeAnswerItem!=null){
                            NodeList nodeList = nodeAnswerItem.getChildNodes();
                            Node nodeAnswerItemItem = nodeList.item(1);
                            value = nodeAnswerItemItem.getTextContent();
//                            value = nodesAnswer.item(a).getChildNodes().item(1).getTextContent();
                        } else {
                            value = "";
                        }
                } catch (Exception e) {
                    value = "";
                }
                answer.setValue(value);
                logger.info("Value = "+ value);
            }
            String categoryName = null;
            int categoryId;
            String subCategoryName = "1";
            if( version == null || version.equals("1") ) {
                categoryName = nodesAnswer.item(a).getParentNode().getAttributes().getNamedItem("name").getNodeValue();
                categoryId = Integer.parseInt(nodesAnswer.item(a).getParentNode().getAttributes().getNamedItem("id").getNodeValue());
            } else {
                categoryName = nodesAnswer.item(a).getParentNode().getParentNode().getAttributes().getNamedItem("name").getNodeValue();
                categoryId = Integer.parseInt(nodesAnswer.item(a).getParentNode().getParentNode().getAttributes().getNamedItem("id").getNodeValue());
                subCategoryName = nodesAnswer.item(a).getParentNode().getAttributes().getNamedItem("subCatId").getNodeValue();
            }

            logger.debug(categoryName+" | "+answerId+ " | " + answerType);
            CategoryAnswer category = categories.get(categoryId);
            logger.debug("category name: " + category.getName());
            answer.setCategoryId(categoryId);

            category.addField(subCategoryName, answer);
            categories.put(categoryId, category);
        }
        result.setCategories(categories);
        return result;
    }
}
