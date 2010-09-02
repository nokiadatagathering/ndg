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

package main.br.org.indt.ndg.controller.util {
	
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	
	
	public class ExceptionUtil {
		
		//Constants
		private static const NDG_SERVER_EXCEPTION:String = "NDGServerException";
		private static const UNEXPECTED_SERVER_EXCEPTION:String = "UNEXPECTED_SERVER_EXCEPTION";
		
		//Error Codes
		private static const MSM_CORE_MSG_INVALID_PASSWORD:String = "MSM_CORE_MSG_INVALID_PASSWORD";
		private static const MSM_CORE_MSG_INVALID_USERNAME:String = "MSM_CORE_MSG_INVALID_USERNAME";
		private static const MSM_CORE_MSG_USER_EMAIL_NOT_VALIDATED:String = "MSM_CORE_MSG_USER_EMAIL_NOT_VALIDATED";
		private static const MSM_CORE_MSG_INVALID_PASSWORD_GENERATION:String = "MSM_CORE_MSG_INVALID_PASSWORD_GENERATION";
		
		private static const MSM_CORE_MSG_USER_NULL:String = "MSM_CORE_MSG_USER_NULL";
		private static const MSM_CORE_MSG_USER_UNKNOWN:String = "MSM_CORE_MSG_USER_UNKNOWN";
		private static const MSM_CORE_MSG_USER_NOT_FOUND:String = "MSM_CORE_MSG_USER_NOT_FOUND";
		private static const MSM_CORE_MSG_USER_ALREADY_EXIST:String = "MSM_CORE_MSG_USER_ALREADY_EXIST";
     	private static const MSM_CORE_MSG_EMAIL_ALREADY_EXIST:String = "MSM_CORE_MSG_EMAIL_ALREADY_EXIST";
     	private static const MSM_CORE_MSG_USER_LIMIT_REACHED:String = "MSM_CORE_MSG_USER_LIMIT_REACHED";
      	private static const MSM_CORE_MSG_USER_HAS_RELATIONSHIP:String = "MSM_CORE_MSG_USER_HAS_RELATIONSHIP";
      	private static const MSM_PROBLEM_SENDING_EMAIL:String = "MSM_PROBLEM_SENDING_EMAIL";
		
		private static const MSM_CORE_MSG_SURVEY_NOT_FOUND:String = "MSM_CORE_MSG_SURVEY_NOT_FOUND";
		private static const MSM_CORE_MSG_SURVEY_FILE_NOT_FOUND:String = "MSM_CORE_MSG_SURVEY_FILE_NOT_FOUND";
		private static const MSM_CORE_MSG_UNABLE_TO_PARSER_SURVEYS:String = "MSM_CORE_MSG_UNABLE_TO_PARSER_SURVEYS";
		private static const MSM_CORE_MSG_UNABLE_TO_PARSER_RESULTS:String = "MSM_CORE_MSG_UNABLE_TO_PARSER_RESULTS";
		private static const MSM_CORE_MSG_IMEI_FILE_NOT_FOUND:String = "MSM_CORE_MSG_IMEI_FILE_NOT_FOUND";
		private static const MSM_CORE_MSG_SURVEY_ALREADY_EXIST:String = "MSM_CORE_MSG_SURVEY_ALREADY_EXIST";
		private static const MSM_CORE_MSG_IMEI_NOT_MATCH_DEVICE:String = "MSM_CORE_MSG_IMEI_NOT_MATCH_DEVICE";
		
		private static const MSM_CORE_MSG_DEVICE_ALREADY_EXIST:String = "MSM_CORE_MSG_DEVICE_ALREADY_EXIST";
		private static const MSM_CORE_MSG_DEVICE_NOT_FOUND:String = "MSM_CORE_MSG_DEVICE_NOT_FOUND";
		private static const MSM_CORE_MSG_DEVICE_HAS_RELATIONSHIP:String = "MSM_CORE_MSG_DEVICE_HAS_RELATIONSHIP";
		private static const MSM_CORE_MSG_IMEI_LIMIT_REACHED:String = "MSM_CORE_MSG_IMEI_LIMIT_REACHED";
		private static const MSM_CORE_MSG_IMEI_NOT_FOUND:String = "MSM_CORE_MSG_IMEI_NOT_FOUND";
		private static const MSM_CORE_MSG_IMEI_NOT_RECORDED_EXCEPTION:String = "MSM_CORE_MSG_IMEI_NOT_RECORDED_EXCEPTION";
		private static const MSM_CORE_MSG_IMEI_HAS_RELATIONSHIP:String = "MSM_CORE_MSG_IMEI_HAS_RELATIONSHIP";
		private static const MSM_CORE_MSG_IMEI_ALREADY_EXIST:String = "MSM_CORE_MSG_IMEI_ALREADY_EXIST";
		private static const MSM_CORE_MSG_MSISDN_ALREADY_EXIST:String = "MSM_CORE_MSG_MSISDN_ALREADY_EXIST";
      
		private static const MSM_CORE_MSG_SURVEY_NOT_RECORDED_EXCEPTION:String = "MSM_CORE_MSG_SURVEY_NOT_RECORDED_EXCEPTION";
		private static const MSM_CORE_MSG_SURVEY_HAS_RELATIONSHIP:String = "MSM_CORE_MSG_SURVEY_HAS_RELATIONSHIP";
		private static const MSM_CORE_MSG_JAD_DOWNLOAD_ERROR:String = "MSM_CORE_MSG_JAD_DOWNLOAD_ERROR";

      		
		public static function getMessage(msg:String):String{
			var result:String = null;
			if (msg != null && ExceptionUtil.isNDGServerException(msg)){
				var errorCode:String = ExceptionUtil.getErrorCode(msg);
				result = ExceptionUtil.getLocalizedMessage(errorCode);
			} else{
				result = ConfigI18n.getInstance().getString("unknownError");
			}
			return result;			
		}
		
		
		private static function isNDGServerException(msg:String):Boolean{
			return (msg.search(ExceptionUtil.NDG_SERVER_EXCEPTION) >= 0);
		}
		
		public static function getErrorCode(msg:String):String{
			var result:String = null;
			var array:Array = msg.split(' ', 4);
			if (array.length >= 3){
				result = array[2];
			}
			return result;
		}
		
		public static function getLocalizedMessage(errorCode:String):String{
			var result:String = null;
			
			switch(errorCode) {
	    		case ExceptionUtil.MSM_CORE_MSG_INVALID_PASSWORD:
	    			result = ConfigI18n.getInstance().getString("invalidUserPassError");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_INVALID_USERNAME:
	    			result = ConfigI18n.getInstance().getString("invalidUserPassError");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_USER_EMAIL_NOT_VALIDATED:
	        		result = ConfigI18n.getInstance().getString("emailNotValidatedError");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_INVALID_PASSWORD_GENERATION:
	        		result = ConfigI18n.getInstance().getString("invalidPassGeneration");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_USER_NULL:
	        		result = ConfigI18n.getInstance().getString("userNotFound");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_USER_UNKNOWN:
	        		result = ConfigI18n.getInstance().getString("userUnknownError");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_USER_NOT_FOUND:
	        		result = ConfigI18n.getInstance().getString("userNotFoundError");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_UNABLE_TO_PARSER_SURVEYS:
	        		result = ConfigI18n.getInstance().getString("unableToParserSurveys");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_UNABLE_TO_PARSER_RESULTS:
	        		result = ConfigI18n.getInstance().getString("unableToParserResults");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_SURVEY_NOT_FOUND:
	        		result = ConfigI18n.getInstance().getString("surveyNotFound");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_USER_ALREADY_EXIST:
	        		result = ConfigI18n.getInstance().getString("userAlreadyExistError");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_EMAIL_ALREADY_EXIST:
	        		result = ConfigI18n.getInstance().getString("emailAlreadyExistError");
	        		break;
	        	case ExceptionUtil.MSM_PROBLEM_SENDING_EMAIL:
	        		result = ConfigI18n.getInstance().getString("problemSendingEmail");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_USER_LIMIT_REACHED:
	        		result = ConfigI18n.getInstance().getString("userLimitReachedError");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_USER_HAS_RELATIONSHIP:
	        		result = ConfigI18n.getInstance().getString("userHasRelationshipError");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_IMEI_FILE_NOT_FOUND:
	        		result = ConfigI18n.getInstance().getString("imeiFileNotFound");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_SURVEY_ALREADY_EXIST:
	        		result = ConfigI18n.getInstance().getString("surveyAlreadyExist");
	        		break;
	    		case ExceptionUtil.MSM_CORE_MSG_IMEI_NOT_MATCH_DEVICE:
	        		result = ConfigI18n.getInstance().getString("imeiNotMatchDevice");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_DEVICE_ALREADY_EXIST:
	        		result = ConfigI18n.getInstance().getString("deviceAlreadyExistError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_DEVICE_NOT_FOUND:
	        		result = ConfigI18n.getInstance().getString("deviceNotFoundError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_DEVICE_HAS_RELATIONSHIP:
	        		result = ConfigI18n.getInstance().getString("deviceHasRelationshipError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_IMEI_LIMIT_REACHED:
	        		result = ConfigI18n.getInstance().getString("imeiLimitReachedError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_IMEI_NOT_FOUND:
	        		result = ConfigI18n.getInstance().getString("imeiNotFoundError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_IMEI_NOT_RECORDED_EXCEPTION:
	        		result = ConfigI18n.getInstance().getString("imeiNotRecordedError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_IMEI_HAS_RELATIONSHIP:
	        		result = ConfigI18n.getInstance().getString("imeiHasRelationshipError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_IMEI_ALREADY_EXIST:
	        		result = ConfigI18n.getInstance().getString("imeiAlreadyExistError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_MSISDN_ALREADY_EXIST: 		
	        		result = ConfigI18n.getInstance().getString("msisdnAlreadyExistError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_SURVEY_NOT_RECORDED_EXCEPTION: 		
	        		result = ConfigI18n.getInstance().getString("surveyNotRecordedError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_SURVEY_HAS_RELATIONSHIP: 		
	        		result = ConfigI18n.getInstance().getString("surveyHasRelationshipError");
	        		break;
				case ExceptionUtil.MSM_CORE_MSG_JAD_DOWNLOAD_ERROR: 		
	        		result = ConfigI18n.getInstance().getString("jadDownloadError");
	        		break;
			}
			
			if (result == null){
				result = ConfigI18n.getInstance().getString("unexpectedError");
			}
			return result;
		}
	}
	
}