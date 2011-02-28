package br.org.indt.ndg.client.transformer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.jboss.util.Base64;

import br.org.indt.ndg.common.ResultXml;
import br.org.indt.ndg.common.SurveyXML;
import br.org.indt.ndg.common.TaggedImage;

public abstract class ResultsTransformer extends Transformer {

	public static final String PHOTOS_DIR = "photos";
	public static final String UNDERLINE_SEPARATOR = "_";
	public static final String JPG_EXTENSION = ".jpg";

	protected Boolean exportWithImages;

	public ResultsTransformer(SurveyXML survey, Boolean exportWithImages) {
		super(survey);
		this.exportWithImages = exportWithImages;
	}

	public void write(String path) {
		ArrayList<ResultXml> results = survey.getResults();
		processResults(path, results);
	}

	public void write(String path, Collection<ResultXml> results) {
		processResults(path, results);
	}

	public abstract byte[] getBytes();

	protected abstract void processResults(String path, Collection<ResultXml> results);

	/**
	 * This method stores image files from structures.
	 * NOTE: Root of the storage path is hardcoded, outside code depend on it.
	 * @return	string in format: "image_path" or "image_path[latitude,longitude]"
	 */
	public String storeImagesAndGetValueToExport( String surveyId, int categoryId, String resultId, int fieldId,
			ArrayList<TaggedImage> imageList )
	{
		String value = "";
		if ( exportWithImages ) {
			String imageDirPath =
				File.separator + PHOTOS_DIR +
				File.separator + resultId +
				File.separator;
			new File(surveyId + imageDirPath).mkdirs();

			String imageFileNameBase = categoryId + UNDERLINE_SEPARATOR + fieldId;

			if (!imageList.isEmpty()) {
				for ( int imgIndex = 0; imgIndex < imageList.size(); imgIndex++ ) {
					try {
						TaggedImage image = imageList.get(imgIndex);
						String imageFileName = imageFileNameBase + UNDERLINE_SEPARATOR + (imgIndex+1) + JPG_EXTENSION;
						// Store image
						FileOutputStream arqImg = new FileOutputStream(surveyId + imageDirPath + imageFileName);
						arqImg.write(Base64.decode(image.getImageData()));
						arqImg.close();
						// add file path and GeoTag to returned value
						String formattedGeoTag = "";
						if ( image.hasGeoTag() ) {
							formattedGeoTag = "[" + image.getLatitude() + "," + image.getLongitude() +"]";
						}
						value += (imageDirPath + imageFileName + formattedGeoTag + " ");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				value = value.trim();
			} else {
				value = "no images";
			}
		} else {
			value = "<img>";
		}
		return value;
	}
}
