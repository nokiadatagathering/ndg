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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ResultWriter
{
	private SurveyXML survey;
	private ResultXml result;

	public ResultWriter(SurveyXML survey, ResultXml result)
	{
		this.survey = survey;
		this.result = result;
	}

	public String write() throws ParserConfigurationException, Exception
	{
		DOMSource domSource = new DOMSource(getDocument());
		StreamResult streamResult = new StreamResult(new StringWriter());
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, Resources.ENCODING);
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		serializer.transform(domSource, streamResult);
		
		String xmlString = streamResult.getWriter().toString();

		return xmlString;
	}
	
	public Document getDocument() throws ParserConfigurationException
	{
		Document xmldoc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();
		Element answerElement = null;
		Element categoryElement = null;
		Element text = null;
		Element title = null;
		Element lat = null;
		Element lon = null;
		Element other = null;
		Node titleText = null;
		Node nodeStr = null;

		xmldoc = impl.createDocument(null, "result", null);
		
		Element root = xmldoc.getDocumentElement();

		root.setAttribute("r_id", result.getResultId());
		root.setAttribute("s_id", result.getSurveyId());
		root.setAttribute("u_id", result.getImei());
		root.setAttribute("time", result.getTime());
		
		if (result.getLatitude() != null ) 
		{
			lat = xmldoc.createElementNS(null, "latitude");
			titleText = xmldoc.createTextNode(result.getLatitude());
			lat.appendChild(titleText);
			root.appendChild(lat);				
		}

		if (result.getLongitude() != null)
		{
			lon = xmldoc.createElementNS(null, "longitude");
			titleText = xmldoc.createTextNode(result.getLongitude());
			lon.appendChild(titleText);
			root.appendChild(lon);				
		}
		
		title = xmldoc.createElementNS(null, "title");
		titleText = xmldoc.createTextNode("");
		title.appendChild(titleText);
		root.appendChild(title);
		
		TreeMap<Integer, Category> categories = survey.getCategories();
		Iterator<Category> iteratorCat = categories.values().iterator();
		
		while(iteratorCat.hasNext())
		{
			Category category = iteratorCat.next();
			categoryElement = xmldoc.createElementNS(null, "category");
			categoryElement.setAttribute("name", category.getName() );
			categoryElement.setAttribute("id", String.valueOf(category.getId()));
			root.appendChild(categoryElement);
			Integer categoryId = new Integer(category.getId());
			Vector<Field> questions = category.getFields();
			
			for(Field question : questions)
			{
				answerElement = xmldoc.createElementNS(null, "answer");
				answerElement.setAttributeNS(null, "type", question.getXmlType());
				answerElement.setAttributeNS(null, "id", String.valueOf(question.getId()));
				answerElement.setAttributeNS(null, "visited", "false");
				Field answer = result.getCategories().get(categoryId).getFieldById(question.getId());

				if (question.getFieldType() == FieldType.CHOICE)
				{
					ArrayList<Item> items = answer.getChoice().getItems();
					
					for(Item item : items )
					{
						if (answer.getValue().contains(item.getIndex() + "^"))
						{
							if (item.getOtr() == null || item.getOtr().equals("0"))
							{ 
								if (answer.getValue().contains(item.getIndex() + "^"))
								{
									nodeStr = xmldoc.createTextNode("" + item.getIndex());
									text = xmldoc.createElementNS(null, question.getElementName());
									text.appendChild(nodeStr);
									answerElement.appendChild(text);
									categoryElement.appendChild(answerElement);
								}
							} 
							else 
							{
								String value = answer.getValue();
								System.out.println(value.substring(value.indexOf('_') + 1, value.lastIndexOf('_')));
								nodeStr = xmldoc.createTextNode(item.getValue());
								nodeStr = xmldoc.createTextNode(value.substring(value.indexOf('_') + 1, value.lastIndexOf('_')));
								other = xmldoc.createElementNS(null, "other");
								other.setAttributeNS(null, "index", "" + item.getIndex());
								other.appendChild(nodeStr);
								answerElement.appendChild(other);
								categoryElement.appendChild(answerElement);	
							}
						}
					}
				}
				else
				{
					nodeStr = xmldoc.createTextNode((answer.getValue() == null ? "" : answer.getValue()));
					text = xmldoc.createElementNS(null, question.getElementName());
					text.appendChild(nodeStr);
					answerElement.appendChild(text);
					categoryElement.appendChild(answerElement);
				}

				if ((question.getCategoryId() == survey.getDisplayCategory()) && (question.getId() == survey.getDisplayQuestion()))
				{
					(root.getElementsByTagName("title")).item(0).setTextContent(answer.getValue());				
				}
			}
		}
		
		return xmldoc;
	}
}
